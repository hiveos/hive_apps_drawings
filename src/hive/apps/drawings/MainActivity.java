package hive.apps.drawings;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

public class MainActivity extends Activity implements OnColorChangedListener {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	private Menu menu;

	private ColorPicker picker;
	private SVBar svBar;
	private OpacityBar opacityBar;
	private Button button;
	private TextView text;
	public int color;

	Browser BrowserObj;

	String saveResult;

	CrtanjeView cv;
	public static String value;
	int Value = 0;
	int EraserStatus = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		cv = (CrtanjeView) findViewById(R.id.view1);

		final SeekBar sizeBar = (SeekBar) findViewById(R.id.sbDebljina);
		sizeBar.setProgress(3);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_navigation_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				CrtanjeView.boja.setStrokeWidth(sizeBar.getProgress());
				getActionBar().setTitle("");
				updateSetings();
				enableEraser(menu.findItem(R.id.action_eraser));
				menu.findItem(R.id.action_drawing_options).setIcon(
						R.drawable.ic_brush_settings);

			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(R.string.brush_settings);
				disableEraser(menu.findItem(R.id.action_eraser));
				menu.findItem(R.id.action_drawing_options).setIcon(
						R.drawable.ic_brush_settings_selected);
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		picker = (ColorPicker) findViewById(R.id.picker);
		svBar = (SVBar) findViewById(R.id.svbar);
		opacityBar = (OpacityBar) findViewById(R.id.opacitybar);

		picker.addSVBar(svBar);
		picker.addOpacityBar(opacityBar);
		picker.setOnColorChangedListener(this);

		Intent i = getIntent();
		String drawingName = i.getStringExtra("Drawing Name");

		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View CustomActionBarView = inflator.inflate(R.layout.save, null);

		EditText ActionBarTitle = (EditText) CustomActionBarView
				.findViewById(R.id.action_bar_title);
		ActionBarTitle.setText(drawingName);
		ActionBarTitle.requestFocus();

		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setCustomView(CustomActionBarView);

		updateSetings();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
		this.menu = menu;
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void saveDrawing() {

		File gdjeSnimiti = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/Drawings/");
		if (!gdjeSnimiti.exists()) {
			gdjeSnimiti.mkdirs();
		}

		View CustomActionBarView = getActionBar().getCustomView();
		EditText ActionBarTitle = (EditText) CustomActionBarView
				.findViewById(R.id.action_bar_title);

		value = ActionBarTitle.getText().toString().trim();

		final File FileToSave = new File(
				Environment.getExternalStorageDirectory() + "/HIVE/Drawings/"
						+ value + ".png");

		if (FileToSave.exists()) {
			new AlertDialog.Builder(this)
					.setMessage(
							"Are you sure that you want to overwrite existing file?")
					.setCancelable(true)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									FileOutputStream ostream;

									try {
										FileToSave.createNewFile();
										ostream = new FileOutputStream(
												FileToSave);
										CrtanjeView.MyBitmap.compress(
												CompressFormat.PNG, 100,
												ostream);
										ostream.flush();
										ostream.close();
										saveResult = "saved";
									} catch (Exception e) {
										e.printStackTrace();
									}
									cv.mijenjan = false;
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									saveResult = "failed";
								}
							}).show();

		} else {
			FileOutputStream ostream;

			try {
				FileToSave.createNewFile();
				ostream = new FileOutputStream(FileToSave);
				CrtanjeView.MyBitmap.compress(CompressFormat.PNG, 100, ostream);
				ostream.flush();
				ostream.close();
				Toast.makeText(this, R.string.notif_file_saved,
						Toast.LENGTH_LONG).show();
				saveResult = "saved";
				uploadajNaFTPSliku();
			} catch (Exception e) {
				e.printStackTrace();
			}
			cv.mijenjan = false;

		}
	}
	
	public void uploadajNaFTPSliku(){
		//Poziva upload na server
		new FtpTask().execute();
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
									saveDrawing();
									if (saveResult.equals("saved")) {
										finish();

									} else if (saveResult.equals("failed")) {
										Toast.makeText(
												getApplicationContext(),
												R.string.error_failed_to_save_drawing,
												Toast.LENGTH_LONG).show();
									}
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									cv.ocistiFunkcija();
									finish();

								}
							}).show();
		} else {
			
			finish();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		MenuItem eraserItem = menu.findItem(R.id.action_eraser);
		MenuItem brushSettingsItem = menu.findItem(R.id.action_drawing_options);

		switch (item.getItemId()) {
		case R.id.action_save:

			View CustomActionBarView = getActionBar().getCustomView();
			EditText ActionBarTitle = (EditText) CustomActionBarView
					.findViewById(R.id.action_bar_title);

			String TitleValue = ActionBarTitle.getText().toString().trim();

			if (TitleValue.equals("New Drawing")) {
				toggleIME();
			} else {
				saveDrawing();

			}

			return true;

		case R.id.action_clear:
			cv.ocistiFunkcija();
			return true;

		case R.id.action_drawing_options:
			if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
				closeDrawer();
				updateSetings();
				enableEraser(eraserItem);
				brushSettingsItem.setIcon(R.drawable.ic_brush_settings);
			} else {
				openDrawer();
				disableEraser(eraserItem);
				brushSettingsItem
						.setIcon(R.drawable.ic_brush_settings_selected);
			}
			return true;

		case R.id.action_eraser:
			if (EraserStatus == 0) {
				CrtanjeView.boja.setColor(Color.WHITE);
				CrtanjeView.putanja = new mojaPutanja(new Paint(
						CrtanjeView.boja));
				CrtanjeView.paths.add(CrtanjeView.putanja);
				eraserItem.setIcon(R.drawable.ic_eraser_selected);
				brushSettingsItem
						.setIcon(R.drawable.ic_brush_settings_disabled);
				brushSettingsItem.setEnabled(false);
				EraserStatus = 1;
			} else if (EraserStatus == 1) {
				eraserItem.setIcon(R.drawable.ic_eraser);
				brushSettingsItem.setIcon(R.drawable.ic_brush_settings);
				brushSettingsItem.setEnabled(true);
				updateSetings();
				EraserStatus = 0;
			}
		default:
			return false;
		}
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

	public void enableEraser(MenuItem item) {
		item.setIcon(R.drawable.ic_eraser);
		item.setEnabled(true);
	}

	public void disableEraser(MenuItem item) {
		item.setIcon(R.drawable.ic_eraser_disabled);
		item.setEnabled(false);
	}

	public void toggleIME() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		CrtanjeView.paths.clear();
		
	}
	
	
	
}
