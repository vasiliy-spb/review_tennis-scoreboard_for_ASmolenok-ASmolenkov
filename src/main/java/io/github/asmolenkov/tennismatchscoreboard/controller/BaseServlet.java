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

    // TODO: Класс отправляет сообщение из исключения (`e.getMessage()`) напрямую пользователю.
        // Сообщения об ошибках из исключений могут содержать технические детали, которые не предназначены
        // для конечного пользователя и могут представлять угрозу безопасности. Например, сообщение может быть
        // `"No entity found for query 'SELECT ...'"` или `"Validation failed for field 'internalFieldName'"`,
        // что раскрывает структуру БД или внутренние имена полей.
        //
        // Лучше никогда не отправлять необработанное сообщение из исключения на клиент.
        // Вместо этого можно использовать заранее определённые, безопасные сообщения или коды ошибок.
        // Само исключение при этом нужно логировать для разработчиков.
        //
        // Это повысит безопасность приложения и улучшит пользовательский опыт при возникновении ошибок.
        //
        // Допустимо оставить e.getMessage() для ошибок валидации.

    // Все повторяющиеся или важные строковые литералы лучше выносить в `private static final` константы с понятными именами.
        // Именованная константа делает код более семантически понятным.

    private static final String ERROR_PATH_TEMPLATE = "/WEB-INF/views/%s.jsp";
    private static final String LOG_VALIDATION_ERROR_TEMPLATE = "Validation error [{}] on {}: {}";
    private static final String LOG_CONFLICT_ERROR_TEMPLATE = "Conflict error [{}] on {}: {}";
    private static final String LOG_NOT_FOUND_ERROR_TEMPLATE = "Not found error [{}] on {}: {}";
    private static final String LOG_ITERNAL_SERVER_ERROR_TEMPLATE = "Internal Server Error [{}] on {}: {}"; // Опечатка: ITERNAL —> INTERNAL

    protected abstract String getErrorPath();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            super.service(req, resp);
        } catch (NameIncorrectException | PlayerSideException | PlayerIdException e) {
            log.warn(LOG_VALIDATION_ERROR_TEMPLATE,
                    e.getClass()
                     .getSimpleName(),
                    req.getRequestURI(),
                    e.getMessage());
            handleError(req, resp, getErrorPath(), e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (DuplicateNameException e) {
            log.warn(LOG_CONFLICT_ERROR_TEMPLATE,
                    e.getClass()
                     .getSimpleName(),
                    req.getRequestURI(),
                    e.getMessage());
            handleError(req, resp, getErrorPath(), e.getMessage(), HttpServletResponse.SC_CONFLICT);
        } catch (FindMatchException e) {
            log.warn(LOG_NOT_FOUND_ERROR_TEMPLATE,
                    e.getClass()
                     .getSimpleName(),
                    req.getRequestURI(),
                    e.getMessage());
            handleError(req, resp, getErrorPath(), e.getMessage(), HttpServletResponse.SC_NOT_FOUND);
        } catch (PlayerCreationException | SaveMatchException  | SaveActiveMatchException e) {
            log.error(LOG_ITERNAL_SERVER_ERROR_TEMPLATE,
                    e.getClass()
                     .getSimpleName(),
                    req.getRequestURI(),
                    e.getMessage(),
                    e);
            handleError(req, resp, getErrorPath(), e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleError(HttpServletRequest req, HttpServletResponse resp, String viewName,
                             String errorMessage, int status) throws IOException, ServletException {
        req.setAttribute("error", errorMessage);
        resp.setStatus(status);
        renderView(req, resp, viewName);
    }

    protected void renderView(HttpServletRequest req, HttpServletResponse resp, String viewName) throws ServletException, IOException {
        String path = ERROR_PATH_TEMPLATE.formatted(viewName);
        req.getRequestDispatcher(path)
           .forward(req, resp);
    }


}
