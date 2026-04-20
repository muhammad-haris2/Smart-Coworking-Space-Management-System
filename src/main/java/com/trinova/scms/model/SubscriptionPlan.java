package com.trinova.scms.model;

public class SubscriptionPlan {
    private int planId;
    private String planName;
    private int durationDays;
    private double price;
    private String planType;

    public SubscriptionPlan() {}

    public int getPlanId()         { return planId; }
    public String getPlanName()    { return planName; }
    public int getDurationDays()   { return durationDays; }
    public double getPrice()       { return price; }
    public String getPlanType()    { return planType; }

    public void setPlanId(int v)          { this.planId = v; }
    public void setPlanName(String v)     { this.planName = v; }
    public void setDurationDays(int v)    { this.durationDays = v; }
    public void setPrice(double v)        { this.price = v; }
    public void setPlanType(String v)     { this.planType = v; }

    @Override
    public String toString() {
        return planName + " - PKR " +
               String.format("%.0f", price) +
               " / " + durationDays + " days";
    }
}