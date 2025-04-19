-- =============================================
-- Urbanfood Database Setup
-- =============================================

-- Enable output for debugging
SET SERVEROUTPUT ON;
-- =============================================
-- Tables
-- =============================================

-- User Accounts table
CREATE TABLE user_accounts (
    userid NUMBER PRIMARY KEY,
    username VARCHAR2(50) NOT NULL UNIQUE,
    password VARCHAR2(100) NOT NULL,
    firstname VARCHAR2(50),
    lastname VARCHAR2(50),
    name VARCHAR2(100),
    email VARCHAR2(100) NOT NULL UNIQUE,
    phone VARCHAR2(20),
    address VARCHAR2(200),
    city VARCHAR2(50),
    statedistrict VARCHAR2(50),
    country VARCHAR2(50),
    zipcode VARCHAR2(20),
    datecreated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR2(20) DEFAULT 'ACTIVE',
    is_google_user NUMBER(1) DEFAULT 0,
    google_id VARCHAR2(100) UNIQUE,
    created_at TIMESTAMP NOT NULL,
    enabled NUMBER(1) DEFAULT 1,
    last_login TIMESTAMP
);

-- Products table
CREATE TABLE Products(
    ProductID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ProductName VARCHAR2(600),
    Description VARCHAR2(800),
    Status VARCHAR2(20)CHECK(Status IN('AVAILABLE','OUT_OF_STOCK')),
    Amount INT,
    Brand VARCHAR2(60)DEFAULT('NO BRAND'),
    Price FLOAT,
    Tags CLOB,
    MainImageName VARCHAR2(500),
    ShippingCost FLOAT,
    CatID NUMBER,
    SupplierID NUMBER
);

-- Cart table
CREATE TABLE carts (
    cart_id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES user_accounts(userid)
);

-- Cart Items table
CREATE TABLE cart_items (
    cart_item_id NUMBER PRIMARY KEY,
    cart_id NUMBER NOT NULL,
    ProductID NUMBER NOT NULL,
    quantity NUMBER DEFAULT 1,
    price NUMBER(10,2) NOT NULL,
    added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_id FOREIGN KEY (cart_id) REFERENCES carts(cart_id),
    CONSTRAINT fk_product_id FOREIGN KEY (ProductID) REFERENCES products(ProductID)
);

-- Email Notifications table
CREATE TABLE email_notifications (
    notification_id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    email VARCHAR2(255) NOT NULL,
    firstname VARCHAR2(100),
    lastname VARCHAR2(100),
    subject VARCHAR2(255) NOT NULL,
    notification_type VARCHAR2(50) NOT NULL,
    ProductID NUMBER,
    processed NUMBER(1) DEFAULT 0,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_date TIMESTAMP,
    CONSTRAINT fk_notification_user_id FOREIGN KEY (user_id) REFERENCES user_accounts(userid),
    CONSTRAINT fk_notification_product_id FOREIGN KEY (ProductID) REFERENCES products(ProductID)
);

-- =============================================
-- Sequences
-- =============================================

CREATE SEQUENCE cart_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE cart_item_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE email_notification_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE product_id_seq START WITH 101 INCREMENT BY 1;

-- =============================================
-- PL/SQL Procedures
-- =============================================

-- Procedure to add item to cart
CREATE OR REPLACE PROCEDURE add_to_cart(
    p_user_id IN NUMBER,
    p_product_id IN NUMBER,
    p_quantity IN NUMBER,
    p_price IN NUMBER
) AS
    v_cart_id NUMBER;
    v_item_exists NUMBER;
