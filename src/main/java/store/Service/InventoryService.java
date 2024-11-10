package store.Service;

import store.domain.Product;
import store.domain.ProductOrder;
import store.repository.InventoryManager;

import java.util.List;

public class InventoryService {

    private final InventoryManager inventoryManager;

    public InventoryService(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    // 재고 확인 및 검증
    public void checkProductStock(List<ProductOrder> productOrders) {
        for (ProductOrder order : productOrders) {
            Product product = inventoryManager.getProduct(order.getProductName());
            if (!product.isStockAvailable(order.getQuantity())) {
                throw new IllegalArgumentException("재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
            }
        }
    }

    // 모든 제고 확인
    public List<Product> getProducts() {
        return inventoryManager.getAllProducts();
    }
}
