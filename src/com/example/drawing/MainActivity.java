package com.example.drawing;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;

public class MainActivity extends Activity implements OnColorChangedListener {

	CrtanjeView cv;
	int value = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		cv = (CrtanjeView) findViewById(R.id.view1);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void snimi() {
		int brojCrteza = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Drawings").listFiles().length;
		String imeCrteza="Drawing"+brojCrteza+".png";
		File gdjeSnimiti = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Drawings/");
		if (!gdjeSnimiti.exists()) {
			gdjeSnimiti.mkdirs();
		}
		
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
	    final EditText input = new EditText(this);
	    input.setText(imeCrteza);
	    alert.setTitle("Pick a name for your drawing:");
	    alert.setView(input);
	    alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            String value = input.getText().toString().trim();
	            File crtez = new File(Environment.getExternalStorageDirectory()
	    				+ "/HIVE/Drawings/"+value);
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
	    		cv.mijenjan = false;
	        }
	    });

	    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            dialog.cancel();
	        }
	    });
	    alert.show();

	}

	@Override
	public void onBackPressed() {
		if (cv.mijenjan == true) {
			new AlertDialog.Builder(this)
					.setMessage("Do you want to save your drawing?")
					.setCancelable(true)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									snimi();
									//MainActivity.this.finish();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									cv.ocistiFunkcija();
									MainActivity.this.finish();
								}
							}).show();
		} else
			MainActivity.this.finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_save:
			snimi();
			return true;
		case R.id.action_clear:
			cv.ocistiFunkcija();
			return true;
		case R.id.action_color:
			dijalogZaBoju();
			return true;
		case R.id.action_eraser:
			CrtanjeView.boja.setColor(Color.WHITE);
			CrtanjeView.putanja = new mojaPutanja(new Paint(CrtanjeView.boja));
			CrtanjeView.paths.add(CrtanjeView.putanja);
			return true;
		case R.id.action_size:
			dijalogZaDebljinu();
			return true;
		default:
			return false;
		}
	}

	public void dijalogZaDebljinu() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Brush size");
		final TextView text = new TextView(this);
		text.setText("Hello Android");
		text.setPadding(10, 10, 10, 10);
		LinearLayout linear = new LinearLayout(this);
		linear.setOrientation(1);
		SeekBar seek = new SeekBar(this);
		linear.addView(seek);
		linear.addView(text);
		alert.setView(linear);

		seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar bar) {
				value = bar.getProgress(); // the value of the seekBar progress
			}

			public void onStartTrackingTouch(SeekBar bar) {
			}

			public void onProgressChanged(SeekBar bar, int paramInt,
					boolean paramBoolean) {
				value = paramInt;
				text.setText("" + paramInt + "%"); // here in textView the
													// percent will be shown
			}
		});

		alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				CrtanjeView.boja.setStrokeWidth((float) value);
				CrtanjeView.putanja = new mojaPutanja(new Paint(
						CrtanjeView.boja));
				CrtanjeView.paths.add(CrtanjeView.putanja);
				dialog.dismiss();
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});

		alert.show();
	}

	public void dijalogZaBoju() {
		Intent myIntent = new Intent(MainActivity.this, Pickcolor.class);
		MainActivity.this.startActivity(myIntent);

	}

	@Override
	public void onColorChanged(int color) {
		// TODO Auto-generated method stub

	}

}
