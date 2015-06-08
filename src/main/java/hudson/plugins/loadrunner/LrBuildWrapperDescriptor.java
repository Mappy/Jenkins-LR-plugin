/**
 * LrBuildWrapperDescriptor.java
 * 
 * BuildWrapperDescriptor
 *
 * @author Yann LE VAN
 *
 */


package hudson.plugins.loadrunner;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildWrapperDescriptor;


@Extension
public class LrBuildWrapperDescriptor extends BuildWrapperDescriptor{


		public LrBuildWrapperDescriptor() {
	    super(LrBuildWrapper.class);
	    // Load the persisted properties from file.
	        load();
	    }
	 
	    @Override
	    public boolean isApplicable(AbstractProject<?, ?> item) {
	        return true; //(item instanceof ProminentProjectAction);
	    }
	 
	    @Override
	    public String getDisplayName() {
	        return PluginImpl.LBWD_CONFIG_DISPLAY_NAME;
	    }
	 
}