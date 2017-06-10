package il.ac.technion.cs.sd.buy.test;

import Database.Reader;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializer;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializerImpl;
import il.ac.technion.cs.sd.buy.app.BuyProductReader;
import il.ac.technion.cs.sd.buy.app.BuyProductReaderImpl;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

// This module is in the testing project, so that it could easily bind all dependencies from all levels.
class BuyProductModule extends AbstractModule {
  private FutureLineStorageFactory lsf = null;
  BuyProductModule() {}
  BuyProductModule(FutureLineStorageFactory lsf) {
    this.lsf = lsf;
  }
  @Override
  protected void configure() {
    bind(BuyProductInitializer.class).to(BuyProductInitializerImpl.class);
    bind(BuyProductReader.class).to(BuyProductReaderImpl.class);
      bind(Reader.class).annotatedWith(Names.named("order_filename"))
              .toInstance(new Reader(lsf,BuyProductInitializerImpl.order_filename));
      bind(Reader.class).annotatedWith(Names.named("user_prodlist_filename"))
              .toInstance(new Reader(lsf,BuyProductInitializerImpl.user_prodlist_filename));
      bind(Reader.class).annotatedWith(Names.named("user_data_filename"))
              .toInstance(new Reader(lsf,BuyProductInitializerImpl.user_data_filename));
      bind(Reader.class).annotatedWith(Names.named("user_index_filename"))
              .toInstance(new Reader(lsf,BuyProductInitializerImpl.user_index_filename));
    bind(Reader.class).annotatedWith(Names.named("product_orderlist_filename"))
            .toInstance(new Reader(lsf,BuyProductInitializerImpl.product_orderlist_filename));
    bind(Reader.class).annotatedWith(Names.named("product_data_filename"))
            .toInstance(new Reader(lsf,BuyProductInitializerImpl.product_data_filename));
    bind(Reader.class).annotatedWith(Names.named("product_index_filename"))
            .toInstance(new Reader(lsf,BuyProductInitializerImpl.product_index_filename));
  }
}