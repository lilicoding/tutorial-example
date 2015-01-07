package soot;

import java.util.Iterator;
import java.util.Map;

import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;

/**
 * CallGraph, if method1 calls method2, then you will get an edge from method1 to method2
 *
 */
public class CallGraphDumper extends SceneTransformer {
    public static void main(String[] args) 
    {
    	String apkPath = "testapps/AlarmMe.apk";
		String androidJars = "android-platforms";
		
		String[] args2 =
        {
            "-android-jars", androidJars,
            "-process-dir", apkPath,
            "-ire",
            "-pp",
            "-allow-phantom-refs",
            "-w",
			"-p", "cg", "enabled:true"    //enable call graph
        };
		
		G.reset();
		
		Options.v().set_src_prec(Options.src_prec_apk);
		
		Options.v().set_output_format(Options.output_format_class);
			
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.CallGraphDumper", new CallGraphDumper()));
		
        soot.Main.main(args2);
    }

    protected void internalTransform(String phaseName, Map<String, String> options)
    {
        CallGraph cg = Scene.v().getCallGraph();

        Iterator<Edge> it = cg.listener();
        while( it.hasNext() ) 
        {
        	soot.jimple.toolkits.callgraph.Edge e = (soot.jimple.toolkits.callgraph.Edge) it.next();

            StringBuilder sb = new StringBuilder();
            sb.append("src: " + e.src() + "\n");
            sb.append("srcStmt: " + e.srcStmt() + "\n");
            sb.append("kind: " + e.kind() + "\n");
            sb.append("tgt: " + e.tgt() + "\n");
            sb.append("\n");
            
            System.out.println(sb.toString());
        }
    }
}
