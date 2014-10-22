
import java.io.*;
import java.util.*;

public class ClueReasoner
{
	private int numPlayers;
	private int playerNum;
	private int numCards;
	private SATSolver solver;
	private String caseFile = "cf";
	private String[] players = {"sc", "mu", "wh", "gr", "pe", "pl"};
	private String[] suspects = {"mu", "pl", "gr", "pe", "sc", "wh"};
	private String[] weapons = {"kn", "ca", "re", "ro", "pi", "wr"};
	private String[] rooms = {"ha", "lo", "di", "ki", "ba", "co", "bi", "li", "st"};
	private String[] cards;

	public ClueReasoner()
	{
		numPlayers = players.length;
		// Initialize card info
		cards = new String[suspects.length + weapons.length + rooms.length];
		int i = 0;
		for (String card : suspects)
			cards[i++] = card;
		for (String card : weapons)
			cards[i++] = card;
		for (String card : rooms)
			cards[i++] = card;
		numCards = i;
		// Initialize solver
		solver = new SATSolver();
		addInitialClauses();
	}
	
	private int getPlayerNum(String player)
	{
		if (player.equals(caseFile))
			return numPlayers;
		for (int i = 0; i < numPlayers; i++)
			if (player.equals(players[i]))
					return i;
		System.out.println("Illegal player: " + player);
		return -1;
	}

	private int getCardNum(String card)
	{
		for (int i = 0; i < numCards; i++)
			if (card.equals(cards[i]))
		return i;
			System.out.println("Illegal card: " + card);
		return -1;
	}

	private int getPairNum(String player, String card)
	{
		return getPairNum(getPlayerNum(player), getCardNum(card));
	}
	private int getPairNum(int playerNum, int cardNum)
	{
		return playerNum * numCards + cardNum + 1;
	}

	public void addInitialClauses()
	{
	// TO BE IMPLEMENTED AS AN EXERCISE
	// Each card is in at least one place (including case file).
		for (int c = 0; c < numCards; c++) {
			int[] clause = new int[numPlayers + 1];
			for (int p = 0; p <= numPlayers; p++)
				clause[p] = getPairNum(p, c);
			solver.addClause(clause);
		}
	// If a card is one place, it cannot be in another place.
		for (int c = 0; c < numCards; c++) {
			for (int p = 0; p <= numPlayers; p++) {
				for (int q = 0; q <= numPlayers; q++) {
					if (p != q){
						int[] clause = new int[2];
						clause[0] = getPairNum(p, c) * -1;
						clause[1] = getPairNum(q, c) * -1;
						solver.addClause(clause);
					}
				}
			}
		}
	// At least one card of each category is in the case file.
		int[] clause = new int[suspects.length];
		for (int i = 0; i < suspects.length; i++) {
			clause[i] = getPairNum(caseFile, suspects[i]);
		}
		solver.addClause(clause);
		clause = new int[weapons.length];
		for (int i = 0; i < weapons.length; i++) {
			clause[i] = getPairNum(caseFile, weapons[i]);
		}
		solver.addClause(clause);
		clause = new int[rooms.length];
		for (int i = 0; i < rooms.length; i++) {
			clause[i] = getPairNum(caseFile, rooms[i]);
		}
		solver.addClause(clause);
	// No two cards in each category can both be in the case file.
		for (int i = 0; i < suspects.length; i++) {
			for (int j = 0; j < suspects.length; j++) {
				if (i != j){
					clause = new int[2];
					clause[0] = getPairNum(caseFile, suspects[i]) * -1;
					clause[1] = getPairNum(caseFile, suspects[j]) * -1;
					solver.addClause(clause);
				}
			}
		}
		
		for (int i = 0; i < weapons.length; i++) {
			for (int j = 0; j < weapons.length; j++) {
				if (i != j){
					clause = new int[2];
					clause[0] = getPairNum(caseFile, weapons[i]) * -1;
					clause[1] = getPairNum(caseFile, weapons[j]) * -1;
					solver.addClause(clause);
				}
			}
		}
		
		for (int i = 0; i < rooms.length; i++) {
			for (int j = 0; j < rooms.length; j++) {
				if (i != j){
					clause = new int[2];
					clause[0] = getPairNum(caseFile, rooms[i]) * -1;
					clause[1] = getPairNum(caseFile, rooms[j]) * -1;
					solver.addClause(clause);
				}
			}
		}
	}

	public void hand(String player, String[] cards)
	{
		playerNum = getPlayerNum(player);
		for (String card : cards) {
			int[] clause = new int[1];
			clause[0] = getPairNum(player, card);
			solver.addClause(clause);
		}
	}

