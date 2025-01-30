package assemblerProject;
import java.io.FileWriter;
import java.io.BufferedWriter;
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
        this.opCodes.put("STR", "000010");
        this.opCodes.put("LDA", "000011");
        this.opCodes.put("LDX", "100001");
        this.opCodes.put("STX", "100010");
        this.opCodes.put("JZ", "001000");
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
            if(line.equals("")){ //empty line
                continue;
            }
            //split each line into two columns
            String[] arr = line.split(" ");
            String leftColumn = arr[0];
            String rightColumn = arr[1];
            if(leftColumn.equals("LOC")){ 
                System.out.println("Address change to :" + rightColumn);
                //setting location/address
                address = Integer.parseInt(rightColumn);
                //LOC does not increment address
            }else if(leftColumn.endsWith(":") && true){ //TODO: true is placeholder: also add a check that left column is not a Key in the opCodes hashmap
                //its a label, so current value of that label is current address
                String temp_label = leftColumn.substring(0, leftColumn.length() - 1); //remove colon from label
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
            if(line.equals("")){ //empty line
                continue;
            }
            //split each line into two columns
            String[] arr = line.split(" ");    
            String leftColumn = arr[0];
            String rightColumn = arr[1];
            String outputLine = "";
            if(leftColumn.equals("LOC")){ 
                //setting location/address
                address = Integer.parseInt(rightColumn);
                //LOC does not increment address
                outputLine = "                  " + line; //adds tab characters for blank left and right column
                this.writeToFile(outputLine, this.listFilename);
            }else if(leftColumn.equals("Data")){
                if(this.labels.containsKey(rightColumn)){
                    outputLine = this.convertToOctal(address) + "   " + this.convertToOctal(this.labels.get(rightColumn));
                }else{    
                    outputLine = this.convertToOctal(address) + "   " + this.convertToOctal(Integer.parseInt(rightColumn));
                }
                this.writeToFile(outputLine, this.loadFilename);
                String listOutputline = outputLine + "   " + line;
                this.writeToFile(listOutputline, this.listFilename);
                address++;
            }else if(rightColumn.equals("HLT")){
                //stop the program
                outputLine = this.convertToOctal(address) + "   " + "000000";
                this.writeToFile(outputLine, this.loadFilename);
                String listOutputline = outputLine + "   " + line;
                this.writeToFile(listOutputline, this.listFilename);
            }else if(leftColumn.equals("LDR") || leftColumn.equals("STR") || leftColumn.equals("LDA") || leftColumn.equals("LDX")
             || leftColumn.equals("STX")){
                //Load/Store
                outputLine = this.convertToOctal(address) + "   " + this.loadStore(leftColumn, rightColumn);
                this.writeToFile(outputLine, this.loadFilename);
                String listOutputline = outputLine + "   " + line;
                this.writeToFile(listOutputline, this.listFilename);
                address++;
            }else if(leftColumn.equals("JZ") || leftColumn.equals("JNE") || leftColumn.equals("JCC") || leftColumn.equals("JMA")
             || leftColumn.equals("JSR") || leftColumn.equals("RFS") || leftColumn.equals("SOB") || leftColumn.equals("JGE")){
                //Transfer
                outputLine = this.convertToOctal(address) + "   " + this.transfer(leftColumn, rightColumn);
                this.writeToFile(outputLine, this.loadFilename);
                String listOutputline = outputLine + "   " + line;
                this.writeToFile(listOutputline, this.listFilename);
                address++;
            }else{
                outputLine = line;
                this.writeToFile(outputLine, this.loadFilename);
                this.writeToFile(outputLine, this.listFilename);
                address++;
            }
        }
    }

    //converts a Load/Store instruction into its octal string format
    //OpCodes: 01:LDR, 02:STR, 03:LDA, 41:LDX, 42:STX
    public String loadStore(String leftColumn, String rightColumn){
        String[] operands = rightColumn.split(",");
        String indirect = "";
        if((leftColumn.equals("LDR") || leftColumn.equals("STR") || leftColumn.equals("LDA")) && operands.length == 4 && operands[operands.length-1] == "1"){ //indirect bit set
            indirect = "1";
        }else if((leftColumn.equals("LDX") || leftColumn.equals("STX")) && operands.length == 3 && operands[operands.length-1] == "1"){
            indirect = "1";
        }else{
            indirect = "0";
        }
        String gpr = "0"; //general purpose register
        String ix = "0"; //index register
        String address = ""; 
        if(leftColumn.equals("LDR") || leftColumn.equals("STR") || leftColumn.equals("LDA")){
            gpr = operands[0];
            ix = operands[1];
            address = operands[2];
        }else{ //LDX, STX
            ix = operands[0];
            address = operands[1];
        }
        String opCode = this.opCodes.get(leftColumn);
        gpr = this.convertToBinaryString(gpr, 2);
        ix = this.convertToBinaryString(ix, 2);
        address = this.convertToBinaryString(address, 5);
        String instruction = opCode + gpr + ix + indirect + address;
        return this.convertToOctal(instruction);
    }

    //converts a Transfer instruction into its octal string format
    //OpCodes: 10:JZ, 11:JNE, 12:JCC, 13:JMA, 14:JSR, 15:RFS, 16:SOB, 17:JGE.
    public String transfer(String leftColumn, String rightColumn){
        if(leftColumn.equals("RFS")){
            return this.rfs(leftColumn, rightColumn);
        }
        String[] operands = rightColumn.split(",");
        String indirect = "";
        if((leftColumn.equals("JZ") || leftColumn.equals("JNE") || leftColumn.equals("JCC") || leftColumn.equals("SOB") || leftColumn.equals("JGE")) && operands.length == 4 && operands[operands.length-1] == "1"){ //indirect bit set
            indirect = "1";
        }else if((leftColumn.equals("JMA") || leftColumn.equals("JSR")) && operands.length == 3 && operands[operands.length-1] == "1"){
            indirect = "1";
        }else{
            indirect = "0";
        }
        String gpr = "0"; //general purpose register
        String ix = "0"; //index register
        String address = ""; 
        if(leftColumn.equals("JZ") || leftColumn.equals("JNE") || leftColumn.equals("JCC") || leftColumn.equals("SOB") || leftColumn.equals("JGE")){
            gpr = operands[0];
            ix = operands[1];
            address = operands[2];
        }else{ //JMA, JSR
            ix = operands[0];
            address = operands[1];
        }
        String opCode = this.opCodes.get(leftColumn);
        gpr = this.convertToBinaryString(gpr, 2);
        ix = this.convertToBinaryString(ix, 2);
        address = this.convertToBinaryString(address, 5);
        String instruction = opCode + gpr + ix + indirect + address;
        return this.convertToOctal(instruction);
    }

    //helper function to handle special case for RFS opCode: 15
    private String rfs(String leftColumn, String rightColumn){
        String opCode = this.opCodes.get(leftColumn);
        //right column should have only immed address 
        String address = this.convertToBinaryString(rightColumn, 5);  
        String instruction = opCode + "00" + "00" + "0" + address;
        return this.convertToOctal(instruction);
    }
   
    private boolean writeToFile(String line, String filename){
        //helper function for writing to load and list files
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filename, true)); 
            writer.write(line);
            writer.newLine();
            writer.close();
            return true;
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
            return false;
        }
    }

    private String convertToBinaryString(String num, int len){
        //converts an String num to a binary string with a specified number of characters len
        //ensures that the number of characters is equal to len
        return String.format("%" + len +"s", Integer.toBinaryString(Integer.parseInt(num))).replace(' ', '0');
    }

    private String convertToOctal(int decimal) {
        //converts a decimal number to octal - base 8
        String octalNum = Integer.toOctalString(decimal);
        int octalLength = octalNum.length();
        if (octalLength == 6){
            return octalNum;
        }else{
            return "0".repeat(6-octalLength) + octalNum;
        }
    }

    private String convertToOctal(String binaryNum){
        //converts a binary string to an octal string
        int decimalNum = Integer.parseInt(binaryNum, 2);
        return this.convertToOctal(decimalNum);
    }

}
