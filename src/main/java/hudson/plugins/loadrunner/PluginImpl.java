/**
 * PluginImpl.java
 *
 * Stores generic configuration values such as graphs/pages titles
 * or URLs and maximum builds to be displayed on graphs.
 *
 * @author Yann LE VAN
 *
 */

package hudson.plugins.loadrunner;


import hudson.Plugin;

/**
 * Entry point of LoadRunner plugin.
 */
public class PluginImpl extends Plugin {
   public static final String DISPLAY_NAME = "LoadRunner Test Automation";
   public static final String GRAPH_NAME = "GRAPH_NAME";
   public static final String URL = "mappy-jenkins-perf";
   public static final String ICON_FILE_NAME = "/plugin/data-plugin/icons/report.png";
   
   public static final String LPA_DISPLAY_NAME = "Performance Graphs";
   public static final String LPA_NAME = "NAME";
   public static final String LPA_URL = "perfGraphs";
   public static final String LPA_ICON_FILE_NAME = "/plugin/loadrunner/icons/area_chart_128.png";
   public static final int LPA_MAX_BUILDS_IN_GRAPHS = 20; 
   
   public static final String LRT_DISPLAY_NAME = null;
   public static final String LRT_NAME = null;
   public static final String LRT_URL = null;
   public static final String LRT_ICON_FILE_NAME = null;
   
   
   public static final String LTL_DISPLAY_NAME = "LoadRunner plugin configuration";
   public static final String LBWD_CONFIG_DISPLAY_NAME = "Publish LR results and display performance graphs over builds";
   
}
