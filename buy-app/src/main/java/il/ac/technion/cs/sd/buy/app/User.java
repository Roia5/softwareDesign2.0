package il.ac.technion.cs.sd.buy.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Roey on 09/06/2017.
 */
public class User {
    private String userID;
    private Integer countOrders;
    private Integer countCancelledOrders;
    private Integer countModifiedOrders;
    private Long totalAmountSpent;

    public List<String> getOrderIdList() {
        return orderIdList;
    }

    private List<String> orderIdList = new ArrayList<>();
    public Map<String, Long> getProductAmountMap() {
        return productAmountMap;
    }

    //private List<Order> orderList = new ArrayList<>();
    private Map<String,Long> productAmountMap = new HashMap<>();
    User(String userID){
        this.userID = userID;
        countOrders = 0;
        countCancelledOrders = 0;
        countModifiedOrders = 0;
        totalAmountSpent = 0L;
    }
    void addOrder(Order new_order, Integer price){
        countOrders++;
        if(new_order.isCancelled()){
            countCancelledOrders++;
        }
        else{
            totalAmountSpent += price*new_order.getAmount();
        }
        if(new_order.isModified()){
            countModifiedOrders++;
        }
        if(!new_order.isCancelled()){
            if(productAmountMap.containsKey(new_order.getProductID())){
                Long oldAmount = productAmountMap.get(new_order.getProductID());
                productAmountMap.put(new_order.getProductID(),oldAmount + new_order.getAmount());
            }
            else {
                productAmountMap.put(new_order.getProductID(), new_order.getAmount());
            }
        }
        orderIdList.add(new_order.getOrderID());
    }
    Integer getCountOrders() {
        return countOrders;
    }

    Integer getCountCancelledOrders() {
        return countCancelledOrders;
    }

    Integer getCountModifiedOrders() {
        return countModifiedOrders;
    }

    Long getTotalAmountSpent() {
        return totalAmountSpent;
    }

}
