package com.webtimer.timer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

public class CountdownTimer {

	private Timer timer;

	private final int DELAY = 0;
	private final int UPDATE_INTERVAL = 1000;

	private static final int DEFAULT_INTERVAL = 90000; //the countdown interval - default: 90 seconds
	
	private static int interval;
	private static int interval2;
	private static List<Integer> anneTimes;
	private static boolean useAnneTimes = false;
	
	private static int countdown = 3000; //the time left over in the current interval
	private static String comments = "";
	public static boolean isRunning = false;
	
	/**
	 * Default values to change the interval with.
	 */
	//Todo: move these to a conf file.
	private static int plusTime = 10000;
	private static int minusTime = 10000;
	
	/**
	 * The interval must be between <lowerLimit> and <higherLimit>
	 */
	private static int lowerLimit = 20000;
	private static int higherLimit = 120000;
	
	private static int maxLinesUserComments = 5;
	
	private static final Logger logger = Logger.getLogger(CountdownTimer.class);

	//////////////////////////////////////////////////////////////////////////////////
	
	/* GETTER AND SETTERS */
	
	public static int getInterval() {
		return interval;
	}
	
	public static int getIntervalSec() {
		return interval/1000;
	}

	public static void setInterval(int interval) {
		CountdownTimer.interval = interval;
	}
	
	public static int getInterval2() {
		return interval2;
	}
	
	public static int getInterval2Sec() {
		return interval2/1000;
	}

	public static void setInterval2(int interval) {
		CountdownTimer.interval2 = interval;
	}
	
	public static List<Integer> getAnneTimes() {
		return anneTimes;
	}

	public static void setAnneTimes(List<Integer> anneTimes) {
		if (anneTimes.size()>2){
			CountdownTimer.anneTimes = anneTimes;
			CountdownTimer.setInterval(CountdownTimer.anneTimes.remove(0));
			CountdownTimer.setInterval2(CountdownTimer.anneTimes.remove(0));
			CountdownTimer.setCountdown(3000);
			CountdownTimer.useAnneTimes=true;
			logger.debug("anneTimes and the intervals are set!");
			
			if (!CountdownTimer.isRunning){
				CountdownTimer cdt = new CountdownTimer();
				cdt.start();
			}
		}
	}

	public static int getCountdown() {
		return countdown;
	}
	
	public static int getCountdownSec() {
		return countdown/1000;
	}

	public static void setCountdown(int countdown) {
		CountdownTimer.countdown = countdown;
	}
	
	public static String getComments(){
		return comments;
	}
	
