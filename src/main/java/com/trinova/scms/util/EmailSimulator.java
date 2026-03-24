package com.trinova.scms.util;

public class EmailSimulator {

    public static void sendVerification(String email) {
        System.out.println("[EMAIL] Verification email sent to: " + email);
    }

    public static void sendPasswordReset(String email, String token) {
        System.out.println("[EMAIL] Password reset link sent to: " + email);
        System.out.println("[EMAIL] Reset token: " + token);
    }

    public static void sendBookingConfirmation(String email, int bookingId) {
        System.out.println("[EMAIL] Booking confirmation sent to: " + email +
                           " | Booking ID: " + bookingId);
    }

    public static void sendCancellationNotice(String email, int bookingId) {
        System.out.println("[EMAIL] Cancellation notice sent to: " + email +
                           " | Booking ID: " + bookingId);
    }
}