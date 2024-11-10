package store.infra;

import store.domain.Product;
import store.domain.Promotion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PromotionDataLoade {

    private String promotionDataPath;

    public PromotionDataLoade(String promotionDataPath){
        this.promotionDataPath = promotionDataPath;
    }

    public List<Promotion> loadPromotions() throws IOException {
        List<Promotion> promotions = new ArrayList<>();
        List<String> lines = Files.readAllLines(Path.of(promotionDataPath));

        for (String line : lines) {
            processPromotionLine(promotions, line);
        }
        return promotions;
    }

    private void processPromotionLine(List<Promotion> promotions, String line) {
        if (line.isBlank() || line.startsWith("name")) {
            return;
        }

        String[] fields = line.split(",");
        Promotion promotion = createPromotionFromFields(fields);
        promotions.add(promotion);
    }

    private Promotion createPromotionFromFields(String[] fields) {
        String name = fields[0];
        int buyQuantity = Integer.parseInt(fields[1]);
        int getQuantity = Integer.parseInt(fields[2]);
        LocalDate startDate = LocalDate.parse(fields[3]);
        LocalDate endDate = LocalDate.parse(fields[4]);
        return new Promotion(name, buyQuantity, getQuantity, startDate, endDate);
    }
}
