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
        }
        .header {
            display: flex;
            justify-content: flex-start; /* Выравниваем элементы по левому краю */
            align-items: center;
            gap: 50px; /* Устанавливаем расстояние между логотипом и текстом */
            margin-bottom: 10px;
        }
        .logo {
            max-width: 50%; /* Устанавливаем масштаб 50% */
            height: auto;
        }
        .contact-info {
            text-align: left; /* Выравнивание текста по левому краю */
            font-size: 14px;
            line-height: 1.5;
            color: #333;
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
        <!-- Смещаем содержимое вниз -->
        <div style="margin-top: 20px;">
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

            <p><strong>In diesem Betrag sind 7% (${bill.mwst}) MwSt. enthalten</strong></p>

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
        <div style="text-align: center; margin-top: 20px;">
            <button class="print-btn" onclick="window.print()">Rechnung drucken</button>
        </div>
    </div>
</body>
</html>
