package com.enjin.bendcraft.minesweeperblack;

public class LeaderboardItem
{
	int difficulty;
	String name;
	double time;
	public LeaderboardItem(int difficulty, String name, double time){
		this.difficulty = difficulty;
		this.name = name;
		this.time = time;
	}
	public LeaderboardItem(int difficulty, double time){
		this(difficulty,"",time);
	}
	public LeaderboardItem(){
		this(-1,"",-1);
	}
	public int getDifficulty(){
		return difficulty;
	}
	public String getName(){
		return name;
	}
	public double getTime(){
		return time;
	}
	public void setDifficulty(int t){
		difficulty = t;
	}
	public void setName(String t){
		name = t;
	}
	public void setTime(double t){
		time = t;
	}
	public String toString(){
		return "Difficulty:" + difficulty + " Name:" + name + " Time:" + time;
	}
}