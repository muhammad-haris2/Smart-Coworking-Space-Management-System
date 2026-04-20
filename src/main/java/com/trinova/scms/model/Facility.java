package com.trinova.scms.model;

public class Facility {
    private int facilityId;
    private String facilityName;
    private double price;
    private String unit;
    private boolean freeForBasic;
    private boolean freeForPremium;

    // quantity selected by user during booking
    private int selectedQuantity;

    public Facility() {}

    // Getters
    public int getFacilityId()      { return facilityId; }
    public String getFacilityName() { return facilityName; }
    public double getPrice()        { return price; }
    public String getUnit()         { return unit; }
    public boolean isFreeForBasic() { return freeForBasic; }
    public boolean isFreeForPremium(){ return freeForPremium; }
    public int getSelectedQuantity(){ return selectedQuantity; }

    // Setters
    public void setFacilityId(int v)         { this.facilityId = v; }
    public void setFacilityName(String v)    { this.facilityName = v; }
    public void setPrice(double v)           { this.price = v; }
    public void setUnit(String v)            { this.unit = v; }
    public void setFreeForBasic(boolean v)   { this.freeForBasic = v; }
    public void setFreeForPremium(boolean v) { this.freeForPremium = v; }
    public void setSelectedQuantity(int v)   { this.selectedQuantity = v; }

    public double getTotalCost(String planType) {
        if ("PREMIUM".equalsIgnoreCase(planType) && freeForPremium)
            return 0;
        if ("BASIC".equalsIgnoreCase(planType) && freeForBasic)
            return 0;
        return price * selectedQuantity;
    }

    @Override
    public String toString() {
        return facilityName + " (PKR " + price + "/" + unit + ")";
    }
}