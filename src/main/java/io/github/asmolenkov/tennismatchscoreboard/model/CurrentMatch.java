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
    @Builder.Default
    private boolean matchFinished = false;
    private PlayerDto winner;


    public void resetAllPointGame() {
        matchScore.getPlayersGameScore().resetPoint();
    }


    public void finishTheMatch(PlayerSide playerSide){
        this.matchFinished = true;
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
