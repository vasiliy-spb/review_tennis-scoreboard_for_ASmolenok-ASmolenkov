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

    // Можно использовать @RequiredArgsConstructor над классом вместо самописного конструктора

    // TODO: Нет интерфейса для этого класса. (см. файл "service.md" в этом же пакете)

    // TODO: Класс способствует смешению слоёв — передаёт доменную модель в слой контроллеров.
        // (см. файл "separation-of-concerns-principle.md" в этом же пакете)

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
