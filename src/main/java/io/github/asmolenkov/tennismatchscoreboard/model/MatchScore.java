package io.github.asmolenkov.tennismatchscoreboard.model;

import lombok.Builder;
import lombok.Getter;


@Builder // Строитель для этого класса избыточен — его объекты всегда создаются в одном месте и в специальном классе
@Getter
public class MatchScore {

    // Даже если в матче будет сыграно только 2 сета, в этом классе всегда будет 3 объекта SetScore.
        // Также такой подход делает невозможным использовать класс, чтобы сыграть матч из 5 сетов (например).
        // Стоит придумать, как использовать ровно то количество объектов SetScore,
        // которое действительно будет сыграно в матче.

    // TODO: Класс отвечает за счёт в матче, а также содержит объекты SetScore, GameScore, TieBreakScore.
        // Это слишком большая ответственность для этого класса и нарушает Принцип единой ответственности (SRP).
        // Лучшим решением в этом направлении было бы, чтобы MatchScore содержал несколько SetScore,
        // а SetScore содержал несколько GameScore/TieBreakScore
        // Такой подход больше соответствовал бы реальному теннисному матчу.

    // TODO: Хранение поля boolean tieBreakActive вынуждает следить не только за состоянием объекта TieBreakScore,
        // но и за этим флагом.
        // Это нарушает Принцип Единого источника истины.
        // (см. файл "ssot-principle.md" в этом же пакете)
        // Само наличие TieBreakScore в классе SetScore (после рефакторинга) может означать, что идёт тай-брейк.

    // TODO: Класс позволяет любому внешнему коду в произвольный момент изменять своё состояние (например через метод activateTieBreak).
        // Это является признаком анемичной модели.
        // (см. файл "reach-anemic-model.md" в этом же пакете)
        // Класс должен самостоятельно и полностью управлять своим состоянием,
        // предоставляя наружу только необходимые методы, для запуска этих изменений.

    private static final int NUMBER_SET_WON = 2;
    private static final int GAME_SCORE = 6; // Можно назвать MIN_POINT_TO_WIN
    private static final int POINT_DIFFERENCE_IN_SET = 2; // Можно назвать MIN_DIFFERENCE_TO_WIN

    @Builder.Default
    private SetScore setOneScore = new SetScore();
    @Builder.Default
    private SetScore setTwoScore = new SetScore();
    @Builder.Default
    private SetScore setThreeScore = new SetScore();
    @Builder.Default
    private boolean tieBreakActive = false;
    @Builder.Default
    private TieBreakScore tieBreakScore = new TieBreakScore();
    @Builder.Default
    private final GameScore playersGameScore = new GameScore();

    public void activateTieBreak() {

        this.tieBreakActive = true;
    }

    public void deactivateTieBreak() {
        this.tieBreakActive = false;
    }

    public boolean isMatchFinished() {
        int setsWonP1 = countSetWon(PlayerSide.ONE);
        int setsWonP2 = countSetWon(PlayerSide.TWO);

        return setsWonP1 >= NUMBER_SET_WON || setsWonP2 >= NUMBER_SET_WON;
    }

    public boolean isStartTieBreak(int playerOneGames, int playerTwoGames){
        return playerOneGames == GAME_SCORE && playerTwoGames == GAME_SCORE;
    }

    public boolean isStartTieBreak(SetScore set){
        return isStartTieBreak(set.getPlayerOneGameCount(), set.getPlayerSecondGameCount());
    }

    public boolean isSetFinished(SetScore set){
        return isSetFinished(set.getPlayerOneGameCount(), set.getPlayerSecondGameCount());
    }

    private boolean isSetFinished(int playerOneGames, int playerTwoGames) {
        return  (playerOneGames >= GAME_SCORE && playerOneGames - playerTwoGames >= POINT_DIFFERENCE_IN_SET) ||
                (playerTwoGames >= GAME_SCORE && playerTwoGames - playerOneGames >= POINT_DIFFERENCE_IN_SET);

    }

    private int countSetWon(PlayerSide side){
        int count = 0;
        if(isSetWon(setOneScore, side)){
            count ++;
        }
        if(isSetWon(setTwoScore, side)){
            count++;
        }
        if (isSetWon(setThreeScore, side)){
            count++;
        }
        return count;
    }

    private boolean isSetWon(SetScore set, PlayerSide side){
        if(set.isSetActive()){
            return false;
        }
        int p1 = set.getPlayerOneGameCount();
        int p2 = set.getPlayerSecondGameCount();

        return side == PlayerSide.ONE ? p1 > p2 : p2 > p1;
    }

    public SetScore getCurrentSet() {
        if (setOneScore.isSetActive()) {
            return setOneScore;
        } else if (setTwoScore.isSetActive()) {
            return setTwoScore;
        } else {
            return setThreeScore;
        }
    }


}
