package gj.quoridor.player.frosini;

public class PathFinding {
	/*
	 * UTILITY METHODS
	 */
	/*
	 * Absolute value of a number;
	 */
	public int abs(int n) {
		if (n < 0) {
			n = -n;
		}
		return n;
	}

	/*
	 * ASTAR METHODS
	 */
	/*
	 * Set the p-value and the h-value of an element of the board.
	 */
	public void set(int[][][] b, int[] c, int p, int h) {
		b[c[0]][c[1]][4] = p;
		b[c[0]][c[1]][5] = h;
	}

	/*
	 * Logically move one element of the board from open to closed.
	 */
	public void open2closed(boolean[][] open, boolean[][] closed, int[] c) {
		open[c[0]][c[1]] = false;
		closed[c[0]][c[1]] = true;
	}

	/*
	 * Path finding through A* method.
	 */
	int aStar(int[][][] b, int[] s, int[] t) {
		boolean[][] open = new boolean[b.length][b[0].length];
		boolean[][] closed = new boolean[b.length][b[0].length];
		int[][] list = createList(b.length * b[0].length);
		int[] tmp1 = { s[0], s[1], 0 };
		insert(list, tmp1);
		open[s[0]][s[1]] = true;
		while (!isEmpty(list) && !closed[t[0]][t[1]]) {
			int[] m = minimum(list);
			open2closed(open, closed, m);
			for (int d = 0; d < 4; d++) {
				int[] nm = adiacentCell(b, m, d);
				if (nm != null && !closed[nm[0]][nm[1]]) {
					int p = b[m[0]][m[1]][4] + 1;
					int h = abs(nm[0] - t[0]) + abs(nm[1] - t[1]);
					if (!open[nm[0]][nm[1]]) {
						set(b, nm, p, h);
						int[] tmp2 = { nm[0], nm[1], p + h };
						insert(list, tmp2);
						open[nm[0]][nm[1]] = true;
					} else {
						if (update(list, nm, p + h)) {
							set(b, nm, p, h);
						}
					}

				}
			}
		}
		if (closed[t[0]][t[1]]) {
			return b[t[0]][t[1]][4] + b[t[0]][t[1]][5];
		}
		return -1;
	}

	/*
	 * BOARD MANAGEMENT METHODS
	 */
	/**
	 * Return the coordinates of adjacent cell in specific direction. Meaning of
	 * directions: 0=up, 1=left, 2=down, 3=right. If cell does not exist or is not
	 * reachable, return null.
	 */
	public int[] adiacentCell(int[][][] b, int[] c, int d) {
		int[][] delta = { { -2, 0 }, { 0, -2 }, { 2, 0 }, { 0, 2 } };
		int[] ac = { c[0] + delta[d][0], c[1] + delta[d][1] };
		if (b[c[0]][c[1]][d] == 0) {
			ac = null;
		}
		return ac;
	}

	/*
	 * Create the board nxm. Each element of the board contains an array of 6
	 * values. The first four values specify whether a given direction can be
	 * followed starting from the element of the board. The next two values are used
	 * by the A* method. The first one denotes the number of steps executed to reach
	 * the element, the second one a lower bound on the number of steps to be
	 * executed in order to reach the target element from the given element.
	 */
	public int[][][] createBoard(int n, int m) {
		int[][][] b = new int[n][m][6];
		for (int r = 2; r < n - 2; r = r + 2) {
			for (int c = 2; c < m - 2; c = c + 2) {
				int[] tmp = { 1, 1, 1, 1, 0, 0 };
				b[r][c] = tmp;
			}
		}
		for (int c = 2; c < m - 2; c = c + 2) {
			int[] tmp1 = { 0, 1, 1, 1, 0, 0 };
			b[0][c] = tmp1;
		}
		for (int c = 2; c < m - 2; c = c + 2) {
			int[] tmp2 = { 1, 1, 0, 1, 0, 0 };
			b[n - 1][c] = tmp2;
		}
		for (int r = 2; r < n - 2; r = r + 2) {
			int[] tmp3 = { 1, 0, 1, 1, 0, 0 };
			b[r][0] = tmp3;
		}
		for (int r = 2; r < n - 2; r = r + 2) {
			int[] tmp4 = { 1, 1, 1, 0, 0, 0 };
			b[r][m - 1] = tmp4;
		}
		int[] tmp5 = { 0, 0, 1, 1, 0, 0 };
		b[0][0] = tmp5;
		int[] tmp6 = { 0, 1, 1, 0, 0, 0 };
		b[0][m - 1] = tmp6;
		int[] tmp7 = { 1, 0, 0, 1, 0, 0 };
		b[n - 1][0] = tmp7;
		int[] tmp8 = { 1, 1, 0, 0, 0, 0 };
		b[n - 1][m - 1] = tmp8;
		return b;
	}

