package store.Service;

import store.domain.Product;
import store.domain.Promotion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManager {

    private Map<String, Product> products = new HashMap<>();
    private Map<String, Promotion> promotions = new HashMap<>();

    // 상품 목록 업데이트
    public void updateProduct(List<Product> newProducts){
        for(Product product : newProducts){
            String key = product.getName();
            products.put(key, product);
        }
    }

    // 프로모션 목록 업데이트
    public void updatePromotion(List<Promotion> newPromotions){
        for(Promotion promotion : newPromotions){
            String key = promotion.getName();
            promotions.put(key, promotion);
        }
    }

    // 모든 상품 목록 반환
    public List<Product> getAllProduct(){
        return new ArrayList<>(products.values());
    }

}
