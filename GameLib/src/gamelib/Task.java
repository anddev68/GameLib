package gamelib;

import android.graphics.Canvas;
import android.view.MotionEvent;

/****************************************
 * ゲームプログラミングにおけるタスクです。
 * このタスクはTaskManagerクラスで一括管理されます。
 *
 ****************************************/
public abstract class Task{

	public interface MessageListener {
		public void onReceievedMessage(Task task,String... args);
		public void onReceievedMessage(Task from,String toTag,String... args);
		public void onReceievedMessage(Task from,int toId,String... args);
	}
	
	
	//	IDもしくはTAGを使用してください
	private int iId;					//	タスクのID
	private String mTag;				//	タスクのタグ
	
	//	メッセージ発行リスナー
	private MessageListener mListener;
	
	
	private int iPrior;				//	優先順位


	public abstract void draw(Canvas c);
	public abstract boolean onUpdate();
	public abstract void release();
	public abstract boolean onTouchEvent(MotionEvent me);
	
	public void setMessageListener(MessageListener l){
		mListener = l;
	}
	
	/*********************************************
	 * タスクマネージャからメッセージを受け取ったとき
	 ********************************************/
	public void receiveMessage(String... args){	}
	
	/*********************************************
	 * タスクからメッセージを受け取ったとき
	 ********************************************/
	public void receiveMessage(Task task,String... args){}
	
	/*******************************************
	 * タスクマネージャーにメッセージを送信する
	 ********************************************/
	protected void sendMessage(String... args){
		mListener.onReceievedMessage(this, args);
	}
	
	/*******************************************
	 * 指定したタスクに対してメッセージを送信する
	 *
	 ******************************************/
	protected final void sendMessage(String tag,String... args){
		mListener.onReceievedMessage(this, tag,args);	
	}
	
	protected final void sendMessage(int id,String... args){
		mListener.onReceievedMessage(this, id,args);	
	}
	
	public final void setId(int id){ iId = id; }
	public final void setTag(String str){mTag = str;}
	public final void setPrior(int prior){iPrior = prior;}
	
	
	public int getId(){ return iId; }
	public String getTag(){ return mTag; }
	public int getPrior(){ return iPrior; }
	
	
	
}
