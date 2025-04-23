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
            margin-bottom: 15px;
        }
        table th, table td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }
        table th {
            background-color: #f4f4f4;
            font-weight: bold;
        }
        .column-description {
            text-align: left;
        }
        .column-amount {
            text-align: right;
            width: 90px;
        }
        @media print {
            body {
                background-color: white;
                box-shadow: none;
            }
            .bill-container {
                box-shadow: none;
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

        <h2>Rechnung</h2>
        <table id="billTable">
            <thead>
                <tr>
                    <th colspan="2" class="column-description">Rechnung</th>
                </tr>
            </thead>
            <tbody>
                <#if bill?has_content && bill.main?has_content>
                    <#list bill.main as item>
                        <tr>
                            <td class="column-description">${item.label}</td>
                            <td class="column-amount">${item.value}</td>
                        </tr>
                    </#list>
                </#if>
            </tbody>
        </table>

        <table>
            <thead>
                <tr>
                    <th colspan="2" class="column-description">Kurbeitrag</th>
                </tr>
            </thead>
            <tbody>
                <#if bill?has_content && bill.kurbeitrag?has_content>
                    <#list bill.kurbeitrag as item>
                        <tr>
                            <td class="column-description">${item.label}</td>
                            <td class="column-amount">${item.value}</td>
                        </tr>
                    </#list>
                </#if>
            </tbody>
        </table>

        <table id="totalTable">
            <thead>
                <tr>
                    <th colspan="2" class="column-description">Gesamt</th>
                </tr>
            </thead>
            <tbody>
                <#if bill?has_content && bill.gesamt?has_content>
                    <#list bill.gesamt as item>
                        <tr>
                            <td class="column-description">${item.label}</td>
                            <td class="column-amount">${item.value}</td>
                        </tr>
                    </#list>
                </#if>
            </tbody>
        </table>
    </div>
</body>
</html>
