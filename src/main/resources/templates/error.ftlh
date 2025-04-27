<!DOCTYPE html>
<html>
<head>
    <title>Fehler</title>
    <script>
        // Логируем сообщение об ошибке в консоль
        document.addEventListener('DOMContentLoaded', () => {
            const errorMessageElement = document.querySelector('p');
            const errorMessage = errorMessageElement.innerText.trim();
            if (!errorMessage) {
                console.error('Error occurred: Ein unerwarteter Fehler ist aufgetreten.');
            } else {
                console.error('Error occurred:', errorMessage);
            }
        });
    </script>
</head>
<body>
    <h1>Ein Fehler ist aufgetreten</h1>
    <p th:text="${errorMessage! 'Ein unerwarteter Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.'}"></p>
</body>
</html>
