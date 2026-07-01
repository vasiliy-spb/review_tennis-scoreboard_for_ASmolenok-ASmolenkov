package io.github.asmolenkov.tennismatchscoreboard.repository;

import io.github.asmolenkov.tennismatchscoreboard.exception.SaveActiveMatchException;
import io.github.asmolenkov.tennismatchscoreboard.model.CurrentMatch;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
public class ActiveMatchRepository {

    private static final String LOG_MATCH_SAVE_TEMPLATE = "Матч {} - {} сохранен";
    private static final String CURRENT_MATCH_NULL = "Current match must not be null";
    private static final String MATCH_UUID_NULL = "Match UUID must not be null";
    private static final String MATCH_DELETED_TEMPLATE = "Матч с UUID - {} удален";

    private final Map<UUID, CurrentMatch> activeMatches = new ConcurrentHashMap<>();

    public void save(CurrentMatch currentMatch){
        if (currentMatch == null) {
            throw new SaveActiveMatchException(CURRENT_MATCH_NULL);
        }
        if (currentMatch.getUuid() == null) {
            throw new SaveActiveMatchException(MATCH_UUID_NULL);
        }
        activeMatches.put(currentMatch.getUuid(),currentMatch);
        log.info(LOG_MATCH_SAVE_TEMPLATE,currentMatch.getPlayerOne().name(), currentMatch.getPlayerSecond().name());
    }

    public Optional<CurrentMatch> find(UUID uuidActiveMatch){
        if(uuidActiveMatch == null){
            return Optional.empty();
        }
        return Optional.ofNullable(activeMatches.get(uuidActiveMatch));
    }

    public void delete (UUID finishedMatch){
        if(finishedMatch == null){
            throw new SaveActiveMatchException(MATCH_UUID_NULL);
        }
        activeMatches.remove(finishedMatch);
        log.info(MATCH_DELETED_TEMPLATE, finishedMatch);
    }
}
