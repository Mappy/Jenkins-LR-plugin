/**
 * LrTransactionStats.java
 *
 * Data model : class storing the results for a specific
 * transaction name from a LR test run.
 *
 * @author Yann LE VAN
 *
 */


package hudson.plugins.loadrunner.results;

import java.util.HashMap;

public class LrTransactionStats {
	
   /**
	 * 
	 */
	
	private String transact_name;
	private HashMap<String, Float> transact_stats = new HashMap<String, Float>();

	
	public LrTransactionStats(String transact_name) {
      this.transact_name = transact_name;
    }

	public void addName(String name){
		transact_name.concat(name);
	}
   
	public void addStat(String stat_key, Float stat_value) {
		transact_stats.put(stat_key, stat_value);
	}
	
	public String getName(){
		return transact_name.toString();
	}
   
	public HashMap<String, Float> getStat() {
		return transact_stats;
	}
   
	public int getCount() {
		return transact_stats.size();
	}

}