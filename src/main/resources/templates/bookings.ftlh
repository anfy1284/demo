<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Bookings</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: center;
        }
        th {
            background-color: #f4f4f4;
        }
        .month-navigation {
            display: flex;
            justify-content: center;
            align-items: center;
            margin-bottom: 20px;
        }
        .month-navigation button {
            background-color: #f4f4f4;
            border: 1px solid #ddd;
            padding: 5px 10px;
            cursor: pointer;
        }
        .month-navigation span {
            margin: 0 15px;
            font-size: 18px;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <h1>Bookings for ${month}/${year}</h1>
    <div class="month-navigation">
        <form action="/bookings/${previousYear}/${previousMonth}" method="get">
            <button type="submit">&lt;</button>
        </form>        
        <span>Current Month</span>
        <form action="/bookings/${nextYear}/${nextMonth}" method="get">
            <button type="submit">&gt;</button>
        </form>        
    </div>
    <table>
        <thead>
            <tr>
                <th>Date</th>
                <#list rooms as room>
                    <th>${room.name}</th>
                </#list>
            </tr>
        </thead>
        <tbody>
            <#list datesOfMonth as date>
                <tr>
                    <td>${date}</td>
                    <#list rooms as room>
                        <#if room.ID??>
                            <#if bookingsMap[date][room.ID]??>
                                <#assign booking = bookingsMap[date][room.ID]>
                                    <#if booking.startDate == date>
                                        <td rowspan="${booking.duration}">
                                            ${booking.description}
                                        </td>
                                    <#else>
                                        
                                    </#if>
                            <#else>
                                <td>Available</td>
                            </#if>
                        <#else>
                            <td>N/A</td>
                        </#if>
                    </#list>
                </tr>
            </#list>
        </tbody>
    </table>
</body>
</html>
