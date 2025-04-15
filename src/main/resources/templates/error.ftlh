<!DOCTYPE html>
<html>
<head>
    <title>Fehler</title>
</head>
<body>
    <h1>Ein Fehler ist aufgetreten</h1>
    <p th:text="${errorMessage! 'Ein unerwarteter Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.'}"></p>
</body>
</html>
