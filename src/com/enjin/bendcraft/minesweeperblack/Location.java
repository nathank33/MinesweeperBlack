package com.enjin.bendcraft.minesweeperblack;

import java.util.ArrayList;

public class Location 
{
	private int row;
	private int col;
	public Location(int row, int col)
	{
		/**
		 * A constructor that sets the row and column for the Location.
		 */
		this.row = row;
		this.col = col;
	}
	public int getRow(){
		/**
		 * Returns the row of this Location.
		 */
		return row;
	}
	public int getCol(){
		/**
		 * Returns the column of this Location.
		 */
		return col;
	}
	public void setRow(int t){
		/**
		 * Sets the Row in this Location.
		 */
		row = t;
	}
	public void setCol(int t){
		/**
		 * Sets the column in this Location.
		 */
		col = t;
	}
	public boolean equals(Location loc)
	{
		/**
		 * Overloaded .equals function to determine of the Row and Column are
		 * equal when compared to another Location.
		 */
		if(loc.getRow() == row && loc.getCol() == col)
			return true;
		return false;
	}
	public String toString()
	{
		/**
		 * Returns the Location as a String.
		 */
		return "Row:" + row + " Col:" + col;
	}
	public static String saveToString(ArrayList<Location> locs)
	{
		/**
		 * Takes an arrayList of locs and turns it into a large string representing each of the rows and cols
		 * for each Location.
		 * Example format: "0,0;1,0;3,5;6,3"
		 * 		1,0 would represent a Location of row 1 and col 0
		 * Note: Use loadFromString to revert the String into an ArrayList. 
		 * @param locs contains multiple locations which will then be turned into 1 large string
		 * @return Returns a lengthy String that represents the rows and columns for each Location in locs
		 */
		StringBuilder sb = new StringBuilder(1024);
		for(Location loc : locs)
		{
			if(loc != null)
				sb.append(loc.getRow() + "," + loc.getCol() + ";");
		}
		return sb.toString();
	}
	/**
	 * Converts a String of rows,cols and turns it into an ArrayList of Locations.
	 * Example format: "0,0;1,0;3,5;6,3"
	 * 		1,0 would represent a Location of row 1 and col 0
	 * Note: Use saveToString to revert the ArrayList into a String.
	 * @param s the large string that will represent each of the Locations of the ArrayList
	 * @return Returns an ArrayList of Location which was represented by the param string s
	 */
	public static ArrayList<Location> loadFromString(String s)
	{
		ArrayList<Location> locs = new ArrayList<Location>();
		if(s != null)
		{
			String[] colons = s.split(";");
			for(String colon : colons)
			{
				String[] commas = colon.split(",");
				if(commas.length == 2)
				{
					try{
						locs.add(new Location(Integer.parseInt(commas[0]),
								Integer.parseInt(commas[1])));
					}catch(NumberFormatException nfe){};
				}
			}
		}
		return locs;
	}
}
