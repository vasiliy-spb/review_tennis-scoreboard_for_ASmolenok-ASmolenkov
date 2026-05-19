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
    </tr>
    </thead>

    <tbody>
    <tr>
        <td>${currentMatch.playerOne.name}</td>
        <td>2</td>
        <td>0</td>
        <td>0</td>
        <td>30</td>

    </tr>
    <tr>
        <td>${currentMatch.playerSecond.name}</td>
        <td>0</td>
        <td>0</td>
        <td>0</td>
        <td>15</td>
    </tr>

    </tbody>

    <tfoot>

    </tfoot>
</table>



</body>
</html>