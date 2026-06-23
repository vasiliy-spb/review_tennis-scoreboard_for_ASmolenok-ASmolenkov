package io.github.asmolenkov.tennismatchscoreboard.repository;

import io.github.asmolenkov.tennismatchscoreboard.entity.Match;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

@Slf4j
public class FinishedMatchRepository {

    public void save (Match match, Session session){
        session.persist(match);
    }

    public Optional <Match> find(long id, Session session){
        String jpql = "FROM Match m WHERE m.id = :id";

        List<Match> findMatches = session.createQuery(jpql, Match.class).setParameter("id", id).getResultList();

        return findMatches.isEmpty() ? Optional.empty() : Optional.of(findMatches.getFirst());
    }

    public List <Match> find(String playerName, Session session){
        String jpql = "FROM Match m WHERE LOWER(m.playerOne.name) LIKE LOWER(:name) OR LOWER(m.playerSecond.name) LIKE LOWER(:name)";

        List<Match> findMatches = session.createQuery(jpql, Match.class).setParameter("name", playerName).getResultList();
        log.info("Количество матчей с игроком {} = {}", playerName, findMatches.size());

        return findMatches;
    }

    public List <Match> find(Session session){
        String jpql = """
        SELECT m FROM Match m 
        JOIN FETCH m.playerOne 
        JOIN FETCH m.playerSecond
        JOIN FETCH m.winner
        """;

        List<Match> findMatches = session.createQuery(jpql, Match.class).getResultList();
        log.info("Количество матчей = {}", findMatches.size());

        return findMatches;
    }
}
