package io.github.asmolenok.service;

import io.github.asmolenkov.tennismatchscoreboard.entity.Player;
import io.github.asmolenkov.tennismatchscoreboard.repository.PlayerRepository;
import io.github.asmolenkov.tennismatchscoreboard.service.PlayerService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerServiceTest {
    private static SessionFactory testSf;
    private PlayerService playerService;
    private PlayerRepository playerRepo;

    @BeforeAll
    static void initDatabase() {

        testSf = new Configuration()
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:test_tennis_db;DB_CLOSE_DELAY=-1")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty("hibernate.show_sql", "true")
                .addAnnotatedClass(Player.class)
                .buildSessionFactory();
    }

    @AfterAll
    static void closeDatabase() {
        if (testSf != null) testSf.close();
    }

    @BeforeEach
    void setUp() {
        playerRepo = new PlayerRepository();
        playerService = new PlayerService(playerRepo, testSf);
    }

    @Test
    @DisplayName("Успешное создание игрока")
    void createPlayer_success() {

        playerService.createPlayer("Иван");


        try (Session session = testSf.openSession()) {
            Long count = session.createQuery(
                                        "SELECT COUNT(p) FROM Player p WHERE p.name = :name", Long.class)
                                .setParameter("name", "Иван")
                                .getSingleResult();

            assertEquals(1L, count, "Игрок должен быть сохранен в БД");

        }
    }

    @Test
    @DisplayName("⚠️ Обработка дубликата имени")
    void createPlayer_duplicateName() {

        playerService.createPlayer("Мария");


        playerService.createPlayer("Мария");


        try (Session session = testSf.openSession()) {
            Long count = session.createQuery(
                                        "SELECT COUNT(p) FROM Player p WHERE p.name = :name", Long.class)
                                .setParameter("name", "Мария")
                                .getSingleResult();

            assertEquals(1L, count, "Дубликат не должен сохраниться");
        }
    }
}
