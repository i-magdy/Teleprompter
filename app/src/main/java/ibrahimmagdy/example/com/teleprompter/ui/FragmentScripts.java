package ibrahimmagdy.example.com.teleprompter.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.AsyncTaskLoader;

import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ibrahimmagdy.example.com.teleprompter.R;
import ibrahimmagdy.example.com.teleprompter.data.ScriptContract;

import ibrahimmagdy.example.com.teleprompter.ocr.OcrCaptureActivity;
import ibrahimmagdy.example.com.teleprompter.widget.ScriptService;

public class FragmentScripts extends Fragment implements ScriptsAdapter.ListItemClickListener , LoaderManager.LoaderCallbacks<Cursor>{


    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SCRIPT_LOADER_ID = 0;
    private ScriptsAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageButton mImageButtonSettings;
    private  FloatingActionButton fab;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private NotificationCompat.Builder mBuilder;
    private boolean optionLayoutClicked = false;
    private FrameLayout mOptionLayout;
    private static final int READ_REQUEST_CODE = 1337;
    public static final String TextBlockObject = "String";


    private Uri personPhoto;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext  = getActivity();
        adapter = new ScriptsAdapter(this);
        getLoaderManager().restartLoader(SCRIPT_LOADER_ID,null,this);
        createNotificationChannel();

         mBuilder = new NotificationCompat.Builder(mContext, "IMI")
                .setSmallIcon(R.drawable.scriptlogo)
                .setContentTitle("hello")
                .setContentText("it's me")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        googleAccInfo();
        Cursor c = mContext.getContentResolver().query(ScriptContract.ScriptEntry.CONTENT_URI,
                null,
                null,
                null,
                ScriptContract.ScriptEntry.COLUMN_TIMESTAMP);

        if (c != null && c.getCount() !=0) {
            int titleIndex = c.getColumnIndex(ScriptContract.ScriptEntry.COLUMN_TITLE);
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < c.getCount(); i++) {
                c.moveToPosition(i);
                titleList.add(c.getString(titleIndex));
            }
            ScriptService.startActionWaterPlants(getActivity(), (ArrayList<String>) titleList);
        }









    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container,false);
        mRecyclerView = rootView.findViewById(R.id.descriptions_recyclerview);
        fab = rootView.findViewById(R.id.add_description_button);
        FloatingActionButton addCamera = rootView.findViewById(R.id.add_camera);
        FloatingActionButton addFile = rootView.findViewById(R.id.add_file);
        mImageButtonSettings = rootView.findViewById(R.id.login_image);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        mOptionLayout = rootView.findViewById(R.id.option_layout);
        Toolbar toolbar = rootView.findViewById(R.id.toolbar_main);
        toolbar.setTitle(getString(R.string.app_name));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLoaderManager().restartLoader(SCRIPT_LOADER_ID, null, FragmentScripts.this);
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });

        addFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/*");

                startActivityForResult(intent,READ_REQUEST_CODE);
            }
        });
        addCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), OcrCaptureActivity.class);
                Intent i = new Intent(getContext(),AddScriptActivity.class);
                startActivity(intent);
                ableToClick();

            }

        });

        //adapter.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

// notificationId is a unique int for each notification that you must define
                notificationManager.notify(0, mBuilder.build());


                unableToClick();

                }
        });

        mOptionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ableToClick();
            }
        });

        if (personPhoto != null) {
            Picasso.get()
                    .load(personPhoto)
                    .fit()
                    .into(mImageButtonSettings);
        }




        mImageButtonSettings.setOnCreateContextMenuListener(this);

        mRecyclerView.setAdapter(adapter);


        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                sendFile(uri);
            }
        }
    }

    private void sendFile(Uri uri)  {
        if (uri != null){

            try {
                InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        inputStream));
                StringBuilder  stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
                Intent i = new Intent(mContext,AddScriptActivity.class);
                i.putExtra(TextBlockObject,stringBuilder.toString());
                Log.i("file", "TheFile: " + stringBuilder.toString());
                startActivity(i);
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }


    private void ableToClick(){
        mOptionLayout.setVisibility(View.GONE);
        optionLayoutClicked = false;
        mImageButtonSettings.setClickable(true);
        fab.show();
    }

    private void unableToClick(){
        mOptionLayout.setVisibility(View.VISIBLE);
        optionLayoutClicked = true;
        fab.hide();
        mImageButtonSettings.setClickable(false);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void googleAccInfo(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(mContext);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            personPhoto = acct.getPhotoUrl();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "imi";
            String description ="ibra";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(String.valueOf(0), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onListItemClicked(int clickedItemIndex) {


        if (!optionLayoutClicked) {

            Intent i = new Intent(mContext, ReviewActivity.class);
            i.putExtra(ReviewActivity.EXTRA_INDEX, clickedItemIndex);
            startActivity(i);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.swipe_refresh_layout){
            mSwipeRefreshLayout.setRefreshing(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @NonNull
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Cursor>(mContext) {

            Cursor mCursor = null;


            @Override
            protected void onStartLoading() {
                if (mCursor != null) {

                    deliverResult(mCursor);
                } else {

                    forceLoad();
                }
            }


            public Cursor loadInBackground() {

                try {
                    return mContext.getContentResolver().query(ScriptContract.ScriptEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            ScriptContract.ScriptEntry.COLUMN_TIMESTAMP);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mCursor = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        getLoaderManager().restartLoader(SCRIPT_LOADER_ID,null,this);
        super.onResume();
    }
}
