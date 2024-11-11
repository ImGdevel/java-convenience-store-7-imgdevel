package store.domain;

import store.utils.ErrorMessages;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private List<ProductOrder> totalProductOrders;
    private List<ProductOrder> promotionalProductOrders;
    private boolean isMembershipApplied;

    public Order(List<ProductOrder> totalProductOrders, List<ProductOrder> promotionalProductOrders) {
        this.totalProductOrders = totalProductOrders;
        this.promotionalProductOrders = promotionalProductOrders;
        this.isMembershipApplied = false;
    }

    public List<ProductOrder> getTotalProductOrders() {
        return totalProductOrders;
    }

    public List<ProductOrder> getPromotionalProductOrders() {
        return promotionalProductOrders;
    }

    public boolean isMembershipApplied() {
        return isMembershipApplied;
    }

    public void setMembershipApplied(boolean membershipApplied) {
        isMembershipApplied = membershipApplied;
    }

    // 프로모션 상품 주문 추가
    public void addPromotionalProductOrder(String productName, int quantity){
        if (quantity <= 0) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_RANGE);
        }
        for (ProductOrder order : totalProductOrders) {
            if (order.getProductName().equals(productName)) {
                order.addQuantity(quantity);
            }
        }

        for (ProductOrder order : promotionalProductOrders) {
            if (order.getProductName().equals(productName)) {
                order.addQuantity(quantity);
                return;
            }
        }
        promotionalProductOrders.add(new ProductOrder(productName, quantity));
    }

    // 총 상품 주문 수량 감소
    public void reduceTotalProductOrder(String productName, int quantity){
        if (quantity <= 0) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_RANGE);
        }

        for (ProductOrder order : totalProductOrders) {
            if (order.getProductName().equals(productName)) {
                if (order.getQuantity() < quantity) {
                    throw new IllegalArgumentException(ErrorMessages.OUT_OF_STOCK);
                }
                order.reduceQuantity(quantity);
                if (order.getQuantity() == 0) {
                    totalProductOrders.remove(order);
                }
                return;
            }
        }
        throw new IllegalArgumentException(ErrorMessages.PRODUCT_NOT_FOUND);
    }
}
