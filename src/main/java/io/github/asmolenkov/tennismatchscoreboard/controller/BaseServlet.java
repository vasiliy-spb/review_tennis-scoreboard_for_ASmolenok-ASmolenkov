package io.github.asmolenkov.tennismatchscoreboard.controller;

import io.github.asmolenkov.tennismatchscoreboard.exception.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
@Slf4j
public abstract class BaseServlet extends HttpServlet {

    protected abstract String getErrorPath();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       try {
           super.service(req, resp);
       }catch (NameIncorrectException    | PlayerSideException e){
           log.warn("Validation error [{}] on {}: {}",
                   e.getClass().getSimpleName(),
                   req.getRequestURI(),
                   e.getMessage());
        handleError(req, resp, getErrorPath(),e.getMessage(),HttpServletResponse.SC_BAD_REQUEST);
       }catch (DuplicateNameException e){
           log.warn("Conflict error [{}] on {}: {}",
                   e.getClass().getSimpleName(),
                   req.getRequestURI(),
                   e.getMessage());
           handleError(req, resp, getErrorPath(),e.getMessage(),HttpServletResponse.SC_CONFLICT);
       }catch (FindMatchException e ){
           log.warn("Not found error [{}] on {}: {}",
                   e.getClass().getSimpleName(),
                   req.getRequestURI(),
                   e.getMessage());
           handleError(req, resp, getErrorPath(),e.getMessage(),HttpServletResponse.SC_NOT_FOUND);
       }catch (PlayerCreationException  | SaveMatchException e){
           log.error("Internal Server Error [{}] on {}: {}",
                   e.getClass().getSimpleName(),
                   req.getRequestURI(),
                   e.getMessage(),
                   e);
           handleError(req, resp, getErrorPath(),e.getMessage(),HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
       }
    }

    private void handleError(HttpServletRequest req, HttpServletResponse resp, String viewName, String errorMessage, int status) throws IOException, ServletException {
        req.setAttribute("error", errorMessage);
        resp.setStatus(status);
        renderView(req, resp, viewName);
    }

    protected void renderView(HttpServletRequest req, HttpServletResponse resp, String viewName) throws ServletException, IOException {
        String path = "/WEB-INF/views/%s.jsp".formatted(viewName);
        req.getRequestDispatcher(path).forward(req, resp);
    }


}
