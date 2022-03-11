package ibrahimmagdy.example.com.teleprompter.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import ibrahimmagdy.example.com.teleprompter.R;
import ibrahimmagdy.example.com.teleprompter.data.ScriptContract;

public class ScriptsAdapter extends RecyclerView.Adapter<ScriptsAdapter.DescriptionAdapterViewHolder> {



    private Cursor mCursor;
    final private ListItemClickListener mOnClickListener;


    public ScriptsAdapter(ListItemClickListener listener) {
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public DescriptionAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.script_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new DescriptionAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DescriptionAdapterViewHolder holder, int position) {




        int idIndex = mCursor.getColumnIndex(ScriptContract.ScriptEntry._ID);

        int titleIndex = mCursor.getColumnIndex(ScriptContract.ScriptEntry.COLUMN_TITLE);

        mCursor.moveToPosition(position);

        final int id = mCursor.getInt(idIndex);

        String title = mCursor.getString(titleIndex);

        holder.itemView.setTag(id);

        holder.mDescriptionTextView.setText(title);




    }

    @Override
    public int getItemCount() {
        if (null == mCursor){return 0;}
        return mCursor.getCount();
    }

    public class DescriptionAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mDescriptionTextView;
        public DescriptionAdapterViewHolder(View itemView) {
            super(itemView);
            mDescriptionTextView = itemView.findViewById(R.id.description_text_list);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClicked(clickedPosition);
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    public Cursor swapCursor(Cursor c) {

        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;

        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }
   /* public void setDescriptionsList(List description){
        descriptions = description;
        notifyDataSetChanged();

    }*/
    public interface ListItemClickListener{
        void onListItemClicked(int clickedItemIndex);

    }

}
