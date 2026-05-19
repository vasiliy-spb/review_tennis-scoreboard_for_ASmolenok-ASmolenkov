<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Tennis Match Score Board</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<h1>Tennis Match Score Board</h1>
<img src="resources/image/Tennis.jpg" class="tennis-image" alt="tennis player with a shadow racket">

<form action="${pageContext.request.contextPath}/new-math" method="get">
    <br>
    <input type="submit" value="Начать новый матч!">
    <br/>
</form>

<form action="${pageContext.request.contextPath}/matches" method="get">
    <br>
    <input type="submit" value="Посмотреть сыгранные матчи!">
    <br/>
</form>
</body>
</html>