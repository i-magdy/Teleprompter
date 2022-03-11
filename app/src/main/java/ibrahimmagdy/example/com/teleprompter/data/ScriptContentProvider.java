package ibrahimmagdy.example.com.teleprompter.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

import static ibrahimmagdy.example.com.teleprompter.data.ScriptContract.ScriptEntry.TABLE_NAME;

public class ScriptContentProvider extends ContentProvider {


    private ScriptDbHelper mDbHelper;

    public static final int SCRIPTS = 100;
    public static final int SCRIPT_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(ScriptContract.AUTHORITY,ScriptContract.PATH_SCRIPTS,SCRIPTS);
        uriMatcher.addURI(ScriptContract.AUTHORITY,ScriptContract.PATH_SCRIPTS + "/#",SCRIPT_WITH_ID);

        return uriMatcher;
    }



    @Override
    public boolean onCreate() {

        Context context = getContext();
        mDbHelper = new ScriptDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase mDb = mDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case  SCRIPTS:
                retCursor = mDb.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case SCRIPT_WITH_ID:

                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                retCursor = mDb.query(TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);

           default:

                    throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase mDb = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case SCRIPTS :
                long id = mDb.insert(TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(ScriptContract.ScriptEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int ScriptDeleted;

        switch (match) {

            case SCRIPT_WITH_ID:

                String id = uri.getPathSegments().get(1);

                ScriptDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                break;

            case SCRIPTS :
                ScriptDeleted = db.delete(TABLE_NAME,null,null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        if (ScriptDeleted != 0) {

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ScriptDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int scriptUpdated;

        switch (match){
            case SCRIPTS:
                scriptUpdated = db.update(TABLE_NAME,values,selection,selectionArgs);
                break;

            case SCRIPT_WITH_ID:
                if (selection == null) selection = ScriptContract.ScriptEntry._ID + "=?";
                else selection += " AND " + ScriptContract.ScriptEntry._ID + "=?";
                // Get the place ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Append any existing selection options to the ID filter
                if (selectionArgs == null) selectionArgs = new String[]{id};
                else {
                    ArrayList<String> selectionArgsList = new ArrayList<>();
                    selectionArgsList.addAll(Arrays.asList(selectionArgs));
                    selectionArgsList.add(id);
                    selectionArgs = selectionArgsList.toArray(new String[selectionArgsList.size()]);
                }
                scriptUpdated = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        if (scriptUpdated != 0) {
            // A place (or more) was updated, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return scriptUpdated;
    }
}
