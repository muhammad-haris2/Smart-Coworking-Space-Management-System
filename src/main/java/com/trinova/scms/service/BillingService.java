package com.trinova.scms.service;

import com.trinova.scms.dao.InvoiceDAO;
import com.trinova.scms.dao.SubscriptionDAO;
import com.trinova.scms.model.Invoice;
import com.trinova.scms.model.PromoCode;
import com.trinova.scms.model.SubscriptionPlan;

import java.time.LocalDate;
import java.util.List;

public class BillingService {

    private final InvoiceDAO      invoiceDAO;
    private final SubscriptionDAO subscriptionDAO;

    public BillingService() throws Exception {
        this.invoiceDAO      = new InvoiceDAO();
        this.subscriptionDAO = new SubscriptionDAO();
    }

    public int generateInvoice(int bookingId, int memberId,
                                double baseAmount,
                                double facilityCost,
                                double vatAmount,
                                double totalAmount) throws Exception {
        Invoice inv = new Invoice(
            bookingId, memberId,
            baseAmount, facilityCost,
            vatAmount, totalAmount,
            LocalDate.now());

        int invoiceId = invoiceDAO.create(inv);
        if (invoiceId == -1)
            throw new Exception("Failed to generate invoice.");
        return invoiceId;
    }

    public double applyPromoCode(double total,
                                  String code) throws Exception {
        if (code == null || code.trim().isEmpty()) return total;

        PromoCode promo = subscriptionDAO.findPromoCode(code.trim());
        if (promo == null)
            throw new Exception("Invalid or inactive promo code.");

        double discounted;
        if (promo.getDiscountType().equals("PERCENTAGE")) {
            discounted = total -
                (total * promo.getDiscountValue() / 100.0);
        } else {
            discounted = total - promo.getDiscountValue();
        }
        discounted = Math.max(0,
            Math.round(discounted * 100.0) / 100.0);
        subscriptionDAO.incrementPromoUsage(promo.getPromoId());
        return discounted;
    }

    public List<SubscriptionPlan> getAllPlans() throws Exception {
        return subscriptionDAO.getAllPlans();
    }

    public void assignPlan(int memberId, int planId,
                           LocalDate expiry) throws Exception {
        subscriptionDAO.assignPlan(memberId, planId, expiry);
    }

    public List<Invoice> getMemberInvoices(int memberId) throws Exception {
        return invoiceDAO.getByMember(memberId);
    }

    public List<Invoice> getAllInvoices() throws Exception {
        return invoiceDAO.getAll();
    }

    public Invoice getInvoiceById(int invoiceId) throws Exception {
        return invoiceDAO.findById(invoiceId);
    }
}