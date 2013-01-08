package collision.event;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
 
public class GameView extends SurfaceView 
{
	/**������ ������ GameManager*/
	private GameManager gameLoopThread;
	
	/**������ ��������*/
    private List<Sprite> sprites = new ArrayList<Sprite>();
    
    /**������������ �����*/
    private long lastClick;

    /**���������� ����������� ����� ���������*/
    private boolean running = false;
    
    public class GameManager extends Thread
	{
    	/**������ ������*/
	    private GameView view;	 
	    
	    /**����������� ������*/
	    public GameManager(GameView view) 
	    {
	          this.view = view;
	    }

	    /**������� ��������� ������*/
	    public void setRunning(boolean run) 
	    {
	          running = run;
	    }

	    /** ��������, ����������� � ������ */
	    public void run()
	    {
	        while (running)
	        {
	            Canvas canvas = null;
	            try
	            {
	                // ���������� Canvas-�
	                canvas = view.getHolder().lockCanvas();
	                synchronized (view.getHolder())
	                {
	                    // ���������� ���������
	                    onDraw(canvas);
	                }
	            }
	            catch (Exception e) { }
	            finally
	            {
	                if (canvas != null)
	                {
	                	view.getHolder().unlockCanvasAndPost(canvas);
	                }
	            }
	        }
	    }
	}
    
    //-----------------------------------------����� ������ GameManager-------------------------------------
    
    /**����������� ������*/
    public GameView(Context context) 
    {
          super(context);
          
          gameLoopThread = new GameManager(this);
          
          /*������ ��� ���� ������� � ��� ��� ���*/
          getHolder().addCallback(new SurfaceHolder.Callback() 
          {
        	  	 /*** ����������� ������� ��������� */
                 public void surfaceDestroyed(SurfaceHolder holder) 
                 {
                	 boolean retry = true;
                	 gameLoopThread.setRunning(false);
             	    
                	 while (retry)
             	     {
             	        try
             	        {
             	            // �������� ���������� ������
             	        	gameLoopThread.join();
             	            retry = false;
             	        }
             	        catch (InterruptedException e) { }
             	     }
                 }

                 /** �������� ������� ��������� */
                 public void surfaceCreated(SurfaceHolder holder) 
                 {
                        createSprites();
                        gameLoopThread.setRunning(true);
                        gameLoopThread.start();
                 }

                 /** ��������� ������� ��������� */
                 public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
                 {
                 }
          });
    }

    /**�������� ���� ��������*/
    private void createSprites() 
    {
          sprites.add(createSprite(R.drawable.bad1));
          sprites.add(createSprite(R.drawable.bad2));
          sprites.add(createSprite(R.drawable.bad3));
          sprites.add(createSprite(R.drawable.bad4));
          sprites.add(createSprite(R.drawable.bad5));
    }

    /**������� ������ �� �����*/
    private Sprite createSprite(int resouce) 
    {
          Bitmap bmp = BitmapFactory.decodeResource(getResources(), resouce);
          return new Sprite(this, bmp);
    }
    
    /**������� �������� ��� ������� � ���*/
    protected void onDraw(Canvas canvas) 
    {         
    	canvas.drawColor(Color.BLACK);
    	for (Sprite sprite : sprites) 
    	{
            sprite.onDraw(canvas);
    	}
    }

    /**��������� ������� �� ������*/
    public boolean onTouchEvent(MotionEvent event) 
    {
          if (System.currentTimeMillis() - lastClick > 300) 
          {
                 lastClick = System.currentTimeMillis();
                 float x = event.getX();
                 float y = event.getY();
                 
                 synchronized (getHolder()) 
                 {
                        for (int i = sprites.size() - 1; i >= 0; i--) 
                        {
                               Sprite sprite = sprites.get(i);
                               if (sprite.isCollition(x, y)) 
                               {
                                     sprites.remove(sprite);
                                     break;
                               }
                        }
                 }
          }
          return true;
    }
}
 