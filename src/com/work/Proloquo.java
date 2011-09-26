package com.work;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class Proloquo extends Activity {

	private GridView tabGrid;
	private FileProvider obj;
	private static String path ="";
	private static int index = -1 ;

	private static ArrayList<String[]> rowList = new ArrayList<String[]>();
	private MediaPlayer mPlayer = null;
	File[] audioSet;
	int mCompleted = 0;
	boolean playing = false;
	private static boolean dialogShowing = false;
	Display display;
	ImageView drag;
	String info[] = null; 
	private LinearLayout linearInnerSroll;
	private HorizontalScrollView horizScrollicons;
	private LinearLayout linearMain;
	private TextView textLabel;
	LinearLayout linearScroll;
	LayoutInflater inflater;
	View layout;
	ImageView imageClear;
	GridView gridDialog;
	LinearLayout linearGrid;
	Dialog dialog;  
	private static String buttonPath="";
	Button buttonBack;
	Button buttonHome;

	private static boolean dialogRunning = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			obj = new FileProvider();
		}

		catch(IOException e) {
		}

		finally {
			TextView tv = new TextView(this);
			if(obj == null) {
				tv.setText("Could not load SD card");
				setContentView(tv);
			}
			else {
				display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
				initialise();
			}
			
		}
	}

	private void initialise() {
		textLabel = new TextView(this);
		drag = new ImageView(this);
		linearScroll = new LinearLayout(this);
		imageClear = new ImageView(this);
		tabGrid = new GridView(this);
		linearGrid = new LinearLayout(this);
		tabGrid.setId(123);
		linearInnerSroll = new LinearLayout(this);
		buttonBack = new Button(this);
		buttonHome = new Button(this);
		buttonBack.setMinimumWidth(45);
		buttonBack.setSoundEffectsEnabled(false);
		buttonHome.setSoundEffectsEnabled(false);
		tabGrid.setColumnWidth(65);
		tabGrid.setVerticalSpacing(1);
		tabGrid.setHorizontalSpacing(5);
		tabGrid.setNumColumns(-1);
		tabGrid.setGravity(0x11);
		tabGrid.setStretchMode(1);
		tabGrid.setSoundEffectsEnabled(false);
		View m_vForm;
		populateGrid();
		m_vForm = createUI();

		setContentView(m_vForm);
		
		setButtonControls();
		setTextControls();
		setImages();
		checkDialogIntegrity();
		new Handler().postDelayed(new Runnable() {
			public void run() {
				linearInnerSroll.setMinimumWidth(display.getWidth() -imageClear.getWidth() );
			}
		}, 850L);

	}

	private void setImages() {
		if(index==-1)
			return;
		else {
			String temp[];
			for(int i=0;i<rowList.size();i++) {

				temp = rowList.get(i);
				addThumb(temp[0],Integer.parseInt(temp[1]),i,false);

			}
			scrollView();
		}
	}

	private void populateGrid() {

		boolean exception = false;
		try {
			tabGrid.setAdapter(new ImageAdapter(this,path,0));
		} 

		catch (IOException e) {
			// TODO Auto-generated catch block
			exception=true;
		}
		finally {
			if(exception == true) {

				Toast toast = Toast.makeText(getApplicationContext(), "Could not load the grid", Toast.LENGTH_LONG);
				toast.show();
			}
			setonclicklisteners();
		}

	}

	private void speak(String path,int position) throws NullPointerException{
		File audiopath = obj.audioPath(path,position);
		Uri uri = Uri.fromFile(audiopath);

		mPlayer= MediaPlayer.create(getApplicationContext(), uri);
		mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mPlayer) {
				// TODO Auto-generated method stub
				mPlayer.release();
				mPlayer = null;
			}
		});

		mPlayer.start();
	}


	private void addThumb(String path, final int position,int ind,boolean animate) {

		File dir = obj.directory(path);// 0 to identify whether directory for tabs or dialog
		Context mContext = this; 
		View v;
		 TextView tvc = new TextView(mContext);
		LayoutInflater scrllInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 v = scrllInflater.inflate(R.layout.scroll_icon, (ViewGroup)findViewById(R.id.widget66), false);
		//v.setPadding(0, 0, 5, 0);
		 ImageView iv = new ImageView(mContext);
		 iv = (ImageView)v.findViewById(R.id.scroll_icon);
		 tvc = (TextView)v.findViewById(R.id.scroll_text);
		 int temp = position+1;
		 
		 try {
			 tvc.setText(obj.getTitle(path, temp+""));
			 if(tvc.length()>9) {
				 String text = (String) tvc.getText();
				 String trial = text.substring(0, 3) + ".." + text.substring(text.length()-4, text.length());
				 tvc.setText(trial);
				// tvc.setText(tvc.)
			 }
			}
			catch(Exception e) {
				//tv.setText("DEFAULT");
			}
		 
			Bitmap bmp =(Bitmap)BitmapFactory.decodeFile(dir.getPath() + "/" + (position+1) + ".png");
			Matrix matrix = new Matrix();
			int width = bmp.getWidth();
			int height = bmp.getHeight();

			float scaleWidth = (float)30/width;;
			float scaleHeight = (float)30/height;

			matrix.postScale(scaleWidth, scaleHeight);
			
			
			Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0,
					width, height, matrix, true); 
			
		iv.setImageBitmap(resizedBitmap);
		 
		v.setId(ind);
		v.setSoundEffectsEnabled(false);
		
		 LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		 lp.setMargins(2, 0, 3, 0);
		
		linearInnerSroll.addView(v, ind, lp);

		if(animate){
			AnimationSet set = new AnimationSet(true);
			Animation fade = new AlphaAnimation(0.00f, 1.00f);
			set.addAnimation(fade);
			set.setDuration(200);
			set.setFillAfter(true);        
			linearInnerSroll.getChildAt(ind).startAnimation(set);
		}

		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				linearListener();
			}
		});

		v.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				removeView(v.getId());
				return true;
			}
		});
	}

	private void removeView(final int ind) {

		if(playing) {
			playing = false;
			mPlayer.release();
			mPlayer = null;
		}

		AnimationSet set = new AnimationSet(true);
		Animation fade2 = new AlphaAnimation(1.00f, 0.00f);
		set.addAnimation(fade2);
		set.setDuration(200);
		set.setFillAfter(true);        

		linearInnerSroll.getChildAt(ind).startAnimation(set);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				String temp[];
				for(int i=ind;i<rowList.size();i++) {
					linearInnerSroll.removeViewAt(ind);
				}

				for(int i=ind+1;i<rowList.size();i++) {
					temp = rowList.get(i);
					addThumb(temp[0],Integer.parseInt(temp[1]),i-1,false);
				}
				rowList.remove(ind);
				index--;

				scrollView();
			}
		}, 185L);
	}

	private void scrollView() {

		new Handler().postDelayed(new Runnable() {
			public void run() {
				horizScrollicons.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
			}
		}, 55L);
	}

	private String getPosition(String buttonpath) {

		String newpath="";
		int charCount = buttonpath.replaceAll("[^/]", "").length();
		if(charCount==1) {
			int pos = buttonpath.lastIndexOf("/");
			newpath=buttonpath.substring(0, pos);

		}
		else if(charCount-- > 1) {
			int pos = buttonpath.indexOf('/', 0);
			while (--charCount > 0 && pos != -1)
				pos = buttonpath.indexOf('/', pos+1);

			newpath = buttonpath.substring(pos+1,buttonpath.length()-1);

		}
		return newpath;
	}

	private void setTextControls() {

		if(path.equalsIgnoreCase("")) {
			textLabel.setText("Home");
		}

		else {
			try {
				textLabel.setText(obj.getTitle(removeLastPath(path), getPosition(path)));
			}
			catch(Exception e) {
				textLabel.setText("DEFAULT");
				Toast toast = Toast.makeText(getApplicationContext(), "File name not found for this directory", Toast.LENGTH_LONG);
				toast.show();
			}
		}
	}

	private void setButtonControls() {

		buttonPath=removeLastPath(path);
		if(path.equalsIgnoreCase("") || buttonPath.equalsIgnoreCase("")) {
			buttonHome.setVisibility(4);
		}

		else 
			buttonHome.setVisibility(0);

		if(path.equalsIgnoreCase("")) 
			buttonBack.setVisibility(4);

		else {
			buttonPath = removeLastPath(path);

			if(buttonPath.equalsIgnoreCase("")) 
				buttonBack.setText("Home");

			else {

				try {
					String title =  obj.getTitle(removeLastPath(buttonPath), getPosition(buttonPath));
					buttonBack.setText(title);
				}
				catch(Exception e) {
					buttonBack.setText("DEFAULT");
					Toast toast = Toast.makeText(getApplicationContext(), "File name not found for this directory..check title.txt", Toast.LENGTH_LONG);
					toast.show();
				}
			}
			buttonBack.setVisibility(0);
		}
	}

	private void linearListener() {
		checkDialogIntegrity();

		if(index>=0) {
			int temp;
			playing=true;
			mCompleted=0;
			String[] array;
			audioSet = new File[rowList.size()];
			for(int i=0;i<rowList.size();i++) {
				array = rowList.get(i);
				temp = Integer.parseInt(array[1]);
				audioSet[i] = obj.audioPath(array[0],temp);
			}

			Uri uri = Uri.fromFile(audioSet[0]);

			mPlayer= MediaPlayer.create(getApplicationContext(), uri);

			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mPlayer) {
					// TODO Auto-generated method stub

					mCompleted++;
					mPlayer.reset();
					if (mCompleted < audioSet.length) {
						try {
							Uri uri = Uri.fromFile(audioSet[mCompleted]);
							if (uri != null) {
								mPlayer.setDataSource(getApplicationContext(), uri);
								mPlayer.prepare();
								mPlayer.start();
							}
						} catch (Exception ex) {
							// report a crash
						}
					} else {
						// done with media player
						playing = false;
						mPlayer.release();
						mPlayer = null;
					}
				}
			});
			mPlayer.start();
		}
	}

	private void setonclicklisteners() {
		try {

			info = obj.readLines(Environment.getExternalStorageDirectory().getPath() + "/proloquo/" + path + "info.txt");
		}

		////////////////
		////////////
		// DO MORE HERE
		/////////////////
		////////////////
		catch(Exception e) {
			
		}

		finally {

		}
		tabGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				checkPathIntegrity();
				checkDialogIntegrity();

				if(mPlayer!=null) {
					mPlayer.release();
					mPlayer = null;
				}
				if(playing==true) {
					playing=false;
					index=-1;
					rowList.clear();
					linearInnerSroll.removeAllViews();
				}

				if(info[position].equalsIgnoreCase("touch") || info[position].equalsIgnoreCase("leaf")) {
					if(index>=0) {
						String[] args = rowList.get(index);

						if((Arrays.equals(args,new String[] {path,position+""}))) 
							return;
					}
					rowList.add(new String[] { path, position+""});
					try {
						speak(path,position);
					}
					catch(NullPointerException e) {
						Toast toast = Toast.makeText(getApplicationContext(), "File "+ path + position + ".3gp not found", Toast.LENGTH_LONG);
						toast.show();
					}
					addThumb(path,position,++index,true);
					scrollView();

				}
				else if(info[position].equalsIgnoreCase("sub")){
					try {
						speak(path,position);	
					}
					catch(NullPointerException e) {
						Toast toast = Toast.makeText(getApplicationContext(), "File "+ path + position + ".3gp not found", Toast.LENGTH_LONG);
						toast.show();
					}
					path=path + (position+1) + "/";
					populateGrid();
					if(!(path.equalsIgnoreCase(""))) {
						setButtonControls();
						setTextControls();
					}
				}

				else {
					Toast toast = Toast.makeText(getApplicationContext(), "Information not available (sub/touch/leaf) ..check info.txt", Toast.LENGTH_LONG);
					toast.show();
					return;
				}
			}
		});

		tabGrid.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
				// TODO Auto-generated method stub
				checkDialogIntegrity();

				if(info[position].equalsIgnoreCase("touch")) {

					if(mPlayer!=null) {
						mPlayer.release();
						mPlayer = null;
					}

					if(playing==true) {
						playing=false;
						index=-1;
						rowList.clear();
						linearInnerSroll.removeAllViews();
					}
					try {
						speak(path,position);
					}
					catch(NullPointerException e) {
						Toast toast = Toast.makeText(getApplicationContext(), "File "+ path + position + ".3gp not found", Toast.LENGTH_LONG);
						toast.show();
					}

					Bundle args=new Bundle();
					args.putInt("position", position);
					
					dialogShowing = true;
					
					showDialog(1, args);
					return true;
				}

				return false;
			}
		});

		imageClear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				checkDialogIntegrity();

				if(index>=0) {

					if(mPlayer!=null) {
						mPlayer.release();
						mPlayer = null;
					}

					linearInnerSroll.removeViewAt(index);
					rowList.remove(index);
					index--;
					new Handler().postDelayed(new Runnable() {
						public void run() {
							horizScrollicons.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
						}
					}, 15L);
				}
			}
		});

		imageClear.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				checkDialogIntegrity();

				if(mPlayer!=null) {
					mPlayer.release();
					mPlayer = null;
				}
				if(index!=-1){
					linearInnerSroll.removeAllViews();
					rowList.clear();
					index = -1;
					return true;
				}
				return false;
			}
		});

		linearInnerSroll.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				linearListener();
			}
		});

		buttonHome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				path="";
				buttonPath="";
				setButtonControls();
				setTextControls();
				playHome();
				populateGrid();
			}
		});

		buttonBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkDialogIntegrity();


				buttonPath=removeLastPath(path);
				path=buttonPath;

				if(!(buttonPath.equalsIgnoreCase(""))) {
					buttonPath=removeLastPath(buttonPath);
				}

				populateGrid();

				if(!(path.equalsIgnoreCase(""))) 
					setButtonControls();

				else 
					buttonBack.setVisibility(4);

				setTextControls();
				if(!playing) {

					if(path.equalsIgnoreCase("")) 
						playHome();
					else {
						try {
							speak(buttonPath,Integer.parseInt(getPosition(path))-1);
						}
						catch(NullPointerException e) {
							Toast toast = Toast.makeText(getApplicationContext(), "File "+ path + (Integer.parseInt(getPosition(path))-1) + ".3gp not found", Toast.LENGTH_LONG);
							toast.show();
						}
					}
				}
			}
		});
	}

	private void playHome() {
		File audiopath = new File(Environment.getExternalStorageDirectory()+ "/proloquo/" +"home.3gp");
		Uri uri = Uri.fromFile(audiopath);
		mPlayer= MediaPlayer.create(getApplicationContext(), uri);

		if(mPlayer!=null) {
			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mPlayer) {
					//TODO Auto-generated method stub
					mPlayer.release();
					mPlayer = null;
				}
			});

			mPlayer.start();
		}

		else {
			Toast toast = Toast.makeText(getApplicationContext(), "File home.3gp not found", Toast.LENGTH_LONG);
			toast.show();
		}
	}

	private void checkPathIntegrity() {

		int pathCount = path.replaceAll("[^/]", "").length();
		int buttonPathCount = buttonPath.replaceAll("[^/]", "").length();

		while((pathCount-buttonPathCount)>2) {
			path=removeLastPath(path);
			pathCount = path.replaceAll("[^/]", "").length();
			buttonPathCount = buttonPath.replaceAll("[^/]", "").length();
		}
	}

	private void checkDialogIntegrity() {
		
		if(dialogRunning) {
			path=removeLastPath(path);
			buttonPath=removeLastPath(path);
			setButtonControls();
			setTextControls();
			dialogRunning=false;
		}
	}

	private String removeLastPath(String Path) {
		int charCount = Path.replaceAll("[^/]", "").length();
		if(charCount==1) {
			Path="";
		}
		else if(charCount-- > 1) {
			int pos = Path.indexOf('/', 0);
			while (--charCount > 0 && pos != -1)
				pos = Path.indexOf('/', pos+1);

			Path = Path.substring(0, pos+1);
		}
		return(Path);
	}

	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		// TODO Auto-generated method stub
		super.onPrepareDialog(id, dialog, args);
		
		if(dialogShowing) {
			AnimationSet set = new AnimationSet(true);
			Animation fade = new AlphaAnimation(1.00f, 0.20f);
			set.addAnimation(fade);
			set.setDuration(20);
			set.setFillAfter(true);        
			linearGrid.startAnimation(set);
			}
		
		dialogRunning = true;
		boolean exception = false;

		int parent_pos = args.getInt("position");
		path=path+(parent_pos+1)+"/";
		
		checkPathIntegrity();

		try {

			String temp = removeLastPath(path);
			if(obj.checkDialogExist(temp, getPosition(path))) 
				gridDialog.setAdapter(new ImageAdapter(this,path,1));

			else
				return;

		} catch (IOException e) {
			exception=true;
		}  

		finally {
			if(exception==true) {

				Toast toast = Toast.makeText(getApplicationContext(), "could not load dialog box..check folder structure", Toast.LENGTH_LONG);
				toast.show();
			}
			else {
				gridDialog.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View v, int child_pos, long id) {
						// TODO Auto-generated method stub
						if(mPlayer!=null) {
							mPlayer.release();
							mPlayer = null;
						}

						if(playing==true) {
							index=-1;
							rowList.clear();
							linearInnerSroll.removeAllViews();
							playing=false;
						}

						if(index>=0) {
							String[] args = rowList.get(index);

							if((Arrays.equals(args,new String[] {path,child_pos+""}))) 
								return;
						}

						rowList.add(new String[] {path,child_pos + ""});
						try {
							speak(path,child_pos);
						}
						catch(NullPointerException e) {
							Toast toast = Toast.makeText(getApplicationContext(), "File "+ path + child_pos + ".3gp not found", Toast.LENGTH_LONG);
							toast.show();
						}

						addThumb(path,child_pos,++index,true);
						scrollView();
					}
				});
				dialog.setCanceledOnTouchOutside(true);
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {

		// TODO Auto-generated method stub
		
		
		AlertDialog.Builder builder;  
		Context mContext = this;  
		inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);  
		layout = inflater.inflate(R.layout.dialogue, (ViewGroup)findViewById(R.id.layout_root));
		gridDialog = (GridView) layout.findViewById(R.id.gridSub);
		gridDialog.setSoundEffectsEnabled(false);
		gridDialog.setVerticalSpacing(5);
		builder = new AlertDialog.Builder(mContext);
		builder.setView(layout);
		dialog = builder.create();  
		
		if(display.getWidth()>display.getHeight()) {
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, 0, 0, 10);
			
			gridDialog.setLayoutParams(lp);
			 dialog.getWindow().setGravity(Gravity.BOTTOM);
		}
		
		else {
			 dialog.getWindow().setGravity(Gravity.CENTER);
		}
		
		dialog.setOnCancelListener(new Dialog.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				path=removeLastPath(path);
				dialogShowing = false;
				dialogRunning=false;
			}
		});

		dialog.setOnDismissListener(new Dialog.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				AnimationSet set = new AnimationSet(true);
				Animation fade = new AlphaAnimation(0.00f, 1.00f);
				set.addAnimation(fade);
				set.setDuration(20);
				set.setFillAfter(true);        
				linearGrid.startAnimation(set);

			}
		});
		
		return dialog; 
	}

	private ViewGroup createUI() {

		linearMain = new LinearLayout(this);
		linearMain.setOrientation(LinearLayout.VERTICAL);  ////	 VERTICAL
		linearMain.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)); //width,height, 

		linearScroll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT)); // width,height, 
		linearScroll.setOrientation(LinearLayout.HORIZONTAL);  //// HORIZONTAL

		horizScrollicons = new HorizontalScrollView(this);
		horizScrollicons.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,2)); /// the last parameter being the layout weight
		horizScrollicons.setBackgroundResource(R.drawable.horizscroll);
		horizScrollicons.setHorizontalScrollBarEnabled(false);
		horizScrollicons.setVerticalScrollBarEnabled(false);

		linearInnerSroll.setGravity(16);
		linearInnerSroll.setOrientation(LinearLayout.HORIZONTAL);
		linearInnerSroll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)); /// the last parameter being the layout weight
		linearInnerSroll.setBackgroundResource(R.drawable.iconbackground);
		linearInnerSroll.setSoundEffectsEnabled(false);
		horizScrollicons.setMinimumHeight(54);
		linearInnerSroll.setMinimumHeight(54);
		
		imageClear.setImageResource(R.drawable.clear);
		imageClear.setClickable(true);
		imageClear.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,10)); 
		imageClear.setBackgroundResource(R.drawable.clearbackground);

		horizScrollicons.addView(linearInnerSroll);
		linearScroll.addView(horizScrollicons);
		linearScroll.addView(imageClear);
		linearMain.addView(linearScroll);

		FrameLayout frameAction = new FrameLayout(this);
		frameAction.setBackgroundResource(R.drawable.testing);

		buttonBack.setTextSize(11f);
		buttonHome.setTextSize(11f);
		buttonHome.setText("Home");

		FrameLayout.LayoutParams paramBb = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,48,Gravity.LEFT | Gravity.FILL_VERTICAL);
		FrameLayout.LayoutParams paramI = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,Gravity.CENTER);
		FrameLayout.LayoutParams paramBh = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,48,Gravity.RIGHT | Gravity.FILL_VERTICAL);

		frameAction.addView(buttonBack,paramBb);
		frameAction.addView(textLabel,paramI);
		frameAction.addView(buttonHome,paramBh);

		linearGrid.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		linearGrid.setBackgroundResource(R.drawable.grid);

		ImageView imageLine = new ImageView(this);
		imageLine.setImageResource(R.drawable.line);
		imageLine.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		linearGrid.addView(tabGrid);

		linearMain.addView(frameAction);
		linearMain.addView(imageLine);
		linearMain.addView(linearGrid);
		return linearMain;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(mPlayer!=null) {
			mPlayer.release();
			mPlayer = null;	
		}

		if(dialogRunning) {
			path=removeLastPath(path);
			dialogRunning=false;
		}
	}
}