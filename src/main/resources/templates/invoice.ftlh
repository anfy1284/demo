<!DOCTYPE html>
<html>
<head>
    <title>Rechnung erstellen</title>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f4f9;
        }
        .navbar {
            width: 100%;
            background: #1a237e;
            color: #fff;
            display: flex;
            align-items: center;
            padding: 0 0 0 24px;
            height: 56px;
            box-shadow: 0 2px 8px rgba(26,35,126,0.08);
            font-size: 18px;
            font-weight: 500;
            letter-spacing: 0.5px;
            margin-bottom: 0;
            z-index: 100;
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
        }
        .navbar a {
            color: #fff;
            text-decoration: none;
            margin-right: 32px;
            padding: 0 8px;
            height: 56px;
            display: flex;
            align-items: center;
            border-bottom: 2px solid transparent;
            transition: border-bottom 0.2s, color 0.2s;
        }
        .navbar a.active, .navbar a:hover {
            color: #ffeb3b;
            border-bottom: 2px solid #ffeb3b;
        }
        .navbar a.invoice {
            margin-left:auto;
            background:#43a047;
            padding:0 18px;
            border-radius:6px;
            height:38px;
            display:flex;
            align-items:center;
            font-size:16px;
            font-weight:500;
            box-shadow:0 2px 8px rgba(67,160,71,0.08);
            color:#fff;
        }
        .navbar a.menu-btn {
            margin-left: 0;
            background: none;
            color: #fff;
            border-radius: 0;
            height: 56px;
            display: flex;
            align-items: center;
            font-size: 18px;
            font-weight: 500;
            box-shadow: none;
            padding: 0 8px;
            border-bottom: 2px solid transparent;
            transition: border-bottom 0.2s, color 0.2s;
        }
        .navbar a.menu-btn:hover,
        .navbar a.menu-btn.active {
            color: #ffeb3b;
            border-bottom: 2px solid #ffeb3b;
            background: none;
        }
        .invoice-container {
            max-width: 1200px;
            margin: 32px auto;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.08);
            padding: 24px 32px;
            display: flex;
            flex-direction: row;
            gap: 32px;
            min-height: 600px;
        }
        .invoice-main {
            margin-top: 0px; /* чтобы не перекрывалось меню */
            //flex: 2;
            min-width: 0;
            margin-right: 315px;
        }
        .invoice-controls {
            flex: 1;
            min-width: 320px;
            max-width: 400px;
            background: #f9f9f9;
            border-radius: 8px;
            padding: 18px 18px 18px 18px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.04);
            display: flex;
            flex-direction: column;
            gap: 18px;
            position: fixed;
            top: 56px;
            right: 0;
            width: 370px;
            max-width: 40vw;
            min-width: 320px;
            height: calc(100vh - 56px);
            overflow-y: auto;
            z-index: 101;
        }
        .invoice-controls h2 {
            margin-top: 0;
            font-size: 20px;
            color: #1a237e;
        }
        .invoice-controls label {
            font-weight: bold;
            margin-bottom: 6px;
            color: #333;
        }
        .invoice-controls select, .invoice-controls button {
            width: 100%;
            padding: 8px 10px;
            margin-bottom: 10px;
            border-radius: 5px;
            border: 1px solid #bbb;
            font-size: 15px;
        }
        .invoice-controls ul {
            list-style: none;
            padding: 0;
            margin: 0 0 10px 0;
        }
        .invoice-controls li {
            background: #fff;
            border: 1px solid #ddd;
            border-radius: 5px;
            margin-bottom: 6px;
            padding: 7px 10px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .invoice-controls li .remove-btn {
            background: none;
            border: none;
            color: #dc3545;
            cursor: pointer;
            font-size: 18px;
        }
        .invoice-controls li .remove-btn:hover {
            color: #a71d2a;
        }
        .invoice-controls .print-btn {
            background: #007bff;
            color: #fff;
            border: none;
            border-radius: 5px;
            padding: 10px 0;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            margin-top: 6px; /* уменьшено с 12px до 6px */
        }
        .invoice-controls .print-btn:hover {
            background: #0056b3;
        }
        .invoice-actions-sticky {
            position: sticky;
            bottom: 0;
            background: #f9f9f9;
            padding-bottom: 12px;
            padding-top: 12px;
            box-shadow: 0 -2px 8px rgba(0,0,0,0.04);
            display: flex;
            flex-direction: column;
            gap: 12px;
            z-index: 20;
        }
        @media (max-width: 1100px) {
            .invoice-controls {
                position: static;
                width: 100%;
                max-width: 100%;
                min-width: 0;
                height: auto;
                right: unset;
                top: unset;
            }
            .invoice-container {
                flex-direction: column;
                gap: 0;
                padding: 8px 2vw;
            }
        }
    </style>
</head>
<body>
    <div class="navbar">
        <a href="/bookings" class="menu-btn">Kalender</a>
        <a href="/bookings-list" class="menu-btn">Buchungen</a>
        <a href="/invoices" class="menu-btn">Rechnungen</a>
        <a href="/invoice/new" class="active">Rechnung erstellen</a>
    </div>
    <div style="height:56px"></div> <!-- spacer for fixed navbar -->
    <!--
    <div class="invoice-container">
    -->
        <div class="invoice-main" id="invoiceMain">
            <#if invoiceBookings?has_content>
                <#assign booking=invoiceBookings[0]>
                <#-- bill уже передан из контроллера, не пересчитываем! -->
                <#include "bill.ftlh" />
            <#else>
                <div style="color:#888;font-size:18px;padding:32px 0;">Bitte wählen Sie mindestens eine Buchung aus.</div>
            </#if>
        </div>
        <div class="invoice-controls" id="invoiceControls">
            <#if !(readonly?? && readonly)>
                <div class="invoice-actions-sticky">
                    <button class="print-btn" id="saveInvoiceBtn" type="button" style="margin-bottom:10px;">
                        <span class="material-icons" style="vertical-align:middle;">save</span> Rechnung speichern
                    </button>
                </div>
            </#if>
            <ul id="invoiceBookingsList">
                <#list invoiceBookings as booking>
                    <li data-id="${booking.ID}">
                        <span>${booking.ID} — ${booking.customerName!""} (${booking.startDate} - ${booking.endDate})</span>
                        <#if !(readonly?? && readonly)>
                            <button class="remove-btn" title="Entfernen" onclick="removeBookingFromInvoice('${booking.ID}')">
                                <span class="material-icons">delete</span>
                            </button>
                        </#if>
                    </li>
                </#list>
            </ul>
            <!-- Две кнопки печати и кнопка смены номера -->
            <button class="print-btn" onclick="printColor()">
                <span class="material-icons" style="vertical-align:middle;">print</span> Drucken (Farbe)
            </button>
            <button class="print-btn" style="margin-top:10px;" onclick="printBW()">
                <span class="material-icons" style="vertical-align:middle;">print</span> Drucken (schwarz/weiß)
            </button>
            <#if !!(readonly?? && readonly)>
                <form action="/invoice/change-number/${invoice.number}" method="post" style="display:flex;align-items:center;gap:8px;margin-top:10px;">
                    <input type="text" name="newNumber" value="${invoice.number}" style="width: 110px; font-size: 16px; padding: 4px 8px;" required>
                    <button type="submit" class="print-btn" style="background:#4caf50;min-width:unset;width:auto;padding:10px 16px;">
                        <span class="material-icons">edit</span> Nummer ändern
                    </button>
                </form>
            </#if>
        </div>
    <!--
    </div>
    -->
    <script>
        <#if !(readonly?? && readonly)>
        // Добавление и удаление броней из счета
        function addBookingToInvoice() {
            const select = document.getElementById('bookingSelect');
            const bookingId = select.value;
            if (!bookingId) return;
            window.location = '/invoice/new?add=' + encodeURIComponent(bookingId);
        }
        function removeBookingFromInvoice(id) {
            window.location = '/invoice/new?remove=' + encodeURIComponent(id);
        }
        document.getElementById('saveInvoiceBtn').onclick = function() {
            fetch('/invoice/save', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    bookingIds: Array.from(document.querySelectorAll('#invoiceBookingsList li')).map(li => li.getAttribute('data-id'))
                })
            }).then(resp => resp.json()).then(data => {
                if (data.success && data.invoiceNumber) {
                    // Очищаем счет в сессии после сохранения и переходим на просмотр счета
                    fetch('/invoice/clear', { method: 'POST' }).then(() => {
                        window.location = '/invoice/view/' + encodeURIComponent(data.invoiceNumber);
                    });
                } else if (data.success) {
                    // fallback: если номер не пришел, просто на список
                    fetch('/invoice/clear', { method: 'POST' }).then(() => {
                        window.location = '/invoices';
                    });
                } else {
                    alert(data.error || 'Fehler beim Speichern der Rechnung.');
                }
            });
        };
        </#if>
        // Цветная печать
        function printColor() {
            document.body.classList.remove('bw-print');
            window.print();
        }
        // Черно-белая печать
        function printBW() {
            document.body.classList.add('bw-print');
            window.print();
            setTimeout(function() {
                document.body.classList.remove('bw-print');
            }, 1000);
        }
    </script>
    <style>
        @media print {
            #invoiceControls, .invoice-controls {
                display: none !important;
            }
            .invoice-main, #invoiceMain {
                flex: none !important;
                max-width: 100vw !important;
                width: 100vw !important;
                margin: 0 !important;
                padding: 0 !important;
                background: white !important;
                box-shadow: none !important;
            }
            .invoice-container {
                box-shadow: none !important;
                margin: 0 !important;
                padding: 0 !important;
                background: white !important;
                display: block !important;
            }
            body {
                background: white !important;
            }
        }
        /* Стили для обеих кнопок печати */
        .print-btn {
            background: #007bff;
            color: #fff;
            border: none;
            border-radius: 5px;
            padding: 10px 0;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            margin-top: 6px; /* уменьшено с 12px до 6px */
            width: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            transition: background 0.2s;
        }
        .print-btn:hover {
            background: #0056b3;
        }
        .print-btn .material-icons {
            font-size: 20px;
        }
    </style>
</body>
</html>
