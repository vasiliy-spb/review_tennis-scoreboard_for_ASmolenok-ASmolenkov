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

    <!-- Изображение -->
    <div class="image-wrapper">
        <img src="${pageContext.request.contextPath}/resources/image/Tennis.jpg"
             alt="Tennis player"
             class="tennis-image">
    </div>

    <!-- Сообщения об ошибках -->
    <c:if test="${not empty error}">
        <div class="alert alert-error">
            <span class="alert-icon">⚠️</span>
            <c:forEach var="msg" items="${error}">
                <p>${msg}</p>
            </c:forEach>
        </div>
    </c:if>

    <!-- Форма -->
    <div class="form-card">
        <form action="${pageContext.request.contextPath}/new-math" method="post" class="match-form">
            <div class="form-group">
                <label for="name1">
                    <span class="player-indicator player-one">🎾</span>
                    Игрок 1
                </label>
                <input type="text"
                       id="name1"
                       name="name1"
                       required
                       placeholder="Введите имя первого игрока"
                       value="${param.name1}">
            </div>

            <div class="form-group">
                <label for="name2">
                    <span class="player-indicator player-two">🎾</span>
                    Игрок 2
                </label>
                <input type="text"
                       id="name2"
                       name="name2"
                       required
                       placeholder="Введите имя второго игрока"
                       value="${param.name2}">
            </div>

            <button type="submit" class="btn-start">
                <span class="btn-icon">▶️</span>
                Начать игру!
            </button>
        </form>
    </div>

    <!-- Ссылка назад -->
    <a href="index.jsp" class="back-link">
        <span>←</span> Вернуться к главной странице
    </a>
</div>
</body>
</html>