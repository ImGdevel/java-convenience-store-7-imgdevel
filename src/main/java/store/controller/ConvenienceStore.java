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
        initializeComponents();
    }

    private void initializeComponents() {
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

            Order order = createOrderWithPromotion();
            applyMembershipDiscount(order);
            processCheckout(order);

            if(!inputView.continueShopping()){
                break;
            }
        }
    }

    private Order createOrderWithPromotion() {
        List<ProductOrder> orders = getProductOrders();
        List<ProductOrder> promotionProducts = promotionService.getPromotionProduct(orders);
        Order order = new Order(orders, promotionProducts);

        suggestFreePromotion(order);
        handlePromotionStockLimit(order);

        return order;
    }

    private List<ProductOrder> getProductOrders(){
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

    private void suggestFreePromotion(Order order) {
        List<ProductOrder> freePromotionProducts = promotionService.checkFreePromotionProduct(order.getTotalProductOrders());
        freePromotionProducts.forEach(productOrder -> suggestPromotion(order, productOrder));
    }

    private void suggestPromotion(Order order, ProductOrder productOrder) {
        outputView.displayPromotionSuggestion(productOrder.getProductName(), productOrder.getQuantity());
        if (inputView.confirmAction()) {
            order.addPromotionalProductOrder(productOrder.getProductName(), productOrder.getQuantity());
        }
    }

    private void handlePromotionStockLimit(Order order) {
        List<ProductOrder> withoutPromoStock = promotionService.checkWithoutPromotionStock(order.getTotalProductOrders());
        withoutPromoStock.forEach(productOrder -> handleStockLimit(order, productOrder));
    }

    private void handleStockLimit(Order order, ProductOrder productOrder) {
        outputView.displayPromotionLimitWarning(productOrder.getProductName(), productOrder.getQuantity());
        if (!inputView.confirmAction()) {
            order.reduceTotalProductOrder(productOrder.getProductName(), productOrder.getQuantity());
        }
    }

    private void applyMembershipDiscount(Order order) {
        if(inputView.applyMembershipDiscount()) {
            order.setMembershipApplied(true);
        }
    }

    private void processCheckout(Order order) {
        Receipt receipt = checkoutService.processOrder(order);
        outputView.displayReceipt(receipt);
    }
}
