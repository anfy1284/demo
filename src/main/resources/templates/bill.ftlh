<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Bill</title>
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
        }
        h1, h2 {
            text-align: center;
            color: #333;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
        }
        table th, table td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }
        table th {
            background-color: #f4f4f4;
        }
        .total-row td {
            font-weight: bold;
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
            .print-btn {
                display: none;
            }
        }
    </style>
</head>
<body>
    <div class="bill-container">
        <h1>Booking Bill</h1>
        <h2>Booking Details</h2>
        <p><strong>Customer Name:</strong> ${booking.customerName}</p>
        <p><strong>Room:</strong> ${booking.room.name}</p>
        <p><strong>Start Date:</strong> ${booking.startDate}</p>
        <p><strong>End Date:</strong> ${booking.endDate}</p>
        <p><strong>Guests:</strong></p>
        <ul>
            <#list booking.guests as guest>
                <li>${guest.name} (${guest.dateOfBirth})</li>
            </#list>
        </ul>
        <h2>Bill Summary</h2>
        <table>
            <thead>
                <tr>
                    <th>Description</th>
                    <th>Amount</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Accommodation Price</td>
                    <td>${bill.accommodationPrice} €</td>
                </tr>
                <tr class="total-row">
                    <td>Total</td>
                    <td>${bill.totalPrice} €</td>
                </tr>
            </tbody>
        </table>
        <button class="print-btn" onclick="window.print()">Print Bill</button>
    </div>
</body>
</html>
