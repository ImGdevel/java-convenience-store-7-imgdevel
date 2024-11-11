package store.Service;

import camp.nextstep.edu.missionutils.DateTimes;
import store.domain.Product;
import store.domain.ProductOrder;
import store.domain.Promotion;
import store.repository.InventoryManager;
import store.repository.PromotionManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PromotionService {
    private InventoryManager inventoryManager;
    private PromotionManager promotionManager;

    public PromotionService(InventoryManager inventoryManager, PromotionManager promotionManager) {
        this.inventoryManager = inventoryManager;
        this.promotionManager = promotionManager;
    }

    public List<ProductOrder> getPromotionProduct(List<ProductOrder> productOrders){
        List<ProductOrder> products = new ArrayList<>();
        LocalDate nowDate = DateTimes.now().toLocalDate();

        for (ProductOrder order : productOrders) {
            if (!isPromotionEligible(order, nowDate)) continue;
            int promotionQuantity = calculatePromotionQuantity(order);
            if (promotionQuantity > 0) products.add(new ProductOrder(order.getProductName(), promotionQuantity));
        }
        return products;
    }

    public List<ProductOrder> checkFreePromotionProduct(List<ProductOrder> productOrders) {
        List<ProductOrder> products = new ArrayList<>();
        LocalDate nowDate = DateTimes.now().toLocalDate();

        for (ProductOrder order : productOrders) {
            if (isEligibleForFreePromotion(order, nowDate)) {
                products.add(createFreePromotionOrder(order));
            }
        }
        return products;
    }

    public List<ProductOrder> checkWithoutPromotionStock(List<ProductOrder> productOrders) {
        List<ProductOrder> products = new ArrayList<>();
        LocalDate nowDate = DateTimes.now().toLocalDate();

        for (ProductOrder order : productOrders) {
            if (isEligibleForRegularPurchase(order, nowDate)) {
                products.add(createRegularPurchaseOrder(order));
            }
        }
        return products;
    }

    private boolean isPromotionEligible(ProductOrder order, LocalDate nowDate) {
        Product product = inventoryManager.getProduct(order.getProductName());
        Promotion promotion = promotionManager.getPromotion(product.getPromotion());

        return product.isPromotionAvailable() && promotion.isPromotionActive(nowDate);
    }

    private int calculatePromotionQuantity(ProductOrder order) {
        Product product = inventoryManager.getProduct(order.getProductName());
        Promotion promotion = promotionManager.getPromotion(product.getPromotion());

        int promotionalUnits = promotion.getPurchaseQuantity() + promotion.getRewardQuantity();
        int promotionQuantity = Math.min(order.getQuantity() / promotionalUnits, product.getPromotionStock());

        return promotionQuantity;
    }

    private boolean isEligibleForFreePromotion(ProductOrder order, LocalDate nowDate) {
        Product product = inventoryManager.getProduct(order.getProductName());
        Promotion promotion = promotionManager.getPromotion(product.getPromotion());

        return product.isPromotionAvailable() &&
               promotion.isPromotionActive(nowDate) &&
               hasEnoughPromotionStock(order, product, promotion) &&
               meetsPromotionRequirements(order, promotion);
    }

    private boolean hasEnoughPromotionStock(ProductOrder order, Product product, Promotion promotion) {
        return product.getPromotionStock() >= (order.getQuantity() + promotion.getRewardQuantity());
    }

    private boolean meetsPromotionRequirements(ProductOrder order, Promotion promotion) {
        int promotionalUnits = promotion.getPurchaseQuantity() + promotion.getRewardQuantity();
        return order.getQuantity() / promotionalUnits > 0 &&
               (order.getQuantity() + promotion.getRewardQuantity()) % promotionalUnits == 0;
    }

    private ProductOrder createFreePromotionOrder(ProductOrder order) {
        Product product = inventoryManager.getProduct(order.getProductName());
        int rewardQuantity = promotionManager.getPromotion(product.getPromotion()).getRewardQuantity();

        return new ProductOrder(product.getName(), rewardQuantity);
    }

    private boolean isEligibleForRegularPurchase(ProductOrder order, LocalDate nowDate) {
        Product product = inventoryManager.getProduct(order.getProductName());
        Promotion promotion = promotionManager.getPromotion(product.getPromotion());

        return product.isPromotionAvailable() &&
               promotion.isPromotionActive(nowDate) &&
               order.getQuantity() > calculateMaxPromotionUnits(product, promotion) &&
               product.isStockAvailable(promotion.getPurchaseQuantity() + promotion.getRewardQuantity());
    }

    private int calculateMaxPromotionUnits(Product product, Promotion promotion) {
        int promotionalUnits = promotion.getPurchaseQuantity() + promotion.getRewardQuantity();
        int maxPromotionApplies = product.getPromotionStock() / promotionalUnits;

        return maxPromotionApplies * promotionalUnits;
    }

    private ProductOrder createRegularPurchaseOrder(ProductOrder order) {
        int regularPurchaseQuantity = order.getQuantity() -
                                      calculateMaxPromotionUnits(inventoryManager.getProduct(order.getProductName()),
                                                                 promotionManager.getPromotion(
                                                                     inventoryManager.getProduct(order.getProductName())
                                                                         .getPromotion()));
        return new ProductOrder(order.getProductName(), regularPurchaseQuantity);
    }
}
