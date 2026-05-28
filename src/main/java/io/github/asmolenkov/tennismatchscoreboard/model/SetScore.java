package io.github.asmolenkov.tennismatchscoreboard.model;

import lombok.Getter;

@Getter
public class SetScore {
    private int playerOneGameCount;
    private int playerSecondGameCount;


    public void setPlayerOneAddGame(){
        this.playerOneGameCount++;
    }

    public void setPlayerSecondAddGame(){
        this.playerSecondGameCount++;
    }
}
