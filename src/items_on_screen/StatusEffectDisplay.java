package items_on_screen;

import javafx.util.Duration;
import main.BuggyRun;

import java.io.File;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import miscellaneous.StatusEffect;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;

public class StatusEffectDisplay extends HBox {
	// StatusEffectDisplay only deals with adding indicators of status effects.
	// Actual effects in the game will not be applied here

	public StatusEffectDisplay() {
		super( 2 * BuggyRun.gridCellWidth() );
		setPadding( new Insets( 2, 2, 2, 2 ) );
		setAlignment( Pos.CENTER_LEFT );
	}

	public void add( StatusEffect statusEffect ) {
		// If status effect is already present, reset it
		for ( int i = 0; i < getChildren().size(); i++ ) {
			IconView iconView = (IconView) getChildren().get( i );
			if ( iconView.getStatusEffect() == statusEffect ) {
				iconView.reset();
				return;
			}
		}

		// If added status effect is invincibility, remove blindness and paralysis
		if ( statusEffect == StatusEffect.INVINCIBILITY ) {
			remove( StatusEffect.BLINDNESS );
			remove( StatusEffect.POISON );
			getChildren().add(
					new IconView( StatusEffect.INVINCIBILITY, "file:do-not-touch" + File.separator + "shield.png" ) );

			return;
		}
		// If added status effect is blindness or paralysis, and invincibility is present, do not add status effect
		if ( statusEffect == StatusEffect.BLINDNESS || statusEffect == StatusEffect.POISON ) {
			if ( containsStatusEffect( StatusEffect.INVINCIBILITY ) )
				return;
		}
		// Slow timer and fast timer cancel each other upon adding either one of them
		else if ( statusEffect == StatusEffect.TIME_CONTRACTION )
			remove( StatusEffect.TIME_DILATION );
		else if ( statusEffect == StatusEffect.TIME_DILATION )
			remove( StatusEffect.TIME_CONTRACTION );

		String link = "";
		switch (statusEffect) {
		case TIME_CONTRACTION:
			link = "file:do-not-touch" + File.separator + "snail clock.png";
			break;
		case TIME_DILATION:
			link = "file:do-not-touch" + File.separator + "racing clock.png";
			break;
		case INVINCIBILITY:
			link = "file:do-not-touch" + File.separator + "shield.png";
			break;
		case POISON:
			link = "file:do-not-touch" + File.separator + "poison.png";
			break;
		case DOUBLE_COINS:
			link = "file:do-not-touch" + File.separator + "x2 multiplier.png";
			break;
		case BLINDNESS:
			link = "file:do-not-touch" + File.separator + "blindness.png";
			break;
		}

		getChildren().add( new IconView( statusEffect, link ) );
	}

	// Same thing as add, but no need to check if status effect is already present because each status effect will
	// only be added at most once, and fast forward icon views according to how much time already passed for status
	// effect
	public void load( StatusEffect statusEffect, double fastForwardSeconds ) {
		if ( statusEffect == StatusEffect.INVINCIBILITY ) {
			remove( StatusEffect.BLINDNESS );
			remove( StatusEffect.POISON );

			getChildren().add( new IconView( StatusEffect.INVINCIBILITY,
					"file:do-not-touch" + File.separator + "shield.png", fastForwardSeconds ) );
			return;
		}

		if ( statusEffect == StatusEffect.BLINDNESS || statusEffect == StatusEffect.POISON ) {
			if ( containsStatusEffect( StatusEffect.INVINCIBILITY ) )
				return;
		}

		else if ( statusEffect == StatusEffect.TIME_CONTRACTION )
			remove( StatusEffect.TIME_DILATION );
		else if ( statusEffect == StatusEffect.TIME_DILATION )
			remove( StatusEffect.TIME_CONTRACTION );

		String link = "";
		switch (statusEffect) {
		case TIME_CONTRACTION:
			link = "file:do-not-touch" + File.separator + "snail clock.jpg";
			break;
		case TIME_DILATION:
			link = "file:do-not-touch" + File.separator + "racing clock.png";
			break;
		case INVINCIBILITY:
			link = "file:do-not-touch" + File.separator + "shield.png";
			break;
		case POISON:
			link = "file:do-not-touch" + File.separator + "poison.png";
			break;
		case DOUBLE_COINS:
			link = "file:do-not-touch" + File.separator + "x2 multiplier.png";
			break;
		case BLINDNESS:
			link = "file:do-not-touch" + File.separator + "blindness.png";
			break;
		}

		getChildren().add( new IconView( statusEffect, link, fastForwardSeconds ) );
	}

	public void remove( StatusEffect statusEffect ) {
		for ( int i = 0; i < getChildren().size(); i++ ) {
			IconView iconView = (IconView) getChildren().get( i );
			if ( iconView.getStatusEffect() == statusEffect ) {
				getChildren().remove( iconView );
				break;
			}
		}
	}

	public boolean containsStatusEffect( StatusEffect statusEffect ) {
		for ( Node node : getChildren() ) {
			IconView iconView = (IconView) node;
			if ( iconView.statusEffect == statusEffect ) {
				return true;
			}
		}
		return false;
	}

	public void pauseEverything() {
		for ( Node node : getChildren() ) {
			( (IconView) node ).pause();
		}
	}

	public void resumeEverything() {
		for ( Node node : getChildren() ) {
			( (IconView) node ).resume();
		}
	}

	public void clear() {
		getChildren().clear();
	}

	class IconView extends ImageView {
		private FadeTransition fadeTransition;
		private StatusEffect statusEffect;

		public IconView( StatusEffect statusEffect, String link ) {
			this( statusEffect, link, 0 );
		}

		public IconView( StatusEffect statusEffect, String link, double fastForwardSeconds ) {
			super( link );

			this.statusEffect = statusEffect;
			setFitWidth( 6 * BuggyRun.gridCellWidth() );
			setFitHeight( 6 * BuggyRun.gridCellWidth() );
			setOpacity( 0.9 );

			fadeTransition = new FadeTransition( Duration.millis( 250 ), this );
			fadeTransition.setFromValue( 0.8 );
			fadeTransition.setToValue( 0 );
			fadeTransition.setAutoReverse( true );
			fadeTransition.setCycleCount( Timeline.INDEFINITE );

			if ( fastForwardSeconds <= 7 )
				fadeTransition.setDelay( Duration.seconds( 7 - fastForwardSeconds ) );
			else
				fadeTransition.jumpTo( Duration.seconds( fastForwardSeconds - 7 ) );

			fadeTransition.play();
		}

		public void reset() {
			fadeTransition.stop();
			setOpacity( 0.8 );
			fadeTransition.play();
		}

		public void pause() {
			fadeTransition.pause();
		}

		public void resume() {
			fadeTransition.play();
		}

		public StatusEffect getStatusEffect() {
			return statusEffect;
		}
	}
}