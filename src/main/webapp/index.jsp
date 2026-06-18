<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tennis Match Score Board</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<div class="page-container">
    <!-- Заголовок -->
    <div class="header-section">
        <div class="tennis-ball-icon">🎾</div>
        <h1>Tennis Match<br>Score Board</h1>
        <p class="subtitle">Система учёта теннисных матчей</p>
    </div>

    <!-- Изображение -->
    <div class="image-wrapper">
        <img src="${pageContext.request.contextPath}/resources/image/Tennis.jpg"
             alt="Tennis player"
             class="tennis-image">
    </div>

    <!-- Меню -->
    <div class="menu-card">
        <div class="menu-title">
            <span class="menu-icon">📋</span>
            Главное меню
        </div>

        <div class="menu-buttons">
            <a href="${pageContext.request.contextPath}/new-match" class="menu-btn btn-primary">
                <span class="btn-icon">▶️</span>
                <div class="btn-text">
                    <strong>Начать новый матч</strong>
                    <span>Создать игру и вести счёт</span>
                </div>
            </a>

            <a href="${pageContext.request.contextPath}/matches" class="menu-btn btn-secondary">
                <span class="btn-icon">📊</span>
            </a>
        </div>
    </div>
</div>