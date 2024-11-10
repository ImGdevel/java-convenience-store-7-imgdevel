package store.domain;

import java.util.List;

public class Receipt {
    private List<ProductOrder> purchasedItems;
    private List<ProductOrder> freeItems;
    private int totalAmount;
    private int promotionDiscount;
    private int membershipDiscount;
    private int finalAmount;

    public Receipt(List<ProductOrder> purchasedItems, List<ProductOrder> freeItems, int totalAmount, int promotionDiscount, int membershipDiscount, int finalAmount) {
        this.purchasedItems = purchasedItems;
        this.freeItems = freeItems;
        this.totalAmount = totalAmount;
        this.promotionDiscount = promotionDiscount;
        this.membershipDiscount = membershipDiscount;
        this.finalAmount = finalAmount;
    }

    public List<ProductOrder> getPurchasedItems() { return purchasedItems; }
    public List<ProductOrder> getFreeItems() { return freeItems; }
    public int getTotalAmount() { return totalAmount; }
    public int getPromotionDiscount() { return promotionDiscount; }
    public int getMembershipDiscount() { return membershipDiscount; }
    public int getFinalAmount() { return finalAmount; }
}
