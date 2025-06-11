<!DOCTYPE html>
<html>
<head>
    <title>Rechnung Nr. ${invoice.number}</title>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <style>
        body { font-family: 'Roboto', sans-serif; background: #f4f4f9; margin: 0; }
        .navbar { width: 100%; background: #1a237e; color: #fff; display: flex; align-items: center; padding: 0 0 0 24px; height: 56px; box-shadow: 0 2px 8px rgba(26,35,126,0.08); font-size: 18px; font-weight: 500; letter-spacing: 0.5px; margin-bottom: 0; z-index: 100; }
        .navbar a { color: #fff; text-decoration: none; margin-right: 32px; padding: 0 8px; height: 56px; display: flex; align-items: center; border-bottom: 2px solid transparent; transition: border-bottom 0.2s, color 0.2s; }
        .navbar a.active, .navbar a:hover { color: #ffeb3b; border-bottom: 2px solid #ffeb3b; }
        .navbar a.menu-btn { margin-left: 0; background: none; color: #fff; border-radius: 0; height: 56px; display: flex; align-items: center; font-size: 18px; font-weight: 500; box-shadow: none; padding: 0 8px; border-bottom: 2px solid transparent; transition: border-bottom 0.2s, color 0.2s; }
        .navbar a.menu-btn:hover, .navbar a.menu-btn.active { color: #ffeb3b; border-bottom: 2px solid #ffeb3b; background: none; }
        .container { max-width: 900px; margin: 32px auto; background: #fff; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.08); padding: 24px 32px; }
        .meta { display: flex; gap: 32px; margin-bottom: 18px; }
        .meta-block { flex: 1; }
        .meta-block label { font-weight: bold; color: #333; }
        .readonly { background: #f4f4f4; border: 1px solid #bbb; border-radius: 5px; padding: 8px 10px; font-size: 15px; }
        h1 { color: #1a237e; margin-bottom: 24px; }
        .readonly-table { width: 100%; border-collapse: collapse; margin-top: 8px; }
        .readonly-table th, .readonly-table td { border: 1px solid #e0e0e0; padding: 10px 8px; text-align: left; }
        .readonly-table th { background: #f4f4f4; color: #222; font-weight: bold; }
        .readonly-table tr:nth-child(even) td { background: #fafbfc; }
        .readonly-table tr:hover td { background: #e8eaf6; }
    </style>
</head>
<body>
    <div class="navbar">
        <a href="/bookings">Kalender</a>
        <a href="/bookings-list">Buchungen</a>
        <a href="/invoices" class="menu-btn active">Rechnungen</a>
        <a href="/invoice/new" class="menu-btn">Rechnung erstellen</a>
    </div>
    <div class="container">
        <h1>Rechnung Nr. ${invoice.number}</h1>
        <div class="meta">
            <div class="meta-block">
                <label>Datum</label><br>
                <span class="readonly">${invoice.date}</span>
            </div>
            <div class="meta-block">
                <label>Rechnungsnummer</label><br>
                <span class="readonly">${invoice.number}</span>
            </div>
        </div>
        <div>
            <label>Buchungen:</label>
            <ul>
                <#list invoiceBookings as booking>
                    <li>${booking.ID} — ${booking.customerName!""} (${booking.startDate} - ${booking.endDate})</li>
                </#list>
            </ul>
        </div>
        <#assign booking=invoiceBookings[0]>
        <#assign bill=invoice.bill>
        <#include "bill.ftlh" />
    </div>
</body>
</html>
