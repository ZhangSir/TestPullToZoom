package com.test.pulltozoom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.test.pulltozoom.view.PullToZoomBase.OnPullZoomListener;
import com.test.pulltozoom.view.PullToZoomListView;

public class MainActivity extends Activity {

	private final String TAG = MainActivity.class.getSimpleName();
	
	private PullToZoomListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		listView = (PullToZoomListView) findViewById(R.id.listview);

        String[] adapterData = new String[]{"Activity", "Service", "Content Provider", "Intent", "BroadcastReceiver", "ADT", "Sqlite3", "HttpClient",
                "DDMS", "Android Studio", "Fragment", "Loader", "Activity", "Service", "Content Provider", "Intent",
                "BroadcastReceiver", "ADT", "Sqlite3", "HttpClient", "Activity", "Service", "Content Provider", "Intent",
                "BroadcastReceiver", "ADT", "Sqlite3", "HttpClient"};

        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, adapterData));
        listView.getPullRootView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "position = " + position);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "position = " + position);
            }
        });

//        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
//        int mScreenHeight = localDisplayMetrics.heightPixels;
//        int mScreenWidth = localDisplayMetrics.widthPixels;
//        AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
//        listView.setHeaderLayoutParams(localObject);
        
        
        listView.setOnPullZoomListener(new OnPullZoomListener() {

			@Override
			public void onPullZooming(int newScrollValue) {
				// TODO Auto-generated method stub
				Log.d(TAG, "onPullZooming--" + newScrollValue);
			}

			@Override
			public void onPullZoomEnd() {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "缩放结束", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "刷新", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onLoad() {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "更多", Toast.LENGTH_SHORT).show();
				Intent it = new Intent();
				it.setClass(MainActivity.this, PullToZoomScrollViewActivity.class);
				startActivity(it);
			}
		});
	}
}
