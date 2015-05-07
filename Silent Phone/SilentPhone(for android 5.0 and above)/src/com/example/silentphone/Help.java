package com.example.silentphone;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class Help extends FragmentActivity {

    Button ok;
    
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
			setContentView(R.layout.help);
			ok = (Button) findViewById(R.id.button1);
			
			ok.setOnClickListener(new OnClickListener() {
		        
                @Override
                public void onClick(View v) {
                	
        			finish();
                }
            });
			
		}
		

}
