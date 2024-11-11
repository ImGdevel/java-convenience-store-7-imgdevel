package store.Service;

import store.domain.Order;
import store.domain.Product;
import store.domain.ProductOrder;
import store.domain.Receipt;
import store.domain.ReceiptProduct;
import store.repository.InventoryManager;

import java.util.ArrayList;
import java.util.List;

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
        for (ProductOrder totalOrder : order.getTotalProductOrders()) {
            processStockForOrder(totalOrder);
        }
    }

    private void processStockForOrder(ProductOrder totalOrder) {
        String productName = totalOrder.getProductName();
        int totalQuantity = totalOrder.getQuantity();

        int promoStock = inventoryManager.getProduct(productName).getPromotionStock();
        int promoQuantity = Math.min(totalQuantity, promoStock);

        reduceStock(productName, promoQuantity, true);

        int remainingQuantity = totalQuantity - promoQuantity;
        if (remainingQuantity > 0) {
            reduceStock(productName, remainingQuantity, false);
        }
    }

    private void reduceStock(String productName, int quantity, boolean isPromotion) {
        if (quantity > 0) {
            inventoryManager.reduceProductStock(productName, quantity, isPromotion);
        }
    }

    private Receipt generateReceipt(Order order) {
        List<ReceiptProduct> productOrders = mappingReceiptProduct(order.getTotalProductOrders());
        List<ReceiptProduct> promotionalProductOrders = mappingReceiptProduct(order.getPromotionalProductOrders());
        int totalAmount = calculateAmount(productOrders);
        int promotionDiscount = calculateAmount(promotionalProductOrders);
        int membershipDiscount = calculateMembershipDiscount(order.isMembershipApplied(),productOrders);
        int finalAmount = totalAmount - promotionDiscount - membershipDiscount;
        return new Receipt(
                productOrders,
                promotionalProductOrders,
                totalAmount,
                promotionDiscount,
                membershipDiscount,
                finalAmount
        );
    }

    private  List<ReceiptProduct> mappingReceiptProduct(List<ProductOrder> orders){
        List<ReceiptProduct> products = new ArrayList<>();
        for(ProductOrder order : orders){
            Product product = inventoryManager.getProduct(order.getProductName());

            products.add(new ReceiptProduct(product.getName(), order.getQuantity(), product.getPrice()));
        }
        return products;
    }

    private int calculateAmount(List<ReceiptProduct> orders) {
        return orders.stream().mapToInt(order -> order.getTotalAmount()).sum();
    }

    private int calculateMembershipDiscount(boolean isMembershipApplied, List<ReceiptProduct> productOrders) {
        if (!isMembershipApplied) {
            return 0;
        }

        int nonPromotionAmount = productOrders.stream()
            .filter(order -> !inventoryManager.getProduct(order.getProductName()).isPromotionAvailable())
            .mapToInt(ReceiptProduct::getTotalAmount)
            .sum();

        return Math.min(nonPromotionAmount * 30 / 100, 8000);
    }

}
