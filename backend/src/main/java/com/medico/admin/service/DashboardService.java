package com.medico.admin.service;

import com.medico.admin.dto.DashboardStatsDto;
import com.medico.billing.service.InvoiceService;
import com.medico.billing.service.PaymentService;
import com.medico.laboratory.service.LabOrderService;
import com.medico.laboratory.service.LabResultService;
import com.medico.patient.service.AppointmentService;
import com.medico.patient.service.PatientService;
import com.medico.patient.service.PrescriptionService;
import com.medico.pharmacy.service.DispensingService;
import com.medico.pharmacy.service.InventoryService;
import com.medico.pharmacy.service.MedicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final PrescriptionService prescriptionService;
    private final MedicationService medicationService;
    private final InventoryService inventoryService;
    private final DispensingService dispensingService;
    private final LabOrderService labOrderService;
    private final LabResultService labResultService;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;

    public DashboardStatsDto getDashboardStats() {
        return new DashboardStatsDto(
            // Patient stats
            patientService.getActivePatientCount(),
            appointmentService.getTodayAppointmentCount(),
            0, // Can be implemented as needed
            
            // Pharmacy stats
            medicationService.getActiveMedicationCount(),
            inventoryService.getLowStockCount(),
            inventoryService.getExpiringItemsCount(),
            dispensingService.getTodayDispensingCount(),
            
            // Laboratory stats
            labOrderService.getPendingOrderCount(),
            labOrderService.getTodayOrderCount(),
            labResultService.getPendingVerificationCount(),
            
            // Billing stats
            paymentService.getTodayTotalPayments(),
            invoiceService.getPendingInvoiceCount(),
            invoiceService.getOverdueInvoiceCount(),
            
            // Prescription stats
            prescriptionService.getActivePrescriptionCount()
        );
    }
}

