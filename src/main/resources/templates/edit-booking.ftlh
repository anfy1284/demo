<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Edit Booking</title>
</head>
<body>
    <h1>Edit Booking</h1>
    <#if method == "edit">
        <form action="/booking/${booking.ID}" method="post">
    <#elseif method == "create">
        <form action="/booking/new" method="post">
    </#if>

        <label for="roomId">Room:</label>
        <select id="roomId" name="roomId" required>
            <#list rooms as room>
                <#if booking.room??>
                    <option value="${room.ID}" <#if room.ID == booking.room.ID>selected</#if>>${room.name}</option>
                <#else>
                    <option value="${room.ID}">${room.name}</option>
                </#if>
            </#list>
        </select>
        <br>
        <label for="customerName">Customer Name:</label>
        <input type="text" id="customerName" name="customerName" value="${booking.customerName}" required>
        <br>
        <label for="startDate">Start Date:</label>
        <input type="date" id="startDate" name="startDate" value="${booking.startDate}" required>
        <br>
        <label for="endDate">End Date:</label>
        <input type="date" id="endDate" name="endDate" value="${booking.endDate}" required>
        <br>
        <label for="description">Description:</label>
        <textarea id="description" name="description" required>${booking.description}</textarea>
        <br>
        <button type="submit">Save</button>
    </form>
    
    <a href="/bookings">Cancel</a>
</body>
</html>
