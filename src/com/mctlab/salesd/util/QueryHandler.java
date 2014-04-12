package com.mctlab.salesd.util;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class QueryHandler extends AsyncQueryHandler {

    public static interface OnInsertCompleteListener {
        public void onInsertComplete(int token, Uri uri);
    }

    public static interface OnUpdateCompleteListener {
        public void onUpdateComplete(int token, int result);
    }

    public static interface OnDeleteCompleteListener {
        public void onDeleteComplete(int token, int result);
    }

    public static interface OnQueryCompleteListener {
        public void onQueryComplete(int token, Cursor cursor);
    }

    protected OnInsertCompleteListener mOnInsertCompleteListener;
    protected OnUpdateCompleteListener mOnUpdateCompleteListener;
    protected OnDeleteCompleteListener mOnDeleteCompleteListener;
    protected OnQueryCompleteListener mOnQueryCompleteListener;

    private final ContentResolver mContentResolver;

    public QueryHandler(ContentResolver cr) {
        super(cr);
        mContentResolver = cr;
    }

    public ContentResolver getContentResolver() {
        return mContentResolver;
    }

    public void setOnQueryCompleteListener(OnQueryCompleteListener listener) {
        mOnQueryCompleteListener = listener;
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        if (mOnInsertCompleteListener != null) {
            mOnInsertCompleteListener.onInsertComplete(token, uri);
            return;
        }
        super.onInsertComplete(token, cookie, uri);
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        if (mOnUpdateCompleteListener != null) {
            mOnUpdateCompleteListener.onUpdateComplete(token, result);
            return;
        }
        super.onUpdateComplete(token, cookie, result);
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        if (mOnDeleteCompleteListener != null) {
            mOnDeleteCompleteListener.onDeleteComplete(token, result);
            return;
        }
        super.onDeleteComplete(token, cookie, result);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if (mOnQueryCompleteListener != null) {
            mOnQueryCompleteListener.onQueryComplete(token, cursor);
            return;
        }
        super.onQueryComplete(token, cookie, cursor);
    }
}
