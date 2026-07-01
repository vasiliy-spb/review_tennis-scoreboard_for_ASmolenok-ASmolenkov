package io.github.asmolenkov.tennismatchscoreboard.controller;

import io.github.asmolenkov.tennismatchscoreboard.exception.PlayerIdException;
import io.github.asmolenkov.tennismatchscoreboard.listener.AppContextListener;
import io.github.asmolenkov.tennismatchscoreboard.model.CurrentMatch;
import io.github.asmolenkov.tennismatchscoreboard.service.FinishedMatchesPersistenceService;
import io.github.asmolenkov.tennismatchscoreboard.service.MatchScoreCalculationService;
import io.github.asmolenkov.tennismatchscoreboard.service.OngoingMatchesService;
import io.github.asmolenkov.tennismatchscoreboard.utils.ValidateUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@WebServlet("/match-score")
public class MatchScoreController extends HttpServlet {
    private OngoingMatchesService ongoingMatchesService;
    private MatchScoreCalculationService matchScoreCalculationService;
    private FinishedMatchesPersistenceService finishedMatches;


    @Override
    public void init() {
        ServletContext context = getServletContext();
        this.ongoingMatchesService = (OngoingMatchesService) context.getAttribute(AppContextListener.ONGOING_MATH_SERVICE_KEY);
        this.matchScoreCalculationService = (MatchScoreCalculationService) context.getAttribute(AppContextListener.MATCH_SCORE_CALCULATION_SERVICE_KEY);
        this.finishedMatches = (FinishedMatchesPersistenceService) context.getAttribute(AppContextListener.FINISHED_MATCHES_PERSISTENCE_SERVICE_SERVICE_KEY);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuid = req.getParameter("uuid");

        UUID uuidMath = ValidateUtil.parseUuidOrThrow(uuid);

        CurrentMatch currentMatch = ongoingMatchesService.findMatchByUuid(uuidMath);
        req.setAttribute("currentMatch", currentMatch);

        req.getRequestDispatcher("/WEB-INF/views/MatchScore.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uuid = req.getParameter("uuid");
        String playerId = req.getParameter("playerId");

        UUID uuidMap = ValidateUtil.parseUuidOrThrow(uuid);
        long id = parseLong(playerId);

        CurrentMatch currentMatch = ongoingMatchesService.findMatchByUuid(uuidMap);

        matchScoreCalculationService.addPointToPlayer(currentMatch, id);
        if (currentMatch.isMatchFinished()) {
            finishedMatches.saveMatch(currentMatch);
        }

        req.setAttribute("currentMatch", currentMatch);
        log.info("Идет редирект после обновления счета на /WEB-INF/views/MatchScore.jsp");
        resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + uuid);

    }

    private long parseLong(String playerId) {
        if (playerId == null || playerId.trim().isEmpty()) {
            throw new PlayerIdException("Id игрока не может быть пустым");
        }
        try {
            return Long.parseLong(playerId);
        } catch (NumberFormatException e) {
            throw new PlayerIdException("Id игрока быть только числом, получено %s".formatted(playerId), e);
        }
    }

}
