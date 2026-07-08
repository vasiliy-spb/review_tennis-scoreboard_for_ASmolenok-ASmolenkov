package io.github.asmolenkov.tennismatchscoreboard.service;

import io.github.asmolenkov.tennismatchscoreboard.dto.MatchesPage;
import io.github.asmolenkov.tennismatchscoreboard.dto.PageInfo;
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

import java.util.List;
import java.util.Optional;

@Slf4j
public class FinishedMatchesPersistenceService {
    private final SessionFactory sessionFactory;
    private final FinishedMatchRepository repository;

    public FinishedMatchesPersistenceService(SessionFactory sessionFactory, FinishedMatchRepository repository) {
        this.sessionFactory = sessionFactory;
        this.repository = repository;
    }

    public void saveMatch(CurrentMatch currentMatch) {
        Match match = MatchMapper.toEntity(currentMatch);
        try (Session session = sessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            try {
                repository.save(match, session);
                tr.commit();
                log.info("Матч {} сохранен", currentMatch.getUuid());
            } catch (Exception e) {
                tr.rollback();
                throw new SaveMatchException("Ошибка сохранения матча", e);
            }
        }
    }

    public Match findMathById(long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            try {
                Optional<Match> match = repository.find(id, session);
                if (match.isEmpty()) {
                    throw new FindMatchException("Матч с ID - %s не найден".formatted(id));
                }
                tr.commit();
                return match.get();
            } catch (Exception e) {
                tr.rollback();
                throw new FindMatchException("Ошибка поиска матча", e);
            }
        }
    }

    public MatchesPage getMatchesPage(String playerName, int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            try {
                int safePage = Math.max(page, 1);
                int safeSize = Math.max(size, 3);
                int offset = (safePage - 1) * safeSize;

                String searchName = (playerName != null && !playerName.trim().isEmpty())
                        ? playerName.trim()
                        : null;

                List<Match> matches;
                long totalItems;

                if (searchName != null) {
                    matches = repository.findByNameWithPagination(session, searchName, offset, safeSize);
                    totalItems = repository.countByName(session, searchName);
                } else {
                    matches = repository.findWithPagination(session, offset, safeSize);
                    totalItems = repository.countTotal(session);
                }

                int totalPages = (int) Math.ceil((double) totalItems / safeSize);

                PageInfo pageInfo = PageInfo.builder()
                                            .currentPage(safePage)
                                            .pageSize(safeSize)
                                            .totalItems(totalItems)
                                            .totalPages(totalPages)
                                            .build();

                tr.commit();

                return MatchesPage.builder()
                                  .pageInfo(pageInfo)
                                  .matches(MatchMapper.toDtoList(matches))
                                  .build();
            }catch (Exception e){
                tr.rollback();
                log.error("Ошибка при загрузке страницы матчей", e);
                throw new RuntimeException("Ошибка загрузки матчей", e);
            }
        }
    }
}
