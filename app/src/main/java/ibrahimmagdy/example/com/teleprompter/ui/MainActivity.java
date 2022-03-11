package ibrahimmagdy.example.com.teleprompter.ui;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;


import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import ibrahimmagdy.example.com.teleprompter.R;
import ibrahimmagdy.example.com.teleprompter.models.ScriptModel;
import ibrahimmagdy.example.com.teleprompter.services.SyncJobService;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences.Editor  mEditor;
    private static final String SKIP_KEY = "skip signIn key";
    private static final String FIRST_SIGN_IN="first sign in";
    private boolean isFirstTimeToSignIn;
    private static final int RC_SIGN_IN = 1;
    private static final String FAILED_SIGN_IN = "googleSignInFail";
    private static final String GOOGLE_ACC = "googleAcc";
    private static final String SUCCESS_LOGIN = "success";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FrameLayout mLoginLayout;
    private FragmentScripts mFragmentScripts;



    private static final String TAG = "TestingJob";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(getApplication());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        Context mContext = getApplicationContext();
        SharedPreferences mSharedPref = mContext.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mEditor = mSharedPref.edit();
        boolean isUserSkippedSignIn = mSharedPref.getBoolean(SKIP_KEY, false);
        isFirstTimeToSignIn = mSharedPref.getBoolean(FIRST_SIGN_IN,true);
        mLoginLayout = findViewById(R.id.login_layout);
        Button mSkipLogInButton = findViewById(R.id.skip_login_button);
        SignInButton signInButton = findViewById(R.id.sign_in_button);

        if (isUserSkippedSignIn){
            mLoginLayout.setVisibility(View.GONE);
            mFragmentScripts = new FragmentScripts();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_container,mFragmentScripts)
                    .commit();

        }
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                    // ...
                }
            }
        });

        mSkipLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEditor.putBoolean(SKIP_KEY,true);
                mEditor.apply();
                mLoginLayout.setVisibility(View.GONE);
                mFragmentScripts = new FragmentScripts();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_container,mFragmentScripts)
                        .commit(); }

        });


    }

    private void startSyncService(){
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(SyncJobService.class) // the JobService that will be called
                .setTag(TAG)
                .setRecurring(false)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(0, 60))
                .setReplaceCurrent(false)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(
                        Constraint.ON_UNMETERED_NETWORK)
                .build();

        dispatcher.mustSchedule(myJob);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mCurrentUser = mAuth.getCurrentUser();
        updateUI(mCurrentUser);
    }





    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();



        inflater.inflate(R.menu.settings, menu);

        if (mCurrentUser!=null){
            menu.findItem(R.id.log_in).setVisible(false);
            menu.findItem(R.id.log_out).setVisible(true);
        }else{
            menu.findItem(R.id.log_in).setVisible(true);
            menu.findItem(R.id.log_out).setVisible(false);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_in:
                signIn();
                return true;
            case R.id.log_out:
                signOut();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }



    private void updateUI(FirebaseUser user){
        if (user != null) {
            startSyncService();
            mLoginLayout.setVisibility(View.GONE);
            mFragmentScripts = new FragmentScripts();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_container,  mFragmentScripts)
                    .commit();

        }

    }

    private void settingUpCloud(){
        if (isFirstTimeToSignIn) {
            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();
            FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference mScriptDatabaseReference = mFirebaseDatabase.getReference().child(mCurrentUser.getUid());
            ScriptModel scriptModel = new ScriptModel(getString(R.string.demo_script_title), getString(R.string.demo_script_content));
            mScriptDatabaseReference.push().setValue(scriptModel);
            mEditor.putBoolean(FIRST_SIGN_IN,false);
            mEditor.apply();
        }
    }
    private void signIn() {
        mEditor.clear();
        mEditor.putBoolean(SKIP_KEY,false);
        mEditor.apply();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(FAILED_SIGN_IN, "Google sign in failed", e);
                // ...
            }
        }
    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(GOOGLE_ACC, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(SUCCESS_LOGIN, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            settingUpCloud();
                            updateUI(user);

                            Snackbar snackbar =
                                    Snackbar.make(mLoginLayout, getString(R.string.success_logIn),Snackbar.LENGTH_LONG)
                                            .setAction("Action", null);
                            View view = snackbar.getView();
                            view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.snackbar_color));
                            snackbar.show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(FAILED_SIGN_IN, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.login_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        ImageButton i = findViewById(R.id.login_image);
        i.setImageResource(R.drawable.profile);
        mLoginLayout.setVisibility(View.GONE);

    }



}

