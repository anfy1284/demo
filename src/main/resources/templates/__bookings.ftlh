<!DOCTYPE html>
<html>
<head>
    <title>Bookings</title>
</head>
<body>
    <h1>Bookings for ${month}/${year}</h1>
    <table border="1">
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
                        <td> <!-- Add room-specific booking details here --> </td>
                    </#list>
                </tr>
            </#list>
        </tbody>
    </table>
</body>
</html>