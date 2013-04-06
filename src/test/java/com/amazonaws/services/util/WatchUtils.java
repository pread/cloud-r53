package com.amazonaws.services.util;

import junit.framework.Assert;
import org.springframework.util.StopWatch;

public class WatchUtils {

    private static StopWatch watch = null;
    private static final String ASSERT_MSG = "StopWatch should be instantiated with resetAllTasks()";
	
  /**
	* creates the Task with name specified
	* @param taskName
	*/
    public static void startTask(String taskName)
	{
	    Assert.assertNotNull(ASSERT_MSG, watch);
	    watch.start(taskName);
	}
    
    /**
     * End's the current task 
     *
     */
    public static void endTask()
    {
      Assert.assertNotNull(ASSERT_MSG, watch);
      watch.stop();
    }
	  
    /*
     * reset's all Old task and create the fresh StopWatch
     */
    public static void resetAllTasks()
    {
      watch = new StopWatch("-- DEBUGGING --");
    }
    
    /**
     * prints the summary of Time consumed in formatted manner
     * @return
     */
    public static String getTaskSummary()
    {
      Assert.assertNotNull(ASSERT_MSG, watch);
      return watch.prettyPrint();
    }
    
}
