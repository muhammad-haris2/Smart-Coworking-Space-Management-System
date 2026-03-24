package com.trinova.scms.model;

import java.time.LocalDateTime;

public class Booking {
    private int bookingId;
    private int memberId;
    private int roomId;
    private String memberName;
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String cancelReason;

    public Booking() {}

    public Booking(int memberId, int roomId,
                   LocalDateTime startTime, LocalDateTime endTime) {
        this.memberId  = memberId;
        this.roomId    = roomId;
        this.startTime = startTime;
        this.endTime   = endTime;
        this.status    = "ACTIVE";
    }

    public int getBookingId()          { return bookingId; }
    public int getMemberId()           { return memberId; }
    public int getRoomId()             { return roomId; }
    public String getMemberName()      { return memberName; }
    public String getRoomName()        { return roomName; }
    public LocalDateTime getStartTime(){ return startTime; }
    public LocalDateTime getEndTime()  { return endTime; }
    public String getStatus()          { return status; }
    public String getCancelReason()    { return cancelReason; }

    public void setBookingId(int bookingId)         { this.bookingId = bookingId; }
    public void setMemberId(int memberId)           { this.memberId = memberId; }
    public void setRoomId(int roomId)               { this.roomId = roomId; }
    public void setMemberName(String memberName)    { this.memberName = memberName; }
    public void setRoomName(String roomName)        { this.roomName = roomName; }
    public void setStartTime(LocalDateTime startTime){ this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime)   { this.endTime = endTime; }
    public void setStatus(String status)            { this.status = status; }
    public void setCancelReason(String cancelReason){ this.cancelReason = cancelReason; }

    @Override
    public String toString() {
        return "Booking{id=" + bookingId + ", room=" + roomName +
               ", status=" + status + "}";
    }
}