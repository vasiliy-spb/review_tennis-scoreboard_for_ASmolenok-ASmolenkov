package io.github.asmolenkov.tennismatchscoreboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SetScore {
    private int playerOneGameCount;
    private int playerSecondGameCount;
    private boolean setActive = true;


    public void setPlayerOneAddGame(){
        this.playerOneGameCount++;
    }

    public void setPlayerSecondAddGame(){
        this.playerSecondGameCount++;
    }

    public void fishedSet(){
        this.setActive = false;
    }
}
