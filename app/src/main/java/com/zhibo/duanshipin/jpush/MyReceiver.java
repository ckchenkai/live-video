package com.zhibo.duanshipin.jpush;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.zhibo.duanshipin.R;
import com.zhibo.duanshipin.activity.VideoPlayActivity;
import com.zhibo.duanshipin.utils.Consts;
import com.zhibo.duanshipin.utils.ULog;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
				String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				ULog.d(Consts.TAG, "[MyReceiver] 接收Registration Id : " + regId);
				//send the Registration Id to your server...

			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
				ULog.d(Consts.TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
				showCustomeMsg(context, bundle);

			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
				ULog.d(Consts.TAG, "[MyReceiver] 接收到推送下来的通知");
				int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
				ULog.d(Consts.TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
				ULog.d(Consts.TAG, "[MyReceiver] 用户点击打开了通知");
				String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
				ULog.e(Consts.TAG, extras);
				openNotification(context,bundle);
			} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
				ULog.d(Consts.TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
				//在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

			} else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
				boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
				ULog.w(Consts.TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
			} else {
				ULog.d(Consts.TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
			}
		} catch (Exception e){

		}

	}

	/**
	 * 处理通知消息
	 * @param context
	 * @param bundle
	 */
	private void openNotification(Context context, Bundle bundle){
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		if (TextUtils.isEmpty(extras)) {
			return;
		}
		String content = TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_ALERT))?"":bundle.getString(JPushInterface.EXTRA_ALERT);
		try {
			JSONObject json = new JSONObject(extras);
			String pushType=json.optString("push_type");
			String pushId=json.optString("push_id");
			String pushUrl=json.optString("push_url");
			ULog.e(Consts.TAG, pushType+" "+pushId+" "+pushUrl);
//			if(TextUtils.equals(pushType,"0")){  //图文直播
//				VideoPlayActivity.startVideoPlay(context, "", pushUrl, pushId, content,true, true, false);
//			}else{                               //视频直播
//				VideoPlayActivity.startVideoPlay(context, pushUrl, "", pushId, content, false,true, false);
//			}
			Intent mIntent = new Intent(context, VideoPlayActivity.class);
			mIntent.putExtra("termId", pushId);
			mIntent.putExtra("description", content);
			mIntent.putExtra("liveStreaming", true);
			mIntent.putExtra("cache", false);
			if(TextUtils.equals(pushType,"0")){  //图文直播
				mIntent.putExtra("videoPath", "");
				mIntent.putExtra("thumb", pushUrl);
				mIntent.putExtra("isPicLive",true);
			}else{								//视频直播
				mIntent.putExtra("videoPath", pushUrl);
				mIntent.putExtra("thumb", "");
				mIntent.putExtra("isPicLive",false);
			}
			mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
			context.startActivity(mIntent);
		} catch (JSONException e) {
			e.printStackTrace();
			ULog.e(Consts.TAG, "极光推送解析错误");
		}
	}

	/**
	 * 处理自定义消息
	 */
	private static int notiId = 0;
	private void showCustomeMsg(Context context,Bundle bundle){
		String title = TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_TITLE))?"":bundle.getString(JPushInterface.EXTRA_TITLE);
		String content = TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_MESSAGE))?"":bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		if (TextUtils.isEmpty(extras)) {
			return;
		}
		Intent mIntent = null;
		try {
			JSONObject json = new JSONObject(extras);
			String pushType=json.optString("push_type");
			String pushId=json.optString("push_id");
			String pushUrl=json.optString("push_url");
			ULog.e(Consts.TAG, pushType+" "+pushId+" "+pushUrl);
			mIntent = new Intent(context, VideoPlayActivity.class);
			mIntent.putExtra("termId", pushId);
			mIntent.putExtra("description", content);
			mIntent.putExtra("liveStreaming", true);
			mIntent.putExtra("cache", false);
			if(TextUtils.equals(pushType,"0")){  //图文直播
				mIntent.putExtra("videoPath", "");
				mIntent.putExtra("thumb", pushUrl);
				mIntent.putExtra("isPicLive",true);
			}else{								//视频直播
				mIntent.putExtra("videoPath", pushUrl);
				mIntent.putExtra("thumb", "");
				mIntent.putExtra("isPicLive",false);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			ULog.e(Consts.TAG, "极光推送解析错误");
		}
		NotificationManager  notificationManager = (NotificationManager)context. getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);
		nBuilder.setSmallIcon(R.drawable.jpush_notification_icon)
				//.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher))
				.setTicker(title)
				.setContentTitle(title)
				.setContentText(content)
				.setWhen(System.currentTimeMillis())
				.setContentIntent(pendingIntent)
				.setLights(0xFF0000, 3000, 3000)
				.setAutoCancel(true)
				.setColor(Color.parseColor("#ff7000"));
		notificationManager.notify(notiId++,nBuilder.build());
	}
}
