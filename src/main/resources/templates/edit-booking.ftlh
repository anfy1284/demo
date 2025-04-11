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

                datalist.appendChild(option);
            });
        }
    }

    function addGuestRow() {
        const table = document.getElementById('guestsTable');
        const rowCount = table.rows.length - 1; // Exclude header row
        const row = table.insertRow();

        row.innerHTML = `
            <td>
                <input type="text" name="guests[` + rowCount + `].name" oninput="searchGuests(this, ` + rowCount + `)" list="guestSuggestions-` + rowCount + `" required>
                <datalist id="guestSuggestions-` + rowCount + `"></datalist>
            </td>
            <td>
                <input type="date" name="guests[` + rowCount + `].dateOfBirth" required>
            </td>
            <td class="actions">
                <button type="button" onclick="removeGuestRow(this)">Remove</button>
            </td>
        `;
        calculateBill(); // Recalculate bill when a guest is added
    }

    function removeGuestRow(button) {
        const row = button.parentElement.parentElement;
        row.parentElement.removeChild(row);
        calculateBill(); // Recalculate bill when a guest is removed
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

    document.addEventListener('input', calculateBill); // Recalculate bill on any input change
</script>

<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Edit Booking</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            line-height: 1.6;
        }
        h1, h2 {
            color: #333;
        }
        form {
            max-width: 800px;
            margin: 0 auto;
            background: #f9f9f9;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
        }
        input[type="text"], input[type="date"], textarea, select {
            width: 100%;
            padding: 8px;
            margin-bottom: 15px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
        }
        textarea {
            resize: vertical;
        }
        button {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 10px 15px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
        }
        button:hover {
            background-color: #0056b3;
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
        .actions button {
            background-color: #dc3545;
            margin-left: 5px;
        }
        .actions button:hover {
            background-color: #a71d2a;
        }
        .add-guest-btn {
            margin-top: 10px;
            background-color: #28a745;
        }
        .add-guest-btn:hover {
            background-color: #218838;
        }
        .form-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .form-footer a {
            color: #007bff;
            text-decoration: none;
        }
        .form-footer a:hover {
            text-decoration: underline;
        }
        .container {
            display: flex;
            justify-content: space-between;
        }
        .form-container {
            flex: 0 0 65%;
        }
        .bill-container {
            flex: 0 0 30%;
            background: #f9f9f9;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        .bill-container h2 {
            margin-top: 0;
        }
        .bill-container table {
            width: 100%;
            border-collapse: collapse;
        }
        .bill-container table th, .bill-container table td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }
        .bill-container table th {
            background-color: #f4f4f4;
        }
        .bill-container table td {
            text-align: right;
        }
    </style>
</head>
<body>
    <h1>Edit Booking</h1>
    <div class="container">
        <div class="form-container">
            <#if method == "edit">
                <form action="/booking/${booking.ID}" method="post">
            <#elseif method == "create">
                <form action="/booking/new" method="post">
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
                <input type="date" id="startDate" name="startDate" value="${booking.startDate}" required>

                <label for="endDate">End Date:</label>
                <input type="date" id="endDate" name="endDate" value="${booking.endDate}" required>

                <label for="description">Description:</label>
                <textarea id="description" name="description" required>${booking.description}</textarea>

                <h2>Guests</h2>
                <table id="guestsTable">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Date of Birth</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <#if booking.guests??>
                            <#list booking.guests as guest>
                                <tr>
                                    <td>
                                        <input type="text" name="guests[${guest_index}].name" value="${guest.name}" oninput="searchGuests(this, ${guest_index})" list="guestSuggestions-${guest_index}" required>
                                        <datalist id="guestSuggestions-${guest_index}"></datalist>
                                    </td>
                                    <td>
                                        <input type="date" name="guests[${guest_index}].dateOfBirth" value="${guest.dateOfBirth}" required>
                                    </td>
                                    <td class="actions">
                                        <button type="button" onclick="removeGuestRow(this)">Remove</button>
                                    </td>
                                </tr>
                            </#list>
                        </#if>
                    </tbody>
                </table>
                <button type="button" class="add-guest-btn" onclick="addGuestRow()">Add Guest</button>

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
        </div>
    </div>
</body>
</html>
