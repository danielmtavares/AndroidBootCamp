import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dtavares on 2/16/15.
 */
public class ImageLinksCountDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "imageDatabase";
    private static final String TABLE_NAME = "image_counts";
    private static final int DATABASE_VERSION = 1;

    private static final String KEY_ID = "id";
    private static final String KEY_URL = "url";
    private static final String KEY_TIMES_SEEN = "times_seen";

    public ImageLinksCountDatabase (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ITEMS_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s INTEGER)",
                TABLE_NAME, KEY_ID, KEY_URL, KEY_TIMES_SEEN);
        Log.d("DEBUG", CREATE_ITEMS_TABLE);
        db.execSQL(CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 1) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    public ImageLinkCount getRecord(String url) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] {KEY_ID, KEY_URL, KEY_TIMES_SEEN},
                String.format("%s= ? AND %s = ?", KEY_URL), new String[] {url},
                null, null, null, null);

        if (cursor != null) {
            if (!cursor.moveToFirst()) {
                return null;
            }
//            ImageLinksCount imageLinkCount = new ImageLinkCount();
//            imageLinkCount.setId();
//            imageLinkCount.setUrl();
//            imageLinkCount.set
        }

        return null;
    }

    public ImageLinkCount getOrCreateRecord(String url) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.gut(KEY_URL, url);
    }
}
