/**
 * LRAparser.java
 * 
 * Build Action class : parses .asc file (LoadRunner Analysis summary file)
 * and stores the summary table (LR transactions : min/avg/max/90%/std dev./pass/stop/fail)
 * in a LrResultTable.java class 
 *
 * @author Yann LE VAN
 * 
 */

package hudson.plugins.loadrunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;

import hudson.plugins.loadrunner.results.*;



public class LRAparser implements Action {

	
	/**
    * The owner of this Action.
    */
   private final AbstractBuild<?, ?> build;
   
	/**
    * Attributes.
    */   
   private final File path_to_asc_file;
   private final BuildListener listener;
   private final LrResultTable lr_result_table = new LrResultTable(null);
   
   /**
	 *   Constructor
	 */
   public LRAparser(AbstractBuild<?, ?> build, BuildListener listener, File path_to_asc_file) {
	   this.build = build;
	   this.path_to_asc_file = path_to_asc_file;
	   this.listener = listener;
	   lr_result_table.setOwner(this.build);
   }
	

   
   
	public String getDisplayName() {
		return PluginImpl.DISPLAY_NAME;
	}

   public String getIconFileName() {
	      return PluginImpl.ICON_FILE_NAME;
	   }

   public String getUrlName() {
	      return PluginImpl.URL;
	   }

   public String getName() {
	      return PluginImpl.GRAPH_NAME;
	   }

   public synchronized AbstractBuild<?, ?> getBuild() {
	      return build;
	   }

    
    /**
 	 *   parses the LRA.asc file, regex the 'transaction_name' lines in it and stores the values in a 'LrTransactionStats' object
 	 */
   protected LrResultTable parseAllResults() throws IOException, InterruptedException {

    	if (path_to_asc_file.exists() && path_to_asc_file.isFile()) {

    		/**
    		 * Buffering the .asc file
    		 */
    		BufferedReader br = new BufferedReader(new FileReader(path_to_asc_file));
    		
			listener.getLogger().println("\n### Parsing the LRA report file ## .asc file being parsed = " + path_to_asc_file);

   		
    		/**
    		 * Construct the regex pattern to match the specific line format in the .asc file
    		 *     RowNo0=Transaction Name#Minimum#Average#Maximum#Std. Deviation#90 Percent#Pass#Fail#Stop
    		 */
    		
    		//Pattern row_any_lr_transact = Pattern.compile("RowNo[0-9]+=(" + s_all_lr_transacts + ")#([0-9.,]+)#([0-9.,]+)#([0-9.,]+)#([0-9.,]+)#([0-9.,]+)#([0-9.,]+)#([0-9.,]+)#([0-9.,]+)");
    		Pattern row_any_lr_transact = Pattern.compile("RowNo[0-9]+=([\\d\\w\\s_,.=?!\\-/+]+)#([0-9.,]+)#([0-9.,]+)#([0-9.,]+)#([0-9.,]+)#([0-9.,]+)#([0-9.,]+)#([0-9.,]+)#([0-9.,]+)");

    		String rawline = null;
    		String line = null;
    		Matcher m;
    		
    		
    		/**
    		 * Walks through the .asc file, trying to match the regex pattern for each line
    		 */
    		while ((rawline = br.readLine()) != null) {
    			
    			/*
    			 * line = rawline with painfull encoded characters removed 
    			 */
    			line = rawline.replaceAll("[^\\d\\w\\s_#,.=?!\\-/+]", "");
    			//listener.getLogger().println("## raw line in .asc :" + rawline);
    			//listener.getLogger().println("## modified line in .asc :" + line);
    			
    			m = row_any_lr_transact.matcher(line);

    			
    			/*
    			 * IF MATCH -> adds the following metrics to stats dictionnary
    			 * 				RowNo0=Transaction Name#Minimum#Average#Maximum#Std. Deviation#90 Percent#Pass#Fail#Stop
    			 *				------=m.group(1)#m.group(2)#m.group(3)#m.group(4)#m.group(5)#m.group(6)#m.group(7)#m.group(8)m.group(9)
    			 */
        		if (m.matches()) {
        			//listener.getLogger().println("## raw line matching in .asc :" + rawline);
        			//listener.getLogger().println("## line matching in .asc with painfull W$ characters removed :" + line);
        			LrTransactionStats stat_line = new LrTransactionStats(m.group(1).toString());
        	// TODO : replace "Minimum", "Average" ... labels with PluginImpl.MINI, PluginImpl.AVG ...
        			stat_line.addStat("Minimum", Float.parseFloat(m.group(2).replace(",", ".")));
        			stat_line.addStat("Average", Float.parseFloat(m.group(3).replace(",", ".")));
        			stat_line.addStat("Maximum", Float.parseFloat(m.group(4).replace(",", ".")));
        			stat_line.addStat("Std Dev", Float.parseFloat(m.group(5).replace(",", ".")));
        			stat_line.addStat("90 Percent", Float.parseFloat(m.group(6).replace(",", ".")));
        			stat_line.addStat("Pass", Float.parseFloat(m.group(7).replace(",", ".")));
        			stat_line.addStat("Fail", Float.parseFloat(m.group(8).replace(",", ".")));
        			stat_line.addStat("Stop", Float.parseFloat(m.group(9).replace(",", ".")));
            		
        			listener.getLogger().println(" ### transaction name : " + stat_line.getName().toString() + " ### metrics count : " + Integer.toString(stat_line.getCount()));
        			lr_result_table.addStatToDic(m.group(1).toString(),stat_line);
        		}
    		}
    		br.close();
    	}
    	else {
    		listener.getLogger().println(" ### NO " + path_to_asc_file + " file found"); 
    	}
    	
    	if (lr_result_table.getStatsDicCount() == 0) {
    		listener.getLogger().println(" ### /!\\ LrResultTable is an empty set  /!\\ ### ");
    		build.setResult(Result.FAILURE);
    		return null;
    	}
    	else {
    		listener.getLogger().println(" ### " + lr_result_table.getStatsDicCount() + " items found in LrResultTable");
	    	listener.getLogger().println("===================== PARSING OF THE RESULTS COMPLETED =====================");
	    	listener.getLogger().println("\n\n######################## EXITING LOADRUNNER PLUGIN #########################\n\n\n");
	    	return lr_result_table;
    		
    	}
    }
    
///////
    
}

