package gamelib;

import gamelib.Task.MessageListener;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

/**************************************
 * タスク管理クラス
 * 
 *
 ************************************/
public class TaskManager implements MessageListener{

	ArrayList<Task> mTasks;
	
	/********************************
	 * このクラスのコンストラクタです。
	 * タスクの一括管理ができます。
	 **********************************/
	public TaskManager(){
		mTasks = new ArrayList<Task>();
		_taskMng = this;
	}
	
	public boolean update(){
		for(int i=0; i<mTasks.size(); i++){
            if(mTasks.get(i).onUpdate() == false){       //更新失敗なら
            	mTasks.remove(i);                    //そのタスクを消す
                    i--;
            }
		}
		return true;
		
	}
	
	
	public void draw(Canvas c){
		for(Task task:mTasks){
			task.draw(c);
		}
		
		//	デバッグを表示する場合はコメントアウトを消すこと
		drawDebug(c);
		
		
	}
	
	public boolean onTouchEvent(MotionEvent me){
		for(Task task:mTasks){
			task.onTouchEvent(me);
		}
		return true;
	}
	
	
	public void release(){
		for(Task task:mTasks){
			task.release();
		}
		mTasks.clear();
		mTasks = null;
		_taskMng = null;
	}
	
	/*******************************
	 * 最低優先度でタスクを追加します。
	 * @param task
	 ******************************/
	public void addTask(Task task){
		task.setMessageListener(this);
		task.setPrior(99);	//	最低優先度
		mTasks.add(task);
	}
	
	/**************************************
	 * 優先度を指定してタスクを追加します。
	 * 配列には優先度順に挿入されます。
	 * @param task
	 * @param prior
	 *************************************/
	public void addTask(Task task,int prior){
		task.setMessageListener(this);
		task.setPrior(prior);
		
		//	優先度の高い順に挿入する
		int i;
		for(i=0;i<mTasks.size();i++){
			if( mTasks.get(i).getPrior() > prior ){
				break;
			}
		}
		mTasks.add(i, task);
		
		
	}
	
	
	public void removeTask(Task task){
		mTasks.remove(task);
	}
	
	public void enableTask(Task task){
		
	}
	
	public void diableTask(Task task){
		
	}
	
	
	/************************************
	 * IDがセットされているタスクを探します。
	 * @param id
	 * @return
	 **************************************/
	public Task getTaskById(int id){
		for(Task task:mTasks){
			if(task.getId()==id)
				return task;
		}
		return null;
	}
	
	
	/********************************************
	 * タグがセットされているタスクを探します。
	 * @param tag
	 * @return
	 *******************************************/
	public Task getTaskByTag(String tag){	
		for(Task task:mTasks){
			if(task.getTag()==tag)
				return task;
		}
		return null;
	}
	
	
	
	//	各種情報表示
	//	最前面にデバッグを表示
	public void drawDebug(Canvas c){
		Paint paint = new Paint();
		paint.setTextSize(20);
		paint.setColor(Color.BLUE);
		
		//c.drawText("w:"+c.getWidth()+" h:"+c.getHeight(),0,100,paint);
		
		//	タスク一覧を表示
		c.drawText("タスク名", 0,100-25,paint);
		c.drawText("優先度",   300,100-25,paint);
		c.drawText("タスクID", 400,100-25,paint);

		int i = 0;
		for(Task task:mTasks){
			String id = String.valueOf(task.getId());
			String prior = String.valueOf(task.getPrior());
			//	String tag = String.valueOf(task.getTag());
			String className = task.getClass().getName();
			
			c.drawText(className, 0,  100+i*25,paint);
			c.drawText(prior,     300,100+i*25,paint);
			c.drawText(id,        400,100+i*25,paint);

			
			i++;
		}
		
	}
	
	
	
	
	private static TaskManager _taskMng;
	public static TaskManager getInst(){
		return _taskMng;
	}

	/*************************************************************
	 * 各タスクからタスクマネージャあてに送信されたメッセージの処理
	 **************************************************************/
	@Override
	public void onReceievedMessage(Task task,String... args) {
		
	}

	/******************************************************************
	 * 指定されたタグのタスクのreceiveMessage()を呼びます
	 * @param from
	 * @param toTag
	 * @param args
	 *****************************************************************/
	@Override
	public void onReceievedMessage(Task from, String toTag, String... args) {
		Task task = getTaskByTag(toTag);
		if( task != null){
			task.receiveMessage(from, args);
		}else{
			Log.w("TaskManager","TASK TAG NOT FOUND");
		}
	}

	
	/******************************************************************
	 * 指定されたIDのタスクのreceiveMessage()を呼びます
	 * @param from
	 * @param toTag
	 * @param args
	 *****************************************************************/
	@Override
	public void onReceievedMessage(Task from, int toId, String... args) {
		Task task = getTaskById(toId);
		if(task != null)
			task.receiveMessage(from, args);
		else{
			Log.w("TaskManager","TASK ID NOT FOUND");
		}
	}
	
	
	
}
