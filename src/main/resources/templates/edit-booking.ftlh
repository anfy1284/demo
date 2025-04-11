<script>
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

    function onGuestSelected(input, index) {
        const datalist = document.getElementById(`guestSuggestions-` + index);
        const selectedOption = Array.from(datalist.options).find(option => option.value === input.value);
        if (selectedOption) {
            const dateOfBirth = selectedOption.dataset.dateOfBirth;
            const dobInput = document.querySelector(`input[name="guests[` + index + `].dateOfBirth"]`);
            if (dobInput) {
                dobInput.value = dateOfBirth; // Устанавливаем дату рождения
                calculateBill(); // Пересчитываем прайс при выборе гостя
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
        const guestCount = document.querySelectorAll('#guestsTable tbody tr').length;

        if (startDate && endDate && roomId) {
            const response = await fetch(`/calculate-bill?roomId=` + roomId + `&startDate=` + startDate + `&endDate=` + endDate + `&guestCount=` + guestCount);
            const billData = await response.json();

            const billTable = document.getElementById('billTable');
            billTable.innerHTML = `
                <tr>
                    <td>Accommodation price (` + guestCount + ` guests)</td>
                    <td>` + billData.accommodationPrice + ` €</td>
                </tr>
                <tr>
                    <td><strong>Total</strong></td>
                    <td><strong>` + billData.totalPrice + ` €</strong></td>
                </tr>
            `;
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

    document.addEventListener('DOMContentLoaded', calculateBill); // Пересчет прайса при открытии страницы
</script>

<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Edit Booking</title>
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
        input[type="text"], input[type="date"], textarea, select {
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
        .add-guest-btn {
            margin-top: 10px;
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
        .guest-section {
            display: flex;
            flex-direction: column;
            gap: 10px;
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
    </style>
</head>
<body>
    <div class="container">
        <div class="form-container">
            <h1>Edit Booking</h1>
            <#if method == "edit">
                <form action="/booking/${booking.ID}" method="post" onsubmit="return validateForm()">
            <#elseif method == "create">
                <form action="/booking/new" method="post" onsubmit="return validateForm()">
            </#if>
                <label for="roomId">Room:</label>
                <select id="roomId" name="roomId" required>
                    <#list rooms as room>
                        <#if booking.room??>
                            <option value="${room.ID}" <#if room.ID == booking.room.ID>selected</#if>>${room.name}</option>
                        <#else>
                            <option value="${room.ID}">${room.name}</option>
                        </#if>
                    </#list>
                </select>

                <label for="customerName">Customer Name:</label>
                <input type="text" id="customerName" name="customerName" value="${booking.customerName}" required>

                <label for="startDate">Start Date:</label>
                <input type="date" id="startDate" name="startDate" value="${booking.startDate}" onchange="calculateBill()" required>

                <label for="endDate">End Date:</label>
                <input type="date" id="endDate" name="endDate" value="${booking.endDate}" onchange="calculateBill()" required>

                <label for="description">Description:</label>
                <textarea id="description" name="description">${booking.description}</textarea>

                <h2>Guests</h2>
                <div class="guest-section">
                    <table id="guestsTable">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Date of Birth</th>
                                <th></th>
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
                        <span class="material-icons">person_add</span> Add Guest
                    </button>
                </div>

                <div class="form-footer">
                    <button type="submit">Save</button>
                    <a href="/bookings">Cancel</a>
                </div>
            </form>
        </div>
        <div class="bill-container">
            <h2>Bill</h2>
            <table id="billTable">
                <tr>
                    <td>Accommodation price</td>
                    <td>0 €</td>
                </tr>
                <tr>
                    <td><strong>Total</strong></td>
                    <td><strong>0 €</strong></td>
                </tr>
            </table>
            <#if method == "edit">
                <button class="print-bill-btn" onclick="window.location.href='/booking/${booking.ID}/bill'">
                    <span class="material-icons">print</span> Print Bill
                </button>
            </#if>
        </div>
    </div>
</body>
</html>
