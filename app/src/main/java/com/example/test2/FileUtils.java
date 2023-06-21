package com.example.test2;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class FileUtils {
    /**
        獲取包含文件的應用程式路徑
        @return String 跟目錄路徑
    */

    public static String getAppPath(){
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "PDF" +File.separator);
        if(!dir.exists()){
            dir.mkdirs();
        }
        return dir.getPath() + File.separator;
    }

    /**
     打開PDF文件
     @param context
     @param url
     @throw ActivityNotFoundException
     @throw IOException

     context.getApplicationContext().getPackageName() 是獲取應用程式的包名。
     ".fileprovider" 是 FileProvider 在 AndroidManifest.xml 文件中配置的 authorities 屬性的值。該值用於識別 FileProvider 提供的文件內容。
     url 是要獲取 Uri 的文件路徑。
     通過呼叫 FileProvider.getUriForFile() 方法，使用上述參數，可以獲取指定文件的 Uri。這個 Uri 可用於在應用程式中共享文件，例如通過 Intent 進行共享或授予其他應用程式對該文件的訪問權限。
    */
    public static void openFile(Context context, File url)throws ActivityNotFoundException {
        if(url.exists()){
            Uri uri = FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName()+".fileprovider",url);
            String urlString = url.toString().toLowerCase(Locale.ROOT);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for(ResolveInfo resolveInfo : resolveInfoList){
                String packageName = resolveInfo.activityInfo.packageName;
                context.grantUriPermission(packageName,uri,Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            //通過比較url和擴展名，檢查你要打開的文件類型
            //當if條件匹配時，插件設置正確的意圖(mime)類型
            //所以android 知道用甚麼程序打開文件
            if(urlString.toLowerCase().contains(".doc") || urlString.toLowerCase().contains(".docx")){
                intent.setDataAndType(uri,"application/opensearchdescription+xml");
            }else if(urlString.toLowerCase().contains(".pdf")){
                intent.setDataAndType(uri,"application/pdf");
            }else if(urlString.toLowerCase().contains(".ppt") || urlString.toLowerCase().contains(".pptx")){
                intent.setDataAndType(uri,"application/vnd.ms-powerpoint");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Log.i("openFile","文件不存在");
        }
    }
}
