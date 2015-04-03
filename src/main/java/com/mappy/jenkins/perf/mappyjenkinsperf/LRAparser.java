package com.mappy.jenkins.perf.mappyjenkinsperf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.BuildListener;

import com.mappy.jenkins.perf.mappyjenkinsperf.results.*;



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


    /*
    static float getAvgRespTime() {
    	return (float)1.23;
    }
	*/
    
    /**
 	 *   parses the LRA.asc file, regex the 'transaction_name' lines in it and stores the values in a 'LrTransactionStats' object
 	 */
   protected LrResultTable parseAllResults() throws IOException, InterruptedException {

    	if (path_to_asc_file.exists() && path_to_asc_file.isFile()) {

    		/**
    		 * Buffering the .asc file
    		 */
    		BufferedReader br = new BufferedReader(new FileReader(path_to_asc_file));
    		

   		
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
    			
    			line = rawline.replaceAll("[^\\d\\w\\s_#,.=?!\\-/]", "");
    			//listener.getLogger().println("## raw line in .asc :" + rawline);
    			//listener.getLogger().println("## modified line in .asc :" + line);
    			
    			m = row_any_lr_transact.matcher(line);
    			//int i = 0;
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
            		
        			//listener.getLogger().println("## transaction name :" + stat_line.getName().toString());
        			//listener.getLogger().println("## metrics count :" + Integer.toString(stat_line.getCount()));
        			
        			//LrTableResults.add(i, stat_line);
        			
        			lr_result_table.addStatToDic(m.group(1).toString(),stat_line);
        			//++i;
        		}

    		}
    		
    		br.close();
    	}
    	else {
    		//listener.getLogger().println("### NO " + path_to_asc_file + " file found"); 
    	}
    	//listener.getLogger().println("### " + lr_result_table.getStatsDicCount() + " items found in LrResultTable"); 
    	return lr_result_table;
    }
    
///////
    
}

