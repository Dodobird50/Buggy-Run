package pre_game_panes;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.BuggyRun;
import java.io.*;

public class SettingsPane extends PreGamePane {
	private Text title;
	private CheckBox darkModeCheckBox;
	private Slider slider;
	private Label sliderLabel;
	private Button testAccommodation;
	private Text color;
	private RadioButton[] colorOptions;
	private Button calibrate;
	private Text calibrateInstructions;
	private Button exit;

	public SettingsPane( Stage primaryStage, BuggyRun buggyRun, double gridCellWidth ) {
		VBox base = new VBox( 2 * BuggyRun.gridCellWidth() );

		title = new Text( "Settings:" );
		title.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 4.8 * BuggyRun.gridCellWidth() ) );
		base.getChildren().add( title );
		title.setOnMouseClicked( e -> {
			toggleBackgroundColor();
		} );
		title.setFocusTraversable( false );

		darkModeCheckBox = new CheckBox( "Night mode" );
		darkModeCheckBox.setFont( Font.font( "Calibri", FontWeight.BOLD, 2 * BuggyRun.gridCellWidth() ) );
		base.getChildren().add( darkModeCheckBox );
		darkModeCheckBox.setOnAction( e -> {
			isDarkModeProperty().set( darkModeCheckBox.isSelected() );
			saveSettings();
		} );
		darkModeCheckBox.setFocusTraversable( false );

		slider = new Slider( 7, 10, gridCellWidth );
		slider.setPrefWidth( 30 * BuggyRun.gridCellWidth() );
		slider.setMajorTickUnit( 1 );
		slider.setMinorTickCount( 0 );
		slider.setSnapToTicks( true );
		Timeline delay = new Timeline( new KeyFrame( Duration.millis( 50 ), e -> {
			correctSliderValue();
			if ( slider.getValue() != BuggyRun.gridCellWidth() ) {
				buggyRun.setGridCellWidth( (int) slider.getValue() );
				saveSettings();
			}

		} ) );
		slider.setOnMouseReleased( e -> {
			delay.play();
		} );
		slider.setFocusTraversable( false );

		sliderLabel = new Label( "Display size:   ", slider );
		sliderLabel.setContentDisplay( ContentDisplay.RIGHT );
		sliderLabel.setAlignment( Pos.CENTER_LEFT );
		sliderLabel.setFont( Font.font( "Calibri", FontWeight.BOLD, 2 * BuggyRun.gridCellWidth() ) );
		base.getChildren().add( sliderLabel );
		sliderLabel.setFocusTraversable( false );

		HBox hBox = new HBox( 2 * BuggyRun.gridCellWidth() );
		hBox.setAlignment( Pos.CENTER );
		color = new Text( "Favorite color:   " );
		color.setFont( Font.font( "Calibri", FontWeight.BOLD, 2 * BuggyRun.gridCellWidth() ) );
		hBox.getChildren().add( color );
		color.setFocusTraversable( false );
		color.setOnMouseClicked( e -> toggleBackgroundColor() );

		String[] colors = { "Yellow", "Purple", "Turquoise", "Orange", "White" };
		colorOptions = new RadioButton[colors.length];
		ToggleGroup group = new ToggleGroup();
		for ( int i = 0; i < colors.length; i++ ) {
			colorOptions[i] = new RadioButton( colors[i] );
			colorOptions[i].setFont( Font.font( "Calibri", FontWeight.BOLD, 1.5 * BuggyRun.gridCellWidth() ) );
			hBox.getChildren().add( colorOptions[i] );
			colorOptions[i].setFocusTraversable( false );
			colorOptions[i].setToggleGroup( group );

			colorOptions[i].setOnAction( ov -> {
				saveSettings();
			} );
		}
		base.getChildren().add( hBox );
		hBox.setOnMouseClicked( e -> {
			toggleBackgroundColor();
		} );

		testAccommodation = new Button( "Test accommodation for current display size" );
		testAccommodation.setMinWidth( 45 * BuggyRun.gridCellWidth() );
		testAccommodation.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.8 * BuggyRun.gridCellWidth() ) );
		base.getChildren().add( testAccommodation );
		testAccommodation.setFocusTraversable( false );

		calibrate = new Button( "Calibrate screen size" );
		calibrate.setMinWidth( 23 * BuggyRun.gridCellWidth() );
		calibrate.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.8 * BuggyRun.gridCellWidth() ) );
		base.getChildren().add( calibrate );
		calibrate.setFocusTraversable( false );

		calibrateInstructions = new Text( "Use arrow keys to adjust screen size." );
		calibrateInstructions.setFont( Font.font( "Calibri", FontPosture.ITALIC, 1.4 * BuggyRun.gridCellWidth() ) );
		calibrateInstructions.setOnMouseClicked( e -> {
			toggleBackgroundColor();
		} );

		calibrate.textProperty().addListener( ov -> {
			if ( calibrate.getText().equals( "Calibrate screen size" ) )
				base.getChildren().remove( calibrateInstructions );
			else
				base.getChildren().add( calibrateInstructions );
		} );

		base.setTranslateX( 2 * BuggyRun.gridCellWidth() );
		base.setTranslateY( 2 * BuggyRun.gridCellWidth() );
		getChildren().add( base );
		base.setOnMouseClicked( e -> {
			toggleBackgroundColor();
		} );

		exit = new Button( "Exit" );
		exit.setFont( Font.font( "Calibri", FontWeight.BOLD, 2.4 * BuggyRun.gridCellWidth() ) );
		exit.setTranslateX( BuggyRun.gridCellWidth() * BuggyRun.numColumns() - 11 * BuggyRun.gridCellWidth() );
		exit.setTranslateY( getPaneBackground().getHeight() - 7 * BuggyRun.gridCellWidth() );
		getChildren().add( exit );

		try {
			File settings = new File( "do-not-touch" + File.separator + "settings.dat" );
			if ( !settings.exists() ) {
				// If file does not exist, create new file, put default settings into it, and
				// display default settings
				settings.createNewFile();

				DataOutputStream out = new DataOutputStream( new BufferedOutputStream(
						new FileOutputStream( new File( "do-not-touch" + File.separator + "settings.dat" ) ) ) );
				out.writeBoolean( false );
				out.writeInt( 8 );
				out.writeDouble( 6 );
				out.writeDouble( 29 );
				out.writeInt( 0 );
				out.close();

				// Display default settings
				slider.setValue( 10 );

				return;
			}
			DataInputStream in = new DataInputStream( new BufferedInputStream(
					new FileInputStream( new File( "do-not-touch" + File.separator + "settings.dat" ) ) ) );

			darkModeCheckBox.setSelected( in.readBoolean() );
			in.readInt();
			in.readDouble();
			in.readDouble();
			colorOptions[in.readInt()].setSelected( true );
			in.close();
		}
		catch ( IOException e ) {
			e.printStackTrace();
			darkModeCheckBox.setSelected( false );
			slider.setValue( 10 );

			try {
				// Override corrupted settings data with default settings
				DataOutputStream out = new DataOutputStream( new BufferedOutputStream(
						new FileOutputStream( new File( "do-not-touch" + File.separator + "settings.dat" ) ) ) );
				out.writeBoolean( false );
				out.writeInt( 8 );
				out.writeDouble( 6 );
				out.writeDouble( 29 );
				out.writeInt( 0 );
				out.close();
			}
			catch ( IOException ex ) {
				ex.printStackTrace();
			}
		}
	}

	public Text getTitle() {
		return title;
	}

	public boolean isDarkModeSelected() {
		return darkModeCheckBox.isSelected();
	}

	public Slider getSlider() {
		return slider;
	}

	public int getSliderValue() {
		return (int) ( slider.getValue() + 0.5 );
	}

	private void correctSliderValue() {
		if ( slider.getValue() < 7.5 )
			slider.setValue( 7 );
		else if ( slider.getValue() < 8.5 )
			slider.setValue( 8 );
		else if ( slider.getValue() < 9.5 )
			slider.setValue( 9 );
		else
			slider.setValue( 10 );
	}

	public Button getTestAccommodation() {
		return testAccommodation;
	}

	public Button getCalibrate() {
		return calibrate;
	}

	public Button getExit() {
		return exit;
	}

	public Color getColor() {
		// Yellow, lavendar, aqua, orange, white
		Color[] colors = { Color.YELLOW, Color.rgb( 218, 179, 255 ), Color.rgb( 0, 225, 225 ),
				Color.rgb( 255, 194, 102 ), Color.rgb( 240, 240, 240 ) };
		for ( int i = 0; i < colorOptions.length; i++ ) {
			if ( colorOptions[i].isSelected() )
				return colors[i];
		}

		return null;
	}

	@Override
	public void setDarkMode( boolean isDarkMode ) {
		super.setDarkMode( isDarkMode );
		if ( !isDarkMode() ) {
			title.setFill( Color.BLACK );
			darkModeCheckBox.setTextFill( Color.BLACK );
			sliderLabel.setTextFill( Color.BLACK );
			color.setFill( Color.BLACK );
			for ( RadioButton rb : colorOptions )
				rb.setTextFill( Color.BLACK );
			calibrateInstructions.setFill( Color.BLACK );
		}
		else {
			title.setFill( Color.WHITE );
			darkModeCheckBox.setTextFill( Color.WHITE );
			sliderLabel.setTextFill( Color.WHITE );
			color.setFill( Color.WHITE );
			for ( RadioButton rb : colorOptions )
				rb.setTextFill( Color.WHITE );
			calibrateInstructions.setFill( Color.WHITE );
		}
	}

	public synchronized void saveSettings() {
		try {
			DataOutputStream out = new DataOutputStream( new BufferedOutputStream(
					new FileOutputStream( new File( "do-not-touch" + File.separator + "settings.dat" ) ) ) );
			out.writeBoolean( darkModeCheckBox.isSelected() );
			out.writeInt( (int) slider.getValue() );
			out.writeDouble( BuggyRun.getWidthCalibration() );
			out.writeDouble( BuggyRun.getHeightCalibration() );
			for ( int i = 0; i < colorOptions.length; i++ ) {
				if ( colorOptions[i].isSelected() ) {
					out.writeInt( i );
					break;
				}
			}
			out.close();
		}
		catch ( IOException ex ) {
			ex.printStackTrace();
		}
	}
}
