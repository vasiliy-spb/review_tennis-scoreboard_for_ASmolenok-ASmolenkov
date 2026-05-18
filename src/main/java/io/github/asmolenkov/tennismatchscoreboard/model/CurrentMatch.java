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

}
