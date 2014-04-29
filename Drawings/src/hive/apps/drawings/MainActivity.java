package hive.apps.drawings;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
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

import hive.apps.drawings.helpers.HiveHelper;

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

    String drawingName, drawingId;

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
		drawingName = i.getStringExtra("Drawing Name");
		drawingId = i.getStringExtra("Drawing Id");

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
						+ drawingId + ".png");

		FileOutputStream ostream;

		try {
			FileToSave.createNewFile();
			ostream = new FileOutputStream(FileToSave);
			CrtanjeView.MyBitmap.compress(CompressFormat.PNG, 100, ostream);
			ostream.flush();
			ostream.close();
            new UploadTask().execute();
			saveResult = "saved";
		} catch (Exception e) {
			e.printStackTrace();
		}

        new EditTask().execute(value);

        cv.mijenjan = false;
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

   private class UploadTask extends AsyncTask<Void, Void, Void> {

       @Override
       protected Void doInBackground(Void... voids) {
           File origfile = new File(Environment.getExternalStorageDirectory(),
                   "/HIVE/Drawings/" + drawingId + ".png");

           File file = new File(Environment.getExternalStorageDirectory(),
                   "/HIVE/Zipped_Drawings/" + "page1" + ".png");

           File zipFile = new File(Environment.getExternalStorageDirectory()
                   + "/HIVE/Zipped_Drawings/" + drawingName + ".zip");

           File zipFiles = new File(Environment.getExternalStorageDirectory()
                   + "/HIVE/Zipped_Drawings");

           if(!zipFiles.exists()) {
               zipFiles.mkdirs();
           }

           origfile.renameTo(file);

           try {
               byte[] buffer = new byte[1024];

               FileOutputStream fos = new FileOutputStream(zipFile);
               ZipOutputStream zos = new ZipOutputStream(fos);
               FileInputStream fis = new FileInputStream(file);

               zos.putNextEntry(new ZipEntry("page1" + ".png"));

               int length;

               while ((length = fis.read(buffer)) > 0) {
                   zos.write(buffer, 0, length);
               }

               zos.closeEntry();
               fis.close();
               zos.close();

           }
           catch (IOException ioe) {
               System.out.println("Error creating zip file" + ioe);
           }

           Log.d("URL", getString(R.string.api_base) + new HiveHelper().getUniqueId() + getString(R.string.api_push_drawing) + "/" + drawingId);

           String fileName = Environment.getExternalStorageDirectory()
                   + "/HIVE/Zipped_Drawings/page1.png";

           int serverResponseCode = 0;

           HttpURLConnection conn = null;
           DataOutputStream dos = null;
           String lineEnd = "\r\n";
           String twoHyphens = "--";
           String boundary = "*****";
           int bytesRead, bytesAvailable, bufferSize;
           byte[] buffer;
           int maxBufferSize = 1 * 1024 * 1024;

               try {
                   FileInputStream fileInputStream = new FileInputStream(
                           file);
                   URL url = new URL(getString(R.string.api_base) + new HiveHelper().getUniqueId() + getString(R.string.api_push_drawing) + "/" + drawingId);
                   // Open a HTTP connection to the URL
                   conn = (HttpURLConnection) url.openConnection();
                   conn.setDoInput(true); // Allow Inputs
                   conn.setDoOutput(true); // Allow Outputs
                   conn.setUseCaches(false); // Don't use a Cached Copy
                   conn.setRequestMethod("POST");
                   conn.setRequestProperty("Connection", "Keep-Alive");
                   conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                   conn.setRequestProperty("Content-Type",
                           "multipart/form-data;boundary=" + boundary);
                   conn.setRequestProperty("file", fileName);

                   dos = new DataOutputStream(conn.getOutputStream());
                   dos.writeBytes(twoHyphens + boundary + lineEnd);
                   dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                           + fileName + "\"" + lineEnd);

                   dos.writeBytes(lineEnd);

                   bytesAvailable = fileInputStream.available();

                   bufferSize = Math.min(bytesAvailable, maxBufferSize);
                   buffer = new byte[bufferSize];
                   bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                   while (bytesRead > 0) {

                       dos.write(buffer, 0, bufferSize);
                       bytesAvailable = fileInputStream.available();
                       bufferSize = Math.min(bytesAvailable, maxBufferSize);
                       bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                   }

                   dos.writeBytes(lineEnd);
                   dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                   serverResponseCode = conn.getResponseCode();
                   String serverResponseMessage = conn.getResponseMessage();

                   Log.i("uploadFile", "HTTP Response is : "
                           + serverResponseMessage + ": " + serverResponseCode);
                   Log.i("response", serverResponseMessage);

                   if (serverResponseCode == 200) {

                       runOnUiThread(new Runnable() {
                           public void run() {
                               Log.d("File uploading status:", "Completed");
                           }
                       });
                   }
                   fileInputStream.close();
                   dos.flush();
                   dos.close();

               } catch (MalformedURLException ex) {
                   ex.printStackTrace();
                   Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
               } catch (Exception e) {
                   Log.e("Upload file to server Exception",
                           "Exception : " + e.getMessage(), e);
               }

           return null;
       }

       @Override
       protected void onPostExecute(Void aVoid) {
           super.onPostExecute(aVoid);
           Log.d("TAG", "finished");
           Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
       }
   }

    private class EditTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String response = HttpRequest.get(getString(R.string.api_base) + new HiveHelper().getUniqueId() + getString(R.string.api_edit_drawing)).send("item=" + drawingId + "&name=" + strings[0]).body();
            Log.d("RESPONSE", response);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i("UPDATE", "Done");
        }
    }
}
