package com.enjin.bendcraft.minesweeperblack;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class LeaderboardTabActivity extends Activity
{
	public final static String FILE_NAME = "Leaderboard";
	private final static int MAX_ITEMS = 10;
	int difficulty;
	public LeaderboardTabActivity(){
		difficulty = 0;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(this.getIntent().hasExtra("difficulty"))
			difficulty = getIntent().getIntExtra("difficulty",0);
		setContentView(R.layout.activity_leaderboard_tab);
		TableLayout tlay = (TableLayout) this.findViewById(R.id.leaderboard_table);
		ArrayList<LeaderboardItem> lbitems = loadLeaderboardSave(loadFromFile(this));
		ArrayList<LeaderboardItem> sortedItems = sortByTime(getByDifficulty(lbitems, difficulty));
		
		//int counter = 0;
		//for(LeaderboardItem lbi : sortedItems)
		for(int counter = 0; counter < MAX_ITEMS; counter++)
		{
			//if(counter >= MAX_ITEMS) 
			//	break;
			
			TableRow row = new TableRow(this);
			LinearLayout llay = new LinearLayout(this);
			TextView tv1 = new TextView(this);
			TextView tv2 = new TextView(this);
			TextView tv3 = new TextView(this);
			tv1.setTextSize(this.getResources().getDimension(R.dimen.help_normal_text));
			tv2.setTextSize(this.getResources().getDimension(R.dimen.help_normal_text));
			tv3.setTextSize(this.getResources().getDimension(R.dimen.help_normal_text));
			llay.addView(tv1);
			llay.addView(tv2);
			llay.addView(tv3);
			row.addView(llay);
			tlay.addView(row);
			
			row.setLayoutParams(new TableLayout.LayoutParams(
					TableLayout.LayoutParams.MATCH_PARENT,
					TableLayout.LayoutParams.MATCH_PARENT, 1f));	
			llay.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,
					TableRow.LayoutParams.MATCH_PARENT,1f));
			tv1.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1f));
			tv2.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1f));
			tv3.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1f));
			tv1.setGravity(Gravity.CENTER);
			tv2.setGravity(Gravity.CENTER);
			tv3.setGravity(Gravity.CENTER);
			
			if(counter < sortedItems.size())
			{
				LeaderboardItem lbi = sortedItems.get(counter);
				tv1.setText(String.valueOf(counter+1));
				tv2.setText(String.valueOf(lbi.getName()));
				tv3.setText(String.valueOf(lbi.getTime()));
			}
			//counter++;
		}
	}
	public static String getLeaderboardSave(ArrayList<LeaderboardItem> lbitems){
		StringBuilder sb = new StringBuilder();
		for(LeaderboardItem lbi : lbitems)
		{
			sb.append(lbi.getDifficulty());
			sb.append(",");
			sb.append(lbi.getName());
			sb.append(",");
			sb.append(lbi.getTime());
			sb.append(";");
		}
		return sb.toString();
	}
	public static ArrayList<LeaderboardItem> loadLeaderboardSave(String s)
	{
		ArrayList<LeaderboardItem> lbitems = new ArrayList<LeaderboardItem>();
		if(s != null)
		{
			String[] colonSplit = s.split(";");
			for(String colon : colonSplit)
			{
				String[] commaSplit = colon.split(",");

				if(commaSplit.length == 3)
				{
					LeaderboardItem lbi = new LeaderboardItem();
					lbi.setDifficulty(Integer.parseInt(commaSplit[0]));
					lbi.setName(commaSplit[1]);
					lbi.setTime(Double.parseDouble(commaSplit[2]));
					lbitems.add(lbi);
				}
			}
		}
		return lbitems;
	}
	public static void saveToFile(Context context, String s)
	{
		try
		{
			FileOutputStream fos = context.openFileOutput(FILE_NAME,Context.MODE_PRIVATE);
			fos.write(s.getBytes());
			fos.close();
		}
		catch(Exception e){e.printStackTrace();}
	}
	public static String loadFromFile(Context context)
	{
		StringBuilder sb = new StringBuilder();
		int ch = 0;
		try
		{
			File file = new File(context.getFilesDir(), FILE_NAME);
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			
			while((ch = bis.read()) != -1)
				sb.append((char)ch);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return sb.toString();
	}
	public ArrayList<LeaderboardItem> getByDifficulty(ArrayList<LeaderboardItem> lbitems, int difficulty)
	{
		ArrayList<LeaderboardItem> temp = new ArrayList<LeaderboardItem>();
		for(LeaderboardItem lbi : lbitems)
			if(lbi.getDifficulty() == difficulty)
				temp.add(lbi);
		return temp;
	}
	public ArrayList<LeaderboardItem> sortByTime(ArrayList<LeaderboardItem> lbitems)
	{
		boolean keepGoing = true;
		while(keepGoing)
		{
			keepGoing = false;
			for(int i = 0; i < lbitems.size() - 1; i++)
				if(lbitems.get(i).getTime() > lbitems.get(i+1).getTime())
				{
					keepGoing = true;
					Collections.swap(lbitems, i, i+1);
				}
		}
		return lbitems;
	}
}
