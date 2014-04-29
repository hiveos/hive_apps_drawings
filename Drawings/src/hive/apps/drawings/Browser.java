package hive.apps.drawings;

import hive.apps.drawings.helpers.HiveHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Browser extends Activity implements OnRefreshListener {

	public static Bitmap LoadaniCrtez;
	private ImageAdapter imageAdapter;
	ArrayList<String> f = new ArrayList<String>();
	ArrayList<String> f2 = new ArrayList<String>();
	File[] listFile;
	File[] listFile2;
	ArrayList<String> oldFileNames = new ArrayList<String>();
	ArrayList<String> newFileNames = new ArrayList<String>();
	ArrayList<String> fileNames = new ArrayList<String>();
	ArrayList<String> fileNamesWithExtentions = new ArrayList<String>();

	View selectedItem;

	private int ItemId;
	protected Object mActionMode;

	String[] mDrawings;
	ArrayList<String> mDrawingIds = new ArrayList<String>();
	ArrayList<String> mDrawingNames = new ArrayList<String>();

	GridView imagegrid;

	int firstTime = 1;

	private PullToRefreshLayout mPullToRefreshLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);

		getWindow().setWindowAnimations(0);

		getFromSdcard();

		imagegrid = (GridView) findViewById(R.id.gridview);
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

                Bitmap drawingToLoad = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/HIVE/Drawings/" + mDrawingIds.get(position) + ".png");

                if (drawingToLoad != null) {
                    LoadaniCrtez = drawingToLoad;
                } else {
                    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                    LoadaniCrtez = Bitmap.createBitmap(10, 10, conf);
                    LoadaniCrtez.recycle();
                }

				Intent i = new Intent(getApplicationContext(),
						MainActivity.class);
				i.putExtra("Drawing Name", mDrawingNames.get(position));
                i.putExtra("Drawing Id", mDrawingIds.get(position));
				startActivity(i);
			}
		});
		imagegrid.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int position, long arg3) {
				LaunchContextualActionBar(v);
				ItemId = position;
				return true;
			}

		});

		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);

		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);

		new FetchTask().execute("noreload");
		mPullToRefreshLayout.setRefreshing(true);

		getActionBar().setIcon(null);
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setTitle(
				getResources().getString(R.string.app_name).toUpperCase());

		firstTime = 0;

	}

	@Override
	protected void onResume() {
		super.onResume();
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
            showAddDialog();
			return true;
		default:
			return false;
		}
	}

    public void showAddDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Drawing Name");
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                new AddTask().execute(input.getText().toString());
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
        alert.show();
    }

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.editdrawing, menu);
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.editdrawing, menu);
			selectedItem.setSelected(true);
			return true;
		}

		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

			return false;
		}

		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.action_deletedrawing: {

				deleteDrawing(ItemId);
				mode.finish();
			}
				return true;

			default:
				return false;
			}
		}

		public void onDestroyActionMode(ActionMode mode) {
			selectedItem.setSelected(false);
			mActionMode = null;
		}
	};

	public void showToast(String message) {

		Toast toast = Toast.makeText(getApplicationContext(), message,
				Toast.LENGTH_SHORT);

		toast.show();

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
				fileNamesWithExtentions.add(infile.getName().toString());
			}
		}
	}

	public void compareNames() {

		File file = new File(
				android.os.Environment.getExternalStorageDirectory(),
				"/HIVE/Drawings");

		oldFileNames = fileNames;

		if (file.isDirectory()) {
			listFile2 = file.listFiles();

			for (File infile : listFile2) {
				f2.add(infile.getAbsolutePath());
				newFileNames.add(infile.getName().toString().split("\\.")[0]);
			}
		}

	}

	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ImageAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return mDrawingIds.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
            File fileToLoad = new File(Environment.getExternalStorageDirectory(),
                    "/HIVE/Drawings/" + mDrawingIds.get(position) + ".png");

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

			Bitmap myBitmap = BitmapFactory.decodeFile(fileToLoad.getAbsolutePath());
			holder.imageview.setImageBitmap(myBitmap);
			holder.textview.setText(mDrawingNames.get(position));
			return convertView;
		}
	}

	class ViewHolder {
		ImageView imageview;
		TextView textview;

	}

	public void LaunchContextualActionBar(View v) {
		selectedItem = v;
		mActionMode = this.startActionMode(mActionModeCallback);
	}

	public void deleteDrawing(int position) {
		new DeleteTask().execute(mDrawingIds.get(position));
        reload();
	}

	public void reload() {
		Intent reload = new Intent(this, Browser.class);
		reload.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		reload.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();
		startActivity(reload);
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	private class FetchTask extends AsyncTask<String, Integer, String> {

		String response;
		boolean isEmpty = true;

		ArrayList<String> sDrawingIds = new ArrayList<String>();

		@Override
		protected String doInBackground(String... params) {
			HiveHelper mHiveHelper = new HiveHelper();
			String url = getResources().getString(R.string.api_base)
					+ mHiveHelper.getUniqueId()
					+ getResources().getString(R.string.api_list_drawing);

			if (isNetworkAvailable()) {
				try {
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet(url);
					HttpResponse responseGet;
					responseGet = client.execute(get);
					HttpEntity resEntityGet = responseGet.getEntity();

					if (resEntityGet != null) {
						response = EntityUtils.toString(resEntityGet);
					} else {

					}

					extractData();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Intent mNoNetworkIntent = new Intent();
				mNoNetworkIntent.setAction("hive.action.General");
				mNoNetworkIntent.putExtra("do", "ERROR_NO_CONNECTION");
				sendBroadcast(mNoNetworkIntent);
				finish();
			}

			return params[0];
		}

		private void extractData() {
			clearUp();

			if (!response.equals("")) {
				mDrawings = response.split(";");

				for (int i = 0; i < mDrawings.length; i++) {

					mDrawingIds.add(mDrawings[i].substring(
							mDrawings[i].indexOf("id=") + 3,
							mDrawings[i].indexOf(",name")));
					mDrawingNames.add(mDrawings[i].substring(mDrawings[i]
							.indexOf("name=") + 5));
					isEmpty = false;
				}
			}
		}

		private void clearUp() {
			Browser.this.runOnUiThread(new Runnable() {
				public void run() {
					// LinearLayout NoNotebook = (LinearLayout)
					// findViewById(R.id.no_notebook);
					// NoNotebook.setVisibility(View.GONE);
				}
			});
			clearArrays();
		}

		private void clearArrays() {
			mDrawingNames.clear();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (isNetworkAvailable()) {
				new DownloadTask().execute(result);
			}
		}

		private void addDrawings() {

		}

		private void displayNoDrawings() {
			Browser.this.runOnUiThread(new Runnable() {
				public void run() {
					// LinearLayout NoNotebook = (LinearLayout)
					// findViewById(R.id.no_notebook);
					// NoNotebook.setVisibility(View.VISIBLE);
				}
			});
		}

	}

	private class DownloadTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			HiveHelper mHiveHelper = new HiveHelper();
			String url = getResources().getString(R.string.api_base)
					+ mHiveHelper.getUniqueId()
					+ getResources().getString(R.string.api_output_drawing);

			File file = null;

			for (int i = 0; i <= mDrawings.length; i++) {

				try {
					HttpRequest request = HttpRequest.post(url).send(
							"item=" + mDrawingIds.get(i) + "&page=1");

					if (request.ok()) {
						file = new File(
								Environment.getExternalStorageDirectory()
										+ "/HIVE/Drawings/"
										+ mDrawingIds.get(i) + ".png");
						request.receive(file);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return params[0];
		}

		@Override
		protected void onPostExecute(final String reload) {
			Browser.this.runOnUiThread(new Runnable() {
				public void run() {
					GridView mGridView = (GridView) findViewById(R.id.gridview);
					mGridView.setVisibility(View.VISIBLE);
					if (reload.equals("reload")) {
						reload();
					}
					mPullToRefreshLayout.setRefreshComplete();
				}
			});
		}

	}

	@Override
	public void onRefreshStarted(View view) {
		new FetchTask().execute("reload");
	}

    private class AddTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String response = HttpRequest.get(getString(R.string.api_base) + new HiveHelper().getUniqueId() + getString(R.string.api_add_drawing)).send("name=" + strings[0]).body();
            Log.d("RESPONSE", response);

            return null;
        }
    }

    private class DeleteTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String response = HttpRequest.get(getString(R.string.api_base) + new HiveHelper().getUniqueId() + getString(R.string.api_delete_drawing)).send("item=" + strings[0]).body();
            Log.d("RESPONSE", response);

            return null;
        }
    }
}