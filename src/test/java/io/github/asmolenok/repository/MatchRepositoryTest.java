package io.github.asmolenok.repository;

import io.github.asmolenkov.tennismatchscoreboard.model.CurrentMatch;
import io.github.asmolenkov.tennismatchscoreboard.model.MatchScore;
import io.github.asmolenkov.tennismatchscoreboard.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MatchRepositoryTest {
    private MatchRepository matchRepository;

    @BeforeEach
    void setUp() {
        matchRepository = new MatchRepository();
    }

    @Test
    @DisplayName("✅ Сохранение и поиск матча по UUID")
    void saveAndFindMatch_success() {
        UUID testUuid = UUID.randomUUID();
        CurrentMatch expectedMatch = CurrentMatch.builder()
                                                 .uuid(testUuid)
                                                 .playerOneId(1L)
                                                 .playerSecondId(2L)
                                                 .matchScore(MatchScore.builder().build())
                                                 .build();


        matchRepository.save(expectedMatch);
        Optional<CurrentMatch> actualMatch = matchRepository.find(testUuid);


        assertTrue(actualMatch.isPresent(), "Матч должен быть найден после сохранения");
        assertEquals(expectedMatch.getUuid(), actualMatch.get().getUuid());
        assertEquals(1L, actualMatch.get().getPlayerOneId());
        assertEquals(2L, actualMatch.get().getPlayerSecondId());
    }

    @Test
    @DisplayName("❌ Поиск несуществующего матча возвращает пустой Optional")
    void findNonExistentMatch_returnsEmpty() {

        UUID fakeUuid = UUID.randomUUID();


        Optional<CurrentMatch> result = matchRepository.find(fakeUuid);


        assertTrue(result.isEmpty(), "Для несуществующего UUID должен вернуться пустой Optional");
    }
}
