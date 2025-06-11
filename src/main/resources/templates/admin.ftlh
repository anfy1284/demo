<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Админка пользователей</title>
</head>
<body>
<h2>Пользователи</h2>
<table border="1">
    <tr>
        <th>Имя</th>
        <th>Роли</th>
        <th>Требует смены пароля</th>
        <th>Действия</th>
    </tr>
    <#if users?size != 0>
        <#list users as user>
        <tr>
            <td>${user.username}</td>
            <td>
                <#if user.roles??>
                    <#list user.roles as r>${r}<#if r_has_next>, </#if></#list>
                <#else>
                    -
                </#if>
            </td>
            <td>
                <#if user.mustChangePassword??>
                    ${user.mustChangePassword?string("Да", "Нет")}
                <#else>
                    -
                </#if>
            </td>
            <td>
                <form action="/admin/delete" method="post" style="display:inline">
                    <input type="hidden" name="username" value="${user.username}"/>
                    <button type="submit">Удалить</button>
                </form>
                <form action="/admin/reset-password" method="post" style="display:inline">
                    <input type="hidden" name="username" value="${user.username}"/>
                    <input type="text" name="password" placeholder="Новый пароль"/>
                    <button type="submit">Сбросить пароль</button>
                </form>
                <form action="/admin/force-change" method="post" style="display:inline">
                    <input type="hidden" name="username" value="${user.username}"/>
                    <button type="submit">Заставить сменить пароль</button>
                </form>
            </td>
        </tr>
        </#list>
    <#else>
        <tr>
            <td colspan="4" style="text-align:center;">Нет пользователей</td>
        </tr>
    </#if>
</table>

<h3>Добавить пользователя</h3>
<form action="/admin/add" method="post">
    <input type="text" name="username" placeholder="Имя пользователя" required/>
    <input type="text" name="password" placeholder="Пароль" required/>
    <select name="role">
        <option value="USER">USER</option>
        <option value="ADMIN">ADMIN</option>
    </select>
    <button type="submit">Добавить</button>
</form>
</body>
</html>
