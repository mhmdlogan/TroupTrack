package com.trouptrack.mhmdlogan.trouptrack;

import android.os.Bundle;

/**
 * Created by MhmdLoGaN on 10/04/2018.
 */

public class UnreadMessageHolder {

    private static UnreadMessageHolder instance;
    private Bundle bundle;

    public static synchronized UnreadMessageHolder getInstance()
    {
        UnreadMessageHolder qbUnreadMessageHolder;
        synchronized (UnreadMessageHolder.class)
        {
            if(instance ==null)
                instance = new UnreadMessageHolder();
            qbUnreadMessageHolder =instance;
        }
        return qbUnreadMessageHolder;
    }
    private UnreadMessageHolder(){
        bundle = new Bundle();
    }
    public void setBundle(Bundle bundle)
    {
        this.bundle = bundle;
    }
    public Bundle getBundle()
    {
        return this.bundle;
    }
    public int getUnreadedMessageByDialogid(String id)
    {
        return this.bundle.getInt(id);
    }
    // a7eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeh hewa eeeeeh elly ana b3mlo daaah -_- -___-
}
