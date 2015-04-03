package com.example.silentphone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class Help extends FragmentActivity {

	
	SharedPreferences sh;
    
    Button ok;
    int fl=0;
    
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		sh=getSharedPreferences("flag", 0);
		fl=sh.getInt("fl", 0);
		
		if(fl==0){
			setContentView(R.layout.help);
			ok = (Button) findViewById(R.id.button1);
			
			ok.setOnClickListener(new OnClickListener() {
		        
                @Override
                public void onClick(View v) {
                	fl=1;
                	SharedPreferences.Editor edit=sh.edit();
                	edit.putInt("fl", fl);
                	edit.commit();
                
                	Intent i = new Intent("com.example.silentphone.MainActivity");
        			startActivity(i);
                }
            });
			
		}
		
		else
		{
			Intent i = new Intent("com.example.silentphone.MainActivity");
			startActivity(i);
		}
	}

}
