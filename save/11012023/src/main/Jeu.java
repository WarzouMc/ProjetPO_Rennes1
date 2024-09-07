/** package principal */
package main;

import aw.common.unit.Unit;
import aw.core.game.grid.Grid;
import aw.core.game.grid.Tile;
import librairies.AssociationTouches;
import librairies.StdDraw;
import ressources.Config;
import ressources.ParseurCartes;
import ressources.Affichage;
import ressources.Chemins;

import java.util.Optional;
import nouveaucode.Curseur;
import nouveaucode.CheminParcouru;

public class Jeu {
	private String stateOfTheGame;
	private Unit uniteSelectionne;
	private CheminParcouru cheminParcouru;
	private int indexJoueurActif; //l'indice du joueur actif:  1 = rouge, 2 = bleu
	// l'indice 0 est reserve au neutre, qui ne joue pas mais peut posseder des proprietes
	private int[] creditsJoueurs;//credit des joueurs, au début de la partie ils commencent tous deux à 0
	//creditsJoueurs[0] est le credit du joueur 1 et creditsJoueurs[1] est le credit du joueur 0.
	private Grid grid;
	public Jeu(String fileName) throws Exception {
		this.cheminParcouru=null;
		this.uniteSelectionne=null;
		this.stateOfTheGame="SelectionUnite";
		int[] a= {0,0};
		this.creditsJoueurs=a;
		//appel au parseur, qui renvoie un tableau de String 
		String[][] carteString = ParseurCartes.parseCarte(fileName);
		for (int i = 0; i<carteString.length; i++) {
			for (int j=0; j < carteString[0].length; j++){
				System.out.print(carteString[i][j]);
				if (j != carteString[0].length) {
						System.out.print(" | ");
					}
				else {
					System.out.println();
				}
			}
			System.out.println();
		}	
		// a vous de manipuler ce tableau de String pour le transformer en une carte avec vos propres classes, a l'aide de la methode split de la classe String

		this.grid = new Grid(carteString);
		Config.setDimension(carteString[0].length, carteString.length);
		// initialise la configuration avec la longueur de la carte
		
		indexJoueurActif = 1; // rouge commence
	}

	public boolean isOver() {
		return false;
	}

	public void afficheStatutJeu() {
		Affichage.videZoneTexte();
		Affichage.afficheTexteDescriptif(stateOfTheGame);
		}


	public void display(Curseur curseur) {
		StdDraw.clear();
		afficheStatutJeu();
		Affichage.dessineImageDansCase(1, 1, Chemins.getCheminTerrain(Chemins.FICHIER_FORET)); //exemple d'affichage d'une image de forêt dans la case (1,1)

		Affichage.dessineImageDansCase(1, 1, Chemins.getCheminFleche(Chemins.DIRECTION_DROITE,Chemins.DIRECTION_DEBUT));
		Affichage.dessineImageDansCase(2, 1, Chemins.getCheminFleche(Chemins.DIRECTION_GAUCHE,Chemins.DIRECTION_HAUT));
		Affichage.dessineImageDansCase(2, 2, Chemins.getCheminFleche(Chemins.DIRECTION_BAS,Chemins.DIRECTION_HAUT));
		Affichage.dessineImageDansCase(2, 3, Chemins.getCheminFleche(Chemins.DIRECTION_BAS,Chemins.DIRECTION_FIN));
		
		Affichage.dessineImageDansCase(4, 4, Chemins.getCheminFleche(Chemins.DIRECTION_DEBUT,Chemins.DIRECTION_FIN));
		
		Affichage.dessineGrille(); //affiche une grille, mais n'affiche rien dans les cases

		//NOUS
		Tile[] tiles = this.grid.getGrid(); // todo move in own files
		for (Tile tile : tiles) {
			Affichage.dessineImageDansCase(tile.getX(), tile.getY(), tile.getBiome().getImagePath());
			Optional<Unit> unitOptional = tile.getOptionalUnit();
			if (unitOptional.isEmpty())
				continue;
			Unit unit = unitOptional.get();
			Affichage.dessineImageDansCase(tile.getX(), tile.getY(), unit.getImagePath());
		}
		//NOUS
		drawGameCursor(curseur);
		StdDraw.show(); //montre a l'ecran les changement demandes
	}

	public void initialDisplay() {
		StdDraw.enableDoubleBuffering(); // rend l'affichage plus fluide: tout draw est mis en buffer et ne s'affiche qu'au prochain StdDraw.show();
		display(new Curseur(0,0));
	}

