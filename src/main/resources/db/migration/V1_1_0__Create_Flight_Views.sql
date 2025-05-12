CREATE OR REPLACE VIEW flight_complete_view AS
SELECT f.flight_id,
       f.flight_number,
       f.departure_time,
       f.arrival_time,
       f.base_price,
       f.status_id,

       -- Airline details
       a.airline_id,
       a.name         AS airline_name,
       a.iata_code    AS airline_iata,
       a.icao_code    AS airline_icao,
       a.country      AS airline_country,
       a.is_active    AS airline_active,

       -- Departure airport
       dep.airport_id AS departure_airport_id,
       dep.name       AS departure_airport_name,
       dep.city       AS departure_city,
       dep.country    AS departure_country,
       dep.iata_code  AS departure_iata,
       dep.icao_code  AS departure_icao,
       dep.timezone   AS departure_timezone,

       -- Arrival airport
       arr.airport_id AS arrival_airport_id,
       arr.name       AS arrival_airport_name,
       arr.city       AS arrival_city,
       arr.country    AS arrival_country,
       arr.iata_code  AS arrival_iata,
       arr.icao_code  AS arrival_icao,
       arr.timezone   AS arrival_timezone,

       -- Flight status
       fs.status_name

FROM flights f
         JOIN airlines a ON f.airline_id = a.airline_id
         JOIN airports dep ON f.departure_airport_id = dep.airport_id
         JOIN airports arr ON f.arrival_airport_id = arr.airport_id
         JOIN flight_statuses fs ON f.status_id = fs.status_id;