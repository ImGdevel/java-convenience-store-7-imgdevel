package store.domain;

import java.util.List;

public class Receipt {
    private List<ReceiptProduct> purchasedItems;
    private List<ReceiptProduct> freeItems;
    private int totalAmount;
    private int promotionDiscount;
    private int membershipDiscount;
    private int finalAmount;

    public Receipt(List<ReceiptProduct> purchasedItems, List<ReceiptProduct> freeItems, int totalAmount, int promotionDiscount, int membershipDiscount, int finalAmount) {
        this.purchasedItems = purchasedItems;
        this.freeItems = freeItems;
        this.totalAmount = totalAmount;
        this.promotionDiscount = promotionDiscount;
        this.membershipDiscount = membershipDiscount;
        this.finalAmount = finalAmount;
    }

    public List<ReceiptProduct> getPurchasedItems() { return purchasedItems; }
    public List<ReceiptProduct> getFreeItems() { return freeItems; }
    public int getTotalAmount() { return totalAmount; }
    public int getPromotionDiscount() { return promotionDiscount; }
    public int getMembershipDiscount() { return membershipDiscount; }
    public int getFinalAmount() { return finalAmount; }
}
