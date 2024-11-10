package store.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PromotionTest {

    @Test
    void 프로모션_활성화_확인() {
        Promotion promotion = new Promotion("탄산2+1", 2, 1, "2023-01-01", "2023-12-31");

        assertTrue(promotion.isPromotionActive(LocalDate.of(2023, 5, 1)));
        assertFalse(promotion.isPromotionActive(LocalDate.of(2024, 1, 1)));
    }

    @Test
    void 프로모션명_출력_확인() {
        Promotion promotion = new Promotion("탄산2+1", 2, 1, "2023-01-01", "2023-12-31");

        assertEquals("탄산2+1", promotion.getName());
    }

    @Test
    void 프로모션날짜_출력_확인() {
        Promotion promotion = new Promotion("탄산2+1", 2, 1, "2023-01-01", "2023-12-31");

        assertEquals("2023-01-01", promotion.getStartDateToString());
        assertEquals("2023-12-31", promotion.getEndDateToString());
    }
}
