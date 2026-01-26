-- Medico Hospital Management System - Initial Schema
-- Version: 1.0.0

-- =====================================================
-- PATIENT MANAGEMENT MODULE
-- =====================================================

CREATE TABLE patients (
    id UUID PRIMARY KEY,
    medical_record_number VARCHAR(50) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    phone_number VARCHAR(20),
    email VARCHAR(255),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    emergency_contact_name VARCHAR(200),
    emergency_contact_phone VARCHAR(20),
    blood_type VARCHAR(20),
    allergies TEXT,
    medical_notes TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_patients_mrn ON patients(medical_record_number);
CREATE INDEX idx_patients_name ON patients(last_name, first_name);
CREATE INDEX idx_patients_email ON patients(email);
CREATE INDEX idx_patients_active ON patients(active);

CREATE TABLE appointments (
    id UUID PRIMARY KEY,
    patient_id UUID NOT NULL REFERENCES patients(id),
    doctor_id UUID NOT NULL,
    doctor_name VARCHAR(255) NOT NULL,
    department VARCHAR(100),
    appointment_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    appointment_type VARCHAR(50) NOT NULL,
    reason TEXT,
    notes TEXT,
    room_number VARCHAR(50),
    cancelled_reason VARCHAR(500),
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_appointments_patient ON appointments(patient_id);
CREATE INDEX idx_appointments_doctor ON appointments(doctor_id);
CREATE INDEX idx_appointments_date ON appointments(appointment_date_time);
CREATE INDEX idx_appointments_status ON appointments(status);

CREATE TABLE medical_records (
    id UUID PRIMARY KEY,
    patient_id UUID NOT NULL REFERENCES patients(id),
    doctor_id UUID NOT NULL,
    doctor_name VARCHAR(255) NOT NULL,
    visit_date TIMESTAMP NOT NULL,
    record_type VARCHAR(50) NOT NULL,
    chief_complaint TEXT,
    symptoms TEXT,
    diagnosis TEXT,
    treatment_plan TEXT,
    vital_signs TEXT,
    physical_examination TEXT,
    notes TEXT,
    follow_up_date TIMESTAMP,
    appointment_id UUID REFERENCES appointments(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_medical_records_patient ON medical_records(patient_id);
CREATE INDEX idx_medical_records_doctor ON medical_records(doctor_id);
CREATE INDEX idx_medical_records_date ON medical_records(visit_date);

CREATE TABLE prescriptions (
    id UUID PRIMARY KEY,
    prescription_number VARCHAR(50) UNIQUE NOT NULL,
    patient_id UUID NOT NULL REFERENCES patients(id),
    doctor_id UUID NOT NULL,
    doctor_name VARCHAR(255) NOT NULL,
    prescription_date TIMESTAMP NOT NULL,
    valid_until DATE,
    status VARCHAR(50) NOT NULL,
    diagnosis TEXT,
    notes TEXT,
    appointment_id UUID REFERENCES appointments(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_prescriptions_patient ON prescriptions(patient_id);
CREATE INDEX idx_prescriptions_number ON prescriptions(prescription_number);
CREATE INDEX idx_prescriptions_status ON prescriptions(status);

CREATE TABLE prescription_items (
    id UUID PRIMARY KEY,
    prescription_id UUID NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
    medication_id UUID,
    medication_name VARCHAR(255) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    duration VARCHAR(100),
    quantity INTEGER NOT NULL,
    instructions TEXT,
    dispensed_quantity INTEGER DEFAULT 0,
    refills_allowed INTEGER DEFAULT 0,
    refills_remaining INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_prescription_items_prescription ON prescription_items(prescription_id);
CREATE INDEX idx_prescription_items_medication ON prescription_items(medication_id);

-- =====================================================
-- PHARMACY/INVENTORY MODULE
-- =====================================================

CREATE TABLE medications (
    id UUID PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    generic_name VARCHAR(255),
    brand_name VARCHAR(255),
    description TEXT,
    category VARCHAR(50) NOT NULL,
    form VARCHAR(50) NOT NULL,
    strength VARCHAR(100),
    unit VARCHAR(50),
    manufacturer VARCHAR(255),
    unit_price DECIMAL(10, 2),
    reorder_level INTEGER,
    requires_prescription BOOLEAN DEFAULT FALSE,
    controlled_substance BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_medications_code ON medications(code);
CREATE INDEX idx_medications_name ON medications(name);
CREATE INDEX idx_medications_category ON medications(category);
CREATE INDEX idx_medications_active ON medications(active);

CREATE TABLE suppliers (
    id UUID PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(200),
    email VARCHAR(255),
    phone VARCHAR(20),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    tax_id VARCHAR(50),
    payment_terms VARCHAR(200),
    lead_time_days INTEGER,
    active BOOLEAN DEFAULT TRUE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_suppliers_code ON suppliers(code);
CREATE INDEX idx_suppliers_name ON suppliers(name);
CREATE INDEX idx_suppliers_active ON suppliers(active);

CREATE TABLE inventory (
    id UUID PRIMARY KEY,
    medication_id UUID NOT NULL REFERENCES medications(id),
    batch_number VARCHAR(100) NOT NULL,
    quantity INTEGER NOT NULL,
    reserved_quantity INTEGER DEFAULT 0,
    expiry_date DATE NOT NULL,
    manufacture_date DATE,
    location VARCHAR(100),
    status VARCHAR(50) NOT NULL,
    supplier_id UUID REFERENCES suppliers(id),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_inventory_medication ON inventory(medication_id);
CREATE INDEX idx_inventory_batch ON inventory(batch_number);
CREATE INDEX idx_inventory_expiry ON inventory(expiry_date);
CREATE INDEX idx_inventory_status ON inventory(status);

CREATE TABLE prescription_dispensing (
    id UUID PRIMARY KEY,
    prescription_id UUID NOT NULL REFERENCES prescriptions(id),
    prescription_item_id UUID NOT NULL REFERENCES prescription_items(id),
    inventory_id UUID NOT NULL REFERENCES inventory(id),
    dispensed_quantity INTEGER NOT NULL,
    dispensed_at TIMESTAMP NOT NULL,
    dispensed_by UUID NOT NULL,
    pharmacist_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_dispensing_prescription ON prescription_dispensing(prescription_id);
CREATE INDEX idx_dispensing_item ON prescription_dispensing(prescription_item_id);
CREATE INDEX idx_dispensing_date ON prescription_dispensing(dispensed_at);

-- =====================================================
-- LABORATORY MODULE
-- =====================================================

CREATE TABLE lab_tests (
    id UUID PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    sample_type VARCHAR(100),
    sample_volume VARCHAR(50),
    container_type VARCHAR(100),
    preparation_instructions TEXT,
    turnaround_time VARCHAR(100),
    price DECIMAL(10, 2),
    normal_range TEXT,
    unit VARCHAR(50),
    active BOOLEAN DEFAULT TRUE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_lab_tests_code ON lab_tests(code);
CREATE INDEX idx_lab_tests_name ON lab_tests(name);
CREATE INDEX idx_lab_tests_category ON lab_tests(category);
CREATE INDEX idx_lab_tests_active ON lab_tests(active);

CREATE TABLE lab_orders (
    id UUID PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    patient_id UUID NOT NULL REFERENCES patients(id),
    ordering_doctor_id UUID NOT NULL,
    ordering_doctor_name VARCHAR(255) NOT NULL,
    order_date TIMESTAMP NOT NULL,
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(50) NOT NULL,
    clinical_notes TEXT,
    diagnosis VARCHAR(500),
    sample_collected_at TIMESTAMP,
    sample_collected_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_lab_orders_number ON lab_orders(order_number);
CREATE INDEX idx_lab_orders_patient ON lab_orders(patient_id);
CREATE INDEX idx_lab_orders_doctor ON lab_orders(ordering_doctor_id);
CREATE INDEX idx_lab_orders_date ON lab_orders(order_date);
CREATE INDEX idx_lab_orders_status ON lab_orders(status);

CREATE TABLE lab_order_items (
    id UUID PRIMARY KEY,
    lab_order_id UUID NOT NULL REFERENCES lab_orders(id) ON DELETE CASCADE,
    lab_test_id UUID NOT NULL REFERENCES lab_tests(id),
    status VARCHAR(50) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_lab_order_items_order ON lab_order_items(lab_order_id);
CREATE INDEX idx_lab_order_items_test ON lab_order_items(lab_test_id);

CREATE TABLE lab_results (
    id UUID PRIMARY KEY,
    lab_order_item_id UUID NOT NULL UNIQUE REFERENCES lab_order_items(id),
    result_value TEXT,
    unit VARCHAR(50),
    reference_range VARCHAR(255),
    flag VARCHAR(50),
    interpretation TEXT,
    performed_at TIMESTAMP,
    performed_by UUID,
    technician_name VARCHAR(255),
    verified_at TIMESTAMP,
    verified_by UUID,
    verifier_name VARCHAR(255),
    notes TEXT,
    is_abnormal BOOLEAN DEFAULT FALSE,
    is_critical BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_lab_results_item ON lab_results(lab_order_item_id);
CREATE INDEX idx_lab_results_performed ON lab_results(performed_at);
CREATE INDEX idx_lab_results_abnormal ON lab_results(is_abnormal);
CREATE INDEX idx_lab_results_critical ON lab_results(is_critical);

-- =====================================================
-- BILLING & FINANCE MODULE
-- =====================================================

CREATE TABLE invoices (
    id UUID PRIMARY KEY,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    patient_id UUID NOT NULL REFERENCES patients(id),
    invoice_date TIMESTAMP NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    subtotal DECIMAL(12, 2) NOT NULL,
    tax_amount DECIMAL(12, 2) DEFAULT 0,
    discount_amount DECIMAL(12, 2) DEFAULT 0,
    total_amount DECIMAL(12, 2) NOT NULL,
    paid_amount DECIMAL(12, 2) DEFAULT 0,
    balance_due DECIMAL(12, 2),
    notes TEXT,
    created_by_id UUID,
    created_by_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_invoices_number ON invoices(invoice_number);
CREATE INDEX idx_invoices_patient ON invoices(patient_id);
CREATE INDEX idx_invoices_date ON invoices(invoice_date);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_due_date ON invoices(due_date);

CREATE TABLE billing_items (
    id UUID PRIMARY KEY,
    invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    item_type VARCHAR(50) NOT NULL,
    reference_id UUID,
    description VARCHAR(500) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    discount_percent DECIMAL(5, 2) DEFAULT 0,
    total_price DECIMAL(12, 2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_billing_items_invoice ON billing_items(invoice_id);
CREATE INDEX idx_billing_items_type ON billing_items(item_type);

CREATE TABLE payments (
    id UUID PRIMARY KEY,
    payment_number VARCHAR(50) UNIQUE NOT NULL,
    invoice_id UUID NOT NULL REFERENCES invoices(id),
    amount DECIMAL(12, 2) NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    transaction_reference VARCHAR(200),
    received_by_id UUID,
    received_by_name VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_payments_number ON payments(payment_number);
CREATE INDEX idx_payments_invoice ON payments(invoice_id);
CREATE INDEX idx_payments_date ON payments(payment_date);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_method ON payments(payment_method);

-- =====================================================
-- ADMINISTRATION MODULE
-- =====================================================

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    user_id UUID,
    user_name VARCHAR(255),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id UUID,
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(50),
    user_agent TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_date ON audit_logs(created_at);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);

