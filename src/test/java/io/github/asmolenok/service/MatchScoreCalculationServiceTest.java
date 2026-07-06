package io.github.asmolenok.service;

import io.github.asmolenkov.tennismatchscoreboard.model.*;
import io.github.asmolenkov.tennismatchscoreboard.repository.ActiveMatchRepository;
import io.github.asmolenkov.tennismatchscoreboard.service.MatchScoreCalculationService;
import io.github.asmolenok.record.ThreeSetMatchScenario;
import io.github.asmolenok.record.TieBreakMatchScenario;
import io.github.asmolenok.record.TwoSetsMatchScenario;
import io.github.asmolenok.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class MatchScoreCalculationServiceTest {
    private final ActiveMatchRepository activeMatchRepository = new ActiveMatchRepository();
    private final MatchScoreCalculationService scoreCalculation = new MatchScoreCalculationService(activeMatchRepository);


    @ParameterizedTest(name = "Игрок {0}: счёт {1} → {2} после начисления очка")
    @CsvSource({"1, ZERO, FIFTEEN", "2, ZERO, FIFTEEN", "1, FIFTEEN, THIRTY", "2, FIFTEEN, THIRTY", "1, THIRTY, FORTY", "2, THIRTY, FORTY",

    })
    @DisplayName("Прогрессия очков в обычном гейме для обоих игроков")
    void pointProgression_ShouldAdvanceCorrectly_ForBothPlayers(long playerId, Point startScore, Point expectedScore) {
        CurrentMatch math = TestUtils.buildMatch(startScore, startScore);

        scoreCalculation.addPointToPlayer(math, playerId);

        Point actualTarget = (playerId == 1L) ? math.getMatchScore()
                                                    .getPlayersGameScore()
                                                    .getPlayerOnePoint() : math.getMatchScore()
                                                                               .getPlayersGameScore()
                                                                               .getPlayerSecondPoint();

        Assertions.assertEquals(expectedScore, actualTarget, "Очки целевого игрока должны измениться");

        Point actualOpponent = (playerId == 1L) ? math.getMatchScore()
                                                      .getPlayersGameScore()
                                                      .getPlayerSecondPoint() : math.getMatchScore()
                                                                                    .getPlayersGameScore()
                                                                                    .getPlayerOnePoint();

        Assertions.assertEquals(startScore, actualOpponent, "Очки соперника не должны изменяться");
    }

    @ParameterizedTest(name = "Счет становится 0 - 0, если Игрок {0} выигрывает гейм при счёте 40-{1}")
    @CsvSource({"1, ZERO", "1, FIFTEEN", "1, THIRTY", "2, ZERO", "2, FIFTEEN", "2, THIRTY"})
    @DisplayName("Сброс очков после окончания гейма")
    void pointProgression_GameWin(long playerId, Point opponentScore) {
        CurrentMatch match = TestUtils.buildMatch((playerId == 1L) ? Point.FORTY : opponentScore, (playerId == 2L) ? Point.FORTY : opponentScore);

        scoreCalculation.addPointToPlayer(match, playerId);

        Assertions.assertEquals(Point.ZERO, match.getMatchScore()
                                                 .getPlayersGameScore()
                                                 .getPlayerOnePoint());
        Assertions.assertEquals(Point.ZERO, match.getMatchScore()
                                                 .getPlayersGameScore()
                                                 .getPlayerSecondPoint());
    }

    @ParameterizedTest(name = "Счет гейма {0} - {1} -> Счет матча {2} - {3} после начисления очка Игроку {4}")
    @CsvSource({"FORTY, FORTY, ADVANTAGE, FORTY , 1",
            "FORTY, FORTY, FORTY, ADVANTAGE, 2",
            "ADVANTAGE, FORTY, ZERO, ZERO, 1",
            "FORTY, ADVANTAGE, ZERO, ZERO, 2",
            "FORTY, ADVANTAGE, FORTY , FORTY , 1",
            "ADVANTAGE,  FORTY, FORTY , FORTY , 2"

    })
    @DisplayName("Начисление и сброс преимущества и сброс очков после окончания гейма")
    void AdProgression_ShouldAdvanceCorrectly_ForBothPlayers(Point startScoreOnePlayer, Point startScoreSecondPlayer, Point expectedScoreOnePlayer, Point expectedScoreSecondPlayer, long playerId) {
        CurrentMatch match = TestUtils.buildMatch(startScoreOnePlayer, startScoreSecondPlayer);
        scoreCalculation.addPointToPlayer(match, playerId);

        Point actualTarget = (playerId == 1L) ? match.getMatchScore()
                                                     .getPlayersGameScore()
                                                     .getPlayerOnePoint() : match.getMatchScore()
                                                                                 .getPlayersGameScore()
                                                                                 .getPlayerSecondPoint();

        Point expectedTarget = (playerId == 1L) ? expectedScoreOnePlayer : expectedScoreSecondPlayer;

        Assertions.assertEquals(expectedTarget, actualTarget, "Очки целевого игрока должны измениться");

        Point actualOpponent = (playerId == 1L) ? match.getMatchScore()
                                                       .getPlayersGameScore()
                                                       .getPlayerSecondPoint() : match.getMatchScore()
                                                                                      .getPlayersGameScore()
                                                                                      .getPlayerOnePoint();


        Point expectedOpponent = (playerId == 1L) ? expectedScoreSecondPlayer : expectedScoreOnePlayer;

        Assertions.assertEquals(expectedOpponent, actualOpponent, "Очки соперника не должны изменяться");

    }

    @ParameterizedTest(name = "Сет: {0}-{1} → {2}-{3} после победы в гейме игроком #{4}")
    @CsvSource({
            "0, 0, 1, 0, 1",
            "0, 0, 0, 1, 2",
            "3, 2, 4, 2, 1",
            "2, 5, 2, 6, 2",})
    @DisplayName("Начисление гейма внутри сета")
    void gameProgression_InSet_ShouldAdvanceCorrectly(int startP1, int startP2, int expectedP1, int expectedP2, long winnerId) {
        Point winnerPoints = Point.FORTY;
        Point loserPoints = Point.FIFTEEN;

        Point playerOnePoints = (winnerId == 1L) ? winnerPoints : loserPoints;
        Point playerTwoPoints = (winnerId == 2L) ? winnerPoints : loserPoints;

        CurrentMatch match = TestUtils.createMatchWithGameScore(1L, 2L, startP1,
                startP2, playerOnePoints, playerTwoPoints);

        scoreCalculation.addPointToPlayer(match, winnerId);

        SetScore set = match.getMatchScore()
                            .getSetOneScore();

        Assertions.assertEquals(expectedP1, set.getPlayerOneGameCount(), "Геймы игрока 1 в сете не совпадают");
        Assertions.assertEquals(expectedP2, set.getPlayerSecondGameCount(), "Геймы игрока 2 в сете не совпадают");

        GameScore game = match.getMatchScore()
                              .getPlayersGameScore();
        Assertions.assertEquals(Point.ZERO, game.getPlayerOnePoint());
        Assertions.assertEquals(Point.ZERO, game.getPlayerSecondPoint());

    }

    @ParameterizedTest(name = "Счет Сета {0} - {1}, очко получает игрок {2}, Сет продолжается? {3}, Счет Сета {4} - {5}")
    @CsvSource({
            "6, 5, 1, false, 7, 5",
            "5, 6, 2, false, 5, 7",
    })
    @DisplayName("Окончание сета")
    void setIsFinished_ShouldFinishedCorrectly_ForBothPlayers(int gamePointP1, int gamePointP2, long scorerId,
                                                              boolean setIsActive, int expectedGamePointP1, int expectedGamePointP2) {
        Point winnerPoints = Point.FORTY;
        Point loserPoints = Point.FIFTEEN;

        Point playerOnePoints = (scorerId == 1L) ? winnerPoints : loserPoints;
        Point playerTwoPoints = (scorerId == 2L) ? winnerPoints : loserPoints;

        CurrentMatch currentMatch = TestUtils.createMatchWithGameScore(1L, 2L, gamePointP1,
                gamePointP2, playerOnePoints, playerTwoPoints);

        scoreCalculation.addPointToPlayer(currentMatch, scorerId);

        Assertions.assertEquals(setIsActive, currentMatch.getMatchScore()
                                                         .getSetOneScore()
                                                         .isSetActive());

        Assertions.assertEquals(expectedGamePointP1, currentMatch.getMatchScore()
                                                                 .getSetOneScore()
                                                                 .getPlayerOneGameCount());
        Assertions.assertEquals(expectedGamePointP2, currentMatch.getMatchScore()
                                                                 .getSetOneScore()
                                                                 .getPlayerSecondGameCount());

    }

    @ParameterizedTest(name = "Счет в сете {0} - {1} -> {2} - {3}, забивает игрок {4}, тай брейк {5}")
    @CsvSource({
            "5, 6, 6, 6, 1, true",
            "6, 5, 6, 6, 2, true",
    })
    @DisplayName("Старт Тай брейка")
    void gameProgression_InSet_StartTieBreak(int startP1, int startP2, int expectedP1,
                                             int expectedP2, int winnerId, boolean activeTieBreak) {
        Point winnerPoints = Point.FORTY;
        Point loserPoints = Point.FIFTEEN;

        Point playerOnePoints = (winnerId == 1L) ? winnerPoints : loserPoints;
        Point playerTwoPoints = (winnerId == 2L) ? winnerPoints : loserPoints;

        CurrentMatch match = TestUtils.createMatchWithGameScore(1L, 2L, startP1,
                startP2, playerOnePoints, playerTwoPoints);

        scoreCalculation.addPointToPlayer(match, winnerId);

        SetScore setScore = match.getMatchScore()
                                 .getSetOneScore();

        Assertions.assertEquals(expectedP1, setScore.getPlayerOneGameCount(), "Геймы игрока 1 в сете не совпадают");
        Assertions.assertEquals(expectedP2, setScore.getPlayerSecondGameCount(), "Геймы игрока 2 в сете не совпадают");

        Assertions.assertEquals(activeTieBreak, match.getMatchScore()
                                                     .isTieBreakActive());
    }


    @ParameterizedTest(name = "Тай-брейк {0}-{1}, очко получает игрок #{2}")
    @CsvSource({
            "0, 0, 1",
            "0, 0, 2",
            "3, 4, 1",
            "6, 5, 2"
    })
    @DisplayName("Продвижение счета в тай брейке")
    void TieBreakProgression_ShouldAdvanceCorrectly_ForBothPlayers(int startP1, int startP2, long winnerId) {

        CurrentMatch currentMatch = TestUtils.createMatchWithTieBreakScore(1L, 2L, startP1, startP2);
        scoreCalculation.addPointToPlayer(currentMatch, winnerId);

        int expectedTbPointP1 = (winnerId == 1L) ? startP1 + 1 : startP1;
        int expectedTbPointP2 = (winnerId == 2L) ? startP2 + 1 : startP2;

        TieBreakScore tb = currentMatch.getMatchScore()
                                       .getTieBreakScore();

        Assertions.assertEquals(expectedTbPointP1, tb.getPlayerOnePoint());
        Assertions.assertEquals(expectedTbPointP2, tb.getPlayerSecondPoint());

    }

    @ParameterizedTest(name = "Счет {0} - {1}, очко получает игрок #{2}, тай брейк активен? {3}, победитель сета игрок {4}")
    @CsvSource({
            "6, 5, 1, false, 1",
            "5, 6, 2, false, 2",
            "7, 6, 1, false, 1",
            "6, 7, 2, false, 2",

    })
    @DisplayName("Победа в тай брейке")
    void TieBreakFinished_ShouldFinishedCorrectly_ForBothPlayers(int tbP1, int tbP2, long scoredId,
                                                                 boolean tieBreakActive, int winnerId) {
        CurrentMatch currentMatch = TestUtils.createMatchWithTieBreakScore(1L, 2L, tbP1, tbP2);
        scoreCalculation.addPointToPlayer(currentMatch, scoredId);


        Assertions.assertEquals(tieBreakActive, currentMatch.getMatchScore()
                                                            .isTieBreakActive(), "Тай брейк не должен быть активен!");

        if (winnerId == 1L) {
            Assertions.assertEquals(7, currentMatch.getMatchScore()
                                                   .getSetOneScore()
                                                   .getPlayerOneGameCount());
        } else {
            Assertions.assertEquals(7, currentMatch.getMatchScore()
                                                   .getSetOneScore()
                                                   .getPlayerSecondGameCount());
        }

    }

    @ParameterizedTest(name = "Счет {0} - {1}, очко получает игрок #{2}, тай брейк активен? {3}, победитель сета игрок {4}")
    @CsvSource({
            "6, 6, 1, true, 1",
            "6, 6, 2, true, 2",


    })
    @DisplayName("Продолжение тай брейка при счете 6-6")
    void TieBreakContinues_ForBothPlayers(int tbP1, int tbP2, long scoredId,
                                          boolean tieBreakActive, int winnerId) {
        CurrentMatch currentMatch = TestUtils.createMatchWithTieBreakScore(1L, 2L, tbP1, tbP2);
        scoreCalculation.addPointToPlayer(currentMatch, scoredId);


        Assertions.assertEquals(tieBreakActive, currentMatch.getMatchScore()
                                                            .isTieBreakActive(), "Тай брейк должен продолжаться");
        if (winnerId == 1L) {
            Assertions.assertEquals(6, currentMatch.getMatchScore()
                                                   .getSetOneScore()
                                                   .getPlayerOneGameCount());
        } else {
            Assertions.assertEquals(6, currentMatch.getMatchScore()
                                                   .getSetOneScore()
                                                   .getPlayerSecondGameCount());
        }

    }


    private static Stream<TwoSetsMatchScenario> provideMatchPointScenariosTwoSets() {
        return Stream.of(
                new TwoSetsMatchScenario(1L, 6, 4, 5, 4, Point.FORTY, Point.FIFTEEN),
                new TwoSetsMatchScenario(2L, 4, 6, 4, 5, Point.FIFTEEN, Point.FORTY)
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource({
            "provideMatchPointScenariosTwoSets"
    })
    @DisplayName("Завершение матча после победного очка, победа в 2 сетах подряд")
    void givenBestOfThree_whenSamePlayerWinsTwoSetsInARow_thenMatchEnds(TwoSetsMatchScenario scenario) {

        CurrentMatch match = TestUtils.CreateMatchOnePointFromWin(scenario.set1P1(), scenario.set1P2(), scenario.set2P1(), scenario.set2P2(),
                scenario.pointP1(), scenario.pointP2());

        MatchScore matchScore = match.getMatchScore();

        scoreCalculation.addPointToPlayer(match, scenario.scoringPlayerId());

        Assertions.assertTrue(matchScore.isMatchFinished());
    }

    private static Stream<ThreeSetMatchScenario> provideMatchPointScenariosThreeSets() {
        return Stream.of(
                new ThreeSetMatchScenario(1L, 6, 4, 4, 6, 6, 5, Point.FORTY, Point.FIFTEEN),
                new ThreeSetMatchScenario(2L, 4, 6, 6, 4, 5, 6, Point.FIFTEEN, Point.FORTY)
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource({
            "provideMatchPointScenariosThreeSets"
    })
    @DisplayName("Завершение матча после победного очка, сыграно 3 сета")
    void givenBestOfThree_whenSamePlayerWinsTwoSets_thenMatchEnds(ThreeSetMatchScenario scenario) {
        CurrentMatch match = TestUtils.CreateMatchOnePointFromWinThreeSets(scenario.set1P1(),
                scenario.set1P2(), scenario.set2P1(), scenario.set2P2(),
                scenario.set3P1(), scenario.set3P2(), scenario.pointP1(), scenario.pointP2());

        MatchScore matchScore = match.getMatchScore();

        scoreCalculation.addPointToPlayer(match, scenario.scoringPlayerId());

        Assertions.assertTrue(matchScore.isMatchFinished());
    }


    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource({
            "provideMatchPointScenariosTieBreak"
    })
    @DisplayName("Завершение матча после победного очка в тай брейке")
    void MatchCompletion_FinalTieBreakPointScored_ShouldFinishMatch(TieBreakMatchScenario scenario){
            CurrentMatch match = TestUtils.CreateMatchOnePointTieBreakFromWin(scenario.set1P1(), scenario.set1P2(),
                    scenario.set2P1(), scenario.set2P2(),scenario.tieBreakPointP1(), scenario.tieBreakPointP2());

            MatchScore matchScore = match.getMatchScore();

            scoreCalculation.addPointToPlayer(match, scenario.scoringPlayerId());

            Assertions.assertTrue(matchScore.isMatchFinished());
    }

    private static Stream<TieBreakMatchScenario> provideMatchPointScenariosTieBreak() {
        return Stream.of(
                new TieBreakMatchScenario(1L, 6, 4, 6, 6, 6, 5),
                new TieBreakMatchScenario(2L, 4, 6, 6, 6, 5, 6)
        );
    }

    @ParameterizedTest(name = "Матч завершен. Победитель - {0} {1} {2}")
    @CsvSource({
            "1, ONE, Sasha",
            "2, TWO, Masha"
    })
    @DisplayName("Корректный победитель")
    void MatchIsOver_correctWinnerHasBeenDetermined(long winnerId,PlayerSide side, String nameWinner){
            CurrentMatch match = TestUtils.createCompletedMatch();
            scoreCalculation.finishedMatch(match, side);
            Assertions.assertEquals(winnerId,match.getWinner().id());
            Assertions.assertEquals(nameWinner,match.getWinner().name());
    }

}
