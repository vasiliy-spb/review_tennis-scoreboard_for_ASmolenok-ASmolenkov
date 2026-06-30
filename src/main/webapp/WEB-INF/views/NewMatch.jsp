<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Создание матча | Tennis Scoreboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="page-container">
    <!-- Заголовок с теннисным мячом -->
    <div class="header-section">
        <div class="tennis-ball-icon">🎾</div>
        <h1>Tennis Match<br>Score Board</h1>
        <p class="subtitle">Создайте новый матч</p>
    </div>


    <div class="image-wrapper">
        <img src="${pageContext.request.contextPath}/resources/image/Tennis.jpg"
             alt="Tennis player"
             class="tennis-image">
    </div>


    <c:if test="${not empty error}">
        <div class="alert alert-error">
            <span class="alert-icon">⚠️</span>
            <c:forEach var="msg" items="${error}">
                <p>${msg}</p>
            </c:forEach>
        </div>
    </c:if>




    <div class="form-card">
        <form action="${pageContext.request.contextPath}/new-match" method="post" class="match-form">
            <div class="form-group">
                <label for="playerOneName">
                    <span class="player-indicator player-one">🎾</span>
                    Игрок 1
                </label>
                <input type="text"
                       id="playerOneName"
                       name="playerOneName"
                       required
                       placeholder="Введите имя первого игрока"
                       value="${param.playerOneName}">
            </div>

            <div class="form-group">
                <label for="playerTwoName">
                    <span class="player-indicator player-two">🎾</span>
                    Игрок 2
                </label>
                <input type="text"
                       id="playerTwoName"
                       name="playerTwoName"
                       required
                       placeholder="Введите имя второго игрока"
                       value="${param.playerTwoName}">
            </div>

            <button type="submit" class="btn-start">
                <span class="btn-icon">▶️</span>
                Начать игру!
            </button>
        </form>
    </div>


    <a href="index.jsp" class="back-link">
        <span>←</span> Вернуться к главной странице
    </a>
</div>
</body>
</html>