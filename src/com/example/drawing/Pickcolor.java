package com.example.drawing;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

public class Pickcolor extends Activity implements OnColorChangedListener{
	  private ColorPicker picker;
		private SVBar svBar;
		private OpacityBar opacityBar;
		private Button button;
		private TextView text;
	 
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			
			picker = (ColorPicker) findViewById(R.id.picker);
			svBar = (SVBar) findViewById(R.id.svbar);
			opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
			button = (Button) findViewById(R.id.button1);
			text = (TextView) findViewById(R.id.textView1);
			
			picker.addSVBar(svBar);
			picker.addOpacityBar(opacityBar);
			picker.setOnColorChangedListener(this);
			
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					text.setTextColor(picker.getColor());
					picker.setOldCenterColor(picker.getColor());
				}
			});
		}
	 
		@Override
		public void onColorChanged(int color) {
			//gives the color when it's changed.
		}
}
	
