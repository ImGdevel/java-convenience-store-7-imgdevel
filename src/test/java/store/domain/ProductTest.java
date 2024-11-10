package store.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    private Product product;

    @BeforeEach
    void setup() {
        product = new Product("콜라", 1000);
        product.setRegularStock(10);
        product.setPromotion("탄산2+1");
        product.setPromotionStock(5);
    }

    @Test
    void 프로모션_가능한지_확인() {
        assertTrue(product.isPromotionAvailable());
    }

    @Test
    void 프로모션재고가_없을때_프로모션_불가능() {
        product.setPromotionStock(0);
        assertFalse(product.isPromotionAvailable());
    }

    @Test
    void 정규재고_감소_확인() {
        product.reduceStock(3, false);
        assertEquals(7, product.getRegularStock());
    }

    @Test
    void 프로모션재고_감소_확인() {
        product.reduceStock(2, true);
        assertEquals(3, product.getPromotionStock());
    }

    @Test
    void 정규재고_초과_차감시_예외_확인() {
        assertThrows(IllegalArgumentException.class, () -> product.reduceStock(11, false));
    }

    @Test
    void 프로모션재고_초과_차감시_예외_확인() {
        assertThrows(IllegalArgumentException.class, () -> product.reduceStock(6, true));
    }

    @Test
    void 프로모션_세부사항_설정_확인() {
        assertEquals("탄산2+1", product.getPromotion());
    }
}
