package io.github.asmolenkov.tennismatchscoreboard.repository;

import io.github.asmolenkov.tennismatchscoreboard.entity.Match;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

@Slf4j
public class FinishedMatchRepository {

    private static final String JPQL_FIND_MATCH = """
            FROM Match m WHERE m.id = :id
            """;
    private static final String JPQL_FIND_PAGINATION_MATCH = """
            SELECT m FROM Match m
            JOIN FETCH m.playerOne
            JOIN FETCH m.playerSecond
            LEFT JOIN FETCH m.winner
            ORDER BY m.id DESC  /* Новые матчи сверху */
            """;
    private static final String JPQL_COUNT_TOTAL = "SELECT COUNT(m) FROM Match m";
    private static final String JPQL_FIND_BY_NAME_PAGINATION_MATCH = """
            SELECT m FROM Match m
            JOIN FETCH m.playerOne
            JOIN FETCH m.playerSecond
            LEFT JOIN FETCH m.winner
            WHERE LOWER(m.playerOne.name) LIKE LOWER(:name)
               OR LOWER(m.playerSecond.name) LIKE LOWER(:name)
            ORDER BY m.id DESC
            """;
    private static final String JPQL_COUNT_BY_NAME_TOTAL = """
            SELECT COUNT(m) FROM Match m
            WHERE LOWER(m.playerOne.name) LIKE LOWER(:name)
               OR LOWER(m.playerSecond.name) LIKE LOWER(:name)
            """;

    private static final String PARAMETER_NAME = "name";


    public void save(Match match, Session session) {
        session.persist(match);
    }

    public Optional<Match> find(long id, Session session) {

        List<Match> findMatches = session.createQuery(JPQL_FIND_MATCH, Match.class)
                                         .setParameter("id", id)
                                         .getResultList();

        return findMatches.isEmpty() ? Optional.empty() : Optional.of(findMatches.getFirst());
    }


    public List<Match> findWithPagination(Session session, int offset, int limit) {

        return session.createQuery(JPQL_FIND_PAGINATION_MATCH, Match.class)
                      .setFirstResult(offset)
                      .setMaxResults(limit)
                      .getResultList();
    }

    public long countTotal(Session session) {
        return session.createQuery(JPQL_COUNT_TOTAL, Long.class)
                      .getSingleResult();
    }

    public List<Match> findByNameWithPagination(Session session, String name, int offset, int limit) {
        return session.createQuery(JPQL_FIND_BY_NAME_PAGINATION_MATCH, Match.class)
                      .setParameter(PARAMETER_NAME, "%" + name + "%")
                      .setFirstResult(offset)
                      .setMaxResults(limit)
                      .getResultList();
    }

    public long countByName(Session session, String name) {
        return session.createQuery(JPQL_COUNT_BY_NAME_TOTAL, Long.class)
                      .setParameter(PARAMETER_NAME, "%" + name + "%")
                      .getSingleResult();
    }
}
