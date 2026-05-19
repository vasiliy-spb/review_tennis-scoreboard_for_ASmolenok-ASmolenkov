<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Tennis Match Score Board</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<h1>Tennis Match Score Board</h1>
<img src="resources/image/Tennis.jpg" class="tennis-image" alt="tennis player with a shadow racket">

<c:if test="${not empty error}">
    <div class="alert alert-error">${error}</div>
</c:if>

<form action="${pageContext.request.contextPath}/new-math" method="post">
    <div>
        <label for="name1">Игрок 1</label>
        <input type="text" id="name1" name="name1" required placeholder="Введите имя игрока">
    </div>
    <br>

    <div>
        <label for="name2">Игрок 2</label>
        <input type="text" id="name2" name="name2" required placeholder="Введите имя игрока">
    </div>
    <br>
    <input type="submit" value="Начать игру!">
    <br/>
</form>

<a href="index.jsp">Вернуться к главной странице!</a>


</body>
</html>