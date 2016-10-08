package io.github.wang_jingyi.ZiQian.sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellInteraction {

	public static String executeCommand(String[] command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = 
					new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();

	}
	
	public static void setEnvironmentVariable(String envVar){
		try {

	        // using the Runtime exec method:
	        Process p = Runtime.getRuntime().exec(envVar);

	        BufferedReader stdInput = new BufferedReader(new
	             InputStreamReader(p.getInputStream()));

	        BufferedReader stdError = new BufferedReader(new
	             InputStreamReader(p.getErrorStream()));

	        // read the output from the command
	        System.out.println("Here is the standard output of the command:\n");
	        String s;
	        while ((s = stdInput.readLine()) != null) {
	            System.out.println(s);
	        }

	        // read any errors from the attempted command
	        System.out.println("Here is the standard error of the command (if any):\n");
	        while ((s = stdError.readLine()) != null) {
	            System.out.println(s);
	        }

	        System.exit(0);
	    }
	    catch (IOException e) {
	        System.out.println("exception happened - here's what I know: ");
	        e.printStackTrace();
	        System.exit(-1);
	    }
	}

}
