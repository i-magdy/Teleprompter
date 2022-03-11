package ibrahimmagdy.example.com.teleprompter.ui;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



import ibrahimmagdy.example.com.teleprompter.R;
import ibrahimmagdy.example.com.teleprompter.data.ScriptContract;


import static ibrahimmagdy.example.com.teleprompter.ui.ReviewActivity.EXTRA_INDEX;


public class FragmentReview extends Fragment {
    private Context mContext;
    private int mIndex;
    private String script;
    private Timer timer;
    private ScrollView mScrollView;
    private int scrollPosition;


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mScriptDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
   private String title;
   private String key;
    private boolean cloudIsConnected = false;
    private int idx;
    private FloatingActionButton mReviewButton;
    private FloatingActionButton mStopReview;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        timer = new Timer(10000000, 13);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        if (mCurrentUser != null && isNetworkAvailable()) { cloudIsConnected = true; }
        if(savedInstanceState!= null){
            mIndex = savedInstanceState.getInt(EXTRA_INDEX);
        }

        Cursor c =mContext.getContentResolver().query(ScriptContract.ScriptEntry.CONTENT_URI,
                null,
                null,
                null,
                ScriptContract.ScriptEntry.COLUMN_TIMESTAMP);

        int keyIndex = c.getColumnIndex(ScriptContract.ScriptEntry.COLUMN_KEY);
        int titleIndex = c.getColumnIndex(ScriptContract.ScriptEntry.COLUMN_TITLE);
        int scriptIndex = c.getColumnIndex(ScriptContract.ScriptEntry.COLUMN_SCRIPT);
        int idIndex = c.getColumnIndex(ScriptContract.ScriptEntry._ID);

        c.moveToPosition(mIndex);
        idx  = c.getInt(idIndex);
        key = c.getString(keyIndex);
        title = c.getString(titleIndex);
        script = c.getString(scriptIndex);

        if (cloudIsConnected){
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mScriptDatabaseReference = mFirebaseDatabase.getReference().child(mCurrentUser.getUid()).child(key);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_review, container,false);
        mScrollView = rootView.findViewById(R.id.scrollview_review);
        mReviewButton = rootView.findViewById(R.id.button_start_review);
        mStopReview = rootView.findViewById(R.id.button_pause_review);
        FloatingActionButton fab = rootView.findViewById(R.id.fab_camera);
        TextView mTextView = rootView.findViewById(R.id.textView_review);
        ImageButton mDelete = rootView.findViewById(R.id.delete_button);
        Toolbar appBar = rootView.findViewById(R.id.toolbar_review);
        appBar.setTitle(title);
        mTextView.setText(script);
        mReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mReviewButton.hide();
                    mStopReview.show();
                    timer.start();


            }
        });
        mStopReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReviewButton.show();
                mStopReview.hide();
                timer.cancel();

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                Intent i = new Intent(mContext,RecordActivity.class);
                i.putExtra(Intent.EXTRA_TEXT,script);
                startActivity(i);
            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage(getString(R.string.alert_message));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.dialog_alert_button),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String stringId = Integer.toString(idx);
                                Uri uri = ScriptContract.ScriptEntry.CONTENT_URI;
                                uri = uri.buildUpon().appendPath(stringId).build();
                                mContext.getContentResolver().delete(uri, null, null);
                                mScriptDatabaseReference.removeValue();
                                dialog.dismiss();
                                getActivity().finish();
                            }
                        });
                alertDialog.show();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        scrollPosition = mScrollView.getScrollY();
        mReviewButton.show();
        mStopReview.hide();
    }

    public class Timer extends CountDownTimer {

        public Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub
            mScrollView.smoothScrollBy(0, 1);
        }

        @Override
        public void onFinish() {

        }

    }


    private boolean isNetworkAvailable(){

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo active =connectivityManager.getActiveNetworkInfo();
        return active != null && active.isConnected();
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();


    }

    public void setIndex(int i ){
        this.mIndex = i ;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_INDEX,mIndex);

    }
}
