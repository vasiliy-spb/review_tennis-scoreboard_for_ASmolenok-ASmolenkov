<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/match-score.css">
</head>
<body>
<div class="match-header">
    <div class="player-name">${currentMatch.playerOne.name}</div>
    <div class="vs-badge">VS</div>
    <div class="player-name">${currentMatch.playerSecond.name}</div>
</div>

<table class="players-table">
    <thead>
    <tr>
        <th>Имя игрока</th>
        <th>Set 1</th>
        <th>Set 2</th>
        <th>Set 3</th>
        <th>Game</th>
        <th>Действие</th>
    </tr>
    </thead>

    <tbody>
    <tr>
        <td>${currentMatch.playerOne.name}</td>
        <td>${currentMatch.matchScore.setOneScore.playerOneGameCount}</td> <!-- Set 1 !-->
        <td>${currentMatch.matchScore.setTwoScore.playerOneGameCount}</td> <!-- Set 2 !-->
        <td>${currentMatch.matchScore.setThreeScore.playerOneGameCount}</td> <!-- Set 3 !-->

        <td class="game-cell">
            <c:choose>

                <c:when test="${currentMatch.matchScore.tieBreakActive}">
                    <span class="tb-badge">TB</span>
                    <span class="tb-score">${currentMatch.matchScore.tieBreakScore.playerOnePoint}</span>
                </c:when>

                <c:otherwise>
                    ${currentMatch.matchScore.playersGameScore.playerOnePoint.displayValue}
                </c:otherwise>
            </c:choose>
        </td> <!-- Game  !-->
        <td><form action="${pageContext.request.contextPath}/match-score" method="post">
            <input type="hidden" name="uuid" value="${currentMatch.uuid}">
            <input type="hidden" name="playerId" value="${currentMatch.playerOne.id}">
            <button type="submit" class="btn-add-point">+ Point</button>
        </form>
        </td> <!-- Game  !-->

    </tr>
    <tr>
        <td>${currentMatch.playerSecond.name}</td>
        <td>${currentMatch.matchScore.setOneScore.playerSecondGameCount}</td> <!-- Set 1 !-->
        <td>${currentMatch.matchScore.setTwoScore.playerSecondGameCount}</td><!-- Set 2 !-->
        <td>${currentMatch.matchScore.setThreeScore.playerSecondGameCount}</td> <!-- Set 3 !-->
        <td class="game-cell">
            <c:choose>
                <c:when test="${currentMatch.matchScore.tieBreakActive}">
                    <span class="tb-badge">TB</span>
                    <span class="tb-score">${currentMatch.matchScore.tieBreakScore.playerSecondPoint}</span>
                </c:when>
                <c:otherwise>
                    ${currentMatch.matchScore.playersGameScore.playerSecondPoint.displayValue}
                </c:otherwise>
            </c:choose>
        </td> <!-- Game  !-->
        <td><form action="${pageContext.request.contextPath}/match-score" method="post">
            <input type="hidden" name="uuid" value="${currentMatch.uuid}">
            <input type="hidden" name="playerId" value="${currentMatch.playerSecond.id}">
            <button type="submit" class="btn-add-point">+ Point</button>
        </form>
        </td> <!-- Game  !-->
    </tr>

    </tbody>
    <tfoot>
    </tfoot>


</table>
</body>
</html>