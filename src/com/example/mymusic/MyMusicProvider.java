package com.example.mymusic;

import java.io.FileDescriptor;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.RemoteViews;

public class MyMusicProvider extends AppWidgetProvider {

	public static final String TOGGLEPAUSE_ACTION = "com.android.music.musicservicecommand.togglepause";
	public static final String PAUSE_ACTION = "com.android.music.musicservicecommand.pause";
	public static final String PREVIOUS_ACTION = "com.android.music.musicservicecommand.previous";
	public static final String NEXT_ACTION = "com.android.music.musicservicecommand.next";
	public static final String PLAYSTATE_CHANGED = "com.android.music.playstatechanged";
	public static final String META_CHANGED = "com.android.music.metachanged";
	public static final String MUSIC_SERVICE = "com.example.mymusic.MUSIC_SERVICE";

	// public static RemoteViews musicRemoteViews;
	// public static Context mContext;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		RemoteViews musicRemoteViews = new RemoteViews(
				context.getPackageName(), R.layout.mymusic_widget_layout);
		initView(context, musicRemoteViews);
		musicRemoteViews.setProgressBar(R.id.probar_song, 100, 0, false);
		appWidgetManager.updateAppWidget(appWidgetIds, musicRemoteViews);

		Intent musicIntent = new Intent(MUSIC_SERVICE);
		context.startService(musicIntent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.v("receive", intent.getAction());
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		ComponentName provider = new ComponentName(context,
				MyMusicProvider.class);
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(provider);
		RemoteViews musicRemoteViews = new RemoteViews(
				context.getPackageName(), R.layout.mymusic_widget_layout);
		String actionString = intent.getAction();

		if (actionString.equals(PLAYSTATE_CHANGED)
				|| actionString.equals(META_CHANGED)) {
//			updateViews(context, intent, -1);
			linkButtons(context, musicRemoteViews);
			getSongInfo(intent, musicRemoteViews);
			changePlayState(context, intent, musicRemoteViews);
			musicRemoteViews.setImageViewBitmap(R.id.img_album,
					getArtwork(context, intent));
			musicRemoteViews.setProgressBar(R.id.probar_song, 100,
					MyService.mProgress, false);

		}

		appWidgetManager.updateAppWidget(appWidgetIds, musicRemoteViews);
		// else if (actionString.equals(META_CHANGED)) {
		// updateViews(context, intent, 0);
		// }

	}

//	public static void updateViews(Context context, Intent intent, int progerss) {
//		AppWidgetManager appWidgetManager = AppWidgetManager
//				.getInstance(context);
//		ComponentName provider = new ComponentName(context,
//				MyMusicProvider.class);
//		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(provider);
//		RemoteViews musicRemoteViews = new RemoteViews(
//				context.getPackageName(), R.layout.mymusic_widget_layout);
//
//		linkButtons(context, musicRemoteViews);
//		if (intent != null) {
//			changePlayState(context, intent, musicRemoteViews);
//			getSongInfo(intent, musicRemoteViews);
//			musicRemoteViews.setImageViewBitmap(R.id.img_album,
//					getArtwork(context, intent));
//			if (!intent.getBooleanExtra("playing", false)) {
//				MyService.mHandler.removeMessages(MyService.UPDATE_PROGRESS);
//			} else {
//				MyService.mHandler.sendEmptyMessage(MyService.UPDATE_PROGRESS);
//			}
//		}
//		if (progerss >= 0) {
//			musicRemoteViews.setProgressBar(R.id.probar_song, 100, progerss,
//					false);
//		}
//		appWidgetManager.updateAppWidget(appWidgetIds, musicRemoteViews);
//	}

	public static void updateProgress(Context context, int progress) {
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		ComponentName provider = new ComponentName(context,
				MyMusicProvider.class);
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(provider);
		RemoteViews musicRemoteViews = new RemoteViews(
				context.getPackageName(), R.layout.mymusic_widget_layout);
		musicRemoteViews.setProgressBar(R.id.probar_song, 100, progress,
				false);
		appWidgetManager.updateAppWidget(appWidgetIds, musicRemoteViews);
	}

	private static void getSongInfo(Intent intent, RemoteViews remoteViews) {
		String songString = intent.getStringExtra("track");
		String artiString = intent.getStringExtra("artist");
		remoteViews.setTextViewText(R.id.tv_songname, songString);
		remoteViews.setTextViewText(R.id.tv_artist, artiString);
	}

	private void changePlayState(Context context, Intent intent,
			RemoteViews remoteViews) {

		boolean isPlaying = intent.getBooleanExtra("playing", false);
		Log.e("111", intent.getAction() + " " + isPlaying);
		if (isPlaying) {
			remoteViews
					.setImageViewResource(R.id.img_play, R.drawable.pause_bg);
		} else {
			remoteViews.setImageViewResource(R.id.img_play, R.drawable.play_bg);
		}
	}
