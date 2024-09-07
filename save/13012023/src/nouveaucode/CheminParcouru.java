package nouveaucode;

import aw.common.unit.Unit;
import aw.core.game.grid.Grid;

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
	 */
	public void delChemin(CheminParcouru cheminVoulu) {
		CheminParcouru chemin = new CheminParcouru(abscisse,ordonnee,precedent,suivant);
		while(chemin != null && chemin.abscisse!=cheminVoulu.getAbscisse() && chemin.ordonnee!=cheminVoulu.getOrdonnee()) {
			chemin=chemin.precedent;
		}
		if (chemin == null)
			return;
		chemin.suivant=null;
	}
	
	/**
	 * 
	 * @param x l'abscisse de la position
	 * @param y l'ordonnee de la position
	 * @return si la position est dans le chemin
	 */
	public boolean positionDansChemin(int x, int y) {
		CheminParcouru chemin=new CheminParcouru(abscisse,ordonnee,precedent,suivant);
		CheminParcouru before = chemin.getPrecedent();
		if (before == null)
			return false;
		while(before != null) {
			chemin=before;
			before = chemin.getPrecedent();
		}
		System.out.println("owo" + (chemin.abscisse==abscisse && chemin.getOrdonnee()==ordonnee));
		return chemin.abscisse==abscisse && chemin.getOrdonnee()==ordonnee;
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
	 * @return la difference en point de mouvement entre les deux chemin
	 * permet de recalculer le cout d'un trajet
	 */
	public Unit remboursementPM(Curseur curseur,Grid grid, Unit unite, CheminParcouru cheminInitial, CheminParcouru cheminTronque) {
		int compteDifferenceEntreChemin=0;
		while(!cheminInitial.equals(cheminTronque)) {
			compteDifferenceEntreChemin+=unite.getUnitType().getSubType().getMovementCost()[grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getBiome().id()];
		}
		unite.setMovementPoints(unite.getMovementPoints()+compteDifferenceEntreChemin);
		return unite;
	}
	
	/**
	 * 
	 * @param x l'abscisse de la case
	 * @param y l'ordonnee de la case
	 * @return si la case peut etre ajoutee au chemin
	 */
	public boolean cheminPossibilite(int x,  int y) {
		if(abscisse-x<=1&&abscisse-x>=-1&&ordonnee-y<=1&&ordonnee-y>=-1) {//on verifie que la case peut etre connectee au chemin
			return true;
		}
		return false;
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
