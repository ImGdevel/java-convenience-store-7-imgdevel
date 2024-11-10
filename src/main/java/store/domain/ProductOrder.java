package store.domain;

import java.util.ArrayList;
import java.util.List;

public class ProductOrder {
    private String productName;
    private int quantity;

    public ProductOrder(String productName, int quantity) {
        if (productName == null || productName.isEmpty()) {
            throw new IllegalArgumentException("상품명은 비어 있을 수 없습니다.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }
        this.productName = productName;
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int additionalQuantity) {
        if (additionalQuantity < 0) {
            throw new IllegalArgumentException("추가 수량은 음수가 될 수 없습니다.");
        }
        this.quantity += additionalQuantity;
    }

}
