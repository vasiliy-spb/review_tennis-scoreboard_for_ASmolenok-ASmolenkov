package io.github.asmolenkov.tennismatchscoreboard.service;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import io.github.asmolenkov.tennismatchscoreboard.model.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MatchScoreCalculationService {

    public void addPointToPlayer(CurrentMatch currentMatch, long playerId) {
        if (currentMatch.isMatchFinished()) {
            return;
        }
        PlayerSide side = resolvePlayerSide(currentMatch, playerId); //TODO добавить обработку если метод вернул null

        processPointUpdate(currentMatch, side);

    }

    private PlayerSide resolvePlayerSide(CurrentMatch currentMatch, long playerId) {
        if (currentMatch.getPlayerOne()
                        .id() == playerId) return PlayerSide.ONE;
        if (currentMatch.getPlayerSecond()
                        .id() == playerId) return PlayerSide.TWO;
        return null; //TODO не возвращать нулл (Optional или Exception)
    }

    private void processPointUpdate(CurrentMatch currentMatch, PlayerSide playerSide) {
        PlayerSide opponent = getOpponent(playerSide);
        MatchScore matchScore = currentMatch.getMatchScore();
        int setNumber = matchScore.determineActiveSetNumber();
        SetScore currentSet = getCurrentSet(currentMatch, setNumber);

        Point currentPoint = currentMatch.getPointPlayer(playerSide);
        log.info("Очки Игрок 1 = {}", currentPoint);
        Point opponentPoint = currentMatch.getPointPlayer(opponent);
        log.info("Очки Игрок 2 = {}", opponentPoint);

        if(matchScore.isTieBreakActive()){
            log.info("Идет Тай-брейк");
            tieBreakPointUpdate(matchScore, currentSet , playerSide);
        }else {
            log.info("Идет Гейм");
            classicUpdatePoint(currentMatch, playerSide);

            if(matchScore.isStartTieBreak(currentSet.getPlayerOneGameCount(), currentSet.getPlayerSecondGameCount())){
                log.info("Начинается Тай-брейк");
                matchScore.activateTieBreak();
            }
        }

        if(matchScore.isSetFinished(currentSet.getPlayerOneGameCount(), currentSet.getPlayerSecondGameCount())){
            log.info("Сет №{} - завершен", setNumber);
            currentSet.fishedSet();
        }
        if(matchScore.isMatchFinished()){
            log.info("Матч завершен");
            log.info("Победитель - {}", playerSide);
            finishedMatch(currentMatch,getWinner(currentMatch, playerSide));
        }

    }

    private void tieBreakPointUpdate(MatchScore matchScore, SetScore setScore, PlayerSide current) {
        PlayerSide opponent = getOpponent(current);

        TieBreakScore tieBreakScore = matchScore.getTieBreakScore();

        tieBreakScore.addTieBreakPoint(current);

        if(matchScore.isTieBreakWon(current, opponent)){
            PlayerSide winner = (tieBreakScore.getPlayerOnePoint() > tieBreakScore.getPlayerSecondPoint() ? PlayerSide.ONE : PlayerSide.TWO);
            awardGameToPlayer(setScore,winner);
            matchScore.deactivateTieBreak();
            setScore.fishedSet();
            tieBreakScore.resetPoint();
            matchScore.getPlayersGameScore().resetAllPoint();
            log.info("✅ Тай-брейк завершён. Победитель сета: {}", winner);
        }

    }

    private PlayerSide getOpponent(PlayerSide side) {
        return side == PlayerSide.ONE ? PlayerSide.TWO : PlayerSide.ONE;
    }


    private SetScore getCurrentSet(CurrentMatch currentMatch, int setNumber) {
        return switch (setNumber) {
            case 1 -> currentMatch.getMatchScore()
                                  .getSetOneScore();
            case 2 -> currentMatch.getMatchScore()
                                  .getSetTwoScore();
            case 3 -> currentMatch.getMatchScore()
                                  .getSetThreeScore();
            default -> throw new IllegalStateException("Unexpected value: " + setNumber);
        };
    }

    private boolean isStandardGameWin(Point winner, Point loser) {
        return winner == Point.FORTY && loser != Point.FORTY &&
                loser != Point.ADVANTAGE;
    }

    private void awardGameToPlayer(SetScore setScore, PlayerSide playerSide) {
        if (playerSide == PlayerSide.ONE) {
            setScore.setPlayerOneAddGame();
        } else {
            setScore.setPlayerSecondAddGame();
        }
    }



    private boolean isOpponentAtAdvantage(Point notAdvantage, Point advantage) {
        return notAdvantage == Point.FORTY && advantage == Point.ADVANTAGE;
    }


    public void finishedMatch(CurrentMatch match, PlayerDto winner){
        match.finishTheMatch(winner);
    }

    private PlayerDto getWinner(CurrentMatch currentMatch, PlayerSide side){
        if(side == PlayerSide.ONE){
            return currentMatch.getPlayerOne();
        }else {
            return currentMatch.getPlayerSecond();
        }
    }

    private void classicUpdatePoint(CurrentMatch currentMatch, PlayerSide playerSide){
        PlayerSide opponent = getOpponent(playerSide);
        int setNumber = currentMatch.getMatchScore().determineActiveSetNumber();
        SetScore currentSet = getCurrentSet(currentMatch, setNumber);

        Point currentPoint = currentMatch.getPointPlayer(playerSide);
        Point opponentPoint = currentMatch.getPointPlayer(opponent);

        if (isStandardGameWin(currentPoint, opponentPoint)) {
            log.info("Гейм завершен");
            awardGameToPlayer(currentSet, playerSide);
            currentMatch.resetAllPoint();
            log.info("Очки сброшены");
            return;
        }
        if (isOpponentAtAdvantage(currentPoint, opponentPoint)) {
            log.info("Сброс преимущества оппонента");
            currentMatch.resetAdvantage(opponent);
            return;
        }

        if (currentPoint == Point.ADVANTAGE) {
            awardGameToPlayer(currentSet, playerSide);
            log.info("Гейм завершен");
            log.info("Сброс очков");
            currentMatch.resetAllPoint();
            return;
        }
        currentMatch.addPointToPlayer(playerSide);
    }

}
