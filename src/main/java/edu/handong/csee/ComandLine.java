package edu.handong.csee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.text.SimpleDateFormat;

public class ComandLine {
	private String currentDir;
	private File location;
	private String[] filesAndDirs;
	private String[] ignFilesAndDirs;
	private boolean indexL, indexA, indexR, indexLT, indexF;
	
	public static void main(String[] args) throws IOException {
		ComandLine coml = new ComandLine();
		coml.run(args);
	}
	
	public void run(String[] args) throws IOException {
		
		Options options = createOption();
		if ( parseOption(options, args) ) {
			Ls(getDir(), true);
			
			if(indexF) LsF();
			System.out.println();
			
			if(indexA) LsA();
			System.out.println();
			
			if(indexR) LsR(getDir());
			System.out.println();
			
			if(indexL) LsL();
			System.out.println();
			
			if(indexLT) LsT();
		} 
	}
	public String getDir() {
		currentDir = System.getProperty("user.dir");
		return currentDir;
	}
	public String[] deleteIgnore(String[] fd) {
		String[] nextArr = null;
		int i = 0;
		
		for( String str : fd) {
			if(str.charAt(0)!='.') {
				nextArr[i]=str;
				i++;
			}
		}
		return nextArr;
	}
	
	
	public void LsR(String path) {
		int flag = 0;
		Ls(path, true);
		
		System.out.print(location.getAbsolutePath() + " : \n");
		for( String str : getFilesAndDirs()) {
			System.out.println(str);
		}
		System.out.println();
		for( String str : getFilesAndDirs()) {
			File newFile = new File(str);
			
			if(newFile.isDirectory()) {
				flag = 1;
				//System.out.print(newFile.getAbsolutePath() + " : \n");
				LsR(str);
			}
		}
		
		if(flag == 0) return;
	}
	
	public void Ls(String path, boolean in) {
		location = new File(path);
		filesAndDirs = location.list();
		int i = 0;
		if(in) {
			ArrayList<String> nextArry = new ArrayList<String>();
			i = 0;
			
			for( String str : filesAndDirs) {
				if(str.indexOf('.')==0) {
				}
				else {	nextArry.add(str); }
			}
			
			ignFilesAndDirs = new String[nextArry.size()];
			for( String str: nextArry) {
				ignFilesAndDirs[i] = str;
				i++;
			}
		}
		//filesAndDirs.
	}
	
	public String[] getFilesAndDirs() {
		return ignFilesAndDirs;
	}
	public void setFilesAndDirs(String[] filesAndDirs) {
		this.filesAndDirs = filesAndDirs;
	}
	
	public void LsL() throws IOException {
		//drwxr-xr-x  6 seulgi  staff   192  6 19 01:13 bin
		//ArrayList<String> index = new ArrayList<String>();
		Ls(currentDir, true);
		for( int i=0; i<ignFilesAndDirs.length; i++) {
			Path path = Paths.get(ignFilesAndDirs[i]);
			
			FileOwnerAttributeView view = Files.getFileAttributeView(path,FileOwnerAttributeView.class);
			UserPrincipal userPrincipal = view.getOwner();
			
			PosixFileAttributes attrs = Files.readAttributes(path, PosixFileAttributes.class);
			Set<PosixFilePermission> posixPermissions = attrs.permissions();
			// posixPermissions.clear();
			String perms = PosixFilePermissions.toString(posixPermissions);
			
			String index;
			File fstr = new File(ignFilesAndDirs[i]);
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			
			if(fstr.isDirectory()) {index="d";} else {index="-";}
			System.out.print(index + perms + " " + userPrincipal.getName() + " "+sdf.format(fstr.lastModified())+" ");
			
			System.out.print(fstr.getName() + " \n");
			System.out.println();
		}
	}
	
	public void LsT() throws IOException {
		for( int i=0; i<ignFilesAndDirs.length; i++) {
			Path path = Paths.get(ignFilesAndDirs[i]);
			File file = new File(ignFilesAndDirs[i]);
			long length = file.length();
			
			FileOwnerAttributeView view = Files.getFileAttributeView(path,FileOwnerAttributeView.class);
			UserPrincipal userPrincipal = view.getOwner();
			
			PosixFileAttributes attrs = Files.readAttributes(path, PosixFileAttributes.class);
			Set<PosixFilePermission> posixPermissions = attrs.permissions();
			// posixPermissions.clear();
			String perms = PosixFilePermissions.toString(posixPermissions);
			
			String index;
			File fstr = new File(ignFilesAndDirs[i]);
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			
			if(fstr.isDirectory()) {index="d";} else {index="-";}
			System.out.print(index + perms + " " + userPrincipal.getName() + " ["+length+"]BYTES  "+sdf.format(fstr.lastModified())+" ");
			
			System.out.print(fstr.getName() + " \n");
		}
	}
	
	public void LsF() {
		int index = 0;
		for( String str : filesAndDirs) {
			System.out.print("\n");
			System.out.print(str + "\t");
			index++;
		}
	}
	
	public void LsA() {
		int index = 0;
		Arrays.sort(filesAndDirs);
		for( String str : filesAndDirs) {
			//if( index%5 == 0 ) 
			System.out.print("\n");
			System.out.print(str + "\t");
			index++;
		}
	}
	
	private boolean parseOption(Options options, String[] args) {
		
		CommandLineParser parser = new DefaultParser();

		try {
			CommandLine cmd = parser.parse(options, args);
			
			indexL = cmd.hasOption("l");
			indexA = cmd.hasOption("a");
			indexR = cmd.hasOption("R");
			indexLT = cmd.hasOption("lt");
			indexF = cmd.hasOption("f");
			
		} catch(Exception e) { //최상위 클래스 넣어서 한번에 처리, exception 나오면 도움말 출력  
			System.out.println("<<This is option problem>>");
			printHelp(options);
		 	System.exit(0);
		}
		return true;
	}

	private void printHelp(Options options) {
		// TODO Auto-generated method stub
		HelpFormatter Formatter = new HelpFormatter(); // 도움말 자동으로 만들어주는 클래
		String header = "ls";
		String footer = "";
		Formatter.printHelp("ls", header, options, footer, true);
	}

	// DEFINITION
	private Options createOption() {
		Options options = new Options();
		
		options.addOption(Option.builder("l").longOpt("long")
				.desc("Display long formats") // description
				//.hasArg() //값받아야 하니
				.argName("Long format") //argument name이 어떤 걸 의미하는지 보여주는 역
				//.required()
				.build()); //반드시 필요하다는 걸 의미, 안들어오면 exception 발생.
		
		options.addOption(Option.builder("a").longOpt("all")
				.desc("List all directory") // description
				//.hasArg() //값받아야 하니
				.argName("all name") //argument name이 어떤 걸 의미하는지 보여주는 역
				//.required()
				.build());
		
		options.addOption(Option.builder("R").longOpt("recursive")
				.desc("recursively lists subdirectories") // description
				.argName("recursive") //argument name이 어떤 걸 의미하는지 보여주는 역       
				.build());
		
		options.addOption(Option.builder("lt").longOpt("time")
				.desc("sort the list of files by modification time") // description
				.argName("time") //argument name이 어떤 걸 의미하는지 보여주는 역       
				.build());
		
		options.addOption(Option.builder("f").longOpt("files")
				.desc("do not sort.") // description
				.argName("files") //argument name이 어떤 걸 의미하는지 보여주는 역       
				.build());
		
		return options;
	}
}
