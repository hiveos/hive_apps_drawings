package com.example.drawing;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

public class MainActivity extends Activity implements OnColorChangedListener {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	private ColorPicker picker;
	private SVBar svBar;
	private OpacityBar opacityBar;
	private Button button;
	private TextView text;
	public int color;

	CrtanjeView cv;
	int value = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		cv = (CrtanjeView) findViewById(R.id.view1);

		SeekBar sizeBar = (SeekBar) findViewById(R.id.sbDebljina);
		sizeBar.setProgress(10);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_navigation_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				getActionBar().setTitle("");
				updateSetings();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(R.string.brush_settings);
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		picker = (ColorPicker) findViewById(R.id.picker);
		svBar = (SVBar) findViewById(R.id.svbar);
		opacityBar = (OpacityBar) findViewById(R.id.opacitybar);

		picker.addSVBar(svBar);
		picker.addOpacityBar(opacityBar);
		picker.setOnColorChangedListener(this);

		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		
		updateSetings();

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void snimi() {
		int brojCrteza = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Drawings").listFiles().length;
		String imeCrteza = "Drawing" + brojCrteza + ".png";
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
						+ "/HIVE/Drawings/" + value);
				FileOutputStream ostream;
				try {
					crtez.createNewFile();
					ostream = new FileOutputStream(crtez);
					CrtanjeView.MyBitmap.compress(CompressFormat.PNG, 100,
							ostream);
					ostream.flush();
					ostream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				cv.mijenjan = false;
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
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
									// MainActivity.this.finish();
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
		switch (item.getItemId()) {
		case R.id.action_save:
			snimi();
			return true;
		case R.id.action_clear:
			cv.ocistiFunkcija();
			return true;
		case R.id.action_drawing_options:
			if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
				closeDrawer();
				updateSetings();
			} else {
				openDrawer();
			}
			return true;

		case R.id.action_eraser:
			CrtanjeView.boja.setColor(Color.WHITE);
			CrtanjeView.putanja = new mojaPutanja(new Paint(CrtanjeView.boja));
			CrtanjeView.paths.add(CrtanjeView.putanja);
			return true;
			// case R.id.action_size:
			// dijalogZaDebljinu();
			// return true;
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

	@Override
	public void onColorChanged(int color) {
		// TODO Auto-generated method stub

	}

	public void closeDrawer() {
		mDrawerLayout.closeDrawer(Gravity.START);
	}

	public void openDrawer() {
		mDrawerLayout.openDrawer(Gravity.START);
	}

	public void updateSetings() {
		SeekBar sizeBar = (SeekBar) findViewById(R.id.sbDebljina);
		color = picker.getColor();
		picker.setOldCenterColor(color);
		CrtanjeView.boja.setColor(color);
		CrtanjeView.putanja = new mojaPutanja(new Paint(CrtanjeView.boja));
		CrtanjeView.paths.add(CrtanjeView.putanja);
		CrtanjeView.boja.setStrokeWidth(sizeBar.getProgress());
	}
}
