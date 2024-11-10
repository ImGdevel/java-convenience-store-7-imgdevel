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
        this.promotion = promotion;
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

    public boolean isPromotion() {
        return promotion != null;
    }

    @Override
    public String toString() {
        if (stock <= 0) {
            return String.format("%s %,d원 재고 없음%s", name, price, promotion != null ? " " + promotion : "");
        }
        return String.format("%s %,d원 %d개%s", name, price, stock, promotion != null ? " " + promotion : "");
    }

    public void reduceStock(int quantity) {
        if (quantity > stock) {
            throw new IllegalArgumentException("재고 수량을 초과하여 구매할 수 없습니다.");
        }
        this.stock -= quantity;
    }
}
