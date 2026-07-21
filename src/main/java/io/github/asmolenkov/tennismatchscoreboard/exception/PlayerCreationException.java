package io.github.asmolenkov.tennismatchscoreboard.exception;

public class PlayerCreationException extends RuntimeException {

    // TODO: Оригинальное исключение (Exception e) не передаётся в конструктор супер-класса и молча проглатывается
    public PlayerCreationException(String message, Exception e) {
        super(message);
    }
}
