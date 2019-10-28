package com.chen.r1.acessbility;

import android.content.Context;

import com.chen.r1.service.VirtualKeyService;

/**
 * Created by CHEN on 2016/12/19.
 */

public abstract class BaseAccessbilityJob implements AccessbilityJob {

    private VirtualKeyService service;

    @Override
    public void onCreateJob(VirtualKeyService service) {
        this.service = service;
    }

    public Context getContext() {
        return service.getApplicationContext();
    }

    public VirtualKeyService getService() {
        return service;
    }

}
