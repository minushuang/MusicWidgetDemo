package com.example.mymusic;

import com.android.music.IMediaPlaybackService;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class MyService extends Service {

	private static IMediaPlaybackService mService;
	public static long mPosition;
	public static long mDuration;
	public static int mProgress;
	public static Handler mHandler;
	public static final int UPDATE_PROGRESS = 1;
	public boolean miStart = false;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Intent i = new Intent();
		i.setClassName("com.android.music",
				"com.android.music.MediaPlaybackService");

		ServiceConnection conn = new MediaPlayerServiceConnection();
		boolean isBinded = this.bindService(i, conn, 0);
		Log.v("isbinded", "isBinded : " + isBinded);
		if(!miStart && mHandler!= null){
			
			mHandler.sendEmptyMessage(UPDATE_PROGRESS);
			miStart = true;
		}
		return 1;

	}
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				try {
					mPosition = mService.position();
					mDuration = mService.duration();
					mProgress = (int) (100 * mPosition/mDuration);
					
					MyMusicProvider.updateProgress(MyService.this, mProgress);
					
//					MyMusicProvider.musicRemoteViews.setProgressBar(R.id.probar_song, 100, mProgress, false);
//					AppWidgetManager appWidgetManager = AppWidgetManager
//							.getInstance(MyMusicProvider.mContext);
//					ComponentName provider = new ComponentName(MyMusicProvider.mContext,
//							MyMusicProvider.class);
//					int[] appWidgetIds = appWidgetManager.getAppWidgetIds(provider);
//					appWidgetManager.updateAppWidget(appWidgetIds, MyMusicProvider.musicRemoteViews);
//					MyMusicProvider.updateView(context, intent, remoteViews);
//					Log.i("MyMusic", "Service_position= " + mPosition);
//					Log.i("MyMusic", "Service_duration= " + mDuration);
//					Log.i("MyMusic", "Service_progerss= " + mProgress);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);
			}
		};
	}

	class MediaPlayerServiceConnection implements ServiceConnection {

		public void onServiceConnected(ComponentName name, IBinder boundService) {
			Log.i("MediaPlayerServiceConnection",
					"Connected! Name: " + name.getClassName());

			// This is the important line
			mService = IMediaPlaybackService.Stub.asInterface(boundService);

			// If all went well, now we can use the interface

			try {
				Log.i("MediaPlayerServiceConnection", "Playing track: "
						+ mService.getTrackName());
				// Tell the player to pause the song
				// mService.pause();
				mPosition = mService.position();
				// Log.e("position", " " + position );

				// Log.i("MediaPlayerServiceConnection", "By artist: " +
				// service.getArtistName());
				/*
				 * if (service.isPlaying()) {
				 * Log.i("MediaPlayerServiceConnection",
				 * "Music player is playing."); } else {
				 * Log.i("MediaPlayerServiceConnection",
				 * "Music player is not playing."); }
				 */
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		public void onServiceDisconnected(ComponentName name) {
			mService = null;
			Log.i("MediaPlayerServiceConnection", "Disconnected!");
		}

	}



}
