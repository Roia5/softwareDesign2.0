package il.ac.technion.cs.sd.buy.app;

public class Product{
    private String productID;
    private Integer price;
    private Integer totalAmountBought = 0;
    private Double averageAmountBought;
    private Integer numberOfOrders = 0;
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

    public void addPurchase(Integer new_amount){
        totalAmountBought += new_amount;
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
    public void setPrice(Integer price) {
        this.price = price;
    }
}
