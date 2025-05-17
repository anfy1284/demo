<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Rechnung erstellen</title>
</head>
<body>
    <h1>Rechnung erstellen</h1>
    <#if booking?? && booking.ID??>
        <p><strong>Buchung:</strong> ${booking.customerName} (${booking.startDate} - ${booking.endDate})</p>
        <form action="/invoices/create" method="post">
            <input type="hidden" name="bookingId" value="${booking.ID}">
            <button type="submit">Rechnung erstellen</button>
        </form>
    <#else>
        <p>Fehler: Buchung wurde nicht gefunden oder ist ungültig.</p>
    </#if>
</body>
</html>
