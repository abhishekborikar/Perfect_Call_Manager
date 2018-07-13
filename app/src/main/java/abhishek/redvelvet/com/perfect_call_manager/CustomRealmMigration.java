package abhishek.redvelvet.com.perfect_call_manager;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by abhishek on 4/6/18.
 */

public class CustomRealmMigration implements RealmMigration {
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof RealmMigration);
    }
//
//    @Override
//    public int hashCode() {
//        return 37;
//    }

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0) {
            // Migrate from v0 to v1
            oldVersion++;
        }

        if (oldVersion == 1) {
            // Migrate from v1 to v2
            oldVersion++;
        }
        Log.e("Migration old version",""+oldVersion);
        Log.e("Migration new version",""+newVersion);
//        if (oldVersion < newVersion) {
//            throw new IllegalStateException(String.format(Locale.getDefault(), "Migration missing from v%d to v%d", oldVersion, newVersion));
//        }
    }
}
