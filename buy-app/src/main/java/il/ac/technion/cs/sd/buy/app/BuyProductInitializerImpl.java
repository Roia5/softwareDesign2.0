package il.ac.technion.cs.sd.buy.app;

import com.google.inject.Inject;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializer;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;
import javafx.util.Pair;
import Database.Reader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class BuyProductInitializerImpl implements BuyProductInitializer {
    private Map<String,Product> productMap = new HashMap<>();   //productID -> product
    private Map<String,Order> orderMap = new HashMap<>();       //orderID -> order
    private Map<String,User> userMap = new HashMap<>();

    public static String product_index_filename = "product_index";
    public static String product_data_filename = "product_data";
    public static String product_orderlist_filename = "product_orderlist";

    public static String user_index_filename = "user_index";
    public static String user_prodlist_filename = "user_prodlist";
    public static String user_data_filename = "user_data";


    public static String order_filename = "order_db";

    private Reader productIndexReader, productDataReader, productOrderlistReader;
    private Reader userIndexReader, userProdlistReader, userDataReader;
    private Reader orderReader;

    @Inject
    public BuyProductInitializerImpl(FutureLineStorageFactory lsf) {
        productIndexReader = new Reader(lsf, product_index_filename);
        productDataReader = new Reader(lsf, product_data_filename);
        productOrderlistReader = new Reader(lsf, product_orderlist_filename);
        userIndexReader = new Reader(lsf, user_index_filename);
        userProdlistReader = new Reader(lsf, user_prodlist_filename);
        userDataReader = new Reader(lsf, user_data_filename);
        orderReader = new Reader(lsf, order_filename);
    }
    private Document parseXML(String xmlData){
        DocumentBuilderFactory dbFactory
                = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(new InputSource(new StringReader(xmlData)));
            doc.getDocumentElement().normalize();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return doc;
    }

    private void handleAction(JSONObject row){
        Order currOrd;
        switch(row.getString("type")){
            case "order":
                String orderID = row.getString("order-id");
                orderMap.put(orderID,new Order(orderID,row.getString("user-id"),row.getString("product-id"),
                        row.getInt("amount")));
                break;
            case "product":
                String productID = row.getString("id");
                productMap.put(productID,new Product(productID,row.getInt("price")));
                break;
            case "cancel-order":
                currOrd = orderMap.get(row.getString("order-id"));
                if(currOrd != null) {
                    currOrd.cancel();
                }
                break;
            case "modify-order":
                currOrd = orderMap.get(row.getString("order-id"));
                if(currOrd != null) {
                    currOrd.modify(row.getInt("amount"));
                }
                break;
        }
    }
    private void parseJson(String jsonData){
        JSONArray array = null;
        if (jsonData != null) {
            array = new JSONArray(jsonData);
        }
        assert array != null;
        for (int i = 0; i < array.length(); i++) {
            JSONObject row = array.getJSONObject(i);
            handleAction(row);
        }
    }
    private void removeInvalidOrders(){
        Set<String> productIDSet = productMap.keySet();
        orderMap.entrySet().removeIf((e) -> !(productIDSet.contains(e.getValue().getProductID())));
    }
    private void initializeUserDB(){
        orderMap.forEach((orderID,order)->{
            String userID = order.getUserID();
            Integer price = productMap.get(order.getProductID()).getPrice();
            User curr_user;
            if(userMap.containsKey(userID)){
                curr_user = userMap.get(userID);
                curr_user.addOrder(order,price);
            }
            else{
                curr_user = new User(userID);
                curr_user.addOrder(order,price);
                userMap.put(userID,curr_user);
            }
        });
    }
    private void updateProductsWithAmountData(){
        orderMap.forEach((orderID,order)->{
            Product curr_prod = productMap.get(order.getProductID());
            curr_prod.addPurchase(order.getAmount());
        });
        productMap.forEach((prodID,product)-> product.calcAverageAmountBought());
    }
    private List<String> buildLinesForOrderFile(){
        orderMap.forEach((orderID,order)->{
            //TODO
        });
        return null;
    }
    private void setup(){
        removeInvalidOrders();
        initializeUserDB();
        updateProductsWithAmountData();
        List<String> orderFileLines = buildLinesForOrderFile();
    }
    @Override
    public CompletableFuture<Void> setupXml(String xmlData) {
        Document doc = parseXML(xmlData);
        setup();
        return null;
    }

    @Override
    public CompletableFuture<Void> setupJson(String jsonData) {
        parseJson(jsonData);
        setup();
        return null;
    }
}