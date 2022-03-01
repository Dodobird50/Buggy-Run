package boxes;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import main.BuggyRun;

public class LoginBox extends Pane {
	private Rectangle background;

	private TextField nameField;
	private TextField passwordField; 
	private Button cancel;
	private Button next;
	private Text errorText;

	public LoginBox( Rectangle backgroundFromScreen ) {
		background = new Rectangle( 35 * BuggyRun.gridCellWidth(), 20 * BuggyRun.gridCellWidth() );
		background.setFill( Color.YELLOW );
		background.setStroke( Color.BLACK );
		background.setStrokeWidth( 0.6 * BuggyRun.gridCellWidth() );
		getChildren().add( background );
		setMaxWidth( 35 * BuggyRun.gridCellWidth() );
		setMaxHeight( 15 * BuggyRun.gridCellWidth() );
		setTranslateX( backgroundFromScreen.getWidth() / 2 - background.getWidth() / 2 );
		setTranslateY( backgroundFromScreen.getHeight() / 2 - background.getHeight() / 2 );

		Text prompt = new Text( "Enter name and password:" );
		prompt.setTranslateX( 2 * BuggyRun.gridCellWidth() );
		prompt.setTranslateY( 3.5 * BuggyRun.gridCellWidth() );
		prompt.setFont( Font.font( "Calibri", FontWeight.BOLD, 2.4 * BuggyRun.gridCellWidth() ) );
		getChildren().add( prompt );
		
		nameField = new TextField();
		nameField.setTranslateX( 2 * BuggyRun.gridCellWidth() );
		nameField.setTranslateY( 5 * BuggyRun.gridCellWidth() );
		nameField.setMinWidth( 31 * BuggyRun.gridCellWidth() );
		nameField.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		nameField.setPromptText( "Name" );
		getChildren().add( nameField );

		passwordField = new PasswordField();
		passwordField.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		passwordField.setTranslateX( 2 * BuggyRun.gridCellWidth() );
		passwordField.setTranslateY( 9 * BuggyRun.gridCellWidth() );
		passwordField.setMinWidth( 31 * BuggyRun.gridCellWidth() );
		passwordField.setPromptText( "Password" );
		getChildren().add( passwordField );
		
		
		cancel = new Button( "Cancel" );
		cancel.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		cancel.setTranslateX( 2 * BuggyRun.gridCellWidth() );
		cancel.setTranslateY( 13 * BuggyRun.gridCellWidth() );
		cancel.setPrefWidth( 8 * BuggyRun.gridCellWidth() );
		cancel.setFocusTraversable( false );

		next = new Button( "Next" );
		next.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		next.setTranslateX( 25 * BuggyRun.gridCellWidth() );
		next.setTranslateY( 13 * BuggyRun.gridCellWidth() );
		next.setPrefWidth( 8 * BuggyRun.gridCellWidth() );
		next.setFocusTraversable( false );
		getChildren().addAll( cancel, next );
		
		errorText = new Text();
		errorText.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		errorText.setFill( Color.RED );
		errorText.setTranslateX( 2 * BuggyRun.gridCellWidth() );
		errorText.setTranslateY( 18 * BuggyRun.gridCellWidth() );
		errorText.setFocusTraversable( false );
		getChildren().add( errorText );
	}

	public String getName() {
		return nameField.getText();
	}
	
	public String getPassword() {
		return passwordField.getText();
	}

	public Button getNext() {
		return next;
	}

	public Button getCancel() {
		return cancel;
	}
	
	public void setError( boolean error ) {
		if ( error  )
			errorText.setText( "Incorrect name and/or password" );
		else if ( !error )
			errorText.setText( "" );
	}

	// Clears name and password
	public void reset() {
		nameField.setText( "" );
		passwordField.setText( "" );
		errorText.setText( "" );
	}
	
	public TextField getNameField() {
		return nameField;
	}
	
	public TextField getPasswordField() {
		return passwordField;
	}
}
