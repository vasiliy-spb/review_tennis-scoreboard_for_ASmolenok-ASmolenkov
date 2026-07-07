package io.github.asmolenkov.tennismatchscoreboard.service;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import io.github.asmolenkov.tennismatchscoreboard.model.CurrentMatch;
import io.github.asmolenkov.tennismatchscoreboard.model.MatchScore;
import io.github.asmolenkov.tennismatchscoreboard.repository.ActiveMatchRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;
@Slf4j
public class OngoingMatchesService {
    private final ActiveMatchRepository activeMatchRepository;

    public OngoingMatchesService(ActiveMatchRepository activeMatchRepository) {
        this.activeMatchRepository = activeMatchRepository;
    }

    public CurrentMatch createMatch(PlayerDto playerOne, PlayerDto playerSecond){
        UUID uuid = UUID.randomUUID();
        MatchScore matchScore = MatchScore.builder().build();
        CurrentMatch currentMatch = CurrentMatch.builder()
                .uuid(uuid).playerOne(playerOne).playerSecond(playerSecond).matchScore(matchScore).build();
        activeMatchRepository.save(currentMatch);
        return currentMatch;
    }

    public Optional <CurrentMatch> findMatchByUuid(UUID uuid){
        return activeMatchRepository.find(uuid);
    }


}
