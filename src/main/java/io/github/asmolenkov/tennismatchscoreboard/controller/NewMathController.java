package io.github.asmolenkov.tennismatchscoreboard.controller;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import io.github.asmolenkov.tennismatchscoreboard.exception.DuplicateNameException;
import io.github.asmolenkov.tennismatchscoreboard.exception.NameIncorrectException;
import io.github.asmolenkov.tennismatchscoreboard.exception.PlayerCreationException;
import io.github.asmolenkov.tennismatchscoreboard.listener.AppContextListener;
import io.github.asmolenkov.tennismatchscoreboard.model.CurrentMatch;
import io.github.asmolenkov.tennismatchscoreboard.service.OngoingMatchesService;
import io.github.asmolenkov.tennismatchscoreboard.service.PlayerService;
import io.github.asmolenkov.tennismatchscoreboard.utils.ValidateUtil;
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
    private OngoingMatchesService ongoingMatchesService;

    public NewMathController() {
    }

    @Override
    public void init()  {
        ServletContext context = getServletContext();
        this.playerService = (PlayerService) context.getAttribute(AppContextListener.PLAYER_SERVICE_KEY);
        this.ongoingMatchesService = (OngoingMatchesService) context.getAttribute(AppContextListener.ONGOING_MATH_SERVICE_KEY);
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
        try {
            ValidateUtil.validateNamePlayer(nameOnePlayer);
            ValidateUtil.validateNamePlayer(nameSecondPlayer);
            ValidateUtil.validateNamesAreUnique(nameOnePlayer, nameSecondPlayer);

            PlayerDto playerDtoOne = playerService.createPlayer(nameOnePlayer);
            PlayerDto playerDtoSecond = playerService.createPlayer(nameSecondPlayer);
            log.info("Игроки Сохранены в БД");
            CurrentMatch currentMatch = ongoingMatchesService.createMatch(playerDtoOne, playerDtoSecond);

            //TODO Изменить на редирект
            resp.sendRedirect("/match-score?uuid=%s".formatted(currentMatch.getUuid()));

        } catch (NameIncorrectException | PlayerCreationException e) {
            errorMessage.add(e.getMessage());
            showErrorPage(req, resp, errorMessage, HttpServletResponse.SC_BAD_REQUEST);
        }catch (DuplicateNameException e){
            errorMessage.add(e.getMessage());
            showErrorPage(req, resp, errorMessage, HttpServletResponse.SC_CONFLICT);
        }
        catch (Exception e) {
            log.error("Unexpected error in player creation", e);
            errorMessage.add("Произошла внутренняя ошибка. Попробуйте позже.");
            showErrorPage(req, resp, errorMessage, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void showErrorPage(HttpServletRequest req, HttpServletResponse resp, List<String> errors, int codeError)
            throws ServletException, IOException {
        req.setAttribute("error", errors);
        req.setAttribute("name1", req.getParameter("name1"));
        req.setAttribute("name2", req.getParameter("name2"));
        resp.setStatus(codeError);
        req.getRequestDispatcher("/WEB-INF/views/NewMatch.jsp")
           .forward(req, resp);
    }
}
