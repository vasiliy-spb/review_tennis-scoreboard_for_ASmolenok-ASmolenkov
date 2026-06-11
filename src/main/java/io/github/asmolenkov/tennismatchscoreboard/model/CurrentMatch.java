package io.github.asmolenkov.tennismatchscoreboard.model;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import io.github.asmolenkov.tennismatchscoreboard.exception.NominateWinnerException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CurrentMatch {
    private final UUID uuid;
    private final PlayerDto playerOne;
    private final PlayerDto playerSecond;
    private final MatchScore matchScore;
    private boolean matchFinished = false;
    private PlayerDto winner;

    public void addPointToPlayer(PlayerSide side) {
        GameScore gameScore = matchScore.getPlayersGameScore();
        switch (side) {
            case ONE -> gameScore.playerOneAddPoint();
            case TWO -> gameScore.playerSecondAddPoint();
        }
    }

    public Point getPointPlayer(PlayerSide side) {
        GameScore gameScore = matchScore.getPlayersGameScore();
        return switch (side) {
            case ONE -> gameScore.getPlayerOnePoint();
            case TWO -> gameScore.getPlayerSecondPoint();
        };
    }

    public void resetAllPoint() {
        matchScore.getPlayersGameScore().resetAllPoint();
    }

    public void resetAdvantage (PlayerSide side){
        GameScore gameScore = matchScore.getPlayersGameScore();
        switch (side){
            case ONE -> gameScore.resetAdvantagePlayerOne();
            case TWO -> gameScore.resetAdvantagePlayerSecond();
        }
    }

    public void finishTheMatch(PlayerDto winner){
        this.matchFinished = true;
        this.winner = winner;
    }

}
