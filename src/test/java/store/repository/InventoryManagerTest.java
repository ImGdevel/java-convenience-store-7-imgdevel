package store.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.domain.Product;
import store.infra.ProductDataLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryManagerTest {
    private InventoryManager inventoryManager;
    private ProductDataLoader productDataLoader;
    private Path productFile;

    @BeforeEach
    void 초기_설정() throws IOException {
        productFile = Files.createTempFile("products", ".md");
        Files.writeString(productFile, """
            name,price,quantity,promotion
            콜라,1000,10,탄산2+1
            콜라,1000,10,null
            사이다,1000,7,탄산2+1
            사이다,1000,8,null
            오렌지주스,1800,9,MD추천상품
            """);
        productDataLoader = new ProductDataLoader(productFile.toString());
        inventoryManager = new InventoryManager(productDataLoader);
    }

    @Test
    void 재고_초기화_정상_확인() {
        List<Product> products = inventoryManager.getAllProducts();
        assertEquals(3, products.size());
    }

    @Test
    void 특정_제품_찾기_성공() {
        Product cola = inventoryManager.getProduct("콜라");
        assertNotNull(cola);
        assertEquals("콜라", cola.getName());
        assertEquals(1000, cola.getPrice());
    }

    @Test
    void 제품이_존재하지_않으면_예외_발생() {
        assertThrows(IllegalArgumentException.class, () -> inventoryManager.getProduct("사과주스"));
    }

    @Test
    void 제품_일반재고_정상_차감() {
        inventoryManager.reduceProductStock("사이다", 3, false);
        assertEquals(5, inventoryManager.getProduct("사이다").getRegularStock());
    }

    @Test
    void 제품_프로모션재고_정상_차감() {
        inventoryManager.reduceProductStock("사이다", 3, true);
        assertEquals(4, inventoryManager.getProduct("사이다").getPromotionStock());
    }

    @Test
    void 재고_부족으로_인한_예외_확인() {
        assertThrows(IllegalArgumentException.class, () -> inventoryManager.reduceProductStock("콜라", 11, false));
    }

    @Test
    void 프로모션재고_부족으로_인한_예외_확인() {
        assertThrows(IllegalArgumentException.class, () -> inventoryManager.reduceProductStock("콜라", 11, true));
    }
}
