package assemblerProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Assembler {
    
    String filename;
    //stores labels contained within the source file
    private HashMap<String, Integer> labels; //value of each label is the address that the label occurs at in DECIMAL

    public Assembler(String filename) {
        this.filename = filename;
        this.labels = new HashMap<>();
    }


    public void read_file() {
        //filepath to source file with instructions in it

        try (Scanner scanner = new Scanner(new File(this.filename))) {
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

    public void first_pass(){
        //goes through the assembler first pass and assigns labels their values
        int address = 0;
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(this.filename)); 
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
                System.out.println("detected LOC");
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
