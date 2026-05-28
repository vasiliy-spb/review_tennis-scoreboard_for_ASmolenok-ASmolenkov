package io.github.asmolenkov.tennismatchscoreboard.service;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import io.github.asmolenkov.tennismatchscoreboard.exception.FindMatchException;
import io.github.asmolenkov.tennismatchscoreboard.model.CurrentMatch;
import io.github.asmolenkov.tennismatchscoreboard.model.MatchScore;
import io.github.asmolenkov.tennismatchscoreboard.repository.MatchRepository;

import java.util.Optional;
import java.util.UUID;

public class OngoingMatchesService {
    private final MatchRepository matchRepository;

    public OngoingMatchesService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public CurrentMatch createMatch(PlayerDto playerOne, PlayerDto playerSecond){
        UUID uuid = UUID.randomUUID();
        MatchScore matchScore = MatchScore.builder().build();
        CurrentMatch currentMatch = CurrentMatch.builder()
                .uuid(uuid).playerOne(playerOne).playerSecond(playerSecond).matchScore(matchScore).build();
        matchRepository.save(currentMatch);
        return currentMatch;
    }

    public CurrentMatch findMatchByUuid(UUID uuid){
        Optional<CurrentMatch> currentMatch = matchRepository.find(uuid);
        if(currentMatch.isPresent()){
            return currentMatch.get();
        }else {
            throw new FindMatchException("Такой матч не найден!");
        }
    }

    public void updateMath(CurrentMatch currentMatch) {
        matchRepository.update(currentMatch);
    }
}
