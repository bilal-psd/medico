package com.medico.billing.mapper;

import com.medico.billing.domain.*;
import com.medico.billing.dto.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BillingMapper {

    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "patientName", expression = "java(invoice.getPatient().getFullName())")
    @Mapping(target = "patientMrn", source = "patient.medicalRecordNumber")
    InvoiceDto toDto(Invoice invoice);

    List<InvoiceDto> toInvoiceDtoList(List<Invoice> invoices);

    BillingItemDto toDto(BillingItem billingItem);

    List<BillingItemDto> toBillingItemDtoList(List<BillingItem> items);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    BillingItem toEntity(CreateBillingItemRequest request);

    @Mapping(target = "invoiceId", source = "invoice.id")
    @Mapping(target = "invoiceNumber", source = "invoice.invoiceNumber")
    PaymentDto toDto(Payment payment);

    List<PaymentDto> toPaymentDtoList(List<Payment> payments);
}

