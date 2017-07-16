package com.sargent.mark.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.sargent.mark.todolist.data.Contract;
import com.sargent.mark.todolist.data.ToDoItem;

import java.util.ArrayList;

/**
 * Created by mark on 7/4/17.
 */

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ItemHolder> {

    private Cursor cursor;
    private ItemClickListener listener;

    //A reference to the SQLiteDatabase object so we can perform db operations
    //for when a todoitem is checked for completion.
    private SQLiteDatabase db;

    private String TAG = "todolistadapter";

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item, parent, false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.bind(holder, position);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    //Now passing in a category string to the onItemClick function.
    public interface ItemClickListener {
        void onItemClick(int pos, String description, String duedate, long id, String category);
    }

    public ToDoListAdapter(Cursor cursor, ItemClickListener listener, SQLiteDatabase db) {
        this.cursor = cursor;
        this.listener = listener;

        //Store the reference of the db to this class from its constructor.
        this.db = db;
    }

    public void swapCursor(Cursor newCursor){
        if (cursor != null) cursor.close();
        cursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView descr;
        TextView due;
        String duedate;
        String description;
        long id;

        /*
        *  A CheckBox object to get a reference of the checkbox in the layout
        *  A category string to store the value of the category of the todoitem
        * */
        CheckBox checkBox;
        String category;

        View view;

        ItemHolder(View view) {
            super(view);
            descr = (TextView) view.findViewById(R.id.description);
            due = (TextView) view.findViewById(R.id.dueDate);

            //Instantiate the checkobox
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);

            this.view = view;

            view.setOnClickListener(this);
        }

        public void bind(ItemHolder holder, int pos) {
            cursor.moveToPosition(pos);
            id = cursor.getLong(cursor.getColumnIndex(Contract.TABLE_TODO._ID));
            Log.d(TAG, "deleting id: " + id);

            duedate = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE));
            description = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION));
            descr.setText(description);
            due.setText(duedate);
            holder.itemView.setTag(id);

            /*
                When we get information from the database to store it into a ItemHolder, we need to
                store a reference of the category too
            */
            category = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY));


            /*
            * We check for each todoitem when the information is retrieved from the db
            * If the field is a 1 then we set the checkbox to CHECKED
            * If the field is a 0 then we set the checkbox to UNCHECKED
            * */
            if(cursor.getInt(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_ISCOMPLETED)) == 1){
                checkBox.setChecked(true);
            }
            else{
                checkBox.setChecked(false);
            }


            /*
            * Set a click listener for the checkbox
            * When it is checked, update the database is_completed field for that todoitem
            * If it isn't checked, check it and set the is_completed field to 1
            * If it is checked, uncheck it and set the is_completed field to 0
            * */
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkBox.isChecked()) {
                        updateToDoCompleted(true);
                    }
                    else{
                        updateToDoCompleted(false);
                    }
                }
            });
        }

        //Pass the category string to the onItemClick function.
        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            listener.onItemClick(pos, description, duedate, id, category);
        }

        public void updateToDoCompleted(boolean completed){
            ContentValues cv = new ContentValues();

            /*
            * We check if the is_completed column is 1 or 0
            * and pass it to update the database
            * */

            if(completed) {
                cv.put(Contract.TABLE_TODO.COLUMN_NAME_ISCOMPLETED, 1);
            }
            else{
                cv.put(Contract.TABLE_TODO.COLUMN_NAME_ISCOMPLETED, 0);
            }

            db.update(Contract.TABLE_TODO.TABLE_NAME, cv, Contract.TABLE_TODO._ID + "=" + id, null);
        }
    }

}
