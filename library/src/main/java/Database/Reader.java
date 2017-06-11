/**
 * Created by dani9590 on 23/04/17.
 */
package Database;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class Reader {
    private CompletableFuture<FutureLineStorage> st = null;
    public Reader(FutureLineStorageFactory st_factory, String filename) {
        st = st_factory.open(filename);
    }



    private CompletableFuture<Void> insertInFuture(FutureLineStorage fls,String[] stringArray) {
        for (String string : stringArray) {
            fls.appendLine(string);
        }

        return CompletableFuture.completedFuture(null);
    }
    /*
    * if you want to do your own sorting, pass false to 'toSort'
    */
    public void insertStrings(Collection<String> stringsCollection, boolean toSort) {
        String[] stringsArray = new String[stringsCollection.size()];
        stringsCollection.toArray(stringsArray);
        if (toSort)
            Arrays.sort(stringsArray);


        st = st.thenCompose(fls -> insertInFuture(fls, stringsArray)).thenCompose(v -> st);

    }



    private CompletableFuture<String> futureBinarySearch(int first, int last,String id, String delimiter, int index) {
        if (first > last)
            return CompletableFuture.completedFuture(null);
        int middle = first + (last - first) / 2;
        return st.thenCompose(fls -> fls.read(middle)).thenCompose(string -> {
            String[] keyValue = string.split(delimiter);
            int compareResult = keyValue[0].compareTo(id);
            if (compareResult == 0)
                return CompletableFuture.completedFuture(keyValue[index]);
            if (compareResult > 0)
                return futureBinarySearch(first, middle - 1,id, delimiter,index);
            else
                return futureBinarySearch(middle + 1, last,id, delimiter,index);
        });
    }

    public CompletableFuture<String> find(String id, String delimiter, int index)  {
        return st.thenCompose(FutureLineStorage::numberOfLines)
                .thenCompose(lineNumbers->futureBinarySearch(0, lineNumbers - 1,id,delimiter,index));


    }
    //get the info if you know the line number
    public CompletableFuture<String> find(int lineNum, String delimiter, int index) throws InterruptedException {
        //Add boundary checks
        if (lineNum < 0)
            return CompletableFuture.completedFuture(null);

        return st.thenCompose(FutureLineStorage::numberOfLines)
                .thenCompose(numOfLines -> lineNum >= numOfLines ? CompletableFuture.completedFuture(null) : st)
                .thenCompose(fls -> (fls == null) ? CompletableFuture.completedFuture(null) : fls.read(lineNum))
                .thenCompose(string ->
                        string == null ? CompletableFuture.completedFuture(null) :
                                CompletableFuture.completedFuture(string.split(delimiter)[index]));


    }

    public CompletableFuture<Integer> numberOfLines() throws InterruptedException{
        return st.thenCompose(FutureLineStorage::numberOfLines);
    }

}