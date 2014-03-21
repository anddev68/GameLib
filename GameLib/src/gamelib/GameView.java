package gamelib;


import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;


/**************************************************
 * 汎用ゲームライブラリ
 * ver 1.0.0
 * 
 * SurfaceViewを継承した描画スレッド持ちのViewです。
 * 抽象メソッドをオーバーライドするだけでOKです。
 * 
 **************************************************/
public abstract class GameView extends SurfaceView implements Runnable,Callback{

	//	画面の拡大・縮小に関するモード
	public static final int SCREEN_MODE_AUTO = 0;	//	画面に合わせて伸縮
	public static final int SCREEN_MODE_CENTER = 1;	//	中央に表示
	public static final int SCREEN_MODE_LEFTTOP = 2;//	左上に表示
	
	//	システム的な変数
	private SurfaceHolder surfaceHolder;
	protected boolean loop;
	protected Thread thread = null;
	protected Context mContext;

	//	その他変数
	protected Bitmap mBuffer = null;						//	ダブルバッファリング用Bitmap
	public static Bitmap mSavedBuffer = null;			//	Activity切り替え時に保存しておくbitmap
	
	//	スクリーン解像度を強制的に変更する場合
	//	画面解像度と異なる場合は拡大・縮小する
	protected int iScreenWidth;
	protected int iScreenHeight;
	protected int iScreenMode = -1;	//	スクリーンモード
	
	
	public GameView(Context context) {
		super(context);
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		mContext = context;
		iScreenMode = -1;
	}

	
	/**********************************
	 * 毎ループごとの描画処理
	 * @param c
	 ***********************************/
	protected abstract void render(Canvas c);
	
	/***********************************
	 * その他計算の処理
	 *********************************/
	protected abstract void update();

	/**************************************************
	 * 画面解像度が必要な場合の初期化関数
	 * コンストラクタでは画面解像度が取得できないため
	 ***************************************************/
	protected abstract void init(int width,int height);
	
	/****************************************************
	 * 画面のスクリーンショットを取得します。
	 * 生成されたActivity外からのみ取得できます。
	 *****************************************************/
	public static Bitmap getScreenShot(){
		return mSavedBuffer;
	}
	
	/************************************************
	 * 解像度を強制的に変更します。
	 * デフォルトは端末解像度です。
	 * 使用する場合は必ずコンストラクタで読んでください。
	 ************************************************/
	protected final void changeScreenSize(int width,int height,int mode){
		iScreenMode = mode;	//	変更する
		iScreenWidth = width;
		iScreenHeight = height;
		
	}
	
	/********************************
	 * Assetからストリームを取得する
	 *******************************/
	public InputStream getStream(String fileName) throws IOException{
		AssetManager asset = mContext.getAssets();
		return asset.open(fileName);
	}
	
	/***********************************
	 * AssetからBitmapを取得する
	 * @return 成功時：bitmap 失敗時：null
	 *************************************/
	public Bitmap getBitmap(String fileName){
		try {
			return BitmapFactory.decodeStream(getStream(fileName));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void run() {
		while(loop){
			
			//	ここで描画処理
			Canvas canvas = surfaceHolder.lockCanvas();
			if(canvas==null) continue;
			
			//	ダブルバッファリングで書いてみる
			if( mBuffer!=null ){
				Canvas c2 = new Canvas(mBuffer);
			
				//	ここで描画を行う
				render(c2);
				
				//	位置をモードによって変更
				switch(iScreenMode){
				case GameView.SCREEN_MODE_AUTO:
					// 	元画像から切り出す位置を指定
					Rect src = new Rect(0, 0, mBuffer.getWidth(), mBuffer.getHeight());
					//	リサイズ画像の領域を指定
					Rect dst = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
					// 	リサイズ画像をCanvasに描画
					canvas.drawBitmap(mBuffer,src,dst,null);
					break;
				case GameView.SCREEN_MODE_CENTER:
					//	中央に描画
					int x = getWidth() /2 - mBuffer.getWidth() /2 ;
					int y = getHeight()/2 - mBuffer.getHeight() /2 ;
					canvas.drawBitmap(mBuffer,x,y,null);
					break;
				case GameView.SCREEN_MODE_LEFTTOP:
					//	そのまま描画
					canvas.drawBitmap(mBuffer, 0, 0, null);					
					break;
				default:	
					//	そのまま描画
					canvas.drawBitmap(mBuffer, 0, 0, null);
				}

			}
			
			surfaceHolder.unlockCanvasAndPost(canvas);
			
			//	ここでその他の処理
            update();
		}
		

	}
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		init(arg2,arg3);
		
		if(mSavedBuffer==null)
			//	Activityの初回起動時は何もない画像で作成
			if(iScreenMode!=0){
				//	画面解像度の強制変更
				mBuffer = Bitmap.createBitmap(iScreenWidth, iScreenHeight, Bitmap.Config.ARGB_8888);
			}else{
				mBuffer = Bitmap.createBitmap(arg2, arg3, Bitmap.Config.ARGB_8888);
			}
		else{
			//	保存しておいた画像を元に戻す
			mBuffer = mSavedBuffer;
			mSavedBuffer = null;
		}
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		thread = new Thread(this);
		thread.start();
		loop = true;	
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		//	スレッドを停止する
		loop = false;
		thread = null;
		
		//	Activity再開時用に現在の画像を保存する
		mSavedBuffer = mBuffer;
		
	}
	
	
	

}
