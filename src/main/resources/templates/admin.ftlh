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

<h3>Загрузка и выгрузка файлов данных</h3>
<form action="/admin/upload" method="post" enctype="multipart/form-data">
    <label>Загрузить файл:
        <input type="file" name="file" required>
    </label>
    <select name="target">
        <option value="users.dat">users.dat</option>
        <option value="bookings.dat">bookings.dat</option>
        <option value="guests.dat">guests.dat</option>
        <option value="invoices.dat">invoices.dat</option>
    </select>
    <button type="submit">Загрузить</button>
</form>
<br>
<b>Скачать файл:</b>
<ul>
    <li><a href="/admin/download?filename=users.dat">users.dat</a></li>
    <li><a href="/admin/download?filename=bookings.dat">bookings.dat</a></li>
    <li><a href="/admin/download?filename=guests.dat">guests.dat</a></li>
    <li><a href="/admin/download?filename=invoices.dat">invoices.dat</a></li>
</ul>
<form action="/admin/reload" method="post" style="margin-top:16px;">
    <button type="submit">Перечитать все файлы данных</button>
</form>
</body>
</html>
