//By Craig Duncan.  Created 19 November 2017
/* Tool to help with statistical Frequency Counts on text e.g. legal document

It turns out that the kind of document can usually be predicted by these factors:
The names of the parties, and the document title are in the top 3 most frequent words?
If we have a Lease, we can see if the occurrence of Shopping & Centre is present and frequent
(that will allow classification just using decision trees, without much sophistication)
Having classified the document, the system can then prompt from the sample clauses/alternates.
*/

//make sure package has DefsContainer and Definition classes

import java.util.*;
import java.io.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
//for pattern matching:
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordTool {
	 //setup (declare) instance variables.  Need the qualifying word 'static' to share one for whole class.
	String filename = ""; //optional filename to use for the WordTool
  int mostCommonLimit=5;

//constructor method with argument
public WordTool (String fname) {
 	this.filename=fname;
}
//constructor no arguments
public WordTool () {
  
}

private TreeMap<Integer, String> makeMapFromDefinitions (DefContainer myContainer, String inputMe) {
    //map structure for counting
    Map<String, Integer> map = new HashMap<String, Integer>();
    //use function String as input for inner Scanner
    Scanner linescan = new Scanner(inputMe);
    //line delimeters: not POSIX {Letter} or numerals 0 to 9 or apostrophe
    linescan.useDelimiter("<tr>[^<]|<tc>|[^\\p{L}0-9']+");
          while (linescan.hasNext()) {
             String thisEntry=linescan.next();
             //No output needed.  System.out.println(thisEntry);
             if (map.containsKey(thisEntry)) {
              int tv = map.get(thisEntry);
              tv++;
              map.put(thisEntry,tv);
             }
             else {
              map.put(thisEntry,1);
             }
          }
          linescan.close();    
      //create a new hashmap with k, v reversed, then Treemap it
        HashMap<Integer, String> myNewHashMap = new HashMap<>();
    for(Map.Entry<String, Integer> entry : map.entrySet()){
      myNewHashMap.put(entry.getValue(), entry.getKey());
      }
      //Use TreeMap for count output
      TreeMap<Integer, String> newTreeMap = new TreeMap<>(myNewHashMap);
      return newTreeMap;
}


private TreeMap<Integer, String> makeMapFromStringCounts(String inputMe) {
    //map structure for counting
    Map<String, Integer> map = new HashMap<String, Integer>();
    //use function String as input for inner Scanner
    Scanner linescan = new Scanner(inputMe);
    //line delimeters: not POSIX {Letter} or numerals 0 to 9 or apostrophe
    linescan.useDelimiter("<tr>[^<]|<tc>|[^\\p{L}0-9']+");
          while (linescan.hasNext()) {
             String thisEntry=linescan.next();
             //No output needed.  System.out.println(thisEntry);
             if (map.containsKey(thisEntry)) {
              int tv = map.get(thisEntry);
              tv++;
              map.put(thisEntry,tv);
             }
             else {
              map.put(thisEntry,1);
             }
          }
          linescan.close();    
      //create a new hashmap with k, v reversed, then Treemap it
        HashMap<Integer, String> myNewHashMap = new HashMap<>();
    for(Map.Entry<String, Integer> entry : map.entrySet()){
      myNewHashMap.put(entry.getValue(), entry.getKey());
      }
      //Use TreeMap for count output
      TreeMap<Integer, String> newTreeMap = new TreeMap<>(myNewHashMap);
      return newTreeMap;
}

/* method to trim String
Here the hashmap is assumed to be k = frequency, v = word
@parameter excludedWords is excluded words that are common filler words will be trimmed
These words tell you something about the language of lawyers too.
@return trimmedTreeMap as trimmed TreeMap.  Notice it is in reverse order so largest counts at top.
TO DO: consider if the words "means" should be removed from here
TO DO: put in external Dictionary; possibly deal with phrases rather than words alone
Also - not using double brace initialisation of ArrayList at this stage.
*/

private TreeMap<Integer, String> trimTreeMap(TreeMap<Integer, String> myTMap) {
      ArrayList<String> excludedWords = new ArrayList<String>(Arrays.asList("amended","amount","arising","before","between","clause","conditions","connection","cost","costs","date","entity","from","including","inserted","means","must","notice","person","proposed","provisions","reasonable","reasonably","relevant","respect","only","other","payment","section","shall","specified","such","terms","that","this","than","time","under","which","will","with","written"));
      TreeMap<Integer, String> trimmedTreeMap = new TreeMap<Integer, String>(Collections.reverseOrder());
      myTMap.forEach((k,v)->
        {
          //introduce threshold for results. e.g. 5+ results, length of 4 or more.  Not excluded.
          //exclude numbers
          Boolean isInteger=false;
          //test if only contains digits.  Eliminates years etc.  if (v.matches("^-?\\d+$");)
          if (v.matches("^\\d+$")) {
            isInteger=true;
          }
          if ((k>5 && v.length()>3) && (excludedWords.contains(v)!=true) && isInteger==false) {
            trimmedTreeMap.put(k,v);
        }
    }
    );
    return trimmedTreeMap;
}

/* Print TreeMap.  No trimming here.  Do prior to method call */

private void printTreeMap(TreeMap<Integer, String> myTMap) {

      myTMap.forEach((k,v)->
        {
          System.out.println(k + " : " + v);
    }
    );
}

/* return the most common entries of TreeMap.  Assumes descending order (i.e. reverse sorted) 
Use Generics here <> and not Raw Types
*/

private void displayMostCommon(TreeMap<Integer, String> myTMap) {
  System.out.println("Top "+Integer.toString(mostCommonLimit)+" Frequency Count:\n ------------------------ \n");
  Set<Map.Entry<Integer,String>> set = myTMap.entrySet();
    Iterator<Map.Entry<Integer,String>> i = set.iterator();
    // Display elements
    int x=0;
    while (i.hasNext() && x<mostCommonLimit) {
      x++;
      Map.Entry<Integer,String> me = i.next();
      System.out.println(me.getKey() + " : "+ me.getValue());
    }
}

/* Makes a string with most common words in file, excluding fill words
It uses the Collections Iterator class method 'next', on the set of Map entries.
It uses the Map.Entry methods getKey() and getValue() to extract items.
All objects use the specific TreeMap data types, not raw types.

Uses 'while' rather than 'for' to ensure there are sufficient words to report on 
*/

private String getMostCommonFromMap(TreeMap<Integer, String> myTMap) {
    String output="";
    Set<Map.Entry<Integer,String>> set = myTMap.entrySet();
    Iterator<Map.Entry<Integer,String>> i = set.iterator();
    // Construct string
    int x=0;
    while (i.hasNext() && x<mostCommonLimit) {
      x++;
      Map.Entry<Integer,String> me = i.next();
      output=output + me.getKey() + " : " + me.getValue() + "\n"; //line return (UNIX Style x0A)
    }
    return output;
}

private String readFile(String fname) {
      String output="";
      Scanner sc = null;
      File myFile = new File(fname);
       //you only need try and catch if the Scanner argument is a disk file.
       try {
        sc = new Scanner(myFile); 
       }
       catch (Exception e) {
                   e.printStackTrace();
                  } 
       while (sc.hasNextLine()) {
       String S = sc.nextLine();
       output = output + S + "\n"; //\ r inserts a \x0D: to preserve LF (as x0A) use \n
       }
       return output;

}

/*  Method to find specific parts of the definition - label and text and store all in a container
Checks for a post-text definition i.e. "A" means B.
catch unicode hyphen and line returns and quotes after 'means' 
TO DO: Capture this in-text definition pattern as an alternative:
[this|the|a] ... ("definition")
    */

  public ClauseContainer doDefTextSearch(String mydata) {
    ClauseContainer myContainer = new ClauseContainer();
    String output="";
    String[] patternString = new String[5];
    Integer[] labelGroup = new Integer[5];
    Integer[] textGroup = new Integer[5];
    //This works for quoted definitions only:
    //OLD: Pattern p = Pattern.compile("\\\"(([\\w\\’' ]*)*[\\w\\’']+)\\\" means[: ]([\\w\\^\\w\\s\\(\\)\\:\\-;,\\/\\’'\\<\\>\\u2013\\u2019\\x0a\\\"]*)\\.");
    String Uni_NonBreakspace="\\u00A0";
    String soft_break="\\x0d";
    String all_breaks="\\x0d\\x0a";
    String Uni_single_qt = "\\u2018\\u2019";
    String Uni_dashes = "\\u2010\\u2011\\u2012\\u2013\\u2014\\u2015"; //u2010 is hyphen
    String Uni_dbl_qt = "\\u201c\\u201d\\u201e\\u201f\\\""; //not using \\x22 for now
    //This LooseRegEx does not include the : ; or . as it assumes they are the end of definition delimiter
    String LooseRegEx="[\\w\\d\\s\\(\\)\\-,\\/\\’'\\<\\>\\[\\]"+soft_break+Uni_dbl_qt+Uni_single_qt+Uni_dashes+Uni_NonBreakspace+" ]*";
    int numPatterns=4;
    //patternString[0]="(?=\\x0A)(([\\w\\’' ]*)[\\w\\’']+) means[\\-\\-:, ]"+LooseRegEx+"\\;"+LooseRegEx+"\\x0A"+"(?=[[A-Z][a-z][0-9],\\’' ]+means)";
    // ----unquoted definitions pattern (e.g. statutes) --- 
    //can't use alternate means or includes because they need to occur at start
    //patternString[0]="\\x0A(([\\w\\’' ]*)*[\\w\\’'\\(\\)]+)(( means| includes)"+LooseRegEx+")(?=([\\;\\.:]\\x0A))";
    //(?<!a)b, which is b not preceeded by a
    //a(?!b), which is a not followed by b
    //OLD:patternString[0]="\\x0A(([\\w\\’' ]*)*[\\w\\’'\\(\\)]+) ((?<!and )includes|means(?! inquiry)"+LooseRegEx+")(?=([\\;\\.:]))";
    patternString[0]="\\x0A([\\w\\’'\\(\\) ]+)* ((?<!and )(includes|means)(?! inquiry)(?! by which)"+LooseRegEx+")(?=([\\;\\.:]\\x0A))";
    labelGroup[0]=1;
    textGroup[0]=2;
    // ----quoted definitions pattern (e.g. contracts) ---   MAYBE COMBINE ALTERNATES
    patternString[1]=Uni_dbl_qt+"(([\\w\\’' ]*)[\\w\\’']+)"+Uni_dbl_qt+" means[\\-:, ]([\\w\\^\\w\\s\\(\\)\\:\\-;,\\/\\’'\\<\\>\\u2013\\u2019\\x0a\\\"]*)\\.";
    labelGroup[1]=1;
    textGroup[1]=3;
    patternString[2]="\\x0A(([\\w\\’' ]*)[\\w\\’']+) means[\\-:, ]([\\w\\^\\w\\s\\(\\)\\:\\-;,\\/\\’'\\<\\>\\u2013\\u2019\\x0a\\\"]*)\\.";
    labelGroup[2]=1;
    textGroup[2]=3;
    // ----quoted definitions pattern (e.g. contracts) ---   original pattern 
    patternString[3]="\\\"(([\\w\\’' ]*)*[\\w\\’']+)\\\" means[: ]([\\w\\^\\w\\s\\(\\)\\:\\-;,\\/\\’'\\<\\>\\u2013\\u2019\\x0a\\\"]*)\\.";
    labelGroup[3]=1;
    textGroup[3]=3;
    //no quotes, but number and dot or bracket through to .
    patternString[4]="\\)|\\.(([\\w\\’' ]*)*[\\w\\’']+) means[: ]([\\w\\^\\w\\s\\(\\)\\:\\-;,\\/\\’'\\<\\>\\u2013\\u2019\\\"]*)\\.|\\x0a";
    labelGroup[4]=1;
    textGroup[4]=3;

    for (int sIndex=0;sIndex<numPatterns;sIndex++) {
    //
    Pattern p = Pattern.compile(patternString[sIndex]);
    Matcher matcher = p.matcher(mydata);
    int groupCount = matcher.groupCount();
    int matchCount=0;
    System.out.println("Text search loop  - sindex: "+sIndex);
    while (matcher.find())
        {
         //System.out.println(matcher.group(1)+" group 2:"+matcher.group(2)+" group 3:"+matcher.group(3));
            for (int i = 1; i <= groupCount; i++) {
                // Group i substring
                System.out.println("Group " + i + ": " + matcher.group(i));
          }
         Clause myDef;  //scope
         String label="";
         label=matcher.group(labelGroup[sIndex]);
         if (!label.equals("") && !label.equals(" ")) {
             String text = matcher.group(textGroup[sIndex]);
             matchCount++; 
             myDef = new Clause(label,label,text,"definition");
             myContainer.addClause(myDef);
          }
        }
              if (matchCount>4) {
              System.out.println("Four matches and exit"+sIndex);
              this.updateClauseFreq(myContainer,mydata);
              return myContainer;
              }
        }
      this.updateClauseFreq(myContainer,mydata);
      return myContainer;
    }

  /* OLD
  public DefContainer doDefTextSearch(String mydata) {
    DefContainer myContainer = new DefContainer();
    String output="";
    String[] patternString = new String[5];
    Integer[] labelGroup = new Integer[5];
    Integer[] textGroup = new Integer[5];
    //This works for quoted definitions only:
    //OLD: Pattern p = Pattern.compile("\\\"(([\\w\\’' ]*)*[\\w\\’']+)\\\" means[: ]([\\w\\^\\w\\s\\(\\)\\:\\-;,\\/\\’'\\<\\>\\u2013\\u2019\\x0a\\\"]*)\\.");
    String Uni_NonBreakspace="\\u00A0";
    String soft_break="\\x0d";
    String all_breaks="\\x0d\\x0a";
    String Uni_single_qt = "\\u2018\\u2019";
    String Uni_dashes = "\\u2010\\u2011\\u2012\\u2013\\u2014\\u2015"; //u2010 is hyphen
    String Uni_dbl_qt = "\\u201c\\u201d\\u201e\\u201f\\\""; //not using \\x22 for now
    //This LooseRegEx does not include the : ; or . as it assumes they are the end of definition delimiter
    String LooseRegEx="[\\w\\d\\s\\(\\)\\-,\\/\\’'\\<\\>\\[\\]"+soft_break+Uni_dbl_qt+Uni_single_qt+Uni_dashes+Uni_NonBreakspace+" ]*";
    int numPatterns=4;
    //patternString[0]="(?=\\x0A)(([\\w\\’' ]*)[\\w\\’']+) means[\\-\\-:, ]"+LooseRegEx+"\\;"+LooseRegEx+"\\x0A"+"(?=[[A-Z][a-z][0-9],\\’' ]+means)";
    // ----unquoted definitions pattern (e.g. statutes) --- 
    //can't use alternate means or includes because they need to occur at start
    //patternString[0]="\\x0A(([\\w\\’' ]*)*[\\w\\’'\\(\\)]+)(( means| includes)"+LooseRegEx+")(?=([\\;\\.:]\\x0A))";
    //(?<!a)b, which is b not preceeded by a
    //a(?!b), which is a not followed by b
    //OLD:patternString[0]="\\x0A(([\\w\\’' ]*)*[\\w\\’'\\(\\)]+) ((?<!and )includes|means(?! inquiry)"+LooseRegEx+")(?=([\\;\\.:]))";
    patternString[0]="\\x0A([\\w\\’'\\(\\) ]+)* ((?<!and )(includes|means)(?! inquiry)(?! by which)"+LooseRegEx+")(?=([\\;\\.:]\\x0A))";
    labelGroup[0]=1;
    textGroup[0]=2;
    // ----quoted definitions pattern (e.g. contracts) ---   MAYBE COMBINE ALTERNATES
    patternString[1]=Uni_dbl_qt+"(([\\w\\’' ]*)[\\w\\’']+)"+Uni_dbl_qt+" means[\\-:, ]([\\w\\^\\w\\s\\(\\)\\:\\-;,\\/\\’'\\<\\>\\u2013\\u2019\\x0a\\\"]*)\\.";
    labelGroup[1]=1;
    textGroup[1]=3;
    patternString[2]="\\x0A(([\\w\\’' ]*)[\\w\\’']+) means[\\-:, ]([\\w\\^\\w\\s\\(\\)\\:\\-;,\\/\\’'\\<\\>\\u2013\\u2019\\x0a\\\"]*)\\.";
    labelGroup[2]=1;
    textGroup[2]=3;
    // ----quoted definitions pattern (e.g. contracts) ---   original pattern 
    patternString[3]="\\\"(([\\w\\’' ]*)*[\\w\\’']+)\\\" means[: ]([\\w\\^\\w\\s\\(\\)\\:\\-;,\\/\\’'\\<\\>\\u2013\\u2019\\x0a\\\"]*)\\.";
    labelGroup[3]=1;
    textGroup[3]=3;
    //no quotes, but number and dot or bracket through to .
    patternString[4]="\\)|\\.(([\\w\\’' ]*)*[\\w\\’']+) means[: ]([\\w\\^\\w\\s\\(\\)\\:\\-;,\\/\\’'\\<\\>\\u2013\\u2019\\\"]*)\\.|\\x0a";
    labelGroup[4]=1;
    textGroup[4]=3;

    for (int sIndex=0;sIndex<numPatterns;sIndex++) {
    //
    Pattern p = Pattern.compile(patternString[sIndex]);
    Matcher matcher = p.matcher(mydata);
    int groupCount = matcher.groupCount();
    int matchCount=0;
    System.out.println("Text search loop  - sindex: "+sIndex);
    while (matcher.find())
        {
         //System.out.println(matcher.group(1)+" group 2:"+matcher.group(2)+" group 3:"+matcher.group(3));
            for (int i = 1; i <= groupCount; i++) {
                // Group i substring
                System.out.println("Group " + i + ": " + matcher.group(i));
          }
         Definition myDef  = new Definition();
         String label="";
         label=matcher.group(labelGroup[sIndex]);
         if (!label.equals("") && !label.equals(" ")) {
             String text = matcher.group(textGroup[sIndex]);
             matchCount++; 
             myDef.setDeflabel(label);
             myDef.setDeftext(text);
             myContainer.addDef(myDef);
          }
        }
              if (matchCount>4) {
              System.out.println("Four matches and exit"+sIndex);
              this.updateDefFreq(myContainer,mydata);
              return myContainer;
              }
        }
      this.updateDefFreq(myContainer,mydata);
      return myContainer;
    }

    */

    /* Method to update the frequency count of the current set of definitions for the given String 
    This will search on the Clause "Label", but it should probably search on Heading, which is specific to the clause;
    the Label is for GUI purposes.
    
    */

  public ClauseContainer updateClauseFreq(ClauseContainer myContainer, String mydata) {
    //iterate again and update the frequency of use of Defs
    //TO DO : Make a hash map instead of arraylist with the definition label and the def object?
    ArrayList<Clause> myDList = myContainer.getClauseArray();
    Iterator<Clause> myiterator = myDList.iterator();
      while (myiterator.hasNext()) {
        Clause mydefinition = myiterator.next();
        String myLabel = mydefinition.getLabel(); //not needed now
        String myHeading = mydefinition.getHeading();
        //String mytext = mydefinition.getDef();
        Pattern pd = Pattern.compile(myHeading);
        Matcher checkDefs = pd.matcher(mydata);
         while (checkDefs.find())
         {
           mydefinition.incFreq();
         }
         String FreqCnt = Integer.toString(mydefinition.getFreq());

         //OK: System.out.println(myLabel+" : "+FreqCnt);
        }    
  return myContainer;
}

/* OLD

    public DefContainer updateDefFreq(DefContainer myContainer, String mydata) {
    //iterate again and update the frequency of use of Defs
    //TO DO : Make a hash map instead of arraylist with the definition label and the def object?
    ArrayList<Definition> myDList = myContainer.getDefArray();
    Iterator<Definition> myiterator = myDList.iterator();
      while (myiterator.hasNext()) {
        Definition mydefinition = myiterator.next();
        String myLabel = mydefinition.getLabel();
        //String mytext = mydefinition.getDef();
        Pattern pd = Pattern.compile(myLabel);
        Matcher checkDefs = pd.matcher(mydata);
         while (checkDefs.find())
         {
           mydefinition.incFreq();
         }
         String FreqCnt = Integer.toString(mydefinition.getFreq());

         //OK: System.out.println(myLabel+" : "+FreqCnt);
        }    
  return myContainer;
}
*/

/*  

  Method to find stautory sections (clauses) and store them in clause objects
    
*/

  public ClauseContainer StatuteSectionImport(String mydata) {
    
    //Pattern p = Pattern.compile("[[a-z][0-9]\\<\\>]*([A-Z' ]{2,}[A-Z'](?=\\x0A|\\x0d))");
    ClauseContainer myContainer=null;
    String[] patternString = new String[4];
    Integer[] groupIn = new Integer[4];
    //setup
    int numPatterns=1;
    /*
    patternString[0]="[[a-z][0-9]\\<\\>]*([A-Z']{1,}( )*[A-Z']*(?=\\x0A|\\<))";
    groupIn[0]=1;
    //number after break, followed by dot and words, ended by period or end of line
    patternString[1]="(\\x0A)([0-9]*\\.( )*[[a-z][A-Z], ]+( |\\.|\\x0A))";
    groupIn[1]=2;
    //several lines before line
    patternString[2]="(\\x0A)*([0-9]*\\.( )*[[a-z][A-Z], ]+( |\\.|\\x0A))";
    groupIn[2]=1;
    */
    //some statutes
    //patternString[0]="(\\x0A)*([0-9]*["+Uni_dashes+"]+[[a-z][A-Z], ]+( |\\.|\\x0A))";
    String Uni_NonBreakspace="\\u00A0";
    String soft_break="\\x0d";
    String all_breaks="\\x0d\\x0a";
    String Uni_single_qt = "\\u2018\\u2019";
    String Uni_dashes = "\\u2010\\u2011\\u2012\\u2013\\u2014\\u2015"; //u2010 is hyphen
    String Uni_dbl_qt = "\\u201c\\u201d\\u201e\\u201f\\\""; //not using \\x22 for now
    //This LooseRegEx does not include the : ; or . as it assumes they are the end of definition delimiter
    String LooseRegEx="[\\w\\d\\s\\(\\)\\:\\-\\;,\\/\\’'\\<\\>\\[\\]"+all_breaks+Uni_dbl_qt+Uni_single_qt+Uni_dashes+Uni_NonBreakspace+" ]*";
    patternString[0]="\\x0A(?=Schedule"+LooseRegEx+"\\x0A+)*(?=Part["+LooseRegEx+"].*\\x0A+)*(?=Division"+LooseRegEx+"\\x0A+)*([0-9]{1,3}[A-Z]{0,1}["+Uni_dashes+"]+[[a-z][A-Z], ]+( |\\.|\\x0A))";
    groupIn[0]=1;
    //last resort
    /* just pick out the numbered paragraphs to the next break or stop.
    This should be a clause extract with headings as 'null' rather than the headings extract.
    May need some operator feedback on results.
    
    patternString[3]="([0-9]*\\.( )*[[a-z][A-Z], ]+\\x0A)";
    groupIn[3]=1;
    */

    for (int sIndex=0;sIndex<numPatterns;sIndex++) {
    Pattern p = Pattern.compile(patternString[sIndex]);
    myContainer = new ClauseContainer();
    String output="";
    System.out.println("Pattern matcher set being considered: "+sIndex);
    Matcher matcher = p.matcher(mydata);
    int groupCount = matcher.groupCount();
    System.out.println("Groupcount : "+groupCount);
    int matchCount=0;
    ArrayList<String> myClauseList = new ArrayList<String>();
    while (matcher.find())
          {
            for (int i = 1; i <= groupCount; i++) {
                // Group i substring output for testing
                System.out.println("Group " + i + ": " + matcher.group(i));
          }
         String headingString = matcher.group(groupIn[sIndex]);
         matchCount++; //no longer needed unless output
         Boolean qualityMatch=false;
         if (headingString.equals("") || headingString.equals(" ") || headingString.length()<3) {
            qualityMatch=false;
         }
         else {
          qualityMatch=true;
          }

         if (qualityMatch==true) {
             //Clause myC  = new Clause();
             Clause myC = new Clause(headingString,headingString,"","clause");
             myClauseList.add(headingString);
             myContainer.addClause(myC);
          }
        }
    System.out.println("Finished Statute Heading search");
    System.out.println("# of Headings Found: "+myClauseList.size()); 
    if (myClauseList.size()>4) {
      return this.StatuteTextExtract(myContainer, mydata);
    }
    }
    //populate clause text before returning
    return this.StatuteTextExtract(myContainer, mydata);
    }


/*  Method to find clauses and store them in clause objects
    Assumes headings are capital letters on a single row
    
*/

  public ClauseContainer ClauseImport(String mydata) {
    
    //Pattern p = Pattern.compile("[[a-z][0-9]\\<\\>]*([A-Z' ]{2,}[A-Z'](?=\\x0A|\\x0d))");
    ClauseContainer myContainer=null;
    String[] patternString = new String[4];
    Integer[] groupIn = new Integer[4];
    //setup
    int numPatterns=4;
    patternString[0]="[[a-z][0-9]\\<\\>]*([A-Z']{1,}( )*[A-Z']*(?=\\x0A|\\<))";
    groupIn[0]=1;
    //number after break, followed by dot and words, ended by period or end of line
    patternString[1]="(\\x0A)([0-9]*\\.( )*[[a-z][A-Z], ]+( |\\.|\\x0A))";
    groupIn[1]=2;
    //several lines before line
    patternString[2]="(\\x0A)*([0-9]*\\.( )*[[a-z][A-Z], ]+( |\\.|\\x0A))";
    groupIn[2]=1;
    //some statutes
    String Uni_dashes = "\\u2010\\u2011\u2012\\u2013\\u2014\\u2015"; //u2010 is hyphen
    patternString[2]="(\\x0A)*([0-9]*["+Uni_dashes+"]+[[a-z][A-Z], ]+( |\\.|\\x0A))";
    groupIn[2]=1;
    //last resort
    /* just pick out the numbered paragraphs to the next break or stop.
    This should be a clause extract with headings as 'null' rather than the headings extract.
    May need some operator feedback on results.
    */
    patternString[3]="([0-9]*\\.( )*[[a-z][A-Z], ]+\\x0A)";
    groupIn[3]=1;

    for (int sIndex=0;sIndex<numPatterns;sIndex++) {
    Pattern p = Pattern.compile(patternString[sIndex]);
    myContainer = new ClauseContainer();
    String output="";
    System.out.println("Pattern matcher set being considered: "+sIndex);
    Matcher matcher = p.matcher(mydata);
    int groupCount = matcher.groupCount();
    System.out.println("Groupcount : "+groupCount);
    int matchCount=0;
    ArrayList<String> myClauseList = new ArrayList<String>();
    while (matcher.find())
          {
            for (int i = 1; i <= groupCount; i++) {
                // Group i substring output for testing
                //System.out.println("Group " + i + ": " + matcher.group(i));
          }
         String headingString = matcher.group(groupIn[sIndex]);
         matchCount++; //no longer needed unless output
         Boolean qualityMatch=false;
         if (headingString.equals("") || headingString.equals(" ") || headingString.length()<3) {
            qualityMatch=false;
         }
         else {
          qualityMatch=true;
          }

         if (qualityMatch==true) {
             //Clause myC  = new Clause();
             Clause myC = new Clause(headingString,headingString,"","clause");
             myClauseList.add(headingString);
             myContainer.addClause(myC);
          }
        }
    System.out.println("Finished Cap Heading search");
    System.out.println("# of Headings Found: "+myClauseList.size()); 
    if (myClauseList.size()>4) {
      return this.ClauseTextExtract(myContainer, mydata);
    }
    }
    //populate clause text before returning
    return this.ClauseTextExtract(myContainer, mydata);
    }

/* 

Method to check results of heading extraction exercise
For now, it just checks whether there are few results. 
TO DO: Check whether Clause headings include the most common words etc and report on that

*/
public Boolean checkHeadingExtraction(ClauseContainer myContainer) {
    ArrayList<Clause> myCList = myContainer.getClauseArray();
    if (myCList.size()<2) {
      return false;
    }
    else {
      return true;
    }

}

    /*

    Statute text extract 
    
    */

    public ClauseContainer StatuteTextExtract(ClauseContainer myContainer, String mydata) {
    System.out.println("Statute text extract");
    ArrayList<Clause> myCList = myContainer.getClauseArray();
    Iterator<Clause> myiterator = myCList.iterator();
    System.out.println("Array Size: "+myCList.size()); //conveniently, ArrayList is in Collections with a size method
    if (myiterator!=null) {
    String[] indexedList = new String[150];
    indexedList[0] = "";
    //String UpperWord="";
    String myRegEx = "";
    //
    String Uni_NonBreakspace="[\\u0000-\\u00FF]"; //non breaking \\u00A0
    String soft_break="\\x0D";
    String all_breaks="\\x0D\\x0A";
    String Uni_single_qt = "\\u2018\\u2019";
    String Uni_dashes = "\\u2010\\u2011\\u2012\\u2013\\u2014\\u2015"; //u2010 is hyphen
    String Uni_dbl_qt = "\\u201c\\u201d\\u201e\\u201f\\\""; //not using \\x22 for now
    //This LooseRegEx does not include the : ; or . as it assumes they are the end of definition delimiter
    String LooseRegEx="[\\w\\d\\s\\(\\)\\:\\-\\;,\\/\\’'\\<\\>\\[\\]"+all_breaks+Uni_dbl_qt+Uni_single_qt+Uni_dashes+Uni_NonBreakspace+" ]*";
    /*
    String LooseRegEx="([\\w\\d\\s\\(\\)\\:\\-\\;\\,\\.\\/\\’'\\<\\>\\[\\]\\u201c\\u201d\\u2013\\u2019\\x0d\\x0a\\\" ]*)";
    */
    //ignore part and divisional headings
    String ignoreText = "(?=Schedule"+LooseRegEx+"\\x0A+)*(?=Part["+LooseRegEx+"].*\\x0A+)*(?=Division"+LooseRegEx+"\\x0A+)*";
    
    String LowerWord="lorem ipsum";
    int indexWindow=0;
    Clause FirstClause=null;
    Clause UpperClause=null;
    Clause LowerClause=null;
    if (myiterator.hasNext()) {
      System.out.println("First has next");
      FirstClause=myiterator.next();
      UpperClause = FirstClause;
      LowerClause = FirstClause;
    }
    while (myiterator.hasNext()) {
        //System.out.println("Inner has next");
        if (indexWindow>0) {
         UpperClause = LowerClause;
        }
        LowerClause = myiterator.next();
        String UpperWord = UpperClause.getHeading();
        LowerWord = LowerClause.getHeading();
        //u2010-u201F is a good range for UTF8
        myRegEx="(?="+UpperWord+")"+LooseRegEx+"(?="+LowerWord+")";
        System.out.println("Now matching: "+myRegEx+" on "+UpperWord);
        Pattern pcl = Pattern.compile(myRegEx);
          //System.out.println(pcl.pattern());
        Matcher clauseCaptcha = pcl.matcher(mydata);
        int clauseMatches = clauseCaptcha.groupCount();
          while (clauseCaptcha.find())
          {
            System.out.println("Pattern: "+myRegEx+" # Group + " + clauseCaptcha.group(0));
            UpperClause.setClausetext(clauseCaptcha.group(0));
          }
          indexWindow++;  
        } 
        //pickup the clause text for last match to end of data String
        myRegEx="(?="+LowerWord+")"+LooseRegEx;
        System.out.println("Now matching: "+myRegEx+" on "+LowerWord);
        Pattern pcl = Pattern.compile(myRegEx);
        Matcher clauseCaptcha = pcl.matcher(mydata);
        int clauseMatches = clauseCaptcha.groupCount();
          while (clauseCaptcha.find())
          {
            System.out.println("Pattern: "+myRegEx+" # Group + " + clauseCaptcha.group(0));
            LowerClause.setClausetext(clauseCaptcha.group(0));
          } 
        } 
      return myContainer;
    }

    /* Method to populate clause text from Clause Headings */
    
    public ClauseContainer ClauseTextExtract(ClauseContainer myContainer, String mydata) {
    System.out.println("Clause text extract");
    ArrayList<Clause> myCList = myContainer.getClauseArray();
    Iterator<Clause> myiterator = myCList.iterator();
    System.out.println("Array Size: "+myCList.size()); //conveniently, ArrayList is in Collections with a size method
    if (myiterator!=null) {
    String[] indexedList = new String[150];
    indexedList[0] = "";
    //String UpperWord="";
    String myRegEx = "";
    //
    String Uni_NonBreakspace="\\u00A0";
    String soft_break="\\x0d";
    String all_breaks="\\x0d\\x0a";
    String Uni_single_qt = "\\u2018\\u2019";
    String Uni_dashes = "\\u2010\\u2011\\u2012\\u2013\\u2014\\u2015"; //u2010 is hyphen
    String Uni_dbl_qt = "\\u201c\\u201d\\u201e\\u201f\\\""; //not using \\x22 for now
    //This LooseRegEx does not include the : ; or . as it assumes they are the end of definition delimiter
    String LooseRegEx="[\\w\\d\\s\\(\\)\\:\\-\\;,\\/\\’'\\<\\>\\[\\]"+all_breaks+Uni_dbl_qt+Uni_single_qt+Uni_dashes+Uni_NonBreakspace+" ]*";
    /*
    String LooseRegEx="([\\w\\d\\s\\(\\)\\:\\-\\;\\,\\.\\/\\’'\\<\\>\\[\\]\\u201c\\u201d\\u2013\\u2019\\x0d\\x0a\\\" ]*)";
    */String LowerWord="lorem ipsum";
    int indexWindow=0;
    Clause FirstClause=null;
    Clause UpperClause=null;
    Clause LowerClause=null;
    if (myiterator.hasNext()) {
      System.out.println("First has next");
      FirstClause=myiterator.next();
      UpperClause = FirstClause;
      LowerClause = FirstClause;
    }
    while (myiterator.hasNext()) {
        //System.out.println("Inner has next");
        if (indexWindow>0) {
         UpperClause = LowerClause;
        }
        LowerClause = myiterator.next();
        String UpperWord = UpperClause.getHeading();
        LowerWord = LowerClause.getHeading();
        //u2010-u201F is a good range for UTF8
        myRegEx="(?="+UpperWord+")"+LooseRegEx+"(?="+LowerWord+")";
        System.out.println("Now matching: "+myRegEx+" on "+UpperWord);
        Pattern pcl = Pattern.compile(myRegEx);
          //System.out.println(pcl.pattern());
        Matcher clauseCaptcha = pcl.matcher(mydata);
        int clauseMatches = clauseCaptcha.groupCount();
          while (clauseCaptcha.find())
          {
            System.out.println("Pattern: "+myRegEx+" # Group + " + clauseCaptcha.group(0));
            UpperClause.setClausetext(clauseCaptcha.group(0));
          }
          indexWindow++;  
        } 
        //pickup the clause text for last match to end of data String
        myRegEx="(?="+LowerWord+")"+LooseRegEx;
        System.out.println("Now matching: "+myRegEx+" on "+LowerWord);
        Pattern pcl = Pattern.compile(myRegEx);
        Matcher clauseCaptcha = pcl.matcher(mydata);
        int clauseMatches = clauseCaptcha.groupCount();
          while (clauseCaptcha.find())
          {
            System.out.println("Pattern: "+myRegEx+" # Group + " + clauseCaptcha.group(0));
            LowerClause.setClausetext(clauseCaptcha.group(0));
          } 
        } 
      return myContainer;
    }

//for other methods to call these are public methods
public void printCountFromFile(String fname) {
  String data = this.readFile(fname);
  TreeMap<Integer, String> dataTM = this.makeMapFromStringCounts(data);
  printTreeMap(trimTreeMap(dataTM));
  displayMostCommon(trimTreeMap(dataTM));

}

public ArrayList commonBoxSet(String data) {
  ArrayList<String> output = new ArrayList();
  //String data = this.readFile(fname);
  TreeMap<Integer, String> dataTM = this.makeMapFromStringCounts(data);
  TreeMap<Integer,String> shortList = trimTreeMap(dataTM);
  //iterator on entry set generated by Map
    Set<Map.Entry<Integer,String>> set = shortList.entrySet();
    Iterator<Map.Entry<Integer,String>> i = set.iterator();
    // Construct string
    int x=0;
    while (i.hasNext() && x<mostCommonLimit) {
      x++;
      Map.Entry<Integer,String> me = i.next();
      output.add(me.getValue());
      //output=output + me.getKey() + " : " + me.getValue() + "\n"; //line return (UNIX Style x0A)
    }
    return output;
}

public String getCommonWordsFromFile(String fname) {
  String data = this.readFile(fname);
  return getCommonWordsFromString(data);
}

public String getFileAsString(String fname) {
  String data = this.readFile(fname);
  return data;
}

public static void main(String args[]){
		/* This is needed if running as a single instance on object creation
    WordTool bob = new WordTool();
		String data = bob.readFile("popstarlease.txt");
    bob.makeMapFromStringCounts(data);
    */
}

public String getCommonWordsFromString(String data) {
  TreeMap<Integer, String> dataTM = this.makeMapFromStringCounts(data);
  return getMostCommonFromMap(trimTreeMap(dataTM));
}

}
