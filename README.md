# CSCI6461_Computer_Sim

## Computer Simulator project for CSCI6461: Computer System Architecture

### How to Run

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
   - Any additional source files in this folder will be translated to their listing and load files, respectively.
   - To run the assembler:
     ```bash
     java Main
  
    - This will generate the listing and load files in the OutputFiles/ folder.
    - To remove the listing and load files from the OutputFiles/ folder, run the cleanup bash script:
      ```bash
      ./cleanup.sh
