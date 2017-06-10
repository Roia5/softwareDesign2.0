package il.ac.technion.cs.sd.buy.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializer;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializerImpl;
import il.ac.technion.cs.sd.buy.ext.LineStorageModule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class basicTest {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(20);
    
    @Test
    public void testCorrectBuild(){
        String fileContents = null;
        try {
            fileContents =
                    new Scanner(new File(ExampleTest.class.
                            getResource("small.json").getFile())).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Injector injector = Guice.createInjector(new BuyProductModule(), new LineStorageModule());
        BuyProductInitializer bp = injector.getInstance(BuyProductInitializer.class);
        //BuyProductInitializerImpl bp = new BuyProductInitializerImpl();
        bp.setupJson(fileContents);

    }
}
