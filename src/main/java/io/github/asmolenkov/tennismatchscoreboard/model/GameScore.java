package io.github.asmolenkov.tennismatchscoreboard.model;

import lombok.Getter;

@Getter
public class GameScore {


    private Point playerOnePoint = Point.ZERO;
    private Point playerSecondPoint = Point.ZERO;


    public void playerOneAddPoint() {

        playerOnePoint = addPoint(playerOnePoint);
    }

    public void playerSecondAddPoint() {

        playerSecondPoint = addPoint(playerSecondPoint);
    }
    public void resetAllPoint(){
        this.playerOnePoint = Point.ZERO;
        this.playerSecondPoint = Point.ZERO;
    }

    public void resetAdvantagePlayerOne(){
        this.playerOnePoint = Point.FORTY;
    }

    public void resetAdvantagePlayerSecond(){
        this.playerSecondPoint = Point.FORTY;
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
