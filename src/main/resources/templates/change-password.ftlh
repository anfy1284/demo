<!DOCTYPE html>
<html>
<head>
    <title>Passwort ändern</title>
    <style>
        body { font-family: 'Roboto', sans-serif; background: #f4f4f9; margin: 0; }
        .container {
            max-width: 350px;
            margin: 80px auto;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.08);
            padding: 32px 28px;
        }
        h2 { text-align: center; color: #1a237e; margin-bottom: 24px; }
        label { display: block; margin-bottom: 8px; color: #333; font-weight: bold; }
        input[type="password"] {
            width: 100%; padding: 10px; margin-bottom: 18px; border-radius: 5px; border: 1px solid #bbb; font-size: 15px;
        }
        button {
            width: 100%; padding: 10px; background: #1a237e; color: #fff; border: none; border-radius: 5px; font-size: 16px; font-weight: bold; cursor: pointer;
        }
        button:hover { background: #3949ab; }
        .error { color: #c00; text-align: center; margin-bottom: 12px; }
        .message { color: #2e7d32; text-align: center; margin-bottom: 12px; }
    </style>
</head>
<body>
    <div class="container">
        <h2>Passwort ändern</h2>
        <#if error??>
            <div class="error">${error}</div>
        </#if>
        <#if message??>
            <div class="message">${message}</div>
        </#if>
        <form method="post" action="/do-change-password">
            <label for="oldPassword">Altes Passwort</label>
            <input type="password" id="oldPassword" name="oldPassword" required>
            <label for="newPassword">Neues Passwort</label>
            <input type="password" id="newPassword" name="newPassword" required>
            <label for="confirmPassword">Neues Passwort wiederholen</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required>
            <button type="submit">Ändern</button>
        </form>
    </div>
</body>
</html>
