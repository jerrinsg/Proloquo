package com.work;

import java.io.File;
import java.io.IOException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ImageAdapter extends BaseAdapter {
	
	private Context mContext;
	String path;
	String info[];
	String[] texttoshow ;
	File dir;
	String fileNames[];
	int flag;
	public ImageAdapter(Context c,String Path,int Flag) throws IOException {
		// TODO Auto-generated constructor stub
		mContext = c;
		path = Path;
		flag = Flag;
		FileProvider obj = new FileProvider();
		dir = obj.directory(path);
		fileNames = obj.files(path);
		info = obj.readLines(dir.getPath() + "/info.txt");
		texttoshow = obj.textToShow(path);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fileNames.length;
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return fileNames[position];	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v;
		/*
		 if(convertView==null) {
		 STILL CAUSING PROBLEMS :X RECYCLING EVERYTIME
	         LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	         if(flag==0) {
	        	 v = inflater.inflate(R.layout.grid_image, parent, false);
	         }
	         else {
	        	 v = inflater.inflate(R.layout.dialog_image, parent, false);
	         }
	     }
	     else {
	        v = convertView;
	     }
		 */
		 LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         if(flag==0) {
        	 v = inflater.inflate(R.layout.grid_image, parent, false);
         }
         else {
        	 v = inflater.inflate(R.layout.dialog_image, parent, false);
         }
		
		 ImageView iv = new ImageView(mContext);
		 ImageView marker = new ImageView(mContext);
		 marker = (ImageView)v.findViewById(R.id.marker_image);
		 TextView tv ;
		 if(flag==0) {
			 iv = (ImageView)v.findViewById(R.id.icon_image);
		 }
		 else {
			 iv = (ImageView)v.findViewById(R.id.icon_dialog);
			 
		 }
	     
	     try {
	    	 if(flag==0) {
	    	 if(info[position].equalsIgnoreCase("sub")) {
	    		 marker.setImageResource(R.drawable.img_sub);
	    	 }
	 	     else if(info[position].equalsIgnoreCase("touch")) {
	 	    	 marker.setImageResource(R.drawable.img_touch);
	 	     }
	    	  
	    	 }
	    	 if(flag==0)
	    		 tv = (TextView)v.findViewById(R.id.icon_text);
	    	 else {
	    		 tv = (TextView)v.findViewById(R.id.text_dialog);
	    	 }
	    	 
	    	 if(texttoshow[position]==null)
	    		 tv.setText("NoTextFound");
	    	 else
	    		 tv.setText(texttoshow[position]);
	    	 
	    	 Bitmap bmp =(Bitmap)BitmapFactory.decodeFile(dir.getPath() + "/" + getItem(position));
	    	 Matrix matrix = new Matrix();
		     int width = bmp.getWidth();
		     int height = bmp.getHeight();
		     
		     float w,h;
		     
		     w=(float)50/width;
		     h=(float)50/height;
		     matrix.postScale(w, h);
		     Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0,
		     width, height, matrix, true);
		     iv.setImageBitmap(resizedBitmap);
	     }
	     
	     catch(NullPointerException e) {
	    	 iv.setImageResource(R.drawable.no_image);
    		 
	    		Toast toast = Toast.makeText(mContext, "Image for position " + (position+1) + " not found", Toast.LENGTH_LONG);
				toast.show();	
				}
	     
	     catch(ArrayIndexOutOfBoundsException e) {
	    	 Toast toast = Toast.makeText(mContext, "Information not available for " +  (position+1)+ ".png (sub/touch/leaf) ..check info.txt", Toast.LENGTH_LONG);
				toast.show();
	     }
		 return v;
	}

}