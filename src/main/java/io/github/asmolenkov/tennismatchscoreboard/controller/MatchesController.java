package io.github.asmolenkov.tennismatchscoreboard.controller;

import io.github.asmolenkov.tennismatchscoreboard.dto.MatchesPage;
import io.github.asmolenkov.tennismatchscoreboard.listener.AppContextListener;
import io.github.asmolenkov.tennismatchscoreboard.service.FinishedMatchesPersistenceService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
@Slf4j
@WebServlet("/matches")
public class MatchesController extends BaseServlet {

    private static final String PARAMETER_FILTER = "filterByPlayerName";
    private static final String PARAMETER_PAGE = "page";
    private static final String PARAMETER_SIZE = "size";

    private static final String ATTRIBUTE_MATCHES = "matches";
    private static final String ATTRIBUTE_PAGE_INFO = "pageInfo";
    private static final String ATTRIBUTE_CURRENT_SEARCH = "currentSearch";

    private static final String PATH_FILE = "/WEB-INF/views/Matches.jsp";
    private static final String VIEW_FILE_NAME = "Matches";

    private FinishedMatchesPersistenceService finishedMatches;

    @Override
    public void init()  {
        ServletContext context = getServletContext();
        this.finishedMatches = (FinishedMatchesPersistenceService) context.getAttribute(AppContextListener.FINISHED_MATCHES_PERSISTENCE_SERVICE_SERVICE_KEY);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String playerName = req.getParameter(PARAMETER_FILTER);
        int page = parseIntParam(req.getParameter(PARAMETER_PAGE), 1);
        int size = parseIntParam(req.getParameter(PARAMETER_SIZE), 3);

        MatchesPage result = finishedMatches.getMatchesPage(playerName, page, size);

        req.setAttribute(ATTRIBUTE_MATCHES, result.getMatches());
        req.setAttribute(ATTRIBUTE_PAGE_INFO, result.getPageInfo());
        req.setAttribute(ATTRIBUTE_CURRENT_SEARCH, playerName); // ⚠️ Важно для JSP!

        req.getRequestDispatcher(PATH_FILE).forward(req, resp);
    }

    private int parseIntParam(String param, int defaultValue) {
        if (param == null) return defaultValue;
        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    protected String getErrorPath() {
        return VIEW_FILE_NAME;
    }
}
