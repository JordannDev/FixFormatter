package io.fixformatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * FixFormatter
 *
 * @author JordannDev
 * @version 1.0
 * @since 1/31/2019
 *
 * This Java application will format the FIX.txt file released by the FAA into a
 * format usable in the .sct2 format.
 *
 * You can find the fix file here: https://www.faa.gov/air_traffic/flight_info/aeronav/Aero_Data/NASR_Subscription/
 *
 */
public class FixFormatter {

    // Input file object
    private File file;
    // String for the ARTCC code the reader will look for.
    private String artccCode;
    // Longs for storing currentTimeMilis for stats or something.
    private long startTime, finishTime;

    /**
     * Application entry method.
     * 2 arguments are required
     *
     * @param args Application start arguments. 2 args are required the code of the ARTCC, and the input file (the FIX.txt file).
     * Example: java -jar FixFormat.jar ZME FIX.txt
    /*

     */
    public static void main(String[] args) {
        FixFormatter fixFormatter = new FixFormatter();
        if(args.length > 1){
            fixFormatter.artccCode = args[0];
            fixFormatter.file = new File(args[1]);
            fixFormatter.startTime = System.currentTimeMillis();
            fixFormatter.runFormat();
        }else{
            System.err.println("You need to provide a ARTCC code (i.e ZME) and a input file. Ex: java -jar FixFormat.jar ZME fix.txt");
        }
    }

