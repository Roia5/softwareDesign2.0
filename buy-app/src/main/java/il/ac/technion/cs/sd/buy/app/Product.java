package il.ac.technion.cs.sd.buy.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Product{
    private String productID;
    private Integer price;
    private Long totalAmountBought = -1L;
    private Double averageAmountBought;
    private List<Order> orderList = new ArrayList<>();
    private Map<String,Long> userAmountMap = new HashMap<>();
    Product(String productID, Integer price){
        this.productID = productID;
        this.price = price;
    }

    String getProductID() {
        return productID;
    }

    Integer getPrice() {
        return price;
    }

    Map<String, Long> getUserAmountMap() {
        return userAmountMap;
    }

    void addPurchase(Order ord){
        orderList.add(ord);
        if(!ord.isCancelled()){
            if(userAmountMap.containsKey(ord.getUserID())){
                Long oldAmount = userAmountMap.get(ord.getUserID());
                userAmountMap.put(ord.getUserID(),ord.getAmount()+oldAmount);
            }
            else {
                userAmountMap.put(ord.getUserID(), ord.getAmount());
            }
        }
    }
    void calcAverageAmountBought(){
        if(new Integer(userAmountMap.size()).equals(0)){
            averageAmountBought = 0.0;
        }
        else{
            if(totalAmountBought.equals(-1L)){
                totalAmountBought = calculateTotalAmountBought();
            }
            averageAmountBought =  ((double)totalAmountBought / userAmountMap.size());
        }
    }
    Long calculateTotalAmountBought(){
        final Long[] total = {0L};
        userAmountMap.forEach((userId,amount)-> total[0] += amount);
        return total[0];
    }
    Double getAverageAmountBought(){
        return averageAmountBought;
    }
    List<Order> getOrderList(){
        return orderList;
    }
}
