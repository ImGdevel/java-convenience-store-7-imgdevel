package store.Infra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

import store.domain.Product;
import store.infra.ProductDataLoader;

import java.util.Map;


class ProductDataLoaderTest {
    @TempDir
    Path tempDir;

    private Path productFile;
    private ProductDataLoader productDataLoader;

    @BeforeEach
    void 초기_설정() throws IOException {
        productFile = tempDir.resolve("products.md");
        Files.writeString(productFile, """
            name,price,quantity,promotion
            콜라,1000,10,탄산2+1
            콜라,1000,10,null
            사이다,1000,8,탄산2+1
            사이다,1000,7,null
            오렌지주스,1800,9,MD추천상품
            물,500,10,null
            """);
        productDataLoader = new ProductDataLoader(productFile.toString());
    }

    @Test
    void 상품_목록을_정상적으로_불러오기() throws IOException {
        Map<String, Product> products = productDataLoader.loadProducts();
        assertEquals(4, products.size());

        Product soda = products.get("사이다");
        assertNotNull(soda);
        assertEquals("사이다", soda.getName());
        assertEquals(1000, soda.getPrice());
        assertEquals(7, soda.getRegularStock());
        assertEquals(8, soda.getPromotionStock());
        assertEquals("탄산2+1", soda.getPromotion());

        Product juice = products.get("오렌지주스");
        assertNotNull(juice);
        assertEquals("오렌지주스", juice.getName());
        assertEquals(1800, juice.getPrice());
        assertEquals(9, juice.getPromotionStock());
        assertEquals("MD추천상품", juice.getPromotion());
    }

    @Test
    void 프로모션_없는_제품_정상_처리() throws IOException {
        ProductDataLoader singleLoadProductDataLoader = new ProductDataLoader(productFile.toString());
        Map<String, Product> products = singleLoadProductDataLoader.loadProducts();

        Product cola = products.get("물");
        assertNotNull(cola);
        assertEquals("물", cola.getName());
        assertEquals(500, cola.getPrice());
        assertEquals(10, cola.getRegularStock());
        assertNull(cola.getPromotion());
    }

    @Test
    void 잘못된_데이터로_예외_발생() throws IOException {
        // 잘못된 데이터를 파일에 기록하여 예외 상황 테스트
        Files.writeString(productFile, """
            name,price,quantity,promotion
            콜라,null,10,탄산2+1
            """);
        assertThrows(NumberFormatException.class, () -> productDataLoader.loadProducts());
    }

    @Test
    void 공백_라인_무시_확인() throws IOException {
        Files.writeString(productFile, """
            name,price,quantity,promotion
            
            콜라,1000,10,탄산2+1
            
            오렌지주스,1800,9,MD추천상품
            """);

        Map<String, Product> products = productDataLoader.loadProducts();
        assertEquals(2, products.size());
    }
}
