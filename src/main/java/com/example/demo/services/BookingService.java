package com.example.demo.services;

import org.springframework.stereotype.Service;

import com.example.demo.classes.Booking;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.classes.Guest; // Import the Guest class
import com.example.demo.services.RoomService; // Import the RoomService class
import com.example.demo.classes.RoomOrder;
@Service
public class BookingService extends BaseService<Booking> {

    private final RoomService roomService;
    private final GuestService guestService;

    public BookingService(RoomService roomService, GuestService guestService) {
        this.roomService = roomService;
        this.guestService = guestService;
    }
private static final String BOOKINGS_FILE = "bookings.dat";

    @Override
    protected String getId(Booking booking) {
        return booking.getID();
    }

    @Override
    protected void setId(Booking booking, String id) {
        booking.setID(id);
    }

    public void saveAllToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BOOKINGS_FILE))) {
            List<SerializableBooking> serializableBookings = new ArrayList<>();
            for (Booking booking : items) {
                serializableBookings.add(SerializableBooking.fromBooking(booking));
            }
            oos.writeObject(serializableBookings);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save bookings to file: " + e.getMessage(), e);
        }
    }

    public void loadAllFromFile() {
        File file = new File(BOOKINGS_FILE);
        if (!file.exists()) {
            System.out.println("Bookings file not found. Starting with an empty list.");
            return;
        }
        if (file.length() == 0) {
            System.out.println("Bookings file is empty. Starting with an empty list.");
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<SerializableBooking> serializableBookings = (List<SerializableBooking>) ois.readObject();
            for (SerializableBooking serializableBooking : serializableBookings) {
                Booking booking = serializableBooking.toBooking();

                // Восстанавливаем связанные объекты
                booking.setRoom(roomService.getById(serializableBooking.roomId));
                if (serializableBooking.guestIds != null) {
                    List<Guest> guests = new ArrayList<>();
                    for (String guestId : serializableBooking.guestIds) {
                        Guest guest = guestService.getById(guestId);
                        if (guest != null) {
                            guests.add(guest);
                        }
                    }
                    booking.setGuests(guests);
                }

                add(booking, false); // Добавляем в память без сохранения в файл
            }
        } catch (FileNotFoundException e) {
            System.out.println("Bookings file not found. Starting with an empty list.");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load bookings from file: " + e.getMessage(), e);
        }
    }

    private static class SerializableBooking implements Serializable {
        private String id;
        private String roomId;
        private String customerName;
        private String startDate;
        private String endDate;
        private String status;
        private String paymentStatus;
        private String paymentMethod;
        private String specialRequests;
        private List<String> guestIds;
        private String description;
        private int duration;
        private int dogs;
        private boolean includeBreakfast;
        private String customerAddress;
        private double prepayment;
        private List<RoomOrder> roomOrders;

        public static SerializableBooking fromBooking(Booking booking) {
            SerializableBooking sb = new SerializableBooking();
            sb.id = booking.getID();
            sb.roomId = booking.getRoomId();
            sb.customerName = booking.getCustomerName();
            sb.startDate = booking.getStartDate();
            sb.endDate = booking.getEndDate();
            sb.status = booking.getStatus();
            sb.paymentStatus = booking.getPaymentStatus();
            sb.paymentMethod = booking.getPaymentMethod();
            sb.specialRequests = booking.getSpecialRequests();
            sb.guestIds = booking.getGuests() != null ? booking.getGuests().stream().map(Guest::getID).toList() : null;
            sb.description = booking.getDescription();
            sb.duration = booking.getDuration();
            sb.dogs = booking.getDogs();
            sb.includeBreakfast = booking.isIncludeBreakfast();
            sb.customerAddress = booking.getCustomerAddress();
            sb.prepayment = booking.getPrepayment();
            sb.roomOrders = booking.getRoomOrders();
            return sb;
        }

        public Booking toBooking() {
            Booking booking = new Booking();
            booking.setID(this.id);
            // Room and Guest objects should be resolved by their IDs in the application logic
            booking.setCustomerName(this.customerName);
            booking.setStartDate(this.startDate);
            booking.setEndDate(this.endDate);
            booking.setStatus(this.status);
            booking.setPaymentStatus(this.paymentStatus);
            booking.setPaymentMethod(this.paymentMethod);
            booking.setSpecialRequests(this.specialRequests);
            booking.setDescription(this.description);
            booking.setDuration(this.duration);
            booking.setDogs(this.dogs);
            booking.setIncludeBreakfast(this.includeBreakfast);
            booking.setCustomerAddress(this.customerAddress);
            booking.setPrepayment(this.prepayment);
            booking.setGuests(new ArrayList<>()); // Guests should be resolved by their IDs
            booking.setRoomOrders(this.roomOrders != null ? this.roomOrders : new ArrayList<>());
            return booking;
        }
    }

    @Override
    public void add(Booking booking) {
        add(booking, true); // По умолчанию сохраняем в файл
    }

    @Override
    protected void onItemAdded(Booking booking) {
        saveAllToFile(); // Сохраняем в файл только если это явно указано
    }
}
