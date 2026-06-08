package io.github.asmolenkov.tennismatchscoreboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TieBreakScore {
    private int playerOnePoint;
    private int playerSecondPoint;


    public int getPointPlayer(PlayerSide side) {
        return switch (side) {
            case ONE -> playerOnePoint;
            case TWO -> playerSecondPoint;
        };
    }

    public void addTieBreakPoint(PlayerSide playerSide){
        switch (playerSide){
            case ONE -> addPointPlayerOne();
            case TWO -> addPointPlayerSecond();
        }
    }

    public void resetPoint(){
        playerOnePoint = 0;
        playerSecondPoint = 0;
    }

    private void addPointPlayerOne(){
        playerOnePoint++;
    }

    private void addPointPlayerSecond(){
        playerSecondPoint++;
    }
}
