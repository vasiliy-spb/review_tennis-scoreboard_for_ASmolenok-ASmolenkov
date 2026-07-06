package io.github.asmolenkov.tennismatchscoreboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TieBreakScore {
    private int playerOnePoint;
    private int playerSecondPoint;


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

    public Optional<PlayerSide> getWinner() {
        int p1 = this.playerOnePoint;
        int p2 = this.playerSecondPoint;

        if (p1 >= 7 && p1 - p2 >= 2) {
            return Optional.of(PlayerSide.ONE);
        }

        if (p2 >= 7 && p2 - p1 >= 2) {
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
