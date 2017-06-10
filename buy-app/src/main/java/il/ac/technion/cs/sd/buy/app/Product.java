package il.ac.technion.cs.sd.buy.app;

import java.util.ArrayList;
import java.util.List;

public class Product{
    private String productID;
    private Integer price;
    private Integer totalAmountBought = 0;
    private Double averageAmountBought;
    private Integer numberOfOrders = 0;
    private List<Order> orderList = new ArrayList<>();
    public Product(String productID, Integer price){
        this.productID = productID;
        this.price = price;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public Integer getPrice() {
        return price;
    }

    public void addPurchase(Order ord){
        orderList.add(ord);
        totalAmountBought += ord.getAmount();
        numberOfOrders++;
    }
    public void calcAverageAmountBought(){
        if(numberOfOrders.equals(0)){
            averageAmountBought = 0.0;
        }
        else{
            averageAmountBought = (double) (totalAmountBought / numberOfOrders);
        }
    }
    public Double getAverageAmountBought(){
        return averageAmountBought;
    }
    public Integer getTotalAmountBought(){
        return totalAmountBought;
    }
    public List<Order> getOrderList(){
        return orderList;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }
}
