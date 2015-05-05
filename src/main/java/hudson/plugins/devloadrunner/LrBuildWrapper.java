package hudson.plugins.devloadrunner;


import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildWrapper;
import org.kohsuke.stapler.DataBoundConstructor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class LrBuildWrapper extends BuildWrapper {
	
	private String monitorLrTransacts;
	private ArrayList<LrRepeatableTransactionConfig> lrTransactsConfig;
	
	@DataBoundConstructor
	public LrBuildWrapper(String monitorLrTransacts, ArrayList<LrRepeatableTransactionConfig> lrTransactsConfig) {
		this.monitorLrTransacts = monitorLrTransacts;
		this.lrTransactsConfig = lrTransactsConfig;
	}
	
    @Override
    public Collection<? extends Action> getProjectActions(AbstractProject job) {
    	//String[] trs = new String[] {monitorLrTransacts};
    	//final LrProjectAction lpa = new LrProjectAction(job, null, monitorLrTransacts.split(",") ); //new String[] {monitorMainLrTransact});
    	final LrProjectAction lpa = new LrProjectAction(job, null, lrTransactsConfig);
    	return Arrays.asList(lpa);
    }
	
    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener)
            throws IOException, InterruptedException {
        // Return an empty environment as we are not changing anything.
        return new Environment() {};
    }    
    
/*
    public String getExecTimeout() {
        return lrExecTimeout;
    }

    public String getLrsFile() {
    	return lrsFile;
	}
*/
    public String getMonitorLrTransacts() {
    	return monitorLrTransacts;
    }
    
    public void setMonitorLrTransacts(String lrTransact) {
    	this.monitorLrTransacts = lrTransact;
    }
    
    public ArrayList<LrRepeatableTransactionConfig> getLrTransactsConfig() {
    	return lrTransactsConfig;
    }
    
    public void setLrTransactsConfig(ArrayList<LrRepeatableTransactionConfig> lrTransactsConfig) {
    	this.lrTransactsConfig = lrTransactsConfig;
    }
/*
    public String getLraFileName() {
    	return lraFileName;
    }
*/
	}