BEGIN
    -- Check if cart exists for user
    SELECT COUNT(*) INTO v_item_exists FROM carts WHERE user_id = p_user_id;
    
    IF v_item_exists = 0 THEN
        -- Create new cart
        v_cart_id := cart_id_seq.NEXTVAL;
        INSERT INTO carts (cart_id, user_id) VALUES (v_cart_id, p_user_id);
    ELSE
        -- Get existing cart
        SELECT cart_id INTO v_cart_id FROM carts WHERE user_id = p_user_id;
    END IF;
    
    -- Check if product already in cart
    SELECT COUNT(*) INTO v_item_exists 
    FROM cart_items 
    WHERE cart_id = v_cart_id AND ProductID = p_product_id;
    
    IF v_item_exists > 0 THEN
        -- Update quantity
        UPDATE cart_items 
        SET quantity = quantity + p_quantity,
            added_date = CURRENT_TIMESTAMP
        WHERE cart_id = v_cart_id AND ProductID = p_product_id;
    ELSE
        -- Add new item
        INSERT INTO cart_items (cart_item_id, cart_id, ProductID, quantity, price, added_date)
        VALUES (cart_item_id_seq.NEXTVAL, v_cart_id, p_product_id, p_quantity, p_price, CURRENT_TIMESTAMP);
    END IF;
    
    -- Update cart timestamp
    UPDATE carts SET updated_date = CURRENT_TIMESTAMP WHERE cart_id = v_cart_id;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END add_to_cart;
/

-- Procedure to remove item from cart
CREATE OR REPLACE PROCEDURE remove_from_cart(
    p_user_id IN NUMBER,
    p_product_id IN NUMBER
) AS
    v_cart_id NUMBER;
BEGIN
    -- Get cart ID
    SELECT cart_id INTO v_cart_id FROM carts WHERE user_id = p_user_id;
    
    -- Remove item
    DELETE FROM cart_items 
    WHERE cart_id = v_cart_id AND ProductID = p_product_id;
    
    -- Update cart timestamp
    UPDATE carts SET updated_date = CURRENT_TIMESTAMP WHERE cart_id = v_cart_id;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END remove_from_cart;
/

-- Procedure to update cart item quantity
CREATE OR REPLACE PROCEDURE update_cart_quantity(
    p_user_id IN NUMBER,
    p_product_id IN NUMBER,
    p_quantity IN NUMBER
) AS
    v_cart_id NUMBER;
BEGIN
    -- Get cart ID
    SELECT cart_id INTO v_cart_id FROM carts WHERE user_id = p_user_id;
    
    IF p_quantity <= 0 THEN
        -- Remove item if quantity is 0 or negative
        DELETE FROM cart_items 
        WHERE cart_id = v_cart_id AND ProductID = p_product_id;
    ELSE
        -- Update quantity
        UPDATE cart_items 
        SET quantity = p_quantity,
            added_date = CURRENT_TIMESTAMP
        WHERE cart_id = v_cart_id AND ProductID = p_product_id;
    END IF;
    
    -- Update cart timestamp
    UPDATE carts SET updated_date = CURRENT_TIMESTAMP WHERE cart_id = v_cart_id;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END update_cart_quantity;
/

-- Procedure to clear cart
CREATE OR REPLACE PROCEDURE clear_cart(
    p_user_id IN NUMBER
) AS
    v_cart_id NUMBER;
BEGIN
    -- Get cart ID
    SELECT cart_id INTO v_cart_id FROM carts WHERE user_id = p_user_id;
    
    -- Remove all items
    DELETE FROM cart_items WHERE cart_id = v_cart_id;
    
    -- Update cart timestamp
    UPDATE carts SET updated_date = CURRENT_TIMESTAMP WHERE cart_id = v_cart_id;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END clear_cart;
/

-- Procedure to auto-delete old cart items and notify users
-- Update the cleanup procedure for testing (5 minutes instead of 10 days)
CREATE OR REPLACE PROCEDURE cleanup_old_cart_items AS
    CURSOR c_old_items IS
        SELECT ci.cart_item_id, ci.ProductID, ci.cart_id, c.user_id, 
               ua.email, ua.firstname, ua.lastname
        FROM cart_items ci
        JOIN carts c ON ci.cart_id = c.cart_id
        JOIN user_accounts ua ON c.user_id = ua.userid
        -- Changed from 10 DAYS to 5 MINUTES for testing
        WHERE ci.added_date < CURRENT_TIMESTAMP - INTERVAL '2' MINUTE;
        
    TYPE t_items_to_notify IS TABLE OF c_old_items%ROWTYPE;
    v_items t_items_to_notify;
