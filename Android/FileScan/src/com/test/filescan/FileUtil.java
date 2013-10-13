package com.test.filescan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

public class FileUtil {
	private String tag = "FileUtil";
	static boolean mExternalStorageAvailable = false;
	static boolean mExternalStorageWriteable = false;

	static enum MediaType {
		PIC, VEDIO, AUDIO, PLAYLST
	};

	private long alreadyCopySize;
	private String fromFilePath;
	private String toFilePath;

	FileUtil() {
		alreadyCopySize = 0;
	}

	/* *********************************************************
	 * �ж��ⲿ�洢�豸��״̬ ��������� mExternalStorageAvailable mExternalStorageWriteable
	 * *********************************************************
	 */
	public void updateExternalStorageState() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
	}

	/* *********************************************************
	 * ɨ��dirPath·���µ���Ƶ�ļ���ͼƬ�ļ� ɨ����������picList,videoList return �ҵ��ļ�����true
	 * *********************************************************
	 */
	public boolean scanPicVideo(String dirPath, List<String> picList,
			List<String> videoList) {
		if (picList == null || videoList == null) {
			Log.i(tag, "picList or videoList not init.");
			return false;
		}
		File f = new File(dirPath);// �����ļ�·��

		if (f.exists() && f.isDirectory()) {// �ж����ļ�����Ŀ¼
			if (f.listFiles().length == 0) {
				Log.i(tag, "no file in this directory");
				return false;
			}
			if (!dirPath.endsWith("/")) {
				dirPath += "/";
			}

			MediaFile mediaFile = new MediaFile();
			String fileArray[] = f.list();
			String absolutePath;
			for (int i = 0; i < fileArray.length; i++) {
				absolutePath = dirPath + fileArray[i];
				Log.i(tag, "file name in " + dirPath + "  " + fileArray[i]);
				if (mediaFile.getFileType(fileArray[i]) != null) {
					if (mediaFile.isVideoFileType(mediaFile
							.getFileType(fileArray[i]).fileType)) {
						videoList.add(absolutePath);
					} else if (mediaFile.isImageFileType(mediaFile
							.getFileType(fileArray[i]).fileType)) {
						picList.add(absolutePath);
					}
				}
			}
		}
		return true;
	}

	/* *********************************************************
	 * �õ��ⲿ�洢�豸�Ĺ���·�� return String ���ؾ���·��
	 * *********************************************************
	 */
	public String getExternalStoragePath() {
		String path = null;
		updateExternalStorageState();
		if (mExternalStorageAvailable && mExternalStorageWriteable) {
			path = Environment.getExternalStorageDirectory().getAbsolutePath();
			Log.i(tag, "external directory " + path);
		}
		return path;
	}

	public long getDirSize(File dir) {
		long result = 0;
		File[] fileList = dir.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isDirectory()) {
				result += getDirSize(fileList[i]);
			} else {
				// Sum the file size in bytes
				result += fileList[i].length();
			}
		}
		return result;
	}

	public long getImagesSize(File dir) {
		MediaFile mediaFile = new MediaFile();
		long result = 0;
		File[] fileList = dir.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			if(fileList[i] !=null && fileList[i].isFile())
			{
				Log.i("cl absolutePath",fileList[i].toString());
				if (mediaFile.isImageFileType(mediaFile.getFileType(fileList[i]
						.getAbsolutePath()).fileType)) {
					result += fileList[i].length();
				}
			}
		}
		return result;
	}

	public boolean copyFile(String fromFile, String toFile) {
		try {
			fromFilePath = fromFile;
			toFilePath = toFile;
			InputStream fosfrom = new FileInputStream(fromFile);
			OutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
				alreadyCopySize += c;
			}
			fosfrom.close();
			fosto.close();
			return true;

		} catch (Exception ex) {
			return false;
		}
	}

	public boolean copyDirectory(String fromDir, String toDir) {
		// Ҫ���Ƶ��ļ�Ŀ¼
		File[] currentFiles;
		File root = new File(fromDir);

		// ��ͬ�ж�SD���Ƿ���ڻ����ļ��Ƿ����
		// ����������� return��ȥ
		if (!root.exists()) {
			return false;
		}
		// ����������ȡ��ǰĿ¼�µ�ȫ���ļ� �������
		currentFiles = root.listFiles();

		// ����Ҫ�����Ĵ�С

		// Ŀ��Ŀ¼
		File targetDir = new File(toDir);
		// ����Ŀ¼
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		// ����Ҫ���Ƹ�Ŀ¼�µ�ȫ���ļ�
		for (int i = 0; i < currentFiles.length; i++) {
			Log.i(tag, "File name " + currentFiles[i].getName());
			if (currentFiles[i].isDirectory())// �����ǰ��Ϊ��Ŀ¼ ���еݹ�
			{
				copyDirectory(currentFiles[i].getPath() + "/", toDir
						+ currentFiles[i].getName() + "/");

			} else// �����ǰ��Ϊ�ļ�������ļ�����
			{
				Log.i(tag, "Current File Path " + currentFiles[i].getPath());
				boolean b = copyFile(currentFiles[i].getPath(), toDir
						+ currentFiles[i].getName());
				Log.i(tag,
						"Current toFile Path " + toDir
								+ currentFiles[i].getName());
				Log.i(tag, "Current toFile PathBool " + String.valueOf(b));
			}
		}
		return true;
	}

	public boolean importImage(String fromDir, String toDir) {
		MediaFile mediaFile = new MediaFile();

		// Ҫ���Ƶ��ļ�Ŀ¼
		File[] currentFiles;
		File root = new File(fromDir);

		// ��ͬ�ж�SD���Ƿ���ڻ����ļ��Ƿ����
		// ����������� return��ȥ
		if (!root.exists()) {
			return false;
		}
		// ����������ȡ��ǰĿ¼�µ�ȫ���ļ� �������
		currentFiles = root.listFiles();

		// Ŀ��Ŀ¼
		File targetDir = new File(toDir);
		// ����Ŀ¼
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		// ����Ҫ���Ƹ�Ŀ¼�µ�ȫ���ļ�
		for (int i = 0; i < currentFiles.length; i++) {

			if (currentFiles[i].isFile() && mediaFile.isImageFileType(mediaFile.getFileType(currentFiles[i]
					.getAbsolutePath()).fileType)) {
				boolean b = copyFile(currentFiles[i].getPath(), toDir
						+ currentFiles[i].getName());
			}

		}
		return true;
	}

	public String getfromFilePath() {
		return fromFilePath;
	}

	public void setfromFilePath(String fromFilePath) {
		this.fromFilePath = fromFilePath;
	}

	public long getAlreadyCopySize() {
		return alreadyCopySize;
	}

	public void setAlreadyCopySize(long remainCopySize) {
		this.alreadyCopySize = remainCopySize;
	}

	public String getToFilePath() {
		return toFilePath;
	}

	public void setToFilePath(String toFilePath) {
		this.toFilePath = toFilePath;
	}

}
