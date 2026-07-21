package io.github.asmolenkov.tennismatchscoreboard.model;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter // Вместо геттера для поля PlayerDto winner лучше иметь метод, возвращающий Optional<PlayerDto>
@Builder // Строитель для этого класса избыточен — его объекты всегда создаются в одном месте и в специальном классе
@AllArgsConstructor // Такой конструктор не нужен: поля MatchScore matchScore и PlayerDto winner не должны устанавливаться извне.
public class CurrentMatch {

    // TODO: Класс позволяет любому внешнему коду в произвольный момент изменять своё состояние (например через метод resetAllPointGame).
        // А также не содержит значимой бизнес-логики (сейчас она находится в MatchScoreCalculationService).
        // Это является признаком анемичной модели.
        // (см. файл "reach-anemic-model.md" в этом же пакете)
        // Класс должен самостоятельно и полностью управлять своим состоянием,
        // предоставляя наружу только необходимые методы, для запуска этих изменений.

    // Класс доменной модели для хранения данных об игроке использует DTO.
        // (см. файл "model-types.md" в этом же пакете)
        // Более чистым архитектурным подходом было бы использовать в доменной модели матча доменную модель игрока
        // (даже если по полям она совпадает с DTO).

    private final UUID uuid;
    private final PlayerDto playerOne;
    private final PlayerDto playerSecond;
    private final MatchScore matchScore;
    private PlayerDto winner;


    public void resetAllPointGame() {
        matchScore.getPlayersGameScore().resetPoint();
    }

    public boolean isMatchFinished(){
        return matchScore.isMatchFinished();
    }


    // Артикль можно не использовать в названии метода
    public void finishTheMatch(PlayerSide playerSide){
        if(playerSide == PlayerSide.ONE){
            this.winner = playerOne;
        }
        else {
            this.winner = playerSecond;
        }
    }

    public SetScore getSet (){
        return matchScore.getCurrentSet();
    }

}
