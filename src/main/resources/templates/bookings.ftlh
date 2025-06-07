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
            position: relative;
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
            height: 40px; /* фиксированная высота для всех кнопок */
        }
        .month-navigation button:hover {
            background-color: #0056b3; /* Темно-синий при наведении */
        }
        .month-navigation button .material-icons {
            color: white; /* Белые стрелки */
        }
        /* Buchung hinzufügen button */
        #add-booking-btn {
            position: absolute;
            right: 0;
            top: 0;
            bottom: 0;
            margin: auto 0;
            display: inline-flex;
            align-items: center;
            background: #43a047;
            color: #fff;
            padding: 0 15px;
            border-radius: 5px;
            font-size: 14px;
            font-weight: 500;
            text-decoration: none;
            box-shadow: 0 2px 8px rgba(67,160,71,0.08);
            gap: 8px;
            border: none;
            cursor: pointer;
            height: 40px; /* Совпадает с .month-navigation button */
            line-height: 40px;
        }
        #add-booking-btn .material-icons {
            font-size: 20px;
            color: #fff;
            line-height: 40px;
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
            width: 145px; /* Было 120px, стало чуть шире */
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
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
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
        .fixed-header-block {
            position: fixed;
            top: 56px;
            left: 0;
            right: 0;
            z-index: 99;
            background: #f4f4f9;
            box-shadow: 0 2px 8px rgba(26,35,126,0.04);
            /* Добавим overflow-x для синхронизации прокрутки */
            overflow-x: auto;
        }
        .calendar-spacer {
            height: 215px; /* увеличено для учета высоты заголовка и навигации */
        }
        .fixed-header-table {
            width: 100%;
            border-collapse: collapse;
            table-layout: fixed;
        }
        .fixed-header-table th,
        .fixed-header-table td,
        .main-table th,
        .main-table td {
            padding: 12px 15px;
            text-align: center;
            border: 1px solid #ddd;
        }
        .fixed-header-table th,
        .main-table th {
            background-color: #f4f4f4;
            color: #333;
            font-weight: bold;
        }
        .main-table {
            width: 100%;
            border-collapse: collapse;
            table-layout: fixed;
        }
        .main-table th,
        .main-table td {
            /* Совпадает с .fixed-header-table */
        }
        .main-table td.date-column,
        .fixed-header-table th.date-column {
            width: 145px;
        }
        @media (max-width: 768px) {
            .fixed-header-block {
                position: static;
                box-shadow: none;
            }
            .calendar-spacer {
                height: 0;
            }
        }
    </style>
</head>
<body>
    <div class="navbar">
        <a href="/bookings" class="active">Kalender</a>
        <a href="/bookings-list" class="menu-btn">Buchungen</a>
        <a href="/invoices" class="menu-btn">Rechnungen</a>
        <a href="/invoice/new" class="menu-btn">Rechnung erstellen</a>
    </div>
    <div class="fixed-header-block">
        <h1 style="margin: 20px 0 0 0; text-align: center; color: #333;">Buchungen für ${month}/${year}</h1>
        <div class="month-navigation" style="justify-content: center; position: relative; max-width:1200px; margin-left:auto; margin-right:auto;">
            <form action="/bookings/${previousYear}/${previousMonth}" method="get" style="margin:0;">
                <button type="submit">
                    <span class="material-icons">arrow_back</span> Zurück
                </button>
            </form>
            <span style="margin: 0 24px;">${month}/${year}</span>
            <form action="/bookings/${nextYear}/${nextMonth}" method="get" style="margin:0;">
                <button type="submit">
                    Weiter <span class="material-icons">arrow_forward</span>
                </button>
            </form>
            <a href="/create-booking" id="add-booking-btn" style="
                position: absolute;
                right: 0;
                top: 0;
                bottom: 0;
                margin: auto 0;
                display: inline-flex;
                align-items: center;
                background: #43a047;
                color: #fff;
                padding: 0 15px;
                border-radius: 5px;
                font-size: 14px;
                font-weight: 500;
                text-decoration: none;
                box-shadow: 0 2px 8px rgba(67,160,71,0.08);
                gap: 8px;
                border: none;
                cursor: pointer;
                height: 40px;
                line-height: 40px;
            ">
                <span class="material-icons" style="font-size:20px;color:#fff;line-height:40px;">add</span>
                <span style="display:inline-block;line-height:40px;">Buchung hinzufügen</span>
            </a>
        </div>
        <div style="overflow-x:auto;">
            <table class="fixed-header-table">
                <thead>
                    <tr>
                        <th class="date-column">Datum</th>
                        <#list rooms as room>
                            <th>${room.name}</th>
                        </#list>
                    </tr>
                </thead>
            </table>
        </div>
    </div>
    <div class="calendar-spacer"></div>
    <table class="main-table">
        <thead style="display:none;">
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
                    <td class="date-column">${displayDatesOfMonth[date_index]}</td>
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
                            <td>Nеподходящий номер</td>
                        </#if>
                    </#list>
                </tr>
            </#list>
        </tbody>
    </table>
    <script>
document.addEventListener('DOMContentLoaded', function() {
    // Если пользователь кликнул на ячейку календаря, запоминаем дату и комнату
    let selectedDate = null;
    let selectedRoomId = null;

    // Пример: предполагается, что у ячеек есть data-date и data-room-id
    document.querySelectorAll('.calendar-cell').forEach(cell => {
        cell.addEventListener('click', function() {
            selectedDate = this.getAttribute('data-date');
            selectedRoomId = this.getAttribute('data-room-id');
            // Подсветить выбранную ячейку, если нужно
        });
    });

    // При клике на кнопку Buchung hinzufügen — если выбраны дата и комната, подставляем их в ссылку
    document.getElementById('add-booking-btn').addEventListener('click', function(e) {
        if (selectedDate && selectedRoomId) {
            e.preventDefault();
            // Используем window.encodeURIComponent из JS, а не Freemarker!
            window.location.href = '/create-booking?date=' + encodeURIComponent(selectedDate) + '&roomId=' + encodeURIComponent(selectedRoomId);
        }
        // иначе — стандартное поведение (форма без параметров)
    });
});
</script>
</body>
</html>
