package com.lengyue.videoview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.lengyue.video_view.VideoView;

public class MainActivity extends AppCompatActivity {
    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = findViewById(R.id.video_view);
        videoView.setPath("https://gymoo-project-cdn.oss-cn-shenzhen.aliyuncs.com/mtv_video/VID_20201022_183537.mp4");
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.onDestroy();
    }
}
