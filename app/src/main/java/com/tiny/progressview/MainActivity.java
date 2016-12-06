package com.tiny.progressview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tiny.waveview.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final DrawableProgressView waveView = (DrawableProgressView) findViewById(R.id.wv);
		final CircularProgressView cpv = (CircularProgressView) findViewById(R.id.cpv);

		Observable.interval(200, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<Long>() {

					@Override
					public void call(Long aLong) {

						waveView.setProgress((int) (1 + Math.random() * (100 - 1 + 1)));

						cpv.setProgress(aLong.intValue());
					}
				});
	}
}
