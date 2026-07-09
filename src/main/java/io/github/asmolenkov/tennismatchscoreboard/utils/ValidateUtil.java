package io.github.asmolenkov.tennismatchscoreboard.utils;

import io.github.asmolenkov.tennismatchscoreboard.exception.DuplicateNameException;
import io.github.asmolenkov.tennismatchscoreboard.exception.FindMatchException;
import io.github.asmolenkov.tennismatchscoreboard.exception.NameIncorrectException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@UtilityClass
public class ValidateUtil {

    private static final int MAX_LENGTH_NAME = 30;

    private static final String NAME_LONG_TEMPLATE = "The name %s is too long (30 letter limit).";

    private static final String REGULAR_EXPRESSION_SPECIAL_CHARACTERS = "^[\\p{L}\\s]+$";
    private static final String NAME_CONTAINS_SPECIAL_CHARACTERS_TEMPLATE = "The name %s must not contain special characters or numbers.";
    private static final String NAME_NULL = "Player name missing!";
    private static final String NAME_EMPTY = "The player's name cannot be empty.";
    private static final String NAMES_SOME = "Player names cannot be the same.";
    private static final String UUID_NOT_SPECIFIED = "Match UUID not specified";
    private static final String UUID_INCORRECT_FORMAT_TEMPLATE = "Invalid UUID format: %s";


    public void validateNamePlayer(String name){
        if(name == null ){
            throw new NameIncorrectException(NAME_NULL);
        }
        String result = name.trim();
        if(result.isEmpty()){
            throw new NameIncorrectException(NAME_EMPTY);
        }
        if(result.length() > MAX_LENGTH_NAME){
            throw new NameIncorrectException(NAME_LONG_TEMPLATE.formatted(name));
        }
        if(!result.matches(REGULAR_EXPRESSION_SPECIAL_CHARACTERS)){
            throw new NameIncorrectException(NAME_CONTAINS_SPECIAL_CHARACTERS_TEMPLATE.formatted(name));
        }
    }

    public void validateNamesAreUnique (String nameOne, String nameSecond) {
        if(nameOne.equalsIgnoreCase(nameSecond)){
            throw new DuplicateNameException(NAMES_SOME);
        }
    }

    public UUID parseUuidOrThrow(String uuid){
        if(uuid == null || uuid.trim().isEmpty()){
            throw new FindMatchException(UUID_NOT_SPECIFIED);
        }
        try {
            return UUID.fromString(uuid);
        }catch (IllegalArgumentException e){
            throw new FindMatchException(UUID_INCORRECT_FORMAT_TEMPLATE.formatted(uuid), e);
        }
    }


}
