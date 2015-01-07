package com.enjin.bendcraft.minesweeperblack;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

public class LeaderboardActivity extends TabActivity
{
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/**
		 *  Inflate the menu; this adds items to the action bar if it is present.
		 */
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.leaderboard_settings, menu);
		return super.onCreateOptionsMenu(menu);
	}
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.menuitem_reset_leaderboard:{
	        	//Reset the leaderboard by saving a new arraylist.
	        	showResetAlert();
	            return true;
	        }
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	public void showResetAlert()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(getString(R.string.title_activity_leaderboard));
		alert.setMessage(getString(R.string.leaderboard_reset_hint));

		alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			LeaderboardTabActivity.saveToFile(LeaderboardActivity.this,LeaderboardTabActivity.getLeaderboardSave(new ArrayList<LeaderboardItem>()));
        	finish();
        	startActivity(getIntent());
			}
		});

		alert.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
			  // Canceled.
		  }
		});
		alert.show();
	}
	
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		setContentView(R.layout.activity_leaderboard);
		
		TabHost tabHost = getTabHost();
		TabWidget tabWid = tabHost.getTabWidget();				
		String str1 = getString(R.string.beginner);
		String str2 = getString(R.string.easy);
		String str3 = getString(R.string.medium);
		String str4 = getString(R.string.hard);
		String str5 = getString(R.string.insane);
		
		TabSpec tab1 = tabHost.newTabSpec(str1);
		TabSpec tab2 = tabHost.newTabSpec(str2);
		TabSpec tab3 = tabHost.newTabSpec(str3);
		TabSpec tab4 = tabHost.newTabSpec(str4);
		TabSpec tab5 = tabHost.newTabSpec(str5);
		tab1.setIndicator(str1);
		tab2.setIndicator(str2);
		tab3.setIndicator(str3);
		tab4.setIndicator(str4);
		tab5.setIndicator(str5);		
		
		Intent intent = new Intent(this,LeaderboardTabActivity.class);
		intent.putExtra("difficulty", 0);
		tab1.setContent(intent);	
		intent = new Intent(this,LeaderboardTabActivity.class);
		intent.putExtra("difficulty", 1);
		tab2.setContent(intent);
		intent = new Intent(this,LeaderboardTabActivity.class);
		intent.putExtra("difficulty", 2);
		tab3.setContent(intent);
		intent = new Intent(this,LeaderboardTabActivity.class);
		intent.putExtra("difficulty", 3);
		tab4.setContent(intent);
		intent = new Intent(this,LeaderboardTabActivity.class);
		intent.putExtra("difficulty", 4);
		tab5.setContent(intent);
		
		tabHost.addTab(tab1);
		tabHost.addTab(tab2);
		tabHost.addTab(tab3);
		tabHost.addTab(tab4);
		tabHost.addTab(tab5);
		tabWid.getChildAt(0).getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
		tabWid.getChildAt(1).getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
		tabWid.getChildAt(2).getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
		tabWid.getChildAt(3).getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
		tabWid.getChildAt(4).getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
		
	}

}
