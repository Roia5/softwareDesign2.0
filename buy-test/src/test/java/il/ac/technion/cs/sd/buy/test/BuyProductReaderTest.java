package il.ac.technion.cs.sd.buy.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializer;
import il.ac.technion.cs.sd.buy.app.BuyProductReader;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;



public class BuyProductReaderTest {
    static Injector injector;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    @After
    public void tearDown() throws Exception {
        injector.getProvider(FakeFactoryProvider.class).get().get().clean();
    }

    private static String setUpFile(String filename) throws FileNotFoundException {
        return new Scanner(new File(BuyProductReaderTest.class.getResource(filename).getFile())).useDelimiter("\\Z").next();
    }

    public static void setUp(String fileName) throws Exception {
        injector = Guice.createInjector(new BuyProductModuleFake());
        BuyProductInitializer appInit = injector.getInstance(BuyProductInitializer.class);
        if (fileName.endsWith("xml"))
            appInit.setupXml(setUpFile(fileName));
        else {
            assert fileName.endsWith("json");
            appInit.setupJson(setUpFile(fileName));
        }
    }

    @Test
    public void orderIdNotFound() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertFalse(reader.isValidOrderId("4").get());
        Assert.assertFalse(reader.isValidOrderId("6").get());
        Assert.assertTrue(reader.isValidOrderId("2").get());
        Assert.assertTrue(reader.isValidOrderId("1").get());
        Assert.assertTrue(reader.isValidOrderId("3").get());
    }

    @Test
    public void orderIdOverriddenByNonExistingProductShouldNotBeFound() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertFalse(reader.isValidOrderId("6").get());
    }

    @Test
    public void orderIdFoundEvenThoughOverridden() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertTrue(reader.isValidOrderId("2").get());
    }

    @Test
    public void orderIdFoundEvenThoughModdified() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertTrue(reader.isValidOrderId("1").get());
    }

    @Test
    public void orderIdFoundEvenThoughCancelled() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertTrue(reader.isValidOrderId("3").get());
    }

    @Test
    public void orderIdFound() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertTrue(reader.isValidOrderId("5").get());
    }

    @Test
    public void cancelledOrderFoundAsCancelled() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertTrue(reader.isCanceledOrder("3").get());
    }

    @Test
    public void nonCancelledOrderShouldNotBeCancelled() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertFalse(reader.isCanceledOrder("5").get());
        Assert.assertFalse(reader.isCanceledOrder("1").get());
    }

    @Test
    public void nonExistingOrderShouldNotBeCancelled() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertFalse(reader.isCanceledOrder("4").get());
    }

    @Test
    public void cancelledOrderOverriddenByOtherOrderShouldNotBeCancelled() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertFalse(reader.isCanceledOrder("7").get());
        Assert.assertFalse(reader.isCanceledOrder("8").get());
    }

    @Test
    public void cancelledOrderSeveralTimesFoundAsCancelled() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertTrue(reader.isCanceledOrder("9").get());
    }

    @Test
    public void modifiedOrderFoundAsModified() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertTrue(reader.isModifiedOrder("1").get());
    }

    @Test
    public void nonModifiedOrderShouldNotBeFoundAsModified() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertFalse(reader.isModifiedOrder("3").get());
        Assert.assertFalse(reader.isModifiedOrder("5").get());
    }

    @Test
    public void modifiedSeveralTimesOrderShouldeFoundAsModified() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertTrue(reader.isModifiedOrder("7").get());
    }

    @Test
    public void nonExistingOrderShouldNotBeFoundAsModified() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertFalse(reader.isModifiedOrder("4").get());
    }

    @Test
    public void invalidOrderShouldeNotFoundAsModified() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertFalse(reader.isModifiedOrder("6").get());
    }

    @Test
    public void modifiedButOverriddenOrderShouldeNotFoundAsModified() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertFalse(reader.isModifiedOrder("8").get());
    }

    @Test
    public void modifiedAndCancelledOrderShouldeFoundAsModified() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertTrue(reader.isModifiedOrder("9").get());
    }

    @Test
    public void cancelledOrderOverriddenByModifiedOrderShouldBeFoundAsModified() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertTrue(reader.isModifiedOrder("7").get());
    }

    @Test
    public void validOrderShouldReturnValidAmount() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalInt.of(5), reader.getNumberOfProductOrdered("5").get());
    }

    @Test
    public void severalModifiedOrdersShouldReturnLastAmount() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalInt.of(500000), reader.getNumberOfProductOrdered("1").get());
        Assert.assertEquals(OptionalInt.of(50), reader.getNumberOfProductOrdered("7").get());
    }

    @Test
    public void cancelledOrdersShouldReturnNegationOfAmount() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalInt.of(-10), reader.getNumberOfProductOrdered("3").get());
    }

    @Test
    public void sevrelCancelledOrdersOfSeveralModifiedOrdersShouldReturnNegationOfLastAmount() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalInt.of(-51), reader.getNumberOfProductOrdered("9").get());
    }

    @Test
    public void overriddenOrdersShouldReturnLastAmount() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalInt.of(10), reader.getNumberOfProductOrdered("2").get());
        Assert.assertEquals(OptionalInt.of(8), reader.getNumberOfProductOrdered("8").get());
    }

    @Test
    public void nonExistingOrderShouldNotReturnAnyAmount() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalInt.empty(), reader.getNumberOfProductOrdered("4").get());
    }

    @Test
    public void invalidOrderShouldNotReturnAnyAmount() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalInt.empty(), reader.getNumberOfProductOrdered("6").get());
    }

    @Test
    public void singleOrderHistoryShouldBeWithCorrectValue() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        List<Integer> res = reader.getHistoryOfOrder("5").get();
        Assert.assertEquals(1, res.size());
        assertThat(res, IsIterableContainingInOrder.contains(5));
    }

    @Test
    public void modifiedOrderShouldAppendNewAmountToHistory() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        List<Integer> res = reader.getHistoryOfOrder("1").get();
        Assert.assertEquals(2, res.size());
        assertThat(res, IsIterableContainingInOrder.contains(5, 500000));
    }

    @Test
    public void cancelledOrderShouldAppendMinusOneToHistory() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        List<Integer> res = reader.getHistoryOfOrder("3").get();
        Assert.assertEquals(2, res.size());

        assertThat(res, IsIterableContainingInOrder.contains(10, -1));
    }

    @Test
    public void overridingOrderShouldOverrideHistory() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        List<Integer> res = reader.getHistoryOfOrder("2").get();
        Assert.assertEquals(1, res.size());
        assertThat(res, IsIterableContainingInOrder.contains(10));
        res = reader.getHistoryOfOrder("8").get();
        Assert.assertEquals(1, res.size());
        assertThat(res, IsIterableContainingInOrder.contains(8));
    }

    @Test
    public void severalModifiedOrdersShouldAllBeAppendedToHistory() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        List<Integer> res = reader.getHistoryOfOrder("7").get();
        Assert.assertEquals(3, res.size());
        assertThat(res, IsIterableContainingInOrder.contains(5, 2, 50));
    }

    @Test
    public void severalCancelationsOrdersShouldOnlyBeAppendedOnceToHistory() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        List<Integer> res = reader.getHistoryOfOrder("9").get();
        Assert.assertEquals(3, res.size());
        assertThat(res, IsIterableContainingInOrder.contains(5, 51, -1));
    }

    @Test
    public void modifiedAfterCancelShouldNotOverriddeHistory() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        List<Integer> res = reader.getHistoryOfOrder("10").get();
        Assert.assertEquals(7, res.size());
        assertThat(res, IsIterableContainingInOrder.contains(1, 100, 6, 18, 7, 2, -1));
    }

    @Test
    public void nonExistingOrderShouldNotContainHistory() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getHistoryOfOrder("4").get(), is(empty()));
    }

    @Test
    public void invalidOrderShouldNotContainHistory() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getHistoryOfOrder("6").get(), is(empty()));
    }

    @Test
    public void userMadeOneOrder() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        List<String> result = reader.getOrderIdsForUser("7").get();
        Assert.assertEquals(1, result.size());
        assertThat(result, IsIterableContainingInOrder.contains("5"));
    }

    @Test
    public void severalOrdersForUserShouldBeSorted() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        List<String> result = reader.getOrderIdsForUser("2").get();
        Assert.assertEquals(2, result.size());
        assertThat(result, IsIterableContainingInOrder.contains("12", "7"));
    }

    @Test
    public void cancelledOrdersMadeByUserShouldStillCount() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        List<String> result = reader.getOrderIdsForUser("5").get();
        Assert.assertEquals(3, result.size());
        assertThat(result, IsIterableContainingInOrder.contains("10", "3", "9"));
    }

    @Test
    public void overriddenOrdersMadeByDifferentUserShouldOnlyAppearOnLastUserList() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(1, reader.getOrderIdsForUser("1").get().size());
        assertThat(reader.getOrderIdsForUser("1").get(), IsIterableContainingInOrder.contains("2"));
        Assert.assertEquals(2, reader.getOrderIdsForUser("3").get().size());
        assertThat(reader.getOrderIdsForUser("3").get(), IsIterableContainingInOrder.contains("1", "8"));
        assertThat(reader.getOrderIdsForUser("9").get(), is(empty()));
    }

    @Test
    public void nonExistingUserShouldHaveNoOrders() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getOrderIdsForUser("4").get(), is(empty()));
    }
    @Test
    public void nonExistingProductOrderedByUserShouldNotAppear() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getOrderIdsForUser("6").get(), is(empty()));
    }

    @Test
    public void userMadeOneOrderShouldGetTheProductPriceTimesAmountOrdered() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(Long.valueOf(500),reader.getTotalAmountSpentByUser("7").get());
    }
    @Test
    public void userMadeSeveralOrdersWithModifiesShouldGetSumOfProductPriceTimesLastAmountOrdered() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(Long.valueOf(10005000),reader.getTotalAmountSpentByUser("2").get());
    }
    @Test
    public void userCancelledAllHisOrdersShouldGetZero() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(Long.valueOf(0),reader.getTotalAmountSpentByUser("5").get());
    }
    @Test
    public void overriddenOrdersMadeByDifferentUserShouldGetSumOfProductPriceTimesLastAmountOrdered() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(Long.valueOf(100),reader.getTotalAmountSpentByUser("1").get());
    }
    @Test
    public void userOrderedTooManyIphonesShouldDeclareBankruptcy () throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(Long.valueOf(500008000000L),reader.getTotalAmountSpentByUser("3").get());
    }
    @Test
    public void nonExistingUserShouldNotPay() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(Long.valueOf(0),reader.getTotalAmountSpentByUser("4").get());
    }
    @Test
    public void userOrderedNonExistingProductShouldNotPay() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(Long.valueOf(0),reader.getTotalAmountSpentByUser("6").get());
    }
    @Test
    public void userWithNoCancelledOrdersShouldHaveRatioOfZero() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalDouble.of(0.0),reader.getCancelRatioForUser("3").get());
        Assert.assertEquals(OptionalDouble.of(0.0),reader.getCancelRatioForUser("1").get());
    }
    @Test
    public void userWhoCancelledSomeOfHisOrdersShouldHaveCorrectRatio() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalDouble.of(1/(double)3),reader.getCancelRatioForUser("8").get());
    }
    @Test
    public void userWhoCancelledAllOfHisOrdersShouldHaveRatioOfOne() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalDouble.of(1),reader.getCancelRatioForUser("5").get());
    }
    @Test
    public void userWhosOrderWasStolenShouldHaveNoCancelRatio() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalDouble.empty(),reader.getCancelRatioForUser("9").get());
    }
    @Test
    public void nonExistingUserShouldHaveNoCancelRatio() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalDouble.empty(),reader.getCancelRatioForUser("4").get());
    }
    @Test
    public void userOrderedNonExistingProductShouldHaveNoCancelRatio() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalDouble.empty(),reader.getCancelRatioForUser("6").get());
    }

    @Test
    public void userWithNoModifiedOrdersShouldHaveRatioOfZero() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalDouble.of(0.0),reader.getModifyRatioForUser("7").get());
        Assert.assertEquals(OptionalDouble.of(0.0),reader.getModifyRatioForUser("1").get());
    }
    @Test
    public void userWhoModifiedSomeOfHisOrdersShouldHaveCorrectRatio() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalDouble.of(0.5),reader.getModifyRatioForUser("3").get());
    }
    @Test
    public void userWhoModifiedAllOfHisOrdersShouldHaveRatioOfOne() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalDouble.of(1),reader.getModifyRatioForUser("2").get());
    }
    @Test
    public void userWhosOrderWasStolenShouldHaveNoModifiedRatio() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalDouble.empty(),reader.getModifyRatioForUser("9").get());
    }

    @Test
    public void cancelledOrderShouldNotChangeModifiedRatio() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalDouble.of(2/(double)3),reader.getModifyRatioForUser("5").get());
    }

    @Test
    public void nonExistingUserShouldHaveNoModifiedRatio() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalDouble.empty(),reader.getModifyRatioForUser("4").get());
    }
    @Test
    public void userOrderedNonExistingProductShouldHaveNoModifiedRatio() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalDouble.empty(),reader.getModifyRatioForUser("6").get());
    }

    @Test
    public void userMadeOneOrderShouldGetCorrectProductAndAmount() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Map<String,Long> expected = new TreeMap<>();
        expected.put("android",5L);
        assertThat(reader.getAllItemsPurchased("7").get(),equalTo(expected));
        expected.clear();
        expected.put("nokia",10L);
        assertThat(reader.getAllItemsPurchased("1").get(),equalTo(expected));
    }
    @Test
    public void userMadeSeveralOrdersShouldGetAllProductAndAmount() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Map<String,Long> expected = new TreeMap<>();
        expected.put("android",50L);
        expected.put("iphone",10L);
        assertThat(reader.getAllItemsPurchased("2").get(),equalTo(expected));
    }
    @Test
    public void userCanceledAllOrdersShouldHaveNoProducts() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getAllItemsPurchased("5").get().entrySet(),empty());
    }

    @Test
    public void userOrderedSameProductSeveralTimes() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Map<String,Long> expected = new TreeMap<>();
        expected.put("iphone",500008L);
        assertThat(reader.getAllItemsPurchased("3").get(),equalTo(expected));
    }

    @Test
    public void userModifiedOrderShouldReturnLastAmount() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Map<String,Long> expected = new TreeMap<>();
        expected.put("nokia",25L);
        assertThat(reader.getAllItemsPurchased("8").get(),equalTo(expected));
    }
    @Test
    public void userWhosOrderWasStolenShouldHaveNoProducts() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getAllItemsPurchased("9").get().entrySet(),empty());
    }
    @Test
    public void nonExistingUserShouldHaveNoProducts() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getAllItemsPurchased("4").get().entrySet(),empty());
    }
    @Test
    public void userOrderedNonExistingProductShouldHaveNoProducts() throws Exception {
        setUp("ourData.xml");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getAllItemsPurchased("6").get().entrySet(),empty());
    }

    @Test
    public void productOrderedOnlyOnceByOneUser() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getUsersThatPurchased("android").get(), IsIterableContainingInOrder.contains("3"));
    }
    @Test
    public void productOrderedBySeveralUsersShouldReturnSortedUsers() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getUsersThatPurchased("iphone").get(), IsIterableContainingInOrder.contains("1","2"));
    }
    @Test
    public void productOrderedByUserAndThenCancelledShouldReturnNoUsers() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getUsersThatPurchased("nokia").get(),is(empty()));
    }
    @Test
    public void productOrderedByUserAndThenTheOrderWasOverriddenWithAnotherProductShouldNotAppearForUser() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getUsersThatPurchased("idroid").get(),is(empty()));
    }
    @Test
    public void productOrderedByUserAndThenModifiedShouldReturnUsers() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getUsersThatPurchased("dell").get(), IsIterableContainingInOrder.contains("1"));
    }
    @Test
    public void productNeverOrderedByUserShouldHaveNoUsersThatPurchased() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getUsersThatPurchased("mac").get(),is(empty()));
    }
    @Test
    public void nonExistingProductShouldHaveNoUsersThatPurchased() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getUsersThatPurchased("not a mac").get(),is(empty()));
    }
    @Test
    public void productOrderedOnlyOnceInOneOrder() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getOrderIdsThatPurchased("android").get(), IsIterableContainingInOrder.contains("1"));
    }
    @Test
    public void productOrderedInSeveralOrders() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getOrderIdsThatPurchased("iphone").get(), IsIterableContainingInOrder.contains("2","3"));
    }

    @Test
    public void productOrderedAndThenModifiedShouldReturnOrder() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getOrderIdsThatPurchased("dell").get(), IsIterableContainingInOrder.contains("7"));
    }

    @Test
    public void productOrderedByUserAndThenCancelledShouldReturnTheOrders() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getOrderIdsThatPurchased("nokia").get(), IsIterableContainingInOrder.contains("5"));
    }
    @Test
    public void productOrderedOnceAndThenOverriddenWithAnotherProductShouldNotAppearForOrder() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getOrderIdsThatPurchased("idroid").get(), is(empty()));
    }
    @Test
    public void productNeverOrderedShouldHaveNoOrders() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getOrderIdsThatPurchased("mac").get(),is(empty()));
    }
    @Test
    public void nonExistingProductShouldHaveNoOrders() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getOrderIdsThatPurchased("not a mac").get(),is(empty()));
    }

    @Test
    public void productOrderedOnceShouldReturnCorrectAmount() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalLong.of(10L),reader.getTotalNumberOfItemsPurchased("android").get());
        Assert.assertEquals(OptionalDouble.of(10),reader.getAverageNumberOfItemsPurchased("android").get());
    }
    @Test
    public void productOrderedSeveralTimesShouldReturnSumOfAmounts() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalLong.of(29L),reader.getTotalNumberOfItemsPurchased("iphone").get());
        Assert.assertEquals(OptionalDouble.of(14.5),reader.getAverageNumberOfItemsPurchased("iphone").get());
        Assert.assertEquals(OptionalLong.of(10L),reader.getTotalNumberOfItemsPurchased("android").get());
        Assert.assertEquals(OptionalDouble.of(10),reader.getAverageNumberOfItemsPurchased("android").get());
        assertThat(reader.getOrderIdsThatPurchased("nokia").get(), IsIterableContainingInOrder.contains("5"));
    }
    @Test
    public void productOrderedAndThenModifiedShouldReturnLastAmount() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalLong.of(7L),reader.getTotalNumberOfItemsPurchased("dell").get());
        Assert.assertEquals(OptionalDouble.of(7),reader.getAverageNumberOfItemsPurchased("dell").get());
    }
    @Test
    public void productOrderedAndThenCancelledShouldHaveNoItemsPurchased() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalLong.of(0),reader.getTotalNumberOfItemsPurchased("nokia").get());
        Assert.assertEquals(OptionalDouble.of(0.0),reader.getAverageNumberOfItemsPurchased("nokia").get());
    }
    @Test
    public void productOrderedOnceAndThenOverriddenWithAnotherProductShouldHaveNoItemsPurchased() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalLong.of(0),reader.getTotalNumberOfItemsPurchased("idroid").get());
        Assert.assertEquals(OptionalDouble.of(0.0),reader.getAverageNumberOfItemsPurchased("idroid").get());
    }
    @Test
    public void productNeverOrderedShouldHaveNoItemsPurchased() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalLong.of(0),reader.getTotalNumberOfItemsPurchased("mac").get());
        Assert.assertEquals(OptionalDouble.of(0.0),reader.getAverageNumberOfItemsPurchased("mac").get());
    }
    @Test
    public void nonExistingProductShouldHaveNoItemsPurchased() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalLong.empty(),reader.getTotalNumberOfItemsPurchased("not a mac").get());
        Assert.assertEquals(OptionalDouble.empty(),reader.getAverageNumberOfItemsPurchased("not a mac").get());
    }

    @Test
    public void productOrderdAndThenModifiedSeveralTimesShouldReturnLastAmount() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Assert.assertEquals(OptionalLong.of(2L),reader.getTotalNumberOfItemsPurchased("pizza").get());
        Assert.assertEquals(OptionalDouble.of(2.0),reader.getAverageNumberOfItemsPurchased("pizza").get());
    }

    @Test
    public void productOrderedByOneUserShouldReturnTheUserAndTheAmount() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Map<String,Long> expected = new TreeMap<>();
        expected.put("3",10L);
        assertThat(reader.getItemsPurchasedByUsers("android").get(),equalTo(expected));
    }
    @Test
    public void productOrderedBySeveralUsersShouldReturnAllUsers() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Map<String,Long> expected = new TreeMap<>();
        expected.put("2",12L);
        expected.put("1",17L);
        assertThat(reader.getItemsPurchasedByUsers("iphone").get(),equalTo(expected));
    }
    @Test
    public void productOrderedAndThenCancelledShouldNotReturnUser() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getItemsPurchasedByUsers("nokia").get().entrySet(),empty());
    }
    @Test
    public void productOrderedAndThenModifiedShouldReturnUpdatedAmount() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Map<String,Long> expected = new TreeMap<>();
        expected.put("1",7L);
        assertThat(reader.getItemsPurchasedByUsers("dell").get(),equalTo(expected));
        expected.clear();
        expected.put("1",2L);
        assertThat(reader.getItemsPurchasedByUsers("pizza").get(),equalTo(expected));
    }

    @Test
    public void productOrderedSeveralTimesBySameUserShouldReturnSumOfOrders() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Map<String,Long> expected = new TreeMap<>();
        expected.put("addict",781L);
        assertThat(reader.getItemsPurchasedByUsers("drugs").get(),equalTo(expected));
    }

    @Test
    public void productOrderedByDifferentUsersWithMultipleChangesShouldReturnCorrectAmounts() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Map<String,Long> expected = new TreeMap<>();
        expected.put("addict2",2000L);
        expected.put("addict3",5050L);
        assertThat(reader.getItemsPurchasedByUsers("more drugs").get(),equalTo(expected));
    }

    @Test
    public void productOrderedWithSameOrderIdByDifferentUsersShouldOnlyAppearForLastUser() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        Map<String,Long> expected = new TreeMap<>();
        expected.put("addict5",50L);
        assertThat(reader.getItemsPurchasedByUsers("more").get(),equalTo(expected));
    }
    @Test
    public void productNeverOrderedShouldHaveNoUsersThatPurchased() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getItemsPurchasedByUsers("mac").get().entrySet(),empty());
    }
    @Test
    public void nonExistingProductShouldHaveNoUsersMapThatPurchased() throws Exception {
        setUp("ourData.json");
        BuyProductReader reader = injector.getInstance(BuyProductReader.class);
        assertThat(reader.getItemsPurchasedByUsers("not a mac").get().entrySet(),empty());

    }
}
