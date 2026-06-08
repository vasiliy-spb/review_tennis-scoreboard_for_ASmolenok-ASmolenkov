package io.github.asmolenok;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import io.github.asmolenkov.tennismatchscoreboard.model.*;

import java.util.UUID;

public class TestUtils {
    public static CurrentMatch createMatch(long playerOneId,long playerSecondId,Point pointOnePlayer, Point pointSecondPlayer) {
        MatchScore build = MatchScore.builder()
                                     .setOneScore(new SetScore())
                                     .setTwoScore(new SetScore())
                                     .setThreeScore(new SetScore())
                                     .playersGameScore(new GameScore(pointOnePlayer, pointSecondPlayer))
                                     .tieBreakScore(new TieBreakScore())
                                     .tieBreakActive(false)
                                     .build();
        return CurrentMatch.builder()
                           .uuid(UUID.randomUUID())
                           .playerOne(new PlayerDto(playerOneId, "Sasha"))
                           .playerSecond(new PlayerDto(playerSecondId, "Masha"))
                           .matchScore(build)
                           .matchFinished(false)
                           .build();
    }

    public static CurrentMatch createMatchWithGameScore(long playerOneId,long playerSecondId,int setCountOnePlayer, int setCountSecondPlayer, Point pointOnePlayer, Point pointSecondPlayer) {
        MatchScore build = MatchScore.builder()
                                     .setOneScore(new SetScore(setCountOnePlayer,setCountSecondPlayer,true))
                                     .setTwoScore(new SetScore())
                                     .setThreeScore(new SetScore())
                                     .playersGameScore(new GameScore(pointOnePlayer, pointSecondPlayer))
                                     .tieBreakScore(new TieBreakScore())
                                     .tieBreakActive(false)
                                     .build();
        return CurrentMatch.builder()
                           .uuid(UUID.randomUUID())
                           .playerOne(new PlayerDto(playerOneId, "Sasha"))
                           .playerSecond(new PlayerDto(playerSecondId, "Masha"))
                           .matchScore(build)
                           .matchFinished(false)
                           .build();
    }

    public static CurrentMatch createMatchWithTieBreakScore(long playerOneId,long playerSecondId, int tieBreakPointOnePlayer, int tieBreakPointSecondPlayer) {
        MatchScore build = MatchScore.builder()
                                     .setOneScore(new SetScore(6,6,true))
                                     .setTwoScore(new SetScore())
                                     .setThreeScore(new SetScore())
                                     .playersGameScore(new GameScore())
                                     .tieBreakScore(new TieBreakScore(tieBreakPointOnePlayer, tieBreakPointSecondPlayer))
                                     .tieBreakActive(true)
                                     .build();
        return CurrentMatch.builder()
                           .uuid(UUID.randomUUID())
                           .playerOne(new PlayerDto(playerOneId, "Sasha"))
                           .playerSecond(new PlayerDto(playerSecondId, "Masha"))
                           .matchScore(build)
                           .matchFinished(false)
                           .build();
    }


}
