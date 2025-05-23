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
            /* Удаляем нижнюю границу */
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
            border: none; /* Убираем все границы */
            border-bottom: 1px solid #000; /* Делаем нижнюю границу таблиц черной */
        }
        table th, table td {
            border: none; /* Убираем границы строк */
            padding: 10px;
            text-align: left;
        }
        table tr:last-child td {
            font-weight: bold; /* Делаем последние строки таблиц жирными */
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
            @page {
                margin: 0; /* Убираем поля страницы */
            }
            body {
                background-color: white;
                box-shadow: none;
                margin: 0;
                padding: 0;
            }
            .print-btn {
                display: none;
            }
            .bill-container {
                box-shadow: none;
                padding-top: 0px;
                margin: 0; /* Убираем отступы */
                position: static; /* Убираем центрирование */
                padding-left: 2.5cm; /* Добавляем отступ слева 2 см */
            }
        }
    </style>
</head>
<body>
    <div class="bill-container" style="padding-top: 0px;"> <!-- Уменьшаем верхний отступ -->
        <table style="width: 100%; border-collapse: collapse;">
            <tr>
                <td style="border-bottom: 1px solid #000;"></td> <!-- Делаем нижнюю границу черной -->
            </tr>
        </table>
        <div class="header" style="justify-content: center; text-align: center; margin-top: 10px;">
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
        <table style="width: 100%; border-collapse: collapse; margin-top: 0px;">
            <tr>
                <td style="border-top: 1px solid #000;"></td> <!-- Делаем верхнюю границу черной -->
            </tr>
        </table>
        <!-- Удаляем заголовки и добавляем текущую дату, имя и адрес клиента -->
        <table class="rechn" style="margin-top: 20px; font-weight: normal; border-bottom: none;"> <!-- Убираем нижнюю границу -->
            <tr>
                <td class="column-description" style="width: 70%; font-weight: normal;">
                    <strong>${booking.customerName}</strong><br>
                    <br>
                    <#list customerAddressLines as line>
                        ${line}<br>
                    </#list>
                </td>
                <td class="column-amount" style="text-align: right; font-weight: normal; vertical-align: top;">
                    ${currentDate}
                </td>
            </tr>
        </table>
        <!-- Продолжаем с остальным содержимым -->
        <div style="margin-top: 20px; text-align: center;" >
            <p><strong>Rechnung Nr. ${booking.ID}</strong></p>
            <p><strong>Leistungsnachweis vom ${formattedStartDate} bis zum ${formattedEndDate}</strong></p>
        </div>
        <table id="billTable" class="rechn" style="border-bottom: none;">
            <tbody>
                <#if bill?has_content && bill.main?has_content>
                    <#list bill.main as item>
                    <#if item?is_last>
                        <!-- Формирование последней строки -->
                        <tr style="font-weight: bold; border-bottom: 1px solid #000;"> <!-- Делаем нижнюю границу строки черной -->
                            <td class="column-description">${item.label}</td>
                            <td class="column-amount">${item.value}</td>
                        </tr>
                    <#else>
                        <!-- Формирование обычной строки -->
                        <tr>
                            <td class="column-description">${item.label}</td>
                            <td class="column-amount">${item.value}</td>
                        </tr>
                    </#if>
                    </#list>
                </#if>
                <tr>
                    <td class="column-description" colspan="2" style="font-weight: normal; border: none;">In diesem Betrag sind 7% (${bill.mwst}) MwSt. enthalten</td>
                </tr>
            </tbody>
        </table>
        
        <table class="rechn">
            <thead>
                <tr>
                    <th colspan="2" class="column-description" style="text-align: center;">Kurabgabe</th>
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

        <table id="totalTable" class="rechn">
            <thead>
                <tr>
                    <th colspan="2" class="column-description" style="text-align: center;">Betrag</th>
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
        <table class="rechn" style="margin-top: 20px; font-weight: normal; border-bottom: none;"> <!-- Убираем нижнюю границу -->
            <tr>
                <td class="column-description" style="width: 100%; font-weight: normal;">
                    Wir bitten um Überweisung des Betrages auf das folgende Konto:<br>
                    <br>
                    Malgorzata Fischer<br>
                    Sparkasse Allgäu<br>
                    IBAN: DE09 7335 0000 0515 0433 47
                </td>
            </tr>
        </table>
        <div style="text-align: center; margin-top: 20px;">
            <button class="print-btn" onclick="window.print()">Rechnung drucken</button>
        </div>
    </div>
</body>
</html>
