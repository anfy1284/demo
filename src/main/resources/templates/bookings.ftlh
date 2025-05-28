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
        h1 {
            text-align: center;
            color: #333;
            margin: 20px 0;
        }
        .month-navigation {
            display: flex;
            justify-content: center;
            align-items: center;
            margin-bottom: 20px;
        }
        .month-navigation button {
            background-color: #007bff; /* Синий фон */
            color: white; /* Белый текст */
            border: none;
            padding: 10px 15px;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 5px;
        }
        .month-navigation button:hover {
            background-color: #0056b3; /* Темно-синий при наведении */
        }
        .month-navigation button .material-icons {
            color: white; /* Белые стрелки */
        }
        .month-navigation span {
            margin: 0 15px;
            font-size: 18px;
            font-weight: bold;
            color: #333;
        }
        .table-container {
            overflow-x: auto;
            margin: 20px;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            background: white;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 0;
        }
        th, td {
            padding: 12px 15px;
            text-align: center;
            border: 1px solid #ddd;
        }
        th {
            background-color: #f4f4f4;
            color: #333;
            font-weight: bold;
        }
        td {
            background-color: #fff;
        }
        td.date-column {
            width: 120px; /* Фиксированная ширина для всех дат */
        }
        td a {
            text-decoration: none;
            color: #007bff;
            font-weight: bold;
        }
        td a:hover {
            text-decoration: underline;
        }
        td span {
            color: #6c757d; /* Серый цвет */
            font-weight: normal; /* Не жирный */
            text-decoration: none; /* Убираем подчеркивание */
        }
        tr:nth-child(even) td {
            background-color: #f9f9f9;
        }
        tr:hover td {
            background-color: #e9ecef;
        }
        @media (max-width: 768px) {
            th, td {
                font-size: 12px;
                padding: 8px 10px;
            }
            .month-navigation button {
                padding: 8px 10px;
                font-size: 12px;
            }
            .month-navigation span {
                font-size: 16px;
            }
        }
        .booking-id {
            font-weight: bold;
            color: #007bff;
        }
        .booking-customer {
            font-size: 14px;
            color: #333;
        }
        .booking-description {
            font-size: 12px;
            color: #6c757d;
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
    </style>
</head>
<body>
    <div class="navbar">
        <a href="/bookings" class="active">Kalender</a>
        <a href="/bookings-list">Buchungen</a>
        <a href="/invoice/new" class="menu-btn">Rechnung erstellen</a>
    </div>
    <h1>Buchungen für ${month}/${year}</h1>
    <div class="month-navigation">
        <form action="/bookings/${previousYear}/${previousMonth}" method="get">
            <button type="submit">
                <span class="material-icons">arrow_back</span> Zurück
            </button>
        </form>
        <span>${month}/${year}</span>
        <form action="/bookings/${nextYear}/${nextMonth}" method="get">
            <button type="submit">
                Weiter <span class="material-icons">arrow_forward</span>
            </button>
        </form>
    </div>
    <div class="table-container">
        <table>
            <thead>
                <tr>
                    <th class="date-column">Datum</th>
                    <#list rooms as room>
                        <th>${room.name}</th>
                    </#list>
                </tr>
            </thead>
            <tbody>
                <#list datesOfMonth as date>
                    <tr>
                        <td class="date-column">${displayDatesOfMonth[date_index]}</td> <!-- Используем новую переменную -->
                        <#list rooms as room>
                            <#if room.ID??>
                                <#if bookingsMap[date][room.ID]??>
                                    <#assign booking = bookingsMap[date][room.ID]>
                                    <#if booking.startDate == date>
                                        <td rowspan="${booking.duration}">
                                            <a href="/booking/${booking.ID}">
                                                <div class="booking-id">${booking.ID}</div>
                                                <div class="booking-customer">${booking.customerName! 'Unbekannt'}</div>
                                                <div class="booking-description">${booking.description! ''}</div>
                                            </a>
                                        </td>
                                    <#else>
                                        <!-- Skip rendering -->
                                    </#if>
                                <#else>
                                    <td>
                                        <a href="/create-booking?date=${date}&roomId=${room.ID}">
                                            <span>${displayDatesOfMonth[date_index]}</span>
                                        </a>
                                    </td>
                                </#if>
                            <#else>
                                <td>Nicht verfügbar</td>
                            </#if>
                        </#list>
                    </tr>
                </#list>
            </tbody>
        </table>
    </div>
</body>
</html>
