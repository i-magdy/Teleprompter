package ibrahimmagdy.example.com.teleprompter.services;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;


import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ibrahimmagdy.example.com.teleprompter.R;
import ibrahimmagdy.example.com.teleprompter.data.ScriptContract;
import ibrahimmagdy.example.com.teleprompter.models.ScriptModel;



public class SyncJobService extends JobService {

    private static final String TAG = "TestingJob";


    @Override
    public boolean onStartJob(final JobParameters job) {


        syncDatabase();
        deletingDatabase();
        addData();

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {

        Log.d(TAG, "onStopJob Testing Job: Stopped");

        return true;
    }
    private void setData(final List<String> keysList){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                checkingData(keysList);
            }
        }, 3000);
    }
    public void checkingData(List<String> keys){
         final String NOT_CONNECTED = "storeKeyInLocal";
         FirebaseAuth auth = FirebaseAuth.getInstance();
         FirebaseUser user = auth.getCurrentUser();
         FirebaseDatabase database = FirebaseDatabase.getInstance();
         DatabaseReference mDatabaseRef = database.getReference().child(user.getUid());
         Cursor mC = getContentResolver().query(ScriptContract.ScriptEntry.CONTENT_URI,
                null,
                null,
                null,
                ScriptContract.ScriptEntry.COLUMN_TIMESTAMP);
         if (mC != null && mC.getCount() != 0) {
            int keyIndex = mC.getColumnIndex(ScriptContract.ScriptEntry.COLUMN_KEY);
            int titleIndex = mC.getColumnIndex(ScriptContract.ScriptEntry.COLUMN_TITLE);
            int scriptIndex = mC.getColumnIndex(ScriptContract.ScriptEntry.COLUMN_SCRIPT);
            int x = 0;
            int i = 0;
            boolean notThere = false;
            while (i < mC.getCount()) {
                mC.moveToPosition(i);
                String key = mC.getString(keyIndex);
                String title = mC.getString(titleIndex);
                String script = mC.getString(scriptIndex);
                while (x < keys.size()) {
                    String keyCloud = keys.get(x);
                    Log.i("isItKeyWorking", keyCloud);
                    Log.i("dataBaseLocal", key);
                    if (key.equals(keyCloud)) {
                        notThere = false;
                        break;
                    } else if (key.equals(NOT_CONNECTED)) {
                        x++;
                        notThere = true;

                    } else {
                        x++;
                        notThere = true;
                    }
                }
                if (notThere) {
                    Log.i("isItWorking", title + script);
                    ScriptModel scriptModel = new ScriptModel(title, script);
                    mDatabaseRef.push().setValue(scriptModel);

                }
                i++;
                x = 0;
            }
        }
    }
    public void writeAgainFromCloud(){
        FirebaseAuth mAuthor = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuthor.getCurrentUser();
        final List<String> keyList = new ArrayList<>();
        if (mUser != null) {
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference mDatabaseReference = mDatabase.getReference().child(mUser.getUid());

            ChildEventListener mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    try {

                        String key = dataSnapshot.getKey();
                       ScriptModel scriptModel = dataSnapshot.getValue(ScriptModel.class);
                        addToDatabase(scriptModel.getTitle(),scriptModel.getScript(),key);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }

            };
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }
    private void syncDatabase(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        final List<String> keyList = new ArrayList<>();
        if (mCurrentUser != null) {
            FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference mScriptDatabaseReference = mFirebaseDatabase.getReference().child(mCurrentUser.getUid());

            ChildEventListener mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    try {

                        keyList.add(dataSnapshot.getKey());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }

            };
            mScriptDatabaseReference.addChildEventListener(mChildEventListener);

            setData(keyList);
        }


    }


    /*
     Uri SINGLE_PLANT_URI = ContentUris.withAppendedId(
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build(), plantId);
        ContentValues contentValues = new ContentValues();
        long timeNow = System.currentTimeMillis();
        contentValues.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);
        // Update only if that plant is still alive
        getContentResolver().update(
                SINGLE_PLANT_URI,
                contentValues,
                PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME + ">?",
                new String[]{String.valueOf(timeNow - PlantUtils.MAX_AGE_WITHOUT_WATER)});
        // Always update widgets after watering plants
     */
    private void addData(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                writeAgainFromCloud();


            }
        },6000 );
    }

    private void deleteDatabase(){


        Cursor mCursor = getContentResolver().query(ScriptContract.ScriptEntry.CONTENT_URI,
                null,
                null,
                null,
                ScriptContract.ScriptEntry.COLUMN_TIMESTAMP);

        if (mCursor!= null && mCursor.getCount() > 0){
            Uri uri = ScriptContract.ScriptEntry.CONTENT_URI;
            getContentResolver().delete(uri,null,null);

        }
    }
    private void deletingDatabase(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                    deleteDatabase();


            }
        },5500 );
    }

    private void addToDatabase(String title,String script,String key){
        ContentValues cv = new ContentValues();

        cv.put(ScriptContract.ScriptEntry.COLUMN_KEY,key);
        cv.put(ScriptContract.ScriptEntry.COLUMN_TITLE,title);
        cv.put(ScriptContract.ScriptEntry.COLUMN_SCRIPT, script);

        getContentResolver().insert(ScriptContract.ScriptEntry.CONTENT_URI, cv);


    }






}
