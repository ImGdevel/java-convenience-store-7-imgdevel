package store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.Service.InventoryService;
import store.Service.PromotionService;
import store.domain.Product;
import store.domain.ProductOrder;
import store.infra.ProductDataLoader;
import store.infra.PromotionDataLoader;
import store.repository.InventoryManager;
import store.repository.PromotionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InventoryServiceTest {
    private InventoryService inventoryService;
    private InventoryManager inventoryManager;
    private ProductDataLoader productDataLoader;
    private Path productFile;

    @BeforeEach
    void setUp() throws IOException {
        productFile = Files.createTempFile("products", ".md");
        Files.writeString(productFile, """
        name,price,quantity,promotion
        콜라,1000,10,null
        콜라,1000,10,탄산2+1
        사이다,1000,7,탄산2+1
        사이다,1000,3,null
        오렌지주스,1800,1,MD추천상품
        """);
        productDataLoader = new ProductDataLoader(productFile.toString());
        inventoryManager = new InventoryManager(productDataLoader);
        inventoryService = new InventoryService(inventoryManager);  // InventoryService 초기화
    }

    @Test
    void 재고_확인_정상_구매() {
        List<ProductOrder> productOrders = Arrays.asList(new ProductOrder("콜라", 5));
        inventoryService.checkProductStock(productOrders);
    }

    @Test
    void 재고_부족_예외_발생() {
        List<ProductOrder> productOrders = Arrays.asList(new ProductOrder("콜라", 30));
        assertThrows(IllegalArgumentException.class, () -> inventoryService.checkProductStock(productOrders));
    }

    @Test
    void 존재하지_않는_재고_예외_발생() {
        List<ProductOrder> productOrders = Arrays.asList(new ProductOrder("사과주스", 10));
        assertThrows(IllegalArgumentException.class, () -> inventoryService.checkProductStock(productOrders));
    }

        @Test
    void testGetProducts() {
        List<Product> products = inventoryService.getProducts();

        assertEquals(3, products.size());

        Product cola = products.stream().filter(p -> p.getName().equals("콜라")).findFirst().orElse(null);
        assertTrue(cola != null);
        assertEquals(1000, cola.getPrice());
    }
}
