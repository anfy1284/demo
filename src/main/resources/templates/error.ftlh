<!DOCTYPE html>
<html>
<head>
    <title>Fehler</title>
    <script>
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
    <p>
        <#if errorMessage??>
            ${errorMessage}
        <#else>
            Ein unerwarteter Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.
        </#if>
    </p>
    <#if errorDetails??>
        <h2>Details:</h2>
        <pre>${errorDetails}</pre>
    </#if>
</body>
</html>
