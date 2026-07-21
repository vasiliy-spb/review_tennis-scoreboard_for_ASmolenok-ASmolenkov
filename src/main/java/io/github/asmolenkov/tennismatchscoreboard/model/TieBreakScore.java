package io.github.asmolenkov.tennismatchscoreboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor // После избавления от @AllArgsConstructor необходимость в @NoArgsConstructor исчезнет —
                        // пустой конструктор есть в классе по умолчанию (когда нет других)
@AllArgsConstructor // Не нужен, так как нет необходимости создавать объект с разным начальным счётом
                        // (удобство в тестах не являются достаточной причиной для того,
                        // чтобы подстраивать под них таким образом код)
public class TieBreakScore {

    // TODO: Класс позволяет любому внешнему коду в произвольный момент изменять своё состояние (например через метод resetPoint).
        // Это является признаком анемичной модели.
        // (см. файл "reach-anemic-model.md" в этом же пакете)
        // Класс должен самостоятельно и полностью управлять своим состоянием,
        // предоставляя наружу только необходимые методы, для запуска этих изменений.

    private static final int START_SCORE = 0;
    private static final int MAX_NUMBER_POINT_IN_TIE_BREAK = 7; // Можно назвать MIN_POINT_TO_WIN
    private static final int DIFFERENCE_SCORE_IN_TIE_BREAK = 2; // Можно назвать MIN_DIFFERENCE_TO_WIN

    // Лучше явно инициализировать счёт каждого игрока в конструкторе
    private int playerOnePoint;
    private int playerSecondPoint;


    public void addTieBreakPoint(PlayerSide playerSide){
        // TODO: Нет проверки на то, что тай-брейк не завершён.
            // Попытка начислить очко в уже завершённом тай-брейке — это не нормальная ситуация и
            // должна приводить к исключению.

        switch (playerSide){
            case ONE -> addPointPlayerOne(); // Можно так: case ONE -> playerOnePoint++;
            case TWO -> addPointPlayerSecond(); // Можно так: case TWO -> playerSecondPoint++;
        }
    }

    // TODO: Метод позволяет в любое время извне сбросить счёт в тай-брейке.
        // В текущей реализации стоит сделать проверку на то, что тай-брейк завершён.
        // Реальному теннисному матчу больше бы соответствовал подход, где сет содержит несколько геймов/тай-брейков,
        // а не обнуляет счёт одного и того же объекта.
    public void resetPoint(){
        playerOnePoint = START_SCORE;
        playerSecondPoint = START_SCORE;
    }

    public Optional<PlayerSide> getWinner() {

        // Нет необходимости вводить дополнительные переменные
        int p1 = this.playerOnePoint;
        int p2 = this.playerSecondPoint;

        if (p1 >= MAX_NUMBER_POINT_IN_TIE_BREAK && p1 - p2 >= DIFFERENCE_SCORE_IN_TIE_BREAK) {
            return Optional.of(PlayerSide.ONE);
        }

        if (p2 >= MAX_NUMBER_POINT_IN_TIE_BREAK && p2 - p1 >= DIFFERENCE_SCORE_IN_TIE_BREAK) {
            return Optional.of(PlayerSide.TWO);
        }

        return Optional.empty();
    }

    private void addPointPlayerOne(){
        playerOnePoint++;
    }

    private void addPointPlayerSecond(){
        playerSecondPoint++;
    }
}
