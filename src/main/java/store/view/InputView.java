package store.view;

import camp.nextstep.edu.missionutils.Console;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;

public class InputView {

    public Map<String, Integer> getPurchaseInput() {
        System.out.println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");

        while (true) {
            String input = Console.readLine();
            try {
                return parseItems(input);
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }

    private Map<String, Integer> parseItems(String input) {
        Map<String, Integer> items = new HashMap<>();
        String[] itemInputs = input.split(",");

        for (String itemInput : itemInputs) {
            Matcher matcher = Pattern.compile("\\[(.+)-(\\d+)]").matcher(itemInput.trim());
            if (!matcher.matches()) {
                throw new IllegalArgumentException("올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
            }
            String itemName = matcher.group(1);
            int quantity = Integer.parseInt(matcher.group(2));
            // todo : 존재하지 않는 상품을 입력한 경우
            // todo : 구매 수량이 재고 수량을 초과한 경우
            items.put(itemName, quantity);
        }
        return items;
    }





}
