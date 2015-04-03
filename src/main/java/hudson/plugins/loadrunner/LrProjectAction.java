package hudson.plugins.loadrunner;


import java.io.IOException;



import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.ProminentProjectAction;
import hudson.model.AbstractProject;
import hudson.util.ChartUtil;



import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.graphics2d.svg.*;






import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.plugins.loadrunner.results.LrResultTable;




public class LrProjectAction implements Action, ProminentProjectAction {
   

/**
    * The owner of this action.
    */
	private final /*transient*/ AbstractProject<?, ?> project;
	private /*transient*/ AbstractBuild<?,?> owner;
	private final /*transient*/ BuildListener listener;
	private final /*transient*/ String[] lr_transact_list;

	/**
	 *   Constructor
	 */
   public LrProjectAction(AbstractProject<?,?> project, BuildListener listener,  String[] lr_transact_list) {
	   this.project = project;
	   this.listener = listener;
	   this.lr_transact_list = lr_transact_list;
	   
   }
   


	public String getDisplayName() {
		return PluginImpl.LPA_DISPLAY_NAME;
	}

   public String getIconFileName() {
	      return PluginImpl.LPA_ICON_FILE_NAME;
	   }

   public String getUrlName() {
	      return PluginImpl.LPA_URL;
	   }
   
   public String getName() {
	      return PluginImpl.LPA_NAME;
	   }
   
   public boolean isFloatingBoxActive() {
       return true;
   }
   
   public boolean isGraphActive () {
	   return true;
   }
   
   public String getGraphName() {
       return getDisplayName();
   }
   
   public void setOwner(AbstractBuild<?,?> owner) {
	   this.owner = owner;
   }
   
   /**
    * Getter for property 'project'.
    *
    * @return Value for property 'project'.
    */
   public AbstractProject<?, ?> getProject() {
      return project;
   }
    
   protected Class<LRAparser> getLRAparserClass() {
	   return LRAparser.class;
	}
   
   protected Class<LrResultTable> getLrResultTableClass() {
	   return LrResultTable.class;
	}

/*	protected Class<DataBuildAction> getBuildActionClass() {
		return DataBuildAction.class;
	}
*/


   
/*   
   @SuppressWarnings("deprecation")
   public String getGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
	   SVGGraphics2D drawing = new SVGGraphics2D(getGraphWidth(), getGraphHeight());
	   drawing.fill(new Rectangle(10, 10, 400, 300));
	   
	   listener.getLogger().println(" ### IN DA ProjectGraph.getGraph() ###");

	   File svg = new File("c:/tests-auto/_temp_/try.svg");

	   
	   JFreeChart respTimesChart = new JFreeChart(null);
	   respTimesChart.draw(drawing, null);
	   
	   

	   FileOutputStream f = new FileOutputStream(svg.getAbsoluteFile());
	   try {
		   f.write(drawing.getSVGElement().getBytes());
	   }
	   finally {
		   f.close();
	   }
	   
	   listener.getLogger().println("## getGraph() : svg.toURI() = " + svg.toURI() );
	   listener.getLogger().println("## getGraph() : rsp.toString() = " + rsp.toString());
	   
	   ChartUtil.generateGraph(req, rsp, respTimesChart, getGraphWidth(), getGraphHeight());
	   
	   return rsp.toString();	//svg.toURI();
   }
*/	
 
  /*
   public JFreeChart getGraph(final StaplerRequest req, StaplerResponse rsp) throws IOException, InterruptedException {
	    final JFreeChart respTimeChart = ChartFactory.createLineChart(
	            "geoentity_Global", // charttitle
	            "build", // unused
	            "seconds", // range axis label
	            this.buildRespTimeDataset("geoentity_Global"), // data
		    	PlotOrientation.VERTICAL, // orientation
	            true, // include legend
	            true, // tooltips
	            false // urls
	            );
	    
	    return respTimeChart;

   }
*/
   
   
   /**
    * Generates the graph that shows Response Times Performance Indicators for a given transaction
    * @param req -
    * @param rsp -
    * @throws IOException -
    */
   @SuppressWarnings("deprecation")
   public void doRespTimeGraph(final StaplerRequest req, StaplerResponse rsp) throws IOException, InterruptedException {
	   
    final JFreeChart respTimeChart = ChartFactory.createLineChart(
    			lr_transact_list[0],//"geoentity_Global", // charttitle
	            "build", // unused
	            "seconds", // range axis label
	            this.buildRespTimeDataset(lr_transact_list[0]), // data
		    	PlotOrientation.VERTICAL, // orientation
	            true, // include legend
	            true, // tooltips
	            false // urls
	            );
    Plot plot = respTimeChart.getPlot();
    //plot.setSeriesRenderingOrder(SeriesRenderingOrder.REVERSE);
    
	ChartUtil.generateGraph(req, rsp, respTimeChart ,getGraphWidth(), getGraphHeight());
	
   }

