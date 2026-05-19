package io.github.asmolenkov.tennismatchscoreboard.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Setter
@Getter
public class MatchScore {
    @Builder.Default
    private SetScore setScore = new SetScore();
    @Builder.Default
    private TieBreakScore tieBreakScore = new TieBreakScore();


}
