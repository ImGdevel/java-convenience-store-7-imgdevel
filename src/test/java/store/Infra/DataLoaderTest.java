package store.Infra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import store.infra.DataLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DataLoaderTest {
    @TempDir
    Path tempDir;

    private Path productFile;
    private Path promotionFile;
    private DataLoader dataLoader;

    @BeforeEach
    void 초기_설정() throws IOException {
        productFile = tempDir.resolve("products.md");
        promotionFile = tempDir.resolve("promotions.md");

        Files.writeString(productFile, """
            name,price,quantity,promotion
            콜라,1000,10,탄산2+1
            사이다,1000,8,null
            """);

        Files.writeString(promotionFile, """
            name,buy,get,start_date,end_date
            탄산2+1,2,1,2024-01-01,2024-12-31
            """);

        dataLoader = new DataLoader(productFile.toString(), promotionFile.toString());
    }

    @Test
    void 상품_목록을_정상적으로_불러오기() throws IOException {
        var products = dataLoader.loadProduct();

        assertThat(products).hasSize(2);
        assertThat(products.get(0).getName()).isEqualTo("콜라");
        assertThat(products.get(0).getPrice()).isEqualTo(1000);
        assertThat(products.get(0).getStock()).isEqualTo(10);
        assertThat(products.get(0).getPromotion()).isEqualTo("탄산2+1");
    }

    @Test
    void 프로모션_목록을_정상적으로_불러오기() throws IOException {
        var promotions = dataLoader.loadPromotions();

        assertThat(promotions).hasSize(1);
        var promo = promotions.get(0);
        assertThat(promo.getName()).isEqualTo("탄산2+1");
        assertThat(promo.getPurchaseQuantity()).isEqualTo(2);
        assertThat(promo.getRewardQuantity()).isEqualTo(1);
        assertThat(promo.getStartDateToString()).isEqualTo("2024-01-01");
        assertThat(promo.getEndDateToString()).isEqualTo("2024-12-31");
    }

    @Test
    void 잘못된_상품_데이터로_예외_발생() throws IOException {
        Files.writeString(productFile, "invalid,data");

        assertThrows(NumberFormatException.class, () -> dataLoader.loadProduct());
    }

    @Test
    void 잘못된_날짜로_예외_발생() throws IOException {
        Files.writeString(promotionFile, """
            name,buy,get,start_date,end_date
            탄산2+1,2,1,invalid-date,2024-12-31
            """);

        assertThrows(DateTimeParseException.class, () -> dataLoader.loadPromotions());
    }
}
