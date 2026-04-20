package com.trinova.scms.model;

import java.time.LocalDateTime;

public class Booking {
    private int bookingId;
    private int memberId;
    private int roomId;
    private String memberName;
    private String roomName;
    private String roomType;
    private String bookingType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double durationHours;
    private double baseCost;
    private double facilityCost;
    private double vatAmount;
    private double totalCost;
    private String status;
    private String cancelReason;

    public Booking() {}

    public Booking(int memberId, int roomId, String bookingType,
                   LocalDateTime startTime, LocalDateTime endTime) {
        this.memberId    = memberId;
        this.roomId      = roomId;
        this.bookingType = bookingType;
        this.startTime   = startTime;
        this.endTime     = endTime;
        this.status      = "PENDING";
    }

    // Getters
    public int getBookingId()           { return bookingId; }
    public int getMemberId()            { return memberId; }
    public int getRoomId()              { return roomId; }
    public String getMemberName()       { return memberName; }
    public String getRoomName()         { return roomName; }
    public String getRoomType()         { return roomType; }
    public String getBookingType()      { return bookingType; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime()   { return endTime; }
    public double getDurationHours()    { return durationHours; }
    public double getBaseCost()         { return baseCost; }
    public double getFacilityCost()     { return facilityCost; }
    public double getVatAmount()        { return vatAmount; }
    public double getTotalCost()        { return totalCost; }
    public String getStatus()           { return status; }
    public String getCancelReason()     { return cancelReason; }

    // Setters
    public void setBookingId(int v)            { this.bookingId = v; }
    public void setMemberId(int v)             { this.memberId = v; }
    public void setRoomId(int v)               { this.roomId = v; }
    public void setMemberName(String v)        { this.memberName = v; }
    public void setRoomName(String v)          { this.roomName = v; }
    public void setRoomType(String v)          { this.roomType = v; }
    public void setBookingType(String v)       { this.bookingType = v; }
    public void setStartTime(LocalDateTime v)  { this.startTime = v; }
    public void setEndTime(LocalDateTime v)    { this.endTime = v; }
    public void setDurationHours(double v)     { this.durationHours = v; }
    public void setBaseCost(double v)          { this.baseCost = v; }
    public void setFacilityCost(double v)      { this.facilityCost = v; }
    public void setVatAmount(double v)         { this.vatAmount = v; }
    public void setTotalCost(double v)         { this.totalCost = v; }
    public void setStatus(String v)            { this.status = v; }
    public void setCancelReason(String v)      { this.cancelReason = v; }

    @Override
    public String toString() {
        return "Booking{id=" + bookingId +
               ", room=" + roomName +
               ", type=" + bookingType +
               ", status=" + status +
               ", total=PKR " + totalCost + "}";
    }
}