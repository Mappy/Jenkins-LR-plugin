package hudson.plugins.devloadrunner;


import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildWrapper;
import org.kohsuke.stapler.DataBoundConstructor;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class LrBuildWrapper extends BuildWrapper {
	
	private /*transient*/ String monitorLrTransacts;
	
	@DataBoundConstructor
	public LrBuildWrapper(String monitorLrTransacts) {
		this.monitorLrTransacts = monitorLrTransacts;
	}
	
    @Override
    public Collection<? extends Action> getProjectActions(AbstractProject job) {
    	//String[] trs = new String[] {monitorLrTransacts};
    	final LrProjectAction lpa = new LrProjectAction(job, null, monitorLrTransacts.split(",") ); //new String[] {monitorMainLrTransact});
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
/*
    public String getLraFileName() {
    	return lraFileName;
    }
*/
	}