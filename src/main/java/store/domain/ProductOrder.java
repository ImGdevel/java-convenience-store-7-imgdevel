package store.domain;


import store.utils.ErrorMessages;

public class ProductOrder {
    private String productName;
    private int quantity;

    public ProductOrder(String productName, int quantity) {
        if (productName == null || productName.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_INPUT);
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_RANGE);
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
            throw new IllegalArgumentException(ErrorMessages.INVALID_RANGE);
        }
        this.quantity += additionalQuantity;
    }

    public void reduceQuantity(int amount) {
        if (amount <= 0 || amount > quantity) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_RANGE);
        }
        this.quantity -= amount;
    }

}
