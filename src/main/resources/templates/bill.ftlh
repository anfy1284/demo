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
            padding: 24px 18px 32px 18px;
            border-radius: 12px;
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.13);
            position: relative;
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            gap: 30px;
            margin-bottom: 10px;
        }
        .logo {
            max-width: 45%;
            height: auto;
        }
        .contact-info {
            text-align: left;
            font-size: 13px;
            line-height: 1.5;
            color: #333;
            margin-left: auto;
        }
        h1, h2 {
            text-align: center;
            color: #333;
        }
        .address-block {
            margin: 18px 0 8px 0;
            font-size: 15px;
        }
        .invoice-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 18px;
            margin-bottom: 18px;
            font-size: 14px;
        }
        .invoice-table th, .invoice-table td {
            border: 1px solid #bbb;
            padding: 7px 5px;
            text-align: right;
        }
        .invoice-table th {
            background-color: #f4f4f4;
            color: #222;
            font-weight: bold;
        }
        .invoice-table td:first-child, .invoice-table th:first-child {
            text-align: left;
        }
        .invoice-table tfoot td {
            font-weight: bold;
            background: #f9f9f9;
        }
        .summary-row {
            background: #f1f3f6 !important;
            color: #222 !important;
            font-size: 15px;
            font-weight: bold;
        }
        .highlight-row {
            background: #e0e3ea !important;
            color: #222 !important;
            font-size: 15px;
            font-weight: bold;
        }
        .print-btn {
            display: block;
            margin: 24px auto 0 auto;
            padding: 10px 24px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            text-align: center;
            font-size: 16px;
            font-weight: bold;
        }
        .print-btn:hover {
            background-color: #0056b3;
        }
        @media print {
            @page {
                margin: 12mm 12mm 0 12mm; /* Добавлены отступы сверху, слева и справа */
                size: A4 portrait;
            }
            html, body {
                width: 210mm;
                height: 297mm;
                background: white !important;
                overflow: hidden !important;
                -webkit-print-color-adjust: exact !important;
                print-color-adjust: exact !important;
            }
            body {
                zoom: 1;
                display: flex;
                justify-content: center;
                align-items: flex-start;
            }
            .bill-container {
                box-shadow: none;
                margin: 0;
                position: static;
                padding: 0;
                width: 100vw;
                height: auto;
                transform-origin: top center;
                -webkit-print-color-adjust: exact !important;
                print-color-adjust: exact !important;
            }
            .print-btn {
                display: none !important;
            }
        }
    </style>
