package com.myConsole.instaDownloader.services;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.myConsole.instaDownloader.interfaces.GetData;
import com.myConsole.instaDownloader.util.Constant;
import com.myConsole.instaDownloader.util.FindData;


import java.util.ArrayList;

public class GetAppService extends Service {

    private FindData findData;
    private ClipboardManager mClipboardManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("##SERGetAppService", "Trigered");
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        assert mClipboardManager != null;
        mClipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        Log.d("##SERGetAppService", "Trigered1");
        findData = new FindData(getApplicationContext(), new GetData() {
            @Override
            public void getData(ArrayList<String> linkList, String message, boolean isData) {
                Log.d("##SERGetAppService", "Trigered12");
                if (isData) {
                    if (linkList.size() != 0) {
                        Constant.downloadArray.clear();
                        Constant.downloadArray.addAll(linkList);
                        Intent intent = new Intent(getApplicationContext(), DownloadService.class);
                        intent.setAction("com.download.action.START");
                        startService(intent);
                    } else {
                        Toast.makeText(GetAppService.this, "no_data_found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(GetAppService.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    Log.d("##SERGetAppService", "onPrimaryClipChanged");
                    try {
                        ClipData clip = mClipboardManager.getPrimaryClip();
                        assert clip != null;
                        String string = clip.getItemAt(0).getText().toString();
                        findData.data(string);
                    } catch (Exception e) {
                        Log.d("##SERGetAppService", "e");
                        Toast.makeText(GetAppService.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            };

    @Override
    public void onDestroy() {
        stopSelf();
        stopForeground(false);
        mClipboardManager.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        super.onDestroy();
    }
}