import assemblerProject.Assembler;


public class Main {

	public static void main(String[] args){

		System.out.println("Hello, World!");
		String sourceFile = "SourceFiles/sourceFile1.txt";
		Assembler assemble = new Assembler(sourceFile);
		assemble.read_file();
	
		int num = 10;
		System.out.println();
		System.out.println(assemble.convertToOctal(num));
	}
}