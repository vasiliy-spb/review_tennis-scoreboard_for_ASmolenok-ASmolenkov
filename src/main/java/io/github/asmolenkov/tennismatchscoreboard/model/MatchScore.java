package io.github.asmolenkov.tennismatchscoreboard.model;

import lombok.Builder;
import lombok.Setter;


@Builder
@Setter
public class MatchScore {
    private PointScore pointScore;
    private GameScore gameScore;
    private SetScore setScore;
    private TieBreakScore tieBreakScore;

}
