<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Booking Details</title>
</head>
<body>
    <h1>Booking Details</h1>
    <p><strong>ID:</strong> ${booking.ID}</p>
    <p><strong>Room:</strong> ${booking.room.name}</p>
    <p><strong>Customer Name:</strong> ${booking.customerName! 'N/A'}</p>
    <p><strong>Start Date:</strong> ${booking.startDate}</p>
    <p><strong>End Date:</strong> ${booking.endDate}</p>
    <p><strong>Status:</strong> ${booking.status! 'Unknown'}</p>
    <!-- <a href="/bookings/${year}/${month}">Back to Bookings</a> -->
</body>
</html>
