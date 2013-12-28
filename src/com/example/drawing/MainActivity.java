package com.example.drawing;

import java.io.File;
import java.io.FileOutputStream;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap.CompressFormat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	CrtanjeView cv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		cv = (CrtanjeView)findViewById(R.id.view1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void snimi(){
		File gdjeSnimiti = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Drawings/");
		if (!gdjeSnimiti.exists()) {
			gdjeSnimiti.mkdirs();
		}
		int brojCrteza = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Drawings").listFiles().length;
		File crtez = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Drawings/Drawing" + brojCrteza + ".png");
		FileOutputStream ostream;
		try {
			crtez.createNewFile();
			ostream = new FileOutputStream(crtez);
			CrtanjeView.MyBitmap.compress(CompressFormat.PNG, 100, ostream);
			ostream.flush();
			ostream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		cv.mijenjan=false;

	}
	
	

	@Override
	public void onBackPressed() {
		if(cv.mijenjan==true)
		{
			new AlertDialog.Builder(this)
	        .setMessage("Do you want to save your drawing?")
	        .setCancelable(false)
	        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	            	snimi();
	                 MainActivity.this.finish();
	            }
	        })
	        .setNegativeButton("No", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	                 MainActivity.this.finish();
	            }
	        })
	        .show();
		}
		else MainActivity.this.finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
		{
		case R.id.action_save:
			snimi();
			return true;
		case R.id.action_settings:
			cv.otvoriMenu();
			return true;
		case R.id.action_clear:
			cv.ocistiFunkcija();
		default:
			return false;
		}
	}
	
	

}