/**xianshi*/
	// private void getAlbumCover(Context context, Intent intent) {
	// Bitmap bmBitmap = null;
	// long songid = intent.getLongExtra("id", -1L);
	// Uri uri = Uri.parse("content://media/external/audio/media/" + songid
	// + "/albumart");
	// try {
	// ParcelFileDescriptor pfd = context.getContentResolver()
	// .openFileDescriptor(uri, "r");
	//
	// // if (pfd != null) {
	// FileDescriptor fd = pfd.getFileDescriptor();
	// bmBitmap = BitmapFactory.decodeFileDescriptor(fd);
	// musicRemoteViews.setImageViewBitmap(R.id.img_album, bmBitmap);
	// bmBitmap = null;
	// // }else{
	// // musicRemoteViews.removeAllViews(R.id.img_album);
	// // return;
	// // }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }

	private Bitmap getArtwork(Context context, Intent intent) {
		String str = "content://media/external/audio/media/"
				+ intent.getLongExtra("id", -1L) + "/albumart";
		Uri uri = Uri.parse(str);
		ParcelFileDescriptor pfd = null;
		try {
			pfd = context.getContentResolver().openFileDescriptor(uri, "r");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (pfd != null) {
			Bitmap bm = null;
			FileDescriptor fd = pfd.getFileDescriptor();
			bm = BitmapFactory.decodeFileDescriptor(fd);
			return bm;
		}
		return null;
	}

	private void linkButtons(Context context, RemoteViews remoteViews) {
		Intent intent;
		PendingIntent pendingIntent;
		remoteViews.setImageViewResource(R.id.img_rewind, R.drawable.rewind_bg);
		remoteViews.setImageViewResource(R.id.img_play, R.drawable.play_bg);
		remoteViews.setImageViewResource(R.id.img_next, R.drawable.forward_bg);

		intent = new Intent("com.android.music.PLAYBACK_VIEWER");
		pendingIntent = PendingIntent.getActivity(context, 0,
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);
		remoteViews.setOnClickPendingIntent(R.id.album_cover, pendingIntent);

		intent = new Intent(TOGGLEPAUSE_ACTION);
		pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		remoteViews.setOnClickPendingIntent(R.id.img_play, pendingIntent);

		intent = new Intent(NEXT_ACTION);
		pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		remoteViews.setOnClickPendingIntent(R.id.img_next, pendingIntent);

		intent = new Intent(PREVIOUS_ACTION);
		pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		remoteViews.setOnClickPendingIntent(R.id.img_rewind, pendingIntent);
	}

	private void initView(Context context, RemoteViews remoteViews) {
		Intent intent;
		PendingIntent pendingIntent;
		intent = new Intent("com.android.music.PLAYBACK_VIEWER");
		pendingIntent = PendingIntent.getActivity(context, 0,
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);
		remoteViews.setOnClickPendingIntent(R.id.album_cover, pendingIntent);
		remoteViews.setImageViewResource(R.id.img_rewind,
				R.drawable.button_rewind_gray);
		remoteViews.setImageViewResource(R.id.img_play,
				R.drawable.button_play_gray);
		remoteViews.setImageViewResource(R.id.img_next,
				R.drawable.button_forward_gray);
	}

}
