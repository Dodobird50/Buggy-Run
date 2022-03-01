package boxes;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import main.BuggyRun;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Pane;

public class NameInputBoxWithPassword extends Pane {
	private Button ok;
	private Button cancel;
	private TextField nameField;
	private PasswordField passwordField;
	private PasswordField confirmPasswordField;
	private Text errorText;

	public NameInputBoxWithPassword( Rectangle backgroundFromScreen ) {
		Rectangle background = new Rectangle( 35 * BuggyRun.gridCellWidth(), 25 * BuggyRun.gridCellWidth() );
		background.setFill( Color.LIGHTBLUE );
		background.setStroke( Color.BLACK );
		background.setStrokeWidth( 0.6 * BuggyRun.gridCellWidth() );
		getChildren().add( background );
		
		setMaxWidth( 35 * BuggyRun.gridCellWidth() );
		setMaxHeight( 15 * BuggyRun.gridCellWidth() );
		setTranslateX( backgroundFromScreen.getWidth() / 2 - background.getWidth() / 2 );
		translateYProperty().bind( backgroundFromScreen.heightProperty().divide( 2 ).subtract( background.getHeight() / 2 ) );
		
		Text prompt = new Text( "Enter a name and password:" );
		prompt.setFont( Font.font( "Calibri", FontWeight.BOLD, 2 * BuggyRun.gridCellWidth() ) );
		prompt.setTranslateX( 2 * BuggyRun.gridCellWidth() );
		prompt.setTranslateY( 3.5 * BuggyRun.gridCellWidth() );
		getChildren().add( prompt );
		
		nameField = new TextField();
		nameField.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.8 * BuggyRun.gridCellWidth() ) );
		nameField.setPrefWidth( 31 * BuggyRun.gridCellWidth() );
		nameField.setTranslateX( 2 * BuggyRun.gridCellWidth() );
		nameField.setTranslateY( 5 * BuggyRun.gridCellWidth() );
		nameField.setPromptText( "Name" );
		getChildren().add( nameField );
		
		passwordField = new PasswordField();
		passwordField.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.8 * BuggyRun.gridCellWidth() ) );
		passwordField.setPrefWidth( 31 * BuggyRun.gridCellWidth() );
		passwordField.setTranslateX( 2 * BuggyRun.gridCellWidth() );
		passwordField.setTranslateY( 9 * BuggyRun.gridCellWidth() );
		passwordField.setPromptText( "Password" );
		getChildren().add( passwordField );
		
		confirmPasswordField = new PasswordField();
		confirmPasswordField.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.8 * BuggyRun.gridCellWidth() ) );
		confirmPasswordField.setPrefWidth( 31 * BuggyRun.gridCellWidth() );
		confirmPasswordField.setTranslateX( 2 * BuggyRun.gridCellWidth() );
		confirmPasswordField.setTranslateY( 13 * BuggyRun.gridCellWidth() );
		confirmPasswordField.setPromptText( "Confirm password" );
		getChildren().add( confirmPasswordField );

		ok = new Button( "OK" );
		ok.setFont( Font.font( "Calibri", FontWeight.BOLD, 2 * BuggyRun.gridCellWidth() ) );
		ok.setTranslateX( 2 * BuggyRun.gridCellWidth() );
		ok.setTranslateY( 17.5 * BuggyRun.gridCellWidth() );
		ok.setPrefWidth( 9 * BuggyRun.gridCellWidth() );
		ok.setFocusTraversable( false );
		getChildren().add( ok );
		
		cancel = new Button( "Cancel" );
		cancel.setFont( Font.font( "Calibri", FontWeight.BOLD, 2 * BuggyRun.gridCellWidth() ) );
		cancel.setTranslateX( 24 * BuggyRun.gridCellWidth() );
		cancel.setTranslateY( 17.5 * BuggyRun.gridCellWidth() );
		cancel.setPrefWidth( 9 * BuggyRun.gridCellWidth() );
		cancel.setFocusTraversable( false );
		getChildren().add( cancel );
		
		errorText = new Text();
		errorText.setFont( Font.font( "Calibri", 1.6 * BuggyRun.gridCellWidth() ) );
		errorText.setFill( Color.RED );
		errorText.setX( 2 * BuggyRun.gridCellWidth() );
		errorText.setY( 23.5 * BuggyRun.gridCellWidth() );
		getChildren().add( errorText );
	}

	public NameInputBoxWithPassword( Rectangle backgroundFromScreen, String defaultName ) {
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
	
	public String getPassword() {
		return passwordField.getText();
	}
	
	public String getConfirmPassword() {
		return confirmPasswordField.getText();
	}
	
	public void error( String error ) {
		errorText.setText( error );
	}
	
	public void reset() {
		nameField.setText( "" );
		passwordField.setText( "" );
		errorText.setText( "" );
	}
}
