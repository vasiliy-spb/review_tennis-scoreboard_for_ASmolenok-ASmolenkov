package io.github.asmolenkov.tennismatchscoreboard.service;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import io.github.asmolenkov.tennismatchscoreboard.entity.Player;
import io.github.asmolenkov.tennismatchscoreboard.exception.PlayerCreationException;
import io.github.asmolenkov.tennismatchscoreboard.mapper.PlayerMapper;
import io.github.asmolenkov.tennismatchscoreboard.repository.PlayerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class PlayerService {

    // Даже если сейчас у класса есть только final поля, то всё равно лучше использовать
        // максимально "узкую" аннотацию `@RequiredArgsConstructor` вместо `@AllArgsConstructor`.
        // Чтобы при добавлении новых non-final полей они автоматически не попадали в параметры конструктора.

    // Можно константам с шаблонами для логирования дать суффикс LOG_TEMPLATE,
        // а сообщениям для исключений — ERROR_MESSAGE

    // TODO: Нет интерфейса для этого класса. (см. файл "service.md" в этом же пакете)

    // TODO: Класс вручную управляет сессиями и транзакциями
        // (см. файл "service.md" в этом же пакете)

    private static final String LOG_PLAYER_EXISTS_TEMPLATE = "Player {} already exists in the database!";
    private static final String LOG_PLAYER_SAVE_TEMPLATE = "Player {} is saved in the database!";
    private static final String ERROR_SAVE_PLAYER = "Player creation error";

    private final PlayerRepository playerRepository;
    private final SessionFactory sessionFactory;


    public PlayerDto createPlayer(String name)  {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Optional<Player> existingPlayer = playerRepository.findPlayer(name, session);

                return existingPlayer.map(player -> {
                                         transaction.rollback();
                                         log.info(LOG_PLAYER_EXISTS_TEMPLATE, name);
                                         return PlayerMapper.toDto(player);
                                     })
                                     .orElseGet(() -> {
                                         Player newPlayer = Player.builder()
                                                                  .name(name)
                                                                  .build();
                                         playerRepository.save(newPlayer, session);
                                         log.info(LOG_PLAYER_SAVE_TEMPLATE, name);
                                         transaction.commit();
                                         return PlayerMapper.toDto(newPlayer);
                                     });
            } catch (Exception e) {

                // TODO: Перед откатом транзакции надо проверить, что она активна (isActive())
                // TODO: Откат транзакции тоже должен выполняться в блоке try-catch (см. файл "service.md" в этом же пакете)
                transaction.rollback();
                throw new PlayerCreationException(ERROR_SAVE_PLAYER, e);
            }
        }
    }
}
