/**
 * LrRepeatableTransactionConfig.java
 *
 * Class instantiated to map each line of parameters
 * within LrBuildWrapper/config.jelly
 *
 * @author Yann LE VAN
 *
 */


package hudson.plugins.loadrunner;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;

import org.kohsuke.stapler.DataBoundConstructor;

public class LrRepeatableTransactionConfig extends AbstractDescribableImpl<LrRepeatableTransactionConfig>{

	private String lrTransactName;
	private boolean isMainTransact;
	private float avgRespTimeSLA;
	private float pctRespTimeSLA;
	private float errorRateSLA;
	
	@DataBoundConstructor
	public LrRepeatableTransactionConfig (String lrTransactName, boolean isMainTransact, float avgRespTimeSLA, float pctRespTimeSLA, float errorRateSLA) {
		this.lrTransactName = lrTransactName;
		this.isMainTransact = isMainTransact;
		this.avgRespTimeSLA = avgRespTimeSLA;
		this.pctRespTimeSLA = pctRespTimeSLA;
		this.errorRateSLA = errorRateSLA;
	}
	 
	public String getLrTransactName() {
		return this.lrTransactName;
	}
	
	public boolean isMainTransact() {
		return this.isMainTransact;
	}
	
	public boolean getIsMainTransact() {
		return this.isMainTransact;
	}
	
	public float getAvgRespTimeSLA() {
		return this.avgRespTimeSLA;
	}
	
	public float getPctRespTimeSLA() {
		return this.pctRespTimeSLA;
	}
	
	public float getErrorRateSLA() {
		return this.errorRateSLA;
	}

	
    @Extension
    public static class DescriptorImpl extends Descriptor<LrRepeatableTransactionConfig> {
        public String getDisplayName() { return ""; }
    }	
	
}
