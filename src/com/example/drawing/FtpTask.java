package com.example.drawing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class FtpTask extends AsyncTask<Void, Void, Void> {

	@Override
	protected Void doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		FTPClient mFTP = new FTPClient();
        try {
            // Connect to FTP Server
            mFTP.connect("bluedream.info");
            mFTP.login("hive@bluedream.info", "ihsandzanan2013");
            mFTP.setFileType(FTP.BINARY_FILE_TYPE);
            mFTP.enterLocalPassiveMode();
            
            // Prepare file to be uploaded to FTP Server
            String Id = null;
            File lokacijaId = new File(Environment.getExternalStorageDirectory()+"/HIVE/NFC");
            File[] lista = lokacijaId.listFiles();
            for(File infile: lista){
                    if(infile.isDirectory()){
                            Id=infile.getName();
                            break;
                    }
            }
            File file = new File(Environment.getExternalStorageDirectory()+"/HIVE/Drawings/"+MainActivity.value+".png");
            Log.d("Snimamo sa:", Environment.getExternalStorageDirectory()+"/HIVE/Drawings/"+MainActivity.value+".png");
            FileInputStream ifile = new FileInputStream(file);
            
            // Upload file to FTP Server
            mFTP.storeFile("/student/"+Id+"/Drawings/"+MainActivity.value+".png",ifile);
            Log.d("Snimamo na:", "/student/"+Id+"SCI00035/Drawings/"+MainActivity.value+".png");
            mFTP.disconnect();          
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return null;
	}
}