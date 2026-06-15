package io.github.asmolenkov.tennismatchscoreboard.listener;

import io.github.asmolenkov.tennismatchscoreboard.repository.ActiveMatchRepository;
import io.github.asmolenkov.tennismatchscoreboard.repository.PlayerRepository;
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
    public static final String PLAYER_SERVICE_KEY = "playerService";
    public static final String PLAYER_REPOSITORY_KEY = "playerRepository";
    public static final String MATH_REPOSITORY_KEY = "mathRepository";
    public static final String ONGOING_MATH_SERVICE_KEY = "mathRepository";
    public static final String MATCH_SCORE_CALCULATION_SERVICE_KEY = "matchScoreCalculation";



    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        PlayerRepository playerRepository = new PlayerRepository();
        SessionFactory sessionFactory = HibernateUtils.getSessionFactory();
        PlayerService playerService = new PlayerService(playerRepository, sessionFactory);
        ActiveMatchRepository activeMatchRepository = new ActiveMatchRepository();
        OngoingMatchesService ongoingMatchesService = new OngoingMatchesService(activeMatchRepository);
        MatchScoreCalculationService matchScoreCalculationService = new MatchScoreCalculationService();
        context.setAttribute(PLAYER_SERVICE_KEY, playerService);
        context.setAttribute(PLAYER_REPOSITORY_KEY, playerRepository);
        context.setAttribute(MATH_REPOSITORY_KEY, activeMatchRepository);
        context.setAttribute(ONGOING_MATH_SERVICE_KEY, ongoingMatchesService);
        context.setAttribute(MATCH_SCORE_CALCULATION_SERVICE_KEY, matchScoreCalculationService);
    }
}
