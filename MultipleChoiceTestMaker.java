import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class MultipleChoiceTestMaker{
    
    /////////////////////////////////////////////////////////////////////////////////////////
    ////                                The main method                                  ////
    /////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args) throws IOException{
        
        System.out.println("The Questions file:");
        String pathQuestions = existingFile(false);
        String[][] questionsFile = csvReadQuestionFile(pathQuestions);
        
        
        System.out.println("The Students list:");
        String pathStudents = existingFile(false);
        String[][] studentsFile = csvReadStudentList(pathStudents);
        
        
        String[][][] aProcessed = splitQuestionArray(questionsFile);
        
        
        String[][][] shuffledQuestionSet;
        
        
        String[][] testString = new String[aProcessed.length + 1][6];
        
        String[] question;
        
        String outputFileNameBase = "testFor";
        
        for (int g = 0; g < studentsFile.length; g++){
            shuffledQuestionSet = shuffleQuestions(aProcessed, 10);
            
            testString[0][0] = "Lastname: ";
            testString[0][1] = studentsFile[g][0];
            testString[0][2] = "\nFirstname: ";
            testString[0][3] = studentsFile[g][1];
            testString[0][4] = "\nStudent ID: ";
            testString[0][5] = studentsFile[g][2];
            for (int h = 0; h < aProcessed.length; h++){
                
                shuffledQuestionSet[h] = shuffleAnswers(aProcessed[h], 10);
                question = makeQuestion(aProcessed[h], (h+1));
                testString[h+1] = question;
            }
            
            
            
            File tempFile = new File(".txt");
            tempFile.createNewFile();
            //C:/Users/Administrator/Desktop/Book1.csv
            write(System.getProperty("user.dir") + "//" + outputFileNameBase + g + ".txt", testString);
        }
        
        
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////
    ////                  The GUI for validation of files and inputs                     ////
    /////////////////////////////////////////////////////////////////////////////////////////
    public static String validInput(Scanner input, String[] allowedInputs){ // used for getting Y, N, y, n from user.
        String notSure;
        String valid = "";
        
        
        //Takes several inputs until a valid one comes, based on the valid input list it got as an argument;
        
        
        while (valid == ""){ 
            notSure = input.nextLine();
            
            //cchecks if input is in the valid domain;
            
            for (String h : allowedInputs){
                try{
                    if (h.equals(notSure)){
                        valid = h;
                        break;
                    }
                }
                catch(Exception e){
                    System.out.println("");
                }
            }
            
            
            //no need to type invalid if the input works!
            
            if (valid != ""){
                break;
            }
            
            System.out.println("Invalid, Retry!");
        }
        
        
        
        
        //returns the first valid input entered.
        
        return valid;
    }
    
    public static String existingFile(boolean what){//This method gets a valid file. All the inputs and gui part is done by this method and validInput.
        //what: if false, means that we want a file that exists. if true, means that we want a file that doesn't exist.
        Scanner input = new Scanner(System.in);
        String path;
        if (what){
            System.out.println("Do you want to sav in the same path of program? (y/n)");
        }
        else{
            System.out.println("Is your file in the same path as the program? (y/n)");
        }
        String inPath = validInput(input, new String[]{"y", "Y", "n", "N"});
        String overwrite;
        File file;
        
        if (inPath.equals("y") || inPath.equals("Y")){
            System.out.println("Enter The file name: ");
            path = System.getProperty("user.dir") + "//" + input.nextLine();//making the format appropriate for file making
            file = new File(path);
            if (!what){
                while (!file.exists()){//if you want a file that exists, this loop will run until we get a file that exists. 
                    System.out.println("File doesn't exist; Retry please: ");
                    path = System.getProperty("user.dir") + "//" + input.nextLine();
                    file = new File(path);
                }
            }
            else if (what && file.exists()){//if we want a file to write in, we must get a file that doesn't exist or if exists we must ask for permission to overwrite.
                boolean flag = true;
                while (flag){
                    System.out.println("File already exists. do you wish to overwrite it? (y/n)");
                    overwrite = validInput(input, new String[]{"Y", "N", "y", "n"});
                    if (overwrite.equals("y") || overwrite.equals("Y")){
                        flag = false;
                    }
                    else{ 
                        System.out.println("Give the new file name: ");
                        path = System.getProperty("user.dir") + "//" + input.nextLine();
                        file = new File(path);
                        flag = file.exists();
                    }
                    if (!flag){
                        try{
                            file.createNewFile();
                        }
                        catch (Exception e){
                            System.out.println("Path is not valid; Retry please: ");
                            flag = true;
                        }
                    }
                }
            }
        }
        else{
            System.out.println("Enter The file path: "); 
            path = input.nextLine();
            file = new File(path);
            if (!what){
                while (!file.exists()){
                    System.out.println("File doesn't exist or path is not valid; Retry please: ");
                    path = input.nextLine();//easier for me to code, ya?
                    file = new File(path);
                }
            }
            else if (what && file.exists()){
                boolean flag = true;
                while (flag){
                    System.out.println("File already exists. do you wish to overwrite it? (y/n)");
                    overwrite = validInput(input, new String[]{"Y", "N", "y", "n"});
                    if (overwrite.equals("y") || overwrite.equals("Y")){
                        flag = false;
                    }
                    else{ 
                        System.out.println("Give the new file name: ");
                        path = input.nextLine();
                        file = new File(path);
                        flag = file.exists();
                    }
                    if (!flag){
                        try{
                            file.createNewFile();
                        }
                        catch (Exception e){
                            System.out.println("Path is not valid; Retry please: ");
                            flag = true;
                        }
                    }
                }
            }
        }
        
        return path;
        
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////
    ////                        The file input and data analysis                         ////
    /////////////////////////////////////////////////////////////////////////////////////////
    
    public static String[][] csvReadStudentList(String filePath) throws IOException{
        
        File csvFile = new File(filePath);
        Scanner csvScanner = new Scanner(csvFile);
        int counter = 0;
        while (csvScanner.hasNextLine()){
            csvScanner.nextLine();
            counter ++;
        }
        
        csvScanner = new Scanner(csvFile);
        String[][] theProcessedArray = new String[counter][3];
        String readString;
        
        while (csvScanner.hasNextLine()){
            readString = csvScanner.nextLine();
            theProcessedArray[theProcessedArray.length - counter] = split3(readString);
            counter--;
        }
        
        return theProcessedArray;
        
    }
    
    public static String[][] csvReadQuestionFile(String filePath) throws IOException{//reads a csv file and returns a String[][] including the Table
        /*
         * Purpose: read a csv file to a String[][]
         * 
         * input: the path to .csv file
         * 
         * output: the String[][] 2D array of readed file
         * */
        
        File csvFile = new File(filePath);
        Scanner csvScanner = new Scanner(csvFile);
        int counter = 0;
        while (csvScanner.hasNextLine()){
            csvScanner.nextLine();
            counter ++;
        }
        
        csvScanner = new Scanner(csvFile);
        String[][] theProcessedArray = new String[counter][3];
        String readString;
        String tempString;
        String[] tempStringArray;
        
        while (csvScanner.hasNextLine()){
            readString = csvScanner.nextLine();
            if (findLastDQ(readString) == -1){
                theProcessedArray[theProcessedArray.length - counter] = split3(readString);
            }
            
            else{
                tempString = range(readString, 0, findLastDQ(readString));
                tempString = stringCoder(tempString);
                tempString = stringDecoder(tempString);
                theProcessedArray[theProcessedArray.length - counter][0] = tempString;
                tempString = tempString = range(readString, findLastDQ(readString) + 2, readString.length() - 1);
                //System.out.println(tempString);
                tempStringArray = split2(tempString);
                //System.out.println(tempString);
                //System.out.println(tempStringArray.length);
                theProcessedArray[theProcessedArray.length - counter][1] = tempStringArray[0];
                theProcessedArray[theProcessedArray.length - counter][2] = tempStringArray[1];
            }
            counter --;
            //System.out.println("LOOP!");
            
        }
        return theProcessedArray;
        
        
    }
    
    
    
    /////////////////////////////////////////////////////////////////////////////////////////
    ////                             The question making part                            ////
    /////////////////////////////////////////////////////////////////////////////////////////
    
    public static String[] makeQuestion(String[][] qArray, int qNumber){//turns the question array into a writable array
        int correct = -1;
        boolean tempBoolean;
        
        for (int i = 0; i < qArray.length; i++){
            try{
                tempBoolean = qArray[i][2].equals("null");
                //System.out.println(qArray[i][1]);
                correct = i;
                break;
            }
            catch (Exception e){
                continue; 
            }
        }
        String[] guiString = new String[6];
        guiString[0] = "Question " + qNumber + ": " + qArray[0][0];
        guiString[1] = "\n1) " + qArray[0][1];
        guiString[2] = "  2) " + qArray[1][1];
        guiString[3] = "  3) " + qArray[2][1];
        guiString[4] = "  4) " + qArray[3][1];
        guiString[5] = "\n\nAnswer: " + (correct + 1);
        
        return guiString;
    }
    
    public static String[][][] splitQuestionArray(String[][] csvProcessed){//changes a big csv processed 2d array with 4n lines into n question arrays.
        String[][][] ret = new String[csvProcessed.length / 4][4][3];
        for (int h = 0; h < csvProcessed.length; h += 4){
            ret[h/4][0] = csvProcessed[h];
            ret[h/4][1] = csvProcessed[h + 1];
            ret[h/4][2] = csvProcessed[h + 2];
            ret[h/4][3] = csvProcessed[h + 3];
        }
        return ret;
    }
    public static String[][] shuffleAnswers(String[][] a, int n){//Shuffles the choices place while answer remains correct
        Random rand = new Random();
        int randp1x = 0;
        int randp2x = 0;
        for (int h = 0; h < n; h++){
            randp1x = rand.nextInt(4);
            randp2x = rand.nextInt(4);
            while (randp2x == randp1x){
                randp1x = rand.nextInt(4);
            }
            a = swap(a, new int[]{randp1x, 1}, new int[]{randp2x, 1});
            a = swap(a, new int[]{randp1x, 2}, new int[]{randp2x, 2});
        }
        return a;
    }
    
    public static String[][][] shuffleQuestions(String[][][] a, int n){//shuffles the questions keeping their choices and the correct answer
        Random rand = new Random();
        int randp1x = 0;
        int randp2x = 0;
        for (int h = 0; h < n; h++){
            randp1x = rand.nextInt(a.length);
            randp2x = rand.nextInt(a.length);
            while (randp2x == randp1x){
                randp1x = rand.nextInt(a.length);
            }
            a = swapQuestions(a, randp1x, randp2x);
        }
        return a;
    }
    /////////////////////////////////////////////////////////////////////////////////////////
    ////                                 The utilities                                   ////
    /////////////////////////////////////////////////////////////////////////////////////////
    public static String[][] swap(String[][] a, int[] x, int[] y){//made for shuffling the answers
        String tempString = "";
        tempString = a[x[0]][x[1]];
        a[x[0]][x[1]] = a[y[0]][y[1]];
        a[y[0]][y[1]] = tempString;
        return a;
    }
    public static String[][][] swapQuestions(String[][][] a, int x, int y){//made for shuffling the questions
        String[][] tempString = a[x];
        a[x] = a[y];
        a[y] = tempString;
        return a;
    }
    
    public static String[] split2(String str){//made for lines that have ',' and '"'
        String[] ret = new String[2];
        int index = 0;
        String tempString;
        char[] tempCharArray = new char[str.length()];
        for (int h = 0; h < str.length(); h++){
            if (str.charAt(h) == ',' || h == str.length() - 1){
                if (str.charAt(h) != ',' && h == str.length() - 1){
                    tempCharArray[h] = str.charAt(h);
                    //System.out.println(str.charAt(h));
                }
                tempString = new String(tempCharArray);
                ret[index] = tempString;
                index ++;
            }
            else{
                tempCharArray[h] = str.charAt(h);
            }
            if (index == 2){
                break;
            }
        }
        return ret;
    }
    public static String[] split3(String str){//made for ordinary lines
        String[] ret = new String[3];
        int index = 0;
        String tempString;
        char[] tempCharArray = new char[str.length()];
        for (int h = 0; h < str.length(); h++){
            if (str.charAt(h) == ',' || h == str.length() - 1){
                if (str.charAt(h) != ',' && h == str.length() - 1){
                    tempCharArray[h] = str.charAt(h);
                    //System.out.println(str.charAt(h));
                }
                tempString = new String(tempCharArray);
                ret[index] = tempString;
                index ++;
                tempCharArray = new char[str.length()];
            }
            else{
                tempCharArray[h] = str.charAt(h);
            }
            if (index == 3){
                break;
            }
        }
        return ret;
    }
    
    public static boolean hasComma(String str){//checks if the string given has any ','
        for (char h: str.toCharArray()){
            if (h == ','){
                return true;
            }
        }
        return false;
    }
    public static String range(String str, int first, int last){//returns an interval of a string from index first to index last
        char[] a = new char[last - first + 1];
        for (int h = first; h < last + 1; h++){
            a[h - first] = str.charAt(h);
        }
        String ret = new String(a);
        return ret;
    }
    
    //import a csv file and read it to a String[][]
    // public static File
    
    public static int findLastDQ(String a){//finds the last '"' in the string and returns the index
        for (int h = a.length() - 1; h != 0; h--){
            if (a.charAt(h) == '\"'){
                return h;
            }
        }
        return -1;
    }
    
    public static String stringCoder(String og){//executes a reverse for the csv protocol of ',' which is in a cell and '"' which is for partitioning and '"' which is in a cell
        String processedString = "";
        int counter;
        for (int h = 0; h < og.length(); h++){
            if (og.charAt(h) == '\"'){
                counter = 0;
                while (og.charAt(h) == '\"'){
                    counter++;
                    h++;
                    if (h == og.length()){
                        break;
                    }
                }
                for (int i = 0; i < counter/2; i++){
                    processedString += "\"";
                }
                h--;
            }
            else if (og.charAt(h) == ','){
                processedString += "`";
            }
            
            else{
                processedString += og.charAt(h);
            }
        }
        return processedString;
    }
    public static String stringDecoder(String og){//reverts the changes applied to ',' in stringCoder method
        String processedString = "";
        
        for (int h = 0; h < og.length(); h++){
            if (og.charAt(h) == '`'){
                processedString += ",";
            } 
            else{
                processedString += og.charAt(h);
            }
        }
        return processedString;
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////
    ////                                   Output Stuff                                  ////
    /////////////////////////////////////////////////////////////////////////////////////////
    
    //Write in a new txt file
    public static void write(String filePath, String[][] a) throws IOException{
        
        PrintWriter writer = new PrintWriter(filePath);
        
        for (int h = 0; h < a.length; h++){
            for (int i = 0; i < a[h].length; i++){
                writer.print(a[h][i]);
            }
            writer.println();
            writer.println();
            
        }
        writer.close();
    }
    
    
}