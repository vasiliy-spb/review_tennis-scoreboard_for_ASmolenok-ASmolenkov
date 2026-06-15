package io.github.asmolenkov.tennismatchscoreboard.controller;

import io.github.asmolenkov.tennismatchscoreboard.exception.FindMatchException;
import io.github.asmolenkov.tennismatchscoreboard.exception.NominateWinnerException;
import io.github.asmolenkov.tennismatchscoreboard.listener.AppContextListener;
import io.github.asmolenkov.tennismatchscoreboard.model.CurrentMatch;
import io.github.asmolenkov.tennismatchscoreboard.service.MatchScoreCalculationService;
import io.github.asmolenkov.tennismatchscoreboard.service.OngoingMatchesService;
import io.github.asmolenkov.tennismatchscoreboard.service.PlayerService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Slf4j
@WebServlet("/match-score")
public class MatchScoreController extends HttpServlet {
    private OngoingMatchesService ongoingMatchesService;
    private MatchScoreCalculationService matchScoreCalculationService;



    @Override
    public void init()  {
        ServletContext context = getServletContext();
        this.ongoingMatchesService = (OngoingMatchesService) context.getAttribute(AppContextListener.ONGOING_MATH_SERVICE_KEY);
        this.matchScoreCalculationService = (MatchScoreCalculationService) context.getAttribute(AppContextListener.MATCH_SCORE_CALCULATION_SERVICE_KEY);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       String uuid = req.getParameter("uuid");
       UUID uuidMath = UUID.fromString(uuid);
       try {
           CurrentMatch currentMatch = ongoingMatchesService.findMatchByUuid(uuidMath);
           req.setAttribute("currentMatch", currentMatch);
           log.info("Идет форвард на /WEB-INF/views/MatchScore.jsp");
           req.getRequestDispatcher("/WEB-INF/views/MatchScore.jsp").forward(req, resp);
       }catch (FindMatchException e){
           //TODO Реализовать централизацию обработки исключений.
       }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


            String uuid = req.getParameter("uuid");
            UUID uuidMap = UUID.fromString(uuid);
            String playerId = req.getParameter("playerId");
            long id = Long.parseLong(playerId); //TODO добавить обработку NullPointException

            CurrentMatch currentMatch = ongoingMatchesService.findMatchByUuid(uuidMap);

            log.info("ID игрока для начисления очка = {}", id);
            matchScoreCalculationService.addPointToPlayer(currentMatch, id);

            if(currentMatch.getMatchScore().isMatchFinished()){
                // Код для редиректа на страницу с результатами

            }else {
                req.setAttribute("currentMatch", currentMatch);
                log.info("Идет редирект после обновления счета на /WEB-INF/views/MatchScore.jsp");
                resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + uuid);
            }



    }
}
