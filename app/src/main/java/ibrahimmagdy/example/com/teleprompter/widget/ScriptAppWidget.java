package ibrahimmagdy.example.com.teleprompter.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;

import ibrahimmagdy.example.com.teleprompter.R;
import ibrahimmagdy.example.com.teleprompter.ui.MainActivity;
import ibrahimmagdy.example.com.teleprompter.ui.ReviewActivity;

import static ibrahimmagdy.example.com.teleprompter.widget.ScriptService.INGREDIENTS_LIST;


/**
 * Implementation of App Widget functionality.
 */
public class ScriptAppWidget extends AppWidgetProvider {

    static ArrayList<String> ingredientsList = new ArrayList<>();
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews views = new  RemoteViews(context.getPackageName(), R.layout.widget_list_view);

        Intent appIntent = new Intent(context, MainActivity.class);
        appIntent.addCategory(Intent.ACTION_MAIN);
        appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
       appIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT|Intent.FLAG_ACTIVITY_SINGLE_TOP);
       PendingIntent appPendingIntent = PendingIntent.getBroadcast(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setTextViewText(R.id.app_text, String.valueOf(ingredientsList));
        views.setImageViewResource(R.id.logoScript_widget,R.drawable.scriptlogo);
        Intent intent = new Intent(context, ListRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        views.setRemoteAdapter( R.id.widget_list_view, intent);
        //views.setOnClickPendingIntent(R.id.app_text, appPendingIntent);
        views.setPendingIntentTemplate(R.id.logoScript_widget, appPendingIntent);
        views.setOnClickPendingIntent(R.id.widget_button,pendingIntent);
        Intent appReviewIntent = new Intent(context, ReviewActivity.class);
        PendingIntent reviewPendingIntent = PendingIntent.getActivity(context, 0, appReviewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //views.setPendingIntentTemplate(R.id.logoScript_widget,reviewPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, ScriptAppWidget.class));
        final String action = intent.getAction();

        if (action != null && intent.getExtras() != null) {
            ArrayList<String> extras = intent.getExtras().getStringArrayList(INGREDIENTS_LIST);
            Log.v("ACTION: ", action);
            Log.v("EXTRAS", String.valueOf(extras));
            if(extras != null) {
                Log.v("ACTION:", "Inside Conditional");
                ingredientsList = intent.getExtras().getStringArrayList(INGREDIENTS_LIST);
                Log.v("Broadcast Recieved: ", String.valueOf(ingredientsList));
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
                //update all widgets
                ScriptAppWidget.updateRecipesWidgets(context, appWidgetManager, appWidgetIds);
            }
        }
        super.onReceive(context, intent);
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        ScriptAppWidget.updateRecipesWidgets(context,appWidgetManager,appWidgetIds);
    }
    public static void updateRecipesWidgets(Context context, AppWidgetManager appWidgetManager,
                                            int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

