package miscellaneous;

public enum Direction {
	UP( "UP", 0 ), LEFT( "LEFT", 1 ), RIGHT( "RIGHT", 2 ), DOWN( "DOWN", 3 );

	private Direction( final String name, final int ordinal ) {
	}

	public static Direction randomDirection() {
		final Direction[] directions = { Direction.UP, Direction.LEFT, Direction.RIGHT, Direction.DOWN };
		return directions[(int) ( Math.random() * 4.0 )];
	}
}