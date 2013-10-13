package com.test.filescan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FileScanActivity extends Activity {
	/** Called when the activity is first created. */
	private String tag = "FileScanActivity";
	/*
	 * private List<String> picList; private List<String> videoList;
	 */

	private TextView tv;
	private ProgressBar pb;
	private CopyFilesTask copyFilesTask;

	private String fromDir;
	private String toDir;
	private FileUtil fileUtil;
	private long remainCopySize;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		tv = (TextView) findViewById(R.id.textView);
		pb = (ProgressBar) findViewById(R.id.progressBar);
		copyFilesTask = (CopyFilesTask) new CopyFilesTask().execute();

	}

	class CopyFilesTask extends AsyncTask<Integer, Integer, String> {
		// ����������ڷֱ��ǲ��������������߳���Ϣʱ�䣩������(publishProgress�õ�)������ֵ ����

		@Override
		protected void onPreExecute() {
			// ��һ��ִ�з���
			fromDir = "/sdcard/movie/";
			toDir = "/sdcard/cl_backup/";
			fileUtil = new FileUtil();
			super.onPreExecute();
		}

		@Override  
	        protected String doInBackground(Integer... params) {  
	            //�ڶ���ִ�з���,onPreExecute()ִ�����ִ��  

	    	
	    		File f = new File(fromDir);
	    		remainCopySize = fileUtil.getDirSize(f);
	    		fileUtil.setAlreadyCopySize(0);
	    		
	    		 new Thread() {

	    	           @Override

	    	           public void run() {
	    	        		boolean b = fileUtil.copyDirectory(fromDir,
	    		    				toDir);
	    	        
	    	           }

	    	       }.start();
	    		
	    		   Log.i(tag,"remainCopySize " + String.valueOf(remainCopySize));
	        	   Log.i(tag,"current copy size " + String.valueOf(fileUtil.getAlreadyCopySize()));
	        	   while(fileUtil.getAlreadyCopySize()<remainCopySize)
	        	   {
    	        	   Log.i(tag,"remainCopySize " + String.valueOf(remainCopySize));
    	        	   Log.i(tag,"current copy size " + String.valueOf(fileUtil.getAlreadyCopySize()));
    	        		publishProgress((int)(fileUtil.getAlreadyCopySize()*100/remainCopySize));
    	        
	        	   }
	        	   publishProgress(100);
	    		
	    		// boolean b = fileUtil.copyFile("/sdcard/cl_backup1/1/1.bmp",
	    		// "/sdcard/2.bmp");
	    		/*
	    		if (b == true) {
	    			Toast.makeText(this, "�ļ������ɹ�������", 20000).show();
	    		} else {
	    			Toast.makeText(this, "�ļ�����ʧ�ܣ�����", 20000).show();
	    		}
	    		*/
	    
	    		
	    		  
	   
	            return "ִ�����";  
	        }
		@Override
		protected void onProgressUpdate(Integer... progress) {
			// ���������doInBackground����publishProgressʱ��������Ȼ����ʱֻ��һ������
			// ��������ȡ������һ������,����Ҫ��progesss[0]��ȡֵ
			// ��n����������progress[n]��ȡֵ
		//	tv.setText(progress[0] + "%");
			if(fileUtil.getfromFilePath() !=null && fileUtil.getToFilePath() !=null)
			{
				tv.setText("�� "+ fileUtil.getfromFilePath()+"    ��     "+fileUtil.getToFilePath());
			}
			pb.setProgress(progress[0]);
			super.onProgressUpdate(progress);
		}

		@Override
		protected void onPostExecute(String result) {
			// doInBackground����ʱ���������仰˵������doInBackgroundִ����󴥷�
			// �����result��������doInBackgroundִ�к�ķ���ֵ������������"ִ�����"
			setTitle(result);
			super.onPostExecute(result);
		}

	}

	public void scanPicVideo() {
		/*
		 * FileUtil fileUtil = new FileUtil(); picList = new
		 * ArrayList<String>(); videoList = new ArrayList<String>(); boolean b =
		 * false; //����ⲿ�洢��·�� String mountPath =
		 * fileUtil.getExternalStoragePath();
		 * 
		 * if (mountPath == null) { Log.i(tag, "mountPath is null"); } else {
		 * Log.i(tag, "mountPath is " + String.valueOf(mountPath)); //��ʼɨ��
		 * ͼƬ��Դ����Ƶ��Դ ɨ�� /mnt/sdcard/external_sd/ b =
		 * fileUtil.scanPicVideoInTF(mountPath, picList, videoList); } //��ӡɨ����
		 * �����ã� if (b) { for (int i = 0; i < picList.size(); i++) { Log.i(tag,
		 * "picList " + picList.get(i)); } for (int i = 0; i < videoList.size();
		 * i++) { Log.i(tag, "videoList " + videoList.get(i)); } }
		 */
	}
}