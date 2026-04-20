package com.trinova.scms.service;

import com.trinova.scms.dao.BookingDAO;
import com.trinova.scms.dao.RoomDAO;
import com.trinova.scms.model.Booking;
import com.trinova.scms.model.Room;
import com.trinova.scms.util.EmailSimulator;

import java.time.LocalDateTime;
import java.util.List;

public class BookingService {

    private final BookingDAO     bookingDAO;
    private final RoomDAO        roomDAO;
    private final BillingService billingService;

    public BookingService() throws Exception {
        this.bookingDAO     = new BookingDAO();
        this.roomDAO        = new RoomDAO();
        this.billingService = new BillingService();
    }

    public int book(int memberId, String memberEmail,
                    int roomId, String bookingType,
                    LocalDateTime start,
                    LocalDateTime end,
                    double baseCost,
                    double facilityCost,
                    double vatAmount,
                    double totalCost) throws Exception {

        if (!start.isBefore(end))
            throw new Exception("Start time must be before end time.");

        Room room = roomDAO.findById(roomId);
        if (room == null || !room.isActive())
            throw new Exception("Selected space is not available.");

        if (!room.isPrivateRoom() &&
            start.toLocalDate().equals(end.toLocalDate()) == false)
            throw new Exception(
                "Hourly bookings must start and end on the same day.");

        boolean conflict = bookingDAO.hasConflict(roomId, start, end);
        if (conflict)
            throw new Exception(
                "This space is already booked for the selected time. " +
                "Please choose a different slot.");

        Booking b = new Booking(memberId, roomId, bookingType, start, end);
        b.setBaseCost(baseCost);
        b.setFacilityCost(facilityCost);
        b.setVatAmount(vatAmount);
        b.setTotalCost(totalCost);
        b.setStatus("CONFIRMED");

        int bookingId = bookingDAO.create(b);
        if (bookingId == -1)
            throw new Exception("Booking failed. Please try again.");

        billingService.generateInvoice(
            bookingId, memberId,
            baseCost, facilityCost, vatAmount, totalCost);

        EmailSimulator.sendBookingConfirmation(memberEmail, bookingId);
        return bookingId;
    }

    public void cancel(int bookingId, String memberEmail,
                       String reason) throws Exception {
        if (reason == null || reason.trim().isEmpty())
            throw new Exception(
                "Please provide a reason for cancellation.");
        bookingDAO.cancel(bookingId, reason.trim());
        EmailSimulator.sendCancellationNotice(memberEmail, bookingId);
    }

    public List<Booking> getMemberBookings(int memberId) throws Exception {
        return bookingDAO.getBookingsByMember(memberId);
    }

    public List<Booking> getAllBookings() throws Exception {
        return bookingDAO.getAllBookings();
    }

    public List<Room> getAllRooms() throws Exception {
        return roomDAO.getAllRooms();
    }

    public List<Room> getRoomsByType(String type) throws Exception {
        return roomDAO.getRoomsByType(type);
    }
}