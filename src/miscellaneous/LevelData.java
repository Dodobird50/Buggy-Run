package miscellaneous;

import main.BuggyRun;

public class LevelData {

	// @formatter:off
	private static final int[][] numRows = new int[][] {
			// 1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16
			{ 44, 46, 47, 47, 50, 54, 56, 57, 61, 63, 63, 66, 68, 70, 72, 74 },
			{ 46, 48, 49, 49, 51, 56, 56, 61, 64, 64, 67, 67, 71, 74, 74, 76 },
			{ 48, 49, 50, 53, 53, 57, 63, 63, 68, 68, 73, 73, 76, 76, 78, 78 } };

	private static final boolean[][] hasSafeLine = new boolean[][] {
			{ true, true, true, true, true, false, false, false, false, false, false, false, false, false, false,
					false },
			{ true, true, true, false, false, false, false, false, false, false, false, false, false, false, false,
					false, false },
			{ true, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
					false, false } };

	private static final int[][] secondsInLevels = new int[][] {
			// 1   2   3   4   5   6   7   8   9  10  11  12  13   14   15   16
			{ 50, 50, 55, 60, 60, 65, 70, 70, 75, 80, 80, 90, 90, 100, 100, 110 },
			{ 45, 45, 50, 50, 55, 60, 65, 65, 70, 75, 75, 80, 85,  90, 100, 110 },
			{ 35, 40, 45, 45, 50, 55, 60, 60, 65, 70, 75, 75, 80,  90, 100, 100 } };

	private static final boolean[][] playerGrowsUponBeatingLevel = new boolean[][] {
			{ false, false, true, false, false, true, false, false, false, true, false, true, false, false, false,
					false },
			{ false, true, false, false, true, false, false, true, false, false, true, false, false, false, false,
					false },
			{ true, false, true, false, false, true, false, true, false, false, false, false, true, true, false,
					false } };

	private static final double[][] gravityIndices = new double[][] {
			//	 1     2     3     4     5     6     7     8     9    10    11    12    13    14    15    16
			{ 1.50, 1.60, 1.65, 1.75, 1.80, 1.85, 1.90, 1.95, 2.00, 2.05, 2.10, 2.15, 2.15, 2.20, 2.20, 2.25 },
			{ 1.75, 1.85, 1.90, 1.95, 2.00, 2.00, 2.05, 2.10, 2.20, 2.30, 2.35, 2.40, 2.40, 2.45, 2.45, 2.50 },
			{ 1.90, 1.95, 2.00, 2.00, 2.05, 2.10, 2.15, 2.20, 2.30, 2.35, 2.40, 2.50, 2.55, 2.60, 2.70, 2.80 } };

	private static final double[][] bombsFrequencies = new double[][] {
			//	 1     2     3     4     5     6     7     8     9    10    11    12    13    14    15    16
			{ 1.40, 1.45, 1.55, 1.60, 1.70, 1.75, 1.80, 1.90, 1.95, 2.00, 2.05, 2.10, 2.15, 2.20, 2.20, 2.25 },
			{ 1.60, 1.70, 1.80, 1.85, 1.90, 2.00, 2.05, 2.10, 2.20, 2.25, 2.30, 2.30, 2.40, 2.45, 2.50, 2.55 },
			{ 1.80, 1.85, 1.90, 2.00, 2.10, 2.20, 2.25, 2.30, 2.35, 2.40, 2.50, 2.55, 2.60, 2.60, 2.65, 2.75 } };

	private static final int[][] requiredPointsToLevelUp = new int[][] {
			//	1    2    3    4    5    6    7    8    9   10   11   12   13   14   15   16
			{ 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200, 210, 215, 230, 245, 255 },
			{ 110, 125, 135, 150, 160, 170, 180, 190, 200, 210, 220, 230, 240, 250, 270, 285 },
			{ 120, 130, 150, 170, 180, 185, 195, 200, 210, 225, 240, 260, 280, 290, 305, 325 } };

	private static final int[][] numberOfUnlockedRectangles = new int[][] {
			// 1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16
			{ 30, 30, 28, 28, 28, 24, 24, 24, 20, 20, 20, 15, 15, 12,  9,  9 },
			{ 25, 24, 23, 23, 23, 20, 20, 20, 18, 18, 14, 14,  9,  9,  6,  6 },
			{ 21, 21, 20, 20, 18, 18, 15, 15, 12, 12,  9,  9,  7,  7,  5,  5 } };

	private static final int[][] numberOfTimeBoosts = new int[][] { 
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 2 } };

	private static final int[][] numberOfStatusEffectCoins = new int[][] {
			{ 0, 0,  4,  4,  4,  8,  8,  8, 12, 12, 12, 18, 18, 18, 24, 24 },
			{ 4, 4,  8,  8, 12, 12, 15, 18, 18, 25, 25, 32, 32, 40, 40, 48 },
			{ 8, 8, 12, 12, 16, 16, 20, 24, 28, 32, 36, 40, 45, 50, 54, 60 } };
			
	private static final int[] numberOfExtraLives = new int[] {
		4, 3, 3
	};
	// @formatter:on

	public static int getScreenHeight( int level, int difficulty ) {
		return numRows[difficulty][level - 1] * BuggyRun.gridCellWidth();
	}

	public static int numRows( int level, int difficulty ) {
		return numRows[difficulty][level - 1];
	}

	public static boolean hasSafeArea( int level, int difficulty ) {
		return hasSafeLine[difficulty][level - 1];
	}

	public static int getSecondsInLevel( int level, int difficulty ) {
		return secondsInLevels[difficulty][level - 1];
	}

	public static boolean isPlayerGrowsUponBeatingLevel( int level, int difficulty ) {
		return playerGrowsUponBeatingLevel[difficulty][level - 1];
	}

	public static double getGravityIndex( int level, int difficulty ) {
		return gravityIndices[difficulty][level - 1];
	}

	public static double getVerticalBombsFrequencies( int level, int difficulty ) {
		return bombsFrequencies[difficulty][level - 1] / 250;
	}

	public static double getHorizontalBombsFrequencies( int level, int difficulty ) {
		return bombsFrequencies[difficulty][level - 1] / 250;
	}

	public static double getChanceOfLaser( int level, int difficulty ) {
		return bombsFrequencies[difficulty][level - 1] / 40;
	}

	public static int getRequiredPointsToLevelUp( int level, int difficulty ) {
		return requiredPointsToLevelUp[difficulty][level - 1];
	}

	public static int getNumberOfUnlockedRectangles( int level, int difficulty ) {
		return numberOfUnlockedRectangles[difficulty][level - 1];
	}

	public static int getNumberOfTimeBoosts( int level, int difficulty ) {
		return numberOfTimeBoosts[difficulty][level - 1];
	}

	public static int getNumberOfStatusEffectCoins( int level, int difficulty ) {
		return numberOfStatusEffectCoins[difficulty][level - 1];
	}

	public static int getNumberOfExtraLives( int difficulty ) {
		return numberOfExtraLives[difficulty];
	}
}