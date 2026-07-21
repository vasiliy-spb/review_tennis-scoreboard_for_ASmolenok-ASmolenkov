```text
Знаком ❗️ помечены критически важные замечания, а также места нарушения ТЗ.
```

## service

- ❗️В пакете отсутствуют интерфейсы для сервисных классов. Все классы являются конкретными реализациями, от которых напрямую зависят другие компоненты приложения (например, сервлеты).

Почему это проблема:

  - Нарушение Принципа инверсии зависимостей (Dependency Inversion Principle): Принцип гласит, что модули верхних уровней не должны зависеть от модулей нижних уровней, а также они должны зависеть от абстракций. В данном случае вышестоящие модули (сервлеты) напрямую зависят от конкретных реализаций сервисов, что делает систему жёстко связанной и хрупкой.

  - Низкая тестируемость: Невозможно провести полноценное модульное тестирование компонентов, которые зависят от этих сервисов. Например, чтобы протестировать сервлет, использующий `FinishedMatchesPersistenceService`, необходимо создавать полный экземпляр этого сервиса со всеми его реальными зависимостями, что превращает модульный тест в сложный интеграционный.

  - Низкая гибкость и невозможность расширения: Если потребуется создать альтернативную реализацию какого-либо сервиса, это потребует изменения кода во всех местах, где использовалась оригинальная реализация.

  - В классе-реализации публичные методы смешиваются с его внутренними или вспомогательными методами. Интерфейс же служит чётким, явным контрактом, который показывает, что сервис предоставляет внешнему миру, скрывая детали его внутренней работы.

Для каждого класса в этом пакете стоит создать интерфейс, который будет определять его публичный контракт, и изменить все зависимые классы так, чтобы они использовали этот интерфейс.

- Классы сервисов используют `sessionFactory.openSession()` для получения сессии. Такое ручное управление сессиями имеет несколько недостатков:

  - Дублирование кода: В каждом методе каждого сервиса, который работает с базой данных, вынуждено содержится один и тот же блок кода:

  ```java
  Session session = sessionFactory.getSession();
  Transaction transaction = null;
  try (session) {
      transaction = session.beginTransaction();
      // ... работа ...
      transaction.commit();
  } catch (Exception e) {
      // ... rollback ...
      throw ...;
  }
  ```

  Это громоздко и является нарушением принципа DRY (Don't Repeat Yourself).

  - Смешение ответственности: Сервисный слой, который должен отвечать исключительно за бизнес-логику, начинает заниматься низкоуровневыми техническими задачами — управлением сессиями и транзакциями. Это нарушает Принцип единственной ответственности (SRP).

  - Текущая реализация заставляет слой бизнес-логики (сервисы) напрямую зависеть от низкоуровневой детали реализации — `org.hibernate.Session` и делает его жёстко привязанным к Hibernate.

Один из вариантов исправления — перейти на паттерн "Session-per-Request" ("сессия на запрос"). Его суть в том, что одна сессия Hibernate используется всеми сервисами и репозиториями на протяжении всей обработки HTTP-запроса. А также провести рефакторинг.

<details>

<summary><b>💡 Вот как это можно реализовать 💡</b></summary>

---

Использовать `getCurrentSession()` (`sessionFactory.getCurrentSession()`) — этот метод возвращает сессию текущего контекста. Так в одном потоке (HTTP-запросе) будет использоваться одна и та же сессия. В этом случае закрытие сессии будет происходить автоматически (даже без try-with-resources).

Для получения сессии через `getCurrentSession()` в `hibernate.cfg.xml` уже есть свойство `hibernate.current_session_context_class`.

```xml
<property name="hibernate.current_session_context_class">thread</property> <!-- thread — для режима одна-сессия-на-поток -->
```

А также создать класс, который инкапсулирует всю логику открытия/закрытия транзакций.

В таком духе:

```java
public class TransactionalRunner {
    private final SessionFactory sessionFactory;

    public TransactionalRunner(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T run(Function<Session, T> command) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            T result = command.apply(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DataAccessException("Transaction failed", e);
        }
    }

    public void runInTransaction(Consumer<Session> command) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            command.accept(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DataAccessException("Transaction failed", e);
        }
    }
}
```

Внедрять его в сервисы как зависимость и использовать в необходимых методах.

---

Можно сказать, что подход с `TransactionalRunner` не устраняет нарушение SRP на 100%, но он значительно уменьшает степень этого нарушения.

В случае с транзакциями, ответственность сервиса можно разделить на два уровня:

1. Ответственность за демаркацию: Решение о том, какой именно кусок кода должен выполняться в рамках транзакции. То есть определение границ (`начало`...`конец`).

2. Ответственность за реализацию: Знание того, как именно запустить транзакцию, как сделать `commit` или `rollback`, как управлять сессией. Это низкоуровневая, техническая работа.

В предложенном подходе реализация полностью уходит в `TransactionalRunner`. Сервис больше не знает о `commit` и `rollback`. Сервис теперь говорит ЧТО сделать, а не КАК. Что является прагматичным и чистым подходом в проекте без Spring.

---

</details>

- ❗️В блоке `catch` вызов `transaction.rollback()` не обёрнут в `try-catch`.

Если во время отката транзакции произойдёт ещё одно исключение (например, из-за проблем с сетевым соединением с БД), это новое исключение "замаскирует" исходную ошибку, которая инициировала откат. В логах останется только ошибка отката, и разработчик не сможет узнать, что послужило первопричиной сбоя, что сильно усложняет отладку.

Стоит обернуть `transaction.rollback()` в собственный блок `try-catch` и, в случае ошибки, добавить новое исключение к исходному с помощью `originalException.addSuppressed(rollbackException)`.

<details>

<summary><b>💡 Например, так 💡</b></summary>

---

```java
private void safeRollback(Transaction transaction, Exception originalException) {
    if (transaction != null && transaction.isActive()) {
        try {
            transaction.rollback();
        } catch (Exception rollbackException) {
            originalException.addSuppressed(rollbackException);
        }
    }
}
```

</details>

