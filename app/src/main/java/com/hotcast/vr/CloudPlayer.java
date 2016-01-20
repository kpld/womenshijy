package com.hotcast.vr;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.*;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hotcast.vr.tools.L;

import java.util.Timer;
import java.util.TimerTask;

public class CloudPlayer implements OnPreparedListener, OnBufferingUpdateListener, OnSeekCompleteListener, OnInfoListener, OnCompletionListener, OnErrorListener, OnVideoSizeChangedListener {

    final private String TAG = CloudPlayer.class.getSimpleName();

    private Context mContext;

    public MediaPlayer mPlayer_;
    private IPlayerEventListener mListener_;
    private Handler mHandler_;
    // 播放的surfaceView
    private SurfaceView sfaceView_;
    private SurfaceHolder sfaceHolder_;

    // 视频的总时长
    private int mVideoTotalDuration_;
    // 视频的播放当前时长
    private int mVideoCurPosition_;
    // 视频的尺寸
    private int mVideoWidth_;
    private int mVideoHeight_;

    /**
     * 反馈播放时间的timer
     */
    private Timer playDurationTimer_; // 每一秒检测一次播放器的播放时间，来控制是否loading、seek完成等

    // 播放器反馈给listener的Info类别的信息type
    public static int PLAYER_INOF_TOTAL_DURATION = 0; // 视频的总时长
    public static int PLAYER_INOF_VIDEO_SIZE = 1; // 视频的尺寸

    // 播放器的各个状态
    public static int PLAYER_STATUS_IDEL = 0; // 空虚状态
    public static int PLAYER_STATUS_PLAYING = 1; // 正在播放过程中
    public static int PLAYER_STATUS_PAUSE = 3; // 暂停了

    // 在播放状态下的子状态，正在播放、loading、seek
    public static int PLAYER_PLAYING_IN = 0; // 在播放的状态下，是否在loading
    public static int PLAYER_PLAYING_LOADING = 1; // 在播放的状态下，是否在loading
    public static int PLAYER_PLAYING_SEEK = 2; // 在播放的状态下，是否在loading
    private int mPlayerPlayingSubState_ = PLAYER_PLAYING_IN;

    /**
     * 播放器当前状态
     */
    private int playStatus_ = PLAYER_STATUS_IDEL;

    // 播放器单例
    private static CloudPlayer cloudPlayer_;

    public static CloudPlayer shareCloudPlayer(Context mContext) {
        if (cloudPlayer_ == null) {
            cloudPlayer_ = new CloudPlayer(mContext);
        }

        return cloudPlayer_;
    }

    protected CloudPlayer(Context mContext) {

        this.mContext = mContext;
        mHandler_ = new Handler() {
            public void handleMessage(Message msg) {
                CloudPlayer.this.handleFeedback(msg.what, msg.arg1, msg.arg2);
            }
        };
    }

    // ///////////////////// 内部接口
    private void reset() {

        // 设置各种参数复位
        this.setVideoCurPosition(0);
        this.setVideoTotalDuration(0);
        this.setVideoWidth(0);
        this.setVideoHeight(0);

        // this.mLoadingStartMonitor = 0;
        // this.mLoadingCompleteMonitor = 0;
        this.playStatus_ = PLAYER_STATUS_IDEL;
        this.mPlayerPlayingSubState_ = PLAYER_PLAYING_IN;
        this.mPlayCompleteSucceed_ = true;
        this.mStartSeekDuration_ = 0;
    }

    /**
     * 内部设置
     *
     * @param videoTotalDuration_
     */
    private void setVideoTotalDuration(int videoTotalDuration_) {
        this.mVideoTotalDuration_ = videoTotalDuration_;
    }

    /**
     * 内部设置
     *
     * @param videoCurDuration_
     */
    private void setVideoCurPosition(int videoCurDuration_) {
        this.mVideoCurPosition_ = videoCurDuration_;
    }

