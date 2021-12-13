package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.media.AudioManager;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class MainActivity extends AppCompatActivity {

    protected DrawView dv;
    protected SharedPreferences settings;
    protected Menu mainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dv = new DrawView(this);
        setContentView(dv);
        dv.game.InitSound(this);
        settings = getSharedPreferences("Set", MODE_PRIVATE );
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(dv.game.isMusic()){
            dv.game.TurnOnMusic();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        saveSettings();
        dv.game.TurnOffMusic();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        mainMenu = menu;
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch (id){
            case R.id.music:
                if(dv.game.isMusic()){
                    item.setChecked(false);
                    dv.game.TurnOffMusic();
                }else
                {
                    item.setChecked(true);
                    dv.game.TurnOnMusic();
                }
                dv.game.setMusic(!dv.game.isMusic());
                return true;
            case  R.id.touch:
                item.setChecked(dv.game.isTouch() ? false : true);
                dv.game.setTouch(!dv.game.isTouch());
                return true;
            case R.id.celebrate:
                item.setChecked(dv.game.isCelebrate() ? false : true);
                dv.game.setCelebrate(!dv.game.isCelebrate());
                return true;
            case R.id.save:
                saveSettings();
                return true;
            case R.id.load:
                loadSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void loadSettings(){
        dv.game.setMusic(Integer.parseInt(settings.getString("Music", "")) == 1 ? true : false);
        dv.game.setTouch(Integer.parseInt(settings.getString("Touch", "")) == 1 ? true : false);
        dv.game.setCelebrate(Integer.parseInt(settings.getString("Celebrate", "")) == 1 ? true : false);

        mainMenu.findItem(R.id.music).setChecked(dv.game.isMusic());
        mainMenu.findItem(R.id.touch).setChecked(dv.game.isTouch());
        mainMenu.findItem(R.id.celebrate).setChecked(dv.game.isCelebrate());

        if(dv.game.isMusic()){
            dv.game.TurnOnMusic();
        }
    }

    protected void saveSettings(){
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString("Music", dv.game.isMusic() ? "1" : "0");
        prefEditor.putString("Touch", dv.game.isTouch() ? "1" : "0");
        prefEditor.putString("Celebrate", dv.game.isCelebrate() ? "1" : "0");
        prefEditor.apply();
    }

}

class DrawView extends View {

    static Game game = new Game();

    Paint paint = new Paint();

    public DrawView(Context context){
        super(context);
    }

    public void DrawPic1(Canvas canvas, int x, int y, int w){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.p2);
        canvas.drawBitmap(bitmap, null, new Rect(x,y, x + w, y + w), paint);
    }

    public void DrawPic2(Canvas canvas, int x, int y, int w){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.p1);
        canvas.drawBitmap(bitmap, null, new Rect(x,y, x + w, y + w), paint);
    }

    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawColor(Color.BLACK);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int w = displayMetrics.widthPixels;
        int h = displayMetrics.heightPixels;
        //brush
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(10);
        //line
        canvas.drawLine(0, w / 3, w, w / 3, paint);
        canvas.drawLine(0, 2*w / 3, w, 2*w / 3, paint);
        canvas.drawLine(w / 3, 50, w / 3, w, paint);
        canvas.drawLine(2*w / 3, 50, 2*w / 3, w, paint);
        //
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                if(game.get(i,j)==1) DrawPic1( canvas, i*w/3, j*w/3, w/3);
                else if(game.get(i,j)==2) DrawPic2( canvas, i*w/3, j*w/3, w/3);

        String s=new String();

        int state=game.check();

        if(state==0 && game.getPlayer()==1) s="Ход первого игрока";

        if(state==0 && game.getPlayer()==2) s="Ход второго игрока";

        if(state!=0 && state!=9&& game.getPlayer()==2) s="Победа первого игрока";
        if(state!=0 && state!=9&& game.getPlayer()==1) s="Победа второго игрока";
        if(state==9 ) s="Ничья";

        paint.setTextSize(50);
        canvas.drawText(s, 10, 3*w/3+50,paint);



        if (game.getRun() == 0){
            if(game.isCelebrate()){
                game.TurnOnCelebrate();
            }
            switch (state) {
                case 1:
                    canvas.drawLine(0.5f * w / 3, 50, 0.5f * w / 3, w, paint);
                    break;
                case 2:
                    canvas.drawLine(1.5f * w / 3, 50, 1.5f * w / 3, w, paint);
                    break;
                case 3:
                    canvas.drawLine(2.5f * w / 3, 50, 2.5f * w / 3, w, paint);
                    break;
                case 4:
                    canvas.drawLine(0, 0.5f * w / 3, w, 0.5f * w / 3, paint);
                    break;
                case 5:
                    canvas.drawLine(0, 1.5f * w / 3, w, 1.5f * w / 3, paint);
                    break;
                case 6:
                    canvas.drawLine(0, 2.5f * w / 3, w, 2.5f * w / 3, paint);
                    break;
                case 7:
                    canvas.drawLine(0, 0, w, w, paint);
                    break;
                case 8:
                    canvas.drawLine(0, w, w, 0, paint);
                    break;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (game.getRun() != 0) {

            if(game.isTouch()){
                game.TurnOnTouch();
            }

            float X = event.getX();
            float Y = event.getY();
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int w = displayMetrics.widthPixels;
            int h = displayMetrics.heightPixels;
            int i = 0, j = 0;

            if (X >= 0 && X <= w / 3) i = 0;
            if (X >= w / 3 + 10 && X <= 2 * w / 3 + 10) i = 1;
            if (X >= 2 * w / 3 + 20) i = 2;

            if (Y >= 0 && Y <= w / 3) j = 0;
            if (Y >= w / 3 + 10 && Y <= 2 * w / 3 + 10) j = 1;
            if (Y >= 2 * w / 3 + 20) j = 2;

            if (game.get(i, j) == 0) {
                game.set(i, j);
                game.changePlayer();
                invalidate();
                return true;
            }
        }
        return false;
    }
}

class Game{

    private SoundPool sp;
    private int soundMusic, soundTouch, soundEnd;
    private boolean music = false, touch = false, celebrate = false;
    int[][] a = {{0,0,0},{0,0,0},{0,0,0}};
    int player = 1;
    int run = 1;

    public void InitSound(Context context){
        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundMusic = sp.load(context, R.raw.music, 1);
        soundTouch = sp.load(context, R.raw.touch, 1);
        soundEnd = sp.load(context, R.raw.end, 1);
    }

    public void TurnOnMusic(){
        sp.play(soundMusic,1, 1, 1, 10, 1);
    }

    public void TurnOffMusic(){
        sp.stop(soundMusic);
    }

    public void TurnOnTouch(){
        sp.play(soundTouch, 1,1,1,0, 1);
    }

    public void TurnOnCelebrate() {
        sp.play(soundEnd, 1,1,1,1, 1);
    }

    public boolean isTouch() {
        return touch;
    }

    public void setTouch(boolean touch) {
        this.touch = touch;
    }

    public boolean isCelebrate() {
        return celebrate;
    }

    public void setCelebrate(boolean celebrate) {
        this.celebrate = celebrate;
    }

    public boolean isMusic() {
        return music;
    }

    public void setMusic(boolean music) {
        this.music = music;
    }

    public int getRun(){
        return run;
    }

    public void set(int i, int j){
        a[i][j] = player;
    }

    public int get(int i, int j){
        return a[i][j];
    }

    public void changePlayer(){
        player = player == 1 ? 2 : 1;
    }

    public int getPlayer(){
        return player;
    }

    public int check(){
        int state = 0;

        if(a[0][0] != 0 && a[0][0] == a[0][1] && a[0][1] == a[0][2]) state = 1; else
        if(a[1][0] != 0 && a[1][0] == a[1][1] && a[1][1] == a[1][2]) state = 2; else
        if(a[2][0] != 0 && a[2][0] == a[2][1] && a[2][1] == a[2][2]) state = 3; else

        if(a[0][0] != 0 && a[0][0] == a[1][0] && a[1][0] == a[2][0]) state = 4; else
        if(a[0][1] != 0 && a[0][1] == a[1][1] && a[1][1] == a[2][1]) state = 5; else
        if(a[0][2] != 0 && a[0][2] == a[1][2] && a[1][2] == a[2][2]) state = 6; else

        if(a[0][0] != 0 && a[0][0] == a[1][1] && a[1][1] == a[2][2]) state = 7; else
        if(a[0][2] != 0 && a[0][2] == a[1][1] && a[1][1] == a[2][0]) state = 8; else

        if (a[0][0]*a[0][1]*a[0][2]*a[1][0]*a[1][1]*a[1][2]*a[2][0]*a[2][1]*a[2][2] != 0)
            state = 9;

        if(state != 0) run = 0;

    return state;
    }
}