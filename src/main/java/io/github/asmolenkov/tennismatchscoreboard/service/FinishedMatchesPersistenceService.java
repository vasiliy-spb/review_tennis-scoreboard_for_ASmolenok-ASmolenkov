package io.github.asmolenkov.tennismatchscoreboard.service;

import io.github.asmolenkov.tennismatchscoreboard.entity.Match;
import io.github.asmolenkov.tennismatchscoreboard.exception.FindMatchException;
import io.github.asmolenkov.tennismatchscoreboard.exception.SaveMatchException;
import io.github.asmolenkov.tennismatchscoreboard.mapper.MatchMapper;
import io.github.asmolenkov.tennismatchscoreboard.model.CurrentMatch;
import io.github.asmolenkov.tennismatchscoreboard.repository.FinishedMatchRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Optional;

@Slf4j
public class FinishedMatchesPersistenceService {
    private final SessionFactory sessionFactory;
    private final FinishedMatchRepository repository;

    public FinishedMatchesPersistenceService(SessionFactory sessionFactory, FinishedMatchRepository repository) {
        this.sessionFactory = sessionFactory;
        this.repository = repository;
    }

    public void saveMatch(CurrentMatch currentMatch){
        Match match = MatchMapper.toEntity(currentMatch);
        try(Session session = sessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            try {
                repository.save(match, session);
                tr.commit();
                log.info("Матч {} сохранен", currentMatch.getUuid());
            }catch (Exception e){
                tr.rollback();
                throw new SaveMatchException("Ошибка сохранения матча", e);
            }
        }
    }

    public Match findMathById(long id){
        try(Session session = sessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            try {
                Optional<Match> match = repository.find(id, session);
                if(match.isEmpty()){
                    throw new FindMatchException("Матч с ID - %s не найден".formatted(id));
                }
                tr.commit();
                return match.get();
            }catch (Exception e){
                tr.rollback();
                throw new FindMatchException("Ошибка поиска матча");
            }
        }
    }

}
