package io.github.asmolenkov.tennismatchscoreboard.service;

import io.github.asmolenkov.tennismatchscoreboard.model.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MatchScoreCalculationService {

    public void addPointToPlayer(CurrentMatch currentMatch, long playerId) {
        if (matchIsFinished(currentMatch)) {
            return;
            //TODO Реализовать завершение матча
        }
        PlayerSide side = resolvePlayerSide(currentMatch, playerId); //TODO добавить обработку если метод вернул null

        processPointUpdate(currentMatch, side);

    }

    private PlayerSide resolvePlayerSide(CurrentMatch currentMatch, long playerId) {
        if (currentMatch.getPlayerOne()
                        .id() == playerId) return PlayerSide.ONE;
        if (currentMatch.getPlayerSecond()
                        .id() == playerId) return PlayerSide.TWO;
        return null;
    }

    private void processPointUpdate(CurrentMatch currentMatch, PlayerSide playerSide) {
        PlayerSide opponent = getOpponent(playerSide);
        int setNumber = determineActiveSetNumber(currentMatch);
        SetScore currentSet = getCurrentSet(currentMatch, setNumber);
        MatchScore matchScore = currentMatch.getMatchScore();

        Point currentPoint = currentMatch.getPointPlayer(playerSide);
        Point opponentPoint = currentMatch.getPointPlayer(opponent);

        if(isTieBreakActive(matchScore)){
            tieBreakPointUpdate(matchScore, currentSet , playerSide);
            return;
        }

        if (isStandardGameWin(currentPoint, opponentPoint)) {
            awardGameToPlayer(currentSet, playerSide);
            currentMatch.resetAllPoint();
            checkAndHandleSetCompletion(currentMatch, currentSet, setNumber); //TODO принять решение о необходимости этого метода
            return;
        }
        if (isOpponentAtAdvantage(currentPoint, opponentPoint)) {
            currentMatch.resetAdvantage(opponent);
            return;
        }

        if (currentPoint == Point.ADVANTAGE) {
            awardGameToPlayer(currentSet, playerSide);
            currentMatch.resetAllPoint();
            checkAndHandleSetCompletion(currentMatch, currentSet, setNumber);
            return;
        }
        if(isSetFinished(currentSet.getPlayerOneGameCount(), currentSet.getPlayerSecondGameCount())){
            currentSet.fishedSet();
        }
        currentMatch.addPointToPlayer(playerSide);

        if(isStartTieBreak(currentSet.getPlayerOneGameCount(), currentSet.getPlayerSecondGameCount())){
            matchScore.activateTieBreak();
        }
    }

    private void tieBreakPointUpdate(MatchScore matchScore, SetScore setScore, PlayerSide current) {
        PlayerSide opponent = getOpponent(current);

        TieBreakScore tieBreakScore = matchScore.getTieBreakScore();

        tieBreakScore.addTieBreakPoint(current);

        if(isTieBreakWon(tieBreakScore, current, opponent)){
            PlayerSide winner = (tieBreakScore.getPlayerOnePoint() > tieBreakScore.getPlayerSecondPoint() ? PlayerSide.ONE : PlayerSide.TWO);
            awardGameToPlayer(setScore,winner);
            matchScore.deactivateTieBreak();
            setScore.fishedSet();


            tieBreakScore.resetPoint();
            matchScore.getPlayersGameScore().resetAllPoint();

            log.info("✅ Тай-брейк завершён. Победитель сета: {}", winner);
        }

    }

    private PlayerSide getOpponent(PlayerSide side) {
        return side == PlayerSide.ONE ? PlayerSide.TWO : PlayerSide.ONE;
    }

    private int determineActiveSetNumber(CurrentMatch currentMatch) {
        MatchScore matchScore = currentMatch.getMatchScore();
        SetScore setOne = matchScore.getSetOneScore();
        SetScore setTwo = matchScore.getSetTwoScore();

        if (setOne.isSetActive()) {
            return 1;
        } else if (setTwo.isSetActive()) {
            return 2;
        } else {
            return 3;
        }
    }

    private SetScore getCurrentSet(CurrentMatch currentMatch, int setNumber) {
        return switch (setNumber) {
            case 1 -> currentMatch.getMatchScore()
                                  .getSetOneScore();
            case 2 -> currentMatch.getMatchScore()
                                  .getSetTwoScore();
            case 3 -> currentMatch.getMatchScore()
                                  .getSetThreeScore();
            default -> throw new IllegalStateException("Unexpected value: " + setNumber);
        };
    }

    private boolean isStandardGameWin(Point winner, Point loser) {
        return winner == Point.FORTY && loser != Point.FORTY &&
                loser != Point.ADVANTAGE;
    }

    private void awardGameToPlayer(SetScore setScore, PlayerSide playerSide) {
        if (playerSide == PlayerSide.ONE) {
            setScore.setPlayerOneAddGame();
        } else {
            setScore.setPlayerSecondAddGame();
        }
    }

    private void checkAndHandleSetCompletion(CurrentMatch match, SetScore set, int setNumber) {
        if (isSetFinished(set.getPlayerOneGameCount(), set.getPlayerSecondGameCount())) {
            log.info("Сет {} завершён", setNumber);
        }
    }

    private boolean isOpponentAtAdvantage(Point notAdvantage, Point advantage) {
        return notAdvantage == Point.FORTY && advantage == Point.ADVANTAGE;
    }


    private boolean matchIsFinished(CurrentMatch currentMatch) {
        return currentMatch.isMatchFinished();
    }


    private boolean isSetFinished(int playerOneGames, int playerTwoGames) {
        return  (playerOneGames >= 6 && playerOneGames - playerTwoGames >= 2) ||
                (playerTwoGames >= 6 && playerTwoGames - playerOneGames >= 2);

    }

    private boolean isTieBreakActive(MatchScore matchScore){
        return matchScore.isTieBreakActive();
    }

    private boolean isStartTieBreak(int playerOneGames, int playerTwoGames){
        return playerOneGames == 6 && playerTwoGames == 6;
    }

    private boolean isTieBreakWon(TieBreakScore tieBreakScore, PlayerSide current, PlayerSide opponent){
        int currentPoint = tieBreakScore.getPointPlayer(current);
        int opponentPoint = tieBreakScore.getPointPlayer(opponent);
        return (currentPoint >= 7 && currentPoint - opponentPoint >= 2) || (opponentPoint >= 7 && opponentPoint - currentPoint >= 2);
    }
}
