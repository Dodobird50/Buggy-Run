package items_on_screen;

import pre_game_panes.MainMenu;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import miscellaneous.Direction;
import main.BuggyRun;

import java.util.ArrayList;

public class Player extends ArrayList<PlayerRectangle> implements ItemOnScreen {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PlayerRectangle head;
	private Direction previouslyMovedDirection;
	private boolean isSnake;
	private boolean isAlive;
	private boolean isUnderPoisonEffect;
	private boolean isUnderInvincibilityEffect;
	private boolean isUnderBlindnessEffect;

	private Color playerColor;

	public Player( Color playerColor ) {
		this.playerColor = playerColor;
		start();
	}

	public Player( ArrayList<PlayerRectangle> list, Color playerColor ) {
		this.playerColor = playerColor;
		for ( PlayerRectangle r : list )
			add( r );

		head = get( 0 );
		if ( size() > 1 ) {
			isSnake = true;
			PlayerRectangle secondPlayerRectangle = get( 1 );
			if ( secondPlayerRectangle.getX() - head.getX() == BuggyRun.gridCellWidth() )
				previouslyMovedDirection = Direction.LEFT;
			else if ( secondPlayerRectangle.getX() - head.getX() == -BuggyRun.gridCellWidth() )
				previouslyMovedDirection = Direction.RIGHT;
			else if ( secondPlayerRectangle.getY() - head.getY() == BuggyRun.gridCellWidth() )
				previouslyMovedDirection = Direction.UP;
			else if ( secondPlayerRectangle.getY() - head.getY() == -BuggyRun.gridCellWidth() )
				previouslyMovedDirection = Direction.DOWN;
			// Cannot determine direction
			else
				previouslyMovedDirection = Direction.randomDirection();
		}
		else
			previouslyMovedDirection = Direction.randomDirection();

	}

	private void start() {
		head = new PlayerRectangle( BuggyRun.gridCellWidth(), BuggyRun.gridCellWidth() );
		head.setFill( Color.DEEPSKYBLUE );
		head.setStroke( Color.BLACK );
		head.setStrokeWidth( 0.1 * BuggyRun.gridCellWidth() );
		add( head );
		isSnake = false;
	}

	public void spawn( double xCoordinate, double yCoordinate ) {
		int status = (int) ( Math.random() * 2 );
		int increment;
		if ( status == 0 )
			increment = -BuggyRun.gridCellWidth();
		else
			increment = BuggyRun.gridCellWidth();
		for ( int i = 0; i < size(); i++ ) {
			get( i ).setX( xCoordinate );
			get( i ).setY( yCoordinate );
			xCoordinate += increment;
		}
		if ( status == 0 )
			previouslyMovedDirection = Direction.RIGHT;
		else
			previouslyMovedDirection = Direction.LEFT;
	}

	public boolean move( Direction pendingDirection, Rectangle background, MainMenu mainMenu, Player... otherPlayers ) {
		boolean moved = true;

		if ( willRunIntoOtherPlayers( pendingDirection, otherPlayers ) )
			return false;

		if ( !isSnake ) {
			if ( !willGoOutOfBounds( pendingDirection, background ) )
				movePlayerRectangles( pendingDirection );
			else
				moved = false;
		}
		else if ( isSnake ) {
			if ( !willEatItself( pendingDirection ) && !willDoABackflip( pendingDirection )
					&& !willGoOutOfBounds( pendingDirection, background ) )
				movePlayerRectangles( pendingDirection );
			else
				moved = false;
		}

		return moved;
	}

