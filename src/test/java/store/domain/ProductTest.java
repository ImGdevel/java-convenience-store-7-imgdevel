package store.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product createProduct() {
        return new Product("콜라", 1000, 10, "탄산2+1");
    }

    @Test
    void 프로모션이_있을_경우_출력() {
        Product product = createProduct();

        assertEquals("콜라 1,000원 10개 탄산2+1", product.toString());
    }

    @Test
    void 프로모션이_없을_경우_출력() {
        Product product = new Product("사이다", 1000, 8, null);

        assertEquals("사이다 1,000원 8개", product.toString());
    }

    @Test
    void 재고가_차감되는지_확인() {
        Product product = createProduct();

        product.reduceStock(3);
        assertEquals(7, product.getStock());
    }

    @Test
    void 재고가_0이_될_때_차감후_재고확인() {
        Product product = createProduct();

        product.reduceStock(10);
        assertEquals(0, product.getStock());
        assertEquals("콜라 1,000원 재고 없음 탄산2+1", product.toString());
    }

    @Test
    void 재고_초과_차감_예외_확인() {
        Product product = createProduct();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> product.reduceStock(11));
        assertEquals("재고 수량을 초과하여 구매할 수 없습니다.", exception.getMessage());
    }
}
