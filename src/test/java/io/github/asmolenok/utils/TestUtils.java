package io.github.asmolenok.utils;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import io.github.asmolenkov.tennismatchscoreboard.model.*;

import java.util.UUID;

public class TestUtils {

    private static final String PLAYER_ONE_NAME = "Sasha";
    private static final String PLAYER_TWO_NAME = "Masha";
    private static final long PLAYER_ONE_ID = 1L;
    private static final long PLAYER_TWO_ID = 2L;

    public static CurrentMatch createMatch(Point pointOnePlayer, Point pointSecondPlayer) {
        MatchScore build = MatchScore.builder()
                                     .setOneScore(new SetScore())
                                     .setTwoScore(new SetScore())
                                     .setThreeScore(new SetScore())
                                     .playersGameScore(new GameScore(pointOnePlayer, pointSecondPlayer))
                                     .tieBreakScore(new TieBreakScore())
                                     .tieBreakActive(false)
                                     .build();
        return createMatch(build);
    }

    public static CurrentMatch createMatchWithGameScore(long playerOneId, long playerSecondId, int setCountOnePlayer, int setCountSecondPlayer, Point pointOnePlayer, Point pointSecondPlayer) {
        MatchScore build = MatchScore.builder()
                                     .setOneScore(new SetScore(setCountOnePlayer, setCountSecondPlayer, true))
                                     .setTwoScore(new SetScore())
                                     .setThreeScore(new SetScore())
                                     .playersGameScore(new GameScore(pointOnePlayer, pointSecondPlayer))
                                     .tieBreakScore(new TieBreakScore())
                                     .tieBreakActive(false)
                                     .build();
        return createMatch(build);
    }

    public static CurrentMatch createMatchWithTieBreakScore(long playerOneId, long playerSecondId, int tieBreakPointOnePlayer, int tieBreakPointSecondPlayer) {
        MatchScore build = MatchScore.builder()
                                     .setOneScore(new SetScore(6, 6, true))
                                     .setTwoScore(new SetScore())
                                     .setThreeScore(new SetScore())
                                     .playersGameScore(new GameScore())
                                     .tieBreakScore(new TieBreakScore(tieBreakPointOnePlayer, tieBreakPointSecondPlayer))
                                     .tieBreakActive(true)
                                     .build();
        return createMatch(build);
    }

    public static CurrentMatch CreateMatchOnePointFromWinThreeSets(int gameCountSet1P1, int gameCountSet1P2, int gameCountSet2P1,
                                                                   int gameCountSet2P2, int gameCountSet3P1, int gameCountSet3P2,
                                                                   Point pointP1, Point pointP2) {
        MatchScore build = MatchScore.builder()
                                     .setOneScore(new SetScore(gameCountSet1P1, gameCountSet1P2, false))
                                     .setTwoScore(new SetScore(gameCountSet2P1, gameCountSet2P2, false))
                                     .setThreeScore(new SetScore(gameCountSet3P1, gameCountSet3P2, true))
                                     .playersGameScore(new GameScore(pointP1, pointP2))
                                     .tieBreakScore(new TieBreakScore())
                                     .tieBreakActive(false)

                                     .build();
        return createMatch(build);
    }

    public static CurrentMatch CreateMatchOnePointFromWin(int gameCountSet1P1,
                                                          int gameCountSet1P2, int gameCountSet2P1,
                                                          int gameCountSet2P2, Point pointP1, Point pointP2) {
        MatchScore build = MatchScore.builder()
                                     .setOneScore(new SetScore(gameCountSet1P1, gameCountSet1P2, false))
                                     .setTwoScore(new SetScore(gameCountSet2P1, gameCountSet2P2, true))
                                     .setThreeScore(new SetScore())
                                     .playersGameScore(new GameScore(pointP1, pointP2))
                                     .tieBreakScore(new TieBreakScore())
                                     .tieBreakActive(false)

                                     .build();
        return createMatch(build);
    }

    public static CurrentMatch CreateMatchOnePointTieBreakFromWin(int gameCountSet1P1,
                                                          int gameCountSet1P2, int gameCountSet2P1,
                                                          int gameCountSet2P2, int tieBreakPointP1, int tieBreakPointP2) {
        MatchScore build = MatchScore.builder()
                                     .setOneScore(new SetScore(gameCountSet1P1, gameCountSet1P2, false))
                                     .setTwoScore(new SetScore(gameCountSet2P1, gameCountSet2P2, true))
                                     .setThreeScore(new SetScore())
                                     .playersGameScore(new GameScore())
                                     .tieBreakScore(new TieBreakScore(tieBreakPointP1, tieBreakPointP2))
                                     .tieBreakActive(true)
                                     .build();
        return createMatch(build);
    }

    private static CurrentMatch createMatch(MatchScore matchScore) {
        return CurrentMatch.builder()
                           .uuid(UUID.randomUUID())
                           .playerOne(new PlayerDto(PLAYER_ONE_ID, PLAYER_ONE_NAME))
                           .playerSecond(new PlayerDto(PLAYER_TWO_ID, PLAYER_TWO_NAME))
                           .matchScore(matchScore)
                           .matchFinished(false)
                           .build();
    }


}



