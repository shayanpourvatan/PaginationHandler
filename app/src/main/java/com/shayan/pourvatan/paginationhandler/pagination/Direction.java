package com.shayan.pourvatan.paginationhandler.pagination;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by shayanpourvatan on 4/18/17.
 */

public class Direction {

    public static final int LOAD_FROM_TOP = 1;
    public static final int LOAD_FROM_BOTTOM = 2;

    @IntDef(value = {
            LOAD_FROM_TOP,
            LOAD_FROM_BOTTOM
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PaginationDirection {
    }
}


