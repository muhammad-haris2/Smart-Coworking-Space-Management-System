package com.trinova.scms.model;

import java.time.LocalDate;

public class Member {
    private int memberId;
    private String fullName;
    private String email;
    private String passwordHash;
    private String role;
    private String phone;
    private String bio;
    private String profilePhoto;
    private int planId;
    private String planType;
    private LocalDate planExpiry;
    private boolean isLocked;
    private int failedAttempts;

    public Member(String fullName, String email, String passwordHash) {
        this.fullName     = fullName;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.role         = "MEMBER";
        this.isLocked     = false;
        this.failedAttempts = 0;
    }

    public Member() {}

    // Getters
    public int getMemberId()         { return memberId; }
    public String getFullName()      { return fullName; }
    public String getEmail()         { return email; }
    public String getPasswordHash()  { return passwordHash; }
    public String getRole()          { return role; }
    public String getPhone()         { return phone; }
    public String getBio()           { return bio; }
    public String getProfilePhoto()  { return profilePhoto; }
    public int getPlanId()           { return planId; }
    public String getPlanType()      { return planType; }
    public LocalDate getPlanExpiry() { return planExpiry; }
    public boolean isLocked()        { return isLocked; }
    public int getFailedAttempts()   { return failedAttempts; }

    // Setters
    public void setMemberId(int v)          { this.memberId = v; }
    public void setFullName(String v)       { this.fullName = v; }
    public void setEmail(String v)          { this.email = v; }
    public void setPasswordHash(String v)   { this.passwordHash = v; }
    public void setRole(String v)           { this.role = v; }
    public void setPhone(String v)          { this.phone = v; }
    public void setBio(String v)            { this.bio = v; }
    public void setProfilePhoto(String v)   { this.profilePhoto = v; }
    public void setPlanId(int v)            { this.planId = v; }
    public void setPlanType(String v)       { this.planType = v; }
    public void setPlanExpiry(LocalDate v)  { this.planExpiry = v; }
    public void setLocked(boolean v)        { this.isLocked = v; }
    public void setFailedAttempts(int v)    { this.failedAttempts = v; }

    public boolean hasActivePlan() {
        return planId > 0 &&
               planExpiry != null &&
               !planExpiry.isBefore(LocalDate.now());
    }

    public boolean isPremium() {
        return hasActivePlan() &&
               "PREMIUM".equalsIgnoreCase(planType);
    }

    public boolean isBasic() {
        return hasActivePlan() &&
               "BASIC".equalsIgnoreCase(planType);
    }

    @Override
    public String toString() {
        return "Member{id=" + memberId +
               ", name=" + fullName +
               ", role=" + role +
               ", plan=" + planType + "}";
    }
}