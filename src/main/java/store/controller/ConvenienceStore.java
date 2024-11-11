package store.controller;

import store.Service.CheckoutService;
import store.Service.InventoryService;
import store.Service.PromotionService;
import store.domain.Order;
import store.domain.ProductOrder;
import store.domain.Receipt;
import store.infra.ProductDataLoader;
import store.infra.PromotionDataLoader;
import store.repository.InventoryManager;
import store.repository.PromotionManager;
import store.view.InputView;
import store.view.OutputView;

import java.util.List;

public class ConvenienceStore {

    final String productsDataPath = "src/main/resources/products.md";
    final String promotionDataPath = "src/main/resources/promotions.md";

    InputView inputView;
    OutputView outputView;

    ProductDataLoader productDataLoader;
    PromotionDataLoader promotionDataLoader;

    InventoryManager inventoryManager;
    PromotionManager promotionManager;

    PromotionService promotionService;
    InventoryService inventoryService;
    CheckoutService checkoutService;

    public ConvenienceStore(){
        this.inputView = new InputView();
        this.outputView = new OutputView();

        this.productDataLoader = new ProductDataLoader(productsDataPath);
        this.promotionDataLoader = new PromotionDataLoader(promotionDataPath);

        this.inventoryManager = new InventoryManager(productDataLoader);
        this.promotionManager = new PromotionManager(promotionDataLoader);

        this.inventoryService = new InventoryService(inventoryManager);
        this.promotionService = new PromotionService(inventoryManager, promotionManager);
        this.checkoutService = new CheckoutService(inventoryManager);
    }

    public void start(){
        while (true){

            outputView.displayWelcomeMessage();
            outputView.displayProductList(inventoryService.getProducts());

            List<ProductOrder> orders = InputPurchase();
            List<ProductOrder> promotionProduct = promotionService.getPromotionProduct(orders);

            Order order = new Order(orders, promotionProduct); // 주문서 생성

            List<ProductOrder> freePromotionProducts = promotionService.checkFreePromotionProduct(orders);
            for(ProductOrder productOrder: freePromotionProducts){
                String productName = productOrder.getProductName();
                int quantity = productOrder.getQuantity();

                outputView.displayPromotionSuggestion(productName, quantity);
                boolean decisionInput = inputView.continueShopping();

                if(decisionInput){
                    order.addPromotionalProductOrder(productName, quantity);
                }
            }

            List<ProductOrder> withoutPromoStock = promotionService.checkWithoutPromotionStock(orders);
            for(ProductOrder productOrder: withoutPromoStock){
                String productName = productOrder.getProductName();
                int quantity = productOrder.getQuantity();
                outputView.displayPromotionLimitWarning(productName, quantity);
                boolean decisionInput = inputView.continueShopping();

                if(!decisionInput){
                    order.reduceTotalProductOrder(productName, quantity);
                }
            }

            if(inputView.applyMembershipDiscount()){
                order.setMembershipApplied(true);
            }

            Receipt receipt = checkoutService.processOrder(order);
            outputView.displayReceipt(receipt);

            if(!inputView.continueShopping()){
                break;
            }
        }
    }

    private List<ProductOrder> InputPurchase(){
        while (true){
            try{
                List<ProductOrder> orders = inputView.getProductOrders();
                inventoryService.checkProductStock(orders);
                return orders;
            }catch (IllegalArgumentException e){
                outputView.displayError(e.getMessage());
            }
        }
    }
}
