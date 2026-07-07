package io.github.asmolenkov.tennismatchscoreboard.model;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CurrentMatch {
    private final UUID uuid;
    private final PlayerDto playerOne;
    private final PlayerDto playerSecond;
    private final MatchScore matchScore;
    private PlayerDto winner;


    public void resetAllPointGame() {
        matchScore.getPlayersGameScore().resetPoint();
    }

    public boolean isMatchFinished(){
        return matchScore.isMatchFinished();
    }


    public void finishTheMatch(PlayerSide playerSide){
        if(playerSide == PlayerSide.ONE){
            this.winner = playerOne;
        }
        else {
            this.winner = playerSecond;
        }
    }

    public SetScore getSet (){
        return matchScore.getCurrentSet();
    }

}
