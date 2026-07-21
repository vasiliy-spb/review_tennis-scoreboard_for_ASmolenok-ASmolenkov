package io.github.asmolenkov.tennismatchscoreboard.repository;

import io.github.asmolenkov.tennismatchscoreboard.entity.Player;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class PlayerRepository {

    // TODO: Нет интерфейса для этого класса.
        // Это нарушение Принципа инверсии зависимостей (Dependency Inversion Principle):
        // Принцип гласит, что модули верхних уровней не должны зависеть от модулей нижних уровней,
        // а также они должны зависеть от абстракций. В данном случае вышестоящие модули (сервисы)
        // напрямую зависят от конкретных реализаций репозиториев, что делает систему жёстко связанной и хрупкой.

    // Текст JPQL запроса удобнее читать, когда он логично разбит на строки, даже если он короткий.
        // Для визуального разделения запросов на строки лучше использовать текстовые блоки

    // TODO: Текущая реализация методов заставляет слой сервисов напрямую зависеть от низкоуровневой детали реализации —
        // `org.hibernate.Session` и делает его жёстко привязанным к Hibernate.
        // Лучше внедрять в репозиторий объект SessionFactory как зависимость и в методах получать из неё объект текущей сессии,
        // а не принимать в качестве аргумента в методы.
        // Стоит придумать, как оставить в сервисном слое управление транзакциями, но при этом избавить его от зависимости от Hibernate (`Session`).

    // TODO: Тело каждого метода стоит обернуть в try-catch и отлавливать исключения при работе с БД.
        // Слой репозиториев должен перехватывать специфичные для технологии исключения
        // и оборачивать их в свои исключения слоя доступа к данным.
        // Это скрывает детали реализации от верхних слоёв и делает их независимыми от деталей реализации репозиториев.

    private static final String JPQL_FIND_PLAYER = "FROM Player p WHERE p.name = :name";
    private static final String PARAMETER_NAME = "name";

    public void save(Player player, Session session) {
        session.persist(player);
    }

    public Optional<Player> findPlayer(String name, Session session) {
        List<Player> players = session.createQuery(JPQL_FIND_PLAYER, Player.class)
                                      .setParameter(PARAMETER_NAME, name)
                                      .getResultList(); // Лучше использовать специальный метод для получения единственного значения: .uniqueResultOptional()

        return players.isEmpty() ? Optional.empty() : Optional.of(players.getFirst());
    }
}
