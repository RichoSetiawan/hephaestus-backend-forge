ALTER TABLE customers ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_customers_nik ON customers(nik);
CREATE INDEX IF NOT EXISTS idx_customers_email ON customers(email);
CREATE INDEX IF NOT EXISTS idx_loan_applications_customer_id ON loan_applications(customer_id);
CREATE INDEX IF NOT EXISTS idx_loan_applications_status ON loan_applications(status);
CREATE INDEX IF NOT EXISTS idx_loan_applications_created_at ON loan_applications(created_at);
CREATE INDEX IF NOT EXISTS idx_repayment_schedules_loan_id ON repayment_schedules(loan_application_id);
CREATE INDEX IF NOT EXISTS idx_repayment_schedules_status ON repayment_schedules(status);


CREATE INDEX IF NOT EXISTS idx_customers_active ON customers(id) WHERE is_deleted = FALSE;