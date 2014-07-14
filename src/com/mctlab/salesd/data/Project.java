package com.mctlab.salesd.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;

import com.mctlab.ansight.common.data.BaseData;
import com.mctlab.salesd.provider.TasksDatabaseHelper.ProjectsColumns;
import com.mctlab.salesd.provider.TasksProvider;
import com.mctlab.salesd.util.LogUtil;

/**
 * Created by liqiang on 6/4/14.
 */
public class Project extends BaseData {

    private int id;
    private long serverId;
    private String name;
    private int priority;
    private int estimatedAmount;
    private int status;
    private String description;
    private int ownerId;

    private long version;
    private int isDelete;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getEstimatedAmount() {
        return estimatedAmount;
    }

    public void setEstimatedAmount(int estimatedAmount) {
        this.estimatedAmount = estimatedAmount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        if (!TextUtils.isEmpty(name)) {
            values.put(ProjectsColumns.NAME, name);
        }

        values.put(ProjectsColumns.ESTIMATED_AMOUNT, estimatedAmount);
        values.put(ProjectsColumns.PRIORITY, priority);
        values.put(ProjectsColumns.STATUS, status);

        if (!TextUtils.isEmpty(description)) {
            values.put(ProjectsColumns.DESCRIPTION, description);
        }

        if (serverId > 0L) {
            values.put(ProjectsColumns.SERVER_ID, serverId);
        }
        return values;
    }

    public boolean saveToLocal(ContentResolver resolver, boolean update) {
        Uri uri = TasksProvider.PROJECTS_CONTENT_URI;
        ContentValues values = getContentValues();
        if (update) {
            String where = ProjectsColumns.SERVER_ID + "=" + serverId;
            LogUtil.d("Delete project: " + getName());
            resolver.update(uri, values, where, null);
        } else {
            LogUtil.d("Insert project: " + getName());
            resolver.insert(uri, values);
        }
        return true;
    }

    public boolean deleteFromLocal(ContentResolver resolver) {
        Uri uri = TasksProvider.PROJECTS_CONTENT_URI;
        String where = ProjectsColumns.SERVER_ID + "=" + serverId;
        LogUtil.d("Delete project: " + getName());
        resolver.delete(uri, where, null);
        return true;
    }
}
