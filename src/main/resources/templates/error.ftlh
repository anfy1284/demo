<!DOCTYPE html>
<html>
<head>
    <title>Fehler</title>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            const errorMessageElement = document.querySelector('p');
            const errorMessage = errorMessageElement.innerText.trim();
            if (!errorMessage) {
                console.error('Fehler: Ein unerwarteter Fehler ist aufgetreten.');
            } else {
                console.error('Fehler:', errorMessage);
            }
        });
    </script>
</head>
<body>
    <h1>Ein Fehler ist aufgetreten</h1>
    <p th:text="${errorMessage! 'Ein unerwarteter Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.'}"></p>
    <#if errorDetails??>
        <h2>Details:</h2>
        <pre>${errorDetails}</pre>
    </#if>
</body>
</html>
