package gj.quoridor.player.frosini;

public class Board {
	private boolean isFirst;
	private int[][] board = new int[17][17];
	private int[] muriOrizzontali = new int[64];
	private int[] muriVerticali = new int[64];
	private int[][] posizioneMuri = new int[128][3];

	//Inizializza la board
	public void inizializzaBoard() {
		//Inserisce i possibili muri
		int x = 0;
		int hW = 0;
		int vW = 0;
		for (int i = 0; i < board.length - 1; i++) {
			if (i % 2 == 0) {
				for (int j = 1; j < board.length - 1; j = j + 2) {
					board[i][j] = x;
					muriVerticali[hW] = x;
					hW++;
					x++;
				}
			} else {
				for (int j = 0; j < board.length - 1; j = j + 2) {
					board[i][j] = x;
					muriOrizzontali[vW] = x;
					vW++;
					x++;
				}
			}
		}

		// Setta le celle vuote a -1
		for (int i = 0; i <= board.length - 1; i = i + 2) {
			for (int j = 0; j <= board.length - 1; j = j + 2) {
				board[i][j] = -1;
			}
		}
		// Setta le celle che non si possono usare a -3 ossia i bordi
		for (int k = 1; k < board.length; k = k + 2) {
			board[16][k] = -3;
			board[k][16] = -3;
		}
		// Setta le celle non attraversabili a -2
		for (int i = 1; i < board.length - 1; i++) {
			for (int j = 1; j < board.length - 1; j++) {
				if (board[i][j] == 0) {
					board[i][j] = -2;
				}
			}
		}
		//Inserisce le due pedine in base a chi comincia per primo
		if (isFirst) {
			board[16][8] = 214;
			board[0][8] = 220;
		} else {
			board[0][8] = 214;
			board[16][8] = 220;
		}

		// Inserisce le posizioni dei muri
		for (int i = 0; i < posizioneMuri.length; i++) {
			posizioneMuri[i] = cercaMuro(i);
		}
	}

	// Seleziona il primo giocatore
	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public int[] getPosMuro(int i) {
		return posizioneMuri[i];
	}

	// Calcola la nuova posizione della pedina
	public int[] nuovaPosizione(int[] move, int[] oldPos, boolean isMe) {
		int[] newPos = new int[2];
		if ((isFirst && isMe) || (!isFirst && !isMe)) {
			if (move[1] == 0) {
				newPos[0] = oldPos[0] + 2;
				newPos[1] = oldPos[1];
			} else if (move[1] == 1) {
				newPos[0] = oldPos[0] - 2;
				newPos[1] = oldPos[1];
			} else if (move[1] == 2) {
				newPos[0] = oldPos[0];
				newPos[1] = oldPos[1] + 2;
			} else if (move[1] == 3) {
				newPos[0] = oldPos[0];
				newPos[1] = oldPos[1] - 2;
			}
		} else if ((isFirst && !isMe) || (!isFirst && isMe)) {
			if (move[1] == 0) {
				newPos[0] = oldPos[0] - 2;
				newPos[1] = oldPos[1];
			} else if (move[1] == 1) {
				newPos[0] = oldPos[0] + 2;
				newPos[1] = oldPos[1];
			} else if (move[1] == 2) {
				newPos[0] = oldPos[0];
				newPos[1] = oldPos[1] - 2;
			} else if (move[1] == 3) {
				newPos[0] = oldPos[0];
				newPos[1] = oldPos[1] + 2;
			}
		}
		return newPos;
	}

	// Calcola la mossa da fare, a seconda della posizione di input
	public int[] calcolaMossa(int[] oldPos, int[] newPos) {
		int[] move = new int[2];
		move[0] = 0;
		if (isFirst) {
			if (oldPos[0] < newPos[0]) { 
				move[1] = 0;
			} else if (oldPos[0] > newPos[0]) {
				move[1] = 1;
			} else if (oldPos[1] < newPos[1]) {
				move[1] = 2;
			} else if (oldPos[1] > newPos[1]) {
				move[1] = 3;
			}
		} else {
			if (oldPos[0] > newPos[0]) {
				move[1] = 0;
			} else if (oldPos[0] < newPos[0]) {
				move[1] = 1;
			} else if (oldPos[1] > newPos[1]) {
				move[1] = 2;
			} else if (oldPos[1] < newPos[1]) {
				move[1] = 3;
			}
		}
		return move;
	}

	// Esegue la mossa nella board
	public void aggiornaMossa(int[] start, int[] end) {
		board[end[0]][end[1]] = board[start[0]][start[1]];
		board[start[0]][start[1]] = -1;
	}