	private boolean willRunIntoOtherPlayers( Direction pendingDirection, Player... otherPlayers ) {
		PlayerRectangle tempHead = new PlayerRectangle( head.row(), head.column() );
		if ( pendingDirection == Direction.UP )
			tempHead.setY( tempHead.getY() - BuggyRun.gridCellWidth() );
		else if ( pendingDirection == Direction.LEFT )
			tempHead.setX( tempHead.getX() - BuggyRun.gridCellWidth() );
		else if ( pendingDirection == Direction.RIGHT )
			tempHead.setX( tempHead.getX() + BuggyRun.gridCellWidth() );
		else if ( pendingDirection == Direction.DOWN )
			tempHead.setY( tempHead.getY() + BuggyRun.gridCellWidth() );

		for ( Player otherPlayer : otherPlayers ) {
			for ( PlayerRectangle r : otherPlayer ) {
				if ( tempHead.getX() == r.getX() && tempHead.getY() == r.getY() )
					return true;
			}
		}

		return false;
	}

	public boolean move( Direction pendingDirection, Rectangle screen, FinishLine goal ) {
		if ( !isAlive )
			return false;

		// Reduce motion by 70%
		if ( isUnderPoisonEffect && Math.random() > 0.3 )
			return false;

		boolean moved = true;
		if ( !isSnake ) {
			if ( !willGoOutOfBounds( pendingDirection, screen )
					&& !willEnterALockedRectangle( pendingDirection, goal ) )
				movePlayerRectangles( pendingDirection );
			else
				moved = false;

		}
		else if ( isSnake ) {
			if ( !willEatItself( pendingDirection ) && !willDoABackflip( pendingDirection )
					&& !willGoOutOfBounds( pendingDirection, screen )
					&& !willEnterALockedRectangle( pendingDirection, goal ) )
				movePlayerRectangles( pendingDirection );
			else
				moved = false;
		}
		return moved;
	}

	private void movePlayerRectangles( Direction direction ) {
		for ( int i = size() - 1; i > 0; --i ) {
			get( i ).setX( get( i - 1 ).getX() );
			get( i ).setY( get( i - 1 ).getY() );
		}
		if ( direction == Direction.UP )
			head.setY( head.getY() - BuggyRun.gridCellWidth() );
		else if ( direction == Direction.LEFT )
			head.setX( head.getX() - BuggyRun.gridCellWidth() );
		else if ( direction == Direction.RIGHT )
			head.setX( head.getX() + BuggyRun.gridCellWidth() );
		else if ( direction == Direction.DOWN )
			head.setY( head.getY() + BuggyRun.gridCellWidth() );

		previouslyMovedDirection = direction;
	}

	private boolean willEatItself( Direction pendingDirection ) {
		boolean willEatItself = false;
		PlayerRectangle tempHead = new PlayerRectangle( head.row(), head.column() );
		if ( pendingDirection == Direction.UP )
			tempHead.setY( tempHead.getY() - BuggyRun.gridCellWidth() );
		else if ( pendingDirection == Direction.LEFT )
			tempHead.setX( tempHead.getX() - BuggyRun.gridCellWidth() );
		else if ( pendingDirection == Direction.RIGHT )
			tempHead.setX( tempHead.getX() + BuggyRun.gridCellWidth() );
		else if ( pendingDirection == Direction.DOWN )
			tempHead.setY( tempHead.getY() + BuggyRun.gridCellWidth() );

		for ( int i = 1; i < size() - 1; ++i ) {
			if ( tempHead.getX() == get( i ).getX() && tempHead.getY() == get( i ).getY() )
				willEatItself = true;
		}

		return willEatItself;
	}

	private boolean willDoABackflip( Direction pendingDirection ) {
		return ( pendingDirection == Direction.UP && previouslyMovedDirection == Direction.DOWN )
				|| ( pendingDirection == Direction.LEFT && previouslyMovedDirection == Direction.RIGHT )
				|| ( pendingDirection == Direction.RIGHT && previouslyMovedDirection == Direction.LEFT )
				|| ( pendingDirection == Direction.DOWN && previouslyMovedDirection == Direction.UP );
	}

	private boolean willGoOutOfBounds( Direction pendingDirection, Rectangle screen ) {
		return ( pendingDirection == Direction.UP && head.getY() == 0 )
				|| ( pendingDirection == Direction.LEFT && head.getX() == 0 )
				|| ( pendingDirection == Direction.RIGHT
						&& head.getX() == BuggyRun.gridCellWidth() * ( BuggyRun.numColumns() - 1 ) )
				|| ( pendingDirection == Direction.DOWN
						&& head.getY() == screen.getHeight() - BuggyRun.gridCellWidth() );
	}

