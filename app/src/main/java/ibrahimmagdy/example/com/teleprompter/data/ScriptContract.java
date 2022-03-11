package ibrahimmagdy.example.com.teleprompter.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class ScriptContract {

    public static final String AUTHORITY = "ibrahimmagdy.example.com.teleprompter";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);


    public static final String PATH_SCRIPTS = "scripts";

    public static final class ScriptEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCRIPTS).build();

        public static final String TABLE_NAME ="scripts";

        public static final String COLUMN_KEY ="keys";

        public static final String COLUMN_TITLE = "scriptTitle";

        public static final String COLUMN_SCRIPT = "description";

        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
