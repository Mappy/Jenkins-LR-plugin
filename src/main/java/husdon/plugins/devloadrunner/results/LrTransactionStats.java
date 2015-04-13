package husdon.plugins.devloadrunner.results;

import java.util.HashMap;

public class LrTransactionStats {
	
   /**
	 * 
	 */
	
	private String transact_name;
	private HashMap<String, Float> transact_stats = new HashMap<String, Float>();

	//public LrTransactionStats(String transact_name, HashMap<String, Float> transact_stats) {
	public LrTransactionStats(String transact_name) {
      this.transact_name = transact_name;
      //this.transact_stats = transact_stats;
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