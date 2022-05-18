package hanpoom.internal_cron.utility.nullables;

import org.springframework.stereotype.Service;

@Service
public class Nullables {

    public String handleNull(String nullableVal) {
        if (nullableVal == null) {
            return "";
        } else {
            return nullableVal;
        }
    }
}
