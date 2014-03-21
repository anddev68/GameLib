package gamelib;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;


public class SampleView extends GameView{

	TaskManager _task;
	
	public SampleView(Context context) {
		super(context);
	
		//	640x480の解像度で拡大モードにする
		//	changeScreenSize(640,480,SCREEN_MODE_AUTO);
		
		//	タスク管理クラスを作成
		_task = new TaskManager();
		
		//	タスクを継承したクラスを追加していく
		//	_task.addTask(task);
	}

	@Override
	protected void render(Canvas c) {
		_task.draw(c);
	}

	@Override
	protected void update() {
		_task.update();
	}

	@Override
	protected void init(int width, int height) {
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent me){
		_task.onTouchEvent(me);
		return false;
	}
	
}
