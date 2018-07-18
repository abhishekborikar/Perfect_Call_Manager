package abhishek.redvelvet.com.perfect_call_manager.database;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by abhishek on 3/6/18.
 */

public class BlockedContactsData extends RealmObject {


    public void setNumber(String number) {
        this.number = number;
    }

    private String      number;
    @PrimaryKey
    private String      name;
    private int         type;

    //TYPES
    public static final int NO_TYPE = 0;
    public static final int TYPE_COMPANY = 1;
    public static final int TYPE_PERSONAL = 2;

    //String
    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String TYPE = "type";

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
