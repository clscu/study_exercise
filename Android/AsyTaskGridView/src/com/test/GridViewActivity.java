package com.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class GridViewActivity extends Activity {
	private GridView gridview;
	private Context context;
	private List lstImageItem;
	private GridViewAdapter gridViewAdapter;
	private ImageUpdateTask imageUpdateTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		context = this;
		gridview = (GridView) findViewById(R.id.GridView);
		gridview.setNumColumns(3);

		imageUpdateTask = (ImageUpdateTask) new ImageUpdateTask().execute();

	}

	class ImageUpdateTask extends
			AsyncTask<Void, HashMap<String, Object>, String> {
		@Override
		protected void onPreExecute() {

			lstImageItem = new ArrayList<HashMap<String, Object>>();
			gridViewAdapter = new GridViewAdapter(context, lstImageItem);
			gridview.setAdapter(gridViewAdapter);
			// ��һ��ִ�з���
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(Void... params) {
			for (int i = 0; i < 8; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemImage", R.drawable.gallery_photo_1 + i);// ���ͼ����Դ��ID
				map.put("ItemText", "NO." + String.valueOf(i));// �������ItemText
				publishProgress(map);
			}

			return "ִ�����";
		}

		@Override
		public void onProgressUpdate(HashMap<String, Object>... maps) {
			for (HashMap<String, Object> map : maps) {
				lstImageItem.add(map);
			}
			// �����֪ͨadapter���µ�photo update.
			gridViewAdapter.notifyDataSetChanged();
		}

		@Override
		protected void onPostExecute(String result) {
			// doInBackground����ʱ���������仰˵������doInBackgroundִ����󴥷�
			Log.i("cl","postExecute");
			super.onPostExecute(result);

		}

	}
}