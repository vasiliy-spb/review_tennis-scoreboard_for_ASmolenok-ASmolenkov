package io.github.asmolenkov.tennismatchscoreboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TieBreakScore {
    private static final int START_SCORE = 0;
    private static final int MAX_NUMBER_POINT_IN_TIE_BREAK = 7;
    private static final int DIFFERENCE_SCORE_IN_TIE_BREAK = 2;

    private int playerOnePoint;
    private int playerSecondPoint;


    public void addTieBreakPoint(PlayerSide playerSide){
        switch (playerSide){
            case ONE -> addPointPlayerOne();
            case TWO -> addPointPlayerSecond();
        }
    }

    public void resetPoint(){
        playerOnePoint = START_SCORE;
        playerSecondPoint = START_SCORE;
    }

    public Optional<PlayerSide> getWinner() {
        int p1 = this.playerOnePoint;
        int p2 = this.playerSecondPoint;

        if (p1 >= MAX_NUMBER_POINT_IN_TIE_BREAK && p1 - p2 >= DIFFERENCE_SCORE_IN_TIE_BREAK) {
            return Optional.of(PlayerSide.ONE);
        }

        if (p2 >= MAX_NUMBER_POINT_IN_TIE_BREAK && p2 - p1 >= DIFFERENCE_SCORE_IN_TIE_BREAK) {
            return Optional.of(PlayerSide.TWO);
        }

        return Optional.empty();
    }

    private void addPointPlayerOne(){
        playerOnePoint++;
    }

    private void addPointPlayerSecond(){
        playerSecondPoint++;
    }
}
