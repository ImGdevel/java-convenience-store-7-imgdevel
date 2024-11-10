package store.Service;

import store.domain.Order;
import store.domain.Product;
import store.domain.ProductOrder;
import store.domain.Receipt;
import store.repository.InventoryManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutService {

    private final InventoryManager inventoryManager;

    public CheckoutService(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    public Receipt processOrder(Order order) {
        updateStock(order);
        return generateReceipt(order);
    }

    private void updateStock(Order order) {
        Map<String, Integer> promoOrderMap = createPromoOrderMap(order.getPromotionalProductOrders());
        for (ProductOrder totalOrder : order.getTotalProductOrders()) {
            processStockForOrder(totalOrder, promoOrderMap);
        }
    }

    private Map<String, Integer> createPromoOrderMap(List<ProductOrder> promoOrders) {
        Map<String, Integer> promoOrderMap = new HashMap<>();
        for (ProductOrder promoOrder : promoOrders) {
            promoOrderMap.put(promoOrder.getProductName(), promoOrder.getQuantity());
        }
        return promoOrderMap;
    }

    private void processStockForOrder(ProductOrder totalOrder, Map<String, Integer> promoOrderMap) {
        String productName = totalOrder.getProductName();
        int primaryQuantity = calculatePrimaryQuantity(totalOrder.getQuantity(), productName, promoOrderMap);
        reduceStock(productName, primaryQuantity, false);

        int promoQuantity = promoOrderMap.getOrDefault(productName, 0);
        if (promoQuantity > 0) {
            reduceStock(productName, promoQuantity, true);
        }
    }

    private int calculatePrimaryQuantity(int totalQuantity, String productName, Map<String, Integer> promoOrderMap) {
        return totalQuantity - promoOrderMap.getOrDefault(productName, 0);
    }

    private void reduceStock(String productName, int quantity, boolean isPromotion) {
        if (quantity > 0) {
            inventoryManager.reduceProductStock(productName, quantity, isPromotion);
        }
    }

    private Receipt generateReceipt(Order order) {
        int totalAmount = calculateTotalAmount(order.getTotalProductOrders());
        int promotionDiscount = calculatePromotionDiscount(order.getPromotionalProductOrders());
        int membershipDiscount = calculateMembershipDiscount(order.isMembershipApplied(), totalAmount, promotionDiscount);
        int finalAmount = totalAmount - promotionDiscount - membershipDiscount;

        return new Receipt(
                order.getTotalProductOrders(),
                order.getPromotionalProductOrders(),
                totalAmount,
                promotionDiscount,
                membershipDiscount,
                finalAmount
        );
    }

    private int calculateTotalAmount(List<ProductOrder> totalOrders) {
        return calculateAmount(totalOrders);
    }

    private int calculatePromotionDiscount(List<ProductOrder> promoOrders) {
        return calculateAmount(promoOrders);
    }

    private int calculateAmount(List<ProductOrder> orders) {
        return orders.stream()
            .mapToInt(order -> {
                Product product = inventoryManager.getProduct(order.getProductName());
                return order.getQuantity() * product.getPrice();
            })
            .sum();
    }

    private int calculateMembershipDiscount(boolean isMembershipApplied, int totalAmount, int promotionDiscount) {
        if (!isMembershipApplied) {
            return 0;
        }
        return Math.min((totalAmount - promotionDiscount) * 30 / 100, 8000);
    }
}
