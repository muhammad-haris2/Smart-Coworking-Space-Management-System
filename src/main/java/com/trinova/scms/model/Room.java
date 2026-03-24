package com.trinova.scms.model;

public class Room {
    private int roomId;
    private String roomName;
    private String roomType;
    private int capacity;
    private String amenities;
    private boolean isActive;

    public Room() {}

    public Room(String roomName, String roomType, int capacity, String amenities) {
        this.roomName  = roomName;
        this.roomType  = roomType;
        this.capacity  = capacity;
        this.amenities = amenities;
        this.isActive  = true;
    }

    public int getRoomId()        { return roomId; }
    public String getRoomName()   { return roomName; }
    public String getRoomType()   { return roomType; }
    public int getCapacity()      { return capacity; }
    public String getAmenities()  { return amenities; }
    public boolean isActive()     { return isActive; }

    public void setRoomId(int roomId)           { this.roomId = roomId; }
    public void setRoomName(String roomName)     { this.roomName = roomName; }
    public void setRoomType(String roomType)     { this.roomType = roomType; }
    public void setCapacity(int capacity)        { this.capacity = capacity; }
    public void setAmenities(String amenities)   { this.amenities = amenities; }
    public void setActive(boolean active)        { this.isActive = active; }

    @Override
    public String toString() { return roomName; }
}