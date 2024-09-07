package nouveaucode;

import aw.common.unit.Unit;
import aw.core.game.grid.Grid;
import ressources.Chemins;

public class CheminParcouru {//cette classe nous permet de réaliser le chemin de deplacement d'une unité en utilisant les bonnes images
	//et en autorisant un retropedalage de l'utilisateur(une unite peut revenir sur ses pas)
	private int abscisse;
	private int ordonnee;
	private CheminParcouru precedent;//pour avoir une possibilitee de retrouver le chim deja parcouru à partir d'une étape donnée
	private CheminParcouru suivant;//va nous permettre de choisir quelle image afficher pour le chemin
	public CheminParcouru(int abscisse,int ordonnee,CheminParcouru precedent,CheminParcouru suivant) {
		this.abscisse=abscisse;
		this.ordonnee=ordonnee;
		this.precedent=precedent;
		this.suivant=suivant;
	}
	/**
	 * 
	 * @param cheminVoulu
	 * revient a l'etape ou le chemin actuel est cheminVoulu
	 * on s'en sert dans le mouvement pour revenir a une position donnee
	 
	public void delChemin(CheminParcouru cheminVoulu) {
		CheminParcouru chemin = new CheminParcouru(abscisse,ordonnee,precedent,suivant);
		while(chemin != null && chemin.abscisse!=cheminVoulu.getAbscisse() && chemin.ordonnee!=cheminVoulu.getOrdonnee()) {
			chemin=chemin.precedent;
		}
		if (chemin == null)
			return;
		chemin.suivant=null;
	}
	*/
	/**
	 * 
	 * @param cheminInitial un chemin dont un des elements a pour abscisse et ordonnee abscisse et ordonnee
	 * tronque le chemiIinitial j'usqu'a l'element avec abscisse et ordonnee
	 * @return le chemin tronque
	 */
	public CheminParcouru delChemin(CheminParcouru cheminInitial) {
		if(cheminInitial==null) {
			return null;//ce cas n'est pas cense arriver
		}
		while(cheminInitial.getPrecedent() != null && (cheminInitial.getAbscisse()!=abscisse || cheminInitial.getOrdonnee()!=ordonnee)) {
			cheminInitial=cheminInitial.getPrecedent();
		}
		return cheminInitial;
	}
	
	/**
	 * 
	 * @param x l'abscisse de la position
	 * @param y l'ordonnee de la position
	 * @return si la position est dans le chemin
	 */
	public boolean positionDansChemin(int x, int y) {
		if (abscisse==x && ordonnee==y) {
			return true;
		}
		CheminParcouru chemin=new CheminParcouru(abscisse,ordonnee,precedent,suivant);
		while(chemin.precedent!=null) {
			chemin=chemin.precedent;
			if(chemin.abscisse==x&&chemin.ordonnee==y) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @
	 * @return les coordonnes du point de depart du chemin dans l'ordre abscisse ordonnee
	 */
	public int[] coordonneesOrigineDuChemin() {
		while(precedent != null) {
			abscisse=precedent.getAbscisse();
			ordonnee=precedent.getOrdonnee();
			precedent=precedent.getPrecedent();
		}
		return new int[] {abscisse,ordonnee};
	}
	/**
	 * 
	 * @param curseur le curseur
	 * @param grid la grille de jeu
	 * @param unite l'unite qu'on deplace
	 * @param cheminInitial le chemin initial de l'unite
	 * @param cheminTronque le chemin initial sans une partie de ses elements eleves car ils constituaient une boucle
	 * @return l'unite apres s'etre fait remboursee ses points de mouvements et points de carburant non utilises.
	 * permet de recalculer le cout d'un trajet
	 */
	public Unit remboursementPM(Curseur curseur,Grid grid, Unit unite, CheminParcouru cheminInitial, CheminParcouru cheminTronque) {
		int compteDifferenceEntreChemin=0;
		while(!(cheminInitial.abscisse==cheminTronque.abscisse && cheminInitial.ordonnee==cheminTronque.ordonnee)) {
			compteDifferenceEntreChemin+=unite.getUnitType().getSubType().getMovementCost()[grid.getTile(cheminInitial.getAbscisse(), cheminInitial.getOrdonnee()).getBiome().id()];
			cheminInitial=cheminInitial.getPrecedent();
		}
		unite.setMovementPoints(unite.getMovementPoints()+compteDifferenceEntreChemin);
		unite.setFuel(unite.getFuel()+compteDifferenceEntreChemin);
		return unite;
	}

	/**
	 * Rembourse une unite de ces deplacement quand il est annule
	 * @param grid la grille du jeu
	 * @param unit unit a rembourser
	 */
	public void remboursementPM(Grid grid, Unit unit) {
		CheminParcouru cheminParcouru = this;
		int compteDifferenceEntreChemin=0;
		while(cheminParcouru.getPrecedent() != null) {
			compteDifferenceEntreChemin+=unit.getUnitType().getSubType().getMovementCost()[grid.getTile(cheminParcouru.getAbscisse(), cheminParcouru.getOrdonnee()).getBiome().id()];
			cheminParcouru=cheminParcouru.getPrecedent();
		}

		unit.setMovementPoints(unit.getMovementPoints()+compteDifferenceEntreChemin);
		unit.setFuel(unit.getFuel()+compteDifferenceEntreChemin);
	}
	
    /**
     * 
     * @param x l'abscisse de la case
     * @param y l'ordonnee de la case
     * @return si la case peut etre ajoutee au chemin
     */
    public boolean cheminPossibilite(int x,  int y) {
        //on verifie que la case peut etre connectee au chemin
        return ((abscisse - x == 1 || abscisse - x == -1) && y == ordonnee) || ((ordonnee - y == 1 || ordonnee - y == -1) && abscisse == x);
    }
	
	public int getAbscisse() {
		return abscisse;
	}
	public void setAbscisse(int abscisse) {
		this.abscisse = abscisse;
	}
	public int getOrdonnee() {
		return ordonnee;
	}
	public void setOrdonnee(int ordonnee) {
		this.ordonnee = ordonnee;
	}
	public CheminParcouru getPrecedent() {
		return precedent;
	}
	public void setPrecedent(CheminParcouru precedent) {
		this.precedent = precedent;
	}
	public CheminParcouru getSuivant() {
		return suivant;
	}
	public void setSuivant(CheminParcouru suivant) {
		this.suivant = suivant;
	}
}
