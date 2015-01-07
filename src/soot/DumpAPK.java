package soot;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.G;
import soot.PackManager;
import soot.PatchingChain;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.Stmt;
import soot.options.Options;
import soot.util.Chain;

//There are two common types of Transformer: SceneTransformer & BodyTransformer
//In this example, we show a possible usage of SceneTransformer
public class DumpAPK extends SceneTransformer{

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
			"-p", "cg", "enabled:false"
        };
		
		G.reset();
		
		Options.v().set_src_prec(Options.src_prec_apk);
		
		//Because we set the output format as class files, you will find the apk's classes in sootOutput directory afterwords.
		Options.v().set_output_format(Options.output_format_class);
			
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.DumpAPK", new DumpAPK()));
		
        soot.Main.main(args2);

	}

	@Override
	protected void internalTransform(String arg0, Map<String, String> arg1) 
	{
		Chain<SootClass> sootClasses = Scene.v().getApplicationClasses();
		
		for (Iterator<SootClass> iter = sootClasses.iterator(); iter.hasNext(); )
		{
			SootClass sc = iter.next();
			
			String clsName = sc.getName();
			
			if (clsName.startsWith("android.support.v") || 
					clsName.endsWith("BuildConfig") || 
					clsName.endsWith(".R") ||
					clsName.contains(".R$"))
			{
				//Skipping the android framework codes
				//Since normally we are only interested in such codes that are written by user.
				continue;
			}
			
			System.out.println("---->class name is " + clsName);
			
			try
			{
				List<SootMethod> sms = sc.getMethods();
				
				for (SootMethod sm : sms)
				{
					System.out.println("---->method name is " + sm.getName());
					
					Body b = sm.retrieveActiveBody();
					
					PatchingChain<Unit> units = b.getUnits();
					
					
					for (Iterator<Unit> iterU = units.snapshotIterator(); iterU.hasNext(); )
					{
						Stmt stmt = (Stmt) iterU.next();
						
						System.out.println(stmt);
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
	}

}
