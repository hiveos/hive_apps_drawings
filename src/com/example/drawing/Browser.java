package com.example.drawing;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class Browser extends Activity {

	public static Bitmap LoadaniCrtez;
	private ImageAdapter imageAdapter;
	ArrayList<String> f = new ArrayList<String>();
	File[] listFile;
	ArrayList<String> fileNames = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);
		getFromSdcard();
		GridView imagegrid = (GridView) findViewById(R.id.gridview);
		imageAdapter = new ImageAdapter();
		imagegrid.setAdapter(imageAdapter);

		File DrawingsDir = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Drawings");
		if (!DrawingsDir.exists())
			DrawingsDir.mkdirs();

		imagegrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				LoadaniCrtez = BitmapFactory.decodeFile(listFile[position]
						.getAbsolutePath());

				Intent i = new Intent(getApplicationContext(),
						MainActivity.class);
				i.putExtra("Drawing Name", fileNames.get(position));
				startActivity(i);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.browser, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_add:
			File initialDrawing = new File(
					Environment.getExternalStorageDirectory()
							+ "/HIVE/Drawings/Drawing0.png");
			Bitmap.Config conf = Bitmap.Config.ARGB_8888;
			LoadaniCrtez = Bitmap.createBitmap(10, 10, conf);
			LoadaniCrtez.recycle();
			Intent myIntent = new Intent(Browser.this, MainActivity.class);
			myIntent.putExtra("Drawing Name", "New Drawing");
			Browser.this.startActivity(myIntent);
			return true;
		default:
			return false;
		}
	}

	public void getFromSdcard() {
		File file = new File(
				android.os.Environment.getExternalStorageDirectory(),
				"/HIVE/Drawings");

		if (file.isDirectory()) {
			listFile = file.listFiles();

			for (File infile : listFile) {
				f.add(infile.getAbsolutePath());
				fileNames.add(infile.getName().toString().split("\\.")[0]);
			}
		}
	}

	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ImageAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return f.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.griditem, null);
				holder.imageview = (ImageView) convertView
						.findViewById(R.id.drawingPreview);
				holder.textview = (TextView) convertView
						.findViewById(R.id.drawingName);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Bitmap myBitmap = BitmapFactory.decodeFile(f.get(position));
			holder.imageview.setImageBitmap(myBitmap);
			holder.textview.setText(fileNames.get(position));
			return convertView;
		}
	}

	class ViewHolder {
		ImageView imageview;
		TextView textview;

	}
}