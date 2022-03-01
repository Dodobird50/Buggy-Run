package boxes;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import main.BuggyRun;
//import post_game_things.Leaderboard;

public class EraseLeaderboardDataBox extends Pane {
	private Rectangle background;
	private CheckBox easy;
	private CheckBox normal;
	private CheckBox hard;

	private Button cancel;
	private Button next;

	public EraseLeaderboardDataBox( Rectangle backgroundFromScreen ) {
		background = new Rectangle( 35 * BuggyRun.gridCellWidth(), 14 * BuggyRun.gridCellWidth() );
		background.setFill( Color.YELLOW );
		background.setStroke( Color.BLACK );
		background.setStrokeWidth( 0.6 * BuggyRun.gridCellWidth() );
		getChildren().add( background );
		setMaxWidth( 35 * BuggyRun.gridCellWidth() );
		setMaxHeight( 15 * BuggyRun.gridCellWidth() );
		setTranslateX( backgroundFromScreen.getWidth() / 2 - background.getWidth() / 2 );
		setTranslateY( backgroundFromScreen.getHeight() / 2 - background.getHeight() / 2 );

		Text prompt = new Text( "Select leaderboards:" );
		prompt.setX( 1.5 * BuggyRun.gridCellWidth() );
		prompt.setY( 3.5 * BuggyRun.gridCellWidth() );
		prompt.setFont( Font.font( "Calibri", FontWeight.BOLD, 2.4 * BuggyRun.gridCellWidth() ) );
		getChildren().add( prompt );

		HBox hBox = new HBox( 4.2 * BuggyRun.gridCellWidth() );
		hBox.setTranslateX( 1.5 * BuggyRun.gridCellWidth() );
		hBox.setTranslateY( 5.5 * BuggyRun.gridCellWidth() );
		
		easy = new CheckBox( "Easy" );
		easy.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.8 * BuggyRun.gridCellWidth() ) );
		easy.setFocusTraversable( false );

		normal = new CheckBox( "Normal" );
		normal.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.8 * BuggyRun.gridCellWidth() ) );
		normal.setFocusTraversable( false );
		
		hard = new CheckBox( "Hard" );
		hard.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.8 * BuggyRun.gridCellWidth() ) );
		hard.setFocusTraversable( false );
		hBox.getChildren().addAll( easy, normal, hard );
		getChildren().add( hBox );

		cancel = new Button( "Cancel" );
		cancel.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		cancel.setTranslateX( 1.5 * BuggyRun.gridCellWidth() );
		cancel.setTranslateY( 9.5 * BuggyRun.gridCellWidth() );
		cancel.setFocusTraversable( false );

		next = new Button( "Next" );
		next.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		next.setTranslateX( 28 * BuggyRun.gridCellWidth() );
		next.setTranslateY( 9.5 * BuggyRun.gridCellWidth() );
		next.setFocusTraversable( false );
		next.setDisable( true );
		getChildren().addAll( cancel, next );


		easy.setOnAction( e -> {
			updateNextButtonSelectability();
		} );
		normal.setOnAction( e -> {
			updateNextButtonSelectability();
		} );
		hard.setOnAction( e -> {
			updateNextButtonSelectability();
		});
//		updateSelectability();
	}

	public Button getCancel() {
		return cancel;
	}

	public Button getNext() {
		return next;
	}

	public String getSelected() {
		String out = "";
		if ( easy.isSelected() )
			out += "easy";
		if ( normal.isSelected() )
			out += "normal";
		if ( hard.isSelected() )
			out += "hard";

		return out;
	}

	public void reset() {
		easy.setSelected( false );
		normal.setSelected( false );
		hard.setSelected( false );
		
		next.setDisable( true );
	}
	
//	public void updateSelectability() {
//		if ( Leaderboard.isLeaderboardFileEmpty( 0 ) )
//			easy.setDisable( true );
//		else
//			easy.setDisable( false );
//		
//		if ( Leaderboard.isLeaderboardFileEmpty( 1 ) )
//			normal.setDisable( true );
//		else
//			normal.setDisable( false );
//		
//		if ( Leaderboard.isLeaderboardFileEmpty( 2 ) )
//			hard.setDisable( true );
//		else
//			hard.setDisable( false );
//	}
	
	private void updateNextButtonSelectability() {
		if ( easy.isSelected() || normal.isSelected() || hard.isSelected() )
			next.setDisable( false );
		else
			next.setDisable( true );
	}
}
