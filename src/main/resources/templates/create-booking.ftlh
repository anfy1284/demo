<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Create Booking</title>
</head>
<body>
    <h1>Create Booking</h1>
    <form action="/create-booking" method="post">
        <input type="hidden" name="date" value="${date}">
        <input type="hidden" name="roomId" value="${room.ID}">
        <p><strong>Date:</strong> ${date}</p>
        <p><strong>Room:</strong> ${room.name}</p>
        <label for="customerName">Customer Name:</label>
        <input type="text" id="customerName" name="customerName" required>
        <br>
        <label for="description">Description:</label>
        <textarea id="description" name="description" required></textarea>
        <br>
        <button type="submit">Create Booking</button>
    </form>
    <a href="/bookings">Cancel</a>
</body>
</html>
