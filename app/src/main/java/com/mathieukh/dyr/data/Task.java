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
    private String mSSIDAssociated;

    //Last modified date of the task
    private Date mDateModified;

    //Is the task is temporary or permanent for the wifi access point
    private boolean isPermanent;

    //Is the task need to be display when entering or exiting from the network
    private boolean enteringTask;

    public Task(String mSSID, String mDescription, boolean isPermanent, boolean entering) {
        this.mId = UUID.randomUUID().toString();
        this.mSSIDAssociated = mSSID;
        this.mDescription = mDescription;
        this.isPermanent = isPermanent;
        this.enteringTask = entering;
        mDateModified = new Date();
    }

    public String getId() {
        return mId;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getSSIDAssociated() {
        return mSSIDAssociated;
    }

    public Date getmDateModified() {
        return mDateModified;
    }

    public boolean isPermanent() {
        return isPermanent;
    }

    public boolean isEnteringTask() {
        return enteringTask;
    }

    public void setPermanent(boolean permanent) {
        this.isPermanent = permanent;
    }
}
