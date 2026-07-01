package io.github.asmolenkov.tennismatchscoreboard.exception;

public class PlayerIdException extends RuntimeException {

    public PlayerIdException(String message) {
        super(message);
    }

    public PlayerIdException(String message, Exception e) {
        super(message, e);
    }
}
