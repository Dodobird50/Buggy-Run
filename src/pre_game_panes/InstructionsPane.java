package pre_game_panes;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.BuggyRun;

public class InstructionsPane extends PreGamePane {
	private Text[] instructions;
	private Button back;
	private Button next;
	private int index;

	public InstructionsPane() {
		this( false );
	}

	public InstructionsPane( boolean isDarkMode ) {
		setup();

		HBox hBox = new HBox( 24 * BuggyRun.gridCellWidth() );

		back = new Button( "Back" );
		back.setFont( Font.font( "Calibri", FontWeight.BOLD, 2 * BuggyRun.gridCellWidth() ) );
		back.setFocusTraversable( false );
		back.setMinWidth( 10 * BuggyRun.gridCellWidth() );
		hBox.getChildren().add( back );

		next = new Button( "Next" );
		next.setFont( Font.font( "Calibri", FontWeight.BOLD, 2 * BuggyRun.gridCellWidth() ) );
		next.setFocusTraversable( false );
		next.setMinWidth( 10 * BuggyRun.gridCellWidth() );
		hBox.getChildren().add( next );

		hBox.setTranslateY( getPaneBackground().getHeight() - 9 * BuggyRun.gridCellWidth() );
		hBox.setTranslateX( 18 * BuggyRun.gridCellWidth() );
		getChildren().add( hBox );
	}

	private void setup() {

		String instruction1 = "Welcome to Buggy Run! Let's go through some basic controls. To move around, simply use "
				+ "the arrow keys or the WASD keys. Holding down any of those keys greatly speeds you up in that "
				+ "direction. To pause the game, press P or ESC. Press Q or M to return to the main menu. CTRL + S "
				+ "saves your progress. To self-destruct (why?), press ENTER. Finally, just like any other decently "
				+ "desgined application, pressing CTRL + W shuts down the game in case your parents barge into your "
				+ "room.";

		String instruction2 = "Each difficulty consists of 16 levels. To advance to the next level, you must unlock "
				+ "the finish line at the top of the screen by collecting enough points from coins. Collecting green, "
				+ "yellow, red, blue, and purple coins adds one, two, three, four, and five points, respectively. "
				+ "Just like eating Skittles, you will get to taste the rainbow, in a much healthier way!";

		String instruction3 = "When the finish line is unlocked, green regions will appear. You will then be able to "
				+ "enter through those green regions. Upon entering it, you will automatically advance to the next "
				+ "level. The green regions change quickly, so if you see a clear path to one, try to get there as "
				+ "quickly as possible!";

		String instruction4 = "Beware of those bombs; it's literally a war zone! If you make contact with one, BOOM! "
				+ "You will lose a life and respawn. If you're out of lives, it's game over (did I really have to say "
				+ "that?)! The lower levels have a safe area (marked by a line) in front of the finish line where "
				+ "you're completely safe from them. On the other hand, as the game progresses, bombs will fall more "
				+ "quickly and become more numerous.";

		String instruction5 = "Unfortunately, each level comes with a time limit. If time runs out before you clear "
				+ "the level, you will lose a life and respawn with some time left. For your convenience, there's a "
				+ "timer that displays how much time is left, and even changes color based on that. Go figure!";

		String instruction6 = "There are also a total of six status effects. For negative status effects: blindness "
				+ "makes it much harder to see yourself, time dilation speeds up the timer, and poison interferes "
				+ "with your movement. For positive status effects: x2 multiplier doubles points you get from coins, "
				+ "time contraction (is that a thing?) slows down the timer, and invincibility makes you immune to "
				+ "bombs (and other hostile stuff). It also cancels poison and blindness while active, making it the "
				+ "most powerful status effect out there. Use it to your full advantage if you get it!"
				+ "\n\nNote: status effects that affect the timer only affect how fast the timer runs down; total time "
				+ "elapsed for leaderboard purposes will not be affected.";

		String instruction7 = "To gain a status effect, simply collect a status effect coin (which are gray in "
				+ "color). Collecting a status effect coin gives you a random status effect, positive or negative, "
				+ "and they all last for 10 seconds. Especially in harder difficulties, you might have to rely on "
				+ "status effects for help. If things get difficult, status effect coins might end up working a bit "
				+ "in your favor! ...and vice versa.";

		String instruction8 = "Pressing SPACE spends 25 points to automatically gain the incinvibility effect for 10 "
				+ "seconds. It may help if you're surrounded by bombs and have no way out, or if you're being plagued "
				+ "with negative status effects. There are only a limited number of coins and therefore a limited "
				+ "supply of points in each level, so you will only be allowed to purchase invincibility if after "
				+ "purchasing it, there will still be enough points available to unlock the finish line.";

		String instruction9 = "Without further ado, let's get started! That was a lot of information to swallow, so "
				+ "feel free to look back at anything if you need to. Whenever you're ready, click start to select "
				+ "your difficulty and begin your adventure!"
				+ "\n\nP.S: Please leave this window at the top of your screen, as the window will expand vertically "
				+ "down the road. You can test if your computer screen can accommodate the current display size by "
				+ "going to settings and clicking \"Test accommodation for current display size\" and seeing if the "
				+ "window fits within your computer screen. If not, just simply decrease the screen size.";

		String[] allInstructions = { instruction1, instruction2, instruction3, instruction4, instruction5, instruction6,
				instruction7, instruction8, instruction9 };
		instructions = new Text[allInstructions.length];
		for ( int i = 0; i < allInstructions.length; ++i ) {
			instructions[i] = new Text( 1.2 * BuggyRun.gridCellWidth(), 2.4 * BuggyRun.gridCellWidth(),
					allInstructions[i] );
			instructions[i].setFont( Font.font( "Calibri", FontWeight.BOLD, 1.8 * BuggyRun.gridCellWidth() ) );
			instructions[i].setWrappingWidth(
					BuggyRun.gridCellWidth() * BuggyRun.numColumns() - 2.4 * BuggyRun.gridCellWidth() );
			instructions[i].setTextAlignment( TextAlignment.JUSTIFY );
			instructions[i].setLineSpacing( 0.5 * BuggyRun.gridCellWidth() );
			instructions[i].setOnMouseClicked( e -> {
				toggleBackgroundColor();
			} );
		}

		getChildren().add( instructions[0] );
	}

	public boolean goBack() {
		if ( index > 0 ) {
			getChildren().remove( instructions[index] );
			--index;
			getChildren().add( instructions[index] );
			next.setText( "Next" );
			return true;
		}
		return false;
	}

	public boolean goForward() {
		if ( index < instructions.length - 1 ) {
			getChildren().remove( instructions[index] );
			++index;
			getChildren().add( instructions[index] );
			if ( index == instructions.length - 1 )
				next.setText( "Start!" );
			return true;
		}
		return false;
	}

	public Button getBack() {
		return back;
	}

	public Button getNext() {
		return next;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public void setDarkMode( boolean isDarkMode ) {
		super.setDarkMode( isDarkMode );
		for ( Text text : instructions ) {
			if ( isDarkMode )
				text.setFill( Color.WHITE );
			else
				text.setFill( Color.BLACK );
		}
	}
}