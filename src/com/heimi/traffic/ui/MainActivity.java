package com.heimi.traffic.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.heimi.traffic.R;
import com.heimi.traffic.fragment.MainFragment;

/**
 * 主界面
 * @author sa
 *
 */
public class MainActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
		MainFragment mainFragment=new MainFragment();
		transaction.add(R.id.main_container, mainFragment);
//		transaction.addToBackStack(null);
		transaction.commit();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	
}
