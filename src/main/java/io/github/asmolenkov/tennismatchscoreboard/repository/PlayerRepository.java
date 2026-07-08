package io.github.asmolenkov.tennismatchscoreboard.repository;

import io.github.asmolenkov.tennismatchscoreboard.entity.Player;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class PlayerRepository {

    private static final String JPQL_FIND_PLAYER = "FROM Player p WHERE p.name = :name";
    private static final String PARAMETER_NAME = "name";

    public void save(Player player, Session session) {
        session.persist(player);
    }

    public Optional<Player> findPlayer(String name, Session session) {
        List<Player> players = session.createQuery(JPQL_FIND_PLAYER, Player.class)
                                      .setParameter(PARAMETER_NAME, name)
                                      .getResultList();

        return players.isEmpty() ? Optional.empty() : Optional.of(players.getFirst());
    }
}
