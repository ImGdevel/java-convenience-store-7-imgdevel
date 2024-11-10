package store.Service;

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

    public List<ProductOrder> checkFreePromotionProduct(List<ProductOrder> productOrders) {
        List<ProductOrder> products = new ArrayList<>();
        LocalDate nowDate = LocalDate.now();

        for (ProductOrder order : productOrders) {
            if (isEligibleForFreePromotion(order, nowDate)) {
                products.add(createFreePromotionOrder(order));
            }
        }
        return products;
    }

    public List<ProductOrder> checkWithoutPromotionStock(List<ProductOrder> productOrders) {
        List<ProductOrder> products = new ArrayList<>();
        LocalDate nowDate = LocalDate.now();

        for (ProductOrder order : productOrders) {
            if (isEligibleForRegularPurchase(order, nowDate)) {
                products.add(createRegularPurchaseOrder(order));
            }
        }
        return products;
    }


    private boolean isEligibleForFreePromotion(ProductOrder order, LocalDate nowDate) {
        Product product = inventoryManager.getProduct(order.getProductName());
        if (!product.isPromotionAvailable()) {
            return false;
        }
        Promotion promotion = promotionManager.getPromotion(product.getPromotion());
        if (!promotion.isPromotionActive(nowDate) ||
            !hasEnoughPromotionStock(order, product, promotion)) {
            return false;
        }
        int promotionalUnits = promotion.getPurchaseQuantity() + promotion.getRewardQuantity();
        if( order.getQuantity() / promotionalUnits == 0 ||
                (order.getQuantity() + promotion.getRewardQuantity()) % promotionalUnits != 0 ){
            return false;
        }
        return true;
    }

    private boolean hasEnoughPromotionStock(ProductOrder order, Product product, Promotion promotion) {
        return product.getPromotionStock() >= (order.getQuantity() + promotion.getRewardQuantity());
    }

    private ProductOrder createFreePromotionOrder(ProductOrder order) {
        Product product = inventoryManager.getProduct(order.getProductName());
        int rewardQuantity = promotionManager.getPromotion(product.getPromotion()).getRewardQuantity();
        return new ProductOrder(product.getName(), rewardQuantity);
    }

    private boolean isEligibleForRegularPurchase(ProductOrder order, LocalDate nowDate) {
        Product product = inventoryManager.getProduct(order.getProductName());
        if (!product.isPromotionAvailable()) {
            return false;
        }
        Promotion promotion = promotionManager.getPromotion(product.getPromotion());
        if (!promotion.isPromotionActive(nowDate)) {
            return false;
        }
        if(order.getQuantity() <= calculateMaxPromotionUnits(product, promotion)){
            return false;
        }
        if(!product.isStockAvailable(promotion.getPurchaseQuantity() + promotion.getRewardQuantity())){
            return false;
        }
        return true;
    }


    private int calculateMaxPromotionUnits(Product product, Promotion promotion) {
        int promotionalUnits = promotion.getPurchaseQuantity() + promotion.getRewardQuantity();
        int maxPromotionApplies = product.getPromotionStock() / promotionalUnits;
        return maxPromotionApplies * promotionalUnits;
    }

    private ProductOrder createRegularPurchaseOrder(ProductOrder order) {
        int regularPurchaseQuantity = order.getQuantity() - calculateMaxPromotionUnits(inventoryManager.getProduct(order.getProductName()), promotionManager.getPromotion(inventoryManager.getProduct(order.getProductName()).getPromotion()));
        return new ProductOrder(order.getProductName(), regularPurchaseQuantity);
    }
}
