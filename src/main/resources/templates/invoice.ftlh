<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Rechnung</title>
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f4f9;
        }
        .bill-container {
            max-width: 800px;
            margin: 20px auto;
            background: #fff;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            position: relative;
            padding-top: 50px;
            padding-bottom: 50px;
            padding-left: 30px;
            padding-right: 30px;
        }
        .header {
            display: flex;
            justify-content: flex-start;
            align-items: center;
            gap: 50px;
            margin-bottom: 10px;
        }
        .logo {
            max-width: 50%;
            height: auto;
        }
        .contact-info {
            text-align: left;
            font-size: 14px;
            line-height: 1.5;
            color: #333;
        }
        h1, h2 {
            text-align: center;
            color: #333;
        }
        table.rechn {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 15px;
            border: none;
            border-bottom: 1px solid #000;
        }
        table th, table td {
            border: none;
            padding: 10px;
            text-align: left;
        }
        table tr:last-child td {
            font-weight: bold;
        }
        .column-description {
            text-align: left;
        }
        .column-amount {
            text-align: right;
            width: 90px;
        }
        .print-btn {
            display: block;
            margin: 20px auto;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-align: center;
            font-size: 16px;
        }
        .print-btn:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
    <div class="bill-container">
        <div class="header">
            <img src="/images/beim_seiler_4c_logo_2021.png" alt="Logo" class="logo">
            <div class="contact-info">
                Gästehaus Beim Seiler<br>
                Hauptstr. 8 + 10<br>
                87484 Nesselwang<br>
                Tel 08361/92527-08<br>
                Fax 08361/92527-07<br>
                www.beim-seiler.de<br>
                Steuern. 125/217/31182
            </div>
        </div>
        <h1>
            <#if invoice.id??>
                Rechnung Nr. ${invoice.id}
            <#else>
                Rechnung Nr. Nicht verfügbar
            </#if>
        </h1>
        <h2>Verknüpfte Buchungen:</h2>
        <ul>
            <#if invoice.bookings?? && invoice.bookings?size > 0>
                <#list invoice.bookings as booking>
                    <li>${booking.customerName} (${booking.startDate} - ${booking.endDate})</li>
                    <#if booking.roomOrders?? && booking.roomOrders?size > 0>
                        <ul>
                            <#list booking.roomOrders as order>
                                <li>Zimmerbestellung: ${order.bezeichnung} - ${order.preis?string("0.00")} €</li>
                            </#list>
                        </ul>
                    </#if>
                </#list>
            </#if>
        </ul>
        <h2>Rechnungsposten:</h2>
        <table class="rechn">
            <thead>
                <tr>
                    <th>Beschreibung</th>
                    <th>Betrag</th>
                </tr>
            </thead>
            <tbody>
                <#list invoice.items as item>
                    <tr>
                        <td class="column-description">${item.description}</td>
                        <td class="column-amount">${item.amount}</td>
                    </tr>
                </#list>
            </tbody>
        </table>
        <h3>Gesamtbetrag: ${invoice.totalAmount}</h3>
        <div style="text-align: center; margin-top: 20px;">
            <form action="/invoices/save" method="post" style="display: inline;">
                <#if invoice.id??>
                    <input type="hidden" name="invoiceId" value="${invoice.id}">
                </#if>
                <button type="submit" class="print-btn">Speichern</button>
            </form>
            <form action="/invoices/cancel" method="get" style="display: inline;">
                <#if invoice.id??>
                    <input type="hidden" name="invoiceId" value="${invoice.id}">
                </#if>
                <button type="submit" class="print-btn" style="background-color: #dc3545;">Abbrechen</button>
            </form>
        </div>
    </div>
</body>
</html>
