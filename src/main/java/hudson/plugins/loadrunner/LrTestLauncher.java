package hudson.plugins.loadrunner;


import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

//import MappyJenkinsPerfBuilder.DescriptorImpl;

import hudson.plugins.loadrunner.results.LrResultTable;




public class LrTestLauncher extends Builder {
    //private final String pathToWlrun;
    private String lrsFile;
    private String lrExecTimeout;
    private String lrAnalysisTimeout;
    //private final String monitorLrTransacts;
    private String lraFileName;
	
    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
	@DataBoundConstructor
	public LrTestLauncher(String lrsFile, String lrExecTimeout, String lrAnalysisTimeout,/*String monitorLrTransacts,*/ String lraFileName) {
	    //this.pathToWlrun = pathToWlrun;
	    this.lrsFile = lrsFile;
	    this.lrExecTimeout = lrExecTimeout;
	    this.lrAnalysisTimeout = lrAnalysisTimeout;
	    //this.monitorLrTransacts = monitorLrTransacts;
	    this.lraFileName = lraFileName;
	}
	
    public String getLrExecTimeout() {
        return lrExecTimeout;
    }
    
    public String getLrAnalysisTimeout() {
        return lrAnalysisTimeout;
    }
    
    public String getLrsFile() {
    	return lrsFile;
		}

    public String getLraFileName() {
    	return lraFileName;
    }
    
    public void setLrExecTimeout(String timeout) {
        this.lrExecTimeout = timeout;
    }
    
    public void setLrAnalysisTimeout(String timeout) {
        this.lrAnalysisTimeout = timeout;
    }
    
    public void setLrsFile(String lrsfile) {
    	this.lrsFile = lrsfile;
		}

    public void setLraFileName(String lraFile) {
    	this.lraFileName = lraFile;
    }
    
	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
			 throws InterruptedException, IOException {

		 /*
		  MyBuildAction action = new MyBuildAction(build);
		  build.addAction(action);
		  return true;
		 */

    	EnvVars envVars = new EnvVars();
    	envVars = build.getEnvironment(listener);
    	
    	String job_name = envVars.get("JOB_NAME");
    	String build_number = envVars.get("BUILD_NUMBER");
    	String build_id = envVars.get("BUILD_ID");
    	String workspace = envVars.get("WORKSPACE");
    	
    	String job_builds_dir = workspace + "\\..\\builds\\";
    	
    	listener.getLogger().println("\n######################## ENTERING LOADRUNNER PLUGIN #########################\n");
    	listener.getLogger().println(" ### ENV variables :"
    			+ "\n\tJOB_NAME : " + job_name
				+ "\n\tBUILD_NUMBER : " + build_number
				+ "\n\tBUILD_ID : " + build_id
				+ "\n\tWORKSPACE : " + workspace
				+ "\n\tjob_build_dir : " + job_builds_dir
				+ "\n\tpathToWlrun : " + getDescriptor().getPathToWlrun()
				+ "\n\tlrsFile : " + lrsFile
				+ "\n\tlraFileName : " + lraFileName
				+ "\n\tlrExecTimeout : " + lrExecTimeout
				+ "\n\tlrAnalysisTimeout : " + lrAnalysisTimeout
    			);

		LrTestExecutor lr_test_exec = new LrTestExecutor(build, listener, getDescriptor().getPathToWlrun() , lrsFile, lraFileName, lrExecTimeout, lrAnalysisTimeout);
		try {
			if (lr_test_exec.RunTest(job_builds_dir, build_id)) {
			//if (true) {
				/**
				 * Building the path to the .asc file to be parsed 
				 */
				File targetLraDir = new File(job_builds_dir + build_id + "/" + lraFileName + "/");
			   	File pathToAsc = new File(targetLraDir + "\\LRA.asc");
			    //File pathToAsc = new File("c:\\tests-auto\\test_plugin\\builds\\2015-03-23_18-42-54\\LRA\\LRA.asc");
//		    
//	    	
				/**
				 * Parsing ALL the results from the .asc file and adding the LrResultTable Action to the build
				 */
			    LRAparser lra_parser = new LRAparser(build, listener, pathToAsc);
			    LrResultTable RunResults = lra_parser.parseAllResults();
			    build.addAction(RunResults);
//
//		    
////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////// 
				/**
				 * Graph ALL transactions in List monitorLrTransacts 
				 */    	
				AbstractProject<?,?> project = build.getProject(); 
//			listener.getLogger().println("######### perform() / project.toString() : " + project.toString());
//
				//LrProjectAction lpa = new LrProjectAction(project, listener, monitorLrTransacts.split(",") );
				//lpa.setOwner(build);
//	    	//lpa.buildDataSet("geoentity_Global");
				//build.addAction(lpa);
//
//	    	
//
//	    	//project.addAction(lpa);
//////////////////////////////////////////////////////////////////////// 
////////////////////////////////////////////////////////////////////////
			}
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		return true;
	}
	 
	 
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }
    
 	@Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
    	
        private String pathToWlrun;
        private String lrsFile;
        private String lrExecTimeout;
        //private String monitorLrTransacts;
        private String lraFileName;
        

        

        /**
         * In order to load the persisted global configuration, you have to 
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }


        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "LoadRunner plugin configuration";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
        	pathToWlrun = formData.getString("pathToWlrun");
        	//lrsFile = formData.getString("lrsFile");
        	//lrExecTimeout = formData.getString("lrExecTimeout"); 
        	//monitorLrTransacts = formData.getString("monitorLrTransacts");
        	//lraFileName = formData.getString("lraFileName");
        	
            // Can also use req.bindJSON(this, formData);
            // (easier when there are many fields; need set* methods for this, like setPathToWlrun)
            save();
            return super.configure(req,formData);
        }

        /**
         * This method returns true if the global configuration says we should speak French.
         *
         * The method name is bit awkward because global.jelly calls this method to determine
         * the initial state of the checkbox by the naming convention.
         */
	        
        public String getPathToWlrun() {
            return pathToWlrun;
        }
        
        public void setPathToWlrun(String path) {
            this.pathToWlrun = path;
        }
        

        
	}

}
