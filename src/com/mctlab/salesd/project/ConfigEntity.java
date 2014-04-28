package com.mctlab.salesd.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mctlab.salesd.provider.TasksDatabaseHelper.ConfigColumns;
import com.mctlab.salesd.provider.TasksProvider;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Entity;
import android.content.ContentProviderOperation.Builder;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class ConfigEntity implements Parcelable {

    // Category - values map
    private HashMap<String, ArrayList<ConfigValues>> mEntries =
            new HashMap<String, ArrayList<ConfigValues>>();

    private long mProjectId = -1;

    public ConfigEntity() {

    }

    public ConfigEntity(long projectId) {
        mProjectId = projectId;
    }

    public void setProjectId(long projectId) {
        mProjectId = projectId;
    }

    public void fromCursor(Cursor cursor) {
        if (cursor.moveToFirst()) {
            do {
                final ContentValues before = new ContentValues();

                int index = cursor.getColumnIndex(ConfigColumns._ID);
                if (index != -1) {
                    before.put(ConfigColumns._ID, cursor.getLong(index));
                }

                index = cursor.getColumnIndex(ConfigColumns.PROJECT_ID);
                if (index != -1) {
                    before.put(ConfigColumns.PROJECT_ID, cursor.getLong(index));
                }

                index = cursor.getColumnIndex(ConfigColumns.CATEGORY);
                if (index != -1) {
                    before.put(ConfigColumns.CATEGORY, cursor.getString(index));
                }

                index = cursor.getColumnIndex(ConfigColumns.TYPE);
                if (index != -1) {
                    before.put(ConfigColumns.TYPE, cursor.getString(index));
                }

                index = cursor.getColumnIndex(ConfigColumns.NUMBER);
                if (index != -1) {
                    before.put(ConfigColumns.NUMBER, cursor.getInt(index));
                }

                final ConfigValues entry = ConfigValues.fromBefore(before);
                addEntry(entry);
            } while (cursor.moveToNext());
        }
    }

    public void ensureCategoryExists(String category) {
        if (!(getCategoryEntryCount(category, true) > 0)) {
            insertEntry(category);
        }
    }

    public boolean canInsert(String category) {
        return true;
    }

    public ConfigValues insertEntry(String category) {
        final ContentValues after = new ContentValues();
        after.put(ConfigColumns.CATEGORY, category);

        final ConfigValues entry = ConfigValues.fromAfter(after);
        addEntry(entry);
        return entry;
    }

    public void trimEmpty() {
        boolean hasValues = false;
        for (ArrayList<ConfigValues> entries : mEntries.values()) {
            for (ConfigValues entry : entries) {
                // Skip any values that haven't been touched
                final boolean touched = entry.isInsert() || entry.isUpdate();
                if (!touched) {
                    hasValues = true;
                    continue;
                }

                boolean isEmpty = true;
                String type = entry.getAsString(ConfigColumns.TYPE);
                if (!TextUtils.isEmpty(type) && TextUtils.isGraphic(type)) {
                    isEmpty = false;
                }
                String number = entry.getAsString(ConfigColumns.NUMBER);
                if (!TextUtils.isEmpty(number)) {
                    isEmpty = false;
                }

                if (isEmpty) {
                    entry.markDeleted();
                } else {
                    hasValues = true;
                }
            }
        }
        if (!hasValues) {
            markDeleted();
        }
    }

    public void markDeleted() {
        for (ArrayList<ConfigValues> entries : mEntries.values()) {
            for (ConfigValues entry : entries) {
                entry.markDeleted();
            }
        }
    }

    public ArrayList<ContentProviderOperation> buildDiff() {
        ArrayList<ContentProviderOperation> diff = new ArrayList<ContentProviderOperation>();
        for (ArrayList<ConfigValues> entries : mEntries.values()) {
            for (ConfigValues entry : entries) {
                Builder builder;
                builder = entry.buildDiff(TasksProvider.CONFIG_CONTENT_URI);
                if (entry.isInsert()) {
                    if (mProjectId > 0) {
                        builder.withValue(ConfigColumns.PROJECT_ID, mProjectId);
                    }
                }
                if (builder != null) {
                    diff.add(builder.build());
                }
            }
        }
        return diff;
    }

    public ArrayList<String> getCategories() {
        ArrayList<String> categories = new ArrayList<String>();
        for (String category : mEntries.keySet()) {
            categories.add(category);
        }
        return categories;
    }

    public boolean hasCategoryEntries(String category) {
        return mEntries.containsKey(category);
    }

    private ArrayList<ConfigValues> getCategoryEntries(String category, boolean lazyCreate) {
        ArrayList<ConfigValues> categoryEntries = mEntries.get(category);
        if (categoryEntries == null && lazyCreate) {
            categoryEntries = new ArrayList<ConfigValues>();
            mEntries.put(category, categoryEntries);
        }
        return categoryEntries;
    }

    public ArrayList<ConfigValues> getCategoryEntries(String category) {
        return getCategoryEntries(category, false);
    }

    public int getCategoryEntryCount(String category, boolean onlyVisible) {
        final ArrayList<ConfigValues> categoryEntries = getCategoryEntries(category);
        if (categoryEntries == null) return 0;

        int count = 0;
        for (ConfigValues child : categoryEntries) {
            // Skip deleted items when requesting only visible
            if (onlyVisible && !child.isVisible()) continue;
            count++;
        }
        return count;
    }

    public ConfigValues addEntry(ConfigValues entry) {
        final String category = entry.getCategory();
        getCategoryEntries(category, true).add(entry);
        return entry;
    }

    /**
     * Return the total number of {@link ValuesDelta} contained.
     */
    public int getEntryCount(boolean onlyVisible) {
        int count = 0;
        for (String category : mEntries.keySet()) {
            count += getCategoryEntryCount(category, onlyVisible);
        }
        return count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        final int size = this.getEntryCount(false);
        dest.writeInt(size);
        for (ArrayList<ConfigValues> entries : mEntries.values()) {
            for (ConfigValues entry : entries) {
                dest.writeParcelable(entry, flags);
            }
        }
    }

    public void readFromParcel(Parcel source) {
        final ClassLoader loader = getClass().getClassLoader();
        final int size = source.readInt();
        for (int i = 0; i < size; i++) {
            final ConfigValues child = source.<ConfigValues> readParcelable(loader);
            this.addEntry(child);
        }
    }

    public static final Parcelable.Creator<ConfigEntity> CREATOR =
            new Parcelable.Creator<ConfigEntity>() {
        public ConfigEntity createFromParcel(Parcel in) {
            final ConfigEntity entity = new ConfigEntity();
            entity.readFromParcel(in);
            return entity;
        }

        public ConfigEntity[] newArray(int size) {
            return new ConfigEntity[size];
        }
    };

    public static class ConfigValues implements Parcelable {
        protected ContentValues mBefore;
        protected ContentValues mAfter;

        protected String mIdColumn = ConfigColumns._ID;

        private boolean mFromTemplate;

        /**
         * Next value to assign to {@link #mIdColumn} when building an insert
         * operation through {@link #fromAfter(ContentValues)}. This is used so
         * we can concretely reference this {@link ValuesDelta} before it has
         * been persisted.
         */
        protected static int sNextInsertId = -1;

        protected ConfigValues() {
        }

        /**
         * Create {@link ValuesDelta}, using the given object as the
         * "before" state, usually from an {@link Entity}.
         */
        public static ConfigValues fromBefore(ContentValues before) {
            final ConfigValues entry = new ConfigValues();
            entry.mBefore = before;
            entry.mAfter = new ContentValues();
            return entry;
        }

        /**
         * Create {@link ValuesDelta}, using the given object as the "after"
         * state, usually when we are inserting a row instead of updating.
         */
        public static ConfigValues fromAfter(ContentValues after) {
            final ConfigValues entry = new ConfigValues();
            entry.mBefore = null;
            entry.mAfter = after;

            // Assign temporary id which is dropped before insert.
            entry.mAfter.put(entry.mIdColumn, sNextInsertId--);
            return entry;
        }

        public ContentValues getAfter() {
            return mAfter;
        }

        public boolean containsKey(String key) {
            return ((mAfter != null && mAfter.containsKey(key)) ||
                    (mBefore != null && mBefore.containsKey(key)));
        }

        public String getAsString(String key) {
            if (mAfter != null && mAfter.containsKey(key)) {
                return mAfter.getAsString(key);
            } else if (mBefore != null && mBefore.containsKey(key)) {
                return mBefore.getAsString(key);
            } else {
                return null;
            }
        }

        public byte[] getAsByteArray(String key) {
            if (mAfter != null && mAfter.containsKey(key)) {
                return mAfter.getAsByteArray(key);
            } else if (mBefore != null && mBefore.containsKey(key)) {
                return mBefore.getAsByteArray(key);
            } else {
                return null;
            }
        }

        public Long getAsLong(String key) {
            if (mAfter != null && mAfter.containsKey(key)) {
                return mAfter.getAsLong(key);
            } else if (mBefore != null && mBefore.containsKey(key)) {
                return mBefore.getAsLong(key);
            } else {
                return null;
            }
        }

        public Integer getAsInteger(String key) {
            return getAsInteger(key, null);
        }

        public Integer getAsInteger(String key, Integer defaultValue) {
            if (mAfter != null && mAfter.containsKey(key)) {
                return mAfter.getAsInteger(key);
            } else if (mBefore != null && mBefore.containsKey(key)) {
                return mBefore.getAsInteger(key);
            } else {
                return defaultValue;
            }
        }

        public boolean isChanged(String key) {
            if (mAfter == null || !mAfter.containsKey(key)) {
                return false;
            }

            Object newValue = mAfter.get(key);
            Object oldValue = mBefore.get(key);

            if (oldValue == null) {
                return newValue != null;
            }

            return !oldValue.equals(newValue);
        }

        public String getCategory() {
            return getAsString(ConfigColumns.CATEGORY);
        }

        public Long getId() {
            return getAsLong(mIdColumn);
        }

        public void setIdColumn(String idColumn) {
            mIdColumn = idColumn;
        }

        public void setFromTemplate(boolean isFromTemplate) {
            mFromTemplate = isFromTemplate;
        }

        public boolean isFromTemplate() {
            return mFromTemplate;
        }

        public boolean beforeExists() {
            return (mBefore != null && mBefore.containsKey(mIdColumn));
        }

        /**
         * When "after" is present, then visible
         */
        public boolean isVisible() {
            return (mAfter != null);
        }

        /**
         * When "after" is wiped, action is "delete"
         */
        public boolean isDelete() {
            return beforeExists() && (mAfter == null);
        }

        /**
         * When no "before" or "after", is transient
         */
        public boolean isTransient() {
            return (mBefore == null) && (mAfter == null);
        }

        /**
         * When "after" has some changes, action is "update"
         */
        public boolean isUpdate() {
            if (!beforeExists() || mAfter == null || mAfter.size() == 0) {
                return false;
            }
            for (String key : mAfter.keySet()) {
                Object newValue = mAfter.get(key);
                Object oldValue = mBefore.get(key);
                if (oldValue == null) {
                    if (newValue != null) {
                        return true;
                    }
                } else if (!oldValue.equals(newValue)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * When "after" has no changes, action is no-op
         */
        public boolean isNoop() {
            return beforeExists() && (mAfter != null && mAfter.size() == 0);
        }

        /**
         * When no "before" id, and has "after", action is "insert"
         */
        public boolean isInsert() {
            return !beforeExists() && (mAfter != null);
        }

        public void markDeleted() {
            mAfter = null;
        }

        /**
         * Ensure that our internal structure is ready for storing updates.
         */
        private void ensureUpdate() {
            if (mAfter == null) {
                mAfter = new ContentValues();
            }
        }

        public void put(String key, String value) {
            ensureUpdate();
            mAfter.put(key, value);
        }

        public void put(String key, byte[] value) {
            ensureUpdate();
            mAfter.put(key, value);
        }

        public void put(String key, int value) {
            ensureUpdate();
            mAfter.put(key, value);
        }

        public void put(String key, long value) {
            ensureUpdate();
            mAfter.put(key, value);
        }

        public void putNull(String key) {
            ensureUpdate();
            mAfter.putNull(key);
        }

        /**
         * Return set of all keys defined through this object.
         */
        public Set<String> keySet() {
            final HashSet<String> keys = new HashSet<String>();

            if (mBefore != null) {
                for (Map.Entry<String, Object> entry : mBefore.valueSet()) {
                    keys.add(entry.getKey());
                }
            }

            if (mAfter != null) {
                for (Map.Entry<String, Object> entry : mAfter.valueSet()) {
                    keys.add(entry.getKey());
                }
            }

            return keys;
        }

        /**
         * Return complete set of "before" and "after" values mixed together,
         * giving full state regardless of edits.
         */
        public ContentValues getCompleteValues() {
            final ContentValues values = new ContentValues();
            if (mBefore != null) {
                values.putAll(mBefore);
            }
            if (mAfter != null) {
                values.putAll(mAfter);
            }

            return values;
        }

        /**
         * Merge the "after" values from the given {@link ValuesDelta},
         * discarding any existing "after" state. This is typically used when
         * re-parenting changes onto an updated {@link Entity}.
         */
        public static ConfigValues mergeAfter(ConfigValues local, ConfigValues remote) {
            // Bail early if trying to merge delete with missing local
            if (local == null && (remote.isDelete() || remote.isTransient())) return null;

            // Create local version if none exists yet
            if (local == null) local = new ConfigValues();

            if (!local.beforeExists()) {
                // Any "before" record is missing, so take all values as "insert"
                local.mAfter = remote.getCompleteValues();
            } else {
                // Existing "update" with only "after" values
                local.mAfter = remote.mAfter;
            }

            return local;
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof ConfigValues) {
                // Only exactly equal with both are identical subsets
                final ConfigValues other = (ConfigValues)object;
                return this.subsetEquals(other) && other.subsetEquals(this);
            }
            return false;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            toString(builder);
            return builder.toString();
        }

        /**
         * Helper for building string representation, leveraging the given
         * {@link StringBuilder} to minimize allocations.
         */
        public void toString(StringBuilder builder) {
            builder.append("{ ");
            for (String key : this.keySet()) {
                builder.append(key);
                builder.append("=");
                builder.append(this.getAsString(key));
                builder.append(", ");
            }
            builder.append("}");
        }

        /**
         * Check if the given {@link ValuesDelta} is both a subset of this
         * object, and any defined keys have equal values.
         */
        public boolean subsetEquals(ConfigValues other) {
            for (String key : this.keySet()) {
                final String ourValue = this.getAsString(key);
                final String theirValue = other.getAsString(key);
                if (ourValue == null) {
                    // If they have value when we're null, no match
                    if (theirValue != null) return false;
                } else {
                    // If both values defined and aren't equal, no match
                    if (!ourValue.equals(theirValue)) return false;
                }
            }
            // All values compared and matched
            return true;
        }

        /**
         * Build a {@link ContentProviderOperation} that will transform our
         * "before" state into our "after" state, using insert, update, or
         * delete as needed.
         */
        public ContentProviderOperation.Builder buildDiff(Uri targetUri) {
            Builder builder = null;
            if (isInsert()) {
                // Changed values are "insert" back-referenced to Contact
                mAfter.remove(mIdColumn);
                builder = ContentProviderOperation.newInsert(targetUri);
                builder.withValues(mAfter);
            } else if (isDelete()) {
                // When marked for deletion and "before" exists, then "delete"
                builder = ContentProviderOperation.newDelete(targetUri);
                builder.withSelection(mIdColumn + "=" + getId(), null);
            } else if (isUpdate()) {
                // When has changes and "before" exists, then "update"
                builder = ContentProviderOperation.newUpdate(targetUri);
                builder.withSelection(mIdColumn + "=" + getId(), null);
                builder.withValues(mAfter);
            }
            return builder;
        }

        /** {@inheritDoc} */
        public int describeContents() {
            // Nothing special about this parcel
            return 0;
        }

        /** {@inheritDoc} */
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(mBefore, flags);
            dest.writeParcelable(mAfter, flags);
            dest.writeString(mIdColumn);
        }

        public void readFromParcel(Parcel source) {
            final ClassLoader loader = getClass().getClassLoader();
            mBefore = source.<ContentValues> readParcelable(loader);
            mAfter = source.<ContentValues> readParcelable(loader);
            mIdColumn = source.readString();
        }

        public static final Parcelable.Creator<ConfigValues> CREATOR =
                new Parcelable.Creator<ConfigValues>() {
            public ConfigValues createFromParcel(Parcel in) {
                final ConfigValues values = new ConfigValues();
                values.readFromParcel(in);
                return values;
            }

            public ConfigValues[] newArray(int size) {
                return new ConfigValues[size];
            }
        };
    }
}
