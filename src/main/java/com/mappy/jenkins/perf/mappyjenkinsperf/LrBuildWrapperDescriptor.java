package com.mappy.jenkins.perf.mappyjenkinsperf;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.ProminentProjectAction;
import hudson.tasks.BuildWrapperDescriptor;


/**
 * This Jenkins Extension is the central extension point for including the release plugin
 * into Jenkins. It tells Jenkins which class is the implementation of this BuildWrapper
 * and takes care of loading it's configuration.
 *
 * On the job configuration view the getDisplayName returns the text to be
 * used in the "Build environment" section, to allow enabling of the ReleaseBuildWrapper,
 * which then takes care of wrapping all the fun stuff around a normal Maven build.
 *
 * This is also why isApplicable only returns true, if the project is a Maven project.
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