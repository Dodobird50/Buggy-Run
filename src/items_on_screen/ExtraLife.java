package items_on_screen;

import java.io.File;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.BuggyRun;

public class ExtraLife extends ImageView implements ItemOnScreen {
	public ExtraLife( int row, int column, boolean isDarkMode ) {
		if ( !isDarkMode )
			setImage( new Image( "file:do-not-touch" + File.separator + "heart.png" ) );
		else
			setImage( new Image( "file:do-not-touch" + File.separator + "white heart.png" ) );

		setX( column * BuggyRun.gridCellWidth() );
		setY( row * BuggyRun.gridCellWidth() );
		setFitWidth( BuggyRun.gridCellWidth() );
		setFitHeight( BuggyRun.gridCellWidth() );
	}

	@Override
	public int row() {
		return (int) getY() / BuggyRun.gridCellWidth();
	}

	@Override
	public int column() {
		return (int) getX() / BuggyRun.gridCellWidth();
	}
}