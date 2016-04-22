package com.app.classlogin;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class NoteVoicePlayActivity extends Activity{

	private MediaPlayer mediaplayer;
	private ImageButton Control, Stop;
	private SeekBar VoiceSeekBar;
	private Chronometer TimeCount;

	private String NoteFilePath, NoteFileUri = "null";
	private long begintime, pausetime, subtime, flagtime;
	private int ControlFlag = 0, PauseFlag = 0;
	private int temp_openMode = 0;

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voiceplay_page);

		Intent intent = getIntent();
		
//		temp_openMode = intent.getIntExtra("ExType_OPEN", 0);		
//		if(temp_openMode == 1){
//			
//		}else{
//			
//		}
		
		/*problem*/
		NoteFileUri = intent.getStringExtra("NoteFileUri");
		/*if(NoteFileUri == "null"){
			NoteFilePath = intent.getStringExtra("NoteFilePath");
		}else{
			NoteFilePath = GetPathFromUri(Uri.parse(NoteFileUri), getContentResolver());
		}*/
		NoteFilePath = intent.getStringExtra("NoteFilePath");
		
		Log.i("VoicePlayTag", "NFP:"+NoteFilePath);
		
		
		Control = (ImageButton) findViewById(R.id.VoicePlay);
		Stop = (ImageButton) findViewById(R.id.VoiceStop);
		VoiceSeekBar = (SeekBar) findViewById(R.id.VoiceSeekBar);
		TimeCount = (Chronometer) findViewById(R.id.chronometerTimeCount);

		SetComponentFunction();

	}
	
	private String GetPathFromUri(Uri note_Uri, ContentResolver mContentResolver){
//		String[] FilePathColumn = { MediaStore.Images.Media.DATA };
		String[] FilePathColumn = { MediaStore.Audio.Media.DATA };
//		String[] FilePathColumn = { "_file" };
		
		Cursor cursor = mContentResolver.query(note_Uri, FilePathColumn,
				null, null, null);
		cursor.moveToFirst();
		int ColumnIndex = cursor.getColumnIndex(FilePathColumn[0]);
		String NotePhotoPath = cursor.getString(ColumnIndex);
		cursor.close();
		return NotePhotoPath;
	}

	private void SetComponentFunction(){
		// Listener
		Control.setOnClickListener(new OnClickListener(){

			@SuppressLint("ShowToast")
			@Override
			public void onClick(View arg0){
				// TODO Auto-generated method stub
				try{
					if(ControlFlag == 0){ // Start Play
						flagtime = SystemClock.elapsedRealtime();
						PlayVoice();
						pausetime = 0;
						Log.i("TimeCHTag", "flagtime:" + flagtime);
						TimeCount.setBase(flagtime);
						TimeCount.start();

						Control.setImageResource(R.drawable.red_pause_button);
						ControlFlag = 1;
					}else if(ControlFlag == 1){ // Pause
						PauseFunction();
						Control.setImageResource(R.drawable.playbutton);
						ControlFlag = 2;
					}else if(ControlFlag == 2){ // Restart
						PauseFunction();
						Control.setImageResource(R.drawable.red_pause_button);
						ControlFlag = 1;
					}

				}catch(Exception e){
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "文件播放出現異常", 0).show();
				}
			}

		});
		Stop.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v){
				// TODO Auto-generated method stub
				if(mediaplayer != null){
					mediaplayer.stop();
					mediaplayer.release();
					mediaplayer = null;
					TimeCount.setBase(SystemClock.elapsedRealtime());
					TimeCount.start();
					TimeCount.stop();
					ControlFlag = 0;
				}
				flagtime = 0;
				subtime = 0;
				VoiceSeekBar.setProgress(0);
				VoiceSeekBar.setEnabled(false);
				Control.setImageResource(R.drawable.playbutton);

			}
		});
		VoiceSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser){
				// TODO Auto-generated method stub
				if(fromUser == true && mediaplayer != null){
					mediaplayer.seekTo(progress);
					flagtime = SystemClock.elapsedRealtime();
					begintime = flagtime - VoiceSeekBar.getProgress();
					TimeCount.setBase(begintime);
					TimeCount.start();
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar){
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar){
				// TODO Auto-generated method stub

			}

		});
		TimeCount.setOnChronometerTickListener(new OnChronometerTickListener(){

			@Override
			public void onChronometerTick(Chronometer chronometer){
				// TODO Auto-generated method stub

			}

		});
	}

	Handler handler = new Handler();

	Runnable updateThread = new Runnable(){

		@Override
		public void run(){
			// TODO Auto-generated method stub
			if(mediaplayer != null){
				VoiceSeekBar.setProgress(mediaplayer.getCurrentPosition());
				handler.postDelayed(updateThread, 100);
			}
		}

	};

	private void PlayVoice() throws Exception{
		// TODO Auto-generated method stub
		if(NoteFilePath == null){

		}else{
			File VoiceFile = new File(NoteFilePath);
			Log.i("VoicePlayTag", "NoteFilePath:" + NoteFilePath);
			Log.i("VoicePlayTag", "NoteExists:" + VoiceFile.exists());
			if(VoiceFile.exists()){
				mediaplayer = new MediaPlayer();
				mediaplayer.setDataSource(NoteFilePath);
				mediaplayer.prepareAsync();
				// Log.i("VoicePlayTag", "CheckData");
				mediaplayer.setOnPreparedListener(new OnPreparedListener(){

					@Override
					public void onPrepared(MediaPlayer mp){
						// TODO Auto-generated method stub
						mediaplayer.start();
						VoiceSeekBar.setMax(mediaplayer.getDuration());
						handler.post(updateThread);
						VoiceSeekBar.setEnabled(true);

					}

				});

				mediaplayer.setOnCompletionListener(new OnCompletionListener(){

					@Override
					public void onCompletion(MediaPlayer mp){
						// TODO Auto-generated method stub
						mediaplayer.release();
						mediaplayer = null;
						TimeCount.setBase(SystemClock.elapsedRealtime());
						TimeCount.start();
						TimeCount.stop();
						VoiceSeekBar.setProgress(0);
						VoiceSeekBar.setEnabled(false);
						ControlFlag = 0;
						Control.setImageResource(R.drawable.playbutton);
					}

				});

				mediaplayer.setOnErrorListener(new OnErrorListener(){

					@Override
					public boolean onError(MediaPlayer mp, int what, int extra){
						// TODO Auto-generated method stub
						Log.i("Media Error Code", "What: " + what + " extra: "
								+ extra);
						return false;
					}

				});

				Log.i("VoicePlayTag", "handler open?");

			}else{
				Log.i("VoicePlayTag", "No File!!!!!!!!!!");
			}
		}
	}

	private void PauseFunction(){
		// TODO Auto-generated method stub
		if(mediaplayer != null && mediaplayer.isPlaying()){
			mediaplayer.pause();
			TimeCount.stop();
			pausetime = SystemClock.elapsedRealtime();
			PauseFlag = 1;
		}else if(mediaplayer != null && PauseFlag == 1){
			subtime += SystemClock.elapsedRealtime() - pausetime;
			mediaplayer.start();
			begintime = flagtime + subtime;
			TimeCount.setBase(begintime);
			TimeCount.start();
			PauseFlag = 0;
		}

	}

}
