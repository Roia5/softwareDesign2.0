package il.ac.technion.cs.sd.buy.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roey on 09/06/2017.
 */
public class User {
    private String userID;
    private Integer countOrders;
    private Integer countCancelledOrders;
    private Integer countModifiedOrders;
    private Integer totalAmountSpent;
    private List<Order> orderList = new ArrayList<>();
    public User(String userID){
        this.userID = userID;
        countOrders = 0;
        countCancelledOrders = 0;
        countModifiedOrders = 0;
        totalAmountSpent = 0;
    }
    public void addOrder(Order new_order, Integer price){
        countOrders++;
        if(new_order.isCancelled()){
            countCancelledOrders++;
        }
        if(new_order.isModified()){
            countModifiedOrders++;
        }
        totalAmountSpent += price*new_order.getAmount();
        orderList.add(new_order);
    }
}
