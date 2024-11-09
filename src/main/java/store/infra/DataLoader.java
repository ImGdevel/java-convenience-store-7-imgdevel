package store.infra;

import store.domain.Product;
import store.domain.Promotion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    private final String PRODUCT_PATH = "src/main/resources/products.md";
    private final String PROMOTION_PATH = "src/main/resources/promotions.md";


    public List<Product> loadProduct() throws IOException {
        List<Product> products = new ArrayList<>();
        List<String> lines = Files.readAllLines(Path.of(PRODUCT_PATH));

        for (String line : lines) {
            if (line.isBlank() || line.startsWith("name")) continue;

            String[] fields = line.split(",");
            String name = fields[0];
            int price = Integer.parseInt(fields[1]);
            int quantity = Integer.parseInt(fields[2]);
            String promotion = fields[3].equals("null") ? null : fields[3];

            products.add(new Product(name, price, quantity, promotion));
        }
        return products;

    }

    public List<Promotion> loadPromotions() throws IOException {
        List<Promotion> promotions = new ArrayList<>();
        List<String> lines = Files.readAllLines(Path.of(PROMOTION_PATH));

        for (String line : lines) {
            if (line.isBlank() || line.startsWith("name")) continue;

            String[] fields = line.split(",");
            String name = fields[0];
            int buyQuantity = Integer.parseInt(fields[1]);
            int getQuantity = Integer.parseInt(fields[2]);
            LocalDate startDate = LocalDate.parse(fields[3]);
            LocalDate endDate = LocalDate.parse(fields[4]);

            promotions.add(new Promotion(name, buyQuantity, getQuantity, startDate, endDate));
        }
        return promotions;
    }

}