	// Aggiorna i muri nella board
	public void aggiornaMuro(int indiceMuro) {
		// Setta posizione e orientamento muro
		int[] wallXYO = getPosMuro(indiceMuro);

		//Rende non attraversabile la cella in cui è presente il muro
		board[wallXYO[0]][wallXYO[1]] = -2;
		if (wallXYO[2] == 0) {
			board[wallXYO[0]][wallXYO[1] + 2] = -2;
			if (board[wallXYO[0] - 1][wallXYO[1] + 1] != -2)
				board[wallXYO[0] - 1][wallXYO[1] + 1] = -3;
			if (wallXYO[1] > 0 && board[wallXYO[0]][wallXYO[1] - 2] != -2) {
				board[wallXYO[0]][wallXYO[1] - 2] = -3;
			}
		} else if (wallXYO[2] == 1) {
			board[wallXYO[0] + 2][wallXYO[1]] = -2;
			if (board[wallXYO[0] + 1][wallXYO[1] - 1] != -2)
				board[wallXYO[0] + 1][wallXYO[1] - 1] = -3;
			if (wallXYO[0] > 0 && board[wallXYO[0] - 2][wallXYO[1]] != -2) {
				board[wallXYO[0] - 2][wallXYO[1]] = -3;
			}
		}
	}

	// Restituisce la board
	public int[][] getBoard() {
		return board;
	}

	// Ritorna coordinate e orientamento del muro richiesto
	public int[] cercaMuro(int indiceMuro) {
		int[] xyo = new int[3];
		int orientamentoMuro = 0; // Se uguale a 0, il muro e' orizzontale

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (board[i][j] == indiceMuro) {
					xyo[0] = i;
					xyo[1] = j;
				}
			}
		}

		int i = 0;
		while (i < 64) {
			if (muriVerticali[i] == indiceMuro) {
				orientamentoMuro = 1; // Se uguale a 1, il muro e' verticale
			}
			i++;
		}
		xyo[2] = orientamentoMuro;
		return xyo;
	}

	// Controlla se la mossa e' valida
	public boolean mossaValida(int[] pos) {
		boolean isValid = true;
		if (pos[0] < 0 || pos[0] > 16 || pos[1] < 0 || pos[1] > 16 || board[pos[0]][pos[1]] == -2) {
			isValid = false;
		}
		return isValid;
	}


	// Restituisce le posizioni libere in cui ci possiamo muovere
	public int[][] getPosizioniLibere(int[] ultimaPosizione) {
		int[] su = new int[2];
		int[] invSu = new int[2];
		int[] giu = new int[2];
		int[] invGiu = new int[2];
		int[] sinistra = new int[2];
		int[] invSinistra = new int[2];
		int[] destra = new int[2];
		int[] invDestra = new int[2];

		if (!isFirst) {
			su[0] = ultimaPosizione[0] - 2;
			su[1] = ultimaPosizione[1];
			invSu[0] = ultimaPosizione[0] - 1;
			invSu[1] = ultimaPosizione[1];
			giu[0] = ultimaPosizione[0] + 2;
			giu[1] = ultimaPosizione[1];
			invGiu[0] = ultimaPosizione[0] + 1;
			invGiu[1] = ultimaPosizione[1];
			sinistra[0] = ultimaPosizione[0];
			sinistra[1] = ultimaPosizione[1] - 2;
			invSinistra[0] = ultimaPosizione[0];
			invSinistra[1] = ultimaPosizione[1] - 1;
			destra[0] = ultimaPosizione[0];
			destra[1] = ultimaPosizione[1] + 2;
			invDestra[0] = ultimaPosizione[0];
			invDestra[1] = ultimaPosizione[1] + 1;
		} else { // Con +1 controlla se c'è muro, se non c'è lo mette a +2
			su[0] = ultimaPosizione[0] + 2;
			su[1] = ultimaPosizione[1];
			invSu[0] = ultimaPosizione[0] + 1;
			invSu[1] = ultimaPosizione[1];
			giu[0] = ultimaPosizione[0] - 2;
			giu[1] = ultimaPosizione[1];
			invGiu[0] = ultimaPosizione[0] - 1;
			invGiu[1] = ultimaPosizione[1];
			sinistra[0] = ultimaPosizione[0];
			sinistra[1] = ultimaPosizione[1] + 2;
			invSinistra[0] = ultimaPosizione[0];
			invSinistra[1] = ultimaPosizione[1] + 1;
			destra[0] = ultimaPosizione[0];
			destra[1] = ultimaPosizione[1] - 2;
			invDestra[0] = ultimaPosizione[0];
			invDestra[1] = ultimaPosizione[1] - 1;
		}

		if (!mossaValida(invSu)) {
			su = null;
		}
		if (!mossaValida(invGiu)) {
			giu = null;
		}
		if (!mossaValida(invSinistra)) {
			sinistra = null;
		}
		if (!mossaValida(invDestra)) {
			destra = null;
		}
		int[][] vicini = { null, null, null, null };
		int[][] tempVicini = { su, giu, sinistra, destra };
		int j = 0;
		for (int i = 0; i < tempVicini.length; i++) {
			if (tempVicini[i] != null) {
				vicini[j] = tempVicini[i];
				j++;
			}
		}
		return vicini;
	}
}