package io.github.asmolenkov.tennismatchscoreboard.controller;


import io.github.asmolenkov.tennismatchscoreboard.exception.FindMatchException;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@WebServlet("/match-score")
public class MatchScoreController extends BaseServlet {
    private static final String PARAMETER_UUID = "uuid";
    private static final String PARAMETER_PLAYER_ID = "playerId";

    private static final String ATTRIBUTE_FINISHED_MATCH = "finishedMatch";
    private static final String ATTRIBUTE_CURRENT_MATCH = "currentMatch";

    private static final String PATH_FORWARD = "/WEB-INF/views/MatchScore.jsp";
    private static final String NAME_PAGE = "MatchScore";
    private static final String PATH_REDIRECT_TEMPLATE = "%s/match-score?uuid=%s";
    private static final String ID_PLAYER_EMPTY = "Id игрока не может быть пустым";
    private static final String ID_MUST_BE_NUMBER_TEMPLATE = "Id игрока быть только числом, получено %s";
    private static final String MATCH_NOT_FOUND = "Матч не найден";

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
        String uuid = req.getParameter(PARAMETER_UUID);

        UUID uuidMath = ValidateUtil.parseUuidOrThrow(uuid);

         Optional<CurrentMatch> findMatch = ongoingMatchesService.findMatchByUuid(uuidMath);

         CurrentMatch currentMatch = findMatch.orElseGet(()->{
             CurrentMatch finished = (CurrentMatch) req.getSession().getAttribute(ATTRIBUTE_FINISHED_MATCH);
             if(finished != null){
                 req.getSession().removeAttribute(ATTRIBUTE_FINISHED_MATCH);
             }
             return finished;
         });


        req.setAttribute(ATTRIBUTE_CURRENT_MATCH, currentMatch);

        req.getRequestDispatcher(PATH_FORWARD).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uuid = req.getParameter(PARAMETER_UUID);
        String playerId = req.getParameter(PARAMETER_PLAYER_ID);

        UUID uuidMap = ValidateUtil.parseUuidOrThrow(uuid);
        long id = parseLong(playerId);

        Optional<CurrentMatch> findMatch = ongoingMatchesService.findMatchByUuid(uuidMap);

        if(findMatch.isEmpty()){
            throw new FindMatchException(MATCH_NOT_FOUND);
        }

        CurrentMatch currentMatch = findMatch.get();

        matchScoreCalculationService.addPointToPlayer(currentMatch, id);



        if (currentMatch.isMatchFinished()) {
            finishedMatches.saveMatch(currentMatch);
            req.getSession().setAttribute(ATTRIBUTE_FINISHED_MATCH, currentMatch);
        }

        resp.sendRedirect(PATH_REDIRECT_TEMPLATE.formatted(req.getContextPath(), uuidMap));
    }

    private long parseLong(String playerId) {
        if (playerId == null || playerId.trim()
                                        .isEmpty()) {
            throw new PlayerIdException(ID_PLAYER_EMPTY);
        }
        try {
            return Long.parseLong(playerId);
        } catch (NumberFormatException e) {
            throw new PlayerIdException(ID_MUST_BE_NUMBER_TEMPLATE.formatted(playerId), e);
        }
    }

    @Override
    protected String getErrorPath() {
        return NAME_PAGE;
    }
}
