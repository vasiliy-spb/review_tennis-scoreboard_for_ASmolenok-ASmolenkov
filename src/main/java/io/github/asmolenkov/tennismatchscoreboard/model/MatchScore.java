package io.github.asmolenkov.tennismatchscoreboard.model;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class MatchScore {
    @Builder.Default
    private SetScore setOneScore = new SetScore();
    @Builder.Default
    private SetScore setTwoScore = new SetScore();
    @Builder.Default
    private SetScore setThreeScore = new SetScore();
    @Builder.Default
    private boolean tieBreakActive = false;
    @Builder.Default
    private TieBreakScore tieBreakScore = new TieBreakScore();
    @Builder.Default
    private final GameScore playersGameScore = new GameScore();

    public void activateTieBreak() {

        this.tieBreakActive = true;
    }

    public void deactivateTieBreak() {

        this.tieBreakActive = false;
    }

    public boolean isMatchFinished() {

        int oneSetScoreP1 = setOneScore.getPlayerOneGameCount();
        int oneSetScoreP2 = setOneScore.getPlayerSecondGameCount();

        int twoSetScoreP1 = setTwoScore.getPlayerOneGameCount();
        int twoSetScoreP2 = setTwoScore.getPlayerSecondGameCount();

        if ((!setOneScore.isSetActive() && !setTwoScore.isSetActive() && oneSetScoreP1 > oneSetScoreP2 && twoSetScoreP1 > twoSetScoreP2) ||
                (!setOneScore.isSetActive() && !setTwoScore.isSetActive() && oneSetScoreP2 > oneSetScoreP1 && twoSetScoreP2 > twoSetScoreP1)) {
            return true;
        }

        return !setOneScore.isSetActive() && !setTwoScore.isSetActive() && !setThreeScore.isSetActive();
    }

    public boolean isStartTieBreak(int playerOneGames, int playerTwoGames){
        return playerOneGames == 6 && playerTwoGames == 6;
    }

    public boolean isSetFinished(int playerOneGames, int playerTwoGames) {
        return  (playerOneGames >= 6 && playerOneGames - playerTwoGames >= 2) ||
                (playerTwoGames >= 6 && playerTwoGames - playerOneGames >= 2);

    }

    public boolean isTieBreakWon(PlayerSide current, PlayerSide opponent){
        int currentPoint = tieBreakScore.getPointPlayer(current);
        int opponentPoint = tieBreakScore.getPointPlayer(opponent);
        return (currentPoint >= 7 && currentPoint - opponentPoint >= 2) || (opponentPoint >= 7 && opponentPoint - currentPoint >= 2);
    }

    public int determineActiveSetNumber(){
        if (setOneScore.isSetActive()) {
            return 1;
        } else if (setTwoScore.isSetActive()) {
            return 2;
        } else {
            return 3;
        }
    }


}
