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
    </style>
</head>
<body>
    <h1>Booking Table</h1>
    <table>
        <thead>
            <tr>
                <th>Date</th>
                <th th:each="room : ${rooms}" th:text="${room.name}">Room</th>
            </tr>
        </thead>
        <tbody>
            <!-- <tr th:each="entry : ${tableData}">
                <td th:text="${entry.key}">Date</td>
                <td th:each="room : ${rooms}" th:text="${entry.value[room.id] ?: 'Available'}">Details</td>
            </tr> -->
        </tbody>
    </table>
</body>
</html>
