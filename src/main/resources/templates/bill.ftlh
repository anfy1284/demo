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
        }
        h1, h2 {
            text-align: center;
            color: #333;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
        }
        table th, table td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }
        table th {
            background-color: #f4f4f4;
        }
        .total-row td {
            font-weight: bold;
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
        @media print {
            body {
                background-color: white;
                box-shadow: none; /* Убираем тень */
            }
            .print-btn {
                display: none;
            }
            .bill-container {
                box-shadow: none; /* Убираем тень у контейнера */
            }
        }
    </style>
</head>
<body>
    <div class="bill-container">
        <h1>Rechnung</h1>
        <h2>Buchungsdetails</h2>
        <p><strong>Kundenname:</strong> ${booking.customerName}</p>
        <p><strong>Zimmer:</strong> ${booking.room.name}</p>
        <p><strong>Anfangsdatum:</strong> ${booking.startDate}</p>
        <p><strong>Enddatum:</strong> ${booking.endDate}</p>
        <p><strong>Gäste:</strong></p>
        <ul>
            <#list booking.guests as guest>
                <li>${guest.name} (${guest.dateOfBirth})</li>
            </#list>
        </ul>
        <h2>Rechnungsübersicht</h2>
        <table>
            <thead>
                <tr>
                    <th>Beschreibung</th>
                    <th>Betrag</th>
                </tr>
            </thead>
            <tbody>
                <#if bill?has_content>
                    <#list bill as item>
                        <tr>
                            <td>${item.label}</td>
                            <td>${item.value}</td>
                        </tr>
                    </#list>
                <#else>
                    <tr>
                        <td colspan="2">Keine Rechnungsdaten verfügbar</td>
                    </tr>
                </#if>
            </tbody>
        </table>
        <button class="print-btn" onclick="window.print()">Rechnung drucken</button>
    </div>
</body>
</html>
