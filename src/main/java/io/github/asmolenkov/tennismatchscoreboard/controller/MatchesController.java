package io.github.asmolenkov.tennismatchscoreboard.controller;

import io.github.asmolenkov.tennismatchscoreboard.dto.MatchDto;
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
        String findPlayerName = req.getParameter("playerName");

        if(findPlayerName == null || findPlayerName.trim().isEmpty()){
            List<MatchDto> allMatches = finishedMatches.findAll();
            req.setAttribute("matches", allMatches);
            req.getRequestDispatcher("/WEB-INF/views/Matches.jsp").forward(req,resp);
        }

        List<MatchDto> matches = finishedMatches.findMatchesByName(findPlayerName);

        req.setAttribute("matches", matches);
        req.getRequestDispatcher("/WEB-INF/views/Matches.jsp").forward(req,resp);
    }
}
