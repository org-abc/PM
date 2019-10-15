package com.pocketmechanic.kondie.pm_mechanic;

import android.Manifest;
import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by kondie on 2018/08/11.
 */

public class DirHelper implements PermissionUtils.PermissionResultCallback {

    Activity activity;
    ArrayList<String> permissions;
    PermissionUtils permissionUtils;

    public DirHelper(Activity activity){
        this.activity = activity;
        permissions = new ArrayList<>();
        permissionUtils = new PermissionUtils(activity, this);

        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    void check_permission(){
        permissionUtils.check_permission(permissions,"Need GPS permission for getting your location",1);
    }

    @Override
    public void PermissionGranted(int request_code) {

    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {

    }

    @Override
    public void PermissionDenied(int request_code) {

    }

    @Override
    public void NeverAskAgain(int request_code) {

    }
}
