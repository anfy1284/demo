<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <style>
        body { font-family: 'Roboto', sans-serif; background: #f4f4f9; margin: 0; }
        .login-container {
            max-width: 350px;
            margin: 80px auto;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.08);
            padding: 32px 28px;
        }
        h2 { text-align: center; color: #1a237e; margin-bottom: 24px; }
        label { display: block; margin-bottom: 8px; color: #333; font-weight: bold; }
        input[type="text"], input[type="password"] {
            width: 100%; padding: 10px; margin-bottom: 18px; border-radius: 5px; border: 1px solid #bbb; font-size: 15px;
        }
        button {
            width: 100%; padding: 10px; background: #1a237e; color: #fff; border: none; border-radius: 5px; font-size: 16px; font-weight: bold; cursor: pointer;
        }
        button:hover { background: #3949ab; }
        .error { color: #c00; text-align: center; margin-bottom: 12px; }
    </style>
</head>
<body>
    <div class="login-container">
        <h2>Anmeldung</h2>
        <#if error??>
            <div class="error">Ungültiger Benutzername oder Passwort</div>
        </#if>
        <form method="post" action="/login">
            <label for="username">Benutzername</label>
            <input type="text" id="username" name="username" required autofocus>
            <label for="password">Passwort</label>
            <input type="password" id="password" name="password" required>
            <button type="submit">Login</button>
        </form>
    </div>
</body>
</html>
