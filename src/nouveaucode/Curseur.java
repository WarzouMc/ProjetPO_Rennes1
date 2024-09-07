package nouveaucode;

import aw.core.game.grid.Grid;
import ressources.Config;

public class Curseur {
	private int abscisse;
	private int ordonnee;
	public Curseur(int abscisse, int ordonnee) {
		this.abscisse=abscisse;
		this.ordonnee=ordonnee;
	}
	/**
	 * 
	 * @param grid la grille de jeu
	 * deplace ci possible le curseur vers la droite
	 */
	public void deplacementDroite(Grid grid) {
		if(abscisse<grid.getWidth()-1) {
			abscisse=abscisse+1;
		}
	}
	/**
	 * 
	 * @param grid la grille de jeu
	 * deplace ci possible le curseur vers la gauche
	 */
	public void deplacementGauche(Grid grid) {
		if(abscisse>0) {
			abscisse=abscisse-1;
		}
	}
	/**
	 * 
	 * @param grid la grille de jeu
	 * deplace ci possible le curseur vers le haut
	 */
	public void deplacementHaut(Grid grid) {
		if(ordonnee<grid.getHeight()-1) {
			ordonnee=ordonnee+1;
		}
	}
	/**
	 * 
	 * @param grid la grille de jeu
	 * deplace ci possible le curseur vers le bas
	 */
	public void deplacementBas(Grid grid) {
		if(ordonnee>0) {
			ordonnee=ordonnee-1;
		}
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
}
