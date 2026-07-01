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

    private static final String NAME_LONG_TEMPLATE = "Имя %s слишком длинное (Лимит 30 букв).";

    private static final String REGULAR_EXPRESSION_SPECIAL_CHARACTERS = "^[\\p{L}\\s]+$";
    private static final String NAME_CONTAINS_SPECIAL_CHARACTERS_TEMPLATE = "Имя %s не должно содержать спецсимволы или цифры.";
    private static final String NAME_NULL = "Имя игрока отсутствует!";
    private static final String NAME_EMPTY = "Имя игрока не может быть пустым.";
    private static final String NAMES_SOME = "Имена игроков не могут быть одинаковыми";


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
            throw new FindMatchException("UUID матча не указан");
        }
        try {
            return UUID.fromString(uuid);
        }catch (IllegalArgumentException e){
            throw new FindMatchException("Некорректный формат UUID: %s".formatted(uuid), e);
        }
    }


}
