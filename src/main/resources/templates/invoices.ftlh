<!DOCTYPE html>
<html>
<head>
    <title>Счета</title>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <style>
        body { font-family: 'Roboto', sans-serif; background: #f4f4f9; margin: 0; }
        .navbar { width: 100%; background: #1a237e; color: #fff; display: flex; align-items: center; padding: 0 0 0 24px; height: 56px; box-shadow: 0 2px 8px rgba(26,35,126,0.08); font-size: 18px; font-weight: 500; letter-spacing: 0.5px; margin-bottom: 0; z-index: 100; }
        .navbar a { color: #fff; text-decoration: none; margin-right: 32px; padding: 0 8px; height: 56px; display: flex; align-items: center; border-bottom: 2px solid transparent; transition: border-bottom 0.2s, color 0.2s; }
        .navbar a.active, .navbar a:hover { color: #ffeb3b; border-bottom: 2px solid #ffeb3b; }
        .navbar a.menu-btn { margin-left: 0; background: none; color: #fff; border-radius: 0; height: 56px; display: flex; align-items: center; font-size: 18px; font-weight: 500; box-shadow: none; padding: 0 8px; border-bottom: 2px solid transparent; transition: border-bottom 0.2s, color 0.2s; }
        .navbar a.menu-btn:hover, .navbar a.menu-btn.active { color: #ffeb3b; border-bottom: 2px solid #ffeb3b; background: none; }
        .container { max-width: 1200px; margin: 32px auto; background: #fff; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.08); padding: 24px 32px; }
        h1 { color: #1a237e; margin-bottom: 24px; }
        table { width: 100%; border-collapse: collapse; margin-top: 8px; }
        th, td { border: 1px solid #e0e0e0; padding: 10px 8px; text-align: left; }
        th { background: #f4f4f4; color: #222; font-weight: bold; }
        tr:nth-child(even) td { background: #fafbfc; }
        tr:hover td { background: #e8eaf6; }
        tr.clickable-row { cursor: pointer; transition: background 0.13s; }
        tr.clickable-row:hover { background: #e3e7fa !important; }
        .delete-btn { color: #dc3545; background: none; border: none; cursor: pointer; font-size: 18px; }
        .delete-btn:hover { color: #a71d2a; }
    </style>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            document.querySelectorAll('tr[data-href]').forEach(function(row) {
                row.addEventListener('click', function(e) {
                    if (e.target.classList.contains('delete-btn')) return;
                    window.location = row.getAttribute('data-href');
                });
            });
            document.querySelectorAll('.delete-btn').forEach(function(btn) {
                btn.addEventListener('click', function(e) {
                    e.stopPropagation();
                    if (confirm('Счет будет удалён. Продолжить?')) {
                        fetch('/invoice/delete/' + btn.dataset.id, {method:'POST'}).then(() => location.reload());
                    }
                });
            });
        });
    </script>
</head>
<body>
    <div class="navbar">
        <a href="/bookings">Kalender</a>
        <a href="/bookings-list">Buchungen</a>
        <a href="/invoices" class="menu-btn active">Rechnungen</a>
        <a href="/invoice/new" class="menu-btn">Rechnung erstellen</a>
    </div>
    <div class="container">
        <h1>Rechnungen</h1>
        <table>
            <thead>
                <tr>
                    <th>Rechnungsnummer</th>
                    <th>Datum</th>
                    <th>Kunde</th>
                    <th>Buchungen</th>
                    <th>Betrag</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <#if invoices?? && invoices?size gt 0>
                    <#-- bookings должен быть добавлен в модель контроллером: model.addAttribute("bookings", bookingService.getAll()); -->
                    <#list invoices as invoice>
                        <tr class="clickable-row" data-href="/invoice/view/${invoice.number}">
                            <td>${invoice.number}</td>
                            <td>${invoice.date}</td>
                            <td>
                                <#-- Показываем имя клиента первой брони, если есть -->
                                <#if invoice.bookingIds?has_content>
                                    <#assign firstBookingId = invoice.bookingIds[0]>
                                    <#-- Безопасно ищем первую бронь без #assign firstBooking = null -->
                                    <#assign firstBooking = "">
                                    <#list bookings as b>
                                        <#if b.ID == firstBookingId>
                                            <#assign firstBooking = b.customerName!-"">
                                            <#break>
                                        </#if>
                                    </#list>
                                    <#if firstBooking?has_content>
                                        ${firstBooking}
                                    <#else>
                                        -
                                    </#if>
                                <#else>
                                    -
                                </#if>
                            </td>
                            <td>
                                <#list invoice.bookingIds as id>
                                    <span>${id}</span><#if id_has_next>, </#if>
                                </#list>
                            </td>
                            <td>${invoice.total!""}</td>
                            <td>
                                <button class="delete-btn" data-id="${invoice.number}" title="Löschen">
                                    <span class="material-icons">delete</span>
                                </button>
                            </td>
                        </tr>
                    </#list>
                <#else>
                    <tr><td colspan="6" style="text-align:center;">Keine Rechnungen gefunden.</td></tr>
                </#if>
            </tbody>
        </table>
    </div>
</body>
</html>