   /**
    * Generates the graph that shows Response Times Performance Indicators for a given transaction
    * @param req -
    * @param rsp -
    * @throws IOException -
    */
   @SuppressWarnings("deprecation")
   public void doErrorRateGraph(final StaplerRequest req, StaplerResponse rsp) throws IOException, InterruptedException {
	   
    final JFreeChart errorRateChart = ChartFactory. createBarChart(
    			lr_transact_list[0], // "geoentity_Global", // charttitle
	            "build", // unused
	            "%", // range axis label
	            this.buildErrorRateDataset(lr_transact_list[0]), //"geoentity_Global"), // data
		    	PlotOrientation.VERTICAL, // orientation
	            true, // include legend
	            true, // tooltips
	            false // urls
	            );
    
  
	ChartUtil.generateGraph(req, rsp, errorRateChart ,getGraphWidth(), getGraphHeight());
	
   }
   
   
   /**
    * Builds the DataSet to graph Resp Time for a given LR transaction 
    * @param lrTransact
    * @throws IOException - InterruptedException
    */
   public DefaultCategoryDataset buildRespTimeDataset(String lrTransact) throws IOException, InterruptedException {
	//int runid = getProject().getLastCompletedBuild().number;
	final DefaultCategoryDataset buffer = new DefaultCategoryDataset();
	final DefaultCategoryDataset graphDataset = new DefaultCategoryDataset();
	
	int idx = 0;
	for (AbstractBuild<?, ?> build = getProject().getLastBuild(); build != null; build = build.getPreviousBuild()) {

		LrResultTable action = build.getAction(LrResultTable.class);
    	if (action != null && /*action.DicExists()*/ action.getStatsDic().get(lrTransact) != null && idx < PluginImpl.LPA_MAX_BUILDS_IN_GRAPHS) {
    		
    		buffer.addValue(action.getStatsDic().get(lrTransact).getStat().get("Average").doubleValue(), "Average", Integer.toString(build.number));
    		buffer.addValue(action.getStatsDic().get(lrTransact).getStat().get("90 Percent").doubleValue(), "90 Percent", Integer.toString(build.number));
    		
    		++idx;
    	}
    	else {
    		//listener.getLogger().println(" ### null Action OR null StatsDic in LrResultTable for build " + build.number);
    	}
     }
	
	/*
	 * Reverse Dataset Order
	 */
	int i = buffer.getColumnCount() - 1;
    while (i >= 0) {
    	graphDataset.addValue(buffer.getValue(0,i), buffer.getRowKey(0), buffer.getColumnKey(i));    	
    	graphDataset.addValue(buffer.getValue(1,i), buffer.getRowKey(1), buffer.getColumnKey(i));
    	i--;
    }
    
	return graphDataset;
    
   }
   
   
   /**
    * Builds the DataSet to graph Resp Time for a given LR transaction 
    * @param lrTransact
    * @throws IOException - InterruptedException
    */
   public DefaultCategoryDataset buildErrorRateDataset(String lrTransact) throws IOException, InterruptedException {
	//int runid = getProject().getLastCompletedBuild().number;
	final DefaultCategoryDataset buffer = new DefaultCategoryDataset();
	final DefaultCategoryDataset errorRateDataset = new DefaultCategoryDataset();

	int idx = 0;

	for (AbstractBuild<?, ?> build = getProject().getLastBuild(); build != null; build = build.getPreviousBuild()) {

		LrResultTable action = build.getAction(LrResultTable.class);
    	if (action != null && /*action.DicExists()*/ action.getStatsDic().get(lrTransact) != null && idx < PluginImpl.LPA_MAX_BUILDS_IN_GRAPHS) {
    		double passTr = action.getStatsDic().get(lrTransact).getStat().get("Pass").doubleValue();
    		double failTr = action.getStatsDic().get(lrTransact).getStat().get("Fail").doubleValue();
    		double stopTr = action.getStatsDic().get(lrTransact).getStat().get("Stop").doubleValue();
    		double errorRate = 100*(failTr + stopTr) / (passTr + failTr + stopTr);
    		buffer.addValue(errorRate, "Error Rate", Integer.toString(build.number));
    		++idx;
    	}
    	else {
    		//listener.getLogger().println(" ### null Action OR null StatsDic in LrResultTable for build " + build.number);
    	}
     }	
	
	
	/*
	 * Reverse Dataset Order
	 */
	int i = buffer.getColumnCount() - 1;
    while (i >= 0) {
    	errorRateDataset.addValue(buffer.getValue(0,i), buffer.getRowKey(0), buffer.getColumnKey(i));
    	i--;
    }
    
	return errorRateDataset;
    
   }
   
   
   
   /**
    * Getter for property 'graphWidth'.
    *
    * @return Value for property 'graphWidth'.
    */
   public int getGraphWidth() {
      return 600;
   }

   /**
    * Getter for property 'graphHeight'.
    *
    * @return Value for property 'graphHeight'.
    */
   public int getGraphHeight() {
      return 400;
   }


   public String[] getLrTransactList() {
	   return lr_transact_list;
   }

   
}
