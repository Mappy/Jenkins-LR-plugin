package hudson.plugins.devloadrunner;

import org.kohsuke.stapler.DataBoundConstructor;

public class LrRepeatableTransactionConfig {

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
	 
	public float getAvgRespTimeSLA() {
		return this.avgRespTimeSLA;
	}
	
	public float getPctRestTimeSLA() {
		return this.pctRespTimeSLA;
	}
	
	public float getErrorRateSLA() {
		return this.errorRateSLA;
	}
	
	
}
