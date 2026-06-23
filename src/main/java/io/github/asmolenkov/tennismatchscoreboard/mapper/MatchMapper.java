package io.github.asmolenkov.tennismatchscoreboard.mapper;

import io.github.asmolenkov.tennismatchscoreboard.dto.MatchDto;
import io.github.asmolenkov.tennismatchscoreboard.entity.Match;
import io.github.asmolenkov.tennismatchscoreboard.model.CurrentMatch;

import java.util.ArrayList;
import java.util.List;

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

    public static MatchDto toDto(Match match){
        if (match == null) {
            return null;
        }

        return new MatchDto(match.getPlayerOne().getName(), match.getPlayerSecond().getName(), match.getWinner().getName());
    }

    public static List<MatchDto> toDtoList(List<Match> entity){
        if (entity == null) {
            return null;
        }

        List<MatchDto> matches = new ArrayList<>();
        for (Match match: entity){
            matches.add(toDto(match));
        }

        return matches;
    }
}
