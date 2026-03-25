package com.trinova.scms.service;

import com.trinova.scms.dao.MemberDAO;
import com.trinova.scms.model.Member;
import com.trinova.scms.util.EmailSimulator;
import com.trinova.scms.util.PasswordUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthService {

    private final MemberDAO memberDAO;
    private final Map<String, Long> resetTokens = new HashMap<>();

    public AuthService() throws Exception {
        this.memberDAO = new MemberDAO();
    }

    public Member login(String email, String plainPassword) throws Exception {
        if (email == null || email.trim().isEmpty())
            throw new Exception("Email cannot be empty.");
        if (plainPassword == null || plainPassword.isEmpty())
            throw new Exception("Password cannot be empty.");

        Member member = memberDAO.findByEmail(email.trim().toLowerCase());
        if (member == null)
            throw new Exception("No account found with this email.");
        if (member.isLocked())
            throw new Exception("Account locked after 5 failed attempts. " +
                                "Reset your password to unlock.");

        boolean valid = PasswordUtil.verify(plainPassword, member.getPasswordHash());
        if (!valid) {
            memberDAO.recordFailedAttempt(email);
            int remaining = 5 - (member.getFailedAttempts() + 1);
            if (remaining <= 0)
                throw new Exception("Account locked after 5 failed attempts.");
            throw new Exception("Incorrect password. " + remaining + " attempts remaining.");
        }

        memberDAO.resetFailedAttempts(email);
        return member;
    }

    public void register(String fullName, String email,
                         String password, String confirmPassword) throws Exception {
        if (fullName == null || fullName.trim().isEmpty())
            throw new Exception("Full name cannot be empty.");
        if (email == null || email.trim().isEmpty())
            throw new Exception("Email cannot be empty.");
        if (!email.contains("@") || !email.contains("."))
            throw new Exception("Invalid email format.");
        if (password == null || password.length() < 8)
            throw new Exception("Password must be at least 8 characters.");
        if (!password.equals(confirmPassword))
            throw new Exception("Passwords do not match.");
        if (!isPasswordStrong(password))
            throw new Exception("Password must contain at least one uppercase letter, " +
                                "one digit, and one special character.");

        String normalizedEmail = email.trim().toLowerCase();
        if (memberDAO.emailExists(normalizedEmail))
            throw new Exception("This email is already registered.");

        String hash = PasswordUtil.hash(password);
        Member m = new Member(fullName.trim(), normalizedEmail, hash);
        int id = memberDAO.register(m);
        if (id == -1)
            throw new Exception("Registration failed. Please try again.");

        EmailSimulator.sendVerification(normalizedEmail);
    }

    public void initiatePasswordReset(String email) throws Exception {
        if (email == null || email.trim().isEmpty())
            throw new Exception("Email cannot be empty.");

        Member member = memberDAO.findByEmail(email.trim().toLowerCase());
        if (member == null) {
            System.out.println("[SECURITY] Reset requested for unregistered email: " + email);
            return;
        }

        String token = UUID.randomUUID().toString();
        resetTokens.put(token, System.currentTimeMillis());
        EmailSimulator.sendPasswordReset(email, token);
    }

    public void resetPassword(String token, String newPassword,
                              String confirmPassword) throws Exception {
        if (!resetTokens.containsKey(token))
            throw new Exception("Invalid or expired reset link. Please request a new one.");

        long tokenAge = System.currentTimeMillis() - resetTokens.get(token);
        if (tokenAge > 30 * 60 * 1000)  {
            resetTokens.remove(token);
            throw new Exception("This reset link has expired. Please request a new one.");
        }

        if (newPassword == null || newPassword.length() < 8)
            throw new Exception("Password must be at least 8 characters.");
        if (!newPassword.equals(confirmPassword))
            throw new Exception("Passwords do not match.");
        if (!isPasswordStrong(newPassword))
            throw new Exception("Password must contain uppercase, digit, and special character.");

        resetTokens.remove(token);
    }

    private boolean isPasswordStrong(String password) {
        boolean hasUpper   = password.chars().anyMatch(Character::isUpperCase);
        boolean hasDigit   = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(c ->
            "!@#$%^&*()_+-=[]{}|;':\",./<>?".indexOf(c) >= 0);
        return hasUpper && hasDigit && hasSpecial;
    }
}