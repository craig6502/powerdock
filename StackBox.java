//FX and events
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
//text
//Scene - Text as text
import javafx.scene.text.Text; 
//Layout - use StackPane for now
import javafx.scene.layout.StackPane;
//Events
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;

/* This class started Nov 2017 
Purpose is to create the principal object to visualise concepts within a GUI (therefore extends StackPane) but also as a container for content/data that can be controller through the object itself.

In many ways it acts as a Controller, and the Graphics Window/Scene is a Controller environment.

In the GUI, the position of instances of this class that are Java FX Nodes (Rectangle/Box and Label) can be sync'ed by changing the position of this object.
Event handlers will move the StackPane inclusive of box and text.

TO DO: 
Graphic window will allow new scene in which opening a StackBox reveals contents like clauses, defs, data, text.
in a similar graphic viewer.
Consider if 'contents' should be a more complex object, rather than text string.
Should the data structure be an array of say Clause (obj), DefsObjs (Array).  Each DefsObj has 'Item'?
Perhaps clauses shouldn't wrap Defs because Defs often have direct relationship to Items/Data.

*/

public class StackBox extends StackPane {         
    //instance variables are the graphic box and its text, and location.
    //Not shared with class unless 'static' applied.
    //StackPane boxPane = new StackPane(); 
    DefBox myBox;
    Text myLabel;
    String contents;
    int Xpos = 0;
    int Ypos = 0;
    Boolean isAlert=false;
    String defaultColour="";
    String alertColour="red";

    //default constructor
    public StackBox(String myLabel) {
    	myBox = new DefBox();
        defaultColour="blue";
        this.setCursor(Cursor.HAND); 
        Text boxtext = new Text (myLabel);//myBox.getLabel();
        //boxtext.setTextAlignment(TextAlignment.CENTER);
        this.getChildren().addAll(myBox,boxtext);
     }  
    // constructor with colour
    public StackBox(String myLabel, String mycolour) {
        //myBox = new DefBox(myText,mycolour);
        myBox = new DefBox(mycolour);
        defaultColour=mycolour;
        this.setCursor(Cursor.HAND); 
        Text boxtext = new Text (myLabel);//myBox.getLabel();
        //boxtext.setTextAlignment(TextAlignment.CENTER);
        this.getChildren().addAll(myBox,boxtext);
     }
    
    public int[] getXY() {
        return new int[]{this.Xpos,this.Ypos};
    }

    public void setXY(int x, int y) {
        this.Xpos=x;
        this.Ypos=y;
    }
     
    public Text getLabel() {
        return myLabel;
    }

    public void setLabel(String myString) {
        this.myLabel.setText(myString);
    }

    public void setContent(String myString) {
        this.contents=myString;
    }

    public String getContent() {
        return this.contents;
    }

    public void SetColour(String mycol) {
        myBox.setColour(mycol);
        this.defaultColour=myBox.getColour();
    }

    public String getColour() {
        return myBox.getColour();
    }

    public Boolean isAlert() {
        return this.isAlert;
    }

    public void doAlert() {
        this.isAlert=true;
        myBox.setColour(alertColour);
    }
     public void endAlert() {
        this.isAlert=false;
        myBox.setColour(defaultColour);
    }

}