BEGIN
    -- Collect items to be deleted
    OPEN c_old_items;
    FETCH c_old_items BULK COLLECT INTO v_items;
    CLOSE c_old_items;
    
    -- Log for testing
    DBMS_OUTPUT.PUT_LINE('Found ' || v_items.COUNT || ' items to remove');
    
    -- Delete old items
    FOR i IN 1..v_items.COUNT LOOP
        -- Log for testing
        DBMS_OUTPUT.PUT_LINE('Processing item: ' || v_items(i).cart_item_id || 
                             ' for user: ' || v_items(i).user_id || 
                             ' email: ' || v_items(i).email);
    
        -- Insert into notification queue table for email microservice to process
        INSERT INTO email_notifications (
            notification_id,
            user_id,
            email,
            firstname,
            lastname,
            subject,
            notification_type,
            ProductID,
            created_date
        ) VALUES (
            email_notification_seq.NEXTVAL,
            v_items(i).user_id,
            v_items(i).email,
            v_items(i).firstname,
            v_items(i).lastname,
            'Items removed from your cart (TEST)',
            'CART_ITEM_EXPIRED',
            v_items(i).ProductID,
            CURRENT_TIMESTAMP
        );
        
        -- Delete the item
        DELETE FROM cart_items WHERE cart_item_id = v_items(i).cart_item_id;
    END LOOP;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Error in cleanup_old_cart_items: ' || SQLERRM);
        RAISE;
END cleanup_old_cart_items;
/

-- Procedure to check product availability
CREATE OR REPLACE PROCEDURE check_product_availability(
    p_product_id IN NUMBER,
    p_required_amount IN NUMBER,
    p_is_available OUT NUMBER,
    p_available_quantity OUT NUMBER
)
AS
    v_available_quantity NUMBER;
BEGIN
    -- Get the available quantity for the product
    SELECT Amount INTO v_available_quantity
    FROM Products
    WHERE ProductID = p_product_id;

    -- Check if the required amount is available
    IF v_available_quantity >= p_required_amount THEN
        p_is_available := 1; -- Available
    ELSE
        p_is_available := 0; -- Not available
    END IF;

    -- Return the available quantity
    p_available_quantity := v_available_quantity;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_is_available := 0;
        p_available_quantity := 0;
    WHEN OTHERS THEN
        p_is_available := 0;
        p_available_quantity := 0;
        RAISE;
END check_product_availability;
/

-- Fixed search_products procedure with corrected LIKE condition
CREATE OR REPLACE PROCEDURE search_products(
    searchtext IN VARCHAR2,
    productcursor OUT SYS_REFCURSOR
)
AS
BEGIN
    OPEN productcursor FOR
        SELECT 
            ProductName
        FROM 
            Products
        WHERE 
            UPPER(ProductName) LIKE '%' || UPPER(searchtext) || '%'
            OR UPPER(Brand) LIKE '%' || UPPER(searchtext) || '%'
            OR UPPER(Description) LIKE '%' || UPPER(searchtext) || '%'
            OR UPPER(Tags) LIKE '%' || UPPER(searchtext) || '%';
END search_products;
/

-- =============================================
-- Scheduled Job
-- =============================================

BEGIN
    BEGIN
        DBMS_SCHEDULER.DROP_JOB('CART_CLEANUP_JOB');
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE != -27475 THEN -- ORA-27475: "job_name" is not a job
                RAISE;
            END IF;
    END;
    
    -- Create a new job that runs every 2 minutes
    DBMS_SCHEDULER.CREATE_JOB (
        job_name        => 'CART_CLEANUP_JOB',
        job_type        => 'STORED_PROCEDURE',
        job_action      => 'cleanup_old_cart_items',
        start_date      => SYSTIMESTAMP,
        repeat_interval => 'FREQ=MINUTELY; INTERVAL=2', -- Run every 2 minutes
        enabled         => TRUE,
        comments        => 'TEST JOB: Clean up cart items older than 2 minutes'
    );
    
    DBMS_OUTPUT.PUT_LINE('Scheduler job updated to run every 2 minutes');
END;
/

-- =============================================
-- Insert Sample Data
-- =============================================

);

-- =============================================
-- Verify
-- =============================================
SELECT * FROM user_accounts;
SELECT * FROM products;
SELECT * FROM cart_items;
SELECT * FROM carts;
SELECT * FROM email_notifications;