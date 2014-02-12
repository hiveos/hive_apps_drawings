package hive.apps.drawings.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Environment;

public class HiveHelper {

	ArrayList<String> mUserInformation = new ArrayList<String>();

	public void readUserInformation() {
		File mUserInfoFile = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/User/information");

		mUserInformation.clear();

		try {
			BufferedReader mBufferReader = new BufferedReader(new FileReader(
					mUserInfoFile));
			String line;

			while ((line = mBufferReader.readLine()) != null) {
				mUserInformation.add(line);
			}
		} catch (IOException e) {
		}
	}

	public String getUniqueId() {
		readUserInformation();
		String uniqueId = mUserInformation.get(4).substring(
				mUserInformation.get(4).indexOf("=") + 1);
		return uniqueId;
	}

}
