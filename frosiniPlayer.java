package gj.quoridor.player.frosini;
import gj.quoridor.player.Player;

/**
 * @author Simone Frosini, Camillo Levi.
 */

public class frosiniPlayer implements Player {
	private PathFinding path;
	private Board board;
	private boolean isFirst;
	private int numMuri;
	private int[][][] minBoard;
	private int[][] giocatore;
	private int[] posizioneGiocatore;
	private int[][] avversario;
	private int[] posizioneAvversario;

	/*
	 * Il metodo "start" viene invocato ogni volta che inizio una partita, e
	 * controlla prima di tutto chi e' il primo giocatore a giocare con la variabile
	 * isFirst poi inizializza le variabili per i giocatori
	 */
	@Override
	public void start(boolean isFirst) {
		this.isFirst = isFirst;
		path = new PathFinding();
		board = new Board();
		board.inizializzaBoard();
		// Controllo chi e' il primo a muovere
		board.setFirst(isFirst);
		minBoard = path.createBoard(17, 17); // Crea la board nella variabile path per fare il percorso minimo
		posizioneGiocatore = new int[2];
		posizioneAvversario = new int[2];
		giocatore = new int[9][2];
		avversario = new int[9][2];
		numMuri = 0;
		/*
		 * Se sono il primo a giocare allora isFirst e' vera, e allora setto la posizione
		 * giocatore e quella dell'avversario nelle rispettive posizioni iniziali
		 */
		if (isFirst) {
			posizioneGiocatore[0] = 0;
			posizioneGiocatore[1] = 8;
			posizioneAvversario[0] = 16;
			posizioneAvversario[1] = 8;
			int j = 0;
			for (int i = 0; i < 17; i = i + 2) {
				int[] app = { 16, i };
				giocatore[j] = app; // In giocatore mette tutte le posizioni della riga in cui puo' andare
				int[] app1 = { 0, i };
				avversario[j] = app1;
				j++;
			}
		} else {
			/*
			 * Altrimenti eseguo sempre il settaggio dei due giocatori, pero' invertendone i
			 * lati.
			 */
			posizioneGiocatore[0] = 16;
			posizioneGiocatore[1] = 8;
			posizioneAvversario[0] = 0;
			posizioneAvversario[1] = 8;
			int j = 0;
			for (int i = 0; i < 17; i = i + 2) {
				int[] app = { 0, i };
				giocatore[j] = app;
				int[] app1 = { 16, i };
				avversario[j] = app1;
				j++;
			}
		}
	}

	// Questo metodo restituisce la mossa effettuata dal nostro giocatore
	@Override
	public int[] move() {
		int[] mossa = new int[2];    // Il primo valore ci indica se si muove la pedina o si mette un muro, il
									// secondo valore ci indica l'eventuale spostamento
								   // o indice in cui viene posizionato il muro
		int[] minPos = scegliMossaMinima(posizioneGiocatore, giocatore);
		mossa = board.calcolaMossa(posizioneGiocatore, minPos);
		if (mossaOmuro() == 1 && scegliMuro() != null) {
			mossa = scegliMuro();
			int[] PosMuro = board.getPosMuro(mossa[1]);
			path.createWall(minBoard, PosMuro);
			board.aggiornaMuro(mossa[1]);
			numMuri++;
		} else {
			board.aggiornaMossa(posizioneGiocatore, minPos); // Aggiorna la mossa nella board
			posizioneGiocatore = minPos; // Posizione in cui si sposta la mia pedina
		}
		return mossa;
	}

	// Decidere se effettuare una mossa o inserire un muro
	public int mossaOmuro() {
		int move = 0; // Setto a 0 per scegliere di fare uno spostamento
		int[][][] boardTemporanea = path.copiaBoard(minBoard);
		int[][][] boardTemp1 = path.copiaBoard(minBoard);
		if (numMuri < 10 && lunghezzaMinima(posizioneGiocatore, giocatore,
				boardTemporanea) > lunghezzaMinima(posizioneAvversario, avversario, boardTemp1)) {
			// Setto a 1 per mettere muro
			move = 1;
		}
		return move;
	}

