package com.example.drawing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

@SuppressLint("NewApi")
public class Browser extends ListActivity {

	private List<String> items = null;
	private File Lokacija = new File(
			Environment.getExternalStorageDirectory() + "/HIVE/Drawings/");
	public static Bitmap LoadaniCrtez;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_browser);
		if (!Lokacija.exists())
			Lokacija.mkdirs();
		getFiles(new File(Environment.getExternalStorageDirectory() + "/HIVE/Drawings/")
				.listFiles());

		ActionBar actionBar = getActionBar();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		int selectedRow = (int) id;

		
			File file = new File(items.get(selectedRow));
			String imeFajla = file.getName().toString();
			String ekstenzijaFajla = imeFajla.substring(
					(imeFajla.lastIndexOf(".") + 1), imeFajla.length());

			if (file.isFile() && ekstenzijaFajla.equals("png")) {
					//AKO JE FAJL PNG
				LoadaniCrtez = BitmapFactory.decodeFile(file.getAbsolutePath());
				Intent myIntent = new Intent(Browser.this, MainActivity.class);
				Browser.this.startActivity(myIntent);
		

		}
	}

	private void getFiles(File[] files) {
		items = new ArrayList<String>();
		for (File file : files) {
			items.add(file.getPath());
		}
		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this,
				R.layout.file_list_row, items);
		setListAdapter(fileList);
	}
}