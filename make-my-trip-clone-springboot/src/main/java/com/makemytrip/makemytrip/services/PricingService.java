package com.makemytrip.makemytrip.services;

import com.makemytrip.makemytrip.dto.DynamicPriceResponse;
import com.makemytrip.makemytrip.dto.PriceFreezeResponse;
import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.models.PriceFreeze;
import com.makemytrip.makemytrip.models.PriceHistory;
import com.makemytrip.makemytrip.repositories.FlightRepository;
import com.makemytrip.makemytrip.repositories.HotelRepository;
import com.makemytrip.makemytrip.repositories.PriceFreezeRepository;
import com.makemytrip.makemytrip.repositories.PriceHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
public class PricingService {
    private static final String FLIGHT = "FLIGHT";
    private static final String HOTEL = "HOTEL";
    private static final int FREEZE_MINUTES = 15;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private PriceHistoryRepository priceHistoryRepository;

    @Autowired
    private PriceFreezeRepository priceFreezeRepository;

    public double calculateFlightPrice(Flight flight) {
        return calculateFlightPriceDetails(flight).getDynamicPrice();
    }

    public double calculateHotelPrice(Hotel hotel) {
        return calculateHotelPriceDetails(hotel).getDynamicPrice();
    }

    public DynamicPriceResponse calculateFlightPriceDetails(Flight flight) {
        double demandMultiplier = calculateDemandMultiplier(flight.getAvailableSeats(), 50, 20);
        double seasonalMultiplier = calculateSeasonalMultiplier(flight.getDepartureTime());
        return buildResponse(FLIGHT, flight.getId(), flight.getPrice(), demandMultiplier, seasonalMultiplier);
    }

    public DynamicPriceResponse calculateHotelPriceDetails(Hotel hotel) {
        double demandMultiplier = calculateDemandMultiplier(hotel.getAvailableRooms(), 20, 5);
        double seasonalMultiplier = calculateSeasonalMultiplier(null);
        return buildResponse(HOTEL, hotel.getId(), hotel.getPricePerNight(), demandMultiplier, seasonalMultiplier);
    }

    public DynamicPriceResponse getFlightPrice(String flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        DynamicPriceResponse response = calculateFlightPriceDetails(flight);
        recordPrice(response);
        return response;
    }

    public DynamicPriceResponse getHotelPrice(String hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        DynamicPriceResponse response = calculateHotelPriceDetails(hotel);
        recordPrice(response);
        return response;
    }

    public List<PriceHistory> getPriceHistory(String itemType, String itemId) {
        List<PriceHistory> history = priceHistoryRepository
                .findTop30ByItemTypeAndItemIdOrderByRecordedAtDesc(normalizeType(itemType), itemId);
        Collections.reverse(history);
        return history;
    }

    public PriceFreezeResponse freezeFlightPrice(String userId, String flightId, int quantity) {
        DynamicPriceResponse price = getFlightPrice(flightId);
        return createFreeze(userId, FLIGHT, flightId, quantity, price.getDynamicPrice());
    }

    public PriceFreezeResponse freezeHotelPrice(String userId, String hotelId, int quantity) {
        DynamicPriceResponse price = getHotelPrice(hotelId);
        return createFreeze(userId, HOTEL, hotelId, quantity, price.getDynamicPrice());
    }

    public PriceFreeze validateFreeze(String freezeId, String userId, String itemType, String itemId, int quantity) {
        PriceFreeze freeze = priceFreezeRepository.findById(freezeId)
                .orElseThrow(() -> new RuntimeException("Price freeze not found"));

        if (!freeze.isActive()) {
            throw new RuntimeException("Price freeze expired or already used");
        }
        if (!freeze.getUserId().equals(userId)
                || !freeze.getItemType().equals(normalizeType(itemType))
                || !freeze.getItemId().equals(itemId)
                || freeze.getQuantity() != quantity) {
            throw new RuntimeException("Price freeze does not match this booking");
        }
        return freeze;
    }

    public void markFreezeUsed(PriceFreeze freeze) {
        freeze.setUsed(true);
        priceFreezeRepository.save(freeze);
    }

    private DynamicPriceResponse buildResponse(String itemType, String itemId, double basePrice,
                                               double demandMultiplier, double seasonalMultiplier) {
        double dynamicPrice = roundMoney(basePrice * demandMultiplier * seasonalMultiplier);
        String reason = buildReason(demandMultiplier, seasonalMultiplier);
        return new DynamicPriceResponse(
                itemType,
                itemId,
                roundMoney(basePrice),
                dynamicPrice,
                demandMultiplier,
                seasonalMultiplier,
                reason,
                LocalDateTime.now()
        );
    }

