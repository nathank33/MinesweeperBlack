package com.enjin.bendcraft.minesweeperblack;

import com.enjin.bendcraft.minesweeperblack.GameActivity.IconSet;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class ClassicalButton extends ImageView
{
	public static int totalFlagged = 0;
	public static int totalClicked = 0;
	public static int totalMines = 0;
	private boolean isItAMine;
	private boolean isItClicked;
	private boolean isItFlagged;
	private boolean isWrongFlag;
	private int adjacentMines;
	private Location loc;
	private IconSet currentImage;
	private IconSet onClickImage;
	
	public ClassicalButton(Context context, Location loc)
	{
		/**
		 * Creates a new ClassicalButton with the specified Location.
		 * When the button is first created we initialize the adjecentMines
		 * to 0.
		 */
		super(context);
		this.loc = loc;
		isItAMine = false;
		isItClicked = false;
		isItFlagged = false;
		adjacentMines = 0;
		currentImage = null;
		onClickImage = null;
	}
	public ClassicalButton(Context context) 
	{
		this(context,new Location(-1,-1));
	}
	public boolean isMine(){
		/**
		 * Returns if the ClassicalButton is a mine or not.
		 */
		return isItAMine;
	}
	public boolean isClicked(){
		/**
		 * Returns if the button has already been clicked.
		 */
		return isItClicked;
	}
	public boolean isFlagged(){
		/**
		 * Returns if the player currently flagged this ClassicalButton.
		 */
		return isItFlagged;
	}
	public boolean isWrongFlag(){
		/**
		 * Returns if the user had flag the mine even though it was not a mine.
		 */
		return isWrongFlag;
	}
	public int getAdjacentMines(){
		/**
		 * Returns how many adjacentMines are next to this button,
		 * assume that the function has already been ran to update the amount
		 * of adjacentMines.
		 */
		return adjacentMines;
	}
	public Location getLocation(){
		/**
		 * Returns the location of the ClassicalButton.
		 */
		return loc;
	}
	public IconSet getCurrentImage(){
		/**
		 * Gets the currentImage that the button currently has in view.
		 */
		return currentImage;
	}
	public IconSet getOnClickImage(){
		/**
		 * Gets the Image that the button will display when it is clicked.
		 */
		return onClickImage;
	}
	public void setMine(boolean b)
	{
		/**
		 * Sets the button's variable to say if it is a mine or not.
		 */
		isItAMine = b;
		if(isMine())
			totalMines++;
		else
			totalMines--;
	}
	public void setClicked(boolean b){
		/**
		 * Sets if the button has already been clicked.
		 */
		isItClicked = b;
		if(isClicked())
		{
			totalClicked++;
			this.setImageDrawable(onClickImage.getImage());
			this.setCurrentImage(onClickImage);
		}
		else
		{
			totalClicked--;
			this.setImageDrawable(currentImage.getImage());
		}
	}
	public void setFlagged(boolean b){
		/**
		 * Sets if the button has already been flagged.
		 */
		isItFlagged = b;
		if(isFlagged())
			totalFlagged++;
		else
			totalFlagged--;
	}
	public void setWrongFlag(boolean b){
		/**
		 * Sets if the button was a wrong flag.
		 */
		isWrongFlag = b;
	}
	public void setAdjacentMines(int i){
		/**
		 * Sets how many adjacent mines surround this specific button.
		 */
		adjacentMines = i;
	}
	public void setLocation(Location l){
		/**
		 * Sets the location for this button.
		 */
		loc = l;
	}
	public void setCurrentImage(IconSet img){
		/**
		 * Sets the current Image of this button.
		 */
		currentImage = img;
		this.setImageDrawable(img.getImage());
	}
	public void setOnClickImage(IconSet img){
		/**
		 * Sets the image that will be displayed when the button is clicked.
		 */
		onClickImage = img;
	}


}
