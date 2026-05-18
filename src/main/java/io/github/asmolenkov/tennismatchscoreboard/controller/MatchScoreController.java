package io.github.asmolenkov.tennismatchscoreboard.controller;

import io.github.asmolenkov.tennismatchscoreboard.listener.AppContextListener;
import io.github.asmolenkov.tennismatchscoreboard.model.CurrentMatch;
import io.github.asmolenkov.tennismatchscoreboard.service.OngoingMatchesService;
import io.github.asmolenkov.tennismatchscoreboard.service.PlayerService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/match-score")
public class MatchScoreController extends HttpServlet {
    private OngoingMatchesService ongoingMatchesService;



    @Override
    public void init()  {
        ServletContext context = getServletContext();
        this.ongoingMatchesService = (OngoingMatchesService) context.getAttribute(AppContextListener.ONGOING_MATH_SERVICE_KEY);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       String uuid = req.getParameter("uuid");
       UUID uuidMath = UUID.fromString(uuid);
       CurrentMatch currentMatch = ongoingMatchesService.findMatchByUuid(uuidMath);


    }
}
