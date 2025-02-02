# CSCI6461_Computer_Sim

## Computer Simulator project for CSCI6461: Computer System Architecture

### How to Run with the Assembler.jar file

1. **Prerequisites:**
   - Make sure you have Java installed on your system. You can check by running `java -version` in your terminal.
   - If you don't have Java, download and install it from the official website.

3. **Run the Assember:**
   - Within the SourceFiles/ folder, there are presupplied source files.
   - **The listing files for the two test cases are already in the ExampleOutputFiles/ folder. These will also get created upon running the command below and put into the OutputFiles/ folder.** 
   - **To add additional sourcefiles, add them to the SourceFiles/ folder and they will be translated to their listing and load files, respectively.**
   - They will have the same filename as the source file except the suffixes _listing.txt and _load.txt will be added. 
   - To run the assembler:
     ```bash
     java -jar Assembler.jar
  
    - This will generate the listing and load files in the OutputFiles/ folder.
    - To remove the listing and load files from the OutputFiles/ folder, run the cleanup bash script:
      ```bash
      ./cleanup.sh
      

### How to Run without using the Assembler.jar file

1. **Prerequisites:**
   - Make sure you have Java installed on your system. You can check by running `java -version` in your terminal.
   - If you don't have Java, download and install it from the official website.

2. **Compile the code:**
   - Open your terminal and navigate to the project directory.
   - Run the following command to compile the `java` files:
     ```bash
     javac *.java

3. **Run the Assember:**
   - Within the SourceFiles/ folder, there are presupplied source files.
  - **The listing files for the two test cases are already in the ExampleOutputFiles/ folder. These will also get created upon running the command below and put into the OutputFiles/ folder.** 
   - **To add additional sourcefiles, add them to the SourceFiles/ folder and they will be translated to their listing and load files, respectively.**
   - They will have the same filename as the source file except the suffixes _listing.txt and _load.txt will be added. 
   - To run the assembler:
     ```bash
     java Main
  
    - This will generate the listing and load files in the OutputFiles/ folder.
    - To remove the listing and load files from the OutputFiles/ folder, run the cleanup bash script:
      ```bash
      ./cleanup.sh
