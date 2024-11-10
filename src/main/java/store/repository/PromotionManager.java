package store.repository;

import store.domain.Promotion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromotionManager {
    private Map<String, Promotion> promotions = new HashMap<>();

    public PromotionManager(List<Promotion> promotions) {
        for (Promotion promotion : promotions) {
            this.promotions.put(promotion.getName(), promotion);
        }
    }

    public Promotion getPromotion(String name) {
        return promotions.get(name);
    }
}
