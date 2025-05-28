<#-- Hauptmenü -->
<div class="navbar">
    <a href="/bookings">Kalender</a>
    <a href="/bookings-list" class="active">Buchungen</a>
    <a href="/invoice/new" class="menu-btn">Rechnung erstellen</a>
</div>
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Buchungen</title>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f4f9;
        }
        .navbar {
            width: 100%;
            background: #1a237e;
            color: #fff;
            display: flex;
            align-items: center;
            padding: 0 0 0 24px;
            height: 56px;
            box-shadow: 0 2px 8px rgba(26,35,126,0.08);
            font-size: 18px;
            font-weight: 500;
            letter-spacing: 0.5px;
            margin-bottom: 0;
            z-index: 100;
        }
        .navbar a {
            color: #fff;
            text-decoration: none;
            margin-right: 32px;
            padding: 0 8px;
            height: 56px;
            display: flex;
            align-items: center;
            border-bottom: 2px solid transparent;
            transition: border-bottom 0.2s, color 0.2s;
        }
        .navbar a.active, .navbar a:hover {
            color: #ffeb3b;
            border-bottom: 2px solid #ffeb3b;
        }
        .navbar a.menu-btn {
            margin-left: 0;
            background: none;
            color: #fff;
            border-radius: 0;
            height: 56px;
            display: flex;
            align-items: center;
            font-size: 18px;
            font-weight: 500;
            box-shadow: none;
            padding: 0 8px;
            border-bottom: 2px solid transparent;
            transition: border-bottom 0.2s, color 0.2s;
        }
        .navbar a.menu-btn:hover,
        .navbar a.menu-btn.active {
            color: #ffeb3b;
            border-bottom: 2px solid #ffeb3b;
            background: none;
        }
        .container {
            max-width: 1200px;
            margin: 32px auto;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.08);
            padding: 24px 32px;
        }
        h1 {
            color: #1a237e;
            margin-bottom: 24px;
        }
        .search-bar {
            margin-bottom: 18px;
            display: flex;
            gap: 12px;
        }
        .search-bar input {
            padding: 8px 12px;
            border: 1px solid #bbb;
            border-radius: 5px;
            font-size: 15px;
            width: 250px;
        }
        .search-bar button {
            background: #1a237e;
            color: #fff;
            border: none;
            border-radius: 5px;
            padding: 8px 18px;
            font-size: 15px;
            cursor: pointer;
            transition: background 0.2s;
        }
        .search-bar button:hover {
            background: #3949ab;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 8px;
        }
        th, td {
            border: 1px solid #e0e0e0;
            padding: 10px 8px;
            text-align: left;
        }
        th {
            background: #f4f4f4;
            color: #222;
            font-weight: bold;
        }
        tr:nth-child(even) td {
            background: #fafbfc;
        }
        tr:hover td {
            background: #e8eaf6;
        }
        tr.clickable-row {
            cursor: pointer;
            transition: background 0.13s;
        }
        tr.clickable-row:hover {
            background: #e3e7fa !important;
        }
        @media (max-width: 900px) {
            .container { padding: 12px 2vw; }
            .search-bar input { width: 100px; }
        }
    </style>
    <script>
        // Делает строку таблицы кликабельной
        document.addEventListener('DOMContentLoaded', function() {
            document.querySelectorAll('tr[data-href]').forEach(function(row) {
                row.addEventListener('click', function() {
                    window.location = row.getAttribute('data-href');
                });
            });

            // AJAX-поиск по мере ввода
            const searchInput = document.getElementById('searchInput');
            let searchTimeout = null;
            searchInput.addEventListener('input', function() {
                clearTimeout(searchTimeout);
                searchTimeout = setTimeout(function() {
                    fetch('/bookings-list?q=' + encodeURIComponent(searchInput.value))
                        .then(resp => resp.text())
                        .then(html => {
                            // Извлекаем только <tbody> из ответа
                            const parser = new DOMParser();
                            const doc = parser.parseFromString(html, 'text/html');
                            const newTbody = doc.getElementById('bookingsTbody');
                            if (newTbody) {
                                document.getElementById('bookingsTbody').innerHTML = newTbody.innerHTML;
                                // Повторно навешиваем обработчик на новые строки
                                document.querySelectorAll('tr[data-href]').forEach(function(row) {
                                    row.addEventListener('click', function() {
                                        window.location = row.getAttribute('data-href');
                                    });
                                });
                            }
                        });
                }, 200); // debounce
            });
        });
    </script>
</head>
<body>
    <div class="container">
        <h1>Buchungen</h1>
        <form class="search-bar" method="get" action="/bookings-list" onsubmit="return false;">
            <input type="text" id="searchInput" name="q" placeholder="Suche nach Name, Zimmer, Zeitraum..." value="${q!""}" autocomplete="off">
            <!-- Кнопка поиска удалена -->
        </form>
        <table id="bookingsTable">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Kunde</th>
                    <th>Zimmer</th>
                    <th>Zeitraum</th>
                    <th>Betrag (Anzahlung)</th>
                </tr>
            </thead>
            <tbody id="bookingsTbody">
                <#if bookings?? && (bookings?size > 0)>
                    <#list bookings as booking>
                        <tr class="clickable-row" data-href="/booking/${booking.ID}">
                            <td>${booking.ID}</td>
                            <td>${booking.customerName!""}</td>
                            <td>${booking.room?has_content?then(booking.room.name, "")}</td>
                            <td>
                                <#if booking.startDate?? && booking.endDate??>
                                    <#assign sd = booking.startDate?date("yyyy-MM-dd")>
                                    <#assign ed = booking.endDate?date("yyyy-MM-dd")>
                                    ${sd?string("dd.MM.yyyy")} - ${ed?string("dd.MM.yyyy")}
                                </#if>
                            </td>
                            <td>
                                <#if booking.prepayment??>
                                    ${booking.prepayment?string["0.00"]} €
                                <#else>
                                    -
                                </#if>
                            </td>
                        </tr>
                    </#list>
                <#else>
                    <tr>
                        <td colspan="5" style="text-align:center;">Keine Buchungen gefunden.</td>
                    </tr>
                </#if>
            </tbody>
        </table>
    </div>
</body>
</html>