	public static void setComments(String c){
		CountdownTimer.comments = c;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	
	/*METHODS*/
	
	/**
	 * Start the timer and keep it going.
	 */
	public boolean start(){
		logger.info("Starting the timer.");
		
		setTheIntervals();
		
		timer = new Timer();
		timer.scheduleAtFixedRate(
				new TimerTask(){
					@Override
					public void run(){
						timeStep();
						if (logger.isTraceEnabled()){logger.trace("Timer is at: " + String.valueOf(countdown));}
					}
				},
				this.DELAY,
				this.UPDATE_INTERVAL
				);
		isRunning=true;
		return isRunning;
	}

	/**
	 * Stop the timer.
	 */
	public boolean stop(){
		logger.info("Stopping the timer.");
		timer.cancel();
		isRunning=false;
		useAnneTimes=false;
		anneTimes = null;
		return isRunning;
	}

	/**
	 * Helper method to update the time left over in the current run.
	 * Reset interval when it runs out.
	 */
	private void timeStep(){
		if(CountdownTimer.isRunning){
			
			if(logger.isTraceEnabled()){logger.trace("Using annetime? " + String.valueOf(CountdownTimer.useAnneTimes));}
			
			if (CountdownTimer.useAnneTimes){
				if (countdown <= 0){ //timer ran out
					if (interval==999999){ //this must have been the last countdown of this set, because interval=999999
						this.stop();
						return;
					}
					countdown = interval;
					interval = interval2;
					if (!anneTimes.isEmpty()){ //go to the next interval in the list
						interval2 = anneTimes.remove(0);
					} else { //list ran out --> set indicator for end of list
						interval2 = 999999;
					}
				}
			} else { //not using anneTimes --> keep using the default interval
				if (countdown <= 0){
					countdown = interval;
					interval = interval2;
					interval2 = DEFAULT_INTERVAL;
				}
			}
			countdown -= UPDATE_INTERVAL;
		}
	}
	
	/**
	 * Add a bit of extra time to the next timer.
	 * Must be between the two limits.
	 */
	public static void doPlus(){ 
		if (interval >= lowerLimit && interval < higherLimit){
			if (logger.isDebugEnabled()){logger.debug("Added: " + String.valueOf(plusTime) + " to " + String.valueOf(interval) + " (interval).");}
			interval += plusTime;
		}
	}
	
	/**
	 * Subtract a bit of extra time from the next timer.
	 * Must be between the two limits.
	 */
	public static void doMinus(){
		if (interval > lowerLimit && interval <= higherLimit){
			if (logger.isDebugEnabled()){logger.debug("Substracting: " + String.valueOf(minusTime) + " from " + String.valueOf(interval) + " (interval).");}
			interval -= minusTime;
		}
	}
	
	//TODO: DRY-principle for interval2
	
	/**
	 * Add a bit of extra time to the next timer.
	 * Must be between the two limits.
	 */
	public static void doPlus2(){
		if (interval2 >= lowerLimit && interval2 < higherLimit){
			if (logger.isDebugEnabled()){logger.debug("Added: " + String.valueOf(plusTime) + " to " + String.valueOf(interval2) + " (interval2).");}
			interval2 += plusTime;
		}
	}
	
	/**
	 * Subtract a bit of extra time from the next timer.
	 * Must be between the two limits.
	 */
	public static void doMinus2(){
		if (interval2 > lowerLimit && interval2 <= higherLimit){
			if (logger.isDebugEnabled()){logger.debug("Substracting: " + String.valueOf(minusTime) + " from " + String.valueOf(interval2) + " (interval2).");}
			interval2 -= minusTime;
		}
	}
	
	/**
	 * clear the user comments
	 */
	public static void clearComments(){
		logger.debug("Clearing user comments.");
		comments = "";
	}
	
	/**
	 * add user comment
	 * 
	 * @param c the user comment to add
	 */
	public static void addToComments(String c){
		if (logger.isDebugEnabled()){logger.debug("Adding user comment: " + c);}
		if (comments.isEmpty()){
			comments = c;
			return;
		}
		checkAndLimitUserComments();
		comments = c + "<BR/>" + comments;
	}
	
	/**
	 * check the number of lines with user comments and limit them (remove the first one) if necessary
	 * 
	 * This method checks if the number of lines is not going to exceed the
	 * maximum number when you add one. ie.: this function limits the user
	 * comments to maxLinesUserComments minus one. Although a small effect, but
	 * it limits the memory footprint and should reduce a couple of computing
	 * cycles.
	 */
	static private void checkAndLimitUserComments(){
		String[] l_comments = comments.split("<BR/>");
		if (l_comments.length > maxLinesUserComments -1 ){
			logger.debug("Shortening user comments.");
			comments = l_comments[maxLinesUserComments - 2];
			for (int i = maxLinesUserComments - 2 -1; i >= 0; i--){
				comments = l_comments[i] + "<BR/>" + comments;
			}
		}
	}
	
	/**
	 * Set the interval and interval2 to either the first two times of anneTimes or the defatult.
	 */
	static private void setTheIntervals(){
		if (anneTimes != null && anneTimes.size()>2){
			interval = anneTimes.remove(0);
			interval2 = anneTimes.remove(0);
		} else {
			interval = DEFAULT_INTERVAL;
			interval2 = DEFAULT_INTERVAL;
			
		}
	}
}