import java.net.*;
import java.io.*;
import java.util.*; //scanner

/*
6.6.18
A file reader that incorporates a search term
Iterates through all (Austlii) files in the search folder.  Assumes they are numbered 1.html...n.html
Requires the dictionary.txt file to be available in classpath at runtime (CSV with terms).
nb java basic inputstream can read a char at a time.  FileReader does this one char at a time with convenience settings.
Scanner class allows quick reading of file into string.
Command line argument is the max number of files in directory.  This app will count down until it finds a valid filename
*/

public class FileSearch {

String searchterm="";
int maxbound=500;
String[] dictItems;
int[][] wordcounts;
String searchfolder = "../austlii/";


//constructor
public FileSearch(String searchString, int max){
 
  this.searchterm=searchString;
  this.maxbound=max;
}

//constructor 1 argument
public FileSearch(int max){
 
  this.searchterm="";
  this.maxbound=max;
}

/* returns 0 if no file found or error; 1 if file found */

private int filecheck(String fileref) {

    try {
    String filepath=this.searchfolder+fileref;
	String content = new Scanner(new File(filepath)).useDelimiter("\\Z").next(); //delimiter: stop at end of file
	System.out.println("Content obtained");	
	if (content==null) {
		System.out.println("No text/html content");
		return 0;
	}
	} catch (Throwable t)
		{
		t.printStackTrace();
		return 0;
		}
	return 1;
}


//read dictionary file for concept descriptions

private int readDictionary() {

	String fileref="dictionary.txt";
	try {
	Scanner sc = new Scanner(new File(fileref)).useDelimiter(",|\\n"); //delimiter: stop at end of file
	if (sc==null) {
		System.out.println("No text/html content");
		return 0;
	}
	int idx=0;
	while (sc.hasNext()) {
		idx++;
		sc.next();
	}
	sc.close();
	String[] content = new String[idx];
	sc = new Scanner(new File(fileref)).useDelimiter(",|\\n");
	idx=0;
	while (sc.hasNext()) {
		content[idx]="";
		content[idx]=sc.next();
		System.out.println(content[idx]);
		idx++;
	}
	System.out.println("dictionary entries:"+content.length);
	this.dictItems=content;
	} catch (Throwable t)
	{
		t.printStackTrace();
		//System.exit(0);
		return 0;
	}
	return 1;
	}

/* returns 0 if no file found or error; 1 if file found 
 prints copy of .html file to disk if found
 prints file name to searchstring file if searchstring found
*/

private int fileread(int fc, int di) {

	String fileref=Integer.toString(fc)+".html";
	String filepath=this.searchfolder+fileref;
	String searchString = dictItems[di];
        try {
	String content = new Scanner(new File(filepath)).useDelimiter("\\Z").next(); //delimiter: stop at end of file
	if (content==null) {
		System.out.println("No text/html content");
		return 0;
	}
	//System.out.println(content);
	System.out.println(fileref+" content obtained");
	int count = checkmatch(fileref,content,searchString);
	wordcounts[fc][di]=count;	
	} catch (Throwable t)
	{
		t.printStackTrace();
		//System.exit(0);
		return 0;
	}
	return 1;
	}

/*
search the passed String, count number of matches of searchstring
try and avoid text inside html tags (e.g. 'font-family') by checking prefix is space or <
return number of matches
*/	

private int checkmatch(String filename,String myFile,String searchString) {

	String reportfile=searchString+".txt";
	//String mString="(?i:[.//s//t//w]*"+searchString+"[.//s//t//w]*)"; //case insensitive.  Z is end of 
	//System.out.println("Checking this search term: "+searchString);
	int startidx=0;
	String file_LC = myFile.toLowerCase(); //for matching
	String search_LC = searchString.toLowerCase(); //for matching.  leading space to find whole words?
	int matchcnt=0;
	int newstart=file_LC.indexOf(search_LC,startidx);
	
	//find all occurences of searchstring in file
    while (newstart!=-1) {
		System.out.println("Match");
		String gettext = file_LC.substring(newstart-1,newstart+search_LC.length()+1);
		System.out.println("Search-1: "+search_LC+" context : "+gettext);
		
		if (gettext.substring(0,1).equals(" ") || gettext.substring(0,1).equals(">") || gettext.substring(0,1).equals("(")) {
			System.out.println("match on family at "+newstart);
			matchcnt++;
		}
		startidx=newstart+1;
		newstart=file_LC.indexOf(search_LC,startidx);
		}
		if (matchcnt>0) {
			searchoutput(searchString,filename,matchcnt);
		}
		//return the count
		return matchcnt;
	}  

//write wordcounts (2D array; instance variable) to CSV file; append

private void writewordcounts(int rows, int cols) {

	try{ 
	PrintStream console = System.out;
	PrintStream outfile = new PrintStream(new FileOutputStream("searchstats.csv",true));
	System.setOut(outfile);
	String rowtoprint = "TOP LEFT,";
	for (int p=0;p<cols;p++) {
		rowtoprint=rowtoprint+dictItems[p]+",";
	}
	System.out.println(rowtoprint);
	for (int x=0;x<rows;x++) {
		rowtoprint=Integer.toString(x)+".html,";
		for (int y=0; y<cols;y++) {
			rowtoprint=rowtoprint+wordcounts[x][y]+",";
		}
	System.out.println(rowtoprint);
	}
	System.setOut(console);
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

//write match statistics to individual files based on keyword

private void searchoutput(String searchString, String filename, int matchcnt) {

	String logString=searchString+" found in :"+filename+" "+matchcnt+" times";
	String reportfile="output/"+searchString+".txt";
	try {
	PrintStream console = System.out;
	PrintStream searchfile = new PrintStream(new FileOutputStream(reportfile,true));
	System.setOut(searchfile);
	System.out.println(logString);
	System.setOut(console);
	}
		catch (Throwable t)
		{
			t.printStackTrace();
			return;
		}
}

//check the highest number of last record (1.html...n.html) in search folder

private int checkbound(int lastRecord) {

	int result=0;
	while (result==0 && lastRecord>0) {
		String myFile=Integer.toString(lastRecord)+".html";
		result = filecheck(myFile);
		if (result==0) {
			lastRecord--;
		}
	}
	System.out.println ("Last Record found: "+lastRecord);
	lastRecord++;
	return lastRecord;
}

//START HERE

public void startAL() {

	readDictionary();
	System.out.println(dictItems.toString());
	int lastRecord=checkbound(this.maxbound);
	wordcounts = new int[lastRecord][dictItems.length];
	PrintStream console = System.out;
	//System.setOut(console);
	for (int y=0; y<dictItems.length;y++) {
		dictionarySearch(lastRecord,y);
	}	
	// Use stored value for output stream
    System.setOut(console);
	System.out.println(wordcounts.toString());
	writewordcounts(lastRecord,dictItems.length);
    System.out.println("END!");
}

//perform search for dictionary word in all search folder files, based on index in argument

private void dictionarySearch(int lastRecord, int di) {

	for (int fc=0;fc<lastRecord;fc++) {
		fileread(fc,di);
	}
}

//Argument options if starting this independently from command line

public static void main(String[] args) throws Exception {
	/*if (args.length!=2) {
		System.out.println ("Start with: FileSearch SEARCHTERM MAXRECORDS");
		return;
	}
	*/
	if (args.length!=1) {
		System.out.println ("Start with: FileSearch MAXRECORDS");
		return;
	}
	/*String srch = args[0];
	int max = Integer.parseInt(args[1]);
	*/
	int max = Integer.parseInt(args[0]);
	//System.out.println("Search: "+srch+" Max#:"+max);
	//FileSearch myFile = new FileSearch(srch,max);
	FileSearch myFile = new FileSearch(max);
	myFile.startAL();
	
	
    }
}

