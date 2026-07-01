package io.github.asmolenkov.tennismatchscoreboard.exception;

public class SaveActiveMatchException extends RuntimeException {
    public SaveActiveMatchException(String message) {
        super(message);
    }
}
