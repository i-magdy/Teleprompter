package ibrahimmagdy.example.com.teleprompter.ui;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ibrahimmagdy.example.com.teleprompter.R;

public class ReviewActivity extends AppCompatActivity {

    private int index ;
   public static final String EXTRA_INDEX = "scriptid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        index = getIntent().getIntExtra(EXTRA_INDEX, 0);
        if (savedInstanceState != null) {
            index = savedInstanceState.getInt(EXTRA_INDEX);
        }
            FragmentReview fragmentReview = new FragmentReview();
            fragmentReview.setIndex(index);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.review_container, fragmentReview)
                    .commit();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_INDEX,index);
    }
}