	private boolean willEnterALockedRectangle( Direction pendingDirection, FinishLine goal ) {
		PlayerRectangle pendingHead = new PlayerRectangle( head.row(), head.column() );
		if ( pendingDirection == Direction.UP && pendingHead.getY() == BuggyRun.gridCellWidth() ) {
			int index = (int) ( pendingHead.getX() / BuggyRun.gridCellWidth() );
			return !goal.getAreRectanglesUnlocked()[index];
		}
		return false;
	}

	public void grow() {
		PlayerRectangle extendFromTail = new PlayerRectangle( BuggyRun.gridCellWidth(), BuggyRun.gridCellWidth() );
		extendFromTail.setFill( playerColor );
		extendFromTail.setStroke( Color.BLACK );
		extendFromTail.setStrokeWidth( 0.1 * BuggyRun.gridCellWidth() );
		add( extendFromTail );

		if ( !isSnake )
			isSnake = true;
	}

	public PlayerRectangle getHead() {
		return head;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive( boolean isAlive ) {
		this.isAlive = isAlive;
	}

	public boolean setUnderPoisonEffect( boolean isUnderDrowsinessEffect ) {
		if ( isUnderInvincibilityEffect && isUnderDrowsinessEffect )
			return false;
		this.isUnderPoisonEffect = isUnderDrowsinessEffect;
		if ( isUnderDrowsinessEffect ) {
			if ( size() > 1 ) {
				for ( int i = 1; i < size(); ++i )
					get( i ).setFill( Color.RED );
			}
			else
				head.setFill( Color.RED );
		}
		else {
			if ( !isUnderInvincibilityEffect ) {
				head.setFill( Color.DEEPSKYBLUE );
				for ( int i = 1; i < size(); ++i )
					get( i ).setFill( playerColor );
			}
			else {
				if ( size() > 1 ) {
					for ( int i = 1; i < size(); ++i )
						get( i ).setFill( Color.LIME );
				}
				else
					head.setFill( Color.LIME );
			}
		}
		return true;
	}

	public boolean isUnderBlindnessEffect() {
		return isUnderBlindnessEffect;
	}

	public boolean isUnderPoisonEffect() {
		return isUnderPoisonEffect;
	}

	public boolean setUnderBlindnessEffect( boolean isUnderBlindnessEffect ) {
		if ( isUnderInvincibilityEffect && isUnderBlindnessEffect ) {
			return false;
		}
		this.isUnderBlindnessEffect = isUnderBlindnessEffect;
		if ( this.isUnderBlindnessEffect ) {
			head.setOpacity( 0.017 );
			for ( int i = 1; i < size(); ++i )
				get( i ).setOpacity( 0 );
		}
		else {
			for ( PlayerRectangle r : this )
				r.setOpacity( 1 );
		}
		return true;
	}

	public boolean isUnderInvincibilityEffect() {
		return isUnderInvincibilityEffect;
	}

	public void setUnderInvicibilityEffect( boolean isUnderInvicibilityEffect ) {
		isUnderInvincibilityEffect = isUnderInvicibilityEffect;
		if ( isUnderInvincibilityEffect ) {
			if ( isUnderPoisonEffect )
				setUnderPoisonEffect( false );
			if ( isUnderBlindnessEffect )
				setUnderBlindnessEffect( false );
			if ( size() > 1 ) {
				for ( int i = 1; i < size(); ++i )
					get( i ).setFill( Color.LIME );
			}
			else
				head.setFill( Color.LIME );
		}
		else {
			head.setFill( Color.DEEPSKYBLUE );
			for ( int i = 1; i < size(); ++i ) {
				get( i ).setFill( playerColor );
			}
		}
	}

	public int row() {
		return head.row();
	}

	public int column() {
		return head.column();
	}

}