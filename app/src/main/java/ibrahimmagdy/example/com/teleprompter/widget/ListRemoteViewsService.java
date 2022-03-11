package ibrahimmagdy.example.com.teleprompter.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import ibrahimmagdy.example.com.teleprompter.R;
import ibrahimmagdy.example.com.teleprompter.ui.ReviewActivity;

import static ibrahimmagdy.example.com.teleprompter.widget.ScriptAppWidget.ingredientsList;


public class ListRemoteViewsService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

   Context mContext;
  List<String> mRecipes;

   public ListRemoteViewsFactory(Context applicationContext) {
       mContext = applicationContext;

   }
   @Override
   public void onCreate() {

   }

   @Override
   public void onDataSetChanged() {



     mRecipes = new ArrayList<>();
     mRecipes = ingredientsList;


   }

   @Override
   public void onDestroy() {


   }

   @Override
   public int getCount() {

      if(mRecipes == null) return 0;
      return mRecipes.size();


   }

   @Override
   public RemoteViews getViewAt(int position) {
       RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.script_app_widget);

       views.setTextViewText(R.id.app_text,mRecipes.get(position));
       views.setImageViewResource(R.id.logoScript_widget,R.drawable.scriptlogo);
       Bundle extras = new Bundle();
       extras.putLong(ReviewActivity.EXTRA_INDEX, position);
       Intent fillInIntent = new Intent();
       fillInIntent.putExtras(extras);
       //PendingIntent appPendingIntent = PendingIntent.getActivity(mContext, 0, fillInIntent, PendingIntent.FLAG_UPDATE_CURRENT);
       views.setOnClickFillInIntent(R.id.logoScript_widget, fillInIntent);
       //views.setOnClickPendingIntent(R.id.app_text,appPendingIntent);




       return views;
   }

   @Override
   public RemoteViews getLoadingView() {
       return null;
   }

   @Override
   public int getViewTypeCount() {
       return 1;
   }

   @Override
   public long getItemId(int position) {
       return position;
   }

   @Override
   public boolean hasStableIds() {
       return true;
   }


}
