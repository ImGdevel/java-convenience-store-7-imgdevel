package store.domain;

public class Product {
    private String name;
    private int price;
    private int stock;
    private String promotion;

    public Product(String name, int price, int stock, String promotion) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }
    public int getPrice() {
        return price;
    }
    public int getStock() {
        return stock;
    }
    public String getPromotion(){
        return promotion;
    }

    @Override
    public String toString() {
        return String.format("%s %,d원 %d개 %s", name, price, stock, promotion != null ? promotion : "");
    }

    public void reduceStock(int quantity) {
        this.stock -= quantity;
    }



}
