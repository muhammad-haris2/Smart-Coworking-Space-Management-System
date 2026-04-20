package com.trinova.scms.model;

public class Room {
    private int roomId;
    private String roomName;
    private String roomType;
    private int capacity;
    private String amenities;
    private String privateSize;
    private double monthlyPrice;
    private double dailyPrice;
    private double hourlyPrice;
    private boolean isActive;

    public Room() {}

    // Getters
    public int getRoomId()          { return roomId; }
    public String getRoomName()     { return roomName; }
    public String getRoomType()     { return roomType; }
    public int getCapacity()        { return capacity; }
    public String getAmenities()    { return amenities; }
    public String getPrivateSize()  { return privateSize; }
    public double getMonthlyPrice() { return monthlyPrice; }
    public double getDailyPrice()   { return dailyPrice; }
    public double getHourlyPrice()  { return hourlyPrice; }
    public boolean isActive()       { return isActive; }

    // Setters
    public void setRoomId(int v)           { this.roomId = v; }
    public void setRoomName(String v)      { this.roomName = v; }
    public void setRoomType(String v)      { this.roomType = v; }
    public void setCapacity(int v)         { this.capacity = v; }
    public void setAmenities(String v)     { this.amenities = v; }
    public void setPrivateSize(String v)   { this.privateSize = v; }
    public void setMonthlyPrice(double v)  { this.monthlyPrice = v; }
    public void setDailyPrice(double v)    { this.dailyPrice = v; }
    public void setHourlyPrice(double v)   { this.hourlyPrice = v; }
    public void setActive(boolean v)       { this.isActive = v; }

    public boolean isHotDesk() {
        return "HOT_DESK".equals(roomType);
    }

    public boolean isMeetingRoom() {
        return "MEETING_ROOM".equals(roomType);
    }

    public boolean isPrivateRoom() {
        return "PRIVATE_ROOM".equals(roomType);
    }

    @Override
    public String toString() { return roomName; }
}