package com.sailabs.photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class MainActivity extends Activity implements SurfaceHolder.Callback
{
	  private Camera camera = null;
	  private SurfaceView cameraSurfaceView = null;
	  private SurfaceHolder cameraSurfaceHolder = null;
	  private boolean previewing = false;
	  RelativeLayout relativeLayout;
	 
	  

	 
	 
	  private Button btnCapture = null;
	 
	  @Override
	  protected void onCreate(Bundle savedInstanceState) 
	  {
	    // TODO Auto-generated method stub
	    super.onCreate(savedInstanceState);


	 
	    getWindow().setFormat(PixelFormat.TRANSLUCENT);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(
	                         WindowManager.LayoutParams.FLAG_FULLSCREEN,
	                         WindowManager.LayoutParams.FLAG_FULLSCREEN);
	          
	    setContentView(R.layout.activity_main);
	     
	    relativeLayout=(RelativeLayout) findViewById(R.id.containerImg);
	    relativeLayout.setDrawingCacheEnabled(true);
	    cameraSurfaceView = (SurfaceView)
	                                       findViewById(R.id.surfaceView1);
	  //  cameraSurfaceView.setLayoutParams(new FrameLayout.LayoutParams(640, 480));
	    cameraSurfaceHolder = cameraSurfaceView.getHolder();
	    cameraSurfaceHolder.addCallback(this);
	//    cameraSurfaceHolder.setType(SurfaceHolder.
	  //                                               SURFACE_TYPE_PUSH_BUFFERS);
	          
	
	     
	   
	    btnCapture = (Button)findViewById(R.id.button1);
	    btnCapture.setOnClickListener(new OnClickListener() 
	    {   
	      @Override
	      public void onClick(View v) 
	      {
	         // TODO Auto-generated method stub
	         camera.takePicture(cameraShutterCallback, 
	                                       cameraPictureCallbackRaw,
	                                       cameraPictureCallbackJpeg);
	      }
	    });
	  } 
	 
	  ShutterCallback cameraShutterCallback = new ShutterCallback() 
	  {  
	    @Override
	    public void onShutter() 
	    {
	      // TODO Auto-generated method stub   
	    }
	  };
	 
	  PictureCallback cameraPictureCallbackRaw = new PictureCallback() 
	  {  
	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) 
	    {
	      // TODO Auto-generated method stub   
	    }
	  };
	 
	  PictureCallback cameraPictureCallbackJpeg = new PictureCallback() 
	  {  
	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) 
	    {
	      // TODO Auto-generated method stub   
	      Bitmap cameraBitmap = BitmapFactory.decodeByteArray
	                                                                  (data, 0, data.length);
	      
	   int   wid = cameraBitmap.getWidth();
	     int  hgt = cameraBitmap.getHeight();
	      
	    //  Toast.makeText(getApplicationContext(), wid+""+hgt, Toast.LENGTH_SHORT).show();
	      Bitmap newImage = Bitmap.createBitmap
	                                        (wid, hgt, Bitmap.Config.ARGB_8888);
	   
	      Canvas canvas = new Canvas(newImage);
	   
	      canvas.drawBitmap(cameraBitmap, 0f, 0f, null);
	   
	     Drawable drawable = getResources().getDrawable
	                                                          (R.drawable.mark3);
	      drawable.setBounds(20, 30, drawable.getIntrinsicWidth()+20, drawable.getIntrinsicHeight()+30);
	    drawable.draw(canvas);
	   
	    	

	      File storagePath = new File(Environment.
	                    getExternalStorageDirectory() + "/PhotoAR/"); 
	      storagePath.mkdirs(); 
	   
	      File myImage = new File(storagePath,
	                    Long.toString(System.currentTimeMillis()) + ".jpg");
	            
	      try
	      {
	        FileOutputStream out = new FileOutputStream(myImage);
	        newImage.compress(Bitmap.CompressFormat.JPEG, 80, out);


	        out.flush();
	        out.close();
	      }
	      catch(FileNotFoundException e)
	      {
	        Log.d("In Saving File", e + "");    
	      }
	      catch(IOException e)
	      {
	        Log.d("In Saving File", e + "");
	      }
	   
	      camera.startPreview();
	   
	    
	   
	      newImage.recycle();
	      newImage = null;
	   
	      Intent intent = new Intent();
	      intent.setAction(Intent.ACTION_VIEW);
	     
	      intent.setDataAndType(Uri.parse("file://" + myImage.getAbsolutePath()), "image/*");
	      startActivity(intent);
	      
	    }
	  };
	 
	  @Override
	  public void surfaceChanged(SurfaceHolder holder, 
	                                       int format, int width, int height) 
	  {
	    // TODO Auto-generated method stub
	     
	    if(previewing)
	    {
	      camera.stopPreview();
	      previewing = false;
	    }
	    try 
	    {
	      Camera.Parameters parameters = camera.getParameters();
	      parameters.setPreviewSize(640, 480);
	      parameters.setPictureSize(640, 480);
	      if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
	          camera.setDisplayOrientation(90);
	        
	      }
	      
	     // parameters.setRotation(90);
	      camera.setParameters(parameters);
	          
	      camera.setPreviewDisplay(cameraSurfaceHolder);
	      camera.startPreview();
	      previewing = true;
	    } 
	    catch (IOException e) 
	    {
	      // TODO Auto-generated catch block
	      e.printStackTrace();  
	    }
	  }

	  @Override
	  public void surfaceCreated(SurfaceHolder holder) 
	  {
	    // TODO Auto-generated method stub
	    try
	    {
	      camera = Camera.open();
	    }
	    catch(RuntimeException e)
	    {
	      Toast.makeText(getApplicationContext(), "Device camera  is not working properly, please try after sometime.", Toast.LENGTH_LONG).show();
	    }
	  }

	  @Override
	  public void surfaceDestroyed(SurfaceHolder holder) 
	  {
	    // TODO Auto-generated method stub
	    camera.stopPreview();
	    camera.release();
	    camera = null;
	    previewing = false;
	  }
}
