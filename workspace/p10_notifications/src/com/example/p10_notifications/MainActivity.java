package com.example.p10_notifications;

import java.util.ArrayList;
import java.util.List;

import com.example.p10_notifications.R.id;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity {
	
	List<Integer> notificationIds;
	int stacked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		notificationIds = new ArrayList<Integer>();
		stacked = 0;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	public void notify(View view){
		
		final int thisNotificationId;
		int contentInfo = 0;
		boolean stacking = false;
		boolean progress = false;
		
		if(notificationIds.size()>0){
			switch(view.getId()){
			case R.id.buttonNotify:
				thisNotificationId = notificationIds.size();
				notificationIds.add(Integer.valueOf(thisNotificationId));
				break;
			case R.id.buttonNotifyStack:
				thisNotificationId = notificationIds.size()-1;
				stacking = true;
				stacked++;
				break;
			case R.id.notifyProgress:
				thisNotificationId = notificationIds.size();
				notificationIds.add(Integer.valueOf(thisNotificationId));
				progress = true;
				stacking = true;
				break;
			default:
				thisNotificationId = notificationIds.size();
				notificationIds.add(Integer.valueOf(thisNotificationId));
			}
		}else{
			thisNotificationId = notificationIds.size();
			notificationIds.add(Integer.valueOf(thisNotificationId));
		}
		
		
		EditText title = (EditText)findViewById(R.id.editTextTitle);
		EditText content = (EditText)findViewById(R.id.editTextContent);
		
		String contentTitle = title.getText().toString();
		String contentText = content.getText().toString();
		
		final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
		
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentTitle(contentTitle);
		builder.setContentText(contentText);
		builder.setTicker("Nueva notificación");
		
		builder.setAutoCancel(true);
		builder.setLargeIcon(((BitmapDrawable)getResources().getDrawable(R.drawable.ic_launcher)).getBitmap());
		
		Intent resultIntent = new Intent(getApplicationContext(),ResultsActivity.class);

		resultIntent.putExtra("id", thisNotificationId);
		resultIntent.putExtra("title", contentTitle);
		resultIntent.putExtra("content", contentText);
		resultIntent.putExtra("iconId", R.drawable.ic_launcher);
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
		stackBuilder.addParentStack(ResultsActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
		
		builder.setContentIntent(resultPendingIntent);
		
		Intent pendingIntentAction = new Intent(getApplicationContext(), ResultsActivity.class);

		pendingIntentAction.putExtra("id", -1);
		pendingIntentAction.putExtra("title", "Esta viene de una acción");
		pendingIntentAction.putExtra("content", "Contenido de la acción");
		pendingIntentAction.putExtra("iconId", R.drawable.ic_default);
		
		PendingIntent resultPendingIntentAction = PendingIntent.getActivity(getApplicationContext(), 1, pendingIntentAction, PendingIntent.FLAG_UPDATE_CURRENT);
		
		builder.addAction(R.drawable.ic_default, "Acción 1", resultPendingIntentAction);
		builder.addAction(R.drawable.ic_default, "Acción 2", resultPendingIntentAction);
		builder.addAction(R.drawable.ic_default, "Acción 3", resultPendingIntentAction);

		contentInfo = thisNotificationId;
		String contentInfoStr = String.valueOf(contentInfo);
		if(stacking){
			contentInfoStr += "("+String.valueOf(stacked)+")";
		}else{
			NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
			style.setBigContentTitle(contentTitle);
			String[] lines = new String[5];
			for(int i=0; i<5; i++){
				lines[i] = "Línea "+i;
				style.addLine(lines[i]);
			}
			
			builder.setStyle(style);
		}
		
		builder.setContentInfo(contentInfoStr);
		
		final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		
		
		if(progress){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					int step;
					for (step=0; step<100; step+=10){
						//builder.setProgress(100, step, false);
						builder.setProgress(0, 0, true);
						manager.notify(thisNotificationId,builder.build());
						try {
							Thread.sleep(2*1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					builder.setProgress(0, 0, false);
					builder.setContentText("Completado");
					manager.notify(thisNotificationId, builder.build());
				}
			}).start();
		}else{

			Notification myNotification = builder.build();
			manager.notify(thisNotificationId, myNotification);
		}
		
	}
	
	public void clearNotifications(View view){
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		manager.cancelAll();
		stacked = 0;
	}

}
