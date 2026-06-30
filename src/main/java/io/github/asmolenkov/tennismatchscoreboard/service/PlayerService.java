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

    private static final String LOG_PLAYER_EXISTS_TEMPLATE = "Игрок {} уже существует в БД!";
    private static final String LOG_PLAYER_SAVE_TEMPLATE = "Игрок {} сохранен в БД!";
    private static final String ERROR_SAVE_PLAYER = "Ошибка создания игрока";

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
                transaction.rollback();
                throw new PlayerCreationException(ERROR_SAVE_PLAYER, e);
            }
        }
    }
}
