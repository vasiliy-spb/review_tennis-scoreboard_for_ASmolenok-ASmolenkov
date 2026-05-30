package io.github.asmolenkov.tennismatchscoreboard.service;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import io.github.asmolenkov.tennismatchscoreboard.model.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MatchScoreCalculationService {

    public void addPointToPlayer(CurrentMatch currentMatch, long playerId) {
        if (matchIsFinished(currentMatch)) {

        } else {
            int numberSet = whatIsSetNow(currentMatch);
            SetScore currentSet = getCurrentSet(currentMatch, numberSet);
            PlayerDto playerOne = currentMatch.getPlayerOne();
            log.info("ID игрока 1 = {}, а playerId = {}", playerOne.id(), playerId);
            if (playerId == playerOne.id()) {
                Point point = currentMatch.getPointPlayer(PlayerSide.ONE);
                if (point == Point.ADVANTAGE) {
                    currentSet.setPlayerOneAddGame();
                    currentMatch.resetAllPoint();
                    if(isSetFinished(currentSet.getPlayerOneGameCount(), currentSet.getPlayerSecondGameCount())){
                        currentMatch.resetAllPoint();
                        return;
                    }
                    return;
                }
                currentMatch.addPointToPlayer(PlayerSide.ONE);
            } else {
                Point point = currentMatch.getPointPlayer(PlayerSide.TWO);
                if (point == Point.ADVANTAGE) {
                    currentSet.setPlayerSecondAddGame();
                    currentMatch.resetAllPoint();
                    if(isSetFinished(currentSet.getPlayerOneGameCount(), currentSet.getPlayerSecondGameCount())){
                        currentMatch.resetAllPoint();
                        return;
                    }
                    return;
                }
                currentMatch.addPointToPlayer(PlayerSide.TWO);

            }
        }
    }


    private boolean matchIsFinished(CurrentMatch currentMatch) {
        return currentMatch.isMatchFinished();
    }

    private int whatIsSetNow(CurrentMatch currentMatch) {
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

    private SetScore getCurrentSet(CurrentMatch currentMatch, int countSet) {
        return switch (countSet) {
            case 1 -> currentMatch.getMatchScore()
                                  .getSetOneScore();
            case 2 -> currentMatch.getMatchScore()
                                  .getSetTwoScore();
            case 3 -> currentMatch.getMatchScore()
                                  .getSetThreeScore();
            default -> throw new IllegalStateException("Unexpected value: " + countSet);
        };
    }

    private boolean isSetFinished(int playerOneGames, int playerTwoGames) {
        return (playerOneGames >= 6 && playerOneGames - playerTwoGames >= 1) ||
                (playerTwoGames >= 6 && playerTwoGames - playerOneGames >= 1);
    }
}
