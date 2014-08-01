/* 
 * Copyright (c) 2014, TrailScribe Team.
 * This content is released under the MIT License. See the file named LICENSE for details.
 */
package edu.cmu.sv.trailscribe.model;

//Extracting out AsyncTasks into separate classes makes code cleaner
//http://www.jameselsey.co.uk/blogs/techblog/extracting-out-your-asynctasks-into-separate-classes-makes-your-code-cleaner/
public interface AsyncTaskCompleteListener <T> {
	void onTaskCompleted(T result);
}
