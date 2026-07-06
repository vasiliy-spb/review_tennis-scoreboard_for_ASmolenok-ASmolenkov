package io.github.asmolenkov.tennismatchscoreboard.service;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import io.github.asmolenkov.tennismatchscoreboard.exception.PlayerSideException;
import io.github.asmolenkov.tennismatchscoreboard.model.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MatchScoreCalculationService {

    private static final String PLAYER_NOT_FOUND_IN_MATCH_TEMPLATE = "Игрок ID %s not found in match";
    private static final String LOG_START_TIE_BREAK = "Начинается Тай-брейк";
    private static final String LOG_FINISHED_TIE_BREAK_TEMPLATE = "Тай-брейк завершён. Победитель сета: {}";
    private static final String LOG_SET_FINISHED_TEMPLATE = "Сет №{} - завершен";
    private static final String LOG_MATCH_FINISHED = "Матч завершен";
    private static final String LOG_WINNER_TEMPLATE = "Победитель - {}";
    private static final String LOG_INCORRECT_SET_NUMBER_TEMPLATE = "Некорректный номер сета: %s";
    private static final String LOG_GAME_FINISHED = "Гейм завершен";
    private static final String LOG_RESET_ADVANTAGE_OPPONENT = "Сброс преимущества оппонента";

    public void addPointToPlayer(CurrentMatch currentMatch, long playerId) {
        if (currentMatch.isMatchFinished()) {
            return;
        }
        PlayerSide side = resolvePlayerSide(currentMatch, playerId);

        processPointUpdate(currentMatch, side);

    }

    private PlayerSide resolvePlayerSide(CurrentMatch currentMatch, long playerId) {
        if (currentMatch.getPlayerOne()
                        .id() == playerId) return PlayerSide.ONE;
        if (currentMatch.getPlayerSecond()
                        .id() == playerId) return PlayerSide.TWO;
        throw new PlayerSideException(PLAYER_NOT_FOUND_IN_MATCH_TEMPLATE.formatted(playerId));
    }

    private void processPointUpdate(CurrentMatch currentMatch, PlayerSide playerSide) {
        PlayerSide opponent = getOpponent(playerSide);
        MatchScore matchScore = currentMatch.getMatchScore();
        int setNumber = matchScore.determineActiveSetNumber();
        SetScore currentSet = getCurrentSet(currentMatch, setNumber);

        Point currentPoint = currentMatch.getPointPlayer(playerSide);

        Point opponentPoint = currentMatch.getPointPlayer(opponent);


        if(matchScore.isTieBreakActive()){
            tieBreakPointUpdate(matchScore, currentSet , playerSide);
        }else {

            classicUpdatePoint(currentMatch, playerSide);

            if(matchScore.isStartTieBreak(currentSet)){
                log.info(LOG_START_TIE_BREAK);
                matchScore.activateTieBreak();
            }
        }

        if(matchScore.isSetFinished(currentSet)){
            log.info(LOG_SET_FINISHED_TEMPLATE, setNumber);
            currentSet.fishedSet();
        }
        if(matchScore.isMatchFinished()){
            log.info(LOG_MATCH_FINISHED);
            log.info(LOG_WINNER_TEMPLATE, playerSide);
            finishedMatch(currentMatch,getWinner(currentMatch, playerSide));
        }

    }

    private void tieBreakPointUpdate(MatchScore matchScore, SetScore setScore, PlayerSide current) {

        TieBreakScore tieBreakScore = matchScore.getTieBreakScore();

        tieBreakScore.addTieBreakPoint(current);

        matchScore.getTieBreakScore().getWinner().ifPresent(winner ->{
            awardGameToPlayer(setScore,winner);
            matchScore.deactivateTieBreak();
            setScore.fishedSet();
            tieBreakScore.resetPoint();
            matchScore.getPlayersGameScore().resetAllPoint();
            log.info(LOG_FINISHED_TIE_BREAK_TEMPLATE, winner);
        });


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
            default -> throw new IllegalStateException(LOG_INCORRECT_SET_NUMBER_TEMPLATE.formatted(setNumber));
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
            log.info(LOG_GAME_FINISHED);
            awardGameToPlayer(currentSet, playerSide);
            currentMatch.resetAllPoint();
            return;
        }
        if (isOpponentAtAdvantage(currentPoint, opponentPoint)) {
            log.info(LOG_RESET_ADVANTAGE_OPPONENT);
            currentMatch.resetAdvantage(opponent);
            return;
        }

        if (currentPoint == Point.ADVANTAGE) {
            awardGameToPlayer(currentSet, playerSide);
            log.info(LOG_GAME_FINISHED);
            currentMatch.resetAllPoint();
            return;
        }
        currentMatch.addPointToPlayer(playerSide);
    }

}
