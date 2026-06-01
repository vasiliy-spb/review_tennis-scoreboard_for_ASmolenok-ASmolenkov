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

    public void activateTieBreak(){
        this.tieBreakActive = true;
    }

    public void deactivateTieBreak(){
        this.tieBreakActive = false;
    }

}
