package io.github.asmolenkov.tennismatchscoreboard.utils;

import io.github.asmolenkov.tennismatchscoreboard.exception.DuplicateNameException;
import io.github.asmolenkov.tennismatchscoreboard.exception.NameIncorrectException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ValidateUtil {
    public void validateNamePlayer(String name){
        if(name == null ){
            throw new NameIncorrectException("Имя игра отсутствует!");
        }
        String result = name.trim();
        if(result.isEmpty()){
            throw new NameIncorrectException("Имя игрока не может быть пустым.");
        }
        if(result.length() > 30){
            throw new NameIncorrectException("Имя %s слишком длинное (Лимит 30 букв).".formatted(name));
        }
        if(!result.matches("^[\\p{L}\\s]+$")){
            throw new NameIncorrectException("Имя %s не должно содержать спецсимволы или цифры.".formatted(name));
        }
    }

    public void validateNamesAreUnique (String nameOne, String nameSecond) {
        if(nameOne.equalsIgnoreCase(nameSecond)){
            throw new DuplicateNameException("Имена игроков не могут быть одинаковыми");
        }
    }


}
