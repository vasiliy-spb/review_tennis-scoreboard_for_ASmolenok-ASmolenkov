package io.github.asmolenkov.tennismatchscoreboard.service;


import io.github.asmolenkov.tennismatchscoreboard.exception.PlayerSideException;
import io.github.asmolenkov.tennismatchscoreboard.model.*;
import io.github.asmolenkov.tennismatchscoreboard.repository.ActiveMatchRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MatchScoreCalculationService {

    private final ActiveMatchRepository activeMatchRepository;

    public MatchScoreCalculationService(ActiveMatchRepository activeMatchRepository) {
        this.activeMatchRepository = activeMatchRepository;
    }

    private static final String PLAYER_NOT_FOUND_IN_MATCH_TEMPLATE = "Игрок ID %s not found in match";
    private static final String LOG_START_TIE_BREAK = "Начинается Тай-брейк";
    private static final String LOG_FINISHED_TIE_BREAK_TEMPLATE = "Тай-брейк завершён. Победитель сета: {}";
    private static final String LOG_SET_FINISHED = "Сет завершен";
    private static final String LOG_MATCH_FINISHED = "Матч завершен";
    private static final String LOG_WINNER_TEMPLATE = "Победитель - {}";
    private static final String LOG_GAME_FINISHED = "Гейм завершен";
    private static final String LOG_RESET_ADVANTAGE_OPPONENT = "Сброс преимущества оппонента";

    public void addPointToPlayer(CurrentMatch currentMatch, long playerId) {
        if (currentMatch.getMatchScore().isMatchFinished()) {
            return;
        }
        PlayerSide side = resolvePlayerSide(currentMatch, playerId);

        pointUpdate(currentMatch, side);

    }

    private PlayerSide resolvePlayerSide(CurrentMatch currentMatch, long playerId) {
        if (currentMatch.getPlayerOne().id() == playerId) {
            return PlayerSide.ONE;
        }
        if (currentMatch.getPlayerSecond().id() == playerId){
            return PlayerSide.TWO;
        }
        throw new PlayerSideException(PLAYER_NOT_FOUND_IN_MATCH_TEMPLATE.formatted(playerId));
    }

    private void pointUpdate(CurrentMatch currentMatch, PlayerSide playerSide) {
        MatchScore matchScore = currentMatch.getMatchScore();
        SetScore currentSet = currentMatch.getSet();

        if(matchScore.isTieBreakActive()){
            tieBreakPointUpdate(matchScore, currentSet , playerSide);
        }else {
            classicUpdatePoint(currentMatch, playerSide, currentSet);
            if(matchScore.isStartTieBreak(currentSet)){
                log.info(LOG_START_TIE_BREAK);
                matchScore.activateTieBreak();
            }
        }

        if(matchScore.isSetFinished(currentSet)){
            log.info(LOG_SET_FINISHED);
            currentSet.fishedSet();
        }
        if(matchScore.isMatchFinished()){
            log.info(LOG_MATCH_FINISHED);
            log.info(LOG_WINNER_TEMPLATE, playerSide);
            activeMatchRepository.delete(currentMatch.getUuid());
            currentMatch.finishTheMatch(playerSide);
        }

    }

    private void tieBreakPointUpdate(MatchScore matchScore, SetScore setScore, PlayerSide playerSide) {
        TieBreakScore tieBreakScore = matchScore.getTieBreakScore();

        tieBreakScore.addTieBreakPoint(playerSide);

        matchScore.getTieBreakScore().getWinner().ifPresent(winner ->{
            awardGameToPlayer(setScore,winner);
            matchScore.deactivateTieBreak();
            setScore.fishedSet();
            tieBreakScore.resetPoint();
            matchScore.getPlayersGameScore().resetPoint();
            log.info(LOG_FINISHED_TIE_BREAK_TEMPLATE, winner);
        });


    }

    private void awardGameToPlayer(SetScore setScore, PlayerSide playerSide) {
        setScore.addPoint(playerSide);
    }


    public void finishedMatch(CurrentMatch match,PlayerSide playerSide){
        match.finishTheMatch(playerSide);
    }


    private void classicUpdatePoint(CurrentMatch currentMatch, PlayerSide playerSide, SetScore currentSet){
        GameScore gameScore = currentMatch.getMatchScore().getPlayersGameScore();
        if (gameScore.isStandardGameWon(playerSide)) {
            log.info(LOG_GAME_FINISHED);
            awardGameToPlayer(currentSet, playerSide);
            currentMatch.resetAllPointGame();
            return;
        }
        if (gameScore.isOpponentAtAdvantage(playerSide)) {
            log.info(LOG_RESET_ADVANTAGE_OPPONENT);
            gameScore.resetAdvantage(playerSide);
            return;
        }

        if (gameScore.isCurrentPlayerAtAdvantage(playerSide)) {
            awardGameToPlayer(currentSet, playerSide);
            log.info(LOG_GAME_FINISHED);
            currentMatch.resetAllPointGame();
            return;
        }
        gameScore.addPoint(playerSide);
    }

}
