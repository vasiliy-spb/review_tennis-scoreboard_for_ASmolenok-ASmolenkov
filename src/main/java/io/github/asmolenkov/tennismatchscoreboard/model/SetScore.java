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
public class SetScore {

    // TODO: Класс хранит в поле `setActive` данные, которые являются производными от основного состояния (счёта).
        // Это нарушает Принцип Единого источника истины. Источником истины является счёт, поле `setActive` — это лишь следствие.
        // Хранение производных данных создаёт риск рассинхронизации: можно изменить счёт, но забыть обновить это поле, и объект окажется в неконсистентном состоянии.
        // Лучше удалить поле `setActive` и заменить его методом, который вычисляет результат на лету из текущего счёта.
        // (см. файл "ssot-principle.md" в этом же пакете)

    // TODO: Класс позволяет любому внешнему коду в произвольный момент изменять своё состояние (например через метод fishedSet).
        // Это является признаком анемичной модели.
        // (см. файл "reach-anemic-model.md" в этом же пакете)
        // Класс должен самостоятельно и полностью управлять своим состоянием,
        // предоставляя наружу только необходимые методы, для запуска этих изменений.

    private int playerOneGameCount; // Можно playerOneGames
    private int playerSecondGameCount; // Можно playerTwoGames
    private boolean setActive = true; // Лучше isActive


    public void addPoint(PlayerSide playerSide){
        // TODO: Нет проверки на то, что сет не завершён.
            // Попытка начислить очко в уже завершённом сете — это не нормальная ситуация и
            // должна приводить к исключению.

        if(playerSide == PlayerSide.ONE){
            setPlayerOneAddGame(); // Можно playerOneGameCount++
        }else {
            setPlayerSecondAddGame(); // Можно playerSecondGameCount++
        }
    }


    private void setPlayerOneAddGame(){
        this.playerOneGameCount++;
    }

    private void setPlayerSecondAddGame(){

        this.playerSecondGameCount++;
    }

    // Опечатка: fishedSet —> finishedSet
    public void fishedSet(){
        this.setActive = false;
    }
}
