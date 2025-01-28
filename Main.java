import assemblerProject.Assembler;


public class Main {

	public static void main(String[] args){

		String filename = "File1";
		String sourceFileDestination = "SourceFiles/source" + filename + ".txt";
		String loadFileDestination = "OutputFiles/load" + filename + ".txt";
		String listFileDestination = "OutputFiles/list" + filename + ".txt";

		Assembler assemble = new Assembler(sourceFileDestination, loadFileDestination, listFileDestination);
		assemble.firstPass();
		assemble.secondPass();
	}
}