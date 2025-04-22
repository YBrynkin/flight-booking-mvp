--Initial script flight booking system project
CREATE TABLE users
(
    user_id       SERIAL PRIMARY KEY,
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255)        NOT NULL, -- Store hashed passwords only
    first_name    VARCHAR(50)         NOT NULL,
    last_name     VARCHAR(50)         NOT NULL,
    middle_name   VARCHAR(50),
    phone         VARCHAR(15)         NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login    TIMESTAMP
);

-- How to take into account email veriffication?
-- Future extension: add online status of a user

CREATE TABLE airlines
(
    airline_id SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    iata_code  VARCHAR(2) UNIQUE,
    icao_code  VARCHAR(3) UNIQUE,
    country    VARCHAR(50),
    is_active  BOOLEAN DEFAULT TRUE
);

-- Insert airlines
INSERT INTO airlines (name, iata_code, icao_code, country)
VALUES ('Aeroflot', 'SU', 'AFL', 'Russia'),
       ('S7 Airlines', 'S7', 'SBI', 'Russia'),
       ('Ural Airlines', 'U6', 'SVR', 'Russia'),
       ('Pobeda', 'DP', 'PBD', 'Russia'),
       ('Rossiya Airlines', 'FV', 'SDM', 'Russia'),
       ('Belavia', 'B2', 'BRU', 'Belarus'),
       ('Air Astana', 'KC', 'KZR', 'Kazakhstan'),
       ('Uzbekistan Airways', 'HY', 'UZB', 'Uzbekistan'),
       ('Azerbaijan Airlines', 'J2', 'AHY', 'Azerbaijan');

CREATE TABLE airports
(
    airport_id SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    city       VARCHAR(50)  NOT NULL,
    country    VARCHAR(50)  NOT NULL,
    iata_code  VARCHAR(3) UNIQUE,
    icao_code  VARCHAR(4) UNIQUE,
    timezone   VARCHAR(50)
);

-- Insert major airports
INSERT INTO airports (name, city, country, iata_code, icao_code, timezone)
VALUES ('Sheremetyevo International', 'Moscow', 'Russia', 'SVO', 'UUEE', 'Europe/Moscow'),
       ('Domodedovo International', 'Moscow', 'Russia', 'DME', 'UUDD', 'Europe/Moscow'),
       ('Pulkovo Airport', 'Saint Petersburg', 'Russia', 'LED', 'ULLI', 'Europe/Moscow'),
       ('Koltsovo Airport', 'Yekaterinburg', 'Russia', 'SVX', 'USSS', 'Asia/Yekaterinburg'),
       ('Almaty International', 'Almaty', 'Kazakhstan', 'ALA', 'UAAA', 'Asia/Almaty'),
       ('Tashkent International', 'Tashkent', 'Uzbekistan', 'TAS', 'UTTT', 'Asia/Tashkent'),
       ('Heydar Aliyev International', 'Baku', 'Azerbaijan', 'GYD', 'UBBB', 'Asia/Baku');

CREATE TABLE flight_statuses
(
    status_id   SERIAL PRIMARY KEY,
    status_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

INSERT INTO flight_statuses (status_name, description)
VALUES ('SCHEDULED', 'Flight is on time'),
       ('DELAYED', 'Flight is delayed'),
       ('CANCELLED', 'Flight is cancelled'),
       ('DEPARTED', 'Flight has taken off'),
       ('ARRIVED', 'Flight has landed');

CREATE TABLE flights
(
    flight_id            SERIAL PRIMARY KEY,
    flight_number        VARCHAR(10)    NOT NULL,
    airline_id           INTEGER REFERENCES airlines (airline_id),
    departure_airport_id INTEGER REFERENCES airports (airport_id),
    arrival_airport_id   INTEGER REFERENCES airports (airport_id),
    departure_time       TIMESTAMP      NOT NULL,
    arrival_time         TIMESTAMP      NOT NULL,
    base_price           DECIMAL(10, 2) NOT NULL,
    status_id            INTEGER        NOT NULL REFERENCES flight_statuses (status_id),
    CHECK (arrival_time > departure_time)
);

-- Sample flight data (realistic but fictional flights)
INSERT INTO flights (flight_number, airline_id, departure_airport_id, arrival_airport_id, departure_time, arrival_time,
                     base_price, status_id)
VALUES ('SU 1440', 1, 1, 3, '2023-12-15 08:30:00', '2023-12-15 10:00:00', 8500.00, 1),
       ('S7 4512', 2, 2, 4, '2023-12-16 12:15:00', '2023-12-16 17:45:00', 12000.00, 1),
       ('U6 789', 3, 4, 1, '2023-12-17 19:20:00', '2023-12-17 21:50:00', 9500.00, 1),
       ('KC 112', 7, 5, 1, '2023-12-18 06:00:00', '2023-12-18 08:30:00', 15000.00, 1),
       ('B2 881', 6, 7, 5, '2023-12-19 14:10:00', '2023-12-19 16:40:00', 11000.00, 1);

-- Future extension: Airport terminals, runway data

CREATE TABLE booking_statuses
(
    status_id   SERIAL PRIMARY KEY,
    status_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

INSERT INTO booking_statuses (status_name, description)
VALUES ('CONFIRMED', 'Booking is active'),
       ('CANCELLED', 'Booking was cancelled'),
       ('REFUNDED', 'Booking was refunded');

CREATE TABLE payment_statuses
(
    status_id   SERIAL PRIMARY KEY,
    status_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

INSERT INTO payment_statuses (status_name, description)
VALUES ('PENDING', 'Payment is being processed'),
       ('PAID', 'Payment succeeded'),
       ('FAILED', 'Payment failed'),
       ('REFUNDED', 'Payment was refunded');

CREATE TABLE bookings
(
    booking_id        SERIAL PRIMARY KEY,
    user_id           INTEGER REFERENCES users (user_id),
    flight_id         INTEGER REFERENCES flights (flight_id),
    booking_status_id INTEGER            NOT NULL REFERENCES booking_statuses (status_id),
    payment_status_id INTEGER            NOT NULL REFERENCES payment_statuses (status_id),
    booking_reference VARCHAR(10) UNIQUE NOT NULL,
    total_price       DECIMAL(10, 2)     NOT NULL,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);








