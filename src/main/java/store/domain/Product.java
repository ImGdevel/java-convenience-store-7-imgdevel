package store.domain;

public class Product {
    private String name;
    private int price;
    private int regularStock;
    private int promotionStock;
    private String promotion;

    public Product(String name, int price) {
        this.name = name;
        this.price = price;
        this.regularStock = 0;
        this.promotionStock = 0;
        this.promotion = null;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getRegularStock() {
        return regularStock;
    }

    public int getPromotionStock() {
        return promotionStock;
    }

    public void setRegularStock(int regularStock){
        this.regularStock = regularStock;
    }

    public void setPromotionStock(int promotionStock) {
        this.promotionStock = promotionStock;
    }

    public void setPromotion(String promotion){
        this.promotion = promotion;
    }

    public String getPromotion() {
        return promotion;
    }

    public boolean isPromotionAvailable() {
        return promotion != null && promotionStock > 0;
    }

    public boolean isStockAvailable(int required){
        int allStock = promotionStock + regularStock;
        if(required <= allStock){
            return true;
        }
        return false;
    }

    public void reduceStock(int quantity, boolean isPromotion) {
        if (isPromotion) {
            if (quantity > promotionStock) {
                throw new IllegalArgumentException();
            }
            this.promotionStock -= quantity;
        } else {
            if (quantity > regularStock) {
                throw new IllegalArgumentException();
            }
            this.regularStock -= quantity;
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        if (promotionStock > 0 && promotion != null) {
            result.append(String.format("- %s %,d원 %d개 %s\n", name, price, promotionStock, promotion));
        } else if (promotion != null) {
            // 프로모션이 있지만 프로모션 재고가 없는 경우
            result.append(String.format("- %s %,d원 재고 없음 %s\n", name, price, promotion));
        }

        if (regularStock > 0) {
            result.append(String.format("- %s %,d원 %d개", name, price, regularStock));
        } else {
            result.append(String.format("- %s %,d원 재고 없음", name, price));
        }

        return result.toString();
    }

}
