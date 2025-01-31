import java.io.File;

import assemblerProject.Assembler;


public class Main {

	public static void main(String[] args){

        File folder = new File("SourceFiles/");
        //retrieve all source files
        File[] files = folder.listFiles();
		Assembler assemble = null;
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) { // file, not a directory
                    System.out.println("Translating File: " + file.getName());
					String sourceFileDestination = "SourceFiles/" + file.getName();
					String loadFileDestination = "OutputFiles/" + file.getName().replace(".txt", "_load.txt");
					String listFileDestination = "OutputFiles/" + file.getName().replace(".txt", "_listing.txt");
					assemble = new Assembler(sourceFileDestination, loadFileDestination, listFileDestination);
					assemble.firstPass();
					assemble.secondPass();
					assemble = null;
				}
			}
        }else {
			System.out.println("SourceFiles/ folder does not exist or is empty.");
        }
    } 	
}