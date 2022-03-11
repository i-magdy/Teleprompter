package ibrahimmagdy.example.com.teleprompter.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;


import java.util.ArrayList;

import ibrahimmagdy.example.com.teleprompter.R;
import ibrahimmagdy.example.com.teleprompter.data.ScriptContract;

import static ibrahimmagdy.example.com.teleprompter.data.ScriptContract.BASE_CONTENT_URI;
import static ibrahimmagdy.example.com.teleprompter.data.ScriptContract.PATH_SCRIPTS;

public class ScriptService extends IntentService {


    public static final String ACTION_UPDATE_SCRIPTS_WIDGET = "ibrahimmagdy.example.com.com.example.android.com.example.android.teleprompter.action.update.scripts.widgets";

    public ScriptService(){
        super("ScriptService");
    }



    public static String INGREDIENTS_LIST = "FROM_ACTIVITY_INGREDIENTS_LIST";




    public static void startActionWaterPlants(Context context,ArrayList<String> fromActivityIngredientsList) {
        Intent intent = new Intent(context, ScriptService.class);
        intent.setAction(ACTION_UPDATE_SCRIPTS_WIDGET);
        //intent.putExtra(INGREDIENTS_LIST, fromActivityIngredientsList);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {


      if(intent.getExtras() != null) {
            ArrayList<String> fromActivityIngredientList = intent.getExtras().getStringArrayList(INGREDIENTS_LIST);
            Log.v("Handle Intent: ", String.valueOf(fromActivityIngredientList));
            handleActionUpdateBakingWigdets(fromActivityIngredientList);
        }
    }


    private void handleActionUpdateBakingWigdets(ArrayList<String> fromActivityIngredientsList) {
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        intent.putExtra(INGREDIENTS_LIST, fromActivityIngredientsList);
        if(fromActivityIngredientsList != null) {
            sendBroadcast(intent);
        }
    }
}
