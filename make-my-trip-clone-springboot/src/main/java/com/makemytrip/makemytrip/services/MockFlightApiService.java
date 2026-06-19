package com.makemytrip.makemytrip.services;

import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.FlightStatus;
import com.makemytrip.makemytrip.models.FlightStatusUpdate;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

/**
 * Simulates the behaviour of a real-time flight tracking API (e.g. AviationStack,
 * FlightAware).  In production you would replace the body of
 * {@link #fetchLiveStatus(Flight)} with an HTTP call to the real provider.
 *
 * The mock cycles through realistic scenarios so the dashboard shows varied,
 * believable updates every time the scheduler fires.
 */
@Service
public class MockFlightApiService {

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm");

    /** Possible delay reasons — mirrors what real airlines cite */
    private static final List<String> DELAY_REASONS = List.of(
            "Air traffic congestion at origin airport",
            "Late arriving aircraft from previous sector",
            "Crew scheduling adjustment",
            "Technical inspection required",
            "Weather conditions at destination",
            "Runway maintenance causing reduced capacity",
            "Fueling delay due to ground crew queue",
            "Waiting for connecting passengers"
    );

    private static final List<String> GATES     = List.of("A1","A4","B2","B7","C3","C9","D5","D11");
    private static final List<String> TERMINALS = List.of("T1","T2","T3");

    private final Random random = new Random();

    /**
     * Returns a freshly simulated {@link FlightStatusUpdate} for {@code flight}.
     *
     * The scenario probabilities mirror typical airline on-time performance:
     * ~60 % on-time, ~25 % delayed, ~10 % boarding, ~3 % landed, ~2 % cancelled.
     */
    public FlightStatusUpdate fetchLiveStatus(Flight flight) {
        int roll = random.nextInt(100);

        FlightStatus newStatus;
        if      (roll < 60) newStatus = FlightStatus.ON_TIME;
        else if (roll < 85) newStatus = FlightStatus.DELAYED;
        else if (roll < 95) newStatus = FlightStatus.BOARDING;
        else if (roll < 98) newStatus = FlightStatus.LANDED;
        else                newStatus = FlightStatus.CANCELLED;

        return buildUpdate(flight, newStatus);
    }

    /**
     * Builds a status update for a specific (manually chosen) status.
     * Used by the admin simulate endpoint so testers can force any scenario.
     */
    public FlightStatusUpdate buildManualUpdate(Flight flight,
                                                FlightStatus status,
                                                String overrideReason,
                                                int overrideDelayMinutes,
                                                String gate,
                                                String terminal) {
        FlightStatusUpdate u = buildUpdate(flight, status);
        if (overrideReason    != null) u.setDelayReason(overrideReason);
        if (overrideDelayMinutes > 0)  u.setDelayMinutes(overrideDelayMinutes);
        if (gate              != null) u.setGate(gate);
        if (terminal          != null) u.setTerminal(terminal);
        // Recalculate revised times with the override delay
        if (overrideDelayMinutes > 0 && flight.getDepartureTime() != null) {
            u.setRevisedDeparture(addMinutes(flight.getDepartureTime(), overrideDelayMinutes));
            if (flight.getArrivalTime() != null)
                u.setEstimatedArrival(addMinutes(flight.getArrivalTime(), overrideDelayMinutes));
        }
        return u;
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private FlightStatusUpdate buildUpdate(Flight flight, FlightStatus status) {
        FlightStatusUpdate u = new FlightStatusUpdate();
        u.setFlightId(flight.getId());
        u.setFlightNumber(flight.getFlightName());
        u.setStatus(status);
        u.setScheduledDeparture(flight.getDepartureTime());
        u.setScheduledArrival(flight.getArrivalTime());
        u.setGate(randomGate());
        u.setTerminal(randomTerminal());

        switch (status) {
            case ON_TIME -> {
                u.setDelayMinutes(0);
                u.setRevisedDeparture(flight.getDepartureTime());
                u.setEstimatedArrival(flight.getArrivalTime());
            }
            case DELAYED -> {
                int delay = randomDelayMinutes();
                u.setDelayMinutes(delay);
                u.setDelayReason(randomDelayReason());
                u.setRevisedDeparture(addMinutes(flight.getDepartureTime(), delay));
                u.setEstimatedArrival(addMinutes(flight.getArrivalTime(), delay));
            }
            case BOARDING -> {
                u.setDelayMinutes(0);
                u.setRevisedDeparture(flight.getDepartureTime());
                u.setEstimatedArrival(flight.getArrivalTime());
            }
            case CANCELLED -> {
                u.setDelayReason(randomDelayReason());
                u.setDelayMinutes(0);
            }
            case LANDED -> {
                // Simulate a slight early/late arrival
                int variance = random.nextInt(21) - 10; // -10..+10 min
                u.setDelayMinutes(Math.max(0, variance));
                u.setEstimatedArrival(addMinutes(flight.getArrivalTime(), variance));
            }
        }
        return u;
    }

    private int randomDelayMinutes() {
        // 15, 30, 45, 60, 90, 120 — realistic increments
        int[] options = {15, 30, 45, 60, 90, 120};
        return options[random.nextInt(options.length)];
    }

    private String randomDelayReason() {
        return DELAY_REASONS.get(random.nextInt(DELAY_REASONS.size()));
    }

    private String randomGate() {
        return GATES.get(random.nextInt(GATES.size()));
    }

    private String randomTerminal() {
        return TERMINALS.get(random.nextInt(TERMINALS.size()));
    }

    /**
     * Adds {@code minutes} to a time string formatted as "HH:mm".
     * Falls back to the original string on any parse error so nothing crashes.
     */
    private String addMinutes(String timeStr, int minutes) {
        if (timeStr == null || timeStr.isBlank()) return timeStr;
        try {
            LocalTime t = LocalTime.parse(timeStr, TIME_FMT);
            return t.plusMinutes(minutes).format(TIME_FMT);
        } catch (Exception e) {
            return timeStr;
        }
    }
}
