-- CUSTOMER TABLE
CREATE TABLE IF NOT EXISTS Customer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    identity_number VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    dob DATE,
    prequalified_amount DOUBLE PRECISION,
    max_qualified_amount DOUBLE PRECISION,
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_customer_created_date ON Customer(created_date);

-- PRODUCT TABLES
CREATE TABLE IF NOT EXISTS Product (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_enabled BOOLEAN DEFAULT TRUE,
    grace_period INTEGER,
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Charge (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    is_enabled BOOLEAN DEFAULT TRUE,
    is_upfront BOOLEAN DEFAULT FALSE,
    is_penalty BOOLEAN DEFAULT FALSE,
    amount DOUBLE PRECISION,
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ProductChargeMapping (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL,
    charge_id UUID NOT NULL,
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pcm_product FOREIGN KEY (product_id) REFERENCES Product(id),
    CONSTRAINT fk_pcm_charge FOREIGN KEY (charge_id) REFERENCES Charge(id)
);

-- LOAN TABLES
CREATE TABLE IF NOT EXISTS Loan (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    loan_status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    applied_amount DOUBLE PRECISION,
    interest_amount DOUBLE PRECISION,
    disbursement_amount DOUBLE PRECISION,
    repaid_amount DOUBLE PRECISION,
    negotiated_installment INTEGER,
    payment_frequency VARCHAR(50),
    start_date TIMESTAMP,
    due_date TIMESTAMP,
    end_date TIMESTAMP,
    disbursement_date TIMESTAMP,
    cleared_date TIMESTAMP,
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_loan_product FOREIGN KEY (product_id) REFERENCES Product(id),
    CONSTRAINT fk_loan_customer FOREIGN KEY (customer_id) REFERENCES Customer(id),
    CONSTRAINT valid_loan_status CHECK (
        loan_status IN ('OPEN', 'CANCELLED', 'OVERDUE', 'WRITTEN_OFF', 'CLEARED')
    )
);

CREATE TABLE IF NOT EXISTS RepaymentRequest (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    loan_id UUID NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    status VARCHAR(50),
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_repay_customer FOREIGN KEY (customer_id) REFERENCES Customer(id),
    CONSTRAINT fk_repay_loan FOREIGN KEY (loan_id) REFERENCES Loan(id)
);

-- NOTIFICATION TABLE
CREATE TABLE IF NOT EXISTS Notification (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID,
    loan_id UUID,
    type VARCHAR(50),
    message TEXT,
    status VARCHAR(50),
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_customer FOREIGN KEY (customer_id) REFERENCES Customer(id),
    CONSTRAINT fk_notification_loan FOREIGN KEY (loan_id) REFERENCES Loan(id)
);

-- SEED DATA
INSERT INTO Customer (first_name, last_name, identity_number, phone_number, email, dob, prequalified_amount, max_qualified_amount)
VALUES
  ('Alice', 'Otieno', 'ID12345678', '0712345678', 'alice@example.com', '1990-05-10', 10000, 25000),
  ('Bob', 'Mwangi', 'ID87654321', '0723456789', 'bob@example.com', '1988-07-15', 12000, 30000),
  ('Carol', 'Njeri', 'ID10293847', '0734567890', 'carol@example.com', '1995-03-20', 15000, 35000);

INSERT INTO Product (name, description, is_enabled, grace_period)
VALUES
  ('Personal Loan', 'Short-term personal loan', TRUE, 7),
  ('Business Loan', 'Loan for small businesses', TRUE, 10);
