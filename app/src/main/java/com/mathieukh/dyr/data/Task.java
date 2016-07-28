package com.mathieukh.dyr.data;

import java.util.Date;
import java.util.UUID;

/**
 * Created by sylom on 31/05/2016.
 */
public class Task {

    // Unique identifier for the task
    private String mId;

    //Content of the task
    private String mDescription;

    //Identifier of the wifi access point associated with the task
    private String mBSSIDAssociated;

    //Last modified date of the task
    private Date mDateModified;

    //Is the task is temporary or permanent for the wifi access point
    private boolean isPermanent;

    public Task(String mBSSID, String mDescription, boolean isPermanent) {
        this.mId = UUID.randomUUID().toString();
        this.mBSSIDAssociated = mBSSID;
        this.mDescription = mDescription;
        mDateModified = new Date();
    }

    public String getId() {
        return mId;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getBSSIDAssociated() {
        return mBSSIDAssociated;
    }

    public Date getmDateModified() {
        return mDateModified;
    }

    public boolean isPermanent() {
        return isPermanent;
    }
}
