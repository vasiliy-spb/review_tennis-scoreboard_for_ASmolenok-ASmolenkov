package io.github.asmolenkov.tennismatchscoreboard.exception;

public class SaveMatchException extends RuntimeException {
    public SaveMatchException(String message, Exception e) {
        super(message, e);
    }
}
