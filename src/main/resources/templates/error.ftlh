<!DOCTYPE html>
<html>
<head>
    <title>Error</title>
</head>
<body>
    <h1>An Error Occurred</h1>
    <p th:text="${errorMessage! 'An unexpected error occurred. Please try again later.'}"></p>
</body>
</html>
