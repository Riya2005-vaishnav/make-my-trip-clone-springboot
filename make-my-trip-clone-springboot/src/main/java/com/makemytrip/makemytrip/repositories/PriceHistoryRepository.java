package com.makemytrip.makemytrip.repositories;

import com.makemytrip.makemytrip.models.PriceHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PriceHistoryRepository extends MongoRepository<PriceHistory, String> {
    List<PriceHistory> findTop30ByItemTypeAndItemIdOrderByRecordedAtDesc(String itemType, String itemId);

    Optional<PriceHistory> findTopByItemTypeAndItemIdOrderByRecordedAtDesc(String itemType, String itemId);
}
