package com.chen.r1.acessbility;

import android.content.Context;

import com.chen.r1.acessbility.BaseAccessibilityService;

/**
 * Created by CHEN on 2016/12/19.
 */

public abstract class BaseAccessbilityJob implements AccessbilityJob {

    private BaseAccessibilityService service;

    @Override
    public void onCreateJob(BaseAccessibilityService service) {
        this.service = service;
    }

    public Context getContext() {
        return service.getApplicationContext();
    }

    public BaseAccessibilityService getService() {
        return service;
    }

}
