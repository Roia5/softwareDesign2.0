package il.ac.technion.cs.sd.buy.test;
import Database.Reader;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializer;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializerImpl;
import il.ac.technion.cs.sd.buy.app.BuyProductReader;
import il.ac.technion.cs.sd.buy.app.BuyProductReaderImpl;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

// This module is in the testing project, so that it could easily bind all dependencies from all levels.
public class BuyProductModuleFake extends AbstractModule {
    @Override
    protected void configure() {
        bind(FutureLineStorageFactory.class).toProvider(FakeFactoryProvider.class);
        bind(BuyProductInitializer.class).to(BuyProductInitializerImpl.class);
        bind(BuyProductReader.class).to(BuyProductReaderImpl.class);
        bind(Reader.class).annotatedWith(Names.named("order_filename"))
                .toInstance(new Reader(new FutureLineStorageFactoryFake(), BuyProductInitializerImpl.order_filename));
        bind(Reader.class).annotatedWith(Names.named("user_prodlist_filename"))
                .toInstance(new Reader(new FutureLineStorageFactoryFake(), BuyProductInitializerImpl.user_prodlist_filename));
        bind(Reader.class).annotatedWith(Names.named("user_data_filename"))
                .toInstance(new Reader(new FutureLineStorageFactoryFake(), BuyProductInitializerImpl.user_data_filename));
        bind(Reader.class).annotatedWith(Names.named("user_index_filename"))
                .toInstance(new Reader(new FutureLineStorageFactoryFake(), BuyProductInitializerImpl.user_index_filename));
        bind(Reader.class).annotatedWith(Names.named("product_orderlist_filename"))
                .toInstance(new Reader(new FutureLineStorageFactoryFake(), BuyProductInitializerImpl.product_orderlist_filename));
        bind(Reader.class).annotatedWith(Names.named("product_data_filename"))
                .toInstance(new Reader(new FutureLineStorageFactoryFake(), BuyProductInitializerImpl.product_data_filename));
        bind(Reader.class).annotatedWith(Names.named("product_index_filename"))
                .toInstance(new Reader(new FutureLineStorageFactoryFake(), BuyProductInitializerImpl.product_index_filename));
    }
}
@Singleton
class FakeFactoryProvider implements Provider<FutureLineStorageFactoryFake>{
    static FutureLineStorageFactoryFake ret = new FutureLineStorageFactoryFake();
    @Override
    public FutureLineStorageFactoryFake get() {
        return ret;
    }
}