	public void suggest(String suggester, String card1, String card2, String card3, String refuter, String cardShown)
	{
	// TO BE IMPLEMENTED AS AN EXERCISE
		if (refuter == null) {
			for (int i = 0; i < suspects.length; i++) {
				if (suspects[i] != suggester) {
					int[] clause = new int[1];
					clause[0] = getPairNum(suspects[i], card1) * -1;
					solver.addClause(clause);
					clause = new int[1];
					clause[0] = getPairNum(suspects[i], card2) * -1;
					solver.addClause(clause);
					clause = new int[1];
					clause[0] = getPairNum(suspects[i], card3) * -1;
					solver.addClause(clause);
				}
			}
		}
		else if (cardShown == null) {
			int[] clause = new int[3];
			clause[0] = getPairNum(refuter, card1);
			clause[1] = getPairNum(refuter, card2);
			clause[2] = getPairNum(refuter, card3);
			solver.addClause(clause);
			int suggesterNum = getPlayerNum(suggester);
			int refuterNum = getPlayerNum(refuter);
			for (int i = suggesterNum + 1; i != refuterNum; i++) {
				if (i >= suspects.length) {
					i = 0;
				}
				if (i == refuterNum)
					return;
				clause = new int[3];
				clause[0] = getPairNum(suspects[i], card1) * -1;
				clause[1] = getPairNum(suspects[i], card2) * -1;
				clause[2] = getPairNum(suspects[i], card3) * -1;
				solver.addClause(clause);
			}
		}
		else {
			int[] clause = new int[1];
			clause[0] = getPairNum(refuter, cardShown);
			solver.addClause(clause);
			int suggesterNum = getPlayerNum(suggester);
			int refuterNum = getPlayerNum(refuter);
			for (int i = suggesterNum + 1; i != refuterNum; i++) {
				if (i >= suspects.length) {
					i = 0;
				}
				if (i == refuterNum)
					return;
				clause = new int[3];
				clause[0] = getPairNum(suspects[i], card1) * -1;
				clause[1] = getPairNum(suspects[i], card2) * -1;
				clause[2] = getPairNum(suspects[i], card3) * -1;
				solver.addClause(clause);
			}
		}
	}

	public void accuse(String accuser, String card1, String card2, String card3, boolean isCorrect)
	{
	// TO BE IMPLEMENTED AS AN EXERCISE
		if (isCorrect) {
			int[] clause = new int[1];
			clause[0] = getPairNum(caseFile, card1);
			solver.addClause(clause);
			clause = new int[1];
			clause[0] = getPairNum(caseFile, card2);
			solver.addClause(clause);
			clause = new int[1];
			clause[0] = getPairNum(caseFile, card3);
			solver.addClause(clause);
			
		}
		else {
			int[] clause = new int[3];
			clause[0] = getPairNum(caseFile, card1) * -1;
			clause[0] = getPairNum(caseFile, card2) * -1;
			clause[0] = getPairNum(caseFile, card3) * -1;
			solver.addClause(clause);
		}
	}

	public int query(String player, String card)
	{
		return solver.testLiteral(getPairNum(player, card));
	}

	public String queryString(int returnCode)
	{
	if (returnCode == SATSolver.TRUE)
		return "Y";
	else if (returnCode == SATSolver.FALSE)
		return "N";
	else
		return "-";
	}
	public void printNotepad()
	{
		PrintStream out = System.out;
		for (String player : players)
		out.print("\t" + player);
		out.println("\t" + caseFile);
		for (String card : cards) {
			out.print(card + "\t");
			for (String player : players)
				out.print(queryString(query(player, card)) + "\t");
			out.println(queryString(query(caseFile, card)));
		}
	}

	public static void main(String[] args)
	{
		ClueReasoner cr = new ClueReasoner();
		String[] myCards = {"wh", "li", "st"};
		cr.hand("sc", myCards);
		cr.suggest("sc", "sc", "ro", "lo", "mu", "sc");
		cr.suggest("mu", "pe", "pi", "di", "pe", null);
		cr.suggest("wh", "mu", "re", "ba", "pe", null);
		cr.suggest("gr", "wh", "kn", "ba", "pl", null);
		cr.suggest("pe", "gr", "ca", "di", "wh", null);
		cr.suggest("pl", "wh", "wr", "st", "sc", "wh");
		cr.suggest("sc", "pl", "ro", "co", "mu", "pl");
		cr.suggest("mu", "pe", "ro", "ba", "wh", null);
		cr.suggest("wh", "mu", "ca", "st", "gr", null);
		cr.suggest("gr", "pe", "kn", "di", "pe", null);
		cr.suggest("pe", "mu", "pi", "di", "pl", null);
		cr.suggest("pl", "gr", "kn", "co", "wh", null);
		cr.suggest("sc", "pe", "kn", "lo", "mu", "lo");
		cr.suggest("mu", "pe", "kn", "di", "wh", null);
		cr.suggest("wh", "pe", "wr", "ha", "gr", null);
		cr.suggest("gr", "wh", "pi", "co", "pl", null);
		cr.suggest("pe", "sc", "pi", "ha", "mu", null);
		cr.suggest("pl", "pe", "pi", "ba", null, null);
		cr.suggest("sc", "wh", "pi", "ha", "pe", "ha");
		cr.suggest("wh", "pe", "pi", "ha", "pe", null);
		cr.suggest("pe", "pe", "pi", "ha", null, null);
		cr.suggest("sc", "gr", "pi", "st", "wh", "gr");
		cr.suggest("mu", "pe", "pi", "ba", "pl", null);
		cr.suggest("wh", "pe", "pi", "st", "sc", "st");
		cr.suggest("gr", "wh", "pi", "st", "sc", "wh");
		cr.suggest("pe", "wh", "pi", "st", "sc", "wh");
		cr.suggest("pl", "pe", "pi", "ki", "gr", null);
		cr.printNotepad();
		cr.accuse("sc", "pe", "pi", "bi", true);
	}

}