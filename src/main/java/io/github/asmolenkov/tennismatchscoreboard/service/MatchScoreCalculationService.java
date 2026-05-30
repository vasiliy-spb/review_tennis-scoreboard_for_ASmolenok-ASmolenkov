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

        Point currentPoint = currentMatch.getPointPlayer(playerSide);
        Point opponentPoint = currentMatch.getPointPlayer(opponent);

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
        currentMatch.addPointToPlayer(playerSide);
    }

    private PlayerSide getOpponent(PlayerSide side) {
        return side == PlayerSide.ONE ? PlayerSide.TWO : PlayerSide.ONE;
    }

    private int determineActiveSetNumber(CurrentMatch currentMatch) {
        MatchScore matchScore = currentMatch.getMatchScore();
        SetScore setOne = matchScore.getSetOneScore();
        SetScore setTwo = matchScore.getSetTwoScore();
        int p1Set1 = setOne.getPlayerOneGameCount();
        int p2Set1 = setOne.getPlayerSecondGameCount();
        int p1Set2 = setTwo.getPlayerOneGameCount();
        int p2Set2 = setTwo.getPlayerSecondGameCount();

        if (!isSetFinished(p1Set1, p2Set1)) {
            return 1;
        } else if (!isSetFinished(p1Set2, p2Set2)) {
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
        return (playerOneGames >= 6 && playerOneGames - playerTwoGames >= 1) ||
                (playerTwoGames >= 6 && playerTwoGames - playerOneGames >= 1);
    }


}
