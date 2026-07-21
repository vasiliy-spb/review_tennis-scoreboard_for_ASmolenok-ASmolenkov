package io.github.asmolenkov.tennismatchscoreboard.repository;

import io.github.asmolenkov.tennismatchscoreboard.entity.Match;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

@Slf4j
public class FinishedMatchRepository {

    // TODO: Нет интерфейса для этого класса.
        // Это нарушение Принципа инверсии зависимостей (Dependency Inversion Principle):
        // Принцип гласит, что модули верхних уровней не должны зависеть от модулей нижних уровней,
        // а также они должны зависеть от абстракций. В данном случае вышестоящие модули (сервисы)
        // напрямую зависят от конкретных реализаций репозиториев, что делает систему жёстко связанной и хрупкой.

    // TODO: Текущая реализация методов заставляет слой сервисов напрямую зависеть от низкоуровневой детали реализации —
        // `org.hibernate.Session` и делает его жёстко привязанным к Hibernate.
        // Лучше внедрять в репозиторий объект SessionFactory как зависимость и в методах получать из неё объект текущей сессии,
        // а не принимать в качестве аргумента в методы.
        // Стоит придумать, как оставить в сервисном слое управление транзакциями, но при этом избавить его от зависимости от Hibernate (`Session`).

    // В HQL запросах используется JOIN FETCH, что эквивалентно 'INNER JOIN' в SQL.
        //
        // `INNER JOIN` вернёт только те записи о матчах, у которых все связанные сущности (`player1`, `player2`)
        // гарантированно существуют в базе. Если по какой-либо причине (например, ошибка при импорте или
        // ручное вмешательство) в таблице `matches` окажется запись со значением `NULL` в колонке `player1`,
        // то такой матч будет молчаливо исключён из выборки.
        //
        // `LEFT JOIN` является более безопасным подходом:
            //  - Он вернёт все матчи, даже если у них нарушена связь с игроком.
            //  - Это позволит приложению либо упасть с `NullPointerException` (что явно укажет на проблему
                //  с целостностью данных), либо корректно обработать такую ситуацию, если она допустима.
                //  "Падать громко и рано" часто лучше, чем молча скрывать проблемы.
        //
        // Стоит заменить `JOIN FETCH` на `LEFT JOIN FETCH` для обоих игроков
        // для большей устойчивости запроса к потенциально некорректным данным.
        //
        // (см. файл "join-fetch-left-join-fetch.md" в этом же пакете)

    // TODO: Тело каждого метода стоит обернуть в try-catch и отлавливать исключения при работе с БД.
        // Слой репозиториев должен перехватывать специфичные для технологии исключения
        // и оборачивать их в свои исключения слоя доступа к данным.
        // Это скрывает детали реализации от верхних слоёв и делает их независимыми от деталей реализации репозиториев.

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
                                         .setParameter("id", id) // Строковый литерал "id" тоже можно вынести в константу
                                         .getResultList(); // Лучше использовать специальный метод для получения единственного значения: .uniqueResultOptional()

        return findMatches.isEmpty() ? Optional.empty() : Optional.of(findMatches.getFirst());
    }


    // Можно findAll
    public List<Match> findWithPagination(Session session, int offset, int limit) {

        return session.createQuery(JPQL_FIND_PAGINATION_MATCH, Match.class)
                      .setFirstResult(offset)
                      .setMaxResults(limit)
                      .getResultList();
    }

    // Можно countAll
    public long countTotal(Session session) {
        return session.createQuery(JPQL_COUNT_TOTAL, Long.class)
                      .getSingleResult();
    }

    // Можно findByPlayerName
    public List<Match> findByNameWithPagination(Session session, String name, int offset, int limit) {
        return session.createQuery(JPQL_FIND_BY_NAME_PAGINATION_MATCH, Match.class)
                      .setParameter(PARAMETER_NAME, "%" + name + "%")
                      .setFirstResult(offset)
                      .setMaxResults(limit)
                      .getResultList();
    }

    // Можно countByPlayerName
    public long countByName(Session session, String name) {
        return session.createQuery(JPQL_COUNT_BY_NAME_TOTAL, Long.class)
                      .setParameter(PARAMETER_NAME, "%" + name + "%")
                      .getSingleResult();
    }
}
