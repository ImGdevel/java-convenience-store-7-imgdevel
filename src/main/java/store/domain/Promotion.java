package store.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Promotion {
    private String name;
    private int purchaseQuantity;
    private int rewardQuantity;
    private LocalDate startDate;
    private LocalDate endDate;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Promotion(String name, int purchaseQuantity, int perQuantity, String startDate, String endDate){
        this.name = name;
        this.purchaseQuantity = purchaseQuantity;
        this.rewardQuantity = rewardQuantity;
        this.startDate = LocalDate.parse(startDate, formatter);
        this.endDate = LocalDate.parse(endDate, formatter);
    }

    public Promotion(String name, int purchaseQuantity, int rewardQuantity, LocalDate startDate, LocalDate endDate){
        this.name = name;
        this.purchaseQuantity = purchaseQuantity;
        this.rewardQuantity = rewardQuantity;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }
    public int getPurchaseQuantity(){
        return purchaseQuantity;
    }
    public int getRewardQuantity(){
        return rewardQuantity;
    }
    public String getStartDateToString(){
        return startDate.format(formatter);
    }
    public String getEndDateToString(){
        return endDate.format(formatter);
    }
}
