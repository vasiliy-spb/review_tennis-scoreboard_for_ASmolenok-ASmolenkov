package io.github.asmolenkov.tennismatchscoreboard.mapper;

import io.github.asmolenkov.tennismatchscoreboard.entity.Match;
import io.github.asmolenkov.tennismatchscoreboard.model.CurrentMatch;

public class MatchMapper {

    public static Match toEntity(CurrentMatch model) {
        if (model == null) {
            return null;
        }
        return Match.builder()
                    .playerOne(PlayerMapper.toEntity(model.getPlayerOne()))
                    .playerSecond(PlayerMapper.toEntity(model.getPlayerSecond()))
                    .winner(PlayerMapper.toEntity(model.getWinner()))
                    .build();
    }
}
