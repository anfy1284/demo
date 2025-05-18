<script>
    function validateForm() {
        return true;
    }
</script>

<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title><#if method == "edit">Rechnung bearbeiten<#else>Rechnung erstellen</#if></title>
</head>
<body>
    <h1><#if method == "edit">Rechnung bearbeiten<#else>Rechnung erstellen</#if></h1>

    <#if method == "edit">
        <form action="/invoices/${invoice.id}" method="post" onsubmit="return validateForm()">
    <#elseif method == "create">
        <form action="/invoices/create" method="post" onsubmit="return validateForm()">
    </#if>

        <h2>Verknüpfte Buchungen:</h2>
        <ul>
            <#if invoice.bookings?? && (invoice.bookings?size > 0)>
                <#list invoice.bookings as booking>
                    <li>${booking.customerName} (${booking.startDate} - ${booking.endDate})</li>
                </#list>
            <#else>
                <p>Keine Buchungen verknüpft.</p>
            </#if>
        </ul>
        <h2>Rechnungsposten:</h2>
        <table>
            <thead>
                <tr>
                    <th>Beschreibung</th>
                    <th>Betrag</th>
                </tr>
            </thead>
            <tbody>
                <#if invoice.items?? && (invoice.items?size > 0)>
                    <#list invoice.items as item>
                        <tr>
                            <td><input type="text" name="items[${item_index}].description" value="${item.description}" required></td>
                            <td><input type="number" name="items[${item_index}].amount" value="${item.amount}" required></td>
                        </tr>
                    </#list>
                <#else>
                    <tr>
                        <td colspan="2">Keine Rechnungsposten vorhanden.</td>
                    </tr>
                </#if>
            </tbody>
        </table>
        <button type="submit"><#if method == "edit">Speichern<#else>Erstellen</#if></button>
    </form>
</body>
</html>