    private double calculateDemandMultiplier(int availability, int healthyThreshold, int tightThreshold) {
        if (availability > healthyThreshold) {
            return 1.00;
        }
        if (availability >= tightThreshold) {
            return 1.10;
        }
        return 1.25;
    }

    private double calculateSeasonalMultiplier(String dateTimeText) {
        LocalDate travelDate = parseDate(dateTimeText);
        MonthDay travelDay = MonthDay.from(travelDate);

        boolean winterHoliday = !travelDay.isBefore(MonthDay.of(12, 20))
                || !travelDay.isAfter(MonthDay.of(1, 5));
        boolean summerPeak = !travelDay.isBefore(MonthDay.of(5, 1))
                && !travelDay.isAfter(MonthDay.of(6, 30));
        boolean weekend = travelDate.getDayOfWeek().getValue() >= 6;

        if (winterHoliday || summerPeak) {
            return 1.20;
        }
        if (weekend) {
            return 1.08;
        }
        return 1.00;
    }

    private LocalDate parseDate(String dateTimeText) {
        if (dateTimeText == null || dateTimeText.isBlank()) {
            return LocalDate.now();
        }
        try {
            return LocalDateTime.parse(dateTimeText).toLocalDate();
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDate.parse(dateTimeText);
            } catch (DateTimeParseException secondIgnored) {
                return LocalDate.now();
            }
        }
    }

    private String buildReason(double demandMultiplier, double seasonalMultiplier) {
        StringBuilder reason = new StringBuilder("Base fare");
        if (demandMultiplier > 1.0) {
            reason.append(String.format(Locale.US, " + %.0f%% demand adjustment", (demandMultiplier - 1) * 100));
        }
        if (seasonalMultiplier > 1.0) {
            reason.append(String.format(Locale.US, " + %.0f%% seasonal or peak-period adjustment", (seasonalMultiplier - 1) * 100));
        }
        if (demandMultiplier == 1.0 && seasonalMultiplier == 1.0) {
            reason.append(" with no current adjustment");
        }
        return reason.toString();
    }

    private void recordPrice(DynamicPriceResponse response) {
        PriceHistory latest = priceHistoryRepository
                .findTopByItemTypeAndItemIdOrderByRecordedAtDesc(response.getItemType(), response.getItemId())
                .orElse(null);

        boolean shouldRecord = latest == null
                || latest.getDynamicPrice() != response.getDynamicPrice()
                || latest.getRecordedAt().isBefore(LocalDateTime.now().minusMinutes(15));

        if (!shouldRecord) {
            return;
        }

        PriceHistory history = new PriceHistory();
        history.setItemType(response.getItemType());
        history.setItemId(response.getItemId());
        history.setBasePrice(response.getBasePrice());
        history.setDynamicPrice(response.getDynamicPrice());
        history.setDemandMultiplier(response.getDemandMultiplier());
        history.setSeasonalMultiplier(response.getSeasonalMultiplier());
        history.setReason(response.getReason());
        history.setRecordedAt(response.getCalculatedAt());
        priceHistoryRepository.save(history);
    }

    private PriceFreezeResponse createFreeze(String userId, String itemType, String itemId, int quantity, double unitPrice) {
        if (quantity < 1) {
            throw new RuntimeException("Quantity must be at least 1");
        }

        PriceFreeze freeze = new PriceFreeze();
        freeze.setUserId(userId);
        freeze.setItemType(itemType);
        freeze.setItemId(itemId);
        freeze.setQuantity(quantity);
        freeze.setLockedUnitPrice(roundMoney(unitPrice));
        freeze.setLockedTotalPrice(roundMoney(unitPrice * quantity));
        freeze.setCreatedAt(LocalDateTime.now());
        freeze.setExpiresAt(LocalDateTime.now().plusMinutes(FREEZE_MINUTES));
        freeze.setUsed(false);
        PriceFreeze saved = priceFreezeRepository.save(freeze);

        return new PriceFreezeResponse(
                saved.getId(),
                saved.getItemType(),
                saved.getItemId(),
                saved.getQuantity(),
                saved.getLockedUnitPrice(),
                saved.getLockedTotalPrice(),
                saved.getExpiresAt(),
                saved.isActive()
        );
    }

    private String normalizeType(String itemType) {
        return itemType == null ? "" : itemType.trim().toUpperCase(Locale.US);
    }

    private double roundMoney(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }
}
