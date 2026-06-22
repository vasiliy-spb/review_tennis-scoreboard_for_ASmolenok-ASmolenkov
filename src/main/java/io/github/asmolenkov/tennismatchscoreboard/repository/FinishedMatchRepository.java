package io.github.asmolenkov.tennismatchscoreboard.repository;

import io.github.asmolenkov.tennismatchscoreboard.entity.Match;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class FinishedMatchRepository {

    public void save (Match match, Session session){
        session.persist(match);
    }

    public Optional <Match> find(long id, Session session){
        String jpql = "FROM Match m WHERE m.id = :id";

        List<Match> findMatches = session.createQuery(jpql, Match.class).setParameter("id", id).getResultList();

        return findMatches.isEmpty() ? Optional.empty() : Optional.of(findMatches.getFirst());
    }

    public Optional <Match> find(String playerName, Session session){
        String jpql = "FROM Match m WHERE LOWER(m.playerOne.name) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(m.playerSecond.name) LIKE LOWER(CONCAT('%', :name, '%'))";

        List<Match> findMatches = session.createQuery(jpql, Match.class).setParameter("name", playerName).getResultList();

        return findMatches.isEmpty() ? Optional.empty() : Optional.of(findMatches.getFirst());
    }
}
