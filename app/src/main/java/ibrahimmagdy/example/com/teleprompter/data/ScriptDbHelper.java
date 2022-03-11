package ibrahimmagdy.example.com.teleprompter.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScriptDbHelper  extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "scripts.db";

    private static final int DATABASE_VERSION = 7;

    public ScriptDbHelper(Context context){

        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_SCRIPT_TABLE = "CREATE TABLE " +
                ScriptContract.ScriptEntry.TABLE_NAME + " (" +
                ScriptContract.ScriptEntry._ID  +   " INTEGER PRIMARY KEY, " +
                ScriptContract.ScriptEntry.COLUMN_KEY   + " TEXT NOT NULL, " +
                ScriptContract.ScriptEntry.COLUMN_TITLE   + " TEXT NOT NULL, " +
                ScriptContract.ScriptEntry.COLUMN_SCRIPT   + " TEXT NOT NULL, " +
                ScriptContract.ScriptEntry.COLUMN_TIMESTAMP   + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        db.execSQL(SQL_CREATE_SCRIPT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+ ScriptContract.ScriptEntry.TABLE_NAME);
        onCreate(db);

    }
}