	// Decide la mossa da fare secondo i metodi contenuti nella classe PathFinding.java
	public int[] scegliMossaMinima(int[] posizioneGiocatore, int[][] giocatore) {
		int min = Integer.MAX_VALUE;
		// Cerca le posizioni libere vicine
		int[][] posVicine = board.getPosizioniLibere(posizioneGiocatore);
		int[] t = giocatore[0];
		int[] minPos = posVicine[0];
		int risultato = 0;
		for (int j = 0; j < giocatore.length; j++) {
			t = giocatore[j];
			// Si crea una board temporanea con il metodo copiaBoard prendendo minBoard che
			// aveva settato prima
			int[][][] boardTemp1 = path.copiaBoard(minBoard);
			// Richiama il metodo astar con un valore della mossa
			risultato = path.aStar(boardTemp1, minPos, t);
			// Se e' la mossa con risultato minore allora viene selezionata come mossa minore
			if (risultato > -1 && risultato < min) {
				min = risultato;
				minPos = posVicine[0];
			}
		}
		int i = 1;
		// Esegue lo stesso controllo sulla mossa minore, pero' utilizzando tutte le
		// possibili posizioni vicine libere
		while (i < posVicine.length && posVicine[i] != null) { // Il secondo controllo serve per non avere potenziali eccezioni nell'ultima posizione dell'array
			int[] pos = posVicine[i];
			for (int j = 0; j < giocatore.length; j++) {
				t = giocatore[j];
				int[][][] tmpBoard1 = path.copiaBoard(minBoard);
				risultato = path.aStar(tmpBoard1, pos, t);
				if (risultato > -1 && risultato < min) {
					min = risultato;
					minPos = posVicine[i];
				}
			}
			i++;
		}
		return minPos;
	}

	// Metodo per scegliere quale muro inserire
	public int[] scegliMuro() {
		int[] muro = { 1, 0 };
		int[][][] boardTemporanea = path.copiaBoard(minBoard);
		if (isFirst) {
			int[] PosMuro = { posizioneAvversario[0] - 1, posizioneAvversario[1] }; //
			muro[1] = board.getBoard()[posizioneAvversario[0] - 1][posizioneAvversario[1]];
			if (muro[1] != -2 && muro[1] != -3) {
				path.createWall(boardTemporanea, PosMuro);
				if (lunghezzaMinima(posizioneAvversario, avversario, boardTemporanea) == -1
						|| lunghezzaMinima(posizioneGiocatore, giocatore, boardTemporanea) == -1) {
					muro = null;
				}
			} else {
				PosMuro[0] = posizioneAvversario[0] - 1;
				PosMuro[1] = posizioneAvversario[1] - 2;
				if (PosMuro[1] > 0 && PosMuro[1] < 16) {
					muro[1] = board.getBoard()[posizioneAvversario[0] - 1][posizioneAvversario[1] - 2];
					if (muro[1] != -2 && muro[1] != -3) {
						path.createWall(boardTemporanea, PosMuro);
						if (lunghezzaMinima(posizioneAvversario, avversario, boardTemporanea) == -1
								|| lunghezzaMinima(posizioneGiocatore, giocatore, boardTemporanea) == -1) {
							muro = null;
						}
					} else {
						muro = null;
					}
				} else {
					muro = null;
				}
			}
		} else if (!isFirst) {
			int[] PosMuro = { posizioneAvversario[0] + 1, posizioneAvversario[1] };
			muro[1] = board.getBoard()[posizioneAvversario[0] + 1][posizioneAvversario[1]];
			if (muro[1] != -2 && muro[1] != -3) {
				path.createWall(boardTemporanea, PosMuro);
				if (lunghezzaMinima(posizioneAvversario, avversario, boardTemporanea) == -1
						|| lunghezzaMinima(posizioneGiocatore, giocatore, boardTemporanea) == -1) {
					muro = null;
				}
			} else {
				PosMuro[0] = posizioneAvversario[0] + 1;
				PosMuro[1] = posizioneAvversario[1] - 2;
				if (PosMuro[1] > 0 && PosMuro[1] < 16) {
					muro[1] = board.getBoard()[posizioneAvversario[0] + 1][posizioneAvversario[1] - 2];
					if (muro[1] != -2 && muro[1] != -3) {
						path.createWall(boardTemporanea, PosMuro);
						if (lunghezzaMinima(posizioneAvversario, avversario, boardTemporanea) == -1
								|| lunghezzaMinima(posizioneGiocatore, giocatore, boardTemporanea) == -1) {
							muro = null;
						}
					} else {
						muro = null;
					}
				} else {
					muro = null;
				}
			}
		}
		return muro;
	}

	// Ritorna la lunghezza della mossa minima
	public int lunghezzaMinima(int[] start, int[][] giocatore, int[][][] b) {
		int result = 0;
		int min = Integer.MAX_VALUE;
		for (int j = 0; j < giocatore.length; j++) {
			int[] t = giocatore[j];
			int[][][] tmpBoard = path.copiaBoard(b);
			result = path.aStar(tmpBoard, start, t);
			if (result < min) {
				min = result;
			}
		}
		return min;
	}

	/*
	 * Il metodo restituisce la mossa avversaria che ci serve per poter tenere
	 * aggionata la board.
	 */
	@Override
	public void tellMove(int[] move) {
		if (move[0] == 1) {
			int[] PosMuro = board.getPosMuro(move[1]);
			path.createWall(minBoard, PosMuro);
			board.aggiornaMuro(move[1]);
		} else {
			int[] newPosizioneAvversario = board.nuovaPosizione(move, posizioneAvversario, false);
			board.aggiornaMossa(posizioneAvversario, newPosizioneAvversario);
			posizioneAvversario = newPosizioneAvversario;
		}
	}
}