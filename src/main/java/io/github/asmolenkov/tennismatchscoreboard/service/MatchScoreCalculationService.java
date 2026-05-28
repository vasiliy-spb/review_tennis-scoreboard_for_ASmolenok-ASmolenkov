package io.github.asmolenkov.tennismatchscoreboard.service;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import io.github.asmolenkov.tennismatchscoreboard.model.CurrentMatch;
import io.github.asmolenkov.tennismatchscoreboard.model.MatchScore;
import io.github.asmolenkov.tennismatchscoreboard.model.Point;
import io.github.asmolenkov.tennismatchscoreboard.model.SetScore;
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
                if (currentMatch.getMatchScore()
                                .getPlayersGameScore()
                                .getPlayerOnePoint() == Point.ADVANTAGE) {

                    currentSet.setPlayerOneAddGame();

                    if(isSetFinished(currentSet.getPlayerOneGameCount(), currentSet.getPlayerSecondGameCount())){
                        currentMatch.getMatchScore()
                                    .getPlayersGameScore()
                                    .resetPoint();
                        return;
                    }
                }
                currentMatch.getMatchScore()
                            .getPlayersGameScore()
                            .playerOneAddPoint();

                log.info("Point игрока 1 = {}", currentMatch.getMatchScore()
                                                            .getPlayersGameScore()
                                                            .getPlayerOnePoint().getDisplayValue());
            } else {
                if (currentMatch.getMatchScore()
                                .getPlayersGameScore()
                                .getPlayerSecondPoint() == Point.ADVANTAGE) {
                    currentSet.setPlayerSecondAddGame();
                    if(isSetFinished(currentSet.getPlayerOneGameCount(), currentSet.getPlayerSecondGameCount())){
                        currentMatch.getMatchScore()
                                    .getPlayersGameScore()
                                    .resetPoint();
                        return;
                    }
                }
                currentMatch.getMatchScore()
                            .getPlayersGameScore()
                            .playerSecondAddPoint();

                log.info("Point игрока 2 = {}", currentMatch.getMatchScore()
                                                            .getPlayersGameScore()
                                                            .getPlayerSecondPoint().getDisplayValue());
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
