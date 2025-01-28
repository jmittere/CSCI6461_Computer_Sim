package assemblerProject;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Assembler {
    
    String sourceFilename;
    String loadFilename;
    String listFilename;
    //stores labels contained within the source file
    private HashMap<String, Integer> labels; //value of each label is the address that the label occurs at in DECIMAL
    private HashMap<String, String> opCodes;

    public Assembler(String sourceFilename, String loadFilename, String listingFilename) {
        this.sourceFilename = sourceFilename;
        this.loadFilename = loadFilename;
        this.listFilename = listingFilename;
        this.labels = new HashMap<>();
        this.opCodes = new HashMap<>();
        this.initializeOpcodes();
    }

    private void initializeOpcodes(){
        this.opCodes.put("LDR", "000001");
    }

    public void readFile(String filename) {
        //helper function for printing files
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                // Read and process each line
                String line = scanner.nextLine();
                System.out.println(line);
                System.out.println();
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
    }

    public void firstPass(){
        //goes through the assembler first pass and assigns labels their values
        int address = 0;
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(this.sourceFilename)); 
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
            
        while (scanner.hasNextLine()) {
            // Read and process each line
            String line = scanner.nextLine();
            //split each line into two columns
            String[] arr = line.split(" ");
            String left_column = arr[0];
            String right_column = arr[1];
            if(left_column.equals("LOC")){ 
                System.out.println("Address change to :" + right_column);
                //setting location/address
                address = Integer.valueOf(right_column);
                //LOC does not increment address
            }else if(left_column.endsWith(":") && true){ //TODO: true is placeholder: also add a check that left column is not a Key in the opCodes hashmap
                //its a label, so current value of that label is current address
                String temp_label = left_column.substring(0, left_column.length() - 1); //remove colon from label
                labels.put(temp_label, address);
                address++;
            }else{
                address++;
            }
        }

        //System.out.println("Mappings of labels Hashmap are: " + labels);
        //System.out.println("Ending Address: " + address);
    }

    public void secondPass(){
        //goes through the second assembler pass and writes out load and listing files
        int address = 0;
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(this.sourceFilename)); 
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
            
        while (scanner.hasNextLine()) {
            // Read and process each line
            String line = scanner.nextLine();
            //split each line into two columns
            String[] arr = line.split(" ");
            String[] comment_arr = line.split(";"); //comment is at index 1 if comma is present in a line
            boolean comment_present = false; 
            if (line.contains(";")){
                comment_present = true;
            }
            
            String left_column = arr[0];
            String right_column = arr[1];
            String outputLine = "";
            if(left_column.equals("LOC")){ 
                //setting location/address
                address = Integer.valueOf(right_column);
                //LOC does not increment address
                outputLine = "          " + line; //adds tab characters for blank left and right column
                this.writeToFile(outputLine, this.listFilename);
            }else if(left_column.equals("Data")){
                //TODO
                address++;

            }else{
                //TODO
                System.out.println();
            }
        }
    }

    private boolean writeToFile(String line, String filename){
        //helper function for writing to load and list files
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(filename); 
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
            return false;
        }
            writer.println(line);
            writer.close();
            return true;
    }

    public String convertToOctal(int decimal) {
        //converts a decimal number to octal - base 8
        String octalNum = Integer.toOctalString(decimal);
        int octalLength = octalNum.length();
        if (octalLength == 6){
            return octalNum;
        }else{
            return "0".repeat(6-octalLength) + octalNum;
        }
    }


}
