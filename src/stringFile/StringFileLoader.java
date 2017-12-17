/*
 * Copyright (c) 2015, Carlo Cervellin. All rights reserved.
 * Java is a property of Oracle. Use is subject to license terms.
 * 
 * The use of this code is allowed with an explicit citation of 
 * the author (me) in the final product if and only if it has
 * not commercial purposes. 
 * Otherwise my code might be used under my personal approval.
 * 
 * Contact me: carlocervellin@gmail.com
 * 
 * 
 * 
 **/
package stringFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * @author Carlo Cervellin
 * @since JDK 1.8.0_65
 * @version 1.0.0
 *
 */
public class StringFileLoader {
	
	public String utfFormat = "UTF-8";
	
	public static final String UTF8 = "UTF-8";
	public static final String UTF16 = "UTF-16";
	public static final String UTF32 = "UTF-32";
	public static final String UTF64 = "UTF-64";
	
	private String path;
	public String[] fileContent;
	private int linePointer;
	
	
	/**
	 * Creator of the object {@code SrtingFileLoader}.
	 * @param path	the path from witch to load the content of the file.
	 */
	public StringFileLoader(String path) {
		this.path=path;
		resetPointer();
		try {
			loadContent();
		} catch (FileNotFoundException e) {
			try {
				createFile();
			} catch (PathMissingException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Creator of the object {@code SrtingFileLoader}.
	 * 
	 * @param path	the path from witch to load the content of the file.
	 * @param utfFormat	the UTF format which you want to use.
	 */
	public StringFileLoader(String path,String utfFormat) {
		this(path);
		this.utfFormat=utfFormat;
	}
	
	/**
	 * @return
	 * @throws PathMissingException
	 */
	public String getFormat() throws PathMissingException {
		return getFormat(path);
	}
	
	/**
	 * @param path
	 * @return
	 * @throws PathMissingException
	 */
	private static String getFormat(String path) throws PathMissingException {
		try {
			int index=path.length()-1;
				try {
				while(path.charAt(index)!='.') index--;			
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Illegible format for file: "+path);
			} catch (StringIndexOutOfBoundsException e1) {
				throw new PathMissingException();
			}
			return path.substring(index+1);
		} catch (NullPointerException e) {
			throw new PathMissingException();
		}
		
	}

	/**
	 * Sets the {@code linePointer} to {@code -1}.
	 * <p>
	 * WARNING: the current line will not be readable, reading allowed using {@code nextLine()},
	 */
	public void resetPointer() {
		setLinePointer(-1);
	}
	
	/**
	 * Sets the {@code linePointer} to the given value.
	 * @param pointer the value to set the {@code linePointer} at.
	 * @exception AssertionError if the {@code pointer} is bigger than the
	 * 				{@code fileContent} length or less than {@code -1}.
	 */
	public void setLinePointer(int pointer) {
		assert (pointer>=-1 && pointer<fileContent.length);
		linePointer=pointer;
	}
	
	/**
	 * Initializes the {@code String} array {@code fileContent} by loading
	 * in each {@code String} a line of the file.
	 * @throws FileNotFoundException if there is no file at the location
	 * 			given by the local field {@code path}.
	 */
	private void loadContent() throws FileNotFoundException {
		try {
			fileContent=readFile(this.getPath());
		} catch (PathMissingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This file saves the {@code fileContent} at the path give by
	 * the local variable {@code path} (overwriting eventual older file) 
	 * and then loads the file back again into this object.
	 */
	public void refresh() {
		this.save();
		try {
			this.loadContent();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Jumps to the next line by increasing the {@code linePointer}.
	 */
	public void nextLine() {
		linePointer++;
	}
	
	/**
	 * Jumps to the next line by increasing the {@code linePointer} and
	 * returns the {@code String} value of the new line.
	 * @return		the {@code String} content of the line after the {@code linePointer}.
	 */
	public String nexLine() {
		linePointer++;
		return getLine(linePointer);
	}
	
	/**
	 * @return	the {@code String} content of the line at the {@code linePointer}.
	 */
	public String getCurrentLine() {
		return getLine(linePointer);
	}
	
	/**
	 * This method allows you to get the {@code String} content of the
	 * desired line.
	 * <p>
	 * ATTENTION: the lines number starts from {@code 0}.
	 * @param 	number the number of the line you want to read.
	 * @return	the {@code String} contained in desired line.
	 * @throws	ArrayIndexOutOfBoundsException	if the {@code number} of 
	 * 				the line gets out of bounds of the {@code String} array {@code fileContent}
	 */
	public String getLine(int number) {
		return fileContent[number];
	}
	
	/**
	 * Prints to the console the content of the {@code String} under 
	 * the given index.
	 * @param number	the number of the line you want to print.
	 */
	public void printLine(int number) {
		System.out.println(this.getLine(number));
	}
	
	/**
	 * Prints to the console the content of the {@code String} under 
	 * the index {@code linePointer}.
	 */
	public void printCurrentLine() {
		printLine(linePointer);
	}
	
	/**
	 * Prints the value of the {@code String} obtained with the method {@code nextLine()}.
	 * @see #nextLine()
	 */
	public void printNextLine() {
		System.out.println(this.nexLine());
	}
	
	/**
	 * Reinitializes the {@code String} array {@code fileContent} with a new
	 * empty array.
	 */
	public void clear() {
		this.fileContent=new String[0];
	}
	
	/**
	 * 
	 */
	public String toString() {
		String result = "";
		try {
			result = "File path: "+this.getPath()+"\n";
		} catch (PathMissingException e) {
			result = "Path missing";
		}
		if (fileContent==null) {
			result+="Null content";
			return result;
		}
		if (fileContent.length==0) {
			result+="Empty content";
			return result;
		}
		result+="[content]\n";
		for (int i=0; i<fileContent.length; i++) {
			result+=fileContent[i]+"\n";
		}
		return result;
	}
	
	/**
	 * Prints all the {@code String}s contaned into the {@code fileContent} array.
	 */
	public void printFileContent() {
		for(int i=0; i<fileContent.length;i++) {
			this.printLine(i);
		}
	}
	
	/**
	 * Prints this object.
	 * @see #toString()
	 */
	public void print() {
		System.out.println(this);
	}
	
	/**
	 * Saves the content of this object into a file with the path contaied
	 * into the {@code path} field by overwriting it in the case it alredy exists
	 * or by making a new one in the case it does not.
	 */
	public void save() {
		if (path==null)
			try {
				throw new PathMissingException();
			} catch (PathMissingException e1) {
				e1.printStackTrace();
			}
		PrintWriter writer;
		try {
			writer = new PrintWriter(path, utfFormat);
			for (int i=0; i<fileContent.length; i++) {
				writer.println(fileContent[i]);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a string in a new line at the end of the array {@code fileContent}.
	 * @param newLine	the content of the line we want to insert.
	 */
	public void addNewLine(String newLine) {
		this.addNewLine();
		this.fileContent[fileContent.length-1] = newLine;
	}
	
	/**
	 * Adds a new line at the end of the array {@code fileContent}.
	 */
	public void addNewLine() {
		if (fileContent == null) this.fileContent=new String[0];
		String[] newContent = new String[fileContent.length+1];
		for (int i=0; i<newContent.length; i++) {
			try {
				newContent[i]=fileContent[i];
			} catch (ArrayIndexOutOfBoundsException e) {}
		}
		fileContent=newContent;
	}
	
	/**
	 * 
	 * @return	the path of the loaded file.
	 * @throws PathMissingException	if the path has not been initialized.
	 */
	public String getPath() throws PathMissingException {
		if (path==null) throw new PathMissingException();
		return path;
	}
	
	/**
	 * Changes the path of the object.
	 * <p>
	 * WARNING: does not save the object in the new path.
	 * @param newPath	the new path.
	 */
	public void setNewPath(String newPath) {
		path = newPath;
	}
	
	/**
	 * Changes the path of the object.
	 * <p>
	 * WARNING: does not save the object in the new path.
	 * @param newPath	the new path.
	 */
	public void setPath(String newPath) {
		setNewPath(newPath);
	}
	
	/**
	 * Makes a new file in the position of the {@code String path}.
	 * @throws PathMissingException	if the path has been not initialized.
	 */
	private void createFile() throws PathMissingException {
		createNewFile(this.getPath());
	}
	
	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String[] readFile(String filePath) throws FileNotFoundException {
		BufferedReader br = null;
		BufferedReader br1 = null;
		String[] result=null;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(filePath));
			br1 = new BufferedReader(new FileReader(filePath));
			int numberOfLines=0;
			while ((sCurrentLine = br1.readLine()) != null) numberOfLines++;
			result = new String[numberOfLines];
			int i=0;
			while ((sCurrentLine = br.readLine()) != null) {
				result[i]=sCurrentLine;
				i++;
			}
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (br != null)br.close();
				if (br1 != null)br1.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param filePath
	 */
	public static void createNewFile(String filePath) {
		File f = new File(filePath);
        try {
        	f.getParentFile().mkdirs();
        } catch (NullPointerException e) {}
        if (!f.exists()){
        	try {
        		f.createNewFile();
        	} catch (IOException e) {
			e.printStackTrace();
			}
        } 
		
	}

}
