package il.ac.technion.cs.sd.buy.app;

import java.util.ArrayList;
import java.util.List;

public class Order{
    private String orderID;
    private String userID;
    private String productID;
    private Integer amount;
    private boolean isValid = false;
    private boolean isModified = false;
    private boolean isCancelled = false;
    private List<Integer> listAmounts = new ArrayList<>();

    public Order(String orderID, String userID, String productID, Integer amount) {
        this.orderID = orderID;
        this.userID = userID;
        this.productID = productID;
        this.amount = amount;
        listAmounts.add(amount);
    }

    public void cancel(){
        if(isCancelled){
            return;
        }
        isCancelled = true;
        listAmounts.add(-1);
    }
    public void modify(Integer newAmount){
        this.amount = newAmount;
        isModified = true;
        if(isCancelled){
            isCancelled = false;
            listAmounts.set(listAmounts.size()-1,newAmount);
        }
        else {
            listAmounts.add(newAmount);
        }
    }
    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }
}
