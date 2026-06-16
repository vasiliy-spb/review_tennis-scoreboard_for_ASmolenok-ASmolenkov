package io.github.asmolenkov.tennismatchscoreboard.service;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import io.github.asmolenkov.tennismatchscoreboard.entity.Player;
import io.github.asmolenkov.tennismatchscoreboard.exception.PlayerCreationException;
import io.github.asmolenkov.tennismatchscoreboard.mapper.PlayerMapper;
import io.github.asmolenkov.tennismatchscoreboard.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Optional;

@Slf4j
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final SessionFactory sessionFactory;


    public PlayerService(PlayerRepository playerRepository, SessionFactory sessionFactory) {
        this.playerRepository = playerRepository;
        this.sessionFactory = sessionFactory;
    }

    public PlayerDto createPlayer(String name)  {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Optional<Player> existingPlayer = playerRepository.findPlayer(name, session);

                return existingPlayer.map(player -> {
                                         transaction.rollback();
                                         log.info("Игрок {} уже существует в БД!", name);
                                         return PlayerMapper.toDto(player);
                                     })
                                     .orElseGet(() -> {
                                         Player newPlayer = Player.builder()
                                                                  .name(name)
                                                                  .build();
                                         playerRepository.save(newPlayer, session);
                                         log.info("Игрок {} сохранен в БД!", name);
                                         transaction.commit();
                                         return PlayerMapper.toDto(newPlayer);
                                     });
            } catch (Exception e) {
                transaction.rollback();
                throw new PlayerCreationException("Ошибка создания игрока");
            }
        }
    }


}
