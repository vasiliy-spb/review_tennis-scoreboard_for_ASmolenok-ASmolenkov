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

    // Можно константам с шаблонами для логирования дать суффикс LOG_TEMPLATE,
        // а сообщениям для исключений — ERROR_MESSAGE

    private static final String LOG_MATCH_SAVE_TEMPLATE = "Match {} - {} saved";
    private static final String CURRENT_MATCH_NULL = "Current match must not be null";
    private static final String MATCH_UUID_NULL = "Match UUID must not be null";
    private static final String MATCH_DELETED_TEMPLATE = "Match with UUID - {} removed";
    private static final String MATCH_NOT_FOUND_UUID_NULL = "No match found, UUID = null";

    private final Map<UUID, CurrentMatch> activeMatches = new ConcurrentHashMap<>();

    // Этот метод не должен получать ID из `CurrentMatch`.
        // Хранилище должно само создавать ID для матча (по аналогии с БД) и возвращать его из этого метода.
    public void save(CurrentMatch currentMatch){
        if (currentMatch == null) {
            throw new SaveActiveMatchException(CURRENT_MATCH_NULL);
        }

        // Эта проверка повторяется в нескольких методах — можно вынести её во вспомогательный метод
        if (currentMatch.getUuid() == null) {
            throw new SaveActiveMatchException(MATCH_UUID_NULL);
        }
        activeMatches.put(currentMatch.getUuid(),currentMatch);
        log.info(LOG_MATCH_SAVE_TEMPLATE,currentMatch.getPlayerOne().name(), currentMatch.getPlayerSecond().name());
    }

    public Optional<CurrentMatch> find(UUID uuidActiveMatch){ // Можно называть аргумент matchId
        if(uuidActiveMatch == null){
            log.warn(MATCH_NOT_FOUND_UUID_NULL);
            return Optional.empty();
        }
        return Optional.ofNullable(activeMatches.get(uuidActiveMatch));
    }

    public void delete (UUID finishedMatch){ // Лучше называть аргумент matchId, так как это ID матча, а не сам матч

        // Эта проверка повторяется в нескольких методах — можно вынести её во вспомогательный метод
        if(finishedMatch == null){

            // Исключение "Ошибка при сохранении матча" вводит в заблуждение,
                // так как здесь ошибка возникает при удалении, а не сохранении
            throw new SaveActiveMatchException(MATCH_UUID_NULL);
        }
        activeMatches.remove(finishedMatch);
        log.info(MATCH_DELETED_TEMPLATE, finishedMatch);
    }
}
