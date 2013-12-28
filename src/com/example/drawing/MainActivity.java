package com.example.drawing;

import java.io.File;
import java.io.FileOutputStream;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.CompressFormat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnColorChangedListener {

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
			return true;
		case R.id.action_color:
			dijalogZaBoju();
			return true;
		default:
			return false;
		}
	}
	
	public void dijalogZaBoju(){
		Intent myIntent = new Intent(MainActivity.this, Pickcolor.class);
		MainActivity.this.startActivity(myIntent);
		
	}

	@Override
	public void onColorChanged(int color) {
		// TODO Auto-generated method stub
		
	}

}
