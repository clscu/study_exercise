package com.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class ImageScaleActivity extends Activity {
	/** Called when the activity is first created. */

	public static enum ResizeFlag {
		ONLYWIDTH, ONLYHEIGHT, CONSTRAINT
	}

	private ImageView srcBmpView;
	private ImageView desBmpView;
	private Button resizeBtn;
	private Button saveBtn;
	private Context context;
	private Bitmap srcBmp;
	private Bitmap desBmp;
	private Button loadBtn;
	static public String srcPath = "/sdcard/pic.png";
	static public String desPath = "/sdcard/pic.png";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		context = this;
		srcBmpView = (ImageView) findViewById(R.id.src_image_view);
		desBmpView = (ImageView) findViewById(R.id.des_image_view);
	
		saveBtn = (Button) findViewById(R.id.save_button);
		resizeBtn = (Button) findViewById(R.id.resize_button);
		loadBtn = (Button) findViewById(R.id.load_button);

		resizeBtn.setOnClickListener(new ResizeBtnOnClickListener());
		loadBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				srcBmp = loadBitmapFile(srcPath);
				if(srcBmp ==null)
				{
					return;
				}

				/* ��ȡ��ƭ���ͼƬ���д������ͼƬ�����Ĭ���Ƚ������ŷ��㲼�� */
				if (srcBmp.getHeight() > 300 || srcBmp.getWidth() > 200) {
					int height = (srcBmp.getHeight() > 150) ? 150 : srcBmp
							.getHeight();
					int width = (srcBmp.getWidth() > 150) ? 150 : srcBmp
							.getWidth();
					if (srcBmp.getWidth() > srcBmp.getHeight()) {
						srcBmp = resizeImage(srcBmp, (float) width,
								ResizeFlag.ONLYWIDTH);
					} else {
						srcBmp = resizeImage(srcBmp, (float) height,
								ResizeFlag.ONLYWIDTH);
					}
				}
				srcBmpView.setImageBitmap(srcBmp);
				srcBmpView.setAdjustViewBounds(true);
			}
		});

		saveBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (null != desBmp) {
					writeBitmapFile(desBmp, desPath);
					Log.i("save", "sucsessful");
				}
			}

		});

	}

	/* ����ͼƬ��ָ���ߴ� �ǵȱ����� */
	public Bitmap resizeImage(Bitmap bmp, int sx, int sy) {
		if (null == bmp) {
			return null;
		}
		if (sx <= 0 || sy <= 0) {
			return null;
		}
		
		Matrix matrix = new Matrix();
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		matrix.postScale((float) sx / width, (float) sy / height);

		// �������ź��Bitmap����
		Bitmap resizeBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height,
				matrix, true);
		return resizeBitmap;
	}

	/*
	 * ָ��һ������������ ��flag��־�������ͣ��ɽ�ָ����ȣ���ָ���߶Ȼ��߽�ָ�����ű������еȱ������� �� flag
	 * ΪCONSTRAINT��scaleParaΪ�Ŵ��� ��flag ΪONLYWIDTH��scaleParaΪ��Ҫ���ŵ��Ŀ�ȳߴ�
	 * ��flagΪONLYHEIGHT��scaleParaΪ��Ҫ���ŵ��ĸ߶ȳߴ�
	 */
	public Bitmap resizeImage(Bitmap bmp, float scalePara, ResizeFlag flag) {

		if (null == bmp) {
			return null;
		}

		if (scalePara <= 0) {
			return null;
		}

		Matrix matrix = new Matrix();

		int width = bmp.getWidth();
		int height = bmp.getHeight();
		float sx = 0f;
		float sy = 0f;
		switch (flag) {
		case CONSTRAINT:
			sx = scalePara;
			sy = scalePara;

			break;
		case ONLYWIDTH:
			sx = scalePara / width;
			sy = sx;
			break;
		case ONLYHEIGHT:
			sx = scalePara / height;
			sy = sx;
			break;
		}

		matrix.postScale(sx, sy);
		// �������ź��Bitmap����
		Bitmap resizeBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height,
				matrix, false);
		return resizeBitmap;
	}

	public void writeBitmapFile(Bitmap bmp, String desPath) {
		File file = new File(desPath);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bmp.compress(Bitmap.CompressFormat.PNG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* �����ļ�·��  ����null��ʱ���ʾ�ļ������ڻ��߲���ͼƬ�ļ� */
	public Bitmap loadBitmapFile(String src) {
		Bitmap bmp = BitmapFactory.decodeFile(src);
		if(bmp !=null)
		{
			return bmp;
		}
		return null;
	}

	class ResizeBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (null != srcBmp) {
				desBmp = resizeImage(srcBmp, 2f, ResizeFlag.CONSTRAINT);
				if (desBmp != null) {
					desBmpView.setImageBitmap(desBmp);
					desBmpView.setAdjustViewBounds(true);
				}
			}
		}
	}

}