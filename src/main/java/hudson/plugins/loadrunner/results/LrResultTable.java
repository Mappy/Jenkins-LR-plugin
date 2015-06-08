/**
 * LrResultTable.java
 *
 * Data model : class storing the results for a LR test run.
 * Each transaction line from the summary is stored
 * in a LrTransactionStats.java class
 *
 * @author Yann LE VAN
 *
 */


package hudson.plugins.loadrunner.results;

import hudson.model.Action;
import hudson.model.AbstractBuild;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import hudson.plugins.loadrunner.PluginImpl;

public class LrResultTable implements Action, Serializable {
	
   /**
	 * 
	 */
	private static final long serialVersionUID = 8452395018095821678L;

	
	private String test_run_id;
	private AbstractBuild<?,?> owner;
	private ArrayList<LrTransactionStats> tr_stats_array = new ArrayList<LrTransactionStats>();
	private HashMap<String,LrTransactionStats> tr_stats_dic = new HashMap<String,LrTransactionStats>();


	public LrResultTable(String test_run_id) {
      this.test_run_id = test_run_id;
	}

	@Override
	public String getDisplayName() {
		return PluginImpl.LRT_DISPLAY_NAME;
	}
	
	@Override
	public String getIconFileName() {
		return PluginImpl.LRT_ICON_FILE_NAME;
	}
	
	@Override
	public String getUrlName() {
		return PluginImpl.LRT_URL;
	}

  

   public void setOwner(AbstractBuild<?,?> owner) {
	   this.owner = owner;
   }
   
   
	public void setRunId(String name){
		test_run_id.concat(name);
	}
   
	public void addStatToArray(LrTransactionStats lr_result_line) {
		tr_stats_array.add(lr_result_line);
	}

	public void addStatToDic(String tr_name, LrTransactionStats lr_result_line) {
		tr_stats_dic.put(lr_result_line.getName(), lr_result_line);
	}
	
	public String getRunId(){
		return test_run_id.toString();
	}
   
	public ArrayList<LrTransactionStats> getStatsArray() {
		return tr_stats_array;
	}

	public HashMap<String,LrTransactionStats> getStatsDic() {
		return tr_stats_dic;
	}
	
	public boolean ArrayExists() {
		if (tr_stats_array != null) {
			return true;
		}
		else return false;
	}
	
	public boolean DicExists() {
		if (tr_stats_dic != null) {
			return true;
		}
		else return false;
	}
	
	
	public int getStatsArrayCount() {
		return tr_stats_array.size();
	}

	public int getStatsDicCount() {
		return tr_stats_dic.size();
	}
}