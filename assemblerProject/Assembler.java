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
     //Initializing the binary value with corresponding labels
     private void initializeOpcodes(){
        this.opCodes.put("LDR", "000001");
        this.opCodes.put("TRAP", "011000");
        this.opCodes.put("STR", "000010");
        this.opCodes.put("LDA", "000011");
        this.opCodes.put("LDX", "100001");
        this.opCodes.put("STX", "100010");
        this.opCodes.put("JZ", "001000");
        this.opCodes.put("JNE", "001001");
        this.opCodes.put("JCC", "001010");
        this.opCodes.put("JMA", "001011");
        this.opCodes.put("JSR", "001100");
        this.opCodes.put("RFS", "001101");
        this.opCodes.put("SOB", "001110");
        this.opCodes.put("JGE", "001111");
        this.opCodes.put("AMR", "000100");
        this.opCodes.put("SMR", "000101");
        this.opCodes.put("AIR", "000110");
        this.opCodes.put("SIR", "000111");
        this.opCodes.put("MLT", "111000");
        this.opCodes.put("DVD", "111001");
        this.opCodes.put("TRR", "111010");
        this.opCodes.put("AND", "111011");
        this.opCodes.put("ORR", "111100");
        this.opCodes.put("NOT", "111101");
        this.opCodes.put("SRC", "011001");
        this.opCodes.put("RRC", "011010");
        this.opCodes.put("IN", "110001");
        this.opCodes.put("OUT", "110010");
        this.opCodes.put("CHK", "110011");
        this.opCodes.put("FADD", "011011");
        this.opCodes.put("FSUB", "011100");
        this.opCodes.put("VADD", "011101");
        this.opCodes.put("VSUB", "011110");
        this.opCodes.put("CNVRT", "011111");
        this.opCodes.put("LDFR", "101000");
        this.opCodes.put("STFR", "101001");
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
            String rightColumn = "";
            if(arr.length>1){
                rightColumn = arr[1];
            }
            if(leftColumn.equals("LOC")){ 
                //System.out.println("Address change to :" + rightColumn);
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
            String rightColumn = "";
            if(arr.length>1){
                rightColumn = arr[1];
            }
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
            }else if(rightColumn.equals("HLT") || (leftColumn.equals("HLT"))){
                //stop the program
                outputLine = this.convertToOctal(address) + "   " + "000000";
                this.writeToFile(outputLine, this.loadFilename);
                String listOutputline = outputLine + "   " + line;
                this.writeToFile(listOutputline, this.listFilename);
            }else if(leftColumn.equals("TRAP")){
                //TRAP instruction
                outputLine = this.convertToOctal(address) + "   " + this.trap(leftColumn, rightColumn);
                this.writeToFile(outputLine, this.loadFilename);
                String listOutputline = outputLine + "   " + line;
                this.writeToFile(listOutputline, this.listFilename);
                address++;
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
            }else if(leftColumn.equals("AMR") || leftColumn.equals("SMR") || leftColumn.equals("AIR") || leftColumn.equals("SIR")){
                //Arithmetic and Logic 
                outputLine = this.convertToOctal(address) + "   " + this.arithmeticLogical(leftColumn, rightColumn);
                this.writeToFile(outputLine, this.loadFilename);
                String listOutputline = outputLine + "   " + line;
                this.writeToFile(listOutputline, this.listFilename);
                address++;
            }else if(leftColumn.equals("MLT") || leftColumn.equals("DVD") || leftColumn.equals("TRR") || leftColumn.equals("AND") 
             || leftColumn.equals("ORR") || leftColumn.equals("NOT")){
                //Multiply, Divide and other Logical Operations
                outputLine = this.convertToOctal(address) + "   " + this.multiplyDivideLogical(leftColumn, rightColumn);
                this.writeToFile(outputLine, this.loadFilename);
                String listOutputline = outputLine + "   " + line;
                this.writeToFile(listOutputline, this.listFilename);
                address++;
            }else if(leftColumn.equals("SRC") || leftColumn.equals("RRC")){
                //Shift and Rotate
                outputLine = this.convertToOctal(address) + "   " + this.shiftRotateOperations(leftColumn, rightColumn);
                this.writeToFile(outputLine, this.loadFilename);
                String listOutputline = outputLine + "   " + line;
                this.writeToFile(listOutputline, this.listFilename);
                address++;
            }else if(leftColumn.equals("FADD") || leftColumn.equals("FSUB") || leftColumn.equals("VADD") || leftColumn.equals("VSUB") 
             || leftColumn.equals("CNVRT") || leftColumn.equals("LDFR") || leftColumn.equals("STFR")){
                //Floating Point Instruction and Vector Operation
                outputLine = this.convertToOctal(address) + "   " + this.floatingPointVector(leftColumn, rightColumn);
                this.writeToFile(outputLine, this.loadFilename);
                String listOutputline = outputLine + "   " + line;
                this.writeToFile(listOutputline, this.listFilename);
                address++;
            }
            else if(leftColumn.equals("IN") || leftColumn.equals("OUT") || leftColumn.equals("CHK")){
                //Input Output Operations
                outputLine = this.convertToOctal(address) + "   " + this.ioOperations(leftColumn, rightColumn);
                this.writeToFile(outputLine, this.loadFilename);
                String listOutputline = outputLine + "   " + line;
                this.writeToFile(listOutputline, this.listFilename);
                address++;
            }
            else{
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
    
        if((leftColumn.equals("LDR") || leftColumn.equals("STR") || leftColumn.equals("LDA")) && operands.length == 4 && operands[operands.length-1].equals("1")){ //indirect bit set
            indirect = "1";
        }else if((leftColumn.equals("LDX") || leftColumn.equals("STX")) && operands.length == 3 && operands[operands.length-1].equals("1")){
            indirect = "1";
        }else{
            indirect = "0";
        }
       
        String gpr = "0"; //general purpose register
        String ix = "0"; //index register
        String address = "0"; 
        if(leftColumn.equals("LDR") || leftColumn.equals("STR") || leftColumn.equals("LDA")){
            gpr = operands[0];
            ix = operands[1];
            address = operands[2];
        }else{ //LDX, STX
            ix = operands[0];
            address = operands[1];
        }
        if(Integer.parseInt(gpr) > 3 || Integer.parseInt(gpr) < 0 || Integer.parseInt(ix) > 3 || Integer.parseInt(ix) < 0){
            return "Error: GPR and IX must be between 0 and 3";
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
        if((leftColumn.equals("JZ") || leftColumn.equals("JNE") || leftColumn.equals("JCC") || leftColumn.equals("SOB") || leftColumn.equals("JGE")) && operands.length == 4 && operands[operands.length-1].equals("1")){ //indirect bit set
            indirect = "1";
        }else if((leftColumn.equals("JMA") || leftColumn.equals("JSR")) && operands.length == 3 && operands[operands.length-1].equals("1")){
            indirect = "1";
        }else{
            indirect = "0";
        }
        String gpr = "0"; //general purpose register
        String ix = "0"; //index register
        String address = "0"; 
        if(leftColumn.equals("JZ") || leftColumn.equals("JNE") || leftColumn.equals("JCC") || leftColumn.equals("SOB") || leftColumn.equals("JGE")){
            gpr = operands[0];
            ix = operands[1];
            address = operands[2];
        }else{ //JMA, JSR
            ix = operands[0];
            address = operands[1];
        }
        if(Integer.parseInt(gpr) > 3 || Integer.parseInt(gpr) < 0 || Integer.parseInt(ix) > 3 || Integer.parseInt(ix) < 0){
            return "Error: GPR and IX must be between 0 and 3";
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

    //converts a Arithmetic instruction into its octal string format
    //OpCodes: 04:AMR, 05:SMR, 06:AIR, 07:SIR
    public String arithmeticLogical(String leftColumn, String rightColumn){
        String[] operands = rightColumn.split(",");
        String indirect = "";
        if((leftColumn.equals("AMR") || leftColumn.equals("SMR")) && operands.length == 4 && operands[operands.length-1].equals("1")){ //indirect bit set){ //indirect bit set
            indirect = "1";
        }else{
            indirect = "0";
        }
        String gpr = "0"; //general purpose register
        String ix = "0"; //index register
        String address = "0"; 
        if(leftColumn.equals("AMR") || leftColumn.equals("SMR")){
            gpr = operands[0];
            ix = operands[1];
            address = operands[2];
        }else{ //AIR, SIR
            gpr = operands[0];
            address = operands[1];
        }
        if(Integer.parseInt(gpr) > 3 || Integer.parseInt(gpr) < 0 || Integer.parseInt(ix) > 3 || Integer.parseInt(ix) < 0){
            return "Error: GPR and IX must be between 0 and 3";
        }
        String opCode = this.opCodes.get(leftColumn);
        gpr = this.convertToBinaryString(gpr, 2);
        ix = this.convertToBinaryString(ix, 2);
        address = this.convertToBinaryString(address, 5);
        String instruction = opCode + gpr + ix + indirect + address;
        return this.convertToOctal(instruction);
    }

    //converts a multiply, divide or other logical instruction into its octal string format
    //OpCodes: 70:MLT, 71:DVD, 72:TRR, 73:AND, 74:ORR, 75:NOT
    public String multiplyDivideLogical(String leftColumn, String rightColumn){
        String[] operands = rightColumn.split(",");
        String gprX = "0"; //general purpose register X
        String gprY = "0"; //general purpose register Y
        String address = "0"; 
        if(!leftColumn.equals("NOT")){ //MLT, DVD, TRR, AND, ORR
            gprX = operands[0];
            gprY = operands[1];
        }else{ //NOT
            gprX = operands[0];
        }
        if(Integer.parseInt(gprX) > 3 || Integer.parseInt(gprX) < 0 || Integer.parseInt(gprY) > 3 || Integer.parseInt(gprY) < 0){
            return "Error: GPR and IX must be between 0 and 3";
        }
        String opCode = this.opCodes.get(leftColumn);
        gprX = this.convertToBinaryString(gprX, 2);
        gprY = this.convertToBinaryString(gprY, 2);
        address = this.convertToBinaryString(address, 6);
        String instruction = opCode + gprX + gprY + address;
        return this.convertToOctal(instruction);
    }

    //converts a shift or rotate instruction into its octal string format
    //OpCodes: 31:SRC, 32:RRC
    public String shiftRotateOperations(String leftColumn, String rightColumn){
        String[] operands = rightColumn.split(",");
        String gpr = operands[0]; //general purpose register
        String count = operands[1]; //number of bits to shift/rotate
        String lR = operands[2]; //Logical Shift/Rotate
        String aL = operands[3]; //Arithmetic Shift
        if(Integer.parseInt(gpr) > 3 || Integer.parseInt(gpr) < 0 || Integer.parseInt(lR) > 1 || Integer.parseInt(aL) > 1 || Integer.parseInt(lR) < 0 || Integer.parseInt(aL) < 0){
            return "Error: GPR, lR, or aL incorrect";
        }
        String emptybits = "00";   
        String opCode = this.opCodes.get(leftColumn);
        gpr = this.convertToBinaryString(gpr, 2);
        count = this.convertToBinaryString(count, 4);
        String instruction = opCode + gpr + aL + lR + emptybits + count;
        return this.convertToOctal(instruction);
    }

     //converts a floating point instruction or vector operation into its octal string format
    //OpCodes: 33:FADD, 34:FSUB, 35:VADD, 36:VSUB, 37:CNVRT, 50:LDFR, 51:STFR 
    public String floatingPointVector(String leftColumn, String rightColumn){
        String[] operands = rightColumn.split(",");
        String indirect = "";
        if(operands.length == 4 && operands[operands.length-1].equals("1")){ //indirect bit set
            indirect = "1";
        }else{
            indirect = "0";
        }
        String fpr = operands[0]; //floating point register
        String ix = operands[1]; //index register 
        if(Integer.parseInt(fpr) > 1 || Integer.parseInt(fpr) < 0 || Integer.parseInt(ix) > 3 || Integer.parseInt(ix) < 0){
            return "Error: FPR and IX must be between 0 and 3";
        }
        String address = operands[2]; 
        String opCode = this.opCodes.get(leftColumn);
        fpr = this.convertToBinaryString(fpr, 2);
        ix = this.convertToBinaryString(ix, 2);
        address = this.convertToBinaryString(address, 5);
        String instruction = opCode + fpr + ix + indirect + address;
        return this.convertToOctal(instruction);
    }
    //converts a trap into its octal string format
    //OpCodes: 30:TRAP 
    public String trap(String leftColumn, String rightColumn){
        String emptyBits = "000000";
        String trapCode = this.convertToBinaryString(rightColumn, 4);
        String opCode = this.opCodes.get(leftColumn);
        String instruction = opCode + emptyBits + trapCode;
        return this.convertToOctal(instruction);
    }
     //converts a i/o operations into its octal string format
    //OpCodes: 61:IN, 62: out, 63:CHK
    public String ioOperations(String leftColumn, String rightColumn){
        String[] operands = rightColumn.split(",");
        String r = operands[0]; //register
        String deviceId = operands[1]; //devId
        if(Integer.parseInt(r) > 3 || Integer.parseInt(r) < 0){
            return "Error: GPR must be between 0 and 3";
        }
        String emptybits = "000"; //Empty bits to fill the space    
        String opCode = this.opCodes.get(leftColumn);
        r = this.convertToBinaryString(r, 2);
        deviceId = this.convertToBinaryString(deviceId, 5);
        String instruction = opCode + r + emptybits + deviceId;
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
        //converts an String num in decimal to a binary string with a specified number of characters len
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
