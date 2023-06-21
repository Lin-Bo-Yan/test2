package com.example.test2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

public class Permissions {
    public static void showPermissionsSettingDiolog(Context context, String permission){
        String msg = "";
        if("android.permission.READ_EXTERNAL_STORAGE".equals(permission) || "android.permission.WRITE_EXTERNAL_STORAGE".equals(permission)){
            msg = "本App需要權限";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showSetting(context);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    //如果授權失敗，就要進入App權限設置
    public static void showSetting(Context context){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",context.getPackageName(),null);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
