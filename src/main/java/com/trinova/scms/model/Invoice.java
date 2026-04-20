package com.trinova.scms.model;

import java.time.LocalDate;

public class Invoice {
    private int invoiceId;
    private int bookingId;
    private int memberId;
    private double baseAmount;
    private double facilityCost;
    private double vatAmount;
    private double totalAmount;
    private LocalDate issueDate;
    private String pdfPath;
    private String memberName;
    private String roomName;
    private String bookingType;

    public Invoice() {}

    public Invoice(int bookingId, int memberId,
                   double baseAmount, double facilityCost,
                   double vatAmount, double totalAmount,
                   LocalDate issueDate) {
        this.bookingId    = bookingId;
        this.memberId     = memberId;
        this.baseAmount   = baseAmount;
        this.facilityCost = facilityCost;
        this.vatAmount    = vatAmount;
        this.totalAmount  = totalAmount;
        this.issueDate    = issueDate;
    }

    // Getters
    public int getInvoiceId()        { return invoiceId; }
    public int getBookingId()        { return bookingId; }
    public int getMemberId()         { return memberId; }
    public double getBaseAmount()    { return baseAmount; }
    public double getFacilityCost()  { return facilityCost; }
    public double getVatAmount()     { return vatAmount; }
    public double getTotalAmount()   { return totalAmount; }
    public LocalDate getIssueDate()  { return issueDate; }
    public String getPdfPath()       { return pdfPath; }
    public String getMemberName()    { return memberName; }
    public String getRoomName()      { return roomName; }
    public String getBookingType()   { return bookingType; }

    // Setters
    public void setInvoiceId(int v)         { this.invoiceId = v; }
    public void setBookingId(int v)         { this.bookingId = v; }
    public void setMemberId(int v)          { this.memberId = v; }
    public void setBaseAmount(double v)     { this.baseAmount = v; }
    public void setFacilityCost(double v)   { this.facilityCost = v; }
    public void setVatAmount(double v)      { this.vatAmount = v; }
    public void setTotalAmount(double v)    { this.totalAmount = v; }
    public void setIssueDate(LocalDate v)   { this.issueDate = v; }
    public void setPdfPath(String v)        { this.pdfPath = v; }
    public void setMemberName(String v)     { this.memberName = v; }
    public void setRoomName(String v)       { this.roomName = v; }
    public void setBookingType(String v)    { this.bookingType = v; }
}