	public void drawGameCursor(Curseur curseur) {
		Affichage.dessineCurseur(curseur.getAbscisse(), curseur.getOrdonnee());
	}

	public void update(Curseur curseur) {
		AssociationTouches toucheSuivante = AssociationTouches.trouveProchaineEntree(); //cette fonction boucle jusqu'a la prochaine entree de l'utilisateur
		if (toucheSuivante.isHaut()) { 
			curseur.deplacementHaut(grid);//quoi qu'il se passe on deplace d'abord le curseur
			if(stateOfTheGame.equals("Mouvement")) {//si on est en train d'effectuer un mouvement
				if(uniteSelectionne.getMovementPoints()>uniteSelectionne.getUnitType().getSubType().getMovementCost()[grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getBiome().id()]) {//on verifie que le cout en point de mouvement de l'unite ne soit pas superieur au cout du deplacement
					uniteSelectionne.setMovementPoints(uniteSelectionne.getMovementPoints()-uniteSelectionne.getUnitType().getSubType().getMovementCost()[grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getBiome().id()]);//on modifie les points demouvement de l'unite
					cheminParcouru.setSuivant(new CheminParcouru(curseur.getAbscisse(),curseur.getOrdonnee(),cheminParcouru,null));//on rajoute la nouvelle etape au chemin parcouru
					cheminParcouru=cheminParcouru.getSuivant();//on garde la variable cheminParcouru en tete du mouvement
					
				}
			}
			System.out.println("Touche HAUT");
			display(curseur);
			}
		if (toucheSuivante.isBas()){ 
			curseur.deplacementBas(grid);
			System.out.println("Touche BAS");
			display(curseur);
		}
		if (toucheSuivante.isGauche()) { 
			curseur.deplacementGauche(grid);
			System.out.println("Touche GAUCHE");
			display(curseur);
		}
		if 	(toucheSuivante.isDroite()) { 
			curseur.deplacementDroite(grid);
			System.out.println("Touche DROITE");
			 	display(curseur);
		}
		
		if (toucheSuivante.isCaractere('t')) {//changement de tour si l'utilisateur appuie sur t
			String[] options = {"Oui", "Non"};
			if (Affichage.popup("Finir le tour de joueur"+indexJoueurActif+"?", options, true, 1) == 0) {
				//le choix 0, "Oui", a été selectionné
				indexJoueurActif=(indexJoueurActif+1)%2;
				//TODO reinitialiser les points de mouvement des unites, leur utilisation donner les credits dus aux joueurs etc
				System.out.println("FIN DE TOUR");
				stateOfTheGame="Selection";
			 	display(curseur);
			}
			
			display(curseur);
		}
		if(toucheSuivante.isEntree()) {
			if(grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getOptionalUnit().isPresent()//on verifie qu'il y a une unite sur la case
					&&stateOfTheGame.equals("Selection")//on verifie qu'on soit dans l'etat de selection 
					&& grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getUnit().getPlayer()==indexJoueurActif//on verifie que l'unite appartient au joueur actif 
					&& grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getUnit().isAvailable()) {//on verifie qu'elle n'a pas encore ete activee ce tour-ci
				stateOfTheGame="Mouvement";//on met le jeu dans le mode deplacement d'unite
				uniteSelectionne=grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getUnit();//on garde en memoire l'unite que l'on veut deplacee
			 	display(curseur);
			}
			if(stateOfTheGame.equals("Mouvement")) {
				grid.getTile(cheminParcouru.coordonneesOrigineDuChemin()[0], cheminParcouru.coordonneesOrigineDuChemin()[1]).setOptionalUnit(Optional.empty());
			}
		if(toucheSuivante.isEchap()) {
			if(stateOfTheGame.equals("Mouvement")) {//si l'on appuie sur echap en deplacement d'une unite, on desselectionne celle-ci
				stateOfTheGame="Selection";
			 	display(curseur);
			}
		}
		}
	}

	/**
	 *
	 * @param curseur le curseur actuel
	 * cette fonction place le curseur a la position de la premiere unite disponible
	 */
	public void passageUniteSuivante(Curseur curseur) {
		for(int a=0;a<Config.longueurCarteXCases;a++){
			for(int b=0;b<Config.longueurCarteXCases;b++){
				if(grid.getTile(a,b).getOptionalUnit().isPresent() && grid.getTile(a,b).getOptionalUnit().get().isAvailable()) {
					curseur.setAbscisse(a);
					curseur.setOrdonnee(b);
					return;
				}
			}
		}
	}
}

