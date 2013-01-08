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
	/**Объект класса GameManager*/
	private GameManager gameLoopThread;
	
	/**Список спрайтов*/
    private List<Sprite> sprites = new ArrayList<Sprite>();
    
    /**Определитель клика*/
    private long lastClick;

    /**Переменная запускающая поток рисования*/
    private boolean running = false;
    
    public class GameManager extends Thread
	{
    	/**Объект класса*/
	    private GameView view;	 
	    
	    /**Конструктор класса*/
	    public GameManager(GameView view) 
	    {
	          this.view = view;
	    }

	    /**Задание состояния потока*/
	    public void setRunning(boolean run) 
	    {
	          running = run;
	    }

	    /** Действия, выполняемые в потоке */
	    public void run()
	    {
	        while (running)
	        {
	            Canvas canvas = null;
	            try
	            {
	                // подготовка Canvas-а
	                canvas = view.getHolder().lockCanvas();
	                synchronized (view.getHolder())
	                {
	                    // собственно рисование
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
    
    //-----------------------------------------Конец класса GameManager-------------------------------------
    
    /**Конструктор класса*/
    public GameView(Context context) 
    {
          super(context);
          
          gameLoopThread = new GameManager(this);
          
          /*Рисуем все наши объекты и все все все*/
          getHolder().addCallback(new SurfaceHolder.Callback() 
          {
        	  	 /*** Уничтожение области рисования */
                 public void surfaceDestroyed(SurfaceHolder holder) 
                 {
                	 boolean retry = true;
                	 gameLoopThread.setRunning(false);
             	    
                	 while (retry)
             	     {
             	        try
             	        {
             	            // ожидание завершение потока
             	        	gameLoopThread.join();
             	            retry = false;
             	        }
             	        catch (InterruptedException e) { }
             	     }
                 }

                 /** Создание области рисования */
                 public void surfaceCreated(SurfaceHolder holder) 
                 {
                        createSprites();
                        gameLoopThread.setRunning(true);
                        gameLoopThread.start();
                 }

                 /** Изменение области рисования */
                 public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
                 {
                 }
          });
    }

    /**Создание всех спрайтов*/
    private void createSprites() 
    {
          sprites.add(createSprite(R.drawable.bad1));
          sprites.add(createSprite(R.drawable.bad2));
          sprites.add(createSprite(R.drawable.bad3));
          sprites.add(createSprite(R.drawable.bad4));
          sprites.add(createSprite(R.drawable.bad5));
    }

    /**Создаем спрайт на сцене*/
    private Sprite createSprite(int resouce) 
    {
          Bitmap bmp = BitmapFactory.decodeResource(getResources(), resouce);
          return new Sprite(this, bmp);
    }
    
    /**Функция рисующая все спрайты и фон*/
    protected void onDraw(Canvas canvas) 
    {         
    	canvas.drawColor(Color.BLACK);
    	for (Sprite sprite : sprites) 
    	{
            sprite.onDraw(canvas);
    	}
    }

    /**Обработка косания по экрану*/
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
 