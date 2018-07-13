package abhishek.redvelvet.com.perfect_call_manager.database;


import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by abhishek on 3/6/18.
 */

public class BlockedContactsData extends RealmObject {

    @PrimaryKey
    private String  c_name;
    private RealmList<String> c_number;

    //String
    public static final String NAME = "name";
    public static final String NUMBER = "number";



    public RealmList<String> getC_number() {
        return c_number;
    }

    public void setC_number(RealmList<String> c_number) {
        this.c_number = c_number;
    }

    public String getName() {
        return c_name;
    }

    public void setName(String c_name) {
        this.c_name = c_name;
    }
}
