package store.view;

import store.domain.Product;
import store.domain.Receipt;

import java.util.List;

public class OutputView {

    public void displayWelcomeMessage() {
        System.out.println("안녕하세요. W편의점입니다.");
        System.out.println("현재 보유하고 있는 상품입니다.");
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

    public void displayError(String message) {
        System.err.println(message);
    }

}
