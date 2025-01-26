package assemblerProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Assembler {
    
    String filename;

    public Assembler(String filename) {
        this.filename = filename;
    }


    public void read_file() {
        //filepath to source file with instructions in it

        try (Scanner scanner = new Scanner(new File(this.filename))) {
            while (scanner.hasNextLine()) {
                // Read and process each line
                String line = scanner.nextLine();
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
    }


}
