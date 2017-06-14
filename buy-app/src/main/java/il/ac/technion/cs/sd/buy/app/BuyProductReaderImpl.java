package il.ac.technion.cs.sd.buy.app;

import Database.Reader;
import com.google.inject.Inject;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class BuyProductReaderImpl implements BuyProductReader {

    private Reader productIndexReader, productDataReader, productOrderListReader;
    private Reader userIndexReader, userProdListReader, userDataReader;
    private Reader orderReader;

    @Inject
    public BuyProductReaderImpl(FutureLineStorageFactory lsf) {
        productIndexReader = new Reader(lsf, BuyProductInitializerImpl.product_index_filename);
        productDataReader = new Reader(lsf, BuyProductInitializerImpl.product_data_filename);
        productOrderListReader = new Reader(lsf, BuyProductInitializerImpl.product_orderlist_filename);
        userIndexReader = new Reader(lsf, BuyProductInitializerImpl.user_index_filename);
        userProdListReader = new Reader(lsf, BuyProductInitializerImpl.user_prodlist_filename);
        userDataReader = new Reader(lsf, BuyProductInitializerImpl.user_data_filename);
        orderReader = new Reader(lsf, BuyProductInitializerImpl.order_filename);
    }
    @Override
    public CompletableFuture<Boolean> isValidOrderId(String orderId) {
        CompletableFuture<String> orderLine = orderReader.find(orderId,"_");
        return orderLine.thenApply(Objects::nonNull);
    }

    private boolean getOrderStat(String orderLineString, Integer index){
        if(orderLineString==null){
            return false;
        }
        String stat = orderLineString.split("_")[index];
        return stat.equals("1");
    }
    @Override
    public CompletableFuture<Boolean> isCanceledOrder(String orderId) {
        CompletableFuture<String> orderLine = orderReader.find(orderId,"_");
        return orderLine.thenApply((orderLineString)-> getOrderStat(orderLineString,2));
    }

    @Override
    public CompletableFuture<Boolean> isModifiedOrder(String orderId) {
        CompletableFuture<String> orderLine = orderReader.find(orderId,"_");
        return orderLine.thenApply((orderLineString)->getOrderStat(orderLineString,3));
    }

    @Override
    public CompletableFuture<OptionalInt> getNumberOfProductOrdered(String orderId) {
        CompletableFuture<String> orderLine = orderReader.find(orderId,"_");
        return orderLine.thenApply((orderLineString)->{
            if(orderLineString==null){
                return OptionalInt.empty();
            }
            String isCancelled = orderLineString.split("_")[2];
            Integer numOrdered = Integer.parseInt(orderLineString.split("_")[1]);
            if(isCancelled.equals("1")){
                numOrdered = -numOrdered;
            }
            return OptionalInt.of(numOrdered);
        });
    }

    @Override
    public CompletableFuture<List<Integer>> getHistoryOfOrder(String orderId) {
        CompletableFuture<String> orderLine = orderReader.find(orderId,"_");
        List<Integer> amountList = new ArrayList<>();
        return orderLine.thenApply((orderLineString)->{
            if(orderLineString==null){
                return amountList;
            }
            String[] listAsStrings = orderLineString.split("_")[4].split(",");
            for(int i=0;i<listAsStrings.length;i++){
                amountList.add(Integer.parseInt(listAsStrings[i]));
            }
            return amountList;
        });
    }

    @Override
    public CompletableFuture<List<String>> getOrderIdsForUser(String userId) {
        CompletableFuture<String> indexUserLine = userIndexReader.find(userId, "_");
        List<String> orderIdList = new ArrayList<>();
        return indexUserLine.thenApply((indexString)->{
            if(indexString == null){
                return orderIdList;
            }
            CompletableFuture<String> userLine = userDataReader.find(Integer.parseInt(indexString.split("_")[1]));
            userLine.thenApply((userLineString)->{
                String[] listAsStrings = userLineString.split("_")[5].split(",");
                orderIdList.addAll(Arrays.asList(listAsStrings).subList(0, listAsStrings.length));
                return orderIdList;
            });
            orderIdList.sort(String::compareTo);
            return orderIdList;
        });
    }

    @Override
    public CompletableFuture<Long> getTotalAmountSpentByUser(String userId) {
        CompletableFuture<String> indexUserLine = userIndexReader.find(userId, "_");
        final Long[] totalAmount = new Long[1];
        return indexUserLine.thenCompose((indexString)-> {
                    if (indexString == null) {
                        return CompletableFuture.completedFuture(null);
                    }
                    return userDataReader.find(Integer.parseInt(indexString.split("_")[1]));
                }).thenApply((userLineString)-> {
                if(userLineString==null){
                    return 0L;
                }
                totalAmount[0] = Long.parseLong(userLineString.split("_")[4]);
                return totalAmount[0];
            });
    }

    @Override
    public CompletableFuture<List<String>> getUsersThatPurchased(String productId) {
        CompletableFuture<String> indexProdLine = productIndexReader.find(productId, "_");
        List<String> userIdList = new ArrayList<>();
        return indexProdLine.thenCompose((indexString)-> {
            if (indexString == null) {
                return CompletableFuture.completedFuture(null);
            }
            return productDataReader.find(Integer.parseInt(indexString.split("_")[1]));
        }).thenApply((prodLineString)-> {
            if(prodLineString==null){
                return userIdList;
            }
            String[] lineArray = prodLineString.split("_");
            if (lineArray.length < 4){
                return userIdList;
            }
            String[] pairUserIDAndAmount = prodLineString.split("_")[3].split(",");
            for(int i=0;i<pairUserIDAndAmount.length;i++){
                userIdList.add(pairUserIDAndAmount[i].split("-")[0]);
            }
            return userIdList;
        });
    }

    @Override
    public CompletableFuture<List<String>> getOrderIdsThatPurchased(String productId) {
        CompletableFuture<String> indexProdLine = productIndexReader.find(productId, "_");
        List<String> orderIdList = new ArrayList<>();
        return indexProdLine.thenCompose((indexString)-> {
            if (indexString == null) {
                return CompletableFuture.completedFuture(null);
            }
            return productOrderListReader.find(Integer.parseInt(indexString.split("_")[1]));
        }).thenApply((prodLineString)->{
            if(prodLineString==null){
                return orderIdList;
            }
            String[] lineArray = prodLineString.split("_");
            if(lineArray.length<2){
                return orderIdList;
            }
            String[] orderIdArray = lineArray[1].split(",");
            orderIdList.addAll(Arrays.asList(orderIdArray).subList(0, orderIdArray.length));
            return orderIdList;
        });
    }

    @Override
    public CompletableFuture<OptionalLong> getTotalNumberOfItemsPurchased(String productId) {
        CompletableFuture<String> indexProdLine = productIndexReader.find(productId, "_");
        return indexProdLine.thenCompose((indexString)-> {
            if (indexString == null) {
                return CompletableFuture.completedFuture(null);
            }
            return productDataReader.find(Integer.parseInt(indexString.split("_")[1]));
        }).thenApply((prodLineString)->{
            if(prodLineString==null){
                return OptionalLong.empty();
            }
            return OptionalLong.of(Long.parseLong(prodLineString.split("_")[2]));
        });
    }

    @Override
    public CompletableFuture<OptionalDouble> getAverageNumberOfItemsPurchased(String productId) {
        CompletableFuture<String> indexProdLine = productIndexReader.find(productId, "_");
        return indexProdLine.thenCompose((indexString)-> {
            if (indexString == null) {
                return CompletableFuture.completedFuture(null);
            }
            return productDataReader.find(Integer.parseInt(indexString.split("_")[1]));
        }).thenApply((prodLineString)->{
            if(prodLineString==null){
                return OptionalDouble.empty();
            }
            return OptionalDouble.of(Double.parseDouble(prodLineString.split("_")[1]));
        });
    }

    @Override
    public CompletableFuture<OptionalDouble> getCancelRatioForUser(String userId) {
        CompletableFuture<String> indexUserLine = userIndexReader.find(userId, "_");
        return indexUserLine.thenCompose((indexString)-> {
            if (indexString == null) {
                return CompletableFuture.completedFuture(null);
            }
            return userDataReader.find(Integer.parseInt(indexString.split("_")[1]));
        }).thenApply((userLineString)-> {
            if(userLineString==null){
                return OptionalDouble.empty();
            }
            Integer countOrders = Integer.parseInt(userLineString.split("_")[1]);
            Integer countCancelled = Integer.parseInt(userLineString.split("_")[2]);
            if(countOrders.equals(0)){
                return OptionalDouble.of(0.0);
            }
            return OptionalDouble.of(((double)countCancelled/countOrders));
        });
    }

    @Override
    public CompletableFuture<OptionalDouble> getModifyRatioForUser(String userId) {
        CompletableFuture<String> indexUserLine = userIndexReader.find(userId, "_");
        return indexUserLine.thenCompose((indexString)-> {
            if (indexString == null) {
                return CompletableFuture.completedFuture(null);
            }
            return userDataReader.find(Integer.parseInt(indexString.split("_")[1]));
        }).thenApply((userLineString)-> {
            if(userLineString==null){
                return OptionalDouble.empty();
            }
            Integer countOrders = Integer.parseInt(userLineString.split("_")[1]);
            Integer countModified = Integer.parseInt(userLineString.split("_")[3]);
            if(countOrders.equals(0)){
                return OptionalDouble.of(0.0);
            }
            return OptionalDouble.of(((double)countModified/countOrders));
        });
    }

    @Override
    public CompletableFuture<Map<String, Long>> getAllItemsPurchased(String userId) {
        CompletableFuture<String> indexUserLine = userIndexReader.find(userId, "_");
        Map<String, Long> prodIdAmountMap = new HashMap<>();
        return indexUserLine.thenCompose((indexString)-> {
            if (indexString == null) {
                return CompletableFuture.completedFuture(null);
            }
            return userProdListReader.find(Integer.parseInt(indexString.split("_")[1]));
        }).thenApply((userLineString)-> {
            if(userLineString==null){
                return prodIdAmountMap;
            }
            if(userLineString.split("_").length<2){
                return prodIdAmountMap;
            }
            String[] pairsAsStrings = userLineString.split("_")[1].split(",");
            for(int i=0;i<pairsAsStrings.length;i++){
                prodIdAmountMap.put(pairsAsStrings[i].split("-")[0],
                        Long.parseLong(pairsAsStrings[i].split("-")[1]));
            }
            return prodIdAmountMap;
        });
    }

    @Override
    public CompletableFuture<Map<String, Long>> getItemsPurchasedByUsers(String productId) {
        CompletableFuture<String> indexProdLine = productIndexReader.find(productId, "_");
        Map<String, Long> userIdAmountMap = new HashMap<>();
        return indexProdLine.thenCompose((indexString)-> {
            if (indexString == null) {
                return CompletableFuture.completedFuture(null);
            }
            return productDataReader.find(Integer.parseInt(indexString.split("_")[1]));
        }).thenApply((prodLineString)->{
            if(prodLineString==null){
                return userIdAmountMap;
            }
            if(prodLineString.split("_").length<4){
                return userIdAmountMap;
            }
            String[] pairsAsStrings = prodLineString.split("_")[3].split(",");
            for(int i=0;i<pairsAsStrings.length;i++){
                String[] pairAsArray = pairsAsStrings[i].split("-");
                userIdAmountMap.put(pairAsArray[0],
                        Long.parseLong(pairAsArray[1]));
            }
            return userIdAmountMap;
        });
    }
}
