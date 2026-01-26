package com.medico.admin.dto;

import java.math.BigDecimal;

public record DashboardStatsDto(
    // Patient stats
    long totalPatients,
    long todayAppointments,
    long pendingAppointments,
    
    // Pharmacy stats
    long totalMedications,
    long lowStockItems,
    long expiringItems,
    long todayDispensings,
    
    // Laboratory stats
    long pendingLabOrders,
    long todayLabOrders,
    long pendingVerifications,
    
    // Billing stats
    BigDecimal todayRevenue,
    long pendingInvoices,
    long overdueInvoices,
    
    // Prescription stats
    long activePrescriptions
) {}

