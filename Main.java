import assemblerProject.Assembler;


public class Main {

	public static void main(String[] args){

		System.out.println("Hello, World!");
		String sourceFile = "SourceFiles/sourceFile1.txt";
		Assembler assemble = new Assembler(sourceFile);
		assemble.read_file();
	
	}
}