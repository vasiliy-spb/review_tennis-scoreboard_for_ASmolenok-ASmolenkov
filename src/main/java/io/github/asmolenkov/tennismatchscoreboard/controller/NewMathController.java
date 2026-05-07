package io.github.asmolenkov.tennismatchscoreboard.controller;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import io.github.asmolenkov.tennismatchscoreboard.entity.Player;
import io.github.asmolenkov.tennismatchscoreboard.exception.DuplicateNameException;
import io.github.asmolenkov.tennismatchscoreboard.listener.AppContextListener;
import io.github.asmolenkov.tennismatchscoreboard.service.PlayerService;
import jakarta.servlet.ServletConfig;
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
@Slf4j
@WebServlet("/new-math")
public class NewMathController extends HttpServlet {

    private PlayerService playerService;

    public NewMathController() {
    }

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        this.playerService = (PlayerService) context.getAttribute(AppContextListener.PLAYER_SERVICE_KEY);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/NewMatch.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nameOnePlayer = req.getParameter("name1");
        String nameSecondPlayer = req.getParameter("name2");
        List<String> errorMessage = new ArrayList<>();
        if (nameOnePlayer.trim()
                         .isEmpty() || nameOnePlayer.length() > 30) {
            errorMessage.add("Некорректное имя Игрока #1!");
        }

        if (nameSecondPlayer.trim()
                            .isEmpty() || nameSecondPlayer.length() > 30) {
            errorMessage.add("Некорректное имя Игрока #2!");
        }
        if (nameSecondPlayer.equalsIgnoreCase(nameOnePlayer)) {
            errorMessage.add("Имена игроков не могут быть одинаковы!");
        }
        //TODO Добавить проверку на цифры и прочие знаки

        if (!errorMessage.isEmpty()) {
            req.setAttribute("error", errorMessage);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            req.getRequestDispatcher("/WEB-INF/views/NewMatch.jsp")
               .forward(req, resp);
            return;
        }
        PlayerDto playerDtoOne = playerService.createPlayer(nameOnePlayer);
        PlayerDto playerDtoSecond = playerService.createPlayer(nameSecondPlayer);
        log.info("Игроки Сохранены в БД");
        req.setAttribute("PlayerOneName", playerDtoOne.name());
        req.setAttribute("PlayerSecondName", playerDtoSecond.name());
        //TODO Изменить на редирект
        req.getRequestDispatcher("/WEB-INF/views/MatchScore.jsp")
           .forward(req, resp);
    }
}
