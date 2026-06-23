<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Matches</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/matches.css">
</head>
<body>
<h1>Сыгранные матчи</h1>


<div class="filter">
    <form action="${pageContext.request.contextPath}/matches" method="get" class="search-player">
        <div class="name-player">
            <input type="text"
                   name="playerName"
                   required
                   placeholder="Введите имя игрока"
                   value="${param.playerName}">

        </div>

        <button type="submit" class="btn-search">
            Поиск
        </button>
    </form>
</div>



<table class="matches-table">
    <thead>
    <tr>
        <th>Имя игрока 1</th>
        <th>Имя игрока 2</th>
        <th>Победитель</th>
    </tr>
    </thead>

    <tbody>
    <c:choose>
        <c:when test="${not empty matches}">
            <c:forEach var="match" items="${matches}">
                <tr>
                    <td>${match.playerOneName}</td>
                    <td>${match.playerSecondName}</td>
                    <td class="winner" >${match.winnerName}</td>
                </tr>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <tr>
                <td colspan="3">
                <c:choose>
                    <c:when test="${not empty param.playerName}">
                        Матч для игрока ${param.playerName} не найден
                    </c:when>
                    <c:otherwise>
                        Введите имя игрока для поиска
                    </c:otherwise>
                </c:choose>
                </td>
            </tr>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>

<a href="${pageContext.request.contextPath}/" class="back-link">
    ← Вернуться к главной странице
</a>
</body>
</html>
