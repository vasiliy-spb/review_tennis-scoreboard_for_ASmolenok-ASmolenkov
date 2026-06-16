package io.github.asmolenok.service;

import io.github.asmolenkov.tennismatchscoreboard.entity.Match;
import io.github.asmolenkov.tennismatchscoreboard.entity.Player;
import io.github.asmolenkov.tennismatchscoreboard.mapper.MatchMapper;
import io.github.asmolenkov.tennismatchscoreboard.model.CurrentMatch;
import io.github.asmolenkov.tennismatchscoreboard.repository.FinishedMatchRepository;
import io.github.asmolenkov.tennismatchscoreboard.repository.PlayerRepository;
import io.github.asmolenkov.tennismatchscoreboard.service.FinishedMatchesPersistenceService;
import io.github.asmolenkov.tennismatchscoreboard.service.PlayerService;
import io.github.asmolenok.utils.TestUtils;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;

public class FinishedMatchesPersistenceServiceTest {
    private static SessionFactory testSf;
    private FinishedMatchesPersistenceService finishedMatches;
    private PlayerService playerService;

    @BeforeAll
    static void initDatabase() {
        testSf = new Configuration()
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:test_tennis_db;DB_CLOSE_DELAY=-1")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty("hibernate.show_sql", "true")
                .addAnnotatedClass(Match.class)
                .addAnnotatedClass(Player.class)
                .buildSessionFactory();
    }

    @BeforeEach
    void setUp() {
        FinishedMatchRepository finishedMatchRepository = new FinishedMatchRepository();
        finishedMatches = new FinishedMatchesPersistenceService(testSf, finishedMatchRepository);
        PlayerRepository playerRepository = new PlayerRepository();
        playerService = new PlayerService(playerRepository, testSf);
    }

    @Test
    @DisplayName("Матч сохранен")
    void saveMatch_success(){
        CurrentMatch currentMatch = TestUtils.createCompletedMatch();

        playerService.createPlayer("Sasha");
        playerService.createPlayer("Masha");

        finishedMatches.saveMatch(currentMatch);

        Match match = finishedMatches.findMathById(1L);

        Assertions.assertEquals(currentMatch.getPlayerOne().id(), match.getPlayerOne().getId());
        Assertions.assertEquals(currentMatch.getPlayerSecond().id(), match.getPlayerSecond().getId());
        Assertions.assertEquals(currentMatch.getPlayerOne().name(), match.getPlayerOne().getName());
        Assertions.assertEquals(currentMatch.getPlayerSecond().name(), match.getPlayerSecond().getName());
    }
}
