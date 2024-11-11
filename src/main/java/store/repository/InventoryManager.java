package store.repository;

import store.domain.Product;
import store.infra.ProductDataLoader;
import store.utils.ErrorMessages;

import java.io.IOException;
import java.util.*;

public class InventoryManager {
    private final Map<String, Product> inventory = new HashMap<>();

    public InventoryManager(ProductDataLoader dataLoader) {
        initializeInventory(dataLoader);
    }

    // 재고 초기화
    private void initializeInventory(ProductDataLoader dataLoader) {
        try {
            Map<String, Product> loadedProducts = dataLoader.loadProducts();
            inventory.putAll(loadedProducts);
        } catch (IOException e) {
            throw new IllegalStateException(ErrorMessages.FAILED_INITIALIZE_INVENTORY, e);
        }
    }

    // 특정 제품 재고 확인 후 반환 (없으면 예외 발생)
    public Product getProduct(String productName) {
        Product product = inventory.get(productName);
        if (product == null) {
            throw new IllegalArgumentException(ErrorMessages.PRODUCT_NOT_FOUND);
        }
        return product;
    }

    // 특정 제품의 재고를 요청한 수량만큼 감소
    public void reduceProductStock(String productName, int quantity, boolean isPromotion) {
        Product product = getProduct(productName);
        try {
            product.reduceStock(quantity, isPromotion);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(ErrorMessages.OUT_OF_STOCK);
        }
    }

    // 모든 재고를 List로 반환
    public List<Product> getAllProducts() {
        return new ArrayList<>(inventory.values());
    }
}
