package com.enjin.bendcraft.minesweeperblack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class GameActivity extends Activity {
	private static final long TIMER_DELAY = 30;
	private int rows;
	private int cols;
	private int mines;
	private int difficulty;
	private int msecCounter;
	private boolean mineMode;
	private boolean gameOver;
	private boolean playerWon;
	private Location gameOverLoc;
	private boolean isPaused;
	private boolean isMoveLocked;
	private boolean isFirstClick;
	private boolean isTimerActive;
	private boolean isScoreSubmitted;
	private String name = "";
	
	private Timer timer = null;
	private long timeStamp = -1;
	private TableLayout tableLayout;
	private ImageButton resetButton;
	private ImageButton zoomButton;
	private ImageButton modeButton;
	private ImageButton moveButton;
	private TextView mineCounter;
	private TextView timerLabel;
	private ClassicalButton[][] buttonGrid;
	private GameClickListener clickListener = new GameClickListener();
	private LongClickListener longClickListener = new LongClickListener();
	private ViewPressListener pressListener = new ViewPressListener();
	
	/**
	 * stackDepth is used in the showNearbyTiles function, since
	 * users have the ability to customize the row/col/mines of a board
	 * there is the possibility for stackoverflow when dealing with removing
	 * the nearby empty tiles. We will use stackDepth to keep track of how
	 * deep the recursion is.
	 */
	private int stackDepth = 0;
	private final int MAX_DEPTH = 150;
	
	public ArrayList<IconSet> ISET_SMALL = new ArrayList<IconSet>();
	public ArrayList<IconSet> ISET_MEDIUM = new ArrayList<IconSet>();
	public ArrayList<IconSet> ISET_LARGE = new ArrayList<IconSet>();
	public ArrayList<IconSet> ISET_STATUSBAR = new ArrayList<IconSet>();
	public ArrayList<IconSet> ISET = ISET_SMALL;
	public ArrayList<IconSet> ISET_STATUS = ISET_STATUSBAR;
	public void initIconSets()
	{
		ISET_SMALL.add(new IconSet("unclicked", getResources().getDrawable(R.drawable.ic_unclicked_s)));
		ISET_SMALL.add(new IconSet("mine", 		getResources().getDrawable(R.drawable.ic_mine_s)));
		ISET_SMALL.add(new IconSet("mine2", 	getResources().getDrawable(R.drawable.ic_mine2_s)));
		ISET_SMALL.add(new IconSet("mine3", 	getResources().getDrawable(R.drawable.ic_mine3_s)));
		ISET_SMALL.add(new IconSet("flag", 		getResources().getDrawable(R.drawable.ic_flag_s)));
		ISET_SMALL.add(new IconSet("zoom", 		getResources().getDrawable(R.drawable.ic_zoom_s)));
		ISET_SMALL.add(new IconSet("zoomoff", 	getResources().getDrawable(R.drawable.ic_zoomoff_s)));
		ISET_SMALL.add(new IconSet("move", 		getResources().getDrawable(R.drawable.ic_move_s)));
		ISET_SMALL.add(new IconSet("moveoff", 	getResources().getDrawable(R.drawable.ic_moveoff_s)));
		ISET_SMALL.add(new IconSet("smile", 	getResources().getDrawable(R.drawable.ic_smile_s)));
		ISET_SMALL.add(new IconSet("frown", 	getResources().getDrawable(R.drawable.ic_frown_s)));
		ISET_SMALL.add(new IconSet("glasses", 	getResources().getDrawable(R.drawable.ic_glasses_s)));
		ISET_SMALL.add(new IconSet("0",			getResources().getDrawable(R.drawable.ic_0_s)));
		ISET_SMALL.add(new IconSet("1",			getResources().getDrawable(R.drawable.ic_1_s)));
		ISET_SMALL.add(new IconSet("2",			getResources().getDrawable(R.drawable.ic_2_s)));
		ISET_SMALL.add(new IconSet("3",			getResources().getDrawable(R.drawable.ic_3_s)));
		ISET_SMALL.add(new IconSet("4",			getResources().getDrawable(R.drawable.ic_4_s)));
		ISET_SMALL.add(new IconSet("5",			getResources().getDrawable(R.drawable.ic_5_s)));
		ISET_SMALL.add(new IconSet("6",			getResources().getDrawable(R.drawable.ic_6_s)));
		ISET_SMALL.add(new IconSet("7",			getResources().getDrawable(R.drawable.ic_7_s)));
		ISET_SMALL.add(new IconSet("8",			getResources().getDrawable(R.drawable.ic_8_s)));
		
		ISET_MEDIUM.add(new IconSet("unclicked", 	getResources().getDrawable(R.drawable.ic_unclicked_m)));
		ISET_MEDIUM.add(new IconSet("mine", 		getResources().getDrawable(R.drawable.ic_mine_m)));
		ISET_MEDIUM.add(new IconSet("mine2", 		getResources().getDrawable(R.drawable.ic_mine2_m)));
		ISET_MEDIUM.add(new IconSet("mine3", 		getResources().getDrawable(R.drawable.ic_mine3_m)));
		ISET_MEDIUM.add(new IconSet("flag", 		getResources().getDrawable(R.drawable.ic_flag_m)));
		ISET_MEDIUM.add(new IconSet("zoom", 		getResources().getDrawable(R.drawable.ic_zoom_m)));
		ISET_MEDIUM.add(new IconSet("zoomoff", 		getResources().getDrawable(R.drawable.ic_zoomoff_m)));
		ISET_MEDIUM.add(new IconSet("move", 		getResources().getDrawable(R.drawable.ic_move_m)));
		ISET_MEDIUM.add(new IconSet("moveoff", 		getResources().getDrawable(R.drawable.ic_moveoff_m)));
		ISET_MEDIUM.add(new IconSet("smile", 		getResources().getDrawable(R.drawable.ic_smile_m)));
		ISET_MEDIUM.add(new IconSet("frown", 		getResources().getDrawable(R.drawable.ic_frown_m)));
		ISET_MEDIUM.add(new IconSet("glasses", 		getResources().getDrawable(R.drawable.ic_glasses_m)));
		ISET_MEDIUM.add(new IconSet("0",			getResources().getDrawable(R.drawable.ic_0_m)));
		ISET_MEDIUM.add(new IconSet("1",			getResources().getDrawable(R.drawable.ic_1_m)));
		ISET_MEDIUM.add(new IconSet("2",			getResources().getDrawable(R.drawable.ic_2_m)));
		ISET_MEDIUM.add(new IconSet("3",			getResources().getDrawable(R.drawable.ic_3_m)));
		ISET_MEDIUM.add(new IconSet("4",			getResources().getDrawable(R.drawable.ic_4_m)));
		ISET_MEDIUM.add(new IconSet("5",			getResources().getDrawable(R.drawable.ic_5_m)));
		ISET_MEDIUM.add(new IconSet("6",			getResources().getDrawable(R.drawable.ic_6_m)));
		ISET_MEDIUM.add(new IconSet("7",			getResources().getDrawable(R.drawable.ic_7_m)));
		ISET_MEDIUM.add(new IconSet("8",			getResources().getDrawable(R.drawable.ic_8_m)));
		
		ISET_LARGE.add(new IconSet("unclicked", 	getResources().getDrawable(R.drawable.ic_unclicked_l)));
		ISET_LARGE.add(new IconSet("mine", 			getResources().getDrawable(R.drawable.ic_mine_l)));
		ISET_LARGE.add(new IconSet("mine2", 		getResources().getDrawable(R.drawable.ic_mine2_l)));
		ISET_LARGE.add(new IconSet("mine3", 		getResources().getDrawable(R.drawable.ic_mine3_l)));
		ISET_LARGE.add(new IconSet("flag", 			getResources().getDrawable(R.drawable.ic_flag_l)));
		ISET_LARGE.add(new IconSet("zoom", 			getResources().getDrawable(R.drawable.ic_zoom_l)));
		ISET_LARGE.add(new IconSet("zoomoff", 		getResources().getDrawable(R.drawable.ic_zoomoff_l)));
		ISET_LARGE.add(new IconSet("move", 			getResources().getDrawable(R.drawable.ic_move_l)));
		ISET_LARGE.add(new IconSet("moveoff", 		getResources().getDrawable(R.drawable.ic_moveoff_l)));
		ISET_LARGE.add(new IconSet("smile", 		getResources().getDrawable(R.drawable.ic_smile_l)));
		ISET_LARGE.add(new IconSet("frown", 		getResources().getDrawable(R.drawable.ic_frown_l)));
		ISET_LARGE.add(new IconSet("glasses", 		getResources().getDrawable(R.drawable.ic_glasses_l)));
		ISET_LARGE.add(new IconSet("0",				getResources().getDrawable(R.drawable.ic_0_l)));
		ISET_LARGE.add(new IconSet("1",				getResources().getDrawable(R.drawable.ic_1_l)));
		ISET_LARGE.add(new IconSet("2",				getResources().getDrawable(R.drawable.ic_2_l)));
		ISET_LARGE.add(new IconSet("3",				getResources().getDrawable(R.drawable.ic_3_l)));
		ISET_LARGE.add(new IconSet("4",				getResources().getDrawable(R.drawable.ic_4_l)));
		ISET_LARGE.add(new IconSet("5",				getResources().getDrawable(R.drawable.ic_5_l)));
		ISET_LARGE.add(new IconSet("6",				getResources().getDrawable(R.drawable.ic_6_l)));
		ISET_LARGE.add(new IconSet("7",				getResources().getDrawable(R.drawable.ic_7_l)));
		ISET_LARGE.add(new IconSet("8",				getResources().getDrawable(R.drawable.ic_8_l)));
		
		ISET_STATUSBAR.add(new IconSet("mine2", 	getResources().getDrawable(R.drawable.ic_mine2_bar)));
		ISET_STATUSBAR.add(new IconSet("flag", 		getResources().getDrawable(R.drawable.ic_flag_bar)));
		ISET_STATUSBAR.add(new IconSet("zoom", 		getResources().getDrawable(R.drawable.ic_zoom_bar)));
		ISET_STATUSBAR.add(new IconSet("zoomoff", 	getResources().getDrawable(R.drawable.ic_zoomoff_bar)));
		ISET_STATUSBAR.add(new IconSet("move", 		getResources().getDrawable(R.drawable.ic_move_bar)));
		ISET_STATUSBAR.add(new IconSet("moveoff", 	getResources().getDrawable(R.drawable.ic_moveoff_bar)));
		ISET_STATUSBAR.add(new IconSet("smile", 	getResources().getDrawable(R.drawable.ic_smile_bar)));
		ISET_STATUSBAR.add(new IconSet("frown", 	getResources().getDrawable(R.drawable.ic_frown_bar)));
		ISET_STATUSBAR.add(new IconSet("glasses", 	getResources().getDrawable(R.drawable.ic_glasses_bar)));
	}
	
	@Override
	protected void onCreate(Bundle bundle) 
	{
		/**
		 * When this activity is first called we want to set the visual layout to
		 * res/layout/activity_game.xml. Then get the amount of rows, cols, and mines
		 * that this game will utilize. If the parameter Bundle contains a specific key
		 * "classicalSave" then we know that this game was loaded with saved information,
		 * and we need to load that information. It is possible that the saved information is
		 * not in the Bundle but it is in the Intent, we will check for both.
		 */
		super.onCreate(bundle);
		setContentView(R.layout.activity_game);
	
		initIconSets();
		msecCounter = 0;
		mineMode = true;
		playerWon = false;
		gameOver = false;
		gameOverLoc = null;
		isPaused = true;
		isMoveLocked = false;
		isFirstClick = true;
		isTimerActive = false;
		isScoreSubmitted = false;
		timer = null;
		
		Intent intent = getIntent();
		rows = intent.getIntExtra("rows",0);
		cols = intent.getIntExtra("cols",0);
		mines = intent.getIntExtra("mines",0);
		difficulty = intent.getIntExtra("difficulty",0);
		
		tableLayout = (TableLayout)findViewById(R.id.table_layout);
		resetButton = (ImageButton)findViewById(R.id.reset_button);
		modeButton = (ImageButton)findViewById(R.id.mode_button);
		zoomButton = (ImageButton)findViewById(R.id.zoom_button);
		moveButton = (ImageButton)findViewById(R.id.move_button);
		mineCounter = (TextView)findViewById(R.id.bomb_counter);
		timerLabel = (TextView)findViewById(R.id.timer_label);
		
		resetButton.setOnClickListener(clickListener);
		zoomButton.setOnClickListener(clickListener);
		moveButton.setOnClickListener(clickListener);
		modeButton.setOnClickListener(clickListener);
		resetButton.setOnTouchListener(pressListener);
		zoomButton.setOnTouchListener(pressListener);
		moveButton.setOnTouchListener(pressListener);
		modeButton.setOnTouchListener(pressListener);
		
		Typeface digifont = Typeface.createFromAsset(getAssets(),"fonts/digital-7.ttf");
		mineCounter.setTypeface(digifont);
		timerLabel.setTypeface(digifont);
		
		/*
		 * If the bundle contains the key "reloading" then we know that this onCreate call
		 * came from screen rotation. If the game contains this key we shall call
		 * reloadGame(Bundle) which will load up the previously saved game.
		 * 
		 * If the intent contains the key "classicSave" then we know that this onCreate call
		 * came from MainActivity's loadClassicGame() method. In this case we will use the intent's
		 * bundle to load the game rather than the bundle.
		 */
		if(bundle != null && bundle.containsKey("classicSave"))
			reloadGame(bundle.getBundle("classicSave"));
		else if(intent.hasExtra("classicSave"))
			reloadGame(intent.getBundleExtra("classicSave"));
		else
			resetGame();
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
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.menuitem_help:
	        {
	        	Intent intent = new Intent(this,HelpActivity.class);
	    		startActivity(intent);
	            return true;
	        }
	        case R.id.menuitem_leaderboard:
	        {
	        	Intent intent = new Intent(this,LeaderboardActivity.class);
	    		startActivity(intent);
	            return true;
	        }
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	@Override
	public void onSaveInstanceState(Bundle bundle)
	{		
		/**
		 * When the Player rotates the device this method will be called to save
		 * the game and View information. We will call saveClassicGame to add an
		 * additional Bundle which will hold the game variables for future use.
		 */
		/*
		 * Warning: We don't want to save the classicalGame to the MainActivity,
		 * There was a bug that would cause the ResumeGame button to become available,
		 * causing crashes. That is why we put resetClassicalGameSave.
		 */
		
		bundle.putBundle("classicSave", getSaveBundle());
		if(isTimerActive)
			stopTimer();
		MainActivity.resetClassicGameSave();
		
		// Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(bundle);
	}
	@Override
	protected void onPause()
	{
		/**
		 * Simply save the game whenever the Activity is paused as long as the game is not over.
		 * Do not save the game if the player has not clicked at least 1 button, if they have not clicked
		 * a button then the mines have not been generated (therefore not worth saving the information).
		 */
		super.onPause();
		if(!shouldGameBeSaved())
			return;
		stopTimer();
		MainActivity.saveClassicGame(getSaveBundle());
	}
	protected void onResume()
	{
		//If this is true then we know a game has at least been going.
		if(shouldGameBeSaved() && !isTimerActive)
			startTimer();
		super.onResume();		
	}
	
	public Bundle getSaveBundle()
	{
		/**
		 * When the Activity is the saved and destroyed this method is called.
		 * This method will place the important reload variables into the saving Bundle
		 * so that we can reload each of the variables in the reloadGame method.
		 * Note: We have to save the locations of clicked buttons, flagged buttons, and
		 * buttons that are mines. To do this we will use a ClassicalButtonInfoBundle
		 * which will hold multiple ArrayList<Location> for each of the respective button types.
		 * We will also save these ArrayLists as Strings using Location.saveToString.
		 * Alternatively when reloading the game we will use Location.loadFromString.
		 */
		Bundle bundle = new Bundle();
		if(ISET == ISET_MEDIUM) bundle.putInt("iconSize", 1);
		else if(ISET == ISET_LARGE) bundle.putInt("IconSize", 2);
		else bundle.putInt("iconSize", 0);
		
		bundle.putBoolean("mineMode", mineMode);
		bundle.putBoolean("gameOver", gameOver);
		bundle.putBoolean("playerWon", playerWon);
		bundle.putBoolean("isPaused", isPaused);
		bundle.putBoolean("isMoveLocked", isMoveLocked);
		bundle.putBoolean("isFirstClick", isFirstClick);
		bundle.putBoolean("isTimerActive", isTimerActive);
		bundle.putBoolean("isScoreSubmitted", isScoreSubmitted);
		bundle.putInt("rows", rows);
		bundle.putInt("cols", cols);
		bundle.putInt("mines", mines);
		bundle.putInt("msecCounter", msecCounter);
		
		ClassicalButtonInfoBundle cbif = getButtonInfoBundle();
		bundle.putString("clickedLocs", Location.saveToString(cbif.getClickedButtons()));
		bundle.putString("flaggedLocs", Location.saveToString(cbif.getFlaggedButtons()));
		bundle.putString("mineLocs", Location.saveToString(cbif.getMines()));
		bundle.putString("gameOverLoc", Location.saveToString(new ArrayList<Location>(){{add(gameOverLoc);}}));
		return bundle;
	}
	public boolean shouldGameBeSaved()
	{
		return !(gameOver || isFirstClick);
	}	
	public void reloadGame(Bundle bundle)
	{
		/**
		 * ReloadGame is called from onCreate, it is only called when onCreate contains a bundle with the
		 * key "classicSave". If it has that key then we know that MainActivity passed a bundle that contains
		 * variables in which we can use to reload the old game.
		 * To reload the game we will use bundle.getVar(String key) to reassign the field variables.
		 * We must make sure we call resetGame so that the grid is created beforehand.
		 * After we have the locations of clicked, flagged, and mine buttons then we must update 
		 * the grid buttons by using for loops.
		 * Reset the MainActivity's classicGameSave so that the previous game will be saved for further use.
		 * @param bundle contains the saved key-value sets that will be used to load the variables
		 */
		rows = bundle.getInt("rows");
		cols = bundle.getInt("cols");
		mines = bundle.getInt("mines");
		
		int iconSize = bundle.getInt("iconSize");
		if(iconSize == 1) ISET = ISET_MEDIUM;
		else if(iconSize == 2) ISET = ISET_LARGE;
		
		resetGame();
		
		mineMode = bundle.getBoolean("mineMode");
		playerWon = bundle.getBoolean("playerWon");
		isPaused = bundle.getBoolean("isPaused");
		isMoveLocked = bundle.getBoolean("isMoveLocked");
		isFirstClick = bundle.getBoolean("isFirstClick");
		isTimerActive = bundle.getBoolean("isTimerActive");
		isScoreSubmitted = bundle.getBoolean("isScoreSubmitted");
		msecCounter = bundle.getInt("msecCounter");
		ArrayList<Location> clickedLocs = Location.loadFromString(bundle.getString("clickedLocs"));
		ArrayList<Location> flaggedLocs = Location.loadFromString(bundle.getString("flaggedLocs"));
		ArrayList<Location> mineLocs = Location.loadFromString(bundle.getString("mineLocs"));
		ArrayList<Location> theGameOverLoc = Location.loadFromString(bundle.getString("gameOverLoc")); //Should only be of size 0 or 1
		
		/*
		 * Specific order:
		 * 		Mines must be loaded first
		 * 		If there is a gameOverLocation (the red mine) then click that one first.
		 * 		Then the buttons must have their on clickImages updated
		 * 		Then the buttons should be clicked and flagged
		 * 		Since clicking the buttons might make a frowny face we need to check if it should have been a smiley face.
		 */
			for(Location loc : mineLocs)
				mineButton(buttonGrid[loc.getRow()][loc.getCol()]);
			if(!isFirstClick)
				updateButtonAdjacentMines();
			if(theGameOverLoc.size() == 1){
				ClassicalButton btn = getClassicalButton(theGameOverLoc.get(0));
				clickButton(btn);
			}
			for(Location loc : clickedLocs)
				clickButton(buttonGrid[loc.getRow()][loc.getCol()]);
			for(Location loc : flaggedLocs)
				flagButton(buttonGrid[loc.getRow()][loc.getCol()]);
			
			if(playerWon) 
				resetButton.setImageDrawable(getIconSet(ISET_STATUS,"glasses").getImage());
			
		if(gameOver)
			updateWrongFlags();
		
		setMineMode(mineMode);
		setMoveLock(isMoveLocked);
		updateLabels();
		MainActivity.resetClassicGameSave();
		
		if(!isPaused && !isTimerActive && !gameOver)
			startTimer();
	}
	public void resetGame()
	{
		/**
		 * ResetGame will be called by pressing the resetButton in the center of the header layout.
		 * All the variables will be set to the freshStates, and then the buttonGrid shall be recreated.
		 * Note: The mines are not generated until the player presses the first click.
		 */
		stopTimer();
		
		gameOver = false;
		playerWon = false;
		gameOverLoc = null;
		isFirstClick = true;
		isPaused = true;
		isScoreSubmitted = false;
		msecCounter = 0;
		
		tableLayout.removeAllViews();
		buttonGrid = new ClassicalButton[rows][cols];
		
		createGrid();
		ClassicalButton.totalClicked = 0;
		ClassicalButton.totalFlagged = 0;
		ClassicalButton.totalMines = 0;
		resetButton.setImageDrawable(getIconSet(ISET_STATUS,"smile").getImage());
		updateLabels();

	}
	public void setGameOver(boolean win)
	{
		/**
		 * setGameOver forces the game to stop and will update the resetButton image depending on
		 * playerWon. If playerWon is true the center reset button will display a smiley face with
		 * glasses, else the button will display a frown. 
		 * 
		 */
		gameOver = true;
		playerWon = win;
		stopTimer();
		showAllTiles();			
		MainActivity.resetClassicGameSave();
		
		if(win)
		{
			resetButton.setImageDrawable(getIconSet(ISET_STATUS,"glasses").getImage());
			if(!isScoreSubmitted)
				callLeaderboardPopup();
		}
		else
		{
			resetButton.setImageDrawable(getIconSet(ISET_STATUS,"frown").getImage());
			updateWrongFlags();
		}
	}
	public void pauseGame(boolean pausing)
	{
		isPaused = pausing;
		if(!isPaused)
			startTimer();
	}
	
	public void createGrid()
	{
		/**
		 * The grid of ClassicalButtons is created in onCreate and whenever the game must reset.
		 * The method will also becalled when the activity gets restarted due to the back button
		 * or when the player rotates the screen.
		 */
		for(int i = 0; i < rows; i++)
		{
			TableRow trow = new TableRow(this);
			for(int j = 0; j < cols; j++)
			{
				ClassicalButton btn = new ClassicalButton(this,new Location(i,j));
				btn.setCurrentImage(getIconSet(ISET,"unclicked"));
				buttonGrid[i][j] = btn;
				
				btn.setOnClickListener(clickListener);
				btn.setOnLongClickListener(longClickListener);
				trow.addView(btn);
			}
			tableLayout.addView(trow);
		}
	}
	public void generateMines(Location safeSpot)
	{	
		/**
		 * Generate all of the mines into the buttonGrid,
		 * the safeSpot is going to be the first button that the user presses.
		 * We do not want the mines to generate until the first button has been pressed
		 * that way the user will not lose on the first turn.
		 */
		Random rand = new Random();
		for(int i = 0; i < mines; i++)
		{
			ClassicalButton btn = buttonGrid[rand.nextInt(rows)][rand.nextInt(cols)];
			if(!btn.isMine() && !btn.getLocation().equals(safeSpot))
			{
				mineButton(btn);
			}
			else
			{
				//Don't count this button
				i--;
			}
		}
		updateButtonAdjacentMines();
	}
	public void updateButtonAdjacentMines()
	{
		/**
		 * This method will cycle through all of the ClassicalButtons
		 * and update the adjacentMines variables for each of them.
		 * It will also adjust their onClickImages respectively.
		 */
		for(ClassicalButton[] cbArray : buttonGrid)
			for(ClassicalButton btn : cbArray)
			{
				int nbMines = getNearbyMines(btn);
				btn.setAdjacentMines(nbMines);
				
				/*
				 * If the button is not a mine we should give it a specific onClickImage
				 * that represents the number displayed when clicked.
				 */
				if(!btn.isMine())
					btn.setOnClickImage(getIconSet(ISET,nbMines + ""));
			}
	}
	public ClassicalButton getClassicalButton(int r, int c)
	{
		if(isWithinBounds(r, c))
			return buttonGrid[r][c];
		return null;
	}
	public ClassicalButton getClassicalButton(Location loc)
	{
		return getClassicalButton(loc.getRow(),loc.getCol());
	}
	public void updateWrongFlags()
	{
		/**
		 * If the game is over and the player lost it might be the case that the user
		 * had flagged some buttons that were not actually mines. In these cases we
		 * will show the user which mines they had flagged that were incorrect.
		 */
		for(ClassicalButton[] cbArray : buttonGrid)
			for(ClassicalButton btn : cbArray)
			{
				if(btn.isFlagged() && !btn.isMine())
					btn.setCurrentImage(getIconSet(ISET,"mine3"));					
			}
	}
	
	public void flagButton(ClassicalButton btn)
	{
		/**
		 * FlagButton is called when the player long clicks a button, or regular clicks a 
		 * button while mineMode=false. A flagged button means that it contains the flagged
		 * image, and should not be revealed when clicked.
		 */
		btn.setFlagged(!btn.isFlagged());
		if(btn.isFlagged())
			btn.setCurrentImage(getIconSet(ISET,"flag"));
		else		
			btn.setCurrentImage(getIconSet(ISET,"unclicked"));
		updateLabels();
		
	}
	public void clickButton(ClassicalButton btn)
	{
		/**
		 * This method is called from a few different locations, onClickListener,
		 * onLongClickListener, showNearbyTiles, and showAllTiles.
		 * If the button is already clicked then we do not want to do anything in this method.
		 * If the player did not click a mine then we will check to see if they won the game.
		 * 
		 * NOTE: showAllTiles happens after the game is already over, so if the game is over and buttons
		 * are being clicked then we do not want to call setGameOver(boolean) again.
		 *  
		 * @param btn The btn that is being clicked.
		 */
		if(btn.isClicked())
			return;
		
		btn.setClicked(true);
		if(!btn.isMine())
		{			
			if(btn.getAdjacentMines() == 0)
				showNearbyTiles(btn);
			
			//Check to see if the player won
			if(!gameOver && ClassicalButton.totalClicked >= ((rows * cols) - mines))
				setGameOver(true);
		}
		else
		{
			if(!gameOver && !playerWon) //If the player won then don't set the red mine
			{
				btn.setCurrentImage(getIconSet(ISET,"mine"));
				setGameOver(false);
				gameOverLoc = btn.getLocation();
			}
		}
	}
	public void mineButton(ClassicalButton btn)
	{
		/**
		 * Turns a ClassicalButton into a mine, does not check to see if
		 * the button was already a mine.
		 * @param btn the ClassicalButton being changed into a mine
		 */
		btn.setMine(true);
		btn.setOnClickImage(getIconSet(ISET,"mine2"));
	}
	public boolean isWithinBounds(int r, int c)
	{
		/**
		 * Determines if a row,col is a valid location for the buttonGrid[][].
		 * @param r the row being checked
		 * @param c the column being checked
		 * @return if the row and column is a valid location
		 */
		if(r < rows && c < cols && r > -1 && c > -1)
			return true;
		return false;
	}
	public boolean isWithinBounds(Location loc)
	{
		return isWithinBounds(loc.getRow(),loc.getCol());
	}
	public int getNearbyMines(ClassicalButton btn)
	{
		/**
		 * Counts the amount of adjacent mines for a ClassicalButton,
		 * this will usually be called once after the very first click of the game.
		 * It will be used to update @param adjacentMines
		 * for every ClassicalButton inside of buttonGrid[][].
		 * @param btn the button of which to check adjacentMines.
		 * @return the amount nearbyMines for a specific button
		 */
		int mineCount = 0;
		Location loc = btn.getLocation();
		if(loc != null)
		{
			for(int i = -1; i < 2; i++)
				for(int j = -1; j < 2; j++)
				{
					/*
					 * Make sure that we are not checking the original buttons location,
					 * we are only interested in the adjacent ones
					 */
					int r = loc.getRow() + i;
					int c = loc.getCol() + j;
					if( !(i == 0 && j == 0) )
						if(isWithinBounds(r,c))
							if(buttonGrid[r][c].isMine())
								mineCount++;
				}
		}
		return mineCount;
	}
	public void showNearbyTiles(ClassicalButton btn)
	{
		/**
		 * When a player clicks an empty tile it should go to this algorithm.
		 * This method will reveal any nearby tiles that are also empty.
		 * 
		 * stackDepth is used in the showNearbyTiles function, since
		 * users have the ability to customize the row/col/mines of a board
		 * there is the possibiliity for stackoverflow when dealing with removing
		 * the nearby empty tiles. We will use stackDepth to keep track of how
		 * deep the recursion is.
		 */
		stackDepth++;
		Location loc = btn.getLocation();
		
		if(stackDepth < MAX_DEPTH)
		{
			for(int i = -1; i < 2; i++)
			{
				for(int j = -1; j < 2; j++)
				{
					int r = loc.getRow() + i;
					int c = loc.getCol() + j;
					
					if(isWithinBounds(r,c))
					{
						ClassicalButton tmpBtn = buttonGrid[r][c];
						if(!tmpBtn.isClicked() && !tmpBtn.isFlagged() && !gameOver)
						{
							/* 
							 * Note: clickButton will also cause the button to show its nearby
							 * tiles if that button has 0 nearbyMines. This will cause this function
							 * to be recursively called until the methods can unwind.
							 */
							clickButton(tmpBtn);
						}
					}
				}
			}
		}
		
		stackDepth--;
	}
	public void showAllTiles()
	{
		/**
		 * Cycles through all of the ClassicalButtons inside buttonGrid[][]
		 * and toggles them to be clicked if they were not already clicked.
		 */
		for(ClassicalButton[] img1 : buttonGrid)
			for(ClassicalButton btn : img1)
			{
				if(!btn.isClicked())
				{
					clickButton(btn);
				}
			}
	}

	public void setMineMode(boolean b)
	{
		/**
		 * mineMode=true causes onClicks to click the buttons and onLongClicks to flag the button.
		 * mineMode=false causes onClicks to flag the buttons and onLongClicks to click the buttons.
		 * This allows the player to be able to choose the way they wish to flag/click buttons.
		 * The toggle of this method is controlled by the mineMode button inside of the headerview.
		 * @param b if the mineMode is true or false
		 */
		mineMode = b;
		if(mineMode)
			modeButton.setImageDrawable(getIconSet(ISET_STATUS,"mine2").getImage());
		else
			modeButton.setImageDrawable(getIconSet(ISET_STATUS,"flag").getImage());
	}
	public void setMoveLock(boolean b)
	{
		isMoveLocked = b;
		//zoomView.setMoveLocked(isMoveLocked);
		
		if(isMoveLocked)
		{
			moveButton.setImageDrawable(getIconSet(ISET_STATUS,"moveoff").getImage());
		}
		else
		{
			moveButton.setImageDrawable(getIconSet(ISET_STATUS,"move").getImage());
		}
	}
	public void changeIconSize()
	{
		if(ISET == ISET_SMALL) ISET = ISET_MEDIUM;
		else if(ISET == ISET_MEDIUM) ISET = ISET_LARGE;
		else if(ISET == ISET_LARGE) ISET = ISET_SMALL;
		
		for(ClassicalButton[] btns : buttonGrid)
			for(ClassicalButton btn : btns)
			{
				btn.setCurrentImage(getIconSet(ISET,btn.getCurrentImage().getName()));
				if(btn.getOnClickImage() != null)
					btn.setOnClickImage(getIconSet(ISET,btn.getOnClickImage().getName()));
			}
	}

	public static String getFormattedInt(int num)
	{
		/**
		 * Format a String from the int that can be added into our
		 * timer and mine counter labels. These labels are always 3 digits so the method
		 * will account for 999 being the max value possible.
		 * This method is called to update the timerLabel every second, and called to
		 * update the remaining mines label.
		 * Note: If the player flags too many mines then it is possible that the label
		 * will also contain a negative symbol, in that case it would have 4 digits.
		 */
		String s;
		String sNum = String.valueOf(num);
		if(num > 999) s = "999";
		else if(num >= 100) s = sNum;
		else if(num >= 10) s = "0" + sNum;
		else if(num > -1) s = "00" + sNum;
		else
		{
			sNum = String.valueOf(Math.abs(num));
			if(num > -10) s = "-00" + sNum;
			else if(num > -100) s = "-0" + sNum;
			else s = "-" + sNum;
		}
		return s;
	}
	public void updateLabels()
	{
		/**
		 * Causes the two TextViews of the header to be updated.
		 * The left TextView determines the amount of remaining mines after considering how many
		 * buttons have been flagged by the player.
		 * The right TextView displays how much time has passed since the Player has started the game.
		 */
		mineCounter.setText(getFormattedInt(mines - ClassicalButton.totalFlagged));
		timerLabel.setText(GameActivity.getFormattedInt(msecCounter/1000));
	}
	public void startTimer()
	{
		stopTimer();
		isTimerActive = true;
		timeStamp = System.currentTimeMillis();
		
		final Handler handler = new Handler();
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){
			public void run(){
				handler.post(new Runnable(){
					public void run()
					{
						if(timeStamp > -1)
						{
							long time = System.currentTimeMillis();
							msecCounter+=(time - timeStamp);
							updateLabels();
	
							timeStamp = time;
						}
					}
				});
			}
		}, 0, TIMER_DELAY);
	}
	public void stopTimer()
	{
		isTimerActive = false;
		if(timer != null)
		{
			timer.cancel();
			timer = null;
			timeStamp = -1;
		}
	}
	public ClassicalButtonInfoBundle getButtonInfoBundle()
	{
		/**
		 * Loops through every button and organizes the button locations into respective
		 * arrayLists that represent clicked buttons, flagged buttons, and mines.
		 * These arrayLists are combined into a ClassicalButtonInfoBundle.
		 * This method is mainly called when resuming a saved game, having the
		 * Locations for each button state is how the program loads previous buttons.
		 * @return a classicalButtonInfoBundle which holds ArrayList locations for clicked,flagged, and mines
		 */
		ArrayList<Location> clicked = new ArrayList<Location>();
		ArrayList<Location> flagged = new ArrayList<Location>();
		ArrayList<Location> mines = new ArrayList<Location>();
		
		for(ClassicalButton[] buttons : buttonGrid)
		{
			for(ClassicalButton btn : buttons)
			{
				Location loc = btn.getLocation();
				
				if(btn.isClicked())
					clicked.add(loc);
				else if(btn.isFlagged())
					flagged.add(loc);
				
				if(btn.isMine())
					mines.add(loc);
			}
		}
		return new ClassicalButtonInfoBundle(clicked,flagged,mines);
	}
	public Drawable getImage(String name)
	{
		/**
		 * Gets an image from /res/drawable.
		 * @param name of the value that we will use to load the picture, if it is invalid the picture will be null.
		 * @return a Drawable that represents a minesweeper sprite, depends on the String name given.
		 */
		IconSet iset = null;
		iset = getIconSet(ISET, name);
		if(iset != null)
			return iset.getImage();		
		return null;
	}
	public void setImage(ImageView btn, String name)
	{
		/**
		 * Gets an image from getImage(String name) and will set that image to the ImageView.
		 * @param btn the ImageView to set the image to.
		 * @param name the key name of the image that will be received from getImage()
		 */
		IconSet iset = this.getIconSet(ISET, name);
		if(iset != null)
			btn.setImageDrawable(iset.getImage());
	}
	public void callLeaderboardPopup()
	{
		if(difficulty == 5) 
			return;
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(getString(R.string.title_activity_leaderboard));
		alert.setMessage(getString(R.string.input_name_hint));

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
		if(name != "")
			input.setText(name);
		alert.setView(input);

		alert.setPositiveButton(getString(R.string.submit), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			name = input.getText().toString();
			ArrayList<LeaderboardItem> lbitems = LeaderboardTabActivity.loadLeaderboardSave(LeaderboardTabActivity.loadFromFile(GameActivity.this));
			double time = msecCounter/1000.0;
			DecimalFormat df = new DecimalFormat(".00");
			time = Double.parseDouble(df.format(time));
			lbitems.add(new LeaderboardItem(difficulty,name,time));
			LeaderboardTabActivity.saveToFile(GameActivity.this,LeaderboardTabActivity.getLeaderboardSave(lbitems));
			isScoreSubmitted = true;
			}
		});

		alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
			  // Canceled.
			  isScoreSubmitted = true;
		  }
		});

		alert.show();
	}
	public void handleButtonClick(ClassicalButton btn, boolean isShortClick)
	{
		if(gameOver || btn.isClicked())
			return;
		if(isPaused)
			pauseGame(false);
		if(isFirstClick){
			generateMines(btn.getLocation());
			isFirstClick = false;
		}
		
		if((mineMode && isShortClick) || (!mineMode && !isShortClick))
		{
			if(btn.isFlagged())
				return;
			clickButton(btn);
		}
		else
		{
			flagButton(btn);
		}
		ClassicalButtonInfoBundle cbib = getButtonInfoBundle();
	}
	public class GameClickListener implements OnClickListener
	{
		public void onClick(View v) 
		{
			if(v instanceof ClassicalButton)
			{
				ClassicalButton btn = (ClassicalButton) v;
				handleButtonClick(btn,true);
			}
			else if(v instanceof ImageButton)
			{
				ImageButton btn = (ImageButton)v;
				if(btn == resetButton)
					resetGame();
				else if(btn == modeButton)
					setMineMode((mineMode = !mineMode));
				else if(btn == zoomButton)	
					changeIconSize();
				else if(btn == moveButton)		
					setMoveLock(!isMoveLocked);
			}
		}
	}
	public class LongClickListener implements OnLongClickListener
	{
		public boolean onLongClick(View v) 
		{
			if(v instanceof ClassicalButton)
			{
				ClassicalButton btn = (ClassicalButton) v;
				handleButtonClick(btn,false);
			}
			return true;
		}
	}
	public class ViewPressListener implements OnTouchListener
	{
		/**
		 * Whenever a status button is pressed it should be made slightly transparent or given
		 * a special background just to let the user know which button they are clicking.
		 */
		@SuppressLint("NewApi")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(v instanceof ImageView)
			{
				ImageView iv = (ImageView)v;
				float alpha = 0.6F;
				if(event.getAction() == MotionEvent.ACTION_UP) alpha = 1;
				
				if(VERSION.SDK_INT >= 11)
					iv.setAlpha(alpha);
				
			}
			return false;
		}
	}
	public class ClassicalButtonInfoBundle
	{
		/**
		 * A ClassicalButtonInfoBundle holds the locations for 3 different ClassicalButton states.
		 * clickedButtons represent all buttons of isClicked=true
		 * flaggedButtons represent all buttons of isFlagged=true
		 * mines represent all buttons of isMine=true
		 * This class is mainly used for saving a game when the Activity is in the process of being destroyed.
		 */
		private ArrayList<Location> clickedButtons = new ArrayList<Location>();
		private ArrayList<Location> flaggedButtons = new ArrayList<Location>();
		private ArrayList<Location> mines = new ArrayList<Location>();
		public ClassicalButtonInfoBundle(ArrayList<Location> clickedButtons, ArrayList<Location> flaggedButtons, ArrayList<Location> mines)
		{
			this.clickedButtons = clickedButtons;
			this.flaggedButtons = flaggedButtons;
			this.mines = mines;
		}
		public ArrayList<Location> getClickedButtons(){
			return clickedButtons;
		}
		public ArrayList<Location> getFlaggedButtons(){
			return flaggedButtons;
		}
		public ArrayList<Location> getMines(){
			return mines;
		}
	}
	public class IconSet
	{
		String name;
		Drawable image;
		public IconSet(String name, Drawable image){
			this.name = name;
			this.image = image;
		}
		public IconSet(){
			this.name = null;
			this.image = null;
		}
		public String getName(){
			return name;
		}
		public Drawable getImage(){
			return image;
		}
		public void setName(String t){
			name = t;
		}
		public void setImage(Drawable t){
			image = t;
		}
		public String toString(){
			return name + " " + image.toString();
		}
	}
	public IconSet getIconSet(ArrayList<IconSet> isets, String s){
		/**
		 * Gets an IconSet from an arraylist of IconSets.
		 * @param isets - The arraylist of iconsets to search through.
		 * @param s - The name of the iconset to return.
		 * @return an IconSet that contains a name and an image.
		 */
		for(IconSet iset : isets)
			if(iset.getName().equalsIgnoreCase(s))
				return iset;
		return null;
	}
}