	/*
	 * Put an obstacle in a cell of the board. Update adjacent cell to avoid the
	 * direction to the cell in which the obstacle is put.
	 */
	public void createWall(int[][][] b, int[] w) {
		b[w[0]][w[1]] = null;
		if (w[0] % 2 == 0) {
			b[w[0] + 2][w[1]] = null;
			if (w[1] > 0 && b[w[0]][w[1] - 1] != null) {
				b[w[0]][w[1] - 1][3] = 0;
				b[w[0] + 2][w[1] - 1][3] = 0;
			}
			if (w[1] < b[0].length - 1 && b[w[0]][w[1] + 1] != null) {
				b[w[0]][w[1] + 1][1] = 0;
				b[w[0] + 2][w[1] + 1][1] = 0;
			}
		} else {
			b[w[0]][w[1] + 2] = null;
			if (w[0] > 0 && b[w[0] - 1][w[1]] != null) {
				b[w[0] - 1][w[1]][2] = 0;
				b[w[0] - 1][w[1] + 2][2] = 0;
			}
			if (w[0] < b.length - 1 && b[w[0] + 1][w[1]] != null) {
				b[w[0] + 1][w[1]][0] = 0;
				b[w[0] + 1][w[1] + 2][0] = 0;
			}
		}
	}

	/*
	 * Update the value of an element in the list. The method assumes that the
	 * element is in the list.
	 */
	public boolean update(int[][] l, int[] c, int v) {
		int i = 0;
		while (l[i] == null || l[i][0] != c[0] || l[i][1] != c[1]) {
			i = i + 1;
		}
		if (l[i][2] > v) {
			l[i][2] = v;
			return true;
		}
		return false;
	}

	/*
	 * Extract minimum element from the list. The minimum is decided on the ground
	 * of the third value of each element in the list.
	 */
	public int[] minimum(int[][] l) {
		int min = 2 * l.length;
		int iMin = -1;
		for (int i = 0; i < l.length; i++) {
			if (l[i] != null && l[i][2] < min) {
				min = l[i][2];
				iMin = i;
			}
		}
		int[] t = { l[iMin][0], l[iMin][1], l[iMin][2] };
		remove(l, t);
		return t;
	}

	/*
	 * Create a list, implemented by means of an array. Each element of the list is
	 * a pair of coordinates (which uniquely identifies the element) and a value
	 * associated to the element.
	 */
	public int[][] createList(int s) {
		return new int[s][];
	}

	/*
	 * Check whether the list is empty, that is, if all elements are null.
	 */
	public boolean isEmpty(int[][] l) {
		int i = 0;
		while (i < l.length && l[i] == null) {
			i = i + 1;
		}
		return i == l.length;
	}

	/*
	 * Insert element in the list. The method assumes that the list is not full.
	 */
	public void insert(int[][] l, int[] e) {
		int i = 0;
		while (i < l.length && l[i] != null) {
			i = i + 1;
		}
		int[] tmp = { e[0], e[1], e[2] };
		l[i] = tmp;
	}

	/*
	 * Remove element from the list. The method assumes that the element is in the
	 * list.
	 */
	public void remove(int[][] l, int[] e) {
		int i = 0;
		while (l[i] == null || l[i][0] != e[0] || l[i][1] != e[1]) {
			i = i + 1;
		}
		l[i] = null;
	}

	// Metodo per copiare un vettore tridimensionale
	public int[][][] copiaBoard(int[][][] b) {
		int[][][] newBoard = new int[b.length][b.length][6];
		for (int i = 0; i < b.length; i++) {
			for (int j = 0; j < b.length; j++) {
				for (int k = 0; k < 6; k++) {
					if (b[i][j] == null) {
						newBoard[i][j] = null;
					} else {
						newBoard[i][j][k] = b[i][j][k];
					}
				}
			}
		}
		return newBoard;
	}
}