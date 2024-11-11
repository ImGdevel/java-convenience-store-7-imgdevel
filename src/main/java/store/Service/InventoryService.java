package store.Service;

import store.domain.Product;
import store.domain.ProductOrder;
import store.repository.InventoryManager;
import store.utils.ErrorMessages;

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
                throw new IllegalArgumentException(ErrorMessages.OUT_OF_STOCK);
            }
        }
    }

    // 모든 제고 확인
    public List<Product> getProducts() {
        return inventoryManager.getAllProducts();
    }
}
