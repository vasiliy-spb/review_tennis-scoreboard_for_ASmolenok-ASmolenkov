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

    private static final String PAGE_PATH = "/WEB-INF/views/NewMatch.jsp";
    private static final String REDIRECT_PATH_TEMPLATE = "/match-score?uuid=%s";
    private static final String PAGE_NAME = "NewMatch";

    private PlayerService playerService;
    private OngoingMatchesService ongoingMatchesService;

    public NewMathController() {
    }

    @Override
    public void init() {
        ServletContext context = getServletContext();
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
        String nameOnePlayer = req.getParameter("playerOneName");
        String nameSecondPlayer = req.getParameter("playerTwoName");

        String normalizedNameOne = nameOnePlayer.trim().toLowerCase();
        String normalizedNameSecond = nameSecondPlayer.trim().toLowerCase();

        ValidateUtil.validateNamePlayer(normalizedNameOne);
        ValidateUtil.validateNamePlayer(normalizedNameSecond);
        ValidateUtil.validateNamesAreUnique(normalizedNameOne, normalizedNameSecond);

        PlayerDto playerDtoOne = playerService.createPlayer(normalizedNameOne);
        PlayerDto playerDtoSecond = playerService.createPlayer(normalizedNameSecond);

        CurrentMatch currentMatch = ongoingMatchesService.createMatch(playerDtoOne, playerDtoSecond);

        resp.sendRedirect(REDIRECT_PATH_TEMPLATE.formatted(currentMatch.getUuid()));
    }

    @Override
    protected String getErrorPath() {
        return PAGE_NAME;
    }

}
