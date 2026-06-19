package com.makemytrip.makemytrip.config;

import com.makemytrip.makemytrip.services.LiveFlightStatusService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Drives the simulated "real-time" polling loop.
 *
 * Every 30 seconds the scheduler asks {@link LiveFlightStatusService} to
 * refresh every actively-tracked flight.  Status changes automatically
 * trigger push notifications to all watching users.
 *
 * Adjust {@code fixedRate} (milliseconds) to taste:
 *   30 000  → demo-friendly (lots of updates, good for live demos)
 *  300 000  → production-like (realistic 5-min polling interval)
 */
@Configuration
@EnableScheduling
public class FlightStatusScheduler {

    private final LiveFlightStatusService liveFlightStatusService;

    public FlightStatusScheduler(LiveFlightStatusService liveFlightStatusService) {
        this.liveFlightStatusService = liveFlightStatusService;
    }

    /**
     * Polls all tracked flights every 30 seconds.
     * The initial delay of 10 s lets the application finish start-up first.
     */
    @Scheduled(fixedRate = 30_000, initialDelay = 10_000)
    public void refreshFlightStatuses() {
        System.out.println("[FlightStatusScheduler] Refreshing live flight statuses...");
        try {
            liveFlightStatusService.refreshAllTrackedFlights();
        } catch (Exception ex) {
            System.err.println("[FlightStatusScheduler] Unexpected error: " + ex.getMessage());
        }
    }
}
