package io.github.asmolenkov.tennismatchscoreboard.service;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
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

    public boolean isMatchFinished (CurrentMatch math) {
        MatchScore matchScore = math.getMatchScore();

        SetScore oneSet = matchScore.getSetOneScore();
        SetScore twoSet = matchScore.getSetTwoScore();
        SetScore threeSet = matchScore.getSetThreeScore();

        int oneSetScoreP1 = matchScore.getSetOneScore().getPlayerOneGameCount();
        int oneSetScoreP2 = matchScore.getSetOneScore().getPlayerSecondGameCount();

        int twoSetScoreP1 = matchScore.getSetTwoScore().getPlayerOneGameCount();
        int twoSetScoreP2 = matchScore.getSetTwoScore().getPlayerSecondGameCount();

        if((!oneSet.isSetActive() && !twoSet.isSetActive() && oneSetScoreP1 > oneSetScoreP2 && twoSetScoreP1 > twoSetScoreP2) ||
                (!oneSet.isSetActive() && !twoSet.isSetActive() && oneSetScoreP2 > oneSetScoreP1 && twoSetScoreP2 > twoSetScoreP1)){
            return true;
        }

        return !oneSet.isSetActive() && !twoSet.isSetActive() && !threeSet.isSetActive();
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
        log.info("Очки Игрок 1 = {}", currentPoint);
        Point opponentPoint = currentMatch.getPointPlayer(opponent);
        log.info("Очки Игрок 2 = {}", opponentPoint);


        if(isTieBreakActive(matchScore)){
            log.info("Идет Тай-брейк");
            tieBreakPointUpdate(matchScore, currentSet , playerSide);
            return;
        }

        if (isStandardGameWin(currentPoint, opponentPoint)) {
            log.info("Гейм завершен");
            awardGameToPlayer(currentSet, playerSide);
            currentMatch.resetAllPoint();
            log.info("Очки сброшены");
            if(isStartTieBreak(currentSet.getPlayerOneGameCount(), currentSet.getPlayerSecondGameCount())){
                log.info("Начинается Тай-брейк");
                matchScore.activateTieBreak();
            }
            if(isSetFinished(currentSet.getPlayerOneGameCount(), currentSet.getPlayerSecondGameCount())){
                log.info("Сет №{} - завершен", setNumber);
                currentSet.fishedSet();
            }
            if(isMatchFinished(currentMatch)){
                log.info("Матч завершен");
                log.info("Победитель - {}", playerSide);
                finishedMatch(currentMatch,getWinner(currentMatch, playerSide));
            }
            return;
        }
        if (isOpponentAtAdvantage(currentPoint, opponentPoint)) {
            log.info("Сброс преимущества оппонента");
            currentMatch.resetAdvantage(opponent);
            return;
        }

        if (currentPoint == Point.ADVANTAGE) {
            awardGameToPlayer(currentSet, playerSide);
            log.info("Гейм завершен");
            log.info("Сброс очков");
            currentMatch.resetAllPoint();
            checkAndHandleSetCompletion(currentMatch, currentSet, setNumber);
            return;
        }
        currentMatch.addPointToPlayer(playerSide);
        /*if(isSetFinished(currentSet.getPlayerOneGameCount(), currentSet.getPlayerSecondGameCount())){
            log.info("Сет №{} - завершен", setNumber);
            currentSet.fishedSet();
        }

        if(isMatchFinished(currentMatch)){
            log.info("Матч завершен");
            log.info("Победитель - {}", playerSide);
            finishedMatch(currentMatch,getWinner(currentMatch, playerSide));
        }*/

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
            log.info("Сет {} завершён {}", setNumber, set.isSetActive());
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

    private void finishedMatch(CurrentMatch match, PlayerDto winner){
        match.finishTheMatch(winner);
    }

    private PlayerDto getWinner(CurrentMatch currentMatch, PlayerSide side){
        if(side == PlayerSide.ONE){
            return currentMatch.getPlayerOne();
        }else {
            return currentMatch.getPlayerSecond();
        }
    }

}
