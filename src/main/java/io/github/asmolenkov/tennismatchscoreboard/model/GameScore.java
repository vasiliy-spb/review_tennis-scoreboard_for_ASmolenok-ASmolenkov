package io.github.asmolenkov.tennismatchscoreboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameScore {

    private Point playerOnePoint = Point.ZERO;
    private Point playerSecondPoint = Point.ZERO;


    public void addPoint(PlayerSide side) {
        switch (side) {
            case ONE -> playerOneAddPoint();
            case TWO -> playerSecondAddPoint();
        }
    }

    public void resetPoint() {
        this.playerOnePoint = Point.ZERO;
        this.playerSecondPoint = Point.ZERO;
    }

    public boolean isStandardGameWon(PlayerSide playerSide) {
        if(playerSide == PlayerSide.ONE){
         return    isStandardGameWin(playerOnePoint, playerSecondPoint);
        }
        else
           return isStandardGameWin(playerSecondPoint, playerOnePoint);
    }

    public boolean isOpponentAtAdvantage(PlayerSide currentPlayerSide) {
        Point current = (currentPlayerSide == PlayerSide.ONE) ? playerOnePoint : playerSecondPoint;
        Point opponent = (currentPlayerSide == PlayerSide.ONE) ? playerSecondPoint : playerOnePoint;
        return isOpponentAtAdvantageInternal(current, opponent);
    }

    public void resetAdvantage(PlayerSide side){
        switch (side){
            case PlayerSide.ONE -> resetAdvantagePlayerSecond();
            case PlayerSide.TWO -> resetAdvantagePlayerOne();
        }
    }
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
        this.playerOnePoint = Point.FORTY;
    }

    private void resetAdvantagePlayerSecond() {
        this.playerSecondPoint = Point.FORTY;
    }


    private boolean isOpponentAtAdvantageInternal(Point notAdvantage, Point advantage) {
        return notAdvantage == Point.FORTY && advantage == Point.ADVANTAGE;
    }

    private boolean isStandardGameWin(Point winner, Point loser) {
        return winner == Point.FORTY && loser != Point.FORTY &&
                loser != Point.ADVANTAGE;
    }

    private Point addPoint(Point current) {
        return switch (current) {
            case ZERO -> Point.FIFTEEN;
            case FIFTEEN -> Point.THIRTY;
            case THIRTY -> Point.FORTY;
            case FORTY -> Point.ADVANTAGE;
            case ADVANTAGE -> Point.ZERO;
        };
    }




}
