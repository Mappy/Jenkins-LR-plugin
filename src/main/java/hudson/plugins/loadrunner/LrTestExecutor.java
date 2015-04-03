package hudson.plugins.loadrunner;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.BuildListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class LrTestExecutor implements Action {
	
	/**
    * The owner of this Action.
    */
   private final AbstractBuild<?, ?> build;
   
	/**
    * Attributes.
    */   
   private final BuildListener listener;
   private final String pathToWlrun;
   private final String lrsFile;
   private final String lraFileName;
   private final String lrExecTimeout;

   
   /**
	 *   Constructor
	 */
   public LrTestExecutor(AbstractBuild<?, ?> build, BuildListener listener,
		   String pathToWlrun, String lrsFile, String lraFileName, String lrExecTimeout) {
	   this.build = build;
	   this.listener = listener;
	   this.pathToWlrun = pathToWlrun;
	   this.lrsFile = lrsFile;
	   this.lraFileName = lraFileName;
	   this.lrExecTimeout = lrExecTimeout;
   }

   /**
	 *   model.Action methods to display (or not) this Action in Jenkins left menu
	 */
   @Override
	public String getIconFileName() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public String getUrlName() {
		return null;
	}

	
	
   /**
	 *   Utils methods
	 */
    private ArrayList<String> splitLrTransactionsList(String monitor_lr_transact_from_jelly) { 
    	ArrayList<String> list_transact_name = new ArrayList<String>();
    	int index=0;
    	for (String item:monitor_lr_transact_from_jelly.split(",")) {
    		list_transact_name.add(index, item);
    		++index;
    	}
    	
    	return list_transact_name;
    }
        
    private void removeDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (File aFile : files) {
                    removeDirectory(aFile);
                }
            }
            dir.delete();
        } else {
            dir.delete();
        }
    }
    
    private void cleanDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (File aFile : files) {
                    removeDirectory(aFile);
                }
            }
        }
    }




    

    public boolean RunTest(String job_builds_dir, String build_id) throws IOException, InterruptedException {

    	
    	File temp_dir = new File(job_builds_dir + "../../_temp_/");
    	File temp_lra_dir = new File(job_builds_dir + "../../_temp_/" + lraFileName + "/");
    	File temp_html_dir = new File(job_builds_dir + "../../_temp_/HTML/");
    	File target_lra_dir = new File(job_builds_dir + build_id + "/" + lraFileName + "/");
    	File target_html_dir = new File(job_builds_dir + build_id + "/HTML/");

   	/*
    	listener.getLogger().println("\n### I could get theses ENV variables through the code : \n\tJOB_NAME : " + job_name
    								+ "\n\tBUILD_NUMBER : " + build_number
    								+ "\n\tBUILD_ID : " + build_id
    								+ "\n\tWORKSPACE : " + workspace
    								+ "\n\tjob_build_dir : " + job_builds_dir
    								+ "\n\ttemp_dir : " +  temp_dir
    								+ "\n\ttemp_lra_dir : " +  temp_lra_dir
    								+ "\n\ttemp_html_dir : " +  temp_html_dir
    								+ "\n\ttarget_lra_dir : " +  target_lra_dir
    								+ "\n\ttarget_html_dir : " +  target_html_dir);
 	*/
    	listener.getLogger().println("\n\t\tlist temp_dir : \n\t" + StringUtils.join(temp_dir.list(), "\n\t"));
    	
    	/**
    	 * Building the Wlrun ProcessBuilder with desired command line options 
    	 */
    	ProcessBuilder wlrun = new ProcessBuilder("\""  + pathToWlrun
    													+ "\\wlrun\"", " -Run -TestPath " + lrsFile
    													+ " -ResultName " + job_builds_dir + build_id + "\\LRR"
    													+ " -ResultLocation " + job_builds_dir + build_id
    													+ " -InvokeAnalysis");
    	/**
    	 * ProcessBuilder to kill any wlrun.exe running process on the host 
    	 */
    	ProcessBuilder kill_wlrun = new ProcessBuilder("c:\\windows\\system32\\cmd.exe","/C taskkill /F /IM wlrun.exe");
    	
    	/**
    	 * ProcessBuilder to kill any AnalysisUI.exe running process on the host 
    	 */
    	ProcessBuilder kill_analysis = new ProcessBuilder("c:\\windows\\system32\\cmd.exe","/C taskkill /F /IM AnalysisUI.exe");
    	
    	listener.getLogger().println("## kill_wlrun.command() : " + kill_wlrun.command());
    	listener.getLogger().println("## kill_analysis.command() : " + kill_analysis.command());

    	
    	wlrun.redirectErrorStream(true);
 
   	
    	try {
    		
    		Date now = new Date();
    		String out_wlrun = "";
    		String out_kill_wlrun = "INIT";
    		String out_kill_analysis = "INIT";
    		int taskill_timeout = 3;
    		
    		Boolean dead_or_alive = true;
    		int cpt_time_ms = 0;
    		int loop_wait_ms = 5000;

    		
    		//CLEANUP JOB _temp_ dir
    		if (temp_dir.exists()) {
    			cleanDirectory(temp_dir);
    		}
    		else {
    			temp_dir.mkdir();
    		}
    		
    		
    		//CLEANUP running WLRUN processes
    		Process p_kill_wlrun = kill_wlrun.start();
    		//while (p_kill_wlrun.isAlive() && p_kill_wlrun.waitFor(Long.valueOf(taskill_timeout), TimeUnit.SECONDS)) {
    		//while (p_kill_wlrun.isAlive()) {
	   		BufferedReader kill_wlrun_StrmRead = new BufferedReader(new InputStreamReader(p_kill_wlrun.getInputStream()));
    		while (out_kill_wlrun != null) {
			   		out_kill_wlrun = kill_wlrun_StrmRead.readLine();
			   		Thread.sleep(loop_wait_ms);
			   		//if (out_wlrun != null) {
 			   			listener.getLogger().println("### taskkill_wlrun command line output : " + out_kill_wlrun);
 			   	//	}
			   	//	else {
			   	//		break;
			   	//	}
			   			
    		}
    		
    		//CLEANUP running ANALYSIS processes    		
    		Process p_kill_analysis = kill_analysis.start();
    		//while (p_kill_analysis.isAlive() && p_kill_analysis.waitFor(Long.valueOf(taskill_timeout), TimeUnit.SECONDS)) {
    		while (p_kill_analysis.isAlive()) {
		   		BufferedReader kill_analysis_StrmRead = new BufferedReader(new InputStreamReader(p_kill_analysis.getInputStream()));
		   		out_kill_analysis = kill_analysis_StrmRead.readLine();
		   		Thread.sleep(loop_wait_ms);
		   		if (out_kill_analysis != null) {
			   			listener.getLogger().println("### taskkill_analysisUI command line output : " + out_kill_analysis);
			   		}
		   		else {
		   			break;
		   		}
			}
    		
    		
    		// START wlrun.exe
    		Process p_wlrun = wlrun.start();
    		
    		    		
    		listener.getLogger().println("\n## Path to Wlrun.exe configured in global.jelly : " + pathToWlrun);
    		//listener.getLogger().println("\n## Command launching LR test : " + getDescriptor().getWlrunPath() + "wlrun -Run -TestPath " + lrs_file + " -ResultName \"C:\\Temp\\YLV_tmp\\wlrun\\LRR\" -ResultLocation \"C:\\Temp\\YLV_tmp\\wlrun\\\" -InvokeAnalysis");
    		listener.getLogger().println("\n## String timeout : " + lrExecTimeout + " / Long timeout : " + Long.valueOf(lrExecTimeout));
    		listener.getLogger().println("\n## Test run STARTED at " + now.toString());
    		
    		// MANAGE wlrun timeout
    		try {
    				//listener.getLogger().println("### ---- 0 ----");
    				//while (process.waitFor(Long.valueOf(lr_exec_timeout), TimeUnit.SECONDS)) {
    				while (dead_or_alive) {
	 			   		
    					listener.getLogger().println("### cpt_time_ms : " + cpt_time_ms + " ## 1000*Integer.valueOf(lr_exec_timeout) : " + 1000*Integer.valueOf(lrExecTimeout));
    					if (cpt_time_ms < 1000*Integer.valueOf(lrExecTimeout)) {
    						
	    					dead_or_alive = p_wlrun.isAlive();
		 			   		listener.getLogger().println("\n### " + now.toString() + " ## dead_or_alive : " + dead_or_alive );
		 			   		Thread.sleep(loop_wait_ms);
		 			   		BufferedReader inStreamReader = new BufferedReader(new InputStreamReader(p_wlrun.getInputStream()));
	    		//			while(outout != null) {
	    		//				//do something with commandline output.
	    		//				outout = inStreamReader.readLine();
	    		//				listener.getLogger().println("### " + outout);
	    		//			}
	    		
		 			   		//process.destroyForcibly();
		 			   		
		 			   		//outout = inStreamReader.readLine();
		 			   		if (out_wlrun != null && out_wlrun.length() != 0) {
		 			   			listener.getLogger().println("### Wlrun command line output : " + out_wlrun);
		 			   		}
	 			   		
   					
		 			   		cpt_time_ms += loop_wait_ms;
    					}
    					else {
    						p_wlrun.destroy();
    						break;
    					}
	 			   		
    				}

					///////////////////////////
    				
    				// WAIT FOR LRA results directory to be created
    				while (!temp_lra_dir.exists()) {
    					listener.getLogger().println(" ## LRA directory not created yet");
    					Thread.sleep(1000);
 			   		}
 			   		
    				Long right_now = System.currentTimeMillis();
					listener.getLogger().println(" ## LRA results directory is created. LastModified : " + temp_lra_dir.lastModified() + "\t currTimeMs : " + right_now);					
					
					
					// WAIT for LRA results generation to be completed
					while ( temp_lra_dir.exists() && ((right_now - temp_lra_dir.lastModified()) < 5000) ) {
 			   			listener.getLogger().println(" ## LRA results directory are being generated. LastModified : " + temp_lra_dir.lastModified() + "\t currTimeMs : " + right_now);
 			   			Thread.sleep(loop_wait_ms);
 			   			right_now = System.currentTimeMillis();
 			   		}
					listener.getLogger().println(" ## Generation of LRA results directory is complete");
					///////////////////////////
					
					// WAIT FOR HTML results directory to be created
    				while (!temp_html_dir.exists()) {
    					listener.getLogger().println("\n## HTML directory not created yet");
    					Thread.sleep(1000);
 			   		}
 			   		
    				right_now = System.currentTimeMillis();
					listener.getLogger().println("\n## HTML results directory is created. LastModified : " + temp_html_dir.lastModified() + "\t currTimeMs : " + right_now);					
					
					
					// WAIT for HTML results generation to be completed
					while ( temp_html_dir.exists() && ((right_now - temp_html_dir.lastModified()) < 5000) ) {
 			   			listener.getLogger().println("\n## LRA results directory are being generated. LastModified : " + temp_html_dir.lastModified() + "\t currTimeMs : " + right_now);
 			   			Thread.sleep(loop_wait_ms);
 			   			right_now = System.currentTimeMillis();
 			   		}
					listener.getLogger().println(" ## Generation of LR HTML report is complete");

					///////////////////////////
					
					// MOVE LRA and HTML directories from TEMP to BUILD_ID location
					
					temp_lra_dir.renameTo(target_lra_dir);
					temp_html_dir.renameTo(target_html_dir);
					
					
    			} catch (InterruptedException e1) {
    				listener.getLogger().println("Test runtime ERROR : " + e1.toString());
    			}
    		
    	}
    	catch (IOException e) {
    		listener.getLogger().println("Scenario runtime ERROR : " + e.toString());
    	};

    	
    	listener.getLogger().println("\n\n================================================ TEST RUN COMPLETED ================================================\n\n");
    	
    	return true;
    }
    
    
    
    
    
    
    
    
    
    
    
}
