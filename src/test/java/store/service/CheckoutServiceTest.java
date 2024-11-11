package store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.Service.CheckoutService;
import store.domain.Order;
import store.domain.ProductOrder;
import store.domain.Receipt;
import store.infra.ProductDataLoader;
import store.repository.InventoryManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CheckoutServiceTest {

    private CheckoutService checkoutService;
    private InventoryManager inventoryManager;
    private Path productFile;

    @BeforeEach
    void setUp() throws IOException {
        productFile = Files.createTempFile("products", ".md");
        Files.writeString(productFile, """
                name,price,quantity,promotion
                콜라,1000,10,탄산2+1
                콜라,1000,10,null
                사이다,1000,8,탄산2+1
                사이다,1000,7,null
                오렌지주스,1800,9,MD추천상품
                탄산수,1200,5,탄산2+1
                물,500,10,null
                """);

        inventoryManager = new InventoryManager(new ProductDataLoader(productFile.toString()));
        checkoutService = new CheckoutService(inventoryManager);
    }

    @Test
    void 정상적인_주문_처리_및_영수증_검증() {
        ProductOrder orderCola = new ProductOrder("콜라", 12);
        Order order = new Order(Collections.singletonList(orderCola), Collections.singletonList(new ProductOrder("콜라", 3)));

        Receipt receipt = checkoutService.processOrder(order);

        assertEquals(8, inventoryManager.getProduct("콜라").getRegularStock());
        assertEquals(0, inventoryManager.getProduct("콜라").getPromotionStock());

        assertEquals(12000, receipt.getTotalAmount());
        assertEquals(3000, receipt.getPromotionDiscount());
        assertEquals(9000, receipt.getFinalAmount());
    }

    @Test
    void 멤버십_할인_적용_테스트() {
        List<ProductOrder> productOrders = List.of(
                new ProductOrder("콜라", 1),
                new ProductOrder("사이다", 1),
                new ProductOrder("물", 2)
        );
        Order order = new Order(productOrders, Collections.emptyList());
        order.setMembershipApplied(true);

        Receipt receipt = checkoutService.processOrder(order);

        assertEquals(3000, receipt.getTotalAmount());
        assertEquals(300, receipt.getMembershipDiscount());
        assertEquals(2700, receipt.getFinalAmount());
    }

    @Test
    void 존재하지_않는_제품_주문_예외_테스트() {
        ProductOrder orderInvalid = new ProductOrder("사과주스", 1);
        Order order = new Order(Collections.singletonList(orderInvalid), Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> checkoutService.processOrder(order));
    }

    @Test
    void 잔여_재고_부족_예외_테스트() {
        ProductOrder order = new ProductOrder("사이다", 30);
        Order orderList = new Order(Collections.singletonList(order), Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> checkoutService.processOrder(orderList));
    }
}
