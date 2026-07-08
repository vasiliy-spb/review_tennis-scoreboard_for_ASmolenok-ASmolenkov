package io.github.asmolenkov.tennismatchscoreboard.model;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class MatchScore {
    private static final int NUMBER_SET_WON = 2;
    private static final int GAME_SCORE = 6;
    private static final int POINT_DIFFERENCE_IN_SET = 2;

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
        int setsWonP1 = countSetWon(PlayerSide.ONE);
        int setsWonP2 = countSetWon(PlayerSide.TWO);

        return setsWonP1 >= NUMBER_SET_WON || setsWonP2 >= NUMBER_SET_WON;
    }

    public boolean isStartTieBreak(int playerOneGames, int playerTwoGames){
        return playerOneGames == GAME_SCORE && playerTwoGames == GAME_SCORE;
    }

    public boolean isStartTieBreak(SetScore set){
        return isStartTieBreak(set.getPlayerOneGameCount(), set.getPlayerSecondGameCount());
    }

    public boolean isSetFinished(SetScore set){
        return isSetFinished(set.getPlayerOneGameCount(), set.getPlayerSecondGameCount());
    }

    private boolean isSetFinished(int playerOneGames, int playerTwoGames) {
        return  (playerOneGames >= GAME_SCORE && playerOneGames - playerTwoGames >= POINT_DIFFERENCE_IN_SET) ||
                (playerTwoGames >= GAME_SCORE && playerTwoGames - playerOneGames >= POINT_DIFFERENCE_IN_SET);

    }

    private int countSetWon(PlayerSide side){
        int count = 0;
        if(isSetWon(setOneScore, side)){
            count ++;
        }
        if(isSetWon(setTwoScore, side)){
            count++;
        }
        if (isSetWon(setThreeScore, side)){
            count++;
        }
        return count;
    }

    private boolean isSetWon(SetScore set, PlayerSide side){
        if(set.isSetActive()){
            return false;
        }
        int p1 = set.getPlayerOneGameCount();
        int p2 = set.getPlayerSecondGameCount();

        return side == PlayerSide.ONE ? p1 > p2 : p2 > p1;
    }

    public SetScore getCurrentSet() {
        if (setOneScore.isSetActive()) {
            return setOneScore;
        } else if (setTwoScore.isSetActive()) {
            return setTwoScore;
        } else {
            return setThreeScore;
        }
    }


}
