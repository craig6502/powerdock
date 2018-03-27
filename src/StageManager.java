/* A Class to hold details for management of the main Clause Sandbox Window 
TO DO: Hold position information for saves? */

//Screen
import javafx.stage.Stage;
import javafx.stage.Screen;
//Screen positioning
import javafx.geometry.Rectangle2D;
import javafx.geometry.Insets;
//import utilities needed for Arrays lists etc
import java.util.*;

/* Stages will always be on top of the parent window.  This is important for layout
Make sure the smaller windows are owned by the larger window that is always visible
The owner must be initialized before the stage is made visible.
*/

public class StageManager {

//hold default Stage variables. TO DO: position relative to screen and then increment.
double latestX = 300;
double latestY = 3000;
String StageFocus = "";
Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
double myBigX = ScreenBounds.getWidth();
double myBigY = ScreenBounds.getHeight();
ArrayList<Stage> myStageList = new ArrayList<Stage>();
//TO DO: Use 'Project' concept to save StageLists and containers
//TO DO: Store current Stage selection.
Stage currentStage=new Stage();

//constructor
public StageManager() {

}

    /* A default screen position 

    Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
            double mySetX = ScreenBounds.getWidth() / 1.8;
            myStage.setX(mySetX);
            myStage.setY(450);

    */

    /*Editor panel
    //Layout
    Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
    double mySetX = ScreenBounds.getWidth() / 1.1;
    //myStage.setX(500);
    myStage.setX(mySetX);
    myStage.setY(150);
    */

    /*Toolbar panel
    Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
        double mySetX = ScreenBounds.getWidth() / 1.1;
        //myStage.setX(500);
        myStage.setX(mySetX);
        myStage.setY(50);

    */

    /*textmaker panel 
    //Layout
        Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
        //inspector
        double mySetX = ScreenBounds.getWidth() / 1.8;
        mySetX = ScreenBounds.getWidth() / 1.5; 
        myStage.setX(mySetX); 
        myStage.setScene(defScene);

        */

//method to set current Stage selection
//updated by listeners ?
//nb another option is to update from clausecontainer change
public void setCurrentStage(Stage myStage) {
    this.currentStage = myStage; //points to currentStage rather than index
}

//method to get current Stage stored here.
//Value in this object is updated by listeners

public Stage getCurrentStage() {
    return this.currentStage; //points to currentStage rather than index
}

//getters and setters
public void setCurrentXY(double x, double y) {

	this.latestX=x;
    this.latestY=y;
}

/*Method to set parents.  Call this before showing stage*/
public void setStageParent(Stage Parent, Stage myStage) {
    myStage.initOwner(Parent);
}

/* These are the main Stages that allow toggle views

The order in which the Stages are created and set will determine initial z order for display
Earliest z is toward back
The workspace is, in effect, a large window placed at back.
TO DO: Make the MenuBar etc attach to a group that is at back,
then add WIP spritexboxes to a 'Document Group' that replaces Workspace with 'Document' menu
*/

public void setPosition(Stage myStage, String myCategory) {

    switch(myCategory){

            case "workspace":
                myStage.setX(0);
                myStage.setY(0);
                myStage.toBack();
                break;

            case "editor":
                //myStage.initOwner(Parent);  //this must be called before '.show()' on child
                myStage.setX(850); //TO DO: Relative
                myStage.setY(0);
                myStage.toFront();
                break;

            case "library":
                //myStage.initOwner(Parent);
                myStage.setX(1000); //1440 is about 71%
                myStage.setY(300); 
                myStage.toFront();
                break;

            case "document":
                //myStage.initOwner(Parent);
                myStage.setX(800); //1440 is about 71%
                myStage.setY(200); 
                myStage.toFront();
                break;

            case "toolbar":
                //myStage.initOwner(Parent);
                myStage.setX(1000); //1440 is about 71%
                myStage.setY(50); //900 is abot 0.05%
                myStage.toFront();
                break;

            case "textmaker":  
                //myStage.initOwner(Parent);
                myStage.setX(150);
                myStage.setY(550);
                myStage.toFront();
                break;

            case "importwindow":
                //myStage.initOwner(Parent);
                myStage.setX(800);
                myStage.setY(200);
                myStage.toFront();
                break;
            
            case "display":  
                //myStage.initOwner(Parent);
                myStage.setX(800);
                myStage.setY(550);
                myStage.toFront();
                break;
            
            case "icons":
                //myStage.initOwner(Parent);
                myStage.setX(800);
                myStage.setY(550);
                myStage.toFront();
                break;
                   
            default:
                //myStage.initOwner(Parent);
                myStage.setX(200);
                myStage.setY(200);
                myStage.toFront();
                break;
    }
}

public void incrementXY() {

    this.latestX=this.latestX+50;
    this.latestY=this.latestY+50;
}

public void resetXY() {

    this.latestX=50;
    this.latestY=50;
}

public double getX() {
    return this.latestX;
}

public double getY() {
    return this.latestY;
}

//max screen dimensions
public double getBigX() {
    return this.myBigX;
}


public double getBigY() {
    return this.myBigY;
}

}