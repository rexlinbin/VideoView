package com.lengyue.video_view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

/**
 * @author linbin
 */
public class VideoView extends RelativeLayout {

    public VideoView(Context context) {
        super(context);
        init();
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private View container;
    private FrameLayout flController;
    private LinearLayout llTop;
    private LinearLayout llBottom;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private TextView fullscreen;
    private ImageView ivBack;
    private boolean isFullScreen = false;
    private String path;
    private OnOrientationChangeListener onOrientationChangeListener;

    private void init() {
        container = LayoutInflater.from(getContext()).inflate(R.layout.view_video_player, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(container, layoutParams);

        flController = container.findViewById(R.id.exo_layout_controller);
        llTop = container.findViewById(R.id.exo_layout_top);
        llBottom = container.findViewById(R.id.exo_layout_bottom);

        playerView = container.findViewById(R.id.player_view);
        // Instantiate the player.
        player = new SimpleExoPlayer.Builder(getContext()).build();
        // Attach player to the view.
        playerView.setPlayer(player);

        playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if (visibility == VISIBLE) {
                    show();
                } else {
                    hide();
                }
            }
        });

        fullscreen = container.findViewById(R.id.exo_fullscreen);
        fullscreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFullScreen) {
                    // 全屏转半屏
                    toPortrait();
                } else {
                    // 非全屏切换全屏
                    toLandscape();
                }
            }
        });

        ivBack = container.findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    // 全屏转半屏
                    toPortrait();
                } else {
                    ((Activity) getContext()).finish();
                }
            }
        });
    }

    public FrameLayout getFlController() {
        return flController;
    }

    public void setPath(String path) {
        this.path = path;
        String proxyPath = newProxy().getProxyUrl(path);
        Uri uri = Uri.parse(proxyPath);
        player.setMediaSource(buildMediaSource(uri));
        player.prepare();
        // 开始播放
        player.setPlayWhenReady(false);
    }

    public String getPath() {
        return path;
    }

    public void play() {
        player.setPlayWhenReady(true);
    }

    public boolean toPortrait() {
        if (isFullScreen) {
            isFullScreen = false;
            fullscreen.setBackgroundResource(R.drawable.exo_controls_fullscreen_enter);
            ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (onOrientationChangeListener != null) {
                onOrientationChangeListener.orientationChanged(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            return true;
        }
        return false;
    }

    public boolean toLandscape() {
        if (!isFullScreen) {
            isFullScreen = true;
            fullscreen.setBackgroundResource(R.drawable.exo_controls_fullscreen_exit);
            ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 手动横屏
            if (onOrientationChangeListener != null) {
                onOrientationChangeListener.orientationChanged(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            return true;
        }
        return false;
    }

    private void show() {
        llTop.setAnimation(showTop(350));
        llBottom.setAnimation(showBottom(350));
    }

    private void hide() {
        ((FrameLayout) flController.getParent()).setVisibility(VISIBLE);
        llTop.startAnimation(hideTop(350, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((FrameLayout) flController.getParent()).setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        }));
        llBottom.startAnimation(hideBottom(350, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((FrameLayout) flController.getParent()).setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        }));
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ProgressiveMediaSource.Factory(
                new DefaultHttpDataSourceFactory()).
                createMediaSource(MediaItem.fromUri(uri));
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(getContext().getApplicationContext())
                .build();
    }

    private Animation showTop(int duration){
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                0f, Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(duration);
        return animation;
    }

    private Animation hideTop(int duration, Animation.AnimationListener listener){
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(duration);
        animation.setAnimationListener(listener);
        return animation;
    }

    private Animation showBottom(int duration){
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(duration);
        return animation;
    }

    private Animation hideBottom(int duration, Animation.AnimationListener listener){
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(duration);
        animation.setAnimationListener(listener);
        return animation;
    }

    public void setOnOrientationChangeListener(OnOrientationChangeListener onOrientationChangeListener) {
        this.onOrientationChangeListener = onOrientationChangeListener;
    }

    public interface OnOrientationChangeListener {
        /**
         * 屏幕方向变化
         *
         * @param orientation 屏幕变化后方向
         */
        void orientationChanged(int orientation);
    }

    public void onDestroy() {
        if (player != null) {
            player.release();
        }
    }

    public void onPause() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

}
