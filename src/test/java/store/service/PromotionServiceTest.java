package store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.Service.PromotionService;
import store.domain.ProductOrder;
import store.domain.Promotion;
import store.infra.ProductDataLoader;
import store.infra.PromotionDataLoader;
import store.repository.InventoryManager;
import store.repository.PromotionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromotionServiceTest {
    private InventoryManager inventoryManager;
    private PromotionManager promotionManager;
    private PromotionService promotionService;
    private Path productFile;
    private Path promotionFile;

    @BeforeEach
    void 초기_설정() throws IOException {
        // 임시 파일 생성 및 초기화 - 제품
        productFile = Files.createTempFile("products", ".md");
        Files.writeString(productFile,
                """
                name,price,quantity,promotion
                콜라,1000,10,null
                콜라,1000,10,탄산2+1
                사이다,1000,7,탄산2+1
                사이다,1000,3,null
                초코바,1200,5,MD추천상품
                초코바,1200,5,null
                오렌지주스,1800,1,MD추천상품
                사과주스,2000,10,반짝할인
                """);

        // 임시 파일 생성 및 초기화 - 프로모션
        promotionFile = Files.createTempFile("promotions", ".md");
        Files.writeString(promotionFile,
                """
                name,buy,get,start_date,end_date
                탄산2+1,2,1,2024-01-01,2024-12-31
                MD추천상품,1,1,2024-01-01,2024-12-31
                반짝할인,1,1,2023-11-01,2023-11-30
                """);

        ProductDataLoader productDataLoader = new ProductDataLoader(productFile.toString());
        PromotionDataLoader promotionDataLoader = new PromotionDataLoader(promotionFile.toString());

        inventoryManager = new InventoryManager(productDataLoader);
        promotionManager = new PromotionManager(promotionDataLoader);
        promotionService = new PromotionService(inventoryManager, promotionManager);
    }

    @Test
    void 프로모션_상품_자동_적용_확인() {
        List<ProductOrder> productOrders = List.of(
                new ProductOrder("콜라", 6),
                new ProductOrder("사이다", 3),
                new ProductOrder("초코바", 1)
        );
        List<ProductOrder> withoutPromotions = promotionService.getPromotionProduct(productOrders);

        assertEquals(2, withoutPromotions.size());
        assertEquals("콜라", withoutPromotions.get(0).getProductName());
        assertEquals(2, withoutPromotions.get(0).getQuantity());
        assertEquals("사이다", withoutPromotions.get(1).getProductName());
        assertEquals(1, withoutPromotions.get(1).getQuantity());
    }

    @Test
    void 무료_프로모션_적용_상품_확인() {
        List<ProductOrder> productOrders = List.of(
                new ProductOrder("콜라", 5),
                new ProductOrder("사이다", 2),
                new ProductOrder("초코바", 2)
        );
        List<ProductOrder> freePromotions = promotionService.checkFreePromotionProduct(productOrders);


        assertEquals(2, freePromotions.size());
        assertEquals("콜라", freePromotions.get(0).getProductName());
        assertEquals(1, freePromotions.get(0).getQuantity());
        assertEquals("사이다", freePromotions.get(1).getProductName());
        assertEquals(1, freePromotions.get(1).getQuantity());
    }

        @Test
    void 프로모션_정가_구매_상품_확인() {
        List<ProductOrder> productOrders = List.of(
                new ProductOrder("콜라", 12),
                new ProductOrder("사이다", 2),
                new ProductOrder("초코바", 6),
                new ProductOrder("오렌지주스", 1)
        );
        List<ProductOrder> freePromotions = promotionService.checkWithoutPromotionStock(productOrders);


        assertEquals(2, freePromotions.size());
        assertEquals("콜라", freePromotions.get(0).getProductName());
        assertEquals(3, freePromotions.get(0).getQuantity());
        assertEquals("초코바", freePromotions.get(1).getProductName());
        assertEquals(2, freePromotions.get(1).getQuantity());
    }



    @Test
    void 프로모션_적용불가_상품_제고_부족() {
        List<ProductOrder> productOrders = List.of(new ProductOrder("오렌지주스", 1));
        List<ProductOrder> freePromotions = promotionService.checkFreePromotionProduct(productOrders);
        List<ProductOrder> withoutPromotions = promotionService.checkWithoutPromotionStock(productOrders);

        assertTrue(freePromotions.isEmpty());
        assertTrue(withoutPromotions.isEmpty());
    }

    @Test
    void 만료된_프로모션_적용_확인() throws IOException {
        List<ProductOrder> productOrders = List.of(new ProductOrder("사과주스", 1));
        List<ProductOrder> freePromotions = promotionService.checkFreePromotionProduct(productOrders);

        assertTrue(freePromotions.isEmpty());
    }
}
