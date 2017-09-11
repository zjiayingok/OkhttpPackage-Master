package com.okhttppackage;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import listener.impl.UIProgressListener;
import util.Logger;

public class MainActivity extends AppCompatActivity {
    ProgressBar upload_progress;

    TextView content_length;
    TextView content_spaced;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //这个是ui线程回调，可直接操作UI
    Handler handler =  new Handler();
    final UIProgressListener uiProgressRequestListener = new UIProgressListener() {
        @Override
        public void onUIProgress(long bytesWrite, long contentLength, boolean done) {
            Logger.e("TAG", "bytesWrite:" + bytesWrite);
            Logger.e("TAG", "contentLength" + contentLength);
            Logger.e("TAG", (100 * bytesWrite) / contentLength + " % done ");
            Logger.e("TAG", "done:" + done);
            Logger.e("TAG", "================================");
            //ui层回调
            Logger.e("上传进度%%%%%%%%%%-----------",((100 * bytesWrite) / contentLength)+"");
            final long percent = (100 * bytesWrite) / contentLength;

            handler.post(new Runnable() {
                @Override
                public void run() {
                    upload_progress.setProgress((int) percent);
                    content_spaced.setText("上传速度:"+percent+"%");
                }
            });

        }

        @Override
        public void onUIStart(long bytesWrite,final long contentLength, boolean done) {
            super.onUIStart(bytesWrite, contentLength, done);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    content_length.setText("文件大小:"+contentLength/1024);
//                    ll_progress.setVisibility(View.VISIBLE);
//                    Toast.showToast(WorkTrackActivity.this,"start");
                }
            });

        }

        @Override
        public void onUIFinish(long bytesWrite, long contentLength, boolean done) {
            super.onUIFinish(bytesWrite, contentLength, done);
            handler.post(new Runnable() {
                @Override
                public void run() {

//                    ll_progress.setVisibility(View.GONE);
//                    Toast.showToast(WorkTrackActivity.this,"end");
                }
            });

        }
    };

}
