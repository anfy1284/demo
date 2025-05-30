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
            gap: 32px;
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
            margin-top: 12px;
        }
        .invoice-controls .print-btn:hover {
            background: #0056b3;
        }
        .invoice-main {
            flex: 2;
            min-width: 0;
        }
        @media (max-width: 900px) {
            .invoice-container {
                flex-direction: column;
                gap: 0;
                padding: 8px 2vw;
            }
            .invoice-controls {
                max-width: 100%;
            }
        }
    </style>
</head>
<body>
    <div class="navbar">
        <a href="/bookings">Kalender</a>
        <a href="/bookings-list">Buchungen</a>
        <a href="/invoice/new" class="menu-btn active">Rechnung erstellen</a>
    </div>
    <div class="invoice-container">
        <div class="invoice-controls" id="invoiceControls">
            <h2>Buchungen für Rechnung</h2>
            <form id="invoiceForm" onsubmit="return false;">
                <label for="bookingSelect">Buchung hinzufügen:</label>
                <select id="bookingSelect">
                    <option value="">Bitte wählen...</option>
                    <#list allBookings as booking>
                        <option value="${booking.ID}">${booking.ID} — ${booking.customerName!""} (${booking.startDate} - ${booking.endDate})</option>
                    </#list>
                </select>
                <button type="button" onclick="addBookingToInvoice()">Hinzufügen</button>
            </form>
            <ul id="invoiceBookingsList">
                <#list invoiceBookings as booking>
                    <li data-id="${booking.ID}">
                        <span>${booking.ID} — ${booking.customerName!""} (${booking.startDate} - ${booking.endDate})</span>
                        <button class="remove-btn" title="Entfernen" onclick="removeBookingFromInvoice('${booking.ID}')">
                            <span class="material-icons">delete</span>
                        </button>
                    </li>
                </#list>
            </ul>
            <button class="print-btn" onclick="window.print()">
                <span class="material-icons" style="vertical-align:middle;">print</span> Rechnung drucken
            </button>
        </div>
        <div class="invoice-main" id="invoiceMain">
            <#-- Вставляем шаблон счета (bill.ftlh) с данными по всем выбранным броням -->
            <#if invoiceBookings?has_content>
                <#assign booking=invoiceBookings[0]>
                <#include "bill.ftlh" />
            <#else>
                <div style="color:#888;font-size:18px;padding:32px 0;">Bitte wählen Sie mindestens eine Buchung aus.</div>
            </#if>
        </div>
    </div>
    <script>
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
    </style>
</body>
</html>