    final private int MSG_STARTING_VALUE = 10000;
    final private int MSG_PLAYING_START = MSG_STARTING_VALUE + 1;
    final private int MSG_BUFFERING_START = MSG_STARTING_VALUE + 2;
    final private int MSG_BUFFERING_FINISH = MSG_STARTING_VALUE + 4;
    final private int MSG_SEEK_COMPLETE = MSG_STARTING_VALUE + 5;
    final private int MSG_UPDATE_VIDEO_CUR_DURATION = MSG_STARTING_VALUE + 6;
    final private int MSG_FEEDBACK_MEDIA_TOTAL_DURATION = MSG_STARTING_VALUE + 7;
    final private int MSG_PLAY_COMPLETE = MSG_STARTING_VALUE + 8;
    final private int MSG_FEEDBACK_MEDIA_VIDEO_SIZE = MSG_STARTING_VALUE + 9;
    final private int MSG_BUFFERING_TIME_OUT = MSG_STARTING_VALUE + 10;

    // 监控 是否该loading的 统计值
    private int mLoadingStartMonitor = 0;
    // 监控 seek是否结束的 统计值
//	private int mLoadingCompleteMonitor = 0;

    /**
     * 开始播放器的播放进度检测
     */
    public void startPlayDurationTimer() {

        if (playDurationTimer_ == null) {

            playDurationTimer_ = new Timer();
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    int curDuration = (int) mPlayer_.getCurrentPosition();
                    synchronized (this) {

                        if (playStatus_ == PLAYER_STATUS_IDEL) {
                            if (curDuration > 0) {
                                // 通知开始播放
                                mHandler_.sendEmptyMessage(MSG_PLAYING_START);
                            }
                        } else {
                            // 反馈总时长
                            if (mVideoTotalDuration_ == 0 && mPlayer_ != null) {

                                int totalDuration = (int) mPlayer_.getDuration();
                                if (totalDuration > 0) {
                                    mHandler_.obtainMessage(MSG_FEEDBACK_MEDIA_TOTAL_DURATION, totalDuration, 0).sendToTarget();
                                }
                            }

                            boolean durationChanged = false;
                            if (mVideoCurPosition_ / 1000 != curDuration / 1000) {
                                durationChanged = true;
                            }

                            if (playStatus_ == PLAYER_STATUS_PLAYING) {
                                if (mPlayerPlayingSubState_ == PLAYER_PLAYING_IN) {

                                    if (durationChanged) {
                                        mLoadingStartMonitor = 0;
                                    } else {
                                        mLoadingStartMonitor++;
                                    }

                                    if ((!durationChanged && mLoadingStartMonitor > 2)) { // loading开始了

                                        mLoadingStartMonitor = 0;
                                        mPlayerPlayingSubState_ = PLAYER_PLAYING_LOADING;
                                        mHandler_.sendEmptyMessage(MSG_BUFFERING_START);
                                    }
                                } else if (mPlayerPlayingSubState_ == PLAYER_PLAYING_LOADING || mPlayerPlayingSubState_ == PLAYER_PLAYING_SEEK) {

                                    if (durationChanged) { // loading开始了 && mLoadingCompleteMonitor > 2) { // loading开始了

                                        mPlayerPlayingSubState_ = PLAYER_PLAYING_IN;
                                        mHandler_.sendEmptyMessage(MSG_BUFFERING_FINISH);
                                    }
                                }
                            }

                            // 更新当前时间
                            mVideoCurPosition_ = curDuration;
                            // 只有是播放时间，才会去更新
                            if (playStatus_ == PLAYER_STATUS_PLAYING && mPlayerPlayingSubState_ == PLAYER_PLAYING_IN) {

                                mHandler_.obtainMessage(MSG_UPDATE_VIDEO_CUR_DURATION, mVideoCurPosition_, 0).sendToTarget();
                            }
                        }
                    }
                }
            };

