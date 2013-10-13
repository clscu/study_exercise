package com.test.menu;



import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

public class MenuActivity extends Activity {
    /** Called when the activity is first created. */
	
	private PopupWindow popupWindow ;
	private ImageView iv;


	private Button button;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        button = (Button)findViewById(R.id.button1);
        iv = (ImageView)findViewById(R.id.imageView);
        
     
        iv.setOnTouchListener(new MulitPointTouchListener());
        
       
/*
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				
				
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
			
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
				
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
				
				}
				// return super.onTouchEvent(event);
				return true;
				*/
				
				
				
	
        
        button.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popAwindow(v);
			}
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	this.getMenuInflater().inflate(R.menu.optionmenu, menu);
    	return true;
    }
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	Log.i("itemid",String.valueOf(item.getItemId()));
		switch (item.getItemId()) 
		{
		
		case R.id.item01:
			Toast.makeText(this, "��Ӳ˵��������", Toast.LENGTH_LONG).show();
			break;
		case R.id.item01 + 1:
			Toast.makeText(this, "�༭�˵��������", Toast.LENGTH_LONG).show();
			break;
		case R.id.item01 + 2:
			Toast.makeText(this, "��ϸ�˵��������", Toast.LENGTH_LONG).show();
			break;
		case R.id.item01 + 3:
			Toast.makeText(this, "ɾ���˵��������", Toast.LENGTH_LONG).show();
			break;
		case R.id.item01 + 4:
			Toast.makeText(this, "�����˵��������", Toast.LENGTH_LONG).show();
			break;
		} 
		return false;
	}
    
    private   void  popAwindow(View parent) {   
        if  (popupWindow ==  null ) {   
            LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);   
            View v = lay.inflate(R.layout.popupwindow, null );   
            v.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corners_pop));   
            popupWindow = new  PopupWindow(v,  500 , 260 );   
        }   
           
        //��������popupwindow����ʽ��    
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corners_pop));   
       
        //ʹ��������Ŀռ���ʾ����Ӧ��Ч�����Ƚϵ��buttonʱ������ɫ�ı䡣    
        //���Ϊfalse�����صĿռ������û�з�Ӧ�����¼��ǿ��Լ������ġ�    
        popupWindow.setFocusable(true );   
        popupWindow.update();   
        popupWindow.showAtLocation(parent, Gravity.BOTTOM|Gravity.RIGHT, 100 ,  0 );   
    }   
   
   
    public class MulitPointTouchListener implements OnTouchListener { 
        Matrix matrix = new Matrix(); 
        Matrix savedMatrix = new Matrix(); 

        static final int NONE = 0; 
        static final int DRAG = 1; 
        static final int ZOOM = 2; 
        int mode = NONE; 

        PointF start = new PointF(); 
        PointF mid = new PointF(); 
        float oldDist = 1f; 

        @Override 
        public boolean onTouch(View v, MotionEvent event) { 

                ImageView view = (ImageView) v; 
                Log.i("Touch","Touch in");
                switch (event.getAction() & MotionEvent.ACTION_MASK) { 
                case MotionEvent.ACTION_DOWN: 

                        matrix.set(view.getImageMatrix()); 
                        savedMatrix.set(matrix); 
                        start.set(event.getX(), event.getY()); 
                        mode = DRAG; 

                       
                        break; 
                case MotionEvent.ACTION_POINTER_DOWN: 
                        oldDist = spacing(event); 
                        if (oldDist > 10f) { 
                                savedMatrix.set(matrix); 
                                midPoint(mid, event); 
                                mode = ZOOM; 
                        } 
                        break; 
                case MotionEvent.ACTION_UP: 
                case MotionEvent.ACTION_POINTER_UP: 
                        mode = NONE; 

                        break; 
                case MotionEvent.ACTION_MOVE: 
                    Log.i("Touch","Touch Move");
                        if (mode == DRAG) { 
                                matrix.set(savedMatrix); 
                                matrix.postTranslate(event.getX() - start.x, event.getY() 
                                                - start.y); 
                        } else if (mode == ZOOM) { 
                                float newDist = spacing(event); 
                                if (newDist > 10f) { 
                                        matrix.set(savedMatrix); 
                                        float scale = newDist / oldDist; 
                                        matrix.postScale(scale, scale, mid.x, mid.y); 
                                } 
                        } 
                        break; 
                } 

                view.setImageMatrix(matrix); 
                return true; 
        } 

        
       
        private float spacing(MotionEvent event) { 
                float x = event.getX(0) - event.getX(1); 
                float y = event.getY(0) - event.getY(1); 
                return FloatMath.sqrt(x * x + y * y); 
        } 

       
        private void midPoint(PointF point, MotionEvent event) { 
                float x = event.getX(0) + event.getX(1); 
                float y = event.getY(0) + event.getY(1); 
                point.set(x / 2, y / 2); 
        } 
} 
    

}