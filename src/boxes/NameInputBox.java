package boxes;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import main.BuggyRun;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class NameInputBox extends Pane {
	private Button ok;
	private Button cancel;
	private TextField nameField;

	public NameInputBox( Rectangle backgroundFromScreen ) {
		Rectangle background = new Rectangle( 35 * BuggyRun.gridCellWidth(), 16 * BuggyRun.gridCellWidth() );
		background.setFill( Color.LIGHTBLUE );
		background.setStroke( Color.BLACK );
		background.setStrokeWidth( 0.6 * BuggyRun.gridCellWidth() );
		getChildren().add( background );
		
		setMaxWidth( 35 * BuggyRun.gridCellWidth() );
		setMaxHeight( 15 * BuggyRun.gridCellWidth() );
		setTranslateX( backgroundFromScreen.getWidth() / 2 - 17.5 * BuggyRun.gridCellWidth() );
		setTranslateY( backgroundFromScreen.getHeight() / 2 - 8 * BuggyRun.gridCellWidth() );
		
		Text prompt = new Text( "Enter your name:" );
		prompt.setFont( Font.font( "Calibri", FontWeight.BOLD, 2 * BuggyRun.gridCellWidth() ) );
		prompt.setTranslateX( 2 * BuggyRun.gridCellWidth() );
		prompt.setTranslateY( 3.5 * BuggyRun.gridCellWidth() );
		getChildren().add( prompt );
		
		nameField = new TextField();
		nameField.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.8 * BuggyRun.gridCellWidth() ) );
		nameField.setPromptText( "Name" );
		nameField.setPrefWidth( 31 * BuggyRun.gridCellWidth() );
		nameField.setTranslateX( 2 * BuggyRun.gridCellWidth() );
		nameField.setTranslateY( 5.5 * BuggyRun.gridCellWidth() );
		getChildren().add( nameField );
		
		ok = new Button( "OK" );
		ok.setFont( Font.font( "Calibri", FontWeight.BOLD, 2 * BuggyRun.gridCellWidth() ) );
		ok.setTranslateX( 2 * BuggyRun.gridCellWidth() );
		ok.setTranslateY( 10 * BuggyRun.gridCellWidth() );
		ok.setPrefWidth( 9 * BuggyRun.gridCellWidth() );
		ok.setFocusTraversable( false );
		getChildren().add( ok );
		
		cancel = new Button( "Cancel" );
		cancel.setFont( Font.font( "Calibri", FontWeight.BOLD, 2 * BuggyRun.gridCellWidth() ) );
		cancel.setTranslateX( 24 * BuggyRun.gridCellWidth() );
		cancel.setTranslateY( 10 * BuggyRun.gridCellWidth() );
		cancel.setPrefWidth( 9 * BuggyRun.gridCellWidth() );
		cancel.setFocusTraversable( false );
		getChildren().add( cancel );
	}

	public NameInputBox( Rectangle backgroundFromScreen, String defaultName ) {
		this( backgroundFromScreen );
		nameField.setText( defaultName );
	}
	
	public Button getOk() {
		return ok;
	}

	public Button getCancel() {
		return cancel;
	}

	public String getName() {
		return nameField.getText();
	}
}