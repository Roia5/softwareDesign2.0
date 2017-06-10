package il.ac.technion.cs.sd.buy.app;

import Database.Reader;
import com.google.inject.Inject;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static il.ac.technion.cs.sd.buy.app.BuyProductInitializerImpl.product_index_filename;

/**
 * Created by Roey on 09/06/2017.
 */
public class BuyProductReaderImpl implements BuyProductReader {

    private Reader productIndexReader, productDataReader, productOrderlistReader;
    private Reader userIndexReader, userProdlistReader, userDataReader;
    private Reader orderReader;

    @Inject
    public BuyProductReaderImpl(FutureLineStorageFactory lsf) {
        productIndexReader = new Reader(lsf, BuyProductInitializerImpl.product_index_filename);
        productDataReader = new Reader(lsf, BuyProductInitializerImpl.product_data_filename);
        productOrderlistReader = new Reader(lsf, BuyProductInitializerImpl.product_orderlist_filename);
        userIndexReader = new Reader(lsf, BuyProductInitializerImpl.user_index_filename);
        userProdlistReader = new Reader(lsf, BuyProductInitializerImpl.user_prodlist_filename);
        userDataReader = new Reader(lsf, BuyProductInitializerImpl.user_data_filename);
        orderReader = new Reader(lsf, BuyProductInitializerImpl.order_filename);
    }
    @Override
    public CompletableFuture<Boolean> isValidOrderId(String orderId) {
        orderReader.find(orderId," ", 0);

        return null;
    }

    @Override
    public CompletableFuture<Boolean> isCanceledOrder(String orderId) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> isModifiedOrder(String orderId) {
        return null;
    }

    @Override
    public CompletableFuture<OptionalInt> getNumberOfProductOrdered(String orderId) {
        return null;
    }

    @Override
    public CompletableFuture<List<Integer>> getHistoryOfOrder(String orderId) {
        return null;
    }

    @Override
    public CompletableFuture<List<String>> getOrderIdsForUser(String userId) {
        return null;
    }

    @Override
    public CompletableFuture<Long> getTotalAmountSpentByUser(String userId) {
        return null;
    }

    @Override
    public CompletableFuture<List<String>> getUsersThatPurchased(String productId) {
        return null;
    }

    @Override
    public CompletableFuture<List<String>> getOrderIdsThatPurchased(String productId) {
        return null;
    }

    @Override
    public CompletableFuture<OptionalLong> getTotalNumberOfItemsPurchased(String productId) {
        return null;
    }

    @Override
    public CompletableFuture<OptionalDouble> getAverageNumberOfItemsPurchased(String productId) {
        return null;
    }

    @Override
    public CompletableFuture<OptionalDouble> getCancelRatioForUser(String userId) {
        return null;
    }

    @Override
    public CompletableFuture<OptionalDouble> getModifyRatioForUser(String userId) {
        return null;
    }

    @Override
    public CompletableFuture<Map<String, Long>> getAllItemsPurchased(String userId) {
        return null;
    }

    @Override
    public CompletableFuture<Map<String, Long>> getItemsPurchasedByUsers(String productId) {
        return null;
    }
}
