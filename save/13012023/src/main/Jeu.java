/** package principal */
package main;

import aw.common.unit.Unit;
import aw.common.unit.UnitType.SubType;
import aw.core.game.grid.Grid;
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
	private Curseur curseur;
	private int indexJoueurActif; //l'indice du joueur actif:  1 = rouge, 2 = bleu
	// l'indice 0 est reserve au neutre, qui ne joue pas mais peut posseder des proprietes
	private int[] creditsJoueurs;//credit des joueurs, au début de la partie ils commencent tous deux à 0
	//creditsJoueurs[0] est le credit du joueur 1 et creditsJoueurs[1] est le credit du joueur 0.
	private Grid grid;
	public Jeu(String fileName) throws Exception {
		this.curseur=new Curseur(0,0);
		this.cheminParcouru=new CheminParcouru(0, 0, null, null);;
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


	public void display() {
		StdDraw.clear();
		afficheStatutJeu();

		//NOUS
		this.grid.draw();
		//NOUS
		drawGameCursor(curseur);
		affichageChemin(this.cheminParcouru);
		StdDraw.show(); //montre a l'ecran les changement demandes
	}

	public void initialDisplay() {
		StdDraw.enableDoubleBuffering(); // rend l'affichage plus fluide: tout draw est mis en buffer et ne s'affiche qu'au prochain StdDraw.show();
	}

	public void drawGameCursor(Curseur curseur) {
		Affichage.dessineCurseur(curseur.getAbscisse(), curseur.getOrdonnee());
	}

	public void update() {
		AssociationTouches toucheSuivante = AssociationTouches.trouveProchaineEntree(); //cette fonction boucle jusqu'a la prochaine entree de l'utilisateur
		if (toucheSuivante.isHaut()) {
			curseur.deplacementHaut(grid);//quoi qu'il se passe on deplace d'abord le curseur
			if(stateOfTheGame.equals("Mouvement")) {//si on est en train d'effectuer un mouvement
				deplacementUnite();
			}
			System.out.println("Touche HAUT");
			display();
		}
		if (toucheSuivante.isBas()){ 
			curseur.deplacementBas(grid);
			if(stateOfTheGame.equals("Mouvement")) {//si on est en train d'effectuer un mouvement
				deplacementUnite();
			}
			System.out.println("Touche BAS");
			display();
		}
		if (toucheSuivante.isGauche()) { 
			curseur.deplacementGauche(grid);
			if(stateOfTheGame.equals("Mouvement")) {//si on est en train d'effectuer un mouvement
				deplacementUnite();
			}
			System.out.println("Touche GAUCHE");
			display();
		}
		if 	(toucheSuivante.isDroite()) { 
			curseur.deplacementDroite(grid);
			if(stateOfTheGame.equals("Mouvement")) {//si on est en train d'effectuer un mouvement
				deplacementUnite();
			}
			System.out.println("Touche DROITE");
			 	display();
		}
		
		if (toucheSuivante.isCaractere('t')) {//changement de tour si l'utilisateur appuie sur t
			demandeChangementTour();
			display();
		}
		if(toucheSuivante.isEntree()) {
			Optional<Unit> optionalUnit = grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getOptionalUnit();
			if(optionalUnit.isPresent() && grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getOptionalUnit().isPresent()//on verifie qu'il y a une unite sur la case
					&&stateOfTheGame.equals("SelectionUnite")//on verifie qu'on soit dans l'etat de selection 
					&& optionalUnit.get().getPlayer()==indexJoueurActif//on verifie que l'unite appartient au joueur actif
					&& optionalUnit.get().isAvailable()) {//on verifie qu'elle n'a pas encore ete activee ce tour-ci
				stateOfTheGame="Mouvement";//on met le jeu dans le mode deplacement d'unite
				uniteSelectionne=optionalUnit.get();//on garde en memoire l'unite que l'on veut deplacee
				cheminParcouru.setAbscisse(curseur.getAbscisse());
				cheminParcouru.setOrdonnee(curseur.getOrdonnee());
				cheminParcouru.setPrecedent(null);
				cheminParcouru.setSuivant(null);//on initialise un nouveau chemin
			 	display();
			}
			if(stateOfTheGame.equals("Mouvement") //on verifie qu'on est en deplacement
					&& grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getOptionalUnit().isEmpty() //et que la case est disponible
			&& cheminParcouru.getAbscisse()==curseur.getAbscisse()
			&& cheminParcouru.getOrdonnee()==curseur.getOrdonnee()){//et que le curseur est au bout du chemin de l'unite
				grid.getTile(cheminParcouru.coordonneesOrigineDuChemin()[0], cheminParcouru.coordonneesOrigineDuChemin()[1]).setOptionalUnit(Optional.empty());
				//on enleve l'unite de sa case d'origine
				grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).setOptionalUnit(Optional.ofNullable(uniteSelectionne));
				//on deplace l'unite vers sa destination
				choixAction();
			}
			if(stateOfTheGame.equals("Selection Attaque")) {
				if(optionalUnit.isPresent() && uniteAPortee(uniteSelectionne,optionalUnit.get())) {
					//on verifie qu'il y ait une unite sur la case selectionnee et qu'elle est a portee d'attaque de l'unite selectionnee
					attaque(uniteSelectionne,optionalUnit.get());
						
					}
				}
				
			}
		if(toucheSuivante.isEchap()) {
			if(stateOfTheGame.equals("Mouvement")) {//si l'on appuie sur echap en deplacement d'une unite, on desselectionne celle-ci
				stateOfTheGame="SelectionUnite";
			 	display();
			}
		}
	}

	/**
	 *
	 * cette fonction place le curseur a la position de la premiere unite disponible
	 */
	public void passageUniteSuivante() {
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
	/**
	 *
	 * effectue les actions necessaire a la creation d'un chemin pour une unite
	 * si le chemin boucle, enleve la boucle et rembourse l'unite de ses points de mouvement
	 * si l'unite a les points de mouvement necessaire pour effectuer le mouvement on rajoute un nouvel element au chemin
	 * et on retire les points de mouvement correspondant a l'unite
	 */
	public void deplacementUnite() {
		Optional<Unit> optionalUnit = grid.getTile(curseur.getAbscisse(), curseur.getAbscisse()).getOptionalUnit();
		if(cheminParcouru.positionDansChemin(curseur.getAbscisse(), curseur.getOrdonnee())) {
			//on verifie si la position du curseur fais deja partie du chemin de l'unite
			CheminParcouru cheminInitial = cheminParcouru;
			cheminParcouru.delChemin(new CheminParcouru(curseur.getAbscisse(),curseur.getOrdonnee(),null,null));
			//on retire la partie du chemin qui constitue une boucle
			uniteSelectionne=cheminParcouru.remboursementPM(curseur, grid, uniteSelectionne, cheminInitial, cheminParcouru);
			//on "rembourse" l'unite des points de mouvement qui ne seront finalement pas depenses
		}
		else if(cheminParcouru.cheminPossibilite(curseur.getAbscisse(), curseur.getOrdonnee())//on verifie que la case est adjacent a la derniere partie du chemin
				&&uniteSelectionne.getMovementPoints()>uniteSelectionne.getUnitType().getSubType().getMovementCost()[grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getBiome().id()]
						//on verifie que le cout en point de mouvement de l'unite ne soit pas superieur au cout du deplacement
						&&uniteSelectionne.getUnitType().getSubType().getMovementCost()[grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getBiome().id()]!=0
						//on verifie que le terrain ne soit pas infranchissable
						&&!(optionalUnit.isPresent() && optionalUnit.get().getPlayer()!=indexJoueurActif)) {
						//on verifie qu'une unite ennemie ne soit pas sur la case
			uniteSelectionne.setMovementPoints(uniteSelectionne.getMovementPoints()-uniteSelectionne.getUnitType().getSubType().getMovementCost()[grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getBiome().id()]);
			//on modifie les points de mouvement de l'unite
			CheminParcouru head = new CheminParcouru(curseur.getAbscisse(),curseur.getOrdonnee(),cheminParcouru,null);
			this.cheminParcouru.setSuivant(head);
			this.cheminParcouru = head;
			System.out.println("ttt" + cheminParcouru.getPrecedent());
			//on rajoute la nouvelle etape au chemin parcouru
			//cheminParcouru=cheminParcouru.getSuivant();//on garde la variable cheminParcouru en tete du mouvement
		}
	}
	/**
	 * affiche le chemin prévu pour le deplacement d'une unite selectionnee 
	 */
	public void affichageChemin(CheminParcouru cheminParcouru) {
		if (cheminParcouru != null && cheminParcouru.getPrecedent() != null) {
			int x = cheminParcouru.getAbscisse();
			int y = cheminParcouru.getOrdonnee();
			//on verifie qu'on a bien un chemin a afficher
			if (cheminParcouru.getPrecedent().getAbscisse()-cheminParcouru.getAbscisse()==1) {//chemin depuis la gauche vers la fin du chemin
				affichageFleche(x, y, "DF");
				//on affiche la derniere partie du chemin
			}
			else if(cheminParcouru.getPrecedent().getAbscisse()-cheminParcouru.getAbscisse()==-1) {//chemin depuis la droite vers la fin du chemin
				affichageFleche(x, y, "GF");
				//on affiche la derniere partie du chemin
			}
			else if(cheminParcouru.getPrecedent().getOrdonnee()-cheminParcouru.getOrdonnee()==-1) {//chemin depuis le bas vers la fin du chemin
				affichageFleche(x, y, "BF");
				//on affiche la derniere partie du chemin
			}
			else  {//chemin depuis le haut vers la fin du chemin
				affichageFleche(x, y, "HF");
				//on affiche la derniere partie du chemin
			}
			CheminParcouru parcour = this.cheminParcouru;
			while(parcour.getPrecedent().getPrecedent() != null) {//tant qu'il reste des parties du chemin a afficher
				//parcour.getPrecedent().setPrecedent(parcour);//on relie les elements du chemin
				int oldX = parcour.getAbscisse();
				int oldY = parcour.getOrdonnee();
				parcour=parcour.getPrecedent();//on parcourt le chemin en partant de la fin
				int newX = parcour.getAbscisse();
				int newY = parcour.getOrdonnee();
				if (newX-oldX==1) {//chemin vers la droite
					affichegeFlecheVersDroite(newX, newY, parcour);
					}
				else if (newX-oldX==-1) {//chemin vers la gauche
					affichegeFlecheVersGauche(newX, newY, parcour);
				}
				else if (newY-oldY==1) {//chemin vers le bas
					affichegeFlecheVersBas(newX, newY, parcour);
				}
				else {//chemin vers le haut
					affichegeFlecheVersHaut(newX, newY, parcour);
				}

			}
		}
	}
	
	/**
	 * 
	 * @param string une representation d'une partie de chemin
	 * la premiere lettre representant l'origine du morceau de chemin
	 * et la seconde sa destination
	 */
	public void affichageFleche(int x, int y, String string) {
		System.out.println(string);
		if ("GD".equals(string)) {
			Affichage.dessineImageDansCase(x, y, Chemins.getCheminFleche(Chemins.DIRECTION_GAUCHE, Chemins.DIRECTION_DROITE));
		} else if ("GH".equals(string)) {
			Affichage.dessineImageDansCase(x, y, Chemins.getCheminFleche(Chemins.DIRECTION_GAUCHE, Chemins.DIRECTION_HAUT));
		} else if ("GB".equals(string)) {
			Affichage.dessineImageDansCase(x, y, Chemins.getCheminFleche(Chemins.DIRECTION_GAUCHE, Chemins.DIRECTION_BAS));
		} else if ("DH".equals(string)) {
			Affichage.dessineImageDansCase(x, y, Chemins.getCheminFleche(Chemins.DIRECTION_DROITE, Chemins.DIRECTION_HAUT));
		} else if ("DB".equals(string)) {
			Affichage.dessineImageDansCase(x, y, Chemins.getCheminFleche(Chemins.DIRECTION_DROITE, Chemins.DIRECTION_BAS));
		} else if ("DG".equals(string)) {
			Affichage.dessineImageDansCase(x, y, Chemins.getCheminFleche(Chemins.DIRECTION_DROITE, Chemins.DIRECTION_GAUCHE));
		} else if ("HD".equals(string)) {
			Affichage.dessineImageDansCase(x, y, Chemins.getCheminFleche(Chemins.DIRECTION_HAUT, Chemins.DIRECTION_DROITE));
		} else if ("HB".equals(string)) {
			Affichage.dessineImageDansCase(x, y, Chemins.getCheminFleche(Chemins.DIRECTION_HAUT, Chemins.DIRECTION_BAS));
		} else if ("HG".equals(string)) {
			Affichage.dessineImageDansCase(x, y, Chemins.getCheminFleche(Chemins.DIRECTION_HAUT, Chemins.DIRECTION_GAUCHE));
		} else if ("BD".equals(string)) {
			Affichage.dessineImageDansCase(x, y, Chemins.getCheminFleche(Chemins.DIRECTION_BAS, Chemins.DIRECTION_DROITE));
		} else if ("BH".equals(string)) {
			Affichage.dessineImageDansCase(x, y, Chemins.getCheminFleche(Chemins.DIRECTION_BAS, Chemins.DIRECTION_HAUT));
		} else if ("BG".equals(string)) {
			Affichage.dessineImageDansCase(x, y, Chemins.getCheminFleche(Chemins.DIRECTION_BAS, Chemins.DIRECTION_GAUCHE));
		} else if ("BF".equals(string)) {
			Affichage.dessineImageDansCase(x, y, Chemins.getCheminFleche(Chemins.DIRECTION_BAS, Chemins.DIRECTION_FIN));
		} else if ("HF".equals(string)) {
			Affichage.dessineImageDansCase(x, y, Chemins.getCheminFleche(Chemins.DIRECTION_HAUT, Chemins.DIRECTION_FIN));
		} else if ("GF".equals(string)) {
			Affichage.dessineImageDansCase(x, y, Chemins.getCheminFleche(Chemins.DIRECTION_GAUCHE, Chemins.DIRECTION_FIN));
		} else if ("DF".equals(string)) {
			Affichage.dessineImageDansCase(x, y, Chemins.getCheminFleche(Chemins.DIRECTION_DROITE, Chemins.DIRECTION_FIN));
		} else {
			Affichage.dessineImageDansCase(x, y, Chemins.getCheminFleche(Chemins.DIRECTION_DEBUT, Chemins.DIRECTION_FIN));
		}
	}
	
	
	/**
	 * affiche une partie du chemin d'une direction donnee vers la droite
	 */
	public void affichegeFlecheVersDroite(int x, int y, CheminParcouru cheminParcouru) {
		if (cheminParcouru.getSuivant() == null) {
			affichageFleche(x, y, "GD");
			return;
		}
		if(cheminParcouru.getSuivant().getAbscisse()-cheminParcouru.getAbscisse()==-1) {//chemin depuis la gauche
			affichageFleche(x, y, "GD");
		}
		else if(cheminParcouru.getSuivant().getOrdonnee()-cheminParcouru.getOrdonnee()==-1) {//chemin depuis le bas
			affichageFleche(x, y, "BD");
		}
		else {//chemin depuis le haut
			affichageFleche(x, y, "HD");
		}//on ne peut pas avoir un chemin de la droite vers la droite
	}
	
	
	/**
	 * affiche une partie du chemin d'une direction donnee vers la gauche
	 */
	public void affichegeFlecheVersGauche(int x, int y, CheminParcouru cheminParcouru) {
		if (cheminParcouru.getSuivant() == null) {
			affichageFleche(x, y, "DG");
			return;
		}
		if(cheminParcouru.getSuivant().getAbscisse()-cheminParcouru.getAbscisse()==1) {//chemin depuis la droite
			affichageFleche(x, y, "DG");
		}
		else if(cheminParcouru.getSuivant().getOrdonnee()-cheminParcouru.getOrdonnee()==-1) {//chemin depuis le bas
			affichageFleche(x, y, "BG");
		}
		else {//chemin depuis le haut
			affichageFleche(x, y, "HG");
		}//on ne peut pas avoir un chemin de la gauche vers la gauche
	}
	
	
	/**
	 * affiche une partie du chemin d'une direction donnee vers le haut
	 */
	public void affichegeFlecheVersHaut(int x, int y, CheminParcouru cheminParcouru) {
		if (cheminParcouru.getSuivant() == null) {
			affichageFleche(x, y, "BH");
			return;
		}
		if(cheminParcouru.getSuivant().getAbscisse()-cheminParcouru.getAbscisse()==1) {//chemin depuis la droite
			affichageFleche(x, y, "DH");
		}
		if(cheminParcouru.getSuivant().getAbscisse()-cheminParcouru.getAbscisse()==-1) {//chemin depuis la gauche
			affichageFleche(x, y, "GH");
		}
		else {//chemin depuis le bas
			affichageFleche(x, y, "BH");
		}//on ne peut pas avoir un chemin du haut vers le haut
	}
	
	
	/**
	 * affiche une partie du chemin d'une direction donnee vers le bas
	 */
	public void affichegeFlecheVersBas(int x, int y, CheminParcouru cheminParcouru) {
		if (cheminParcouru.getSuivant() == null) {
			affichageFleche(x, y, "HB");
			return;
		}
		if(cheminParcouru.getSuivant().getAbscisse()-cheminParcouru.getAbscisse()==1) {//chemin depuis la droite
			affichageFleche(x, y, "DB");
		}
		if(cheminParcouru.getSuivant().getAbscisse()-cheminParcouru.getAbscisse()==-1) {//chemin depuis la gauche
			affichageFleche(x, y, "GB");
		}
		else {//chemin depuis le haut
			affichageFleche(x, y, "HB");
		}//on ne peut pas avoir un chemin du bas vers le bas
	}
	

	/**
	 * demande à l'utilisateur d'il veut passer son tour et si oui change le tour
	 */
	public void demandeChangementTour() {
		String[] options = {"Oui", "Non"};
		if (Affichage.popup("Finir le tour de joueur"+indexJoueurActif+"?", options, true, 1) == 0) {
			//le choix 0, "Oui", a été selectionné
			ChangementTour();
		}
	}
	
	/**
	 * methode qui effectue les actions relatives au changement de tour
	 */
	public void ChangementTour() {
		indexJoueurActif=(indexJoueurActif+1)%2;//on change de joueur
		//TODO reinitialiser les points de mouvement des unites, leur utilisation donner les credits dus aux joueurs etc
		System.out.println("FIN DE TOUR");
		stateOfTheGame="SelectionUnite";
		curseur.setAbscisse(0);
		curseur.setOrdonnee(0);//on reinitialise le curseur
	}
	/**
	 * 
	 * @return si l'unite selectionnee peut attaquer une autre unite
	 */
	public boolean peutAttaquer() {
		for(int i=0; i<=grid.getWidth();i++) {
			for(int j=0; j<=grid.getHeight();j++) {
				Optional<Unit> optionalUnit = grid.getTile(i, j).getOptionalUnit();
				if(optionalUnit.isPresent() && uniteAPortee(uniteSelectionne, optionalUnit.get())) {
					return true;//on parcours toute la grille pour voir s'il existe une unite a portee
					//de l'unite selectionnee
				}
			}
		}
		return false;
	}
	/**
	 * 
	 * @return si l'unite selectionnee peut effectuer l'action capturer
	 */
	public boolean peutCapturer() {
		if(uniteSelectionne.getUnitType().getSubType().equals(SubType.FOOT)
		&& grid.getTile(uniteSelectionne.getLocation().getX(), uniteSelectionne.getLocation().getY()).getBiome().getBiomeType().isProperty()) {
			//on regarde si l'unite est une unite a pied et si elle se trouve sur une propriete
			return true;
		}
		return false;
	}
	public void choixAction() {
		if(peutAttaquer()&&peutCapturer()) {
			choixAction1();
		}
		else if(peutAttaquer()) {
			choixAction2();
		}
		else if(peutCapturer()) {
			choixAction3();
		}
		else {//si l'unite ne peut que passer son activation
			finActivationUnitee();
		}
	}
	/**
	 * propose les choix d'option d'une unite a la fin d'un deplacement lorsque 
	 * l'unite peut attaquer et capturer une propriete
	 */
	public void choixAction1() {
		String[] options = {"Fin d'activation", "Attaque", "Capture de Batiment"};
		switch (Affichage.popup("Choisir une Action", options, false, 1)) {
		case 0: finActivationUnitee();break;
		case 1: debutattaque();break;
		case 2:capture();break;
		default:display();
		}
		
	}
	
	/**
	 * propose les choix d'option d'une unite a la fin d'un deplacement lorsque 
	 * l'unite peut attaquer 
	 */
	public void choixAction2() {
		String[] options = {"Fin d'activation", "Attaque", "Capture de Batiment"};
		switch (Affichage.popup("Choisir une Action", options, false, 1)) {
		case 0: finActivationUnitee();break;
		case 1: debutattaque();break;
		case 2:capture();break;
		default:display();
		}
		
	}

	/**
	 * propose les choix d'option d'une unite a la fin d'un deplacement lorsque 
	 * l'unite peut capturer une propriete
	 */
	public void choixAction3() {
		String[] options = {"Fin d'activation", "Capture de Batiment"};
		switch (Affichage.popup("Choisir une Action", options, false, 1)) {
		case 0: finActivationUnitee();break;
		case 1:capture();break;
		default:display();
;
		}
		
	}

	/**
	 * effectue les actions necessaire pour mettre fin a l'activation d'une unite
	 */
	public void finActivationUnitee() {
		uniteSelectionne.setAvailable(false);
		uniteSelectionne=null;
		stateOfTheGame="SelectionUnite";
		passageUniteSuivante();
		display();
	}
	/**
	 * effectue les actions necessaire pour commencer une attaque
	 */
	public void debutattaque() {
		stateOfTheGame="Selection Attaque";
		display();
	}
	/**
	 *
	 * @param uniteAttaquante une unite attaquant
	 * @param uniteDefense une unite se defendant
	 * realise l'attaque d'une unite sur l'autre et si il ya lieu d'en avoir la riposte de l'unitee attaquee
	 */
	public void attaque(Unit uniteAttaquante,Unit uniteDefense) {
		uniteDefense.setLifePoints(uniteDefense.getLifePoints()-uniteAttaquante.getLifePoints()*meilleurArme(uniteAttaquante,uniteDefense)[0]);
		//on reduit les pv de l'unite defensive
		uniteAttaquante.getWeaponery()[meilleurArme(uniteAttaquante,uniteDefense)[1]].setAmmunitions(uniteAttaquante.getWeaponery()[meilleurArme(uniteAttaquante,uniteDefense)[1]].getAmmunitions()-1);
		//on reduit les munitions de l'arme utilisee de 1
		if (uniteDefense.getLifePoints()<=0) {
			grid.getTile(uniteDefense.getLocation().getX(), uniteDefense.getLocation().getY()).setOptionalUnit(Optional.empty());
		}
		else if(uniteAPortee(uniteDefense,uniteAttaquante)){//on verifie que l'unite en defense ait une arme a portee de l'unite attaquante
			uniteAttaquante.setLifePoints(uniteAttaquante.getLifePoints()-uniteDefense.getLifePoints()*meilleurArme(uniteDefense,uniteAttaquante)[0]);
			//l'unite en defense replique si il lui reste des points de vie
			uniteDefense.getWeaponery()[meilleurArme(uniteDefense,uniteAttaquante)[1]].setAmmunitions(uniteDefense.getWeaponery()[meilleurArme(uniteDefense,uniteAttaquante)[1]].getAmmunitions()-1);
			//ses munitions baissent
		}
		finActivationUnitee();
	}
	/**
	 *
	 * @param uniteAttaquante l'unite procedant a l'attaque
	 * @param uniteDefense l'unite se defendant
	 * @return le degat de la meilleur arme et le rang de l'arme dans cet ordre
	 */
	public int[] meilleurArme(Unit uniteAttaquante, Unit uniteDefense) {
		int efficacite=0;
		int rangArme=0;
		int distance = Math.abs((uniteDefense.getLocation().getX()-uniteAttaquante.getLocation().getX())+(uniteDefense.getLocation().getY()-uniteAttaquante.getLocation().getY()));
		for(int i=0; i<uniteAttaquante.getWeaponery().length;i++) {
			//TODO rajouter condition munitions de l'arme>0
			if(uniteAttaquante.getWeaponery()[i].getMaxrange()<=distance
					&&uniteAttaquante.getWeaponery()[i].getMinrange()>=distance
					&&efficacite<uniteAttaquante.getWeaponery()[i].efficiencyWeapon(uniteDefense)
					&&uniteAttaquante.getWeaponery()[i].getAmmunitions()>0) {
				//on verifie que l'arme a la portee necessaire pour tirer sur l'unite defense
				//et que elle est la plus efficace des armes vues pour le moment et qu'elle possede encore des munitions
				efficacite=uniteAttaquante.getWeaponery()[i].efficiencyWeapon(uniteDefense);
				rangArme=i;
			}
		}
		int[] a={efficacite,rangArme};
		return a;
	}
	/**
	 * effectue les actions necessaire pour commencer une capture
	 */
	public void capture() {
		int pointsdeCapture=grid.getTile(uniteSelectionne.getLocation().getX(), uniteSelectionne.getLocation().getY()).getBiome().getPC();
		pointsdeCapture-=uniteSelectionne.getLifePoints();//on soustrait aux points de capture
		//de la propriete les points de vie de l'unite la capturant
		if(pointsdeCapture<=0) {//on regarde si la propriete a ete capturee
			grid.getTile(uniteSelectionne.getLocation().getX(), uniteSelectionne.getLocation().getY()).getBiome().setPC(10);
			//on reinitialise ses points de capture 
			//TODO changer le joueur possedant la propriete
		}
		else {
			grid.getTile(uniteSelectionne.getLocation().getX(), uniteSelectionne.getLocation().getY()).getBiome().setPC(pointsdeCapture);
		}
		finActivationUnitee();
	}

	/**
	 *
	 * @param uniteattaquante unite attaquante
	 * @param unitedefense unite defense
	 * @return si uniteattaquante a au moins une arme a portee de tir de unitdefense
	 */
	public boolean uniteAPortee(Unit uniteattaquante,Unit unitedefense) {
		int distance = Math.abs((unitedefense.getLocation().getX()-uniteattaquante.getLocation().getX())+(unitedefense.getLocation().getY()-uniteattaquante.getLocation().getY()));
		//on clacule la distance separant les deux unites
		for(int i =0; i<=uniteattaquante.getWeaponery().length;i++) {
			if(uniteattaquante.getWeaponery()[i].getAmmunitions()>0//on verifie que l'arme atoujours des munitions
					&&distance<=uniteattaquante.getWeaponery()[i].getMaxrange()&&distance>=uniteattaquante.getWeaponery()[i].getMinrange()) {
				return true;
			}
		}
		return false;
	}
	
}

