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
            throw new NameIncorrectException("Имя не может быть пустым.");
        }
        String result = name.trim();
        if(result.isEmpty()){
            throw new NameIncorrectException("Имя не может быть пустым.");
        }
        if(result.length() > 30){
            throw new NameIncorrectException("Имя не может быть длиннее 30 символов.");
        }
        if(!result.matches("^[\\p{L}\\s]+$")){
            throw new NameIncorrectException("Имя не может содержать спецсимволы.");
        }
    }

    public void validateNamesAreUnique (String nameOne, String nameSecond) {
        if(nameOne.equalsIgnoreCase(nameSecond)){
            throw new DuplicateNameException("Имена не могут быть одинаковыми");
        }
    }
}
