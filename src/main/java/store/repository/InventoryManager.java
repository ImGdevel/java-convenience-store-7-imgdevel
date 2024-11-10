package store.repository;

import store.domain.Product;
import store.infra.ProductDataLoader;

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
            throw new IllegalStateException("재고 초기화 중 오류 발생", e);
        }
    }

    // 특정 제품 재고 확인 후 반환 (없으면 예외 발생)
    public Product getProduct(String productName) {
        Product product = inventory.get(productName);
        if (product == null) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다. 다시 입력해 주세요.");
        }
        return product;
    }

    // 특정 제품의 재고를 요청한 수량만큼 감소
    public void reduceProductStock(String productName, int quantity, boolean isPromotion) {
        Product product = getProduct(productName);
        try {
            product.reduceStock(quantity, isPromotion);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("재고 부족으로 인해 구매할 수 없습니다");
        }
    }

    // 모든 재고를 List로 반환
    public List<Product> getAllProducts() {
        return new ArrayList<>(inventory.values());
    }
}