    /**
     * runFormat - This method is used to parse the input file.
     */
    private void runFormat(){
        try {
            System.out.println("Starting...");
            // Num is used to find the next possible output file number. i.e out-1.txt
            int num = 0;
            // Name of the output file.
            String name = "out.txt";
            // Output file
            File outputFile = new File(name);
            // Loop to find the next possible output file.
            while (outputFile.exists()) {
                name = "out-" + (num++) + ".txt";
                outputFile = new File(name);
            }

            // int used to count the amount of fixes formatted.
            int count = 0;
            // outputData String for writing to the file (decided to include the format.
            String outputData = "##Format: FIX COORD1 COORD2";
            // location String used for getting the location by the parser.
            String location;
            // Reader for reading through the input file.
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                // String for current line.
                String line;
                // While loop.
                while ((line = br.readLine()) != null) {
                    // Parsing to remove the empty spacing to make putting it into an array easier.
                    line = remove_duplicate(line);
                    line = reFormatStates(line);
                    String s = line.replaceAll(" ", ";");

                    // Turning S into an array to make accessing the data easier.
                    String[] formattedSplit = s.split(";");

                    // Checking to see if the line starts with FIX1 because you don't need the other ones.
                    if (formattedSplit[0].startsWith("FIX1")) {
                        // Checking to see if the artcc code is the one we are looking for.
                        if (formattedSplit[6].equalsIgnoreCase(artccCode)) {
                            // Formatting the first coordinate making it easier to read, and make it usable in .sct2 file format.
                            location = formattedSplit[2].substring(1);
                            location = location.substring(1);
                            location = "0" + location;
                            location = getEndOfString(location, 1) + location;
                            location = subStringEnd(location, 1);
                            // Replacing the -'s with .'s because VRC reads .'s not -'s.
                            location = location.replaceAll("-", ".");

                            // Formatting the second coordinate making it easier to read, and making it usable in the .sct2 file format.
                            String locationTwo;
                            locationTwo = formattedSplit[3].substring(1);
                            locationTwo = formattedSplit[3].substring(1);
                            locationTwo = "0" + locationTwo;
                            locationTwo = subStringEnd(locationTwo, 3);
                            locationTwo = getEndOfString(locationTwo, 1) + locationTwo;
                            locationTwo = subStringEnd(locationTwo,1);
                            // Replacing the -'s with .'s because VRC reads .'s not -'s.
                            locationTwo = locationTwo.replaceAll("-", ".");

                            // String for the final product after formatting, and then adding it to the output data.
                            String lineFinal = formattedSplit[0].substring(4) + " " + location + " " + locationTwo;
                            outputData = outputData + "\n" + lineFinal;
                            // Increasing the fix count by 1.
                            count += 1;
                        }
                    }
                }
            }

            // Does the output file exist?
            if(!outputFile.exists()){
                // If not, create, and write the output data to the file.
                outputFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(name);
                // Writing the data to the file.
                fos.write(outputData.getBytes());
                // Flush the fos..
                fos.flush();
                // Close the fos..
                fos.close();
            }

            // Print out statistics.
            System.out.println("Total Fixes: " + count);
            finishTime = System.currentTimeMillis();
            long different = finishTime - startTime;
            System.out.println("Finished in: " + different);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * subStringEnd - Remove ending parts of a string.
     * @param s String you want to remove chars from.
     * @param subString The amount of chars you want to remove from the string/
     * @return Return string without the characters you want.
     */
    public String subStringEnd(String s, int subString){
        // Creating a string with the string provided.
        String fullFormat = s;
        // Remove the ending chars from the string.
        fullFormat = fullFormat.substring(0, s.length() - subString);
        // Return the formatted string.
        return fullFormat;
    }

    /**
     * getEndOfString - Get the end of a string.
     * @param s The string you want to get the ending from.
     * @param subString The amount of chars you want from the end of the string.
     * @return The chars from the end.
     */
    public String getEndOfString(String s, int subString){
        String formatted;
        formatted = s.substring(s.length() - subString);
        return formatted;
    }

    /**
     * reFormatStates - Replaces states (with spaces in the names) with versions without it.
     * @param s Current line of the reader.
     * @return String without states that would break the split.
     */
    public String reFormatStates(String s){
        // I know this is not ideal, but was the easiest way to fix this split issue.
        String formatted = s;
        formatted = formatted.replace("DIST. OF COLUMBIA", "DIST_OF_COLUMBIA");
        formatted = formatted.replace("MIDWAY ATOLL", "MIDWAY_ATOLL");
        formatted = formatted.replace("N MARIANA ISLANDS", "N_MARIANA_ISLANDS");
        formatted = formatted.replace("NEW HAMPSHIRE", "NEW_HAMPSHIRE");
        formatted = formatted.replace("NEW JERSEY", "NEW_JERSEY");
        formatted = formatted.replace("NEW MEXICO", "NEW_MEXICO");
        formatted = formatted.replace("NEW YORK", "NEW_YORK");
        formatted = formatted.replace("NORTH CAROLINA", "NORTH_CAROLINA");
        formatted = formatted.replace("NORTH DAKOTA", "NORTH_DAKOTA");
        formatted = formatted.replace("OFFSHORE ATLANTIC", "OFFSHORE_ATLANTIC");
        formatted = formatted.replace("OFFSHORE GULF", "OFFSHORE_GULF");
        formatted = formatted.replace("OFFSHORE PACIFIC", "OFFSHORE_PACIFIC");
        formatted = formatted.replace("PUERTO RICO", "PUERTO_RICO");
        formatted = formatted.replace("RHODE ISLAND", "RHODE_ISLAND");
        formatted = formatted.replace("SOUTH CAROLINA", "SOUTH_CAROLINA");
        formatted = formatted.replace("SOUTH DAKOTA", "SOUTH_DAKOTA");
        formatted = formatted.replace("VIRGIN ISLANDS", "VIRGIN_ISLANDS");
        formatted = formatted.replace("WEST VIRGINIA", "WEST_VIRGINIA");
        formatted = formatted.replace("RUSSIAN FEDERATION", "RUSSIAN_FEDERATION");
        formatted = formatted.replace("NORTHERN MARIANA ISLANDS", "NORTHERN_MARIANA_ISLANDS");
        formatted = formatted.replace("MARSHALL ISLANDS", "MARSHALL_ISLANDS");
        formatted = formatted.replace("BRITISH WEST INDIES", "BRITISH_WEST_INDIES");
        formatted = formatted.replace("MICRONESIA, FED STATES OF", "MICRONESIA_FED_STATES_OF");
        formatted = formatted.replace("DOMINICAN REPUBLIC", "DOMINICAN_REPUBLIC");
        formatted = formatted.replace("AMERICAN SAMOA", "AMERICAN_SAMOA");
        return formatted;
    }

    public String remove_duplicate(String s) {
        char a[] = s.toCharArray();
        char b[] = new char[a.length];
        if(a.length == sum(a)) {
            return String.valueOf(a[0]);
        }else{
            int i = 1, j = 0, k = 1;
            while(i<a.length) {
                if(a[i] != a[i-1]) {
                    b[j++] = a[i-1];
                }
                i++;
            }
            i--;
            if(a[i] != b[j-1]) {
                b[j]=a[i];
            }
            return String.valueOf(b);
        }
    }

    int sum(char a[]) {
        char b = a[0];
        int i = 1, count = 0;
        while(i<a.length) {
            if(b==a[i]) {
                count++;
            }
            i++;
        }
        return count+1;
    }

}
