package io.github.asmolenkov.tennismatchscoreboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor // Не нужен, так как нет необходимости создавать объект с разным начальным счётом
                        // (удобство в тестах не являются достаточной причиной для того,
                        // чтобы подстраивать под них таким образом код)
@NoArgsConstructor // После избавления от @AllArgsConstructor необходимость в @NoArgsConstructor исчезнет —
                        // пустой конструктор есть в классе по умолчанию (когда нет других)
public class GameScore {

    // Если поля инициализируются в месте объявления, то @AllArgsConstructor лучше не использовать,
        // иначе сгенерированный конструктор получается таким:
    /*
    @Generated
    public GameScore(Point playerOnePoint, Point playerSecondPoint) {
        this.playerOnePoint = Point.ZERO;
        this.playerSecondPoint = Point.ZERO;
        this.playerOnePoint = playerOnePoint;
        this.playerSecondPoint = playerSecondPoint;
    }
     */

    // TODO: Класс позволяет любому внешнему коду в произвольный момент изменять своё состояние (например через метод resetPoint).
        // Это является признаком анемичной модели.
        // (см. файл "reach-anemic-model.md" в этом же пакете)
        // Класс должен самостоятельно и полностью управлять своим состоянием,
        // предоставляя наружу только необходимые методы, для запуска этих изменений.

    private Point playerOnePoint = Point.ZERO;
    private Point playerSecondPoint = Point.ZERO;


    public void addPoint(PlayerSide side) {
        // TODO: Нет проверки на то, что гейм не завершён.
            // Попытка начислить очко в уже завершённом гейме — это не нормальная ситуация и
            // должна приводить к исключению.

        switch (side) {
            case ONE -> playerOneAddPoint(); // Можно так: case ONE -> playerOnePoint = addPoint(playerOnePoint);
            case TWO -> playerSecondAddPoint(); // Можно так: case TWO -> playerSecondPoint = addPoint(playerSecondPoint);
        }
    }

    // TODO: Метод позволяет в любое время извне сбросить счёт в гейме.
        // В текущей реализации стоит сделать проверку на то, что гейм завершён.
        // Реальному теннисному матчу больше бы соответствовал подход, где сет содержит несколько геймов,
        // а не обнуляет счёт одного и того же объекта.
    public void resetPoint() {
        this.playerOnePoint = Point.ZERO;
        this.playerSecondPoint = Point.ZERO;
    }

    // Слово Standard лишнее в названии
    public boolean isStandardGameWon(PlayerSide playerSide) {
        if(playerSide == PlayerSide.ONE){
         return    isStandardGameWin(playerOnePoint, playerSecondPoint);
        }

        // Когда из блока if происходит return, то следующую ветку можно писать без else.
        // Тело блока else всегда нужно оборачивать в {}
        else
           return isStandardGameWin(playerSecondPoint, playerOnePoint);
    }

    // Метод с названием "у оппонента преимущество?" проверяет не только это, но и то, что у текущего игрока счёт 40
    public boolean isOpponentAtAdvantage(PlayerSide currentPlayerSide) {
        Point current = (currentPlayerSide == PlayerSide.ONE) ? playerOnePoint : playerSecondPoint;
        Point opponent = (currentPlayerSide == PlayerSide.ONE) ? playerSecondPoint : playerOnePoint;
        return isOpponentAtAdvantageInternal(current, opponent);
    }

    // Метод позволяет в любое время извне установить значение счёта в 40. Этот метод не должен быть публичным.
        // Всей логикой счёта должен управлять только GameScore.
    public void resetAdvantage(PlayerSide side){
        switch (side){
            case PlayerSide.ONE -> resetAdvantagePlayerSecond(); // Можно так: case PlayerSide.ONE -> playerSecondPoint = Point.FORTY;
            case PlayerSide.TWO -> resetAdvantagePlayerOne(); // Можно так: case PlayerSide.TWO -> playerOnePoint = Point.FORTY;
        }
    }

    // Возможно более понятным было бы название hasAdvantage(playerSide)
    public boolean isCurrentPlayerAtAdvantage(PlayerSide currentPlayerSide) {
        Point current = (currentPlayerSide == PlayerSide.ONE) ? playerOnePoint : playerSecondPoint;
        return current == Point.ADVANTAGE;
    }

    private void playerOneAddPoint() {
        playerOnePoint = addPoint(playerOnePoint);
    }

    private void playerSecondAddPoint() {

        playerSecondPoint = addPoint(playerSecondPoint);
    }

    private void resetAdvantagePlayerOne() {

        // Если нет конфликта имён, то слово this можно не использовать
        this.playerOnePoint = Point.FORTY;
    }

    private void resetAdvantagePlayerSecond() {

        // Если нет конфликта имён, то слово this можно не использовать
        this.playerSecondPoint = Point.FORTY;
    }


    private boolean isOpponentAtAdvantageInternal(Point notAdvantage, Point advantage) {
        return notAdvantage == Point.FORTY && advantage == Point.ADVANTAGE;
    }

    // Слово Standard лишнее в названии
    // TODO: Метод некорректно проверяет условие победы. Сейчас это компенсируется логикой в сервисе,
        // но этот класс сам отвечать за свои вычисления и делать это корректно.
        // В гейме невозможно определить победу по счёту, так как нет специального счёта для победителя.
        // Стоит придумать, как определять завершён ли гейм и кто победил другим способом.
    private boolean isStandardGameWin(Point winner, Point loser) {
        return winner == Point.FORTY && loser != Point.FORTY &&
                loser != Point.ADVANTAGE;
    }

    // Больше подошло бы название getNextPoint
    private Point addPoint(Point current) {
        return switch (current) {
            case ZERO -> Point.FIFTEEN;
            case FIFTEEN -> Point.THIRTY;
            case THIRTY -> Point.FORTY;
            case FORTY -> Point.ADVANTAGE;
            case ADVANTAGE -> Point.ZERO; // TODO: Счёт 0 не является следующим после преимущества — здесь должно бросаться исключение
        };
    }




}