            playDurationTimer_.scheduleAtFixedRate(task, 1 * 1000, 1 * 1000);
        }
    }

    /**
     * 停止播放器的播放时间进度检测
     */
    public void stopPlayDurationTimer() {

        if (playDurationTimer_ != null) {

            playDurationTimer_.cancel();
            playDurationTimer_ = null;
        }
    }

    /**
     * 处理来自于 mediaplayer的回调 | 播放时间检测的反馈，从而向播放界面通知进度 和 最新的播放时间
     *
     * @param msgWhat 消息的类型
     * @param msgArg1 消息的参数1
     * @param msgArg2 消息的参数2
     */
    private void handleFeedback(int msgWhat, int msgArg1, int msgArg2) {

        if (mListener_ == null) {
            return;
        }
        // 反馈拿到了播放总时长
        if (msgWhat == MSG_PLAYING_START) { // 开始播放
            mListener_.onStart(this);
            playStatus_ = PLAYER_STATUS_PLAYING;
        } else if (msgWhat == MSG_BUFFERING_START) { // 开始buffering
            mListener_.onBufferingStart(this);
        } else if (msgWhat == MSG_BUFFERING_FINISH) { // buffering 结束
            mListener_.onBufferingFinish(this);
            playStatus_ = PLAYER_STATUS_PLAYING;
        } else if (msgWhat == MSG_SEEK_COMPLETE) { // seek结束了
            mListener_.onStart(this);
            playStatus_ = PLAYER_STATUS_PLAYING;
        } else if (msgWhat == MSG_UPDATE_VIDEO_CUR_DURATION) { // 更新当前的播放时间
            mListener_.onPositionUpdate(this, msgArg1);
        } else if (msgWhat == MSG_FEEDBACK_MEDIA_TOTAL_DURATION) { // 拿到了视频的总时长
            this.setVideoTotalDuration(msgArg1);
            mListener_.onInfoUpdate(this, PLAYER_INOF_TOTAL_DURATION, Integer.valueOf(this.getVideoTotalDuration()));
        } else if (msgWhat == MSG_FEEDBACK_MEDIA_VIDEO_SIZE) { // 拿到了视频的尺寸
            mListener_.onInfoUpdate(this, PLAYER_INOF_VIDEO_SIZE, CBSize.make(msgArg1, msgArg2));
        } else if (msgWhat == MSG_PLAY_COMPLETE) { // 播放完毕了
            this.stop();
            if (mPlayCompleteSucceed_) {
                mListener_.onFinish(this);
            } else {
                mListener_.onError(this, IPlayerEventListener.ERROR_TYPE_PLAYERERROR);
            }
            mPlayCompleteSucceed_ = true;
            playStatus_ = PLAYER_STATUS_IDEL;
        } else if (msgWhat == MSG_BUFFERING_TIME_OUT && playStatus_ == MSG_BUFFERING_START) { // 播放超时
            L.e("<<<<<<<<<<<<<<<<<播放缓冲10秒超时     执行error");
            this.stop();
            mListener_.onError(this, IPlayerEventListener.ERROR_TYPE_PLAYERERROR);
        }
    }

    /**
     * 保持视频的宽度
     *
     * @param mVideoWidth
     */
    private void setVideoWidth(int mVideoWidth) {
        this.mVideoWidth_ = mVideoWidth;
    }

    /**
     * 保持视频的高度
     *
     * @param mVideoHeight
     */
    private void setVideoHeight(int mVideoHeight) {
        this.mVideoHeight_ = mVideoHeight;
    }

    /**
     * 设置播放器的状态
     *
     * @param playStatus
     */
    private void setPlayStatus(int playStatus) {
        this.playStatus_ = playStatus;
    }

    // ///////////////////// 对外接口

    /**
     * 设置监听者
     *
     * @param listener
     */
    public void setListener(IPlayerEventListener listener) {
        this.mListener_ = listener;
    }

    /**
     * 设置surfaceView
     *
     * @param sfaceView
     */
    public void setSurfaceView(SurfaceView sfaceView) {

        this.sfaceView_ = sfaceView;
        this.sfaceHolder_ = sfaceView.getHolder();
    }

    /**
     * 设置视频的显示组件
     *
     * @param sfaceHolder
     */
    public void setDisplay(SurfaceHolder sfaceHolder) {

        this.sfaceHolder_ = sfaceHolder;
        if (mPlayer_ != null) {
            mPlayer_.setDisplay(sfaceHolder);
        }
    }


    private String _srcUrl = null;

    /**
     * 播放视频
     *
     * @param url               视频的url
     * @param startSeekDuration 开始播放的时间
     */
    public void play(String url, int startSeekDuration) {

        // 初始化一些东西
        prepared = false;
        reset();
        setStartSeekDuration(startSeekDuration);

        // 播放
        play(url);
    }

    private void play(String url) {
        _srcUrl = url;
        // 开始计算10秒超时
        L.e("libin", ">>>>>>>>>>>>>>开始播放失败10秒倒计时");
        mHandler_.removeMessages(MSG_BUFFERING_TIME_OUT);
        mHandler_.sendEmptyMessageDelayed(MSG_BUFFERING_TIME_OUT, 10000);
        try {

            L.e("libin", "当前播放的地址：" + url + "----------------------------");


            if(mPlayer_ == null){
                mPlayer_ = new MediaPlayer();
            }

            mPlayer_.reset();

            mPlayer_.setDataSource(_srcUrl);

            mPlayer_.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer_.setOnPreparedListener(this);
            mPlayer_.setOnBufferingUpdateListener(this);
            mPlayer_.setOnSeekCompleteListener(this);
            mPlayer_.setOnVideoSizeChangedListener(this);
            mPlayer_.setOnErrorListener(this);
            mPlayer_.setOnCompletionListener(this);
            mPlayer_.setOnInfoListener(this);

            if (sfaceHolder_ != null) {
                mPlayer_.setDisplay(sfaceHolder_);
                sfaceHolder_.setKeepScreenOn(true);
                sfaceHolder_.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }


            mPlayer_.prepareAsync();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // /**
    // * 播放视频
    // *
    // * @param url 视频的url
    // * @param startSeekDuration 开始播放的时间
    // * @param heads 播放器的头信息
    // */
    // public void play(String url, final int startSeekDuration,
    // ArrayList<MediaHeadInfo> heads) {
    //
    //
    // url =
    // "http://v.youku.com/player/getM3U8/vid/170434589/type/hd2/ts//v.m3u8";
    //
    //
    // FinalHttp fh = new FinalHttp();
    // // final String dir = mContext.getDir("tempm3u8",
    // 0).getAbsolutePath()+"/m3u8.m3u8"; //+"/temp_m3u8.m3u8";
    // //
    // //
    // // try {
    // // Runtime.getRuntime().exec("chmod 777 " + mContext.getDir("tempm3u8",
    // 0));
    // // Runtime.getRuntime().exec("chmod 777 " + dir);
    // // } catch (IOException e) {
    // // e.printStackTrace();
    // // }
    // //
    // //
    // // fh.get(url, new AjaxCallBack<Object>() {
    // // @Override
    // // public void onSuccess(Object o) {
    // // super.onSuccess(o);
    // // String result = (String)o;
    // // Log.e("libin", "result="+result);
    // //
    // // try {
    // // FileWriter fw = new FileWriter(dir);
    // // fw.write(result);
    // // fw.flush();
    // // fw.close();
    // // } catch (IOException e) {
    // // e.printStackTrace();
    // // }
    // //
    // //
    // //
    // //
    // //
    // // File f= new File(dir);
    // //
    // //
    // // try {
    // //
    // // Log.e("libin", " download file size === "+f.length());
    // //
    // //
    // // InputStream is = new FileInputStream(f);
    // // InputStreamReader isr = new InputStreamReader(is);
    // // BufferedReader br = new BufferedReader(isr);
    // // String data = null;
    // // while(null != (data = br.readLine())){
    // // Log.e("libin", "   data====   "+data);
    // // }
    // // } catch (FileNotFoundException e) {
    // // e.printStackTrace();
    // // } catch (IOException e) {
    // // e.printStackTrace();
    // // }
    // //
    // //
    // // CloudPlayer.this.reset();
    // // CloudPlayer.this.setStartSeekDuration(startSeekDuration);
    // // try {
    // //
    // // if (mPlayer_ == null) {
    // // mPlayer_ = new MediaPlayer();
    // // mPlayer_.setScreenOnWhilePlaying(true);
    // // } else {
    // // mPlayer_.stop();
    // // mPlayer_.reset();
    // // }
    // //
    // // Thread.sleep(50);
    // //
    // // mPlayer_.setDataSource(f.getAbsolutePath());
    // // Log.e("libin", "<<<<<<<<<<不带有头信息>>>>>>>");
    // //
    // //
    // // Log.e("libin", "当前播放的地址：" + f.getAbsolutePath() +
    // "----------------------------");
    // //
    // // if (sfaceHolder_ != null) {
    // // mPlayer_.setDisplay(sfaceHolder_);
    // // }
    // // mPlayer_.prepareAsync();
    // //
    // // mPlayer_.setOnPreparedListener(CloudPlayer.this);
    // // mPlayer_.setOnBufferingUpdateListener(CloudPlayer.this);
    // // mPlayer_.setOnSeekCompleteListener(CloudPlayer.this);
    // // mPlayer_.setOnVideoSizeChangedListener(CloudPlayer.this);
    // // mPlayer_.setOnErrorListener(CloudPlayer.this);
    // // mPlayer_.setOnCompletionListener(CloudPlayer.this);
    // // mPlayer_.setOnInfoListener(CloudPlayer.this);
    // //
    // // // 开始计算10秒超时
    // // Log.e("libin", ">>>>>>>>>>>>>>开始播放失败10秒倒计时");
    // // mHandler_.removeMessages(MSG_BUFFERING_TIME_OUT);
    // // mHandler_.sendEmptyMessageDelayed(MSG_BUFFERING_TIME_OUT, 10000);
    // //
    // // } catch (Exception e) {
    // // e.printStackTrace();
    // // }
    // //
    // // }
    // // });
    //
    //
    //
    //
    // // final String dir = mContext.getCacheDir().getAbsolutePath()+"/m3u8";
    //
    // fh.download(url, getM3u8SavePath(), new AjaxCallBack<File>() {
    // @Override
    // public void onSuccess(File file) {
    // super.onSuccess(file);
    //
    //
    // // file = new File(dir);
    //
    // String newurl = file.getAbsolutePath();
    //
    // try {
    //
    // Log.e("libin", " download file size === "+file.length());
    //
    //
    // InputStream is = new FileInputStream(file);
    // InputStreamReader isr = new InputStreamReader(is);
    // BufferedReader br = new BufferedReader(isr);
    // String data = null;
    // while(null != (data = br.readLine())){
    // Log.e("libin", "   data====   "+data);
    // }
    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    //
    // CloudPlayer.this.reset();
    // CloudPlayer.this.setStartSeekDuration(startSeekDuration);
    // try {
    //
    // if (mPlayer_ == null) {
    // mPlayer_ = new MediaPlayer();
    // mPlayer_.setScreenOnWhilePlaying(true);
    // } else {
    // mPlayer_.stop();
    // mPlayer_.reset();
    // }
    //
    // Thread.sleep(50);
    //
    // mPlayer_.setDataSource(newurl);
    // Log.e("libin", "<<<<<<<<<<不带有头信息>>>>>>>");
    //
    //
    // Log.e("libin", "当前播放的地址：" + newurl + "----------------------------");
    //
    // if (sfaceHolder_ != null) {
    // mPlayer_.setDisplay(sfaceHolder_);
    // }
    // mPlayer_.prepareAsync();
    //
    // mPlayer_.setOnPreparedListener(CloudPlayer.this);
    // mPlayer_.setOnBufferingUpdateListener(CloudPlayer.this);
    // mPlayer_.setOnSeekCompleteListener(CloudPlayer.this);
    // mPlayer_.setOnVideoSizeChangedListener(CloudPlayer.this);
    // mPlayer_.setOnErrorListener(CloudPlayer.this);
    // mPlayer_.setOnCompletionListener(CloudPlayer.this);
    // mPlayer_.setOnInfoListener(CloudPlayer.this);
    //
    // // 开始计算10秒超时
    // Log.e("libin", ">>>>>>>>>>>>>>开始播放失败10秒倒计时");
    // mHandler_.removeMessages(MSG_BUFFERING_TIME_OUT);
    // mHandler_.sendEmptyMessageDelayed(MSG_BUFFERING_TIME_OUT, 10000);
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // });
    //
    //
    // }

    /**
     * 暂停视频
     */
    public void pause() {
        if (mPlayer_ != null && mPlayer_.isPlaying()) {
            this.playStatus_ = PLAYER_STATUS_PAUSE;
            mPlayer_.pause();
            L.d(TAG, "播放器暂停");
        }
    }


    /**
     * 继续播放
     */
    public void resume() {
        if (mPlayer_ != null) {
            this.playStatus_ = PLAYER_STATUS_PLAYING;
            this.mPlayerPlayingSubState_ = PLAYER_PLAYING_IN;
            if (prepared) {
                mPlayer_.start();
            }
        }
    }


    /**
     * 停止播放
     */
    public void stop() {

        this.stopPlayDurationTimer();

        if (null != mPlayer_) {
            mPlayer_.stop();
            mPlayer_.release();
            mPlayer_ = null;
        }

        this.playStatus_ = PLAYER_STATUS_IDEL;
    }


    /**
     * 快进 | 快退
     *
     * @param position 快进 | 快退 的目标位置
     */
    public void seekTo(int position) {

        if (null != mPlayer_) {
            mPlayer_.seekTo(position);
        }
    }

    /**
     * 获取视频的总时长
     *
     * @return
     */
    public int getVideoTotalDuration() {
        return mVideoTotalDuration_;

    }

    /**
     * 获取视频的当前播放时间
     *
     * @return
     */
    public int getVideoCurPosition() {
        return mVideoCurPosition_;
    }

    public int getVideoWidth() {
        return mVideoWidth_;
    }

    public int getVideoHeight() {
        return mVideoHeight_;
    }

    public int getPlayStatus() {
        return playStatus_;
    }

    // ///////////////////////// 监听者
    public interface IPlayerEventListener {

        public static final int ERROR_TYPE_TIMEOUT = 4;
        public static final int ERROR_TYPE_PLAYERERROR = 3;

        public void onStart(CloudPlayer player);

        public void onFinish(CloudPlayer player);

        public void onError(CloudPlayer player, int type);

        public void onBufferingStart(CloudPlayer player);

        public void onBufferingFinish(CloudPlayer player);

        public void onInfoUpdate(CloudPlayer player, int type, Object data);

        public void onPositionUpdate(CloudPlayer player, int duration);
    }

    // //////////////////////////// mediaplayer 的回调
    // 记录播放是否成功
    private boolean mPlayCompleteSucceed_ = true;

    private int mStartSeekDuration_ = 0;

    public void setStartSeekDuration(int startSeekDuration) {
        this.mStartSeekDuration_ = startSeekDuration;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

        if (width != 0 && height != 0) {

            this.setVideoWidth(width);
            this.setVideoHeight(height);
            mHandler_.obtainMessage(MSG_FEEDBACK_MEDIA_VIDEO_SIZE, width, height).sendToTarget();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        mPlayCompleteSucceed_ = false;
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        L.d(TAG, "播放器收到了 onCompletion+++++++++++++++++");
        this.stop();
        mHandler_.sendEmptyMessage(MSG_PLAY_COMPLETE);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        // switch (what) {
        // case MediaPlayer.MEDIA_INFO_BUFFERING_START:
        // mPlayerPlayingSubState_ = PLAYER_PLAYING_LOADING;
        // mHandler_.sendEmptyMessage(MSG_BUFFERING_START);
        // break;
        // case MediaPlayer.MEDIA_INFO_BUFFERING_END:
        // mPlayerPlayingSubState_ = PLAYER_PLAYING_IN;
        // mHandler_.sendEmptyMessage(MSG_BUFFERING_FINISH);
        // break;
        // }
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        L.d(TAG, "onSeekComplete: " + mp.getCurrentPosition());
        if (this.playStatus_ != PLAYER_STATUS_IDEL) { // 如果是空闲，则表面还没有开始播放，seek是为了跳过   历史 | 片头；

            this.resume();
            this.mPlayerPlayingSubState_ = PLAYER_PLAYING_SEEK;
            mVideoCurPosition_ = mp.getCurrentPosition();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
    }

    private boolean prepared;

    @Override
    public void onPrepared(MediaPlayer mp) {
        L.e("libin", "player prepared      start time=" + mStartSeekDuration_);
        prepared = true;
        mPlayer_.start();
        if (mStartSeekDuration_ > 0) {
            mPlayer_.seekTo(mStartSeekDuration_);
        }
        this.startPlayDurationTimer();
    }

    public boolean isPrepared() {
        return prepared;
    }
}
