package com.seatreserve.busbooking.config;

import com.seatreserve.busbooking.model.entity.*;
import com.seatreserve.busbooking.model.enums.*;
import com.seatreserve.busbooking.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final VehicleRepository vehicleRepository;
    private final JourneyScheduleRepository journeyScheduleRepository;
    private final JourneySeatRepository journeySeatRepository;
    private final FareRuleRepository fareRuleRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      RouteRepository routeRepository,
                      VehicleRepository vehicleRepository,
                      JourneyScheduleRepository journeyScheduleRepository,
                      JourneySeatRepository journeySeatRepository,
                      FareRuleRepository fareRuleRepository,
                      BookingRepository bookingRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
        this.vehicleRepository = vehicleRepository;
        this.journeyScheduleRepository = journeyScheduleRepository;
        this.journeySeatRepository = journeySeatRepository;
        this.fareRuleRepository = fareRuleRepository;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        seedFareRules();
        if (routeRepository.count() == 0) {
            seedFullBusCatalog();
        }
    }

    private void seedUsers() {
        if (!userRepository.existsByEmail("admin@seatreserve.com")) {
            User admin = new User(
                    "admin@seatreserve.com",
                    passwordEncoder.encode("Admin@123"),
                    "System Administrator",
                    "+91 9876543210",
                    Role.ROLE_ADMIN
            );
            userRepository.save(admin);
        }

        if (!userRepository.existsByEmail("demo@seatreserve.com")) {
            User demo = new User(
                    "demo@seatreserve.com",
                    passwordEncoder.encode("Demo@123"),
                    "Naveen Yenuganti",
                    "+91 9988776655",
                    Role.ROLE_USER
            );
            userRepository.save(demo);
        }
    }

    private void seedFareRules() {
        if (fareRuleRepository.findByRuleName("DEFAULT_RULE").isEmpty()) {
            FareRule rule = new FareRule();
            rule.setRuleName("DEFAULT_RULE");
            rule.setTier1Hours(24);
            rule.setTier1FeePercent(10.0);
            rule.setTier2Hours(12);
            rule.setTier2FeePercent(25.0);
            rule.setTier3Hours(4);
            rule.setTier3FeePercent(50.0);
            rule.setTier4FeePercent(100.0);
            fareRuleRepository.save(rule);
        }
    }

    private void seedFullBusCatalog() {
        // 1. Create Routes
        Route r1 = new Route("Bangalore", "Hyderabad", 570.0, 9.0);
        r1.addStop(new RouteStop("Madiwala Hub", 1, StopType.PICKUP, 0, "Opposite Police Station"));
        r1.addStop(new RouteStop("Silk Board Junction", 2, StopType.PICKUP, 20, "Near Petrol Pump"));
        r1.addStop(new RouteStop("Electronic City Toll", 3, StopType.PICKUP, 45, "Flyover Exit"));
        r1.addStop(new RouteStop("Anantapur Bypass", 4, StopType.BOTH, 240, "Mid-way Refreshment Hotel"));
        r1.addStop(new RouteStop("Kurnool Highway", 5, StopType.BOTH, 360, "Toll Gate Point"));
        r1.addStop(new RouteStop("Shamshabad Airport Circle", 6, StopType.DROPOFF, 480, "Under Flyover"));
        r1.addStop(new RouteStop("Gachibowli Outer Ring", 7, StopType.DROPOFF, 510, "ORR Service Road"));
        r1.addStop(new RouteStop("Ameerpet Metro", 8, StopType.DROPOFF, 540, "Metro Pillar 1042"));
        r1 = routeRepository.save(r1);

        Route r2 = new Route("Bangalore", "Chennai", 350.0, 6.0);
        r2.addStop(new RouteStop("Majestic Kempegowda", 1, StopType.PICKUP, 0, "Platform 12"));
        r2.addStop(new RouteStop("Indiranagar 100ft Rd", 2, StopType.PICKUP, 25, "Domlur Bridge"));
        r2.addStop(new RouteStop("Hosur Bus Stand", 3, StopType.BOTH, 75, "Highway Motel"));
        r2.addStop(new RouteStop("Vellore New Bypass", 4, StopType.BOTH, 210, "Green Circle"));
        r2.addStop(new RouteStop("Sriperumbudur Toll", 5, StopType.DROPOFF, 300, "Highway Junction"));
        r2.addStop(new RouteStop("Koyambedu CMBT", 6, StopType.DROPOFF, 360, "Omni Bus Stand Gate 3"));
        r2 = routeRepository.save(r2);

        Route r3 = new Route("Mumbai", "Pune", 150.0, 3.5);
        r3.addStop(new RouteStop("Dadar Asiad Bus Stand", 1, StopType.PICKUP, 0, "Near Station"));
        r3.addStop(new RouteStop("Vashi Plaza", 2, StopType.PICKUP, 40, "Highway Bridge"));
        r3.addStop(new RouteStop("Kharghar Three Monkey Point", 3, StopType.PICKUP, 60, "Expressway Entry"));
        r3.addStop(new RouteStop("Lonavala Food Mall", 4, StopType.BOTH, 110, "Food Express Plaza"));
        r3.addStop(new RouteStop("Wakad Bridge", 5, StopType.DROPOFF, 180, "Hinjawadi Flyover"));
        r3.addStop(new RouteStop("Swargate Bus Stand", 6, StopType.DROPOFF, 210, "Terminal Platform 5"));
        r3 = routeRepository.save(r3);

        Route r4 = new Route("Delhi", "Jaipur", 280.0, 5.0);
        r4.addStop(new RouteStop("Kashmiri Gate ISBT", 1, StopType.PICKUP, 0, "Gate 2"));
        r4.addStop(new RouteStop("Dhaula Kuan Metro", 2, StopType.PICKUP, 35, "Under Ring Rd Flyover"));
        r4.addStop(new RouteStop("Gurgaon IFFCO Chowk", 3, StopType.PICKUP, 65, "Near Metro Gate 1"));
        r4.addStop(new RouteStop("Kotputli Highway", 4, StopType.BOTH, 180, "Highway King Dhaba"));
        r4.addStop(new RouteStop("Sindhi Camp Central", 5, StopType.DROPOFF, 300, "Bus Stand Platform 3"));
        r4 = routeRepository.save(r4);

        // 2. Create Luxury Vehicles with Detailed Seats
        Vehicle v1 = createSleeperBus("KA-01-AB-1001", "Orange Travels Volvo B11R AC Sleeper", BusType.LUXURY_MULTI_AXLE);
        v1 = vehicleRepository.save(v1);

        Vehicle v2 = createSemiSleeperBus("KA-05-MM-2020", "VRL Travels Scania Multi-Axle AC Semi-Sleeper", BusType.EXECUTIVE_VOLVO);
        v2 = vehicleRepository.save(v2);

        Vehicle v3 = createSleeperBus("MH-02-EE-3333", "IntrCity SmartBus AC Luxury Sleeper", BusType.AC_SLEEPER_2_PLUS_1);
        v3 = vehicleRepository.save(v3);

        // 3. Create Schedules for Today, Tomorrow, and Next 3 Days
        LocalDate today = LocalDate.now();
        List<JourneySchedule> schedules = new ArrayList<>();

        for (int dayOffset = 0; dayOffset <= 4; dayOffset++) {
            LocalDate travelDate = today.plusDays(dayOffset);

            // Bangalore -> Hyderabad (Volvo Sleeper 21:30)
            LocalDateTime dep1 = travelDate.atTime(21, 30);
            LocalDateTime arr1 = dep1.plusHours(9);
            schedules.add(createSchedule(r1, v1, dep1, arr1, 1.85, 5.0));

            // Bangalore -> Hyderabad (Scania Semi-Sleeper 07:00 Morning)
            LocalDateTime dep2 = travelDate.atTime(7, 0);
            LocalDateTime arr2 = dep2.plusHours(8).plusMinutes(30);
            schedules.add(createSchedule(r1, v2, dep2, arr2, 1.45, 5.0));

            // Bangalore -> Chennai (Scania Semi-Sleeper 06:30 & 14:00)
            LocalDateTime dep3 = travelDate.atTime(6, 30);
            LocalDateTime arr3 = dep3.plusHours(6);
            schedules.add(createSchedule(r2, v2, dep3, arr3, 1.55, 5.0));

            LocalDateTime dep4 = travelDate.atTime(14, 0);
            LocalDateTime arr4 = dep4.plusHours(6);
            schedules.add(createSchedule(r2, v1, dep4, arr4, 1.90, 5.0));

            // Mumbai -> Pune (Volvo 08:00 & 17:30)
            LocalDateTime dep5 = travelDate.atTime(8, 0);
            LocalDateTime arr5 = dep5.plusHours(3).plusMinutes(30);
            schedules.add(createSchedule(r3, v2, dep5, arr5, 2.20, 5.0));

            // Delhi -> Jaipur (Mercedes Sleeper 22:00)
            LocalDateTime dep6 = travelDate.atTime(22, 0);
            LocalDateTime arr6 = dep6.plusHours(5);
            schedules.add(createSchedule(r4, v3, dep6, arr6, 1.75, 5.0));
        }

        // 4. Create an initial sample confirmed booking for testing/demo lookup
        if (!schedules.isEmpty()) {
            createSampleBooking(schedules.get(0));
        }
    }

    private Vehicle createSleeperBus(String regNo, String name, BusType busType) {
        Vehicle v = new Vehicle(
                regNo, name, busType, 30, 5, 3, "2+1_SLEEPER",
                "WiFi,AC,Charging Point,Water Bottle,Reading Light,Blanket,Live Tracking"
        );

        // Lower Deck (15 berths: 5 rows x 3 berths)
        for (int r = 1; r <= 5; r++) {
            // Left Single Berth (Window)
            v.addSeat(new Seat("L" + r + "A", Deck.LOWER, SeatType.SLEEPER, SeatPosition.WINDOW, r, 1, 25.0));
            // Right Double Berths
            v.addSeat(new Seat("L" + r + "B", Deck.LOWER, SeatType.SLEEPER, SeatPosition.AISLE, r, 2, 20.0));
            v.addSeat(new Seat("L" + r + "C", Deck.LOWER, SeatType.SLEEPER, SeatPosition.WINDOW, r, 3, 22.0));
        }

        // Upper Deck (15 berths: 5 rows x 3 berths)
        for (int r = 1; r <= 5; r++) {
            // Left Single Berth (Upper Window)
            v.addSeat(new Seat("U" + r + "A", Deck.UPPER, SeatType.SLEEPER, SeatPosition.WINDOW, r, 1, 30.0));
            // Right Double Berths
            v.addSeat(new Seat("U" + r + "B", Deck.UPPER, SeatType.SLEEPER, SeatPosition.AISLE, r, 2, 22.0));
            v.addSeat(new Seat("U" + r + "C", Deck.UPPER, SeatType.SLEEPER, SeatPosition.WINDOW, r, 3, 26.0));
        }

        return v;
    }

    private Vehicle createSemiSleeperBus(String regNo, String name, BusType busType) {
        Vehicle v = new Vehicle(
                regNo, name, busType, 40, 10, 4, "2+2_SEATER",
                "WiFi,AC,Pushback Seats,Charging Point,Water Bottle,Reading Light"
        );

        // 10 Rows x 4 Seats (2+2 Layout)
        for (int r = 1; r <= 10; r++) {
            v.addSeat(new Seat(r + "A", Deck.LOWER, SeatType.SEATER, SeatPosition.WINDOW, r, 1, 5.0));
            v.addSeat(new Seat(r + "B", Deck.LOWER, SeatType.SEATER, SeatPosition.AISLE, r, 2, 0.0));
            v.addSeat(new Seat(r + "C", Deck.LOWER, SeatType.SEATER, SeatPosition.AISLE, r, 3, 0.0));
            v.addSeat(new Seat(r + "D", Deck.LOWER, SeatType.SEATER, SeatPosition.WINDOW, r, 4, 5.0));
        }

        return v;
    }

    private JourneySchedule createSchedule(Route route, Vehicle vehicle, LocalDateTime dep, LocalDateTime arr, double baseRate, double tax) {
        JourneySchedule schedule = new JourneySchedule(route, vehicle, dep, arr, baseRate, tax);
        schedule = journeyScheduleRepository.save(schedule);

        double baseFare = route.getDistanceKm() * baseRate;
        for (Seat seat : vehicle.getSeats()) {
            double surcharge = baseFare * (seat.getSurchargePercentage() / 100.0);
            double seatPrice = Math.round((baseFare + surcharge) * 100.0) / 100.0;
            JourneySeat js = new JourneySeat(schedule, seat, seatPrice);
            journeySeatRepository.save(js);
        }

        return schedule;
    }

    private void createSampleBooking(JourneySchedule schedule) {
        List<JourneySeat> seats = journeySeatRepository.findByJourneyScheduleIdOrderBySeat_DeckAscSeat_SeatRowAscSeat_SeatColAsc(schedule.getId());
        if (seats.size() < 2) return;

        JourneySeat js1 = seats.get(0);
        JourneySeat js2 = seats.get(1);
        js1.setStatus(SeatStatus.BOOKED);
        js2.setStatus(SeatStatus.BOOKED);
        journeySeatRepository.save(js1);
        journeySeatRepository.save(js2);

        User demoUser = userRepository.findByEmail("demo@seatreserve.com").orElse(null);

        Booking booking = new Booking();
        booking.setBookingReference("PNR-DEMO77");
        booking.setUser(demoUser);
        booking.setPassengerName("Naveen Yenuganti");
        booking.setPassengerEmail("demo@seatreserve.com");
        booking.setPassengerPhone("+91 9988776655");
        booking.setJourneySchedule(schedule);
        booking.setPickupStop("Madiwala Hub");
        booking.setDropoffStop("Gachibowli Outer Ring");
        booking.setBookingDateTime(LocalDateTime.now().minusHours(2));
        booking.setTotalDistanceKm(schedule.getRoute().getDistanceKm());
        booking.setBaseFare(js1.getPrice() + js2.getPrice());
        booking.setSeatSurcharges(50.0);
        booking.setTaxes(110.0);
        booking.setDiscountAmount(0.0);
        booking.setTotalFare(js1.getPrice() + js2.getPrice() + 110.0);
        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        BookingPassenger bp1 = new BookingPassenger(
                "Naveen Yenuganti", 24, "Male", js1.getSeat().getSeatNumber(), js1.getPrice(),
                "TKT-PNR-DEMO77-" + js1.getSeat().getSeatNumber(), js1
        );
        BookingPassenger bp2 = new BookingPassenger(
                "Harshith Kumar", 23, "Male", js2.getSeat().getSeatNumber(), js2.getPrice(),
                "TKT-PNR-DEMO77-" + js2.getSeat().getSeatNumber(), js2
        );

        booking.addPassenger(bp1);
        booking.addPassenger(bp2);

        bookingRepository.save(booking);
    }
}
