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
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@WebServlet("/match-score")
public class MatchScoreController extends BaseServlet {

    // Больше подошло бы название *Servlet

    // TODO: Сервлет работает с доменной моделью `CurrentMatch`.
        // Это нарушает границы между слоями приложения и Принцип разделения ответственности
        // (см. файл "separation-of-concerns-principle.md" в этом же пакете)
        // Сервлет не должен работать с доменными моделями.
        // Вместо этого он должен "общаться" с другими слоями через DTO.

    // TODO: Сервлет оркестрирует работу нескольких сервисов и знает об их бизнес-логике
        // (например, что сохраняется именно завершённый матч).
        // Это является признаком толстого контроллера
        // (см. файл "fat-controller.md" в этом же пакете)
        // Идеальная картина для этого сервлета — использовать только один метод сервиса в каждом методе —
        // отправлять ему входящие данные и получать ответ, который нужно отдать в представление.
        // А бизнес-логикой пусть управляет сервисный слой. Такой рефакторинг сделает контроллер "тонким"
        // и его единственной задачей останется обработка HTTP и делегирование бизнес-запроса сервисному слою.

    // TODO: Класс использует сессии для передачи доменной модели матча между запросами.
        // Это противоречит ТЗ: "Проект не многопользовательский, поэтому не используем сессии".

    private static final String PARAMETER_UUID = "uuid";
    private static final String PARAMETER_PLAYER_ID = "playerId";

    private static final String ATTRIBUTE_FINISHED_MATCH = "finishedMatch";
    private static final String ATTRIBUTE_CURRENT_MATCH = "currentMatch";

    private static final String PATH_FORWARD = "/WEB-INF/views/MatchScore.jsp";
    private static final String NAME_PAGE = "MatchScore";
    private static final String PATH_REDIRECT_TEMPLATE = "%s/match-score?uuid=%s";
    private static final String ID_PLAYER_EMPTY = "Player ID cannot be empty";
    private static final String ID_MUST_BE_NUMBER_TEMPLATE = "Player ID to be only a number, received %s";
    private static final String MATCH_NOT_FOUND = "No match found";

    private OngoingMatchesService ongoingMatchesService;
    private MatchScoreCalculationService matchScoreCalculationService;
    private FinishedMatchesPersistenceService finishedMatches;


    @Override
    public void init() {
        ServletContext context = getServletContext();

        // Для получения объектов из контекста можно использовать "естественные константы" — ClassName.class.getSimpleName() или ClassName.class.getName()
        this.ongoingMatchesService = (OngoingMatchesService) context.getAttribute(AppContextListener.ONGOING_MATH_SERVICE_KEY);
        this.matchScoreCalculationService = (MatchScoreCalculationService) context.getAttribute(AppContextListener.MATCH_SCORE_CALCULATION_SERVICE_KEY);
        this.finishedMatches = (FinishedMatchesPersistenceService) context.getAttribute(AppContextListener.FINISHED_MATCHES_PERSISTENCE_SERVICE_SERVICE_KEY);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuid = req.getParameter(PARAMETER_UUID);

        UUID uuidMath = ValidateUtil.parseUuidOrThrow(uuid);

        Optional<CurrentMatch> findMatch = ongoingMatchesService.findMatchByUuid(uuidMath);

        CurrentMatch currentMatch = findMatch.orElseGet(() -> getFinishedMatchFromSession(req));

        req.setAttribute(ATTRIBUTE_CURRENT_MATCH, currentMatch);

        req.getRequestDispatcher(PATH_FORWARD)
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uuid = req.getParameter(PARAMETER_UUID);
        String playerId = req.getParameter(PARAMETER_PLAYER_ID);

        UUID uuidMap = ValidateUtil.parseUuidOrThrow(uuid);
        long id = parseLong(playerId);

        Optional<CurrentMatch> findMatch = ongoingMatchesService.findMatchByUuid(uuidMap);

        if (findMatch.isPresent()) {
            CurrentMatch currentMatch = findMatch.get();
            matchScoreCalculationService.addPointToPlayer(currentMatch, id);
            if (currentMatch.isMatchFinished()) {
                finishedMatches.saveMatch(currentMatch);
                req.getSession()
                   .setAttribute(ATTRIBUTE_FINISHED_MATCH, currentMatch);
            }
            resp.sendRedirect(PATH_REDIRECT_TEMPLATE.formatted(req.getContextPath(), uuidMap));
        }else {
            throw new FindMatchException(MATCH_NOT_FOUND);
        }


    }

    @Override
    protected String getErrorPath() {
        return NAME_PAGE;
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

    private CurrentMatch getFinishedMatchFromSession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);

        // Тело блока if всегда нужно оборачивать в {}
        if (session == null) return null;

        CurrentMatch match = (CurrentMatch) session.getAttribute(ATTRIBUTE_FINISHED_MATCH);

        if (match != null) {
            session.removeAttribute(ATTRIBUTE_FINISHED_MATCH);
        }
        return match;
    }


}
