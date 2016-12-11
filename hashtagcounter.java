/**
 * Created by chiranth on 10/12/2016.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
public class hashtagcounter {
    public static void main(String[] args)
    {
        String outfilename="output_file.txt"; /* File to write the result*/
        CFibonacciHeap hashtags=new CFibonacciHeap();/*Inititalize the Heap*/
        Scanner filereader=null;
        BufferedWriter bw=null;
        FileWriter writer=null;
        if(args==null || args.length==0)
        {
            System.out.println("Please pass a proper filename when you run the executable. Program usage is: java hashtagcounter file_name");
            System.out.println("Or you can run the generated jar file as : java -jar hashtagcounter.jar file_name");
            System.exit(0);
        }
        try {
            filereader = new Scanner(new File(args[0]));/* Open the input file from the arguments to read*/
            File outfile = new File(outfilename);
        /* Create the putput file to write*/
            if (!outfile.exists()) {
                outfile.createNewFile();
            }
        /*Open the output file to write*/
            writer = new FileWriter(outfile);
            bw = new BufferedWriter(writer);
            while (filereader.hasNext()) {
                String line = filereader.nextLine();
            /*check if the input read is hash tag*/
                if (line.contains("#")) {
                    String[] parts = line.split(" ");
                    parts[0] = parts[0].substring(1, parts[0].length());
                    hashtags.insert(parts[0], Integer.parseInt(parts[1].trim()));/*insert the hash tag into the heap*/
                } else {
                /*check if the line being read has anything other than stop or the hash tag*/
                    if (!line.toLowerCase().contains("stop")) {
                        int numremovals = Integer.parseInt(line.trim());
                        String output = null, tempout = null;
                        for (int i = 0; i < numremovals; i++) {
                            tempout = hashtags.removemax();   /*remove the max node from the heap*/
                        /*make record of the removed hash tag in order in a string*/
                            if (output == null)
                                output = tempout;
                            else
                                output += "," + tempout;
                        }
                        hashtags.reinsert_delitems();   /* reinsert all the deleted items again*/
                        bw.write(output); /* write the deleted hash tags is order to the file*/
                        bw.newLine();
                    } else {
                        break;
                    }
                }
            }
        }
        catch(IOException ie)
        {
            System.out.println("Exception executing:");
            ie.printStackTrace();
        }
        finally { /* Close the output file handle*/
            try {
                if (filereader != null)
                    filereader.close(); /*Close the input file being read*/
                if (bw != null) {
                    bw.close();
                }
            }
            catch(Exception ie)
            {
                System.out.println("Exception executing:");
                ie.printStackTrace();
            }
        }
        return;
    }

}
