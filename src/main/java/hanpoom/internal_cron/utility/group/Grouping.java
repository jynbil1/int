package hanpoom.internal_cron.utility.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Grouping<T> {

    public List<List<T>> groupByNumberSet(List<T> listObject, int noOfItemPerGroup) {
        List<List<T>> listOfListObject = new ArrayList<>();
        List<T> newListObject = new ArrayList<>();
        int index = 0;

        if (noOfItemPerGroup >= listObject.size()) {
            return Arrays.asList(listObject);
        } else {
            for (T object : listObject) {
                ++index;
                newListObject.add(object);
                if (index % noOfItemPerGroup == 0 || index == listObject.size()) {
                    listOfListObject.add(newListObject);
                    newListObject = new ArrayList<>();
                }
            }
        }
        return listOfListObject;
    }
}

