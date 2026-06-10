package io.github.asmolenok.record;

import io.github.asmolenkov.tennismatchscoreboard.model.Point;

public record TwoSetsMatchScenario(long scoringPlayerId,
                                   int set1P1, int set1P2,
                                   int set2P1, int set2P2,
                                   Point pointP1, Point pointP2) {
}
