package io.github.asmolenkov.tennismatchscoreboard.service;


import io.github.asmolenkov.tennismatchscoreboard.exception.PlayerSideException;
import io.github.asmolenkov.tennismatchscoreboard.model.*;
import io.github.asmolenkov.tennismatchscoreboard.repository.ActiveMatchRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MatchScoreCalculationService {

    // Можно использовать @RequiredArgsConstructor над классом вместо самописного конструктора

    // TODO: Нет интерфейса для этого класса. (см. файл "service.md" в этом же пакете)

    // Можно константам с шаблонами для логирования дать суффикс LOG_TEMPLATE,
        // а сообщениям для исключений — ERROR_MESSAGE

    // TODO: Класс содержит в себе бизнес-логику по подсчёту очков, геймов и сетов.
        // Объект, которым он оперирует (`CurrentMatch`), имеет признаки "анемичной" модели —
        // он отдаёт наружу другие объекты доменной модели, которые (как и сам CurrentMatch)
        // не в полной мере контролируют собственное поведение.
        // Это главная архитектурная проблема этой части логики. По этим причинам:
        //
        //  - Нарушение инкапсуляции: Данные (в `CurrentMatch` и вложенных объектах) и поведение (в `MatchScoreCalculationService`) разделены.
            //  Любой другой сервис может так же напрямую изменить счёт, и объект `CurrentMatch` не сможет себя защитить.
        //  - Процедурный стиль: Вместо объектно-ориентированного подхода, где объекты сами управляют своим состоянием
            //  (и начисление очков происходит в духе `CurrentMatch.pointWonBy(player)`), получается процедурный код,
            //  который манипулирует внешними структурами данных.
        //  - Жёсткая связанность (Tight Coupling) и низкая связность (Low Cohesion):
            //  Сервис тесно связан с внутренним устройством `CurrentMatch`. При этом логика,
            //  относящаяся к одному понятию (счёт), размазана по разным классам (модели и сервису).
        //  - Сложность тестирования: Чтобы протестировать один конкретный сценарий (например, переход от "ровно" к "преимуществу"),
            //  нужно разбираться в нескольких классах и переходах по методам. Это сложно и хрупко.
        //
        // Как исправить: Провести рефакторинг классов моделей с переходом к "богатой" доменной модели.

    private final ActiveMatchRepository activeMatchRepository;

    public MatchScoreCalculationService(ActiveMatchRepository activeMatchRepository) {
        this.activeMatchRepository = activeMatchRepository;
    }

    private static final String PLAYER_NOT_FOUND_IN_MATCH_TEMPLATE = "Player ID %s not found in match";
    private static final String LOG_START_TIE_BREAK = "Tie-break begins";
    private static final String LOG_FINISHED_TIE_BREAK_TEMPLATE = "The tie-break is over. Set Winner: {}";
    private static final String LOG_SET_FINISHED = "Set Completed";
    private static final String LOG_MATCH_FINISHED = "Match ended";
    private static final String LOG_WINNER_TEMPLATE = "Winner - {}";
    private static final String LOG_GAME_FINISHED = "Game ended";
    private static final String LOG_RESET_ADVANTAGE_OPPONENT = "Resetting Opponent's Advantage";

    public void addPointToPlayer(CurrentMatch currentMatch, long playerId) {
        if (currentMatch.getMatchScore().isMatchFinished()) {
            return;
        }
        PlayerSide side = resolvePlayerSide(currentMatch, playerId);

        // TODO: Race condition при обработке выигранного очка.
            // Если пользователь очень быстро нажмёт кнопку выигрыша очка, браузер отправит два POST-запроса почти одновременно.
            // Tomcat обработает эти два запроса в двух разных потоках, но так как оба потока будут работать с одним и тем же общим объектом `CurrentMatch`,
            // будет возникать ситуация, когда счёт изменится только один раз.
            // Чтобы это исправить, нужно гарантировать, что только один поток может изменять состояние конкретного матча в один момент времени.
        pointUpdate(currentMatch, side);

    }

    // Логику этого метода более уместно реализовать в CurrentMatch
    private PlayerSide resolvePlayerSide(CurrentMatch currentMatch, long playerId) {
        if (currentMatch.getPlayerOne().id() == playerId) {
            return PlayerSide.ONE;
        }
        if (currentMatch.getPlayerSecond().id() == playerId){
            return PlayerSide.TWO;
        }
        throw new PlayerSideException(PLAYER_NOT_FOUND_IN_MATCH_TEMPLATE.formatted(playerId));
    }

    // TODO: Логика этого метода должна находиться в доменных моделях
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

    // TODO: Логика этого метода должна находиться в доменных моделях
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

    // TODO: Логика этого метода должна находиться в доменных моделях
    private void awardGameToPlayer(SetScore setScore, PlayerSide playerSide) {
        setScore.addPoint(playerSide);
    }

    // TODO: Логика этого метода должна находиться в доменных моделях
    public void finishedMatch(CurrentMatch match,PlayerSide playerSide){
        match.finishTheMatch(playerSide);
    }

    // TODO: Логика этого метода должна находиться в доменных моделях
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
