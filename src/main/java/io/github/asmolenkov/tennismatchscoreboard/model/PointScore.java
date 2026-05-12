package io.github.asmolenkov.tennismatchscoreboard.model;




public class PointScore {
    private Point playerOnePoint = Point.ZERO;
    private Point playerSecondPoint = Point.ZERO;

    public void playerOneAddPoint() {
        playerOnePoint = addPoint(playerOnePoint);
    }

    public void playerSecondAddPoint() {
        playerSecondPoint = addPoint(playerSecondPoint);
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
