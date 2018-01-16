package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ExampleTxtFileBreakUp {

	public static void main (String[] args) {
		System.out.println("running program now...");
		
		ExampleTxtFileBreakUp breakup = new ExampleTxtFileBreakUp();
		
		System.out.println("program has ended. Goodbye!");
		
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public ExampleTxtFileBreakUp() {
		//handles core logic for user's input (what file to breakup, how to break it up)
		
		System.out.println("Welcome! This is 'ExampleTxtFileBreakUp', meant to break up a large non-format-dependent file into smaller files.");
		System.out.println("To cancel at anytime, enter '0' when prompted for input.");
		System.out.println("Please enter the file name you want to break up (with full filepath, please):");
		
		
		
		// read user input to get what file needs to be broken up
		openUserInput();
		String fileUserInput = "";
		String fileEx = "";
		String fileName = "";
		String fileDirectory = "";
		FileReader fileReader;
		BufferedReader bufferedReader;
		try {
			fileUserInput = getUserInput();
			if (fileUserInput.compareTo("0") == 0) {
				System.out.println("You asked to quit. Application will close in 5 seconds.");
				TimeUnit.SECONDS.sleep(5);
				return;
			}
			
			fileEx = fileUserInput.substring(fileUserInput.lastIndexOf('.'));
			fileName = fileUserInput.substring(fileUserInput.lastIndexOf('\\')+1,fileUserInput.lastIndexOf('.'));
			fileDirectory = fileUserInput.substring(0,fileUserInput.lastIndexOf('\\')+1);
			
			System.out.println("You entered with fileDirectory = (" + fileDirectory + "), fileName = (" + fileName + "), fileExtension = (" + fileEx + ")");
			
			fileReader = new FileReader(fileUserInput);
			bufferedReader = new BufferedReader(fileReader);
		} catch (Exception e) {
			System.out.println("Something went wrong when trying to locate the file name and path you entered: " + e.getMessage());
			return;
		}
		
		
		
		// read user input to know how user wants to break up the file
		System.out.println("Please enter how you want to break up the file ('1' = by number of files, '2' = by max file size):");
		
		String breakMethod = "0";
		int numOfFiles = 0;
		float sizeOfFiles = 0;
		try {
			breakMethod = getUserInput();
			if (breakMethod.compareTo("0") == 0) {
				System.out.println("You asked to quit. Application will close in 5 seconds.");
				TimeUnit.SECONDS.sleep(5);
				return;
			}
			
			if (breakMethod.compareTo("1") == 0) {
				System.out.println("Enter the number of files you want to break the main file into:");
				numOfFiles = Integer.parseInt(getUserInput());
				if (numOfFiles == 0) {
					System.out.println("You asked to quit. Application will close in 5 seconds.");
					TimeUnit.SECONDS.sleep(5);
					return;
				} else if (numOfFiles < 1 || numOfFiles > 1000000) {
					System.out.println("Invalid input (we cannot split file into less than 1 file, or into a number greater than 1,000,000). Application will close in 5 seconds.");
					TimeUnit.SECONDS.sleep(5);
					return;
				} else {
					// DO THE WORK! BREAK UP THE FILE!
					splitFileToNumberOfFiles(bufferedReader, fileDirectory, fileName, fileEx, numOfFiles);
				}
			} else if (breakMethod.compareTo("2") == 0) {
				System.out.println("Enter the max size (MB) of each file you want the content to be broken into:");
				sizeOfFiles = Float.parseFloat(getUserInput());		//sizeOfFiles = Integer.parseInt(getUserInput());
				if (sizeOfFiles == 0) {
					System.out.println("You asked to quit. Application will close in 5 seconds.");
					TimeUnit.SECONDS.sleep(5);
					return;
				} else if (sizeOfFiles <= 0 || sizeOfFiles > 1000000) {
					System.out.println("Invalid input (we cannot split file into files the size of a negative number, or into a size greater than 1,000,000 MB). Application will close in 5 seconds.");
					TimeUnit.SECONDS.sleep(5);
					return;
				} else {
					// DO THE WORK! BREAK UP THE FILE!
					splitFileToFilesOfSize(bufferedReader, fileDirectory, fileName, fileEx, sizeOfFiles);
				}
			} else {
				System.out.println("User input was invalid (should have been '1' or '2'). Application will close in 5 seconds.");
				TimeUnit.SECONDS.sleep(5);
				return;
			}
		} catch (Exception e) {
			System.out.println("Something went wrong when reading the input you entered: " + e.getMessage());
			return;
		}
		
		closeUserInput();		
	}
	
	
	
	
	Scanner reader = null;
	private String getUserInput() {
		String returnString = "";
		returnString = reader.nextLine();
		return returnString;
	}
	
	private void openUserInput() {
		reader = new Scanner(System.in);
	}
	
	private void closeUserInput() {
		reader.close();
	}
	
	
	
	
	private void splitFileToNumberOfFiles(BufferedReader b, String fileDirectory, String fileName, String fileExtension, int numOfFiles) {
		System.out.println("Breaking file now into a specific number of files...");
		
		/* This part is an alternative to the logic used below.
		 * good: doesn't read main file once to get line count, so it starts output new files right away (otherwise, may wait a few minutes, unsure if program is running)
		 * bad: due to rounding error, difficult to make exactly the number of files the user requests 
		 * 
		double bigFileSize = ((double)(new File(fileDirectory + fileName + fileExtension)).length()/(double)(1000*1024));
		System.out.println("\t(reusing logic for method that writes files based on file size, a little easier/faster)");
		System.out.println("\t(DISCLAIMER: program may not map data to exact number of files, due to rounding error)");
		System.out.println("\tsize of file in megabytes = " + bigFileSize);
		System.out.println("\tnumOfFiles requested = " + numOfFiles);
		//int sizeOfFiles = (int)Math.ceil(((double)bigFileSize / (double)numOfFiles)) ;
		float roundingError = (float)numOfFiles / 10f;
		float sizeOfFiles = (float)((double)bigFileSize / (double)numOfFiles) ;
		sizeOfFiles = (float) ((Math.ceil((double)(sizeOfFiles * roundingError))) / roundingError);
		System.out.println("\tmax sizeOfFiles set to = " + sizeOfFiles + " MB");
		splitFileToFilesOfSize(b, fileDirectory, fileName, fileExtension, sizeOfFiles);
		*/
		
		int fileLineCount = 0;
		String line = "";
		try {
			while((line = b.readLine()) != null) {
				fileLineCount++;
			}
			b.close();
			
			FileReader fileReader2 = new FileReader(fileDirectory + fileName + fileExtension);

			BufferedReader b2 = new BufferedReader(fileReader2);
			for (int i = 0; i < numOfFiles; i++) {
				BufferedWriter fos;
				fos = new BufferedWriter(new FileWriter(fileDirectory+fileName+String.format("%07d", i)+fileExtension,true));
				for (int j = 0; j < fileLineCount / numOfFiles; j++)
				{
					line = b2.readLine();
					if (line == null) {
						break;
					} else {
						fos.write(line + "\n");
					}
				}
				fos.flush();
				fos.close();
			}
			b2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// good idea to put ".close()" logic in "finally {}" block for safety...
		
		System.out.println("Finished output of files.");
	}
	
	
	
	
	private void splitFileToFilesOfSize(BufferedReader b, String fileDirectory, String fileName, String fileExtension, float sizeOfFiles) {
		System.out.println("Breaking file now into smaller-sized files...");
		
		String line = "";
		int i = 0;
		int currentSize = 0;
		BufferedWriter fos = null;
		try {
			
			fos = new BufferedWriter(new FileWriter(fileDirectory+fileName+String.format("%07d", i)+fileExtension,true));
			while ((line = b.readLine()) != null) {
				line = line + "\n";
				/* Why is 1 MB = 1 KB * 1000 * 1024? 
				 * Online sources say it should be 1 MB = 1 KB * 1024 * 1024.
				 * Could be operating-system dependent... 
				 * */
				if ((currentSize + line.getBytes().length) < (sizeOfFiles * 1000 * 1024)) {
					fos.write(line);
					currentSize += line.getBytes().length;
				} else {
					fos.flush();
					fos.close();
					i++;
					fos = new BufferedWriter(new FileWriter(fileDirectory+fileName+String.format("%07d", i)+fileExtension,true));
					fos.write(line);
					currentSize = 0;
					currentSize += line.getBytes().length;
				}
			}
			
			fos.flush();
			fos.close();
			b.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		// good idea to put ".close()" logic in "finally {}" block for safety...
		
		System.out.println("Finished output of files.");
	}
	
}
