package io.github.asmolenkov.tennismatchscoreboard.repository;

import io.github.asmolenkov.tennismatchscoreboard.entity.Player;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class PlayerRepository {

    public void save(Player player, Session session) {
        session.persist(player);
    }

    public boolean existsByName(String name, Session session) {

        String jpql = "SELECT 1 FROM Player p WHERE p.name = :name";
        long result = session.createQuery(jpql, Player.class)
                             .setParameter("name", name)
                             .setMaxResults(1)
                             .getResultCount();

        return result > 0;
    }

    public Optional<Player> findPlayer(String name, Session session) {
        String jpql = "FROM Player p WHERE p.name = :name";

        List<Player> players = session.createQuery(jpql, Player.class)
                                      .setParameter("name", name)
                                      .getResultList();

        return players.isEmpty() ? Optional.empty() : Optional.of(players.getFirst());
    }
}
