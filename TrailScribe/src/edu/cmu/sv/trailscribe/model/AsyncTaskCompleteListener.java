package edu.cmu.sv.trailscribe.model;

//http://www.jameselsey.co.uk/blogs/techblog/extracting-out-your-asynctasks-into-separate-classes-makes-your-code-cleaner/
public interface AsyncTaskCompleteListener <T> {
    void onTaskCompleted(T result);
}
