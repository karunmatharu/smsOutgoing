package com.example.smstest;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.example.smsoutgoing.R;

/*
 * This application shows sample code to listen for 
 * outgoing SMS messages. Unlike incoming messages, outgoing messages
 * cannot be listened for via a broadcast intent.
 * This method uses Android's system SMS content provider 
 * and a content observer.
 * 
 * It is important to note that the SMS content provider 
 * is not part of of Android's public API
 */
public class MainActivity extends Activity {

	private static final String TAG = "smsOutgoing";
	private String lastSmsId = "";	//used to store id of the last sent message

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Context context = getApplicationContext();

		// Handler parameter is passed as null so that
		// onChange method is called immediately regardless of thread
		SmsObserver SmsObserver = new SmsObserver(null);

		ContentResolver contentResolver = context.getContentResolver();
		contentResolver.registerContentObserver(Uri.parse("content://sms/"),
				true, SmsObserver);
	}

	
	protected void getOutgoingSMS() {
		Log.i(TAG, "attempting to retrieve message");

		ContentValues values = new ContentValues();
		values.put("type", 1);

		Uri uriSMSURI = Uri.parse("content://sms/");

		this.getContentResolver().insert(uriSMSURI, values);

		// Uri uriSMSURI = Uri.parse("content://sms/");
		Cursor cur = this.getContentResolver().query(uriSMSURI, null, null,
				null, null);

		// this will make it point to the first record, which is the last SMS
		// sent
		cur.moveToNext();
		String content = cur.getString(cur.getColumnIndex("body"));
		// use cur.getColumnNames() to get a list of all available columns...
		// each field that compounds a SMS is represented by a column (phone
		// number, status, etc.)
		// then just save all data you want to the SDcard :)
		String columnNum = cur.getString(cur.getColumnIndex("type"));

		// Log.i(TAG, "Type : " + columnNum + " Content: " + content);

		// Column headers for content://sms/ table
		/*
		 * 0: _id 1: thread_id 2: address 3: person 4: date 5: protocol 6: read
		 * 7: status 8: type 9: reply_path_present 10: subject 11: body 12:
		 * service_center 13: locked
		 */

		String[] columns = new String[] { "address", "person", "date", "body",
				"type" };

		// if (cur.getCount() > 0) {
		String count = Integer.toString(cur.getCount());
		Log.i("Count", count);
		// while (cur.moveToNext()){
		String type = cur.getString(cur.getColumnIndex(columns[4]));
		if (type.equals("2")) // 2 for Sent Sms
		{
			String address = cur.getString(cur.getColumnIndex("address"));
			String name = cur.getString(cur.getColumnIndex("person"));
			String date = cur.getString(cur.getColumnIndex("date"));
			String msg = cur.getString(cur.getColumnIndex("body"));
			String id = cur.getString(cur.getColumnIndex("_id"));

			// Only log message details if message it isnt
			// a duplicate of the previous message
			if (!id.equals(lastSmsId)) {
				Log.i(TAG, " ");
				Log.i(TAG, "NEW MESSAGE");
				Log.i(TAG, "Id: " + id);
				Log.i(TAG, "Address: " + address);
				Log.i(TAG, "Name: " + name);
				Log.i(TAG, "date: " + date);
				Log.i(TAG, "msg: " + msg);

				// update the last sms id
				lastSmsId = id;
			}

		}
		// }
		// }
	}

	class SmsObserver extends ContentObserver {
		public SmsObserver(Handler handler) {
			super(handler);
		}

		/*
		 * Called when content://sms/ changes This method is often called
		 * multiple times when a single sms message is sent
		 */
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.i(TAG, "SMS content changed");
			getOutgoingSMS();
		}
	}
}
