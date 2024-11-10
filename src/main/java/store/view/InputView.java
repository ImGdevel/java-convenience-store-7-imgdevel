package store.view;

import camp.nextstep.edu.missionutils.Console;
import store.domain.ProductOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class InputView {

    public List<ProductOrder> getProductOrders() {
        System.out.println("\n구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");

        while (true) {
            String input = Console.readLine();
            try {
                return parseItems(input);
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }

    private List<ProductOrder> parseItems(String input) {
        List<ProductOrder> items = new ArrayList<>();
        String[] itemInputs = input.split(",");

        for (String itemInput : itemInputs) {
            Matcher matcher = Pattern.compile("\\[(.+)-(\\d+)]").matcher(itemInput.trim());
            if (!matcher.matches()) {
                throw new IllegalArgumentException("올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
            }
            String itemName = matcher.group(1);
            int quantity = Integer.parseInt(matcher.group(2));
            items.add(new ProductOrder(itemName, quantity));
        }
        return items;
    }


    public boolean confirmAction() {
        while (true) {
            String input = Console.readLine();
            try {
                return checkInput(input);
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }

    public boolean applyMembershipDiscount() {
        System.out.println("멤버십 할인을 받으시겠습니까? (Y/N)");
        return confirmAction();
    }

    public boolean continueShopping() {
        System.out.println("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");
        return confirmAction();
    }

    private boolean checkInput(String input){
        if (!input.equals("Y") && !input.equals("N")) {
                throw new IllegalArgumentException("잘못된 입력입니다. 다시 입력해 주세요.");
            }
        return input.equals("Y");
    }
}
