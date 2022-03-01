package boxes;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import main.BuggyRun;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class ConfirmDecisionBox extends Pane {
	private Rectangle backgroundFromScreen;
	private Rectangle background;
	private Button yes;
	private Button no;
	private Text mainText;
	private Text subText;

	public ConfirmDecisionBox( Rectangle backgroundFromScreen, double height, String mainMessage,
			String nameInMainMessage, String subMessage ) {
		this( backgroundFromScreen, height, mainMessage, nameInMainMessage, subMessage, "Yes", "No" );
	}

	public ConfirmDecisionBox( Rectangle backgroundFromScreen, double height, String mainMessage, String name,
			String subMessage, String yesMessage, String noMessage ) {
		this.backgroundFromScreen = backgroundFromScreen;

		background = new Rectangle( 35 * BuggyRun.gridCellWidth(), height );
		background.setFill( Color.YELLOW );
		background.setStroke( Color.BLACK );
		background.setStrokeWidth( 0.5 * BuggyRun.gridCellWidth() );
		getChildren().add( background );
		setMaxWidth( 35 * BuggyRun.gridCellWidth() );
		setMaxHeight( height );
		setTranslateX( backgroundFromScreen.getWidth() / 2 - background.getWidth() / 2 );
		translateYProperty().bind( backgroundFromScreen.heightProperty().divide( 2 ).subtract( height / 2 ) );

		mainText = new Text();
		mainText.maxWidth( 31.5 * BuggyRun.gridCellWidth() );
		mainText.setFont( Font.font( "Calibri", FontWeight.BOLD, 2.4 * BuggyRun.gridCellWidth() ) );
		mainText.setTranslateX( 1.5 * BuggyRun.gridCellWidth() );
		mainText.setTranslateY( 3.2 * BuggyRun.gridCellWidth() );
		getChildren().add( mainText );
		if ( name != null )
			setMainMessage( mainMessage, name );
		else
			setMainMessage( mainMessage );

		subText = new Text( subMessage );
		subText.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		subText.setFill( Color.DARKRED );
		subText.setWrappingWidth( background.getWidth() - 3 * BuggyRun.gridCellWidth() );
		subText.setTranslateX( 1.5 * BuggyRun.gridCellWidth() );
		subText.setTranslateY( 5.7 * BuggyRun.gridCellWidth() );
		getChildren().add( subText );

		yes = new Button( yesMessage );
		yes.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		yes.setTranslateX( 18 * BuggyRun.gridCellWidth() );
		yes.setTranslateY( height - 4.8 * BuggyRun.gridCellWidth() );
		yes.setFocusTraversable( false );
		getChildren().add( yes );

		no = new Button( noMessage );
		no.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		no.setTranslateX( 1.5 * BuggyRun.gridCellWidth() );
		no.setTranslateY( height - 4.8 * BuggyRun.gridCellWidth() );
		no.setFocusTraversable( false );
		getChildren().add( no );
	}

	public Button getYes() {
		return yes;
	}

	public Button getNo() {
		return no;
	}

	public String getMainMessage() {
		return mainText.getText();
	}

	public void setMainMessage( String mainMessage ) {
		this.mainText.setText( mainMessage );
	}

	public void setMainMessage( String mainMessage, String name ) {
		setMainMessage( mainMessage.replaceAll( "###", name ) );
		
		double width = mainText.computeAreaInScreen() / ( 2.4 * BuggyRun.gridCellWidth() );
		if ( width > 31.5 * BuggyRun.gridCellWidth() && name.length() > 0 ) {
			mainText.setText( mainMessage.replaceAll( "###", name + "..." ) );
			while ( width > 31.5 * BuggyRun.gridCellWidth() && name.length() > 0 ) {
				name = name.substring( 0, name.length() - 1 );
				mainText.setText( mainMessage.replaceAll( "###", name + "..." ) );
				width = mainText.computeAreaInScreen() / ( 2.4 * BuggyRun.gridCellWidth() );
			}
		}
	}

	public String getSubMessage() {
		return this.subText.getText();
	}

	public void setSubMessage( String subMessage ) {
		this.subText.setText( subMessage );
	}

	public double getBackgroundHeight() {
		return background.getHeight();
	}

	public void setBackgroundHeight( double height ) {
		background.setHeight( height );
		translateYProperty().bind(
				backgroundFromScreen.heightProperty().divide( 2 ).subtract( background.heightProperty().divide( 2 ) ) );
		yes.setTranslateY( height - 48 );
		no.setTranslateY( height - 48 );
	}
}