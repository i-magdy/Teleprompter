package ibrahimmagdy.example.com.teleprompter.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import ibrahimmagdy.example.com.teleprompter.R;
import ibrahimmagdy.example.com.teleprompter.data.ScriptContract;
import ibrahimmagdy.example.com.teleprompter.ocr.OcrCaptureActivity;


public class AddScriptActivity extends AppCompatActivity {

    private String title;
    private String script;
    public static final String TextBlockObject = "String";
    public static final String TITLE_CONETNT = " title editBox";
    public static final String SCRIPT_CONTENT = "script edit box";
    private EditText mTitleEditText;
    private EditText mScriptEditText;
    FloatingActionButton fab ;
    private static final String NOT_CONNECTED = "storeKeyInLocal";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_script);

        script = getIntent().getStringExtra(TextBlockObject);
        mTitleEditText = findViewById(R.id.title_edit_text);
        mScriptEditText = findViewById(R.id.script_edit_tex);
        fab = findViewById(R.id.add_capture_text);
        final ImageButton mSaveButton = findViewById(R.id.save_button);
        mSaveButton.setEnabled(false);

        if (script != null){
            mScriptEditText.setText(script);
        }

        if (savedInstanceState!= null){
            title = savedInstanceState.getString(TITLE_CONETNT);
            script = savedInstanceState.getString(SCRIPT_CONTENT);

            mTitleEditText.setText(title);
            mScriptEditText.setText(script);

        }

        mTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0){
                    mSaveButton.setEnabled(true);
                    mTitleEditText.setNextFocusForwardId(R.id.script_edit_tex);

                }else {
                    mSaveButton.setEnabled(false);
                }
                title = mTitleEditText.getText().toString();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = mTitleEditText.getText().toString();
                script = mScriptEditText.getText().toString();
                addNewScript(title,script,NOT_CONNECTED);

                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addedScript = mScriptEditText.getText().toString();
                Intent i = new Intent(getApplicationContext(),OcrCaptureActivity.class);
                i.putExtra(Intent.EXTRA_TEXT,addedScript);
                startActivity(i);
                finish();
            }
        });

    }



    private void addNewScript(String title , String script,String key){

        ContentValues cv = new ContentValues();
        cv.put(ScriptContract.ScriptEntry.COLUMN_KEY,key);
        cv.put(ScriptContract.ScriptEntry.COLUMN_TITLE,title);
        cv.put(ScriptContract.ScriptEntry.COLUMN_SCRIPT, script);
        getApplicationContext().getContentResolver().insert(ScriptContract.ScriptEntry.CONTENT_URI, cv);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TITLE_CONETNT,mTitleEditText.getText().toString());
        outState.putString(SCRIPT_CONTENT,mScriptEditText.getText().toString());
    }

}
