package com.chebuso.chargetimer.models;

import com.chebuso.chargetimer.helpers.StringHelper;

public class CalendarEntity {
    public long id;
    public String displayName;
    public String accountName;
    public String accountType;
    public String ownerAccount;
    public boolean isPrimary;
    public boolean visible;

    public boolean isPrimaryAlternative(){
        String laccountName = StringHelper.emptyIfNull(accountName);
        String lownerName = StringHelper.emptyIfNull(ownerAccount);
        return laccountName.equals(lownerName);
    }
}

