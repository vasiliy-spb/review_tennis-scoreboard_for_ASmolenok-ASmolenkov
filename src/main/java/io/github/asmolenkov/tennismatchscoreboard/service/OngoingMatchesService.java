package io.github.asmolenkov.tennismatchscoreboard.service;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import io.github.asmolenkov.tennismatchscoreboard.exception.FindMatchException;
import io.github.asmolenkov.tennismatchscoreboard.model.CurrentMatch;
import io.github.asmolenkov.tennismatchscoreboard.model.MatchScore;
import io.github.asmolenkov.tennismatchscoreboard.repository.ActiveMatchRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;
@Slf4j
public class OngoingMatchesService {

    private final static String MATCH_NOT_FOUND = "Такой матч не найден!";
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

    public CurrentMatch findMatchByUuid(UUID uuid){
        Optional<CurrentMatch> currentMatch = activeMatchRepository.find(uuid);
        if(currentMatch.isPresent()){
            return currentMatch.get();
        }else {
            log.warn(MATCH_NOT_FOUND);
            throw new FindMatchException(MATCH_NOT_FOUND);
        }
    }

    public void deleteMatchByUuid(UUID uuid){
        activeMatchRepository.delete(uuid);
    }
}
