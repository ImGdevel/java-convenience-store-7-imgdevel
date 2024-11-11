package store.domain;

public class ReceiptProduct {
    private String productName;
    private int quantity;
    private int price;

    public ReceiptProduct(String productName, int quantity, int price){
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getProductName() {
        return productName;
    }

    public int getTotalAmount(){
        return price * quantity;
    }

}
