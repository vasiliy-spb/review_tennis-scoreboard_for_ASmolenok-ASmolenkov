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
}
