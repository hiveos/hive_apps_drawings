package com.example.drawing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
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
		public int color;
	 
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_pickcolor);
			
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
					color=picker.getColor();
					text.setTextColor(color);
					picker.setOldCenterColor(color);
					CrtanjeView.boja.setColor(color);
					CrtanjeView.putanja = new mojaPutanja(new Paint(CrtanjeView.boja));
			        CrtanjeView.paths.add(CrtanjeView.putanja);
					onBackPressed();
				}
			});
		}
	 
		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			super.onBackPressed();
		}
		
		

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			switch(item.getItemId()){
			case R.id.action_done:
				color=picker.getColor();
				text.setTextColor(color);
				picker.setOldCenterColor(color);
				CrtanjeView.boja.setColor(color);
				CrtanjeView.putanja = new mojaPutanja(new Paint(CrtanjeView.boja));
		        CrtanjeView.paths.add(CrtanjeView.putanja);
				onBackPressed();
				return true;
			default: return false;
			
			}
		}

		@Override
		public void onColorChanged(int color) {
			//gives the color when it's changed.
		}
}
	
