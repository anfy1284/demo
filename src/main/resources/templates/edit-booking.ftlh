<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Buchung bearbeiten</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }
        .container {
            width: 90%;
            max-width: 1200px;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            overflow: hidden;
            display: flex;
            flex-direction: row;
            flex-wrap: wrap;
            margin-top: 56px; /* чтобы не перекрывалось меню */
        }
        .form-container, .bill-container {
            padding: 20px;
            box-sizing: border-box;
        }
        .form-container {
            flex: 2;
            border-right: 1px solid #ddd;
        }
        .bill-container {
            flex: 1;
            background: #f9f9f9;
        }
        h1, h2 {
            margin: 0 0 20px;
            color: #333;
        }
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
            color: #555;
        }
        input[type="text"], input[type="date"], input[type="number"], textarea, select {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border: 1px solid #ccc;
            border-radius: 5px;
            box-sizing: border-box;
            font-size: 14px;
        }
        textarea {
            resize: vertical;
        }
        button {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 10px 15px;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            display: flex;
            justify-content: center; /* Центрирование текста */
            align-items: center;
            gap: 5px;
        }
        button:hover {
            background-color: #0056b3;
        }
        .icon-btn {
            background: none;
            border: none;
            cursor: pointer;
            padding: 0;
            color: #dc3545;
        }
        .icon-btn:hover {
            color: #a71d2a;
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
        /* Добавлено: убираем нижний отступ у guestsTable и roomOrdersTable */
        #guestsTable,
        #roomOrdersTable {
            margin-bottom: 0;
        }
        .add-guest-btn {
            margin-top: 0;
            background-color: #007bff;
            align-self: flex-start;
        }
        .add-guest-btn:hover {
            background-color: #0056b3;
        }
        .form-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 20px;
            gap: 10px;
        }
        .form-footer button, .form-footer a {
            flex: 1;
            text-align: center;
        }
        .form-footer a {
            color: #007bff;
            text-decoration: none;
            font-size: 14px;
            padding: 10px 15px;
            border: 1px solid #007bff;
            border-radius: 5px;
            background-color: white;
        }
        .form-footer a:hover {
            background-color: #007bff;
            color: white;
        }
        .bill-container h2 {
            margin-top: 0;
            color: #333;
        }
        .bill-container table td {
            text-align: right;
        }
        .bill-container table td:first-child {
            text-align: left;
        }
        .bill-container table td:nth-child(2) {
            width: 90px;
        }
        .guest-section { 
            display: flex;
            flex-direction: column;
            gap: 10px;
            margin-bottom: 20px;
        }
        .print-bill-btn {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 10px 15px;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            text-align: center;
            margin-top: 20px;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .print-bill-btn:hover {
            background-color: #0056b3;
        }
        .print-bill-btn .material-icons {
            font-size: 20px;
        }

        /* Responsive Design */
        @media (max-width: 768px) {
            .container {
                flex-direction: column;
            }
            .form-container {
                border-right: none;
                border-bottom: 1px solid #ddd;
            }
            .bill-container {
                padding-top: 10px;
            }
            .form-footer {
                flex-direction: row; /* Кнопки в ряд на мобильных устройствах */
                gap: 10px;
            }
        }

        .suggestions-list {
            list-style: none;
            margin: 0;
            padding: 0;
            border: 1px solid #ccc;
            max-height: 150px;
            overflow-y: auto;
            position: absolute;
            background: white;
            z-index: 1000;
            width: calc(100% - 2px); /* Совпадает с шириной поля ввода */
            box-sizing: border-box;
            display: none; /* Скрываем список по умолчанию */
        }

        .suggestion-item {
            padding: 10px;
            cursor: pointer;
        }

        .suggestion-item.highlighted {
            background-color: #007bff;
            color: white;
        }

        .loading-indicator {
            position: absolute;
            top: 50%;
            right: 10px;
            transform: translateY(-50%);
            font-size: 12px;
            color: #555;
        }

        #totalTable {
            width: 100%;
            border-collapse: collapse;
        }
        #totalTable th, #totalTable td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }
        #totalTable th {
            background-color: #f4f4f4;
            font-weight: bold;
        }
        #totalTable .column-description {
            text-align: left;
        }
        #totalTable .column-amount {
            text-align: right;
            width: 90px;
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
            margin: 0;
            z-index: 1000;
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
        @media print {
            .navbar {
                display: none !important;
            }
        }

        .add-to-invoice-btn {
            background-color: #43a047;
            color: white;
            border: none;
            padding: 10px 15px;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            text-align: center;
            margin-top: 10px;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .add-to-invoice-btn:hover {
            background-color: #2e7d32;
        }
        .add-to-invoice-btn .material-icons {
            font-size: 20px;
        }
    </style>
</head>
<body>
    <div class="navbar">
        <a href="/bookings">Kalender</a>
        <a href="/bookings-list">Buchungen</a>
        <a href="/invoice/new" class="menu-btn">Rechnung erstellen</a>
    </div>
    <div class="container">
        <div class="form-container">
            <h1>Buchung bearbeiten</h1>
            <#if method == "edit">
                <form action="/booking/${booking.ID}" method="post" onsubmit="return validateForm()">
            <#elseif method == "create">
                <form action="/booking/new" method="post" onsubmit="return validateForm()">
            </#if>
                <label for="roomId">Zimmer:</label>
                <select id="roomId" name="roomId" required>
                    <#list rooms as room>
                        <#if booking.room??>
                            <option value="${room.ID}" <#if room.ID == booking.room.ID>selected</#if>>${room.name}</option>
                        <#else>
                            <option value="${room.ID}">${room.name}</option>
                        </#if>
                    </#list>
                </select>

                <label for="customerName">Kundenname:</label>
                <input type="text" id="customerName" name="customerName" value="${booking.customerName!""}">

                <!-- Адрес: визуально сгруппировано и компактно -->
                <fieldset style="border: 1px solid #ccc; border-radius: 6px; padding: 15px; margin-bottom: 18px;">
                    <legend style="font-weight: bold; color: #555; font-size: 15px;">Adresse des Kunden</legend>
                    <div style="display: flex; gap: 10px; margin-bottom: 10px;">
                        <div style="flex: 2;">
                            <label for="customerStreet" style="margin-bottom: 3px;">Straße</label>
                            <input type="text" id="customerStreet" name="customerStreet" value="${booking.customerStreet!""}">
                        </div>
                        <div style="flex: 1;">
                            <label for="customerHouseNumber" style="margin-bottom: 3px;">Nr.</label>
                            <input type="text" id="customerHouseNumber" name="customerHouseNumber" value="${booking.customerHouseNumber!""}">
                        </div>
                    </div>
                    <div style="display: flex; gap: 10px;">
                        <div style="flex: 1;">
                            <label for="customerPostalCode" style="margin-bottom: 3px;">PLZ</label>
                            <input type="text" id="customerPostalCode" name="customerPostalCode" value="${booking.customerPostalCode!""}">
                        </div>
                        <div style="flex: 2;">
                            <label for="customerCity" style="margin-bottom: 3px;">Ort</label>
                            <input type="text" id="customerCity" name="customerCity" value="${booking.customerCity!""}">
                        </div>
                        <div style="flex: 2;">
                            <label for="customerCountry" style="margin-bottom: 3px;">Land</label>
                            <input type="text" id="customerCountry" name="customerCountry" value="${booking.customerCountry!""}">
                        </div>
                    </div>
                </fieldset>

                <label for="startDate">Anfangsdatum:</label>
                <input type="date" id="startDate" name="startDate" value="${booking.startDate}" required>

                <label for="endDate">Enddatum:</label>
                <input type="date" id="endDate" name="endDate" value="${booking.endDate}" required>

                <label for="prepayment">Anzahlung:</label>
                <input type="text" id="prepayment" name="prepayment" 
                       value="<#if booking.prepayment??>${booking.prepayment}<#else>0</#if>" 
                       inputmode="decimal" required 
                       style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 5px; box-sizing: border-box;">

                <label for="description">Beschreibung:</label>
                <textarea id="description" name="description">${booking.description}</textarea>

                <div style="display: flex; align-items: center; gap: 10px; line-height: 1; margin-bottom: 20px;">
                    <label for="dogs" style="margin: 0;">Gebühr für den Hund:</label>
                    <input type="checkbox" id="dogs" name="dogs" value="1" 
                           <#if booking.dogs?exists && (booking.dogs?number == 1)>checked</#if> 
                           style="margin: 0;">
                </div>

                <div style="display: flex; align-items: center; gap: 10px; line-height: 1; margin-bottom: 20px;">
                    <label for="includeBreakfast" style="margin: 0;">Frühstück hinzufügen:</label>
                    <input type="checkbox" id="includeBreakfast" name="includeBreakfast" value="1"
                           <#if booking.includeBreakfast?? && booking.includeBreakfast>checked</#if>
                           style="margin: 0;">
                </div>
                
                <h2>Gäste</h2>
                <div class="guest-section">
                    <table id="guestsTable">
                        <thead>
                            <tr>
                                <th style="width: 65%;">Name</th>
                                <th style="width: 30%;">Geburtsdatum</th>
                                <th style="width: 5%;"></th>
                            </tr>
                        </thead>
                        <tbody>
                            <#if booking.guests??>
                                <#list booking.guests as guest>
                                    <tr>
                                        <td>
                                            <input type="text" name="guests[${guest_index}].name" value="${guest.name}" oninput="searchGuests(this, ${guest_index})" onchange="onGuestSelected(this, ${guest_index})" list="guestSuggestions-${guest_index}" required>
                                            <datalist id="guestSuggestions-${guest_index}"></datalist>
                                        </td>
                                        <td>
                                            <input type="date" name="guests[${guest_index}].dateOfBirth" value="${guest.dateOfBirth}" required>
                                        </td>
                                        <td class="actions">
                                            <button type="button" class="icon-btn" onclick="removeGuestRow(this)">
                                                <span class="material-icons">delete</span>
                                            </button>
                                        </td>
                                    </tr>
                                </#list>
                            </#if>
                        </tbody>
                    </table>
                    <button type="button" class="add-guest-btn" onclick="addGuestRow()">
                        <span class="material-icons">person_add</span> Gast hinzufügen
                    </button>
                </div>

                <h2>Bestellungen im Zimmer</h2>
                <div class="guest-section">
                    <table id="roomOrdersTable">
                        <thead>
                            <tr>
                                <th style="width: 65%;">Bezeichnung</th>
                                <th style="width: 30%;">Preis&nbsp;(&euro;)</th>
                                <th style="width: 5%;"></th>
                            </tr>
                        </thead>
                        <tbody>
                            <#if booking.roomOrders?? && (booking.roomOrders?size > 0)>
                                <#list booking.roomOrders as order>
                                    <tr>
                                        <td>
                                            <input type="text" name="roomOrders[${order_index}].bezeichnung" value="${order.bezeichnung}" required style="width: 100%;">
                                        </td>
                                        <td>
                                            <input type="number" step="0.01" name="roomOrders[${order_index}].preis" value="${order.preis}" required
                                                style="width: 100%; text-align: right; font-size: 15px; padding: 8px; border-radius: 5px; border: 1px solid #ccc;">
                                        </td>
                                        <td>
                                            <button type="button" class="icon-btn" onclick="removeRoomOrderRow(this)">
                                                <span class="material-icons">delete</span>
                                            </button>
                                        </td>
                                    </tr>
                                </#list>
                            </#if>
                        </tbody>
                    </table>
                    <button type="button" class="add-guest-btn" onclick="addRoomOrderRow()">
                        <span class="material-icons">add</span> Bestellung hinzufügen
                    </button>
                </div>

                <div class="form-footer">
                    <button type="submit">Speichern</button>
                    <a href="/bookings">Abbrechen</a>
                </div>
            </form>
        </div>
        <div class="bill-container">
            <#if method == "edit">
                <button class="print-bill-btn" onclick="saveAndPrint('${booking.ID}')">
                    <span class="material-icons">print</span> Rechnung drucken
                </button>
                <button class="add-to-invoice-btn" onclick="addToInvoice('${booking.ID}')">
                    <span class="material-icons">playlist_add</span> Zur Rechnung hinzufügen
                </button>
            </#if>
        </div>
    </div>
    <script>
        const enableAddressSuggestions = false; // Установите true для включения автоподсказок

        async function searchGuests(input, index) {
            const query = input.value;
            if (query.length > 1) {
                const response = await fetch(`/guests/search?query=` + query);
                const suggestions = await response.json();
                const datalist = document.getElementById(`guestSuggestions-` + index);
                datalist.innerHTML = '';
                suggestions.forEach(guest => {
                    const option = document.createElement('option');
                    option.value = guest.name;
                    option.dataset.dateOfBirth = guest.dateOfBirth; // Сохраняем дату рождения в data-атрибуте
                    datalist.appendChild(option);
                });
            }
        }

        async function onGuestSelected(input, index) {
            const datalist = document.getElementById(`guestSuggestions-` + index);
            const selectedOption = Array.from(datalist.options).find(option => option.value.trim() === input.value.trim());
            const dobInput = document.querySelector(`input[name="guests[` + index + `].dateOfBirth"]`);

            if (selectedOption && selectedOption.dataset.dateOfBirth) {
                const dateOfBirth = selectedOption.dataset.dateOfBirth;
                if (dobInput) {
                    dobInput.value = dateOfBirth; // Устанавливаем дату рождения
                }
            } else {
                // Если дата рождения не найдена, выполняем запрос к серверу
                const response = await fetch(`/guests/search?query=` + encodeURIComponent(input.value));
                const guests = await response.json();
                const guest = guests.find(g => g.name.trim().toLowerCase() === input.value.trim().toLowerCase());
                if (guest && guest.dateOfBirth && dobInput) {
                    dobInput.value = guest.dateOfBirth; // Устанавливаем дату рождения из базы
                } else if (dobInput) {
                    dobInput.value = ''; // Очищаем поле, если дата рождения не найдена
                }
            }
        }

        function addGuestRow() {
            const table = document.getElementById('guestsTable');
            const rowCount = table.rows.length - 1; // Exclude header row
            const row = table.insertRow();

            row.innerHTML = `
                <td>
                    <input type="text" name="guests[` + rowCount + `].name" oninput="searchGuests(this, ` + rowCount + `)" onchange="onGuestSelected(this, ` + rowCount + `)" list="guestSuggestions-` + rowCount + `" required>
                    <datalist id="guestSuggestions-` + rowCount + `"></datalist>
                </td>
                <td>
                    <input type="date" name="guests[` + rowCount + `].dateOfBirth" required>
                </td>
                <td class="actions">
                    <button type="button" class="icon-btn" onclick="removeGuestRow(this)">
                        <span class="material-icons">delete</span>
                    </button>
                </td>
            `;
        }

        function removeGuestRow(button) {
            const row = button.parentElement.parentElement;
            row.parentElement.removeChild(row);
        }

        function validateForm() {
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;
            const guestNames = Array.from(document.querySelectorAll('input[name^="guests["][name$="].name"]')).map(input => input.value);

            if (new Date(startDate) > new Date(endDate)) {
                alert("Start date cannot be later than end date.");
                return false;
            }

            const duplicateGuests = guestNames.filter((name, index, self) => self.indexOf(name) !== index);
            if (duplicateGuests.length > 0) {
                alert("Duplicate guests are not allowed: " + duplicateGuests.join(", "));
                return false;
            }

            return true;
        }

        async function saveAndPrint(bookingId) {
            try {
                const form = document.querySelector('form');
                const formData = new FormData(form);
                const response = await fetch(`/booking/` + bookingId, {
                    method: 'POST',
                    body: formData
                });

                if (response.ok) {
                    window.location.href = `/booking/` + bookingId + `/bill`;
                } else {
                    alert('Fehler beim Speichern der Buchung vor dem Drucken.');
                }
            } catch (error) {
                console.error('Fehler beim SpeSaving и Drucken:', error);
                alert('Ein unerwarteter Fehler ist aufетен.');
            }
        }

        async function addToInvoice(bookingId) {
            try {
                // Отправляем запрос на сервер
                await fetch(`/invoice/add`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ bookingId })
                });

            } catch (error) {
                console.error('Fehler beim Hinzufügen zur Rechnung:', error);
            }
        }

        let selectedSuggestionIndex = -1;

        async function fetchAddressSuggestions(query) {
            if (!enableAddressSuggestions) return; // Отключаем функционал, если настройка выключена

            if (query.length < 2) {
                clearSuggestions();
                return;
            }

            showLoadingIndicator(true);

            try {
                const response = await fetch('/address/parse', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ query })
                });

                if (!response.ok) {
                    console.error('Failed to fetch address suggestions:', response.statusText);
                    clearSuggestions();
                    return;
                }

                const suggestions = await response.json();
                updateSuggestionsList(suggestions);
            } catch (error) {
                console.error('Error fetching address suggestions:', error);
            } finally {
                showLoadingIndicator(false);
            }
        }

        function updateSuggestionsList(suggestions) {
            const suggestionsList = document.getElementById('addressSuggestions');
            suggestionsList.innerHTML = '';

            if (suggestions.length === 0) {
                suggestionsList.style.display = 'none'; // Скрываем список, если нет предложений
                return;
            }

            suggestionsList.style.display = 'block'; // Показываем список, если есть предложения

            suggestions.forEach((suggestion, index) => {
                const li = document.createElement('li');
                li.textContent = suggestion.formattedAddress; // Используем отформатированный адрес
                li.dataset.value = suggestion.formattedAddress;
                li.className = 'suggestion-item';
                li.onclick = () => selectSuggestion(suggestion.formattedAddress);
                li.onmouseenter = () => highlightSuggestion(index);
                suggestionsList.appendChild(li);
            });

            selectedSuggestionIndex = -1;
            if (suggestions.length > 0) {
                highlightSuggestion(0); // Выделяем первый элемент
            }
        }

        function selectSuggestion(value) {
            const input = document.getElementById('customerAddress');
            input.value = value;
            clearSuggestions();
        }

        function highlightSuggestion(index) {
            const suggestionsList = document.getElementById('addressSuggestions');
            const items = suggestionsList.querySelectorAll('.suggestion-item');
            items.forEach((item, i) => {
                item.classList.toggle('highlighted', i === index);
            });
            selectedSuggestionIndex = index;
        }

        function clearSuggestions() {
            const suggestionsList = document.getElementById('addressSuggestions');
            suggestionsList.innerHTML = '';
            suggestionsList.style.display = 'none'; // Скрываем список при очистке
            selectedSuggestionIndex = -1;
        }

        function showLoadingIndicator(show) {
            const loadingIndicator = document.getElementById('loadingIndicator');
            loadingIndicator.style.display = show ? 'block' : 'none';
        }
        
        //RoomOrders

        function addRoomOrderRow() {
            const table = document.getElementById('roomOrdersTable').getElementsByTagName('tbody')[0];
            const rowCount = table.rows.length;
            const row = table.insertRow();
            row.innerHTML = `
                <td>
                    <input type="text" name="roomOrders[` + rowCount + `].bezeichnung" required style="width: 100%;">
                </td>
                <td>
                    <input type="number" step="0.01" name="roomOrders[` + rowCount + `].preis" required
                        <input type="text" name="roomOrders[` + rowCount + `].bezeichnung" required style="width: 100%;">
                </td>
                <td>
                    <button type="button" class="icon-btn" onclick="removeRoomOrderRow(this)">
                        <span class="material-icons">delete</span>
                    </button>
                </td>
            `;
        }

        function removeRoomOrderRow(button) {
            const row = button.parentElement.parentElement;
            row.parentElement.removeChild(row);
        }

        //RoomOrders/    

    //    document.addEventListener('DOMContentLoaded', async () => {
    //        await calculateBill(); // Пересчитываем прайс при загрузке страницы
    //    });

    //    document.addEventListener('DOMContentLoaded', calculateBill); // Пересчет прайса при открытии страницы

        document.addEventListener('DOMContentLoaded', () => {

            document.getElementById('prepayment').addEventListener('change', calculateBill); // Вызываем calculateBill при изменении предоплаты

            calculateBill();

        });    

    </script>

</body>
</html>
