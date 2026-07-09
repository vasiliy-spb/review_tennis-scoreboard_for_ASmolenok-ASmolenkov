package io.github.asmolenkov.tennismatchscoreboard.exception;

public class DuplicateNameException extends RuntimeException{
    public DuplicateNameException(String message) {
        super(message);
    }
}
