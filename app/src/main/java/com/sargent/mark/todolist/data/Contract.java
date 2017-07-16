package com.sargent.mark.todolist.data;

import android.provider.BaseColumns;

/**
 * Created by mark on 7/4/17.
 */

public class Contract {

    /*
    * ADDED 2 NEW columns.
    *
    * category : Every todoitem will have this field to indicate its category.
    *
    * is_completed : A value of 0 or 1 to indicate if the todoitem is completed or not.
    *
    * */

    public static class TABLE_TODO implements BaseColumns{
        public static final String TABLE_NAME = "todoitems";

        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_DUE_DATE = "duedate";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_ISCOMPLETED = "is_completed";
    }
}
