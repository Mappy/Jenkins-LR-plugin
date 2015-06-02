/**
 * LrBuildWrapper.java
 * 
 * Extends Jenkins' BuildWrapper to implement a pre-build step 
 * consisting in parsing the config form LrBuildWrapper/config.jelly
 * and setting the LR transactions to be graphed post-build.
 *
 * @author Yann LE VAN
 * 
 */

package hudson.plugins.loadrunner;


import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildWrapper;
import org.kohsuke.stapler.DataBoundConstructor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class LrBuildWrapper extends BuildWrapper {
	
	private ArrayList<LrRepeatableTransactionConfig> lrTransactsConfig;
	
	@DataBoundConstructor
	public LrBuildWrapper(ArrayList<LrRepeatableTransactionConfig> lrTransactsConfig) {
		this.lrTransactsConfig = lrTransactsConfig;
	}
	
    @Override
    public Collection<? extends Action> getProjectActions(AbstractProject job) {
    	final LrProjectAction lpa = new LrProjectAction(job, null, lrTransactsConfig);
    	return Arrays.asList(lpa);
    }
	
    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener)
            throws IOException, InterruptedException {
        // Return an empty environment as we are not changing anything.
        return new Environment() {};
    }    
    
    
    public ArrayList<LrRepeatableTransactionConfig> getLrTransactsConfig() {
    	return lrTransactsConfig;
    }
    
    public void setLrTransactsConfig(ArrayList<LrRepeatableTransactionConfig> lrTransactsConfig) {
    	this.lrTransactsConfig = lrTransactsConfig;
    }

}