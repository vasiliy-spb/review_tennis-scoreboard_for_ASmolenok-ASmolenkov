package io.github.asmolenkov.tennismatchscoreboard.controller;

import io.github.asmolenkov.tennismatchscoreboard.dto.MatchDto;
import io.github.asmolenkov.tennismatchscoreboard.dto.MatchesPage;
import io.github.asmolenkov.tennismatchscoreboard.listener.AppContextListener;
import io.github.asmolenkov.tennismatchscoreboard.repository.FinishedMatchRepository;
import io.github.asmolenkov.tennismatchscoreboard.service.FinishedMatchesPersistenceService;
import io.github.asmolenkov.tennismatchscoreboard.service.MatchScoreCalculationService;
import io.github.asmolenkov.tennismatchscoreboard.service.OngoingMatchesService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
@Slf4j
@WebServlet("/matches")
public class MatchesController extends HttpServlet {
    private FinishedMatchesPersistenceService finishedMatches;

    @Override
    public void init()  {
        ServletContext context = getServletContext();
        this.finishedMatches = (FinishedMatchesPersistenceService) context.getAttribute(AppContextListener.FINISHED_MATCHES_PERSISTENCE_SERVICE_SERVICE_KEY);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String playerName = req.getParameter("filterByPlayerName");
        int page = parseIntParam(req.getParameter("page"), 1);
        int size = parseIntParam(req.getParameter("size"), 3);

        MatchesPage result = finishedMatches.getMatchesPage(playerName, page, size);

        req.setAttribute("matches", result.getMatches());
        req.setAttribute("pageInfo", result.getPageInfo());
        req.setAttribute("currentSearch", playerName); // ⚠️ Важно для JSP!

        req.getRequestDispatcher("/WEB-INF/views/Matches.jsp").forward(req, resp);
    }

    private int parseIntParam(String param, int defaultValue) {
        if (param == null) return defaultValue;
        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
