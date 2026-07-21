package io.github.asmolenkov.tennismatchscoreboard.controller;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import io.github.asmolenkov.tennismatchscoreboard.listener.AppContextListener;
import io.github.asmolenkov.tennismatchscoreboard.model.CurrentMatch;
import io.github.asmolenkov.tennismatchscoreboard.service.OngoingMatchesService;
import io.github.asmolenkov.tennismatchscoreboard.service.PlayerService;
import io.github.asmolenkov.tennismatchscoreboard.utils.ValidateUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@WebServlet("/new-match")
public class NewMathController extends BaseServlet {

    // Опечатка: NewMathController —> NewMatchController

    // Больше подошло бы название *Servlet

    // TODO: Сервлет работает с доменной моделью `CurrentMatch`.
        // Это нарушает границы между слоями приложения и Принцип разделения ответственности
        // (см. файл "separation-of-concerns-principle.md" в этом же пакете)
        // Сервлет не должен работать с доменными моделями.
        // Вместо этого он должен "общаться" с другими слоями через DTO.

    // TODO: После валидации имён игроков, сервлет получает PlayerDto из PlayerService только для того, чтобы передать их в ongoingMatchesService.createMatch(playerDtoOne, playerDtoSecond).
        // Такая оркестрация работы сервисов является признаком толстого контроллера
        // (см. файл "fat-controller.md" в этом же пакете)
        // Идеальная картина для этого сервлета — использовать только один сервис (например, `OngoingMatchesService`) —
        // отправлять ему входящие данные и получать ответ, который нужно отдать в представление.
        // А логикой создания матча пусть управляет сервисный слой. Такой рефакторинг сделает контроллер "тонким"
        // и его единственной задачей останется обработка HTTP и делегирование бизнес-запроса сервисному слою.

    private static final String PAGE_PATH = "/WEB-INF/views/NewMatch.jsp";
    private static final String REDIRECT_PATH_TEMPLATE = "/match-score?uuid=%s";
    private static final String PAGE_NAME = "NewMatch";

    private static final String PARAMETER_PLAYER_ONE_NAME = "playerOneName";
    private static final String PARAMETER_PLAYER_TWO_NAME = "playerTwoName";


    private PlayerService playerService;
    private OngoingMatchesService ongoingMatchesService;

    // Пустой конструктор в java есть по умолчанию — можно не писать его явно
    public NewMathController() {
    }

    @Override
    public void init() {
        ServletContext context = getServletContext();

        // Для получения объектов из контекста можно использовать "естественные константы" — ClassName.class.getSimpleName() или ClassName.class.getName()
        this.playerService = (PlayerService) context.getAttribute(AppContextListener.PLAYER_SERVICE_KEY);
        this.ongoingMatchesService = (OngoingMatchesService) context.getAttribute(AppContextListener.ONGOING_MATH_SERVICE_KEY);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(PAGE_PATH)
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String nameOnePlayer = req.getParameter(PARAMETER_PLAYER_ONE_NAME);
        String nameSecondPlayer = req.getParameter(PARAMETER_PLAYER_TWO_NAME);

        String normalizedNameOne = nameOnePlayer.trim().toLowerCase();
        String normalizedNameSecond = nameSecondPlayer.trim().toLowerCase();

        // Можно создать DTO с именами обоих игроков и валидировать его вместо валидации имён по одиночке
        ValidateUtil.validateNamePlayer(normalizedNameOne);
        ValidateUtil.validateNamePlayer(normalizedNameSecond);
        ValidateUtil.validateNamesAreUnique(normalizedNameOne, normalizedNameSecond);

        PlayerDto playerDtoOne = playerService.createPlayer(normalizedNameOne);
        PlayerDto playerDtoSecond = playerService.createPlayer(normalizedNameSecond);

        CurrentMatch currentMatch = ongoingMatchesService.createMatch(playerDtoOne, playerDtoSecond);

        // Редирект происходит без учёта контекстного пути, поэтому если приложение
            // будет развёрнуто не в корне, пользователь увидит сообщение об ошибке
        resp.sendRedirect(REDIRECT_PATH_TEMPLATE.formatted(currentMatch.getUuid()));
    }

    @Override
    protected String getErrorPath() {
        return PAGE_NAME;
    }

}
