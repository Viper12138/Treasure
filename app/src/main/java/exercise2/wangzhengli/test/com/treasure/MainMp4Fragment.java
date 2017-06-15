package exercise2.wangzhengli.test.com.treasure;

import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.io.FileDescriptor;
import java.io.IOException;

import Util.ActivityUtils;

/**
 * Created by Administrator on 2017/6/7.
 * 视频播放：
 * 1. MediaPlayer 视频播放
 * 2. TextureView和SurfaceView都可以实现播放视频的展示，SurfaceView效率比较高
 * 主要用于展示渲染播放的视频，使用的时候TextureView需要SurfaceTexure，主要用于渲染和呈现播放的内容
 */
public class MainMp4Fragment extends Fragment implements TextureView.SurfaceTextureListener {
    private TextureView mTextureView;
    private ActivityUtils mActivityUtils;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Fragment全屏显示播放控件
        mTextureView = new TextureView(getContext());
        mActivityUtils = new ActivityUtils(this);
        return mTextureView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 当我们的视频控件准备好的时候，可以让MediaPlayer播放视频了
        // 什么时候会准备好，设置监听
        mTextureView.setSurfaceTextureListener(this);
    }

    // -------------------监听重写的方法------------------------
    // 准备好了，可以进行视频播放的展示了
    @Override
    public void onSurfaceTextureAvailable(final SurfaceTexture surface, int width, int height) {
        /**
         * 视频展示的控件准备好了
         * 1. 找到播放的视频资源
         * 2. 什么时候可以播放：使用MediaPlayer播放，当MediaPlayer准备好的播放出来
         * 3. 播放的展示展示到什么控件上，设置循环播放等
         */
        try {
            AssetFileDescriptor openFd = getContext().getAssets().openFd("welcome.mp4");
            // 拿到MediaPlayer需要的类型
            FileDescriptor fileDescriptor = openFd.getFileDescriptor();
            mediaPlayer = new MediaPlayer();
            //设置播放资源
            mediaPlayer.setDataSource(fileDescriptor, openFd.getStartOffset(), openFd.getLength());
            // 异步准备
            mediaPlayer.prepareAsync();
            // 设置准备的监听：看一下什么时候准备好了
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Surface mysurface=new Surface(surface);
                    mediaPlayer.setSurface(mysurface);
                    mediaPlayer.setLooping(true);// 循环播放
                    mediaPlayer.start();//开始播放

                }
            });
        } catch (IOException e) {
            mActivityUtils.showToast("媒体文件播放失败了");

        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mediaPlayer!=null){
            // 释放资源
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }
}
