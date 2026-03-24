package com.trinova.scms.model;

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
    private boolean isLocked;
    private int failedAttempts;

    public Member(String fullName, String email, String passwordHash) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = "MEMBER";
        this.isLocked = false;
        this.failedAttempts = 0;
    }

    public int getMemberId()          { return memberId; }
    public String getFullName()       { return fullName; }
    public String getEmail()          { return email; }
    public String getPasswordHash()   { return passwordHash; }
    public String getRole()           { return role; }
    public String getPhone()          { return phone; }
    public String getBio()            { return bio; }
    public String getProfilePhoto()   { return profilePhoto; }
    public int getPlanId()            { return planId; }
    public boolean isLocked()         { return isLocked; }
    public int getFailedAttempts()    { return failedAttempts; }

    public void setMemberId(int memberId)           { this.memberId = memberId; }
    public void setFullName(String fullName)         { this.fullName = fullName; }
    public void setEmail(String email)               { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(String role)                 { this.role = role; }
    public void setPhone(String phone)               { this.phone = phone; }
    public void setBio(String bio)                   { this.bio = bio; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }
    public void setPlanId(int planId)               { this.planId = planId; }
    public void setLocked(boolean locked)            { this.isLocked = locked; }
    public void setFailedAttempts(int failedAttempts){ this.failedAttempts = failedAttempts; }

    @Override
    public String toString() {
        return "Member{id=" + memberId + ", name=" + fullName + ", role=" + role + "}";
    }
}