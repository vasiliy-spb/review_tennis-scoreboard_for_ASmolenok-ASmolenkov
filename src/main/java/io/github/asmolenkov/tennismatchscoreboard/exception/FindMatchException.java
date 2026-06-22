package io.github.asmolenkov.tennismatchscoreboard.exception;

public class FindMatchException extends RuntimeException {
    public FindMatchException(String message, Exception e){
        super(message);}

    public FindMatchException(String message){
        super(message);}
}
