package com.enjin.bendcraft.minesweeperblack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private final static int[] rowDifficulties = {8,9,16,16,24};
	private final static int[] colDifficulties = {8,9,16,30,36};
	private final static int[] mineDifficulties = {5,10,40,99,200};
	public final static int MAX_ROWS = 40;
	public final static int MAX_COLS = 40;
	
	private static Bundle classicSave = null;

	private LinearLayout mainLayout;
	private Spinner spinnerDiff;
	private LinearLayout llCustomInput;
	private Button startButton;
	private Button resumeButton;
	private Button helpButton;
	private Button exitButton;
	private EditText rowField;
	private EditText colField;
	private EditText minesField;
	
	/**
	 * MainActivity, when this activity starts the /res/layout/activity_main.xml is loaded.
	 * When the activity is created the private objects will be initialized by using
	 * findViewById.
	 * Since the Activity has just been initialized we need to check to see if the
	 * resume button should be hidden since no games have been started.
	 * Similarly we will hide the CustomInput view since we know that the user has not
	 * selected "Custom" as their game difficulty. Once it is selected then the layout
	 * should be set to Visible.
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mainLayout = (LinearLayout) this.findViewById(R.id.main_linear_layout);
		
		//Add the customizations to the difficulty spinner
		spinnerDiff = (Spinner) this.findViewById(R.id.difficulty_spinner);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.difficulty_array, R.layout.custom_spinner_list);
        adapter.setDropDownViewResource(R.layout.custom_spinner);
        spinnerDiff.setAdapter(adapter);
		
		llCustomInput = (LinearLayout) this.findViewById(R.id.custom_input_layout);
		startButton = (Button) this.findViewById(R.id.start_button);
		resumeButton = (Button) this.findViewById(R.id.resume_button);
		helpButton = (Button) this.findViewById(R.id.close_help_window);
		exitButton = (Button) this.findViewById(R.id.exit_button);
		rowField = (EditText) this.findViewById(R.id.etext_row);
		colField = (EditText) this.findViewById(R.id.etext_col);
		minesField = (EditText) this.findViewById(R.id.etext_mines);
		
		llCustomInput.setVisibility(View.GONE);
		if(classicSave == null)
			resumeButton.setVisibility(View.GONE);
		else
			resumeButton.setVisibility(View.VISIBLE);
		
		
		spinnerDiff.setOnItemSelectedListener(new DifficultySpinnerListener());
		startButton.setOnClickListener(new ButtonListener());
		resumeButton.setOnClickListener(new ButtonListener());
		helpButton.setOnClickListener(new ButtonListener());
		exitButton.setOnClickListener(new ButtonListener());
	}
	/**
	 * When this activity is resumed there is the possibility that a game is in progress,
	 * if there is a Bundle saved for a specific game then show the resumeButton.
	 * When the user presses the resumeButton then the GameActivity will be loaded up
	 * to it's previous settings.
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		if(classicSave == null)
			resumeButton.setVisibility(View.GONE);
		else
			resumeButton.setVisibility(View.VISIBLE);
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/**
		 *  Inflate the menu; this adds items to the action bar if it is present.
		 */
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings, menu);
		return super.onCreateOptionsMenu(menu);
	}
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.menuitem_help:{
	        	Intent intent = new Intent(this,HelpActivity.class);
	    		startActivity(intent);
	            return true;
	        }
	        case R.id.menuitem_leaderboard:{
	        	Intent intent = new Intent(this,LeaderboardActivity.class);
	    		startActivity(intent);
	            return true;
	        }
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	/**
	 * @author Nathan Braun-Krafft
	 * DifficultySpinnerListener handles the game difficulty selection drop down box.
	 * The user can select a few different difficulties, and depending on the selection
	 * a certain amount of rows, cols, and mines will be selected. If they select the
	 * "Custom" difficulty then additional textfields should become VISIBLE allowing 
	 * the player to input their desired information.
	 * To see the possible difficulties go to /res/values/strings.xml name=difficulty_array
	 */
	public class DifficultySpinnerListener implements OnItemSelectedListener
	{
		public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
		{
			if(selectedItemView instanceof TextView)
			{

				TextView tv = (TextView)selectedItemView;
				String txt = tv.getText().toString();
					
				if(position == 5)
				{
					llCustomInput.setVisibility(View.VISIBLE);
					rowField.requestFocus();
					rowField.post(new Runnable(){
						public void run()
						{
							InputMethodManager lManager = (InputMethodManager)rowField.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
					        lManager.showSoftInput(rowField, 0);
						}
					});				
				}
				else 
					llCustomInput.setVisibility(View.GONE);
			}
		}
	    public void onNothingSelected(AdapterView<?> parentView) {}
	}
	
	/**
	 * @author Nathan Braun-Krafft
	 * Handles the Buttons that the Player might press, startButton should
	 * initialize the startNewGame process, and resumeButton will control
	 * loading previously saved data into an activity.
	 */
	public class ButtonListener implements OnClickListener
	{
		public void onClick(View v) 
		{
			if(v.getId() == startButton.getId())
				startNewGame();
			else if(v.getId() == resumeButton.getId())
				loadClassicGame();
			else if(v.getId() == helpButton.getId())
				startHelp();
			else if(v.getId() == exitButton.getId())
			{
				LayoutInflater linf = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
				View popup = linf.inflate(R.layout.exit_popup, null);
				final PopupWindow window = new PopupWindow(popup,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				window.setFocusable(true);
				Button btnYes = (Button)popup.findViewById(R.id.button_quit_yes);
				Button btnNo = (Button)popup.findViewById(R.id.button_quit_no);
				
				btnNo.setOnClickListener(new OnClickListener(){
					public void onClick(View v){
						window.dismiss();
					}});
				btnYes.setOnClickListener(new OnClickListener(){
					public void onClick(View v){
						window.dismiss();
						finish();
						System.exit(0);
					}});
				window.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
			}
		}
	}
	/**
	 * Starts a new ClassicalGame with a specific amount of rows, columns, and mines.
	 * If the difficulty spinner is in a certain the position for a custom game, we need
	 * to parse the data from the textFields, that way we know the amount of rows, cols, and mines.
	 * If it is not in that specific custom position then we can choose the game difficulty directly.
	 * @return
	 */
	public boolean startNewGame()
	{
		int pos = spinnerDiff.getSelectedItemPosition();
		int rows = 0;
		int cols = 0;
		int mines = 0;
		boolean isCustom = false;
		boolean isValid = true;
		if(pos == spinnerDiff.getCount() - 1) 
			isCustom = true;
		
		if(isCustom)
		{
			try{ rows = Integer.parseInt(rowField.getText().toString());} catch(NumberFormatException nfe){}
			try{ cols = Integer.parseInt(colField.getText().toString());} catch(NumberFormatException nfe){}
			try{ mines = Integer.parseInt(minesField.getText().toString());} catch(NumberFormatException nfe){}
			
			if(rows < 1){
				Toast.makeText(this, "Rows must be greater than 1", Toast.LENGTH_SHORT).show();
				isValid = false;
			}
			if(cols < 1){
				Toast.makeText(this, "Columns must be greater than 1", Toast.LENGTH_SHORT).show();
				isValid = false;
			}
			if(mines < 1){
				Toast.makeText(this, "Bombs must be greater than 1", Toast.LENGTH_SHORT).show();
				isValid = false;
			}
			if(rows > MAX_ROWS){
				Toast.makeText(this, "Rows cannot be greater than " + MAX_ROWS, Toast.LENGTH_SHORT).show();
				isValid = false;
			}
			if(cols > MAX_COLS){
				Toast.makeText(this, "Columns cannot be greater than " + MAX_COLS, Toast.LENGTH_SHORT).show();
				isValid = false;
			}
			if(mines >= (rows * cols)){
				Toast.makeText(this, "There must be less mines than spaces (" + (rows*cols) + ")", Toast.LENGTH_SHORT).show();
				isValid = false;
			}
			
			if(!isValid)
				return false;
		}
		else
		{
			rows = rowDifficulties[pos];
			cols = colDifficulties[pos];
			mines = mineDifficulties[pos];
		}
		
		resetClassicGameSave();
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra("isCustom", isCustom);
		intent.putExtra("rows", rows);
		intent.putExtra("cols", cols);
		intent.putExtra("mines", mines);
		intent.putExtra("difficulty", pos);
		startActivity(intent);
		return true;
	}
	public void startHelp()
	{
		Intent intent = new Intent(this,HelpActivity.class);
		startActivity(intent);
	}
	/**
	 * To load a previously saved game we need to make sure that the 
	 * Bundle of saved information is not null. If it is not null then we
	 * know that we have some information that we can reload the game with.
	 * To load the game we create a new intent but use putExtra to add the 
	 * previously saved bundle with the key "classicSave".
	 * The game will be loaded inside of GameActivity.java.
	 */
	public void loadClassicGame()
	{
		if(classicSave == null)
			return;
		
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra("classicSave",classicSave);
		startActivity(intent);
	}
	/**
	 * Takes a bundle of variables and saves it to a static variable to be loaded with
	 * loadClassicGame().
	 * @param bundle created inside of GameActivity.java and allows contains all of the variables
	 * 		used to recreate the previously saved game.
	 */
	public static void saveClassicGame(Bundle bundle)
	{
		classicSave = bundle;
	}
	/**
	 * Explicitly resets the classicSave to null so that we know
	 * there is no saved data for a previously saved ClassicGame.
	 */
	public static void resetClassicGameSave()
	{
		classicSave = null;
	}
    


}
