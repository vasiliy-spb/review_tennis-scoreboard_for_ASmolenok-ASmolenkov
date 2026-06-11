package io.github.asmolenok.record;

public record TieBreakMatchScenario(long scoringPlayerId,
                                    int set1P1, int set1P2,
                                    int set2P1, int set2P2,
                                    int tieBreakPointP1, int tieBreakPointP2) {
}
