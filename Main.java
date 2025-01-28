import assemblerProject.Assembler;


public class Main {

	public static void main(String[] args){

		String sourceFile = "SourceFiles/sourceFile1.txt";
		Assembler assemble = new Assembler(sourceFile);
		assemble.first_pass();
	}
}