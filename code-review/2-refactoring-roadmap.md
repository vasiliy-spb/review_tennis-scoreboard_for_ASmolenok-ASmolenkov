# Роадмап рефакторинга по файлам

Это упорядоченный список файлов, которые следует исправлять в соответствии с замечаниями в комментариях. Рекомендую двигаться последовательно.

Файлы, не указанные в списке, можно исправлять в любом порядке.

### Шаг 1: Entity и слой доступа к данным

- `/entity/Player.java`
- `/entity/Match.java`
- `/repository/PlayerRepository.java`
- `/repository/FinishedMatchRepository.java`

### Шаг 2: Доменные модели

- `/model/PlayerSide.java`
- `/model/Point.java`
- `/model/GameScore.java`
- `/model/TieBreakScore.java`
- `/model/SetScore.java`
- `/model/MatchScore.java`
- `/model/CurrentMatch.java`
- `/service/MatchScoreCalculationService.java`

### Шаг 3: Сервисный слой

- `/repository/ActiveMatchRepository.java`
- `/service/PlayerService.java`
- `/service/OngoingMatchesService.java`
- `/service/FinishedMatchesPersistenceService.java`

### Шаг 4: DTO (Data Transfer Object)

- `/dto/PageInfo.java`
- `/dto/MatchesPage.java`

### Шаг 5: Контроллеры

- `/controller/BaseServlet.java`
- `/controller/NewMathController.java`
- `/controller/MatchScoreController.java`
- `/controller/MatchesController.java`

### Шаг 6: Конфигурация, мапперы, валидаторы, обработка исключений

- `/listener/AppContextListener.java`
- `/mapper/PlayerMapper.java`
- `/mapper/MatchMapper.java`
- `/utils/ValidateUtil.java`
- `/utils/HibernateUtils.java`
- `/exception/PlayerCreationException.java`
- `/exception/FindMatchException.java`

### Шаг 7: Тесты и JSP

- `src/main/webapp/WEB-INF/views/MatchScore.jsp`
- `src/main/webapp/WEB-INF/views/Matches.jsp`
