package store.repository;

import store.domain.Promotion;
import store.infra.PromotionDataLoader;
import store.utils.ErrorMessages;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromotionManager {
    private Map<String, Promotion> promotions = new HashMap<>();

    // PromotionDataLoader를 주입받아 초기화하는 생성자
    public PromotionManager(PromotionDataLoader dataLoader) {
        try {
            List<Promotion> promotionsList = dataLoader.loadPromotions();
            initializePromotions(promotionsList);
        } catch (IOException e) {
            throw new IllegalStateException(ErrorMessages.FAILED_INITIALIZE_PROMOTION, e);
        }
    }

    // 프로모션 데이터를 초기화하는 메서드
    private void initializePromotions(List<Promotion> promotionsList) {
        for (Promotion promotion : promotionsList) {
            this.promotions.put(promotion.getName(), promotion);
        }
    }

    public Promotion getPromotion(String name) {
        return promotions.get(name);
    }
}
