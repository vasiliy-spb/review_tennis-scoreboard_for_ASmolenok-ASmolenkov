package io.github.asmolenok.service;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import io.github.asmolenkov.tennismatchscoreboard.exception.FindMatchException;
import io.github.asmolenkov.tennismatchscoreboard.model.CurrentMatch;
import io.github.asmolenkov.tennismatchscoreboard.repository.MatchRepository;
import io.github.asmolenkov.tennismatchscoreboard.service.OngoingMatchesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OngoingMatchesServiceTest {
    @Mock
    private MatchRepository matchRepository;
    @InjectMocks
    private OngoingMatchesService matchService;



    @Test
    @DisplayName("Сервис создаёт матч и вызывает репозиторий")
    void createMatch_success_callsRepository() {
        PlayerDto p1 = new PlayerDto(1L, "Sasha");
        PlayerDto p2 = new PlayerDto(2L, "Masha");

        matchService.createMatch(p1, p2);


        ArgumentCaptor<CurrentMatch> captor = ArgumentCaptor.forClass(CurrentMatch.class);
        verify(matchRepository, times(1)).save(captor.capture());

        CurrentMatch savedMatch = captor.getValue();
        assertNotNull(savedMatch.getUuid(), "UUID должен быть сгенерирован");
        assertEquals(1L, savedMatch.getPlayerOneId());
        assertEquals(2L, savedMatch.getPlayerSecondId());
        assertNotNull(savedMatch.getMatchScore(), "MatchScore должен быть инициализирован");
    }

    @Test
    @DisplayName("Сервис возвращает матч, если репозиторий его нашёл")
    void findMatchByUuid_success() {

        UUID testUuid = UUID.randomUUID();
        CurrentMatch fakeMatch = CurrentMatch.builder()
                                             .uuid(testUuid)
                                             .playerOneId(10L)
                                             .playerSecondId(20L)
                                             .build();

        when(matchRepository.find(testUuid)).thenReturn(Optional.of(fakeMatch));


        CurrentMatch result = matchService.findMatchByUuid(testUuid);


        assertEquals(testUuid, result.getUuid());
        assertEquals(10L, result.getPlayerOneId());
        verify(matchRepository, times(1)).find(testUuid);
    }

    @Test
    @DisplayName("Сервис выбрасывает исключение, если матч не найден")
    void findMatchByUuid_notFound_throwsException() {

        UUID missingUuid = UUID.randomUUID();
        when(matchRepository.find(missingUuid)).thenReturn(Optional.empty());

        assertThrows(FindMatchException.class, () -> matchService.findMatchByUuid(missingUuid));
    }
}
