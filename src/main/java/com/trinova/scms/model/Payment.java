package com.trinova.scms.model;

import java.time.LocalDateTime;

public class Payment {
    private int paymentId;
    private int memberId;
    private int bookingId;
    private int subscriptionId;
    private double amount;
    private String paymentMethod;
    private String status;
    private String transactionRef;
    private LocalDateTime paidAt;

    public Payment() {}

    // Getters
    public int getPaymentId()           { return paymentId; }
    public int getMemberId()            { return memberId; }
    public int getBookingId()           { return bookingId; }
    public int getSubscriptionId()      { return subscriptionId; }
    public double getAmount()           { return amount; }
    public String getPaymentMethod()    { return paymentMethod; }
    public String getStatus()           { return status; }
    public String getTransactionRef()   { return transactionRef; }
    public LocalDateTime getPaidAt()    { return paidAt; }

    // Setters
    public void setPaymentId(int v)          { this.paymentId = v; }
    public void setMemberId(int v)           { this.memberId = v; }
    public void setBookingId(int v)          { this.bookingId = v; }
    public void setSubscriptionId(int v)     { this.subscriptionId = v; }
    public void setAmount(double v)          { this.amount = v; }
    public void setPaymentMethod(String v)   { this.paymentMethod = v; }
    public void setStatus(String v)          { this.status = v; }
    public void setTransactionRef(String v)  { this.transactionRef = v; }
    public void setPaidAt(LocalDateTime v)   { this.paidAt = v; }
}