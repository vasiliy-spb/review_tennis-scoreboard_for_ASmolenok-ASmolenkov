package io.github.asmolenkov.tennismatchscoreboard.repository;

import io.github.asmolenkov.tennismatchscoreboard.model.CurrentMatch;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MatchRepository {
    private final Map<UUID, CurrentMatch> activeMatches = new ConcurrentHashMap<>();

    public void save(CurrentMatch currentMatch){
        activeMatches.put(currentMatch.getUuid(),currentMatch);
    }

    public Optional<CurrentMatch> find(UUID uuid){
        return Optional.ofNullable(activeMatches.get(uuid));
    }

    public void update(CurrentMatch currentMatch) {
        activeMatches.replace(currentMatch.getUuid(),currentMatch);
    }
}
