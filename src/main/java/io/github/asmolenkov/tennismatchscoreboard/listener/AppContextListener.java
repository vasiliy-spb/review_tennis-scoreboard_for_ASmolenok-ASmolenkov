package io.github.asmolenkov.tennismatchscoreboard.listener;

import io.github.asmolenkov.tennismatchscoreboard.repository.ActiveMatchRepository;
import io.github.asmolenkov.tennismatchscoreboard.repository.FinishedMatchRepository;
import io.github.asmolenkov.tennismatchscoreboard.repository.PlayerRepository;
import io.github.asmolenkov.tennismatchscoreboard.service.FinishedMatchesPersistenceService;
import io.github.asmolenkov.tennismatchscoreboard.service.MatchScoreCalculationService;
import io.github.asmolenkov.tennismatchscoreboard.service.OngoingMatchesService;
import io.github.asmolenkov.tennismatchscoreboard.service.PlayerService;
import io.github.asmolenkov.tennismatchscoreboard.utils.HibernateUtils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.hibernate.SessionFactory;

@WebListener
public class AppContextListener implements ServletContextListener {

    // TODO: Класс не реализует метод `contextDestroyed`, который вызывается при остановке приложения.
        // В приложении есть ресурсы, которые требуют явного освобождения (например, `SessionFactory` в Hibernate, которая управляет пулом соединений).
        // Без реализации `contextDestroyed` нет гарантированного способа их закрыть.
        // Это приведёт к утечкам ресурсов, особенно в окружении сервера приложений, где приложение может многократно перезапускаться.

    // Все зависимости создаются и хранятся как конкретные классы, а не как интерфейсы.
        // Это нарушение Принципа инверсии зависимостей (DIP).

    // Объекты, которые нигде не запрашиваются из контекста, можно в него не помещать.

    // TODO: Ключи MATH_REPOSITORY_KEY = "mathRepository" и ONGOING_MATH_SERVICE_KEY = "mathRepository" одинаковые
        // Если бы ActiveMatchRepository запрашивался из контекста, это приводило бы к исключению,
        // так как он переписывается в контексте объектом OngoingMatchesService.

    public static final String PLAYER_SERVICE_KEY = "playerService";
    public static final String PLAYER_REPOSITORY_KEY = "playerRepository";
    public static final String MATH_REPOSITORY_KEY = "mathRepository"; // Опечатки: MATH —> MATCH, math —> match
    public static final String ONGOING_MATH_SERVICE_KEY = "mathRepository"; // Опечатки: MATH —> MATCH, math —> match
    public static final String MATCH_SCORE_CALCULATION_SERVICE_KEY = "matchScoreCalculation";
    public static final String FINISHED_MATCH_REPOSITORY_SERVICE_KEY = "finishedMatchRepository";
    public static final String FINISHED_MATCHES_PERSISTENCE_SERVICE_SERVICE_KEY = "finishedMatchesPersistenceService";



    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        PlayerRepository playerRepository = new PlayerRepository();
        SessionFactory sessionFactory = HibernateUtils.getSessionFactory();
        PlayerService playerService = new PlayerService(playerRepository, sessionFactory);
        ActiveMatchRepository activeMatchRepository = new ActiveMatchRepository();
        OngoingMatchesService ongoingMatchesService = new OngoingMatchesService(activeMatchRepository);
        FinishedMatchRepository finishedMatchRepository = new FinishedMatchRepository();
        MatchScoreCalculationService matchScoreCalculationService = new MatchScoreCalculationService(activeMatchRepository);
        FinishedMatchesPersistenceService finishedMatchesPersistenceService = new FinishedMatchesPersistenceService(sessionFactory, finishedMatchRepository);

        // Для помещения объектов в контекст можно использовать "естественные константы" — ClassName.class.getSimpleName() или ClassName.class.getName()
        context.setAttribute(PLAYER_SERVICE_KEY, playerService);
        context.setAttribute(PLAYER_REPOSITORY_KEY, playerRepository);
        context.setAttribute(MATH_REPOSITORY_KEY, activeMatchRepository);
        context.setAttribute(ONGOING_MATH_SERVICE_KEY, ongoingMatchesService);
        context.setAttribute(MATCH_SCORE_CALCULATION_SERVICE_KEY, matchScoreCalculationService);
        context.setAttribute(FINISHED_MATCH_REPOSITORY_SERVICE_KEY, finishedMatchRepository);
        context.setAttribute(FINISHED_MATCHES_PERSISTENCE_SERVICE_SERVICE_KEY, finishedMatchesPersistenceService);
    }
}