</head>
<body>
    <div class="bill-container">
        <div style="border-top: 1px solid #000; margin-bottom: 10px;"></div>
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
        <div style="border-bottom: 1px solid #000; margin-bottom: 10px;"></div>
        <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0;">
            <div class="address-block" style="margin-bottom: 0;">
                <strong>${booking.customerName}</strong><br>
                <#if booking.customerStreet?? && booking.customerHouseNumber??>
                    ${booking.customerStreet} ${booking.customerHouseNumber}<br>
                </#if>
                <#if booking.customerPostalCode?? && booking.customerCity??>
                    ${booking.customerPostalCode} ${booking.customerCity}<br>
                </#if>
                <#if booking.customerCountry??>
                    ${booking.customerCountry}<br>
                </#if>
            </div>
            <div class="address-block">
                ${currentDate}
            </div>
        </div>
        <h2>Rechnung Nr. ${booking.ID}</h2>
        <table class="invoice-table">
            <thead>
                <tr>
                    <th style="width: 38%;">Leistung / Beschreibung</th>
                    <th style="width: 13%;">MwSt.-Satz</th>
                    <th style="width: 13%;">MwSt.</th>
                    <th style="width: 18%;">Gesamtpreis</th>
                </tr>
            </thead>
            <tbody>
                <#if bill?has_content && bill.main?has_content>
                    <#list bill.main as item>
                        <#assign isTotal = item.key?has_content && (item.key == "totalPrice" || item.key == "prepayment" || item.key == "totalSum")>
                        <#if !isTotal>
                        <tr>
                            <td>${item.label}</td>
                            <td>
                                <#if item.taxRate?has_content>
                                    <#if item.taxRate?number == 0>
                                        -
                                    <#else>
                                        ${item.taxRate}%
                                    </#if>
                                <#else>
                                    -
                                </#if>
                            </td>
                            <td>
                                <#if item.tax?has_content && (item.tax == "0.00" || item.tax == "0.00 €" || item.tax == "0" || item.tax == "0.0" || item.tax == "0,00 €")>
                                    -
                                <#else>
                                    ${item.tax}
                                </#if>
                            </td>
                            <td>
                                <#if item.value?has_content && (item.value == "0.00" || item.value == "0.00 €" || item.value == "0" || item.value == "0.0" || item.value == "0,00 €")>
                                    -
                                <#else>
                                    ${item.value}
                                </#if>
                            </td>
                        </tr>
                        </#if>
                    </#list>
                </#if>
                <#-- Итоговые суммы налогов по ставкам -->
                <#if bill?has_content && bill.mwst?has_content>
                    <#list bill.mwst as mwstItem>
                        <tr class="summary-row">
                            <td style="text-align: left;">
                                <#-- Вытаскиваем % из label, если есть -->
                                <#if mwstItem.label?contains("%")>
                                    <#assign parts = mwstItem.label?split("%")>
                                    <#if parts[0]?replace("MwSt. ","")?trim == "0">
                                        MwSt. -
                                    <#else>
                                        MwSt. <span>${parts[0]?replace("MwSt. ","")?trim}</span>%
                                    </#if>
                                <#elseif mwstItem.label == "MwSt. 0%">
                                    MwSt. -
                                <#elseif mwstItem.label?lower_case?contains("gesamt")>
                                    ${mwstItem.label}
                                <#else>
                                    ${mwstItem.label}
                                </#if>
                            </td>
                            <td colspan="1"></td>
                            <td>${mwstItem.value}</td>
                            <td colspan="2"></td>
                        </tr>
                    </#list>
                </#if>
                <#-- Итоговые строки (Betrag, Anzahlung, Restbetrag) в самом низу таблицы -->
                <#if bill?has_content && bill.main?has_content>
                    <#list bill.main as item>
                        <#assign isTotal = item.key?has_content && (item.key == "totalPrice" || item.key == "prepayment" || item.key == "totalSum")>
                        <#if isTotal>
                            <#if item.key == "totalSum">
                                <tr class="highlight-row">
                                    <td style="text-align: left;">${item.label}</td>
                                    <td colspan="3">
                                        <#if item.net?has_content && (item.net == "0.00" || item.net == "0.00 €" || item.net == "0" || item.net == "0.0" || item.net == "0,00 €")>
                                            -
                                        <#else>
                                            ${item.net}
                                        </#if>
                                    </td>
                                </tr>
                            <#else>
                                <tr class="summary-row">
                                    <td style="text-align: left;">${item.label}</td>
                                    <td colspan="3">
                                        <#if item.net?has_content && (item.net == "0.00" || item.net == "0.00 €" || item.net == "0" || item.net == "0.0" || item.net == "0,00 €")>
                                            -
                                        <#else>
                                            ${item.net}
                                        </#if>
                                    </td>
                                </tr>
                            </#if>
                        </#if>
                    </#list>
                </#if>
            </tbody>
        </table>
        <div style="margin-top: 14px; font-size: 14px;">
            <strong>Wir bitten um Überweisung des Betrages auf das folgende Konto:</strong><br>
            Malgorzata Fischer<br>
            Sparkasse Allgäu<br>
            IBAN: DE09 7335 0000 0515 0433 47
        </div>
        <button class="print-btn" onclick="window.print()">Rechnung drucken</button>
    </div>
    <script>
        window.onbeforeprint = function() {
            var pageHeight = 1122; // px for A4 at 96dpi
            var content = document.querySelector('.bill-container');
            if (!content) return;
            var printBtn = content.querySelector('.print-btn');
            var btnHeight = printBtn ? printBtn.offsetHeight : 0;
            var contentHeight = content.scrollHeight - btnHeight;
            var scale = Math.min(1, pageHeight / contentHeight);
            content.style.transform = 'scale(' + scale + ')';
            content.style.transformOrigin = 'top center';
        };
        window.onafterprint = function() {
            var content = document.querySelector('.bill-container');
            if (content) content.style.transform = '';
        };
    </script>
</body>
</html>
