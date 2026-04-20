package com.trinova.scms.model;

public class PromoCode {
    private int promoId;
    private String code;
    private String discountType;
    private double discountValue;
    private boolean isActive;
    private int usageCount;

    public PromoCode() {}

    public int getPromoId()           { return promoId; }
    public String getCode()           { return code; }
    public String getDiscountType()   { return discountType; }
    public double getDiscountValue()  { return discountValue; }
    public boolean isActive()         { return isActive; }
    public int getUsageCount()        { return usageCount; }

    public void setPromoId(int promoId)             { this.promoId = promoId; }
    public void setCode(String code)                { this.code = code; }
    public void setDiscountType(String type)        { this.discountType = type; }
    public void setDiscountValue(double value)      { this.discountValue = value; }
    public void setActive(boolean active)           { this.isActive = active; }
    public void setUsageCount(int usageCount)       { this.usageCount = usageCount; }
}