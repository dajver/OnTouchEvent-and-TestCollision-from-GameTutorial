package collision.event;

import java.util.Random;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
 
public class Sprite 
{
		/**Направление на карте = 3, 1, 0, 2*/
       int[] DIRECTION_TO_ANIMATION_MAP = { 3, 1, 0, 2 };
       
       /**Рядков в спрайте = 4*/
       private static final int BMP_ROWS = 4;
       
       /**Колонок в спрайте = 3*/
       private static final int BMP_COLUMNS = 3;
       
       /**Максимальная скорость = 5
        * Этот пункт влияет на скорость передвижения спрайтов*/
       private static final int MAX_SPEED = 5;
       
       /**Объект класса GameView*/
       private GameView gameView;
       
       /**Картинка*/
       private Bitmap bmp;
       
       /**Позиция по Х=0*/
       private int x = 0;
       
       /**Позиция по У=0*/
       private int y = 0;
       
       /**Скорость по Х*/
       private int xSpeed;
       
       /**Скорость по У*/
       private int ySpeed;
       
       /**Текущий кадр = 0*/
       private int currentFrame = 0;
       
       /**Ширина*/
       private int width;
       
       /**Ввыоста*/
       private int height;
 
       /**Конструктор*/
       public Sprite(GameView gameView, Bitmap bmp) 
       {
             this.width = bmp.getWidth() / BMP_COLUMNS;
             this.height = bmp.getHeight() / BMP_ROWS;
             this.gameView = gameView;
             this.bmp = bmp;
 
             /*Делаем так что бы все спрайты появлялись на рандомных позициях*/
             Random rnd = new Random();
             x = rnd.nextInt(gameView.getWidth() - width);
             y = rnd.nextInt(gameView.getHeight() - height);
             
             /*Делаем скорость объектов рандомной*/
             xSpeed = rnd.nextInt(MAX_SPEED * 2) - MAX_SPEED;
             ySpeed = rnd.nextInt(MAX_SPEED * 2) - MAX_SPEED;
       }
 
       /**Перемещение объекта*/
       private void update() 
       {
             if (x >= gameView.getWidth() - width - xSpeed || x + xSpeed <= 0) 
             {
                    xSpeed = -xSpeed;
             }
             x += xSpeed;
             if (y >= gameView.getHeight() - height - ySpeed || y + ySpeed <= 0) 
             {
                    ySpeed = -ySpeed;
             }
             y += ySpeed;
             currentFrame = ++currentFrame % BMP_COLUMNS;
       }
 
       /**Рисуем наши спрайты*/
       public void onDraw(Canvas canvas) 
       {
             update();
             
             int srcX = currentFrame * width;
             int srcY = getAnimationRow() * height;
             
             Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
             Rect dst = new Rect(x, y, x + width, y + height);
             canvas.drawBitmap(bmp, src, dst, null);
       }
 
       /**Что то*/
       private int getAnimationRow() 
       {
             double dirDouble = (Math.atan2(xSpeed, ySpeed) / (Math.PI / 2) + 2);
             int direction = (int) Math.round(dirDouble) % BMP_ROWS;
             return DIRECTION_TO_ANIMATION_MAP[direction];
       }
 
       /**Проверка на столкновения*/
       public boolean isCollition(float x2, float y2) 
       {
             return x2 > x && x2 < x + width && y2 > y && y2 < y + height;
       }
}