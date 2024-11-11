package store.view;

import store.domain.Product;
import store.domain.ProductOrder;
import store.domain.Receipt;
import store.domain.ReceiptProduct;

import java.text.DecimalFormat;
import java.util.List;

public class OutputView {

    public void displayWelcomeMessage() {
        System.out.println("안녕하세요. W편의점입니다.");
        System.out.println("현재 보유하고 있는 상품입니다.\n");
    }

    public void displayProductList(List<Product> products){
        for (Product product : products) {
            System.out.println(product.toString());
        }
    }

    public void displayPromotionSuggestion(String productName, int extraQuantity) {
        System.out.printf("현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)%n", productName, extraQuantity);
    }

    public void displayPromotionLimitWarning(String productName, int quantity) {
        System.out.printf("현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)%n", productName, quantity);
    }

    public void displayReceipt(Receipt receipt) {
        DecimalFormat currencyFormat = new DecimalFormat("#,###");

        System.out.println("==============W 편의점================");
        System.out.format("%-15s%-8s%-8s%n", "상품명", "수량", "금액");

        for (ReceiptProduct item : receipt.getPurchasedItems()) {
            int totalItemPrice = item.getQuantity() * item.getPrice();
            System.out.format("%-15s%-8d%-8s%n", item.getProductName(), item.getQuantity(), currencyFormat.format(totalItemPrice));
        }

        System.out.println("=============증 정===============");
        for (ReceiptProduct item : receipt.getFreeItems()) {
            System.out.format("%-15s%-8d%n", item.getProductName(), item.getQuantity());
        }

        System.out.println("====================================");
        System.out.format("총구매액\t\t\t%s%n", currencyFormat.format(receipt.getTotalAmount()));
        System.out.format("행사할인\t\t\t-%s%n", currencyFormat.format(receipt.getPromotionDiscount()));
        System.out.format("멤버십할인\t\t-%s%n", currencyFormat.format(receipt.getMembershipDiscount()));
        System.out.format("내실돈\t\t\t%s%n", currencyFormat.format(receipt.getFinalAmount()));
    }

    public void displayError(String message) {
        System.err.println(message);
    }

}
