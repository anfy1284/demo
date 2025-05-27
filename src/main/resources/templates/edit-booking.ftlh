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

        // Убедимся, что событие `change` срабатывает для обновления интерфейса
        if (dobInput) {
            const event = new Event('change', { bubbles: true });
            dobInput.dispatchEvent(event);
        }

        calculateBill(); // Пересчитываем прайс при выборе гостя
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
                <input type="date" name="guests[` + rowCount + `].dateOfBirth" onchange="calculateBill()" required>
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
        calculateBill(); // Пересчитываем прайс при удалении гостя
    }

    async function calculateBill() {
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;
        const roomId = document.getElementById('roomId').value;
        const guestDatesOfBirth = Array.from(document.querySelectorAll('input[name^="guests["][name$="].dateOfBirth"]'))
            .map(input => input.value)
            .filter(date => date); // Remove empty values
        const dogs = document.getElementById('dogs').checked ? 1 : 0;
        const includeBreakfast = document.getElementById('includeBreakfast').checked;
        const prepayment = document.getElementById('prepayment').value || 0;

        // Собираем Bestellungen im Zimmer из таблицы
        const roomOrders = Array.from(document.querySelectorAll('#roomOrdersTable tbody tr')).map(row => {
            const bezeichnungInput = row.querySelector('input[name^="roomOrders"][name$=".bezeichnung"]');
            const preisInput = row.querySelector('input[name^="roomOrders"][name$=".preis"]');
            return {
                bezeichnung: bezeichnungInput ? bezeichnungInput.value : '',
                preis: preisInput && preisInput.value ? parseFloat(preisInput.value) : 0
            };
        }).filter(order => order.bezeichnung && !isNaN(order.preis));

        // Формируем roomOrdersParam для передачи на сервер
        // Используем encodeURIComponent только для bezeichnung, а не всей строки
        const roomOrdersParam = roomOrders.map(order =>
            'roomOrders=' + encodeURIComponent(order.bezeichnung) + ':' + order.preis
        );

        if (startDate && endDate && roomId) {
            try {
                let url = `/calculate-bill?roomId=` + roomId +
                    `&startDate=` + startDate +
                    `&endDate=` + endDate +
                    `&guests=` + encodeURIComponent(guestDatesOfBirth.join(',')) +
                    `&dogs=` + dogs +
                    `&includeBreakfast=` + includeBreakfast +
                    `&prepayment=` + prepayment;

                // Если нет bookingId (создание новой брони), добавляем roomOrders
                if (!window.bookingId) {
                    url += '&' + roomOrdersParam.join('&');
                }

                const response = await fetch(url);
                const billData = await response.json();

                // Update the main bill table
                const billTableBody = document.querySelector('#billTable tbody');
                billTableBody.innerHTML = '';
                if (billData.main) {
                    billData.main.forEach((item, index) => {
                        const row = document.createElement('tr');
                        const labelCell = document.createElement(index === billData.main.length - 1 ? 'th' : 'td');
                        const valueCell = document.createElement(index === billData.main.length - 1 ? 'th' : 'td');

                        labelCell.textContent = item.label;
                        valueCell.textContent = item.value;

                        labelCell.style.textAlign = 'left';
                        valueCell.style.textAlign = 'right';

                        row.appendChild(labelCell);
                        row.appendChild(valueCell);
                        billTableBody.appendChild(row);
                    });
                }

                // Update the Kurbeitrag table
                const kurbeitragTableBody = document.querySelector('.bill-container table:nth-of-type(2) tbody');
                kurbeitragTableBody.innerHTML = '';
                if (billData.kurbeitrag) {
                    billData.kurbeitrag.forEach((item, index) => {
                        const row = document.createElement('tr');
                        const labelCell = document.createElement(index === billData.kurbeitrag.length - 1 ? 'th' : 'td');
                        const valueCell = document.createElement(index === billData.kurbeitrag.length - 1 ? 'th' : 'td');

                        labelCell.textContent = item.label;
                        valueCell.textContent = item.value;

                        labelCell.style.textAlign = 'left';
                        valueCell.style.textAlign = 'right';

                        row.appendChild(labelCell);
                        row.appendChild(valueCell);
                        kurbeitragTableBody.appendChild(row);
                    });
                }

                // Update the Bestellungen im Zimmer table
                const roomOrdersTableBody = document.querySelector('#roomOrdersBillTable tbody');
                roomOrdersTableBody.innerHTML = '';
                let totalRoomOrders = 0;
                roomOrders.forEach(order => {
                    const row = document.createElement('tr');
                    const labelCell = document.createElement('td');
                    const valueCell = document.createElement('td');
                    labelCell.textContent = order.bezeichnung;
                    valueCell.textContent = order.preis.toFixed(2) + ' €';
                    labelCell.className = 'column-description';
                    valueCell.className = 'column-amount';
                    row.appendChild(labelCell);
                    row.appendChild(valueCell);
                    roomOrdersTableBody.appendChild(row);
                    totalRoomOrders += order.preis;
                });
                // Итоговая строка
                if (roomOrders.length > 0) {
                    const totalRow = document.createElement('tr');
                    const totalLabel = document.createElement('td');
                    const totalValue = document.createElement('td');
                    totalLabel.textContent = 'Gesamt';
                    totalValue.textContent = totalRoomOrders.toFixed(2) + ' €';
                    totalLabel.style.fontWeight = 'bold';
                    totalValue.style.fontWeight = 'bold';
                    totalLabel.className = 'column-description';
                    totalValue.className = 'column-amount';
                    totalRow.appendChild(totalLabel);
                    totalRow.appendChild(totalValue);
                    roomOrdersTableBody.appendChild(totalRow);
                }

                // Update the Gesamt table
                const totalTableBody = document.querySelector('#totalTable tbody');
                totalTableBody.innerHTML = '';
                if (billData.gesamt) {
                    billData.gesamt.forEach(item => {
                        const row = document.createElement('tr');
                        const labelCell = document.createElement('td');
                        const valueCell = document.createElement('td');

                        labelCell.textContent = item.label;
                        valueCell.textContent = item.value;

                        labelCell.className = 'column-description';
                        valueCell.className = 'column-amount';

                        row.appendChild(labelCell);
                        row.appendChild(valueCell);
                        totalTableBody.appendChild(row);
                    });
                }
            } catch (error) {
                console.error('Error calculating bill:', error);
                alert('Ein Fehler ist beim Berechnen der Rechnung aufgetreten.');
            }
        }
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
            console.error('Fehler beim Speichern und Drucken:', error);
            alert('Ein unerwarteter Fehler ist aufетен.');
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
    </style>
</head>
<body>
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
                <input type="date" id="startDate" name="startDate" value="${booking.startDate}" onchange="calculateBill()" required>

                <label for="endDate">Enddatum:</label>
                <input type="date" id="endDate" name="endDate" value="${booking.endDate}" onchange="calculateBill()" required>

                <label for="prepayment">Anzahlung:</label>
                <input type="text" id="prepayment" name="prepayment" 
                       value="<#if booking.prepayment??>${booking.prepayment}<#else>0</#if>" 
                       inputmode="decimal" onchange="calculateBill()" required 
                       style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 5px; box-sizing: border-box;">

                <label for="description">Beschreibung:</label>
                <textarea id="description" name="description">${booking.description}</textarea>

                <div style="display: flex; align-items: center; gap: 10px; line-height: 1; margin-bottom: 20px;">
                    <label for="dogs" style="margin: 0;">Gebühr für den Hund:</label>
                    <input type="checkbox" id="dogs" name="dogs" value="1" 
                           <#if booking.dogs?exists && (booking.dogs?number == 1)>checked</#if> 
                           onchange="calculateBill()" style="margin: 0;">
                </div>

                <div style="display: flex; align-items: center; gap: 10px; line-height: 1; margin-bottom: 20px;">
                    <label for="includeBreakfast" style="margin: 0;">Frühstück hinzufügen:</label>
                    <input type="checkbox" id="includeBreakfast" name="includeBreakfast" value="1"
                           <#if booking.includeBreakfast?? && booking.includeBreakfast>checked</#if>
                           onchange="calculateBill()" style="margin: 0;">
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
                                            <input type="date" name="guests[${guest_index}].dateOfBirth" value="${guest.dateOfBirth}" onchange="calculateBill()" required>
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
            <table id="billTable">
                <thead>
                    <tr>
                        <th colspan="2" class="column-description">Rechnung</th>
                    </tr>
                </thead>
                <tbody>
                    <!-- Сюда будут добавляться строки счета -->
                </tbody>
            </table>

            <table>
                <thead>
                    <tr>
                        <th colspan="2" class="column-description">Kurabgabe</th>
                    </tr>
                </thead>
                <tbody>
                    <#if bill?has_content && bill.kurbeitrag?has_content>
                        <#list bill.kurbeitrag as item>
                            <tr>
                                <td>${item.label}</td>
                                <td>${item.value}</td>
                            </tr>
                        </#list>
                    </#if>
                </tbody>
            </table>

            <!-- Таблица Bestellungen im Zimmer -->
            <table id="roomOrdersBillTable">
                <thead>
                    <tr>
                        <th colspan="2" class="column-description">Bestellungen im Zimmer</th>
                    </tr>
                </thead>
                <tbody>
                    <!-- Сюда будут добавляться строки заказов -->
                </tbody>
            </table>

            <table id="totalTable">
                <thead>
                    <tr>
                        <th colspan="2" class="column-description">Betrag</th>
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
            <#if method == "edit">
                <button class="print-bill-btn" onclick="saveAndPrint('${booking.ID}')">
                    <span class="material-icons">print</span> Rechnung drucken
                </button>
            </#if>
        </div>
    </div>

</body>
</html>
