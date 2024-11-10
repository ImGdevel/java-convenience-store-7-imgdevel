package store.infra;

import store.domain.Product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDataLoader {

    private String productDataPath;

    public ProductDataLoader(String productDataPath){
        this.productDataPath = productDataPath;
    }

    public Map<String, Product> loadProducts() throws IOException {
        Map<String, Product> productMap = new HashMap<>();
        List<String> lines = Files.readAllLines(Path.of(productDataPath));

        for (String line : lines) {
            processLine(productMap, line);
        }
        return productMap;
    }

    private void processLine(Map<String, Product> productMap, String line) {
        if (line.isBlank() || line.startsWith("name")) {
            return;
        }

        String[] fields = line.split(",");
        String name = fields[0];
        int price = Integer.parseInt(fields[1]);
        int quantity = Integer.parseInt(fields[2]);
        String promotion = parsePromotion(fields[3]);

        addOrUpdateProduct(productMap, name, price, quantity, promotion);
    }

    private String parsePromotion(String promotionField) {

        if("null".equals(promotionField)){
            return null;
        }
        return promotionField;
    }

    private void addOrUpdateProduct(Map<String, Product> productMap, String name, int price, int quantity, String promotion) {
        String key = name;

        if (productMap.containsKey(key)) {
            updateExistingProduct(productMap.get(key), quantity, promotion);
            return;
        }

        Product product = createNewProduct(name, price, quantity, promotion);
        productMap.put(key, product);
    }

    private void updateExistingProduct(Product product, int quantity, String promotion) {
        if (promotion != null) {
            product.setPromotion(promotion);
            product.setPromotionStock(quantity);
            return;
        }
        product.setRegularStock(quantity);
    }

    private Product createNewProduct(String name, int price, int quantity, String promotion) {
        Product product = new Product(name, price);
        if (promotion != null) {
            product.setPromotion(promotion);
            product.setPromotionStock(quantity);
            return product;
        }
        product.setRegularStock(quantity);
        return product;
    }
}
