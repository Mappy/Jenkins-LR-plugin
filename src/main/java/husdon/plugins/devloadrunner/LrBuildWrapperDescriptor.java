package husdon.plugins.devloadrunner;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildWrapperDescriptor;


/**
 * 
 * Author: 
 * Date: 
 */
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