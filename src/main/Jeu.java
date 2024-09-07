/**
 * package principal
 */
package main;

import aw.common.tile.biome.TileBiome;
import aw.common.tile.biome.TileBiomeType;
import aw.common.unit.Unit;
import aw.common.unit.UnitType;
import aw.common.unit.UnitType.SubType;
import aw.common.unit.Weapon;
import aw.common.unit.impl.aerien.Aerien;
import aw.core.game.grid.Grid;
import aw.core.game.grid.Tile;
import aw.core.game.grid.location.Location;
import aw.core.player.Player;
import librairies.AssociationTouches;
import librairies.StdDraw;
import ressources.Config;
import ressources.ParseurCartes;
import ressources.Affichage;
import ressources.Chemins;

import java.awt.Color;
import java.awt.Font;
import java.util.*;
import java.util.stream.Collectors;

import nouveaucode.Curseur;
import nouveaucode.CheminParcouru;

public class Jeu {
    private List<Tile> listQG;//la liste des QG
    private List<Unit> uniteObservees;//contient les unites deja observees
    private boolean finTourAutomatiqueActivejoueur1;
    private boolean finTourAutomatiqueActivejoueur2;
    private String stateOfTheGame;
    private Unit uniteSelectionne;
    private CheminParcouru cheminParcouru;
    private Curseur curseur;
    private int indexJoueurActif; //l'indice du joueur actif:  1 = rouge, 2 = bleu
    // l'indice 0 est reserve au neutre, qui ne joue pas mais peut posseder des proprietes
    //creditsJoueurs[0] est le credit du joueur 1 et creditsJoueurs[1] est le credit du joueur 0.
    private Player player1;
    private Player player2;
    private Grid grid;
    private boolean gameIsOver;//true si la partie est fini false sinon

    public Jeu(String fileName) throws Exception {
        this.listQG = new ArrayList<Tile>();
        this.uniteObservees = new ArrayList<Unit>();
        this.finTourAutomatiqueActivejoueur1 = false;
        this.finTourAutomatiqueActivejoueur2 = false;
        this.gameIsOver = false;
        this.curseur = new Curseur(0, 0);
        this.cheminParcouru = new CheminParcouru(0, 0, null, null);
        ;
        this.uniteSelectionne = null;
        this.stateOfTheGame = "SelectionUnite";
        int[] a = {0, 0};
        this.player1 = new Player(1, new ArrayList<Unit>(), new ArrayList<Unit>(), new ArrayList<Tile>(), 0);
        this.player2 = new Player(2, new ArrayList<Unit>(), new ArrayList<Unit>(), new ArrayList<Tile>(), 0);
        //appel au parseur, qui renvoie un tableau de String
        String[][] carteString = ParseurCartes.parseCarte(fileName);
        for (int i = 0; i < carteString.length; i++) {
            for (int j = 0; j < carteString[0].length; j++) {
                System.out.print(carteString[i][j]);
                if (j != carteString[0].length) {
                    System.out.print(" | ");
                } else {
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
        return gameIsOver;
    }

    /**
     * Affiche les different statues et statistique du jeu
     */
    public void afficheStatutJeu() {
        Affichage.videZoneTexte();
        if (isOver()) {
            Affichage.afficheTexteDescriptif(stateOfTheGame);
        } else {
            if (indexJoueurActif == 1)
                afficheStatutJeu(player1);
            else
                afficheStatutJeu(player2);

            afficheStatueUnitEtPropriete();
        }
    }

    /**
     * Affiche le statue du jeu pour un joueur
     * @param player un joueur
     */
    private void afficheStatutJeu(Player player) {
        Affichage.afficheTexteDescriptif("Statut : " + stateOfTheGame +
                ", Joueur numero " + indexJoueurActif +
                ", Credits joueur : " + player.getCredit() +
                (stateOfTheGame.equals("Mouvement") ? ", PV unit select : " + uniteSelectionne.getLifePoints() : ""));
    }

    /**
     * Affiche certaine statistique a cote des units et des proprietes
     */
    private void afficheStatueUnitEtPropriete() {
        for (int i = 0; i < this.grid.getWidth(); i++) {
            for (int j = 0; j < this.grid.getHeight(); j++) {
                Tile tile = this.grid.getTile(i, j);
                String s = "";
                if (tile.getOptionalUnit().isPresent()) {
                    Unit unit = tile.getOptionalUnit().get();
                    s += unit.getLifePoints() + "HP";
                    if (unit.getFuel() < unit.getFuelMax() / 2 || unit.equals(uniteSelectionne))
                        s += " | " + unit.getFuel() + " Fuel";
                }

                TileBiome biome = tile.getBiome();
                if (biome.getBiomeType().isProperty() && biome.getPC() < 10)
                    s += (s.isEmpty() ? "" : " | ") + biome.getPC() + "PC";

                Affichage.afficheTexteDansCase(i, j, s,
                        Color.BLACK, 0, 0, Font.decode("Arial"));
            }
        }
    }

    /**
     * effectue l'affichage de la partie en cours
     */
    public void display() {
        StdDraw.clear();
        this.grid.draw();//on dessine les tuiles de la grille et les unites sur ces tuiles
        afficheStatutJeu();
        //TODO afficher les pv munition et carburant des unites
        drawGameCursor();//dessine la position du curseur
        affichageChemin();//afiche le chemin que va parcourir une unite en mouvement si c'est pertinent
        StdDraw.show(); //montre a l'ecran les changement demandes
    }

    public void initialDisplay() {
        debutJeu();
        StdDraw.enableDoubleBuffering(); // rend l'affichage plus fluide: tout draw est mis en buffer et ne s'affiche qu'au prochain StdDraw.show();
    }

    /**
     * dessine le curseur a sa position
     */
    public void drawGameCursor() {
        Affichage.dessineCurseur(curseur.getAbscisse(), curseur.getOrdonnee());
    }

    /**
     * prend les entrees de l'utilisateur et effectue les actions y correspondant
     */
    public void update() {
        if ((finTourAutomatiqueActivejoueur1 && indexJoueurActif == 1) || (finTourAutomatiqueActivejoueur2 && indexJoueurActif == 2)) {
            //on verifie si le joueur actif a activee la fin de tour automatique
            verificationFinTourAuto();//on verifie qu'il reste des actions au joueur actif sans quoi le tour passe automatiquement a l'autre joueur
        }
        AssociationTouches toucheSuivante = AssociationTouches.trouveProchaineEntree(); //cette fonction boucle jusqu'a la prochaine entree de l'utilisateur
        if (toucheSuivante.isHaut()) {
            actionHaut();
            display();
        }
        if (toucheSuivante.isBas()) {
            actionBas();
            display();
        }
        if (toucheSuivante.isGauche()) {
            actionGauche();
            display();
        }
        if (toucheSuivante.isDroite()) {
            actionDroite();
            display();
        }

        if (toucheSuivante.isCaractere('t')) {//changement de tour si l'utilisateur appuie sur t
            demandeChangementTour();
            display();
        }
        if (toucheSuivante.isCaractere('w')) {//fin de partie si l'utilisateur appui sur f
            demandeFinPartie();
            display();
        }
        if (toucheSuivante.isCaractere('a')) {//activation ou desactivation de la fin de tour automatique pour le joueur actif
            actionA();
        }
        if (toucheSuivante.isCaractere('n')) {//passage a la prochaine unite utlisiable
            actionN();
            display();
        }
        if (toucheSuivante.isEntree()) {
            actionEntree();
        }
        if (toucheSuivante.isEchap()) {
            actionEchap();
        }
    }

    public void actionHaut() {
        System.out.println("Touche HAUT");
        curseur.deplacementHaut(grid);//quoi qu'il se passe on deplace d'abord le curseur
        if (stateOfTheGame.equals("Mouvement")) {//si on est en train d'effectuer un mouvement
            deplacementUnite();//on deplace l'unite prealablement selectionnee selon les souhaits de l'utilisateur
        }
    }

    public void actionBas() {
        System.out.println("Touche BAS");
        curseur.deplacementBas(grid);
        if (stateOfTheGame.equals("Mouvement")) {
            deplacementUnite();
        }
    }

    public void actionGauche() {
        System.out.println("Touche GAUCHE");
        curseur.deplacementGauche(grid);
        if (stateOfTheGame.equals("Mouvement")) {
            deplacementUnite();
        }
    }

    public void actionDroite() {
        System.out.println("Touche DROITE");
        curseur.deplacementDroite(grid);
        if (stateOfTheGame.equals("Mouvement")) {
            deplacementUnite();
        }
    }

    public void actionA() {
        if (indexJoueurActif == 1) {
            finTourAutomatiqueActivejoueur1 = !finTourAutomatiqueActivejoueur1;//on inverse le boolean correespiondant a si le joueur vet la fin de tour automatique
        } else {
            finTourAutomatiqueActivejoueur2 = !finTourAutomatiqueActivejoueur2;
        }
    }

    public void actionEntree() {
        Optional<Unit> optionalUnit = grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getOptionalUnit();
        if (optionalUnit.isPresent() && grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getOptionalUnit().isPresent()//on verifie qu'il y a une unite sur la case
                && stateOfTheGame.equals("SelectionUnite")//on verifie qu'on soit dans l'etat de selection
                && optionalUnit.get().getPlayer() == indexJoueurActif//on verifie que l'unite appartient au joueur actif
                && isAvailable(optionalUnit.get())) {//on verifie qu'elle n'a pas encore ete activee ce tour-ci
            uniteSelectionne = optionalUnit.get();//on garde en memoire l'unite que l'on veut deplacee
            initialisationMouvement();
            display();
        } else if (UsineAvailable()) {
            creationUniteUsine();
            display();
        } else if (peutDeplacerUnite()) {
            deplacementEffectifUnite();
        } else if (stateOfTheGame.equals("Selection Attaque")) {
            if (optionalUnit.isPresent()
                    && uniteAPortee(uniteSelectionne, optionalUnit.get())
                    && optionalUnit.get().getPlayer() != indexJoueurActif) {
                //on verifie qu'il y ait une unite ennemie sur la case selectionnee et qu'elle est a portee d'attaque de l'unite selectionnee
                attaque(uniteSelectionne, optionalUnit.get());

            }
        } else if (stateOfTheGame.equals("Ravitaillement") && peutRavitaillerUnite()) {
            Ravitaillement();
            display();
        }
    }

    public void actionEchap() {
        if (stateOfTheGame.equals("Mouvement")) {//si l'on appuie sur echap en deplacement d'une unite, on desselectionne celle-ci
            stateOfTheGame = "SelectionUnite";
            if (!(uniteSelectionne instanceof Aerien))
                cheminParcouru.remboursementPM(grid, uniteSelectionne);
            cheminParcouru = new CheminParcouru(0, 0, null, null);
            uniteSelectionne = null;
            display();
        }
    }

    public void actionN() {
        passageUniteSuivante();
    }

    /**
     *
     * cette fonction place le curseur a la position de la premiere unite disponible du joueur actif
     * qui n'a pas encore ete observee
     */
    public void passageUniteSuivante() {
        if (indexJoueurActif == 1) {
            if (player1.getUnitesDisponibles().isEmpty()) {//si le joueur actif n'a plus d'unites disponibles
                String[] options = {"revenir au jeu"};
                Affichage.popup("Vous n'avez plus d'unites disponibles", options, true, 0);
            } else {
                for (int i = 0; i < player1.getUnitesDisponibles().size(); i++) {
                    if (!uniteObservees.contains(player1.getUnitesDisponibles().get(i))) {//si l'unite disponible n'est pas dans la liste des unites deja observees
                        uniteObservees.add(player1.getUnitesDisponibles().get(i));//on y rajoute l'unite en question
                        curseur.setAbscisse(player1.getUnitesDisponibles().get(i).getLocation().getX());
                        curseur.setOrdonnee(player1.getUnitesDisponibles().get(i).getLocation().getY());
                        //on place le curseur a la position de l'unite en question

                        return;

                    }

                }
            }
        }
        display();
    }

    /**
     *
     * effectue les actions necessaire a la creation d'un chemin pour une unite
     * si le chemin boucle, enleve la boucle et rembourse l'unite de ses points de mouvement
     * si l'unite a les points de mouvement necessaire pour effectuer le mouvement on rajoute un nouvel element au chemin
     * et on retire les points de mouvement correspondant a l'unite
     */
    public void deplacementUnite() {
        Optional<Unit> optionalUnit = grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getOptionalUnit();
        if (cheminParcouru.positionDansChemin(curseur.getAbscisse(), curseur.getOrdonnee())) {
            //on verifie si la position du curseur fais deja partie du chemin de l'unite
            CheminParcouru cheminInitial = cheminParcouru;
            cheminParcouru = new CheminParcouru(curseur.getAbscisse(), curseur.getOrdonnee(), null, null).delChemin(cheminParcouru);
            //on retire la partie du chemin qui constitue une boucle
            uniteSelectionne = cheminParcouru.remboursementPM(curseur, grid, uniteSelectionne, cheminInitial, cheminParcouru);
            //on "rembourse" l'unite des points de mouvement qui ne seront finalement pas depenses
        } else if (peutSeDeplacer(optionalUnit)) {
            int[] movementCost = uniteSelectionne.getUnitType().getSubType().getMovementCost();
            Tile tile = grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee());
            uniteSelectionne.setMovementPoints(uniteSelectionne.getMovementPoints() - movementCost[tile.getBiome().id()]);
            if (!(uniteSelectionne instanceof Aerien))
                uniteSelectionne.setFuel(uniteSelectionne.getFuel() - movementCost[tile.getBiome().id()]);
            //on modifie les points de mouvement de l'unite
            CheminParcouru head = new CheminParcouru(curseur.getAbscisse(), curseur.getOrdonnee(), cheminParcouru, null);
            this.cheminParcouru.setSuivant(head);
            this.cheminParcouru = head;
            //on rajoute la nouvelle etape au chemin parcouru
            //cheminParcouru=cheminParcouru.getSuivant();//on garde la variable cheminParcouru en tete du mouvement
        }
    }

    /**
     *
     * @param uniteeOptionnelle
     * @return si l'unitee peut se deplacer sur la case du curseur
     */
    public boolean peutSeDeplacer(Optional<Unit> uniteeOptionnelle) {
        return (cheminParcouru.cheminPossibilite(curseur.getAbscisse(), curseur.getOrdonnee())//on verifie que la case est adjacent a la derniere partie du chemin
                && uniteSelectionne.getMovementPoints() >= uniteSelectionne.getUnitType().getSubType().getMovementCost()[grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getBiome().id()]
                //on verifie que le cout en point de mouvement de l'unite ne soit pas superieur au cout du deplacement
                && uniteSelectionne.getUnitType().getSubType().getMovementCost()[grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getBiome().id()] != 0
                //on verifie que le terrain ne soit pas infranchissable
                && !(uniteeOptionnelle.isPresent() && uniteeOptionnelle.get().getPlayer() != indexJoueurActif)
                //on verifie qu'une unite ennemie ne soit pas sur la case
                && uniteSelectionne.getFuel() >= uniteSelectionne.getUnitType().getSubType().getMovementCost()[grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getBiome().id()]);
        //on verifie que l'unite a le carburant necessaire pour effectuer le mouvement

    }

    /**
     * affiche le chemin prévu pour le deplacement d'une unite selectionnee
     */
    public void affichageChemin() {
        if (cheminParcouru != null && cheminParcouru.getPrecedent() != null) {
            int x = cheminParcouru.getAbscisse();
            int y = cheminParcouru.getOrdonnee();
            //on verifie qu'on a bien un chemin a afficher
            if (cheminParcouru.getPrecedent().getAbscisse() - cheminParcouru.getAbscisse() == 1) {//chemin depuis la gauche vers la fin du chemin
                affichageFleche(x, y, "DF");
                //on affiche la derniere partie du chemin
            } else if (cheminParcouru.getPrecedent().getAbscisse() - cheminParcouru.getAbscisse() == -1) {//chemin depuis la droite vers la fin du chemin
                affichageFleche(x, y, "GF");
                //on affiche la derniere partie du chemin
            } else if (cheminParcouru.getPrecedent().getOrdonnee() - cheminParcouru.getOrdonnee() == -1) {//chemin depuis le bas vers la fin du chemin
                affichageFleche(x, y, "BF");
                //on affiche la derniere partie du chemin
            } else {//chemin depuis le haut vers la fin du chemin
                affichageFleche(x, y, "HF");
                //on affiche la derniere partie du chemin
            }
            CheminParcouru parcour = this.cheminParcouru;
            while (parcour.getPrecedent().getPrecedent() != null) {//tant qu'il reste des parties du chemin a afficher
                //parcour.getPrecedent().setPrecedent(parcour);//on relie les elements du chemin
                int oldX = parcour.getAbscisse();
                int oldY = parcour.getOrdonnee();
                parcour = parcour.getPrecedent();//on parcourt le chemin en partant de la fin
                int newX = parcour.getAbscisse();
                int newY = parcour.getOrdonnee();
                if (newX - oldX == -1) {//chemin vers la droite
                    affichegeFlecheVersDroite(newX, newY, parcour);
                } else if (newX - oldX == 1) {//chemin vers la gauche
                    affichegeFlecheVersGauche(newX, newY, parcour);
                } else if (newY - oldY == 1) {//chemin vers le bas
                    affichegeFlecheVersBas(newX, newY, parcour);
                } else {//chemin vers le haut
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
     *
     * @param x l'abscisse de la case a afficher
     * @param y l'ordonnee de la case
     * @param cheminParcouru le chemin de l'unite
     * affiche la partie du chemin correspondant au chement en c,y qui se dirige vers la droite
     */
    public void affichegeFlecheVersDroite(int x, int y, CheminParcouru cheminParcouru) {
        if (cheminParcouru.getPrecedent().getAbscisse() - cheminParcouru.getAbscisse() == -1) {//chemin depuis la gauche
            affichageFleche(x, y, "GD");
        } else if (cheminParcouru.getPrecedent().getOrdonnee() - cheminParcouru.getOrdonnee() == -1) {//chemin depuis le bas
            affichageFleche(x, y, "BD");
        } else {//chemin depuis le haut
            affichageFleche(x, y, "HD");
        }//on ne peut pas avoir un chemin de la droite vers la droite
    }


    /**
     *
     * @param x l'abscisse de la case a afficher
     * @param y l'ordonnee de la case
     * @param cheminParcouru le chemin de l'unite
     * affiche la partie du chemin correspondant au chement en c,y qui se dirige vers la gauche
     */
    public void affichegeFlecheVersGauche(int x, int y, CheminParcouru cheminParcouru) {
        if (cheminParcouru.getPrecedent().getAbscisse() - cheminParcouru.getAbscisse() == 1) {//chemin depuis la droite
            affichageFleche(x, y, "DG");
        } else if (cheminParcouru.getPrecedent().getOrdonnee() - cheminParcouru.getOrdonnee() == -1) {//chemin depuis le bas
            affichageFleche(x, y, "BG");
        } else {//chemin depuis le haut
            affichageFleche(x, y, "HG");
        }//on ne peut pas avoir un chemin de la gauche vers la gauche
    }


    /**
     *
     * @param x l'abscisse de la case a afficher
     * @param y l'ordonnee de la case
     * @param cheminParcouru le chemin de l'unite
     * affiche la partie du chemin correspondant au chement en c,y qui se dirige vers le haut
     */
    public void affichegeFlecheVersHaut(int x, int y, CheminParcouru cheminParcouru) {
        if (cheminParcouru.getPrecedent().getAbscisse() - cheminParcouru.getAbscisse() == 1) {//chemin depuis la droite
            affichageFleche(x, y, "DH");
        } else if (cheminParcouru.getPrecedent().getAbscisse() - cheminParcouru.getAbscisse() == -1) {//chemin depuis la gauche
            affichageFleche(x, y, "GH");
        } else {//chemin depuis le bas
            affichageFleche(x, y, "BH");
        }//on ne peut pas avoir un chemin du haut vers le haut
    }


    /**
     *
     * @param x l'abscisse de la case a afficher
     * @param y l'ordonnee de la case
     * @param cheminParcouru le chemin de l'unite
     * affiche la partie du chemin correspondant au chement en c,y qui se dirige vers le bas
     */
    public void affichegeFlecheVersBas(int x, int y, CheminParcouru cheminParcouru) {
        if (cheminParcouru.getPrecedent().getAbscisse() - cheminParcouru.getAbscisse() == 1) {//chemin depuis la droite
            affichageFleche(x, y, "DB");
        } else if (cheminParcouru.getPrecedent().getAbscisse() - cheminParcouru.getAbscisse() == -1) {//chemin depuis la gauche
            affichageFleche(x, y, "GB");
        } else {//chemin depuis le haut
            affichageFleche(x, y, "HB");
        }//on ne peut pas avoir un chemin du bas vers le bas
    }


    /**
     * demande à l'utilisateur d'il veut passer son tour et si oui change le tour
     */
    public void demandeChangementTour() {
        String[] options = {"Oui", "Non"};
        if (Affichage.popup("Finir le tour de joueur" + indexJoueurActif + "?", options, true, 1) == 0) {
            //le choix 0, "Oui", a été selectionné
            changementTour();
        }
    }

    /**
     * methode qui effectue les actions relatives au changement de tour
     */
    public void changementTour() {
        finTour();
        debutTour();
    }

    /**
     * effectue les actions necessaires a la fin du tour
     * reinitialise la liste des unites observees
     * reinitialise la disponibilite des unites du joueur passant le tour
     * rajoute au joueur les credits que ses batiments lui rapportent
     * change le joueur actif
     */
    public void finTour() {
        uniteObservees.clear();
        if (indexJoueurActif == 1) {
            finTour(player1);
        } else {
            finTour(player2);
        }
        switch (indexJoueurActif) {//on change de joueur
            case 1:
                indexJoueurActif = 2;
                break;
            default:
                indexJoueurActif = 1;
        }
    }

    private void finTour(Player player) {
        for (int i = 0; i < player.getUnitesNonDisponible().size(); i++) {//on reinitialise la disponibilite des unites
            player.getUnitesNonDisponible().get(i).setAvailable(true);
            player.getUnitesNonDisponible().get(i).setMovementPoints(player.getUnitesNonDisponible().get(i).getMaxMouvementPoint());
            ;
            player.getUnitesDisponibles().add(player.getUnitesNonDisponible().get(i));
        }

        for (Unit unit : player.getUnitesDisponibles()
                .stream()
                .filter(unit -> unit.getUnitType().getSubType() == SubType.AERIAL)
                .collect(Collectors.toSet())) {
            System.out.println("tost");
            unit.setFuel(Math.max(0, unit.getFuel() - ((Aerien) unit).removedFuelByTurn()));
        }
        player.getUnitesNonDisponible().clear();
        player.setCredit(player.getCredit() + 1000 * player.getProprietes().size());//on rajoute au joueur les credits que ses proprietes rapportent
    }

    /**
     * reinitialise les munitions, les points de vie et le carburant des unites du joueur debutant son tour si elles se situent sur une de ses proprietes
     * remet le jeu en position de debut de tour en changeant le status du jeu en "SelectionUnite" et en reinitialisant le curseur en (0.0)
     */
    public void debutTour() {
        if (indexJoueurActif == 1) {
            debutTour(player1);
        } else {
            debutTour(player2);
        }
        stateOfTheGame = "SelectionUnite";
        display();
    }

    private void debutTour(Player player) {
        List<Unit> disponible = player.getUnitesDisponibles();
        for (int i = 0; i < player.getUnitesNonDisponible().size(); i++) {//on reinitialise les munitions et carburant des unites commencant sur une
            Unit unit = disponible.get(i);

            //propriete de son joueur
            if (grid.getTile(disponible.get(i).getLocation().getX(), disponible.get(i).getLocation().getY()).getBiome().getBiomeType().isProperty()
                    //on verifie que l'unite est sur une propriete
                    && grid.getTile(disponible.get(i).getLocation().getX(), disponible.get(i).getLocation().getY()).getBiome().getPlayer() == 1) {
                //et que cette unite est au joueur actif
                unit.setFuel(unit.getFuelMax());//on reinitialise le carburant
                reinitialisationMunition(unit);//on reinitialise les munitions
                unit.setLifePoints(10);//on reinitialise les points de vie
            }
        }

        disponible.stream()
                .filter(unit -> unit instanceof Aerien && unit.getFuel() <= 0)
                .forEach(unit -> {
                    Tile tile = this.grid.getTile(unit.getLocation().getX(), unit.getLocation().getY());
                    tile.setOptionalUnit(Optional.empty());
                });
    }

    /**
     * @param unite
     * reinitialise les munitions de toutes les armes de l'unite
     */
    public void reinitialisationMunition(Unit unite) {
        for (int i = 0; i < unite.getWeaponery().length; i++) {
            unite.getWeaponery()[i].setAmmunitions(unite.getWeaponery()[i].getAmmunitionsMax());
        }
    }

    /**
     * demande a l'utilisateur s'il veut finir la partie
     */
    public void demandeFinPartie() {
        String[] options = {"Oui", "Non"};
        if (Affichage.popup("Finir la Partie" + "?", options, true, 1) == 0) {
            //le choix 0, "Oui", a été selectionné
            stateOfTheGame = "Fin de la Partie";
            gameIsOver = true;
        }
    }

    /**
     *
     * @return si l'unite selectionnee peut attaquer une autre unite
     */
    public boolean peutAttaquer() {
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                Optional<Unit> optionalUnit = grid.getTile(i, j).getOptionalUnit();
                if (optionalUnit.isPresent() && optionalUnit.get().getPlayer() != indexJoueurActif && uniteAPortee(uniteSelectionne, optionalUnit.get())) {
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
        if (uniteSelectionne.getUnitType().getSubType().equals(SubType.FOOT)
                && grid.getTile(uniteSelectionne.getLocation().getX(), uniteSelectionne.getLocation().getY()).getBiome().getBiomeType().isProperty()) {
            //on regarde si l'unite est une unite a pied et si elle se trouve sur une propriete
            return true;
        }
        return false;
    }

    /**
     *
     * @return si l'unite est un convoi et possede une unite alliee adjacente
     */
    public boolean peutRavitailler() {
        if (uniteSelectionne.getUnitType().equals(UnitType.CONVOY) && uniteAllieAdjacente()) {
            return true;
        }
        return false;
    }

    /**
     *
     * @return si il y aune unite alliee adjacente a l'unitee seletionnee
     */
    public boolean uniteAllieAdjacente() {
        Location location = uniteSelectionne.getLocation();
        int x = location.getX();
        int y = location.getY();
        int gridWidth = this.grid.getWidth();
        int gridHeight = this.grid.getHeight();
        return (x - 1 >= 0 && (grid.getTile(x - 1, y).getOptionalUnit().isPresent()
                && grid.getTile(x - 1, y).getOptionalUnit().get().getPlayer() == indexJoueurActif)
                || (x + 1 < gridWidth && grid.getTile(x + 1, y).getOptionalUnit().isPresent()
                && grid.getTile(x + 1, y).getOptionalUnit().get().getPlayer() == indexJoueurActif)
                || (y + 1 < gridHeight && grid.getTile(x, y + 1).getOptionalUnit().isPresent()
                && grid.getTile(x, y + 1).getOptionalUnit().get().getPlayer() == indexJoueurActif)
                || (y - 1 >= 0 && grid.getTile(x, y - 1).getOptionalUnit().isPresent())
                && grid.getTile(x, y - 1).getOptionalUnit().get().getPlayer() == indexJoueurActif);
    }

    /**
     * demande au joueur quelle action il veut effectuer entre celles possibles
     */
    public void choixAction() {
        if (peutAttaquer() && peutCapturer() && peutRavitailler()) {
            choixAction0();
        } else if (peutAttaquer() && peutCapturer()) {
            choixAction1();
        } else if (peutAttaquer()) {
            choixAction2();
        } else if (peutCapturer()) {
            choixAction3();
        } else if (peutAttaquer() && peutRavitailler()) {
            choixAction4();
        } else if (peutCapturer() && peutRavitailler()) {
            choixAction5();
        } else if (peutRavitailler()) {
            choixAction6();
        } else {//si l'unite ne peut que passer son activation
            finActivationUnitee();
        }
    }

    /**
     * propose les choix d'option d'une unite a la fin d'un deplacement lorsque
     * l'unite peut attaquer et capturer une propriete
     */
    public void choixAction0() {
        String[] options = {"Fin d'activation", "Attaque", "Capture de Batiment", "Ravitaillement"};
        switch (Affichage.popup("Choisir une Action", options, false, 1)) {
            case 0:
                finActivationUnitee();
                break;
            case 1:
                debutattaque();
                break;
            case 2:
                capture();
                break;
            case 3:
                debutRavitaillement();
                break;
            default:
                display();
        }

    }

    /**
     * propose les choix d'option d'une unite a la fin d'un deplacement lorsque
     * l'unite peut attaquer et capturer une propriete
     */
    public void choixAction1() {
        String[] options = {"Fin d'activation", "Attaque", "Capture de Batiment"};
        switch (Affichage.popup("Choisir une Action", options, false, 1)) {
            case 0:
                finActivationUnitee();
                break;
            case 1:
                debutattaque();
                break;
            case 2:
                capture();
                break;
            default:
                display();
        }

    }

    /**
     * propose les choix d'option d'une unite a la fin d'un deplacement lorsque
     * l'unite peut attaquer
     */
    public void choixAction2() {
        String[] options = {"Fin d'activation", "Attaque"};
        switch (Affichage.popup("Choisir une Action", options, false, 1)) {
            case 0:
                finActivationUnitee();
                break;
            case 1:
                debutattaque();
                break;
            default:
                display();
        }

    }

    /**
     * propose les choix d'option d'une unite a la fin d'un deplacement lorsque
     * l'unite peut capturer une propriete
     */
    public void choixAction3() {
        String[] options = {"Fin d'activation", "Capture de Batiment"};
        switch (Affichage.popup("Choisir une Action", options, false, 1)) {
            case 0:
                finActivationUnitee();
                break;
            case 1:
                capture();
                break;
            default:
                display();
                ;
        }

    }

    /**
     * propose les choix d'option d'une unite a la fin d'un deplacement lorsque
     * l'unite peut attaquer et ravitailler
     */
    public void choixAction4() {
        String[] options = {"Fin d'activation", "Attaque", "Ravitaillement"};
        switch (Affichage.popup("Choisir une Action", options, false, 1)) {
            case 0:
                finActivationUnitee();
                break;
            case 1:
                debutattaque();
                break;
            case 2:
                debutRavitaillement();
                break;
            default:
                display();
        }

    }

    /**
     * propose les choix d'option d'une unite a la fin d'un deplacement lorsque
     * l'unite peut capturer et ravitailler
     */
    public void choixAction5() {
        String[] options = {"Fin d'activation", "Capture de Batiment", "Ravitaillement"};
        switch (Affichage.popup("Choisir une Action", options, false, 1)) {
            case 0:
                finActivationUnitee();
                break;
            case 1:
                debutattaque();
                break;
            case 2:
                debutRavitaillement();
                break;
            default:
                display();
        }

    }

    /**
     * propose les choix d'option d'une unite a la fin d'un deplacement lorsque
     * l'unite peut ravitailler
     */
    public void choixAction6() {
        String[] options = {"Fin d'activation", "Ravitaillement"};
        switch (Affichage.popup("Choisir une Action", options, false, 1)) {
            case 0:
                finActivationUnitee();
                break;
            case 1:
                debutRavitaillement();
                break;
            default:
                display();
        }

    }

    /**
     * met le jeu dans le mode ravitaillement
     */
    public void debutRavitaillement() {
        stateOfTheGame = "Ravitaillement";
        display();
    }

    /**
     *
     * @return si l'unite sur le curseur est adjacente au convoit et est alliee
     */
    public boolean peutRavitaillerUnite() {
        return (Math.abs(curseur.getAbscisse() - uniteSelectionne.getLocation().getX()) + Math.abs(curseur.getAbscisse() - uniteSelectionne.getLocation().getX())) == 1
                && (grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getOptionalUnit().get().getPlayer() == indexJoueurActif);
    }

    /**
     * effectue le ravitaillement de l'unite sur le curseur
     */
    public void Ravitaillement() {
        reinitialisationMunition(grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getOptionalUnit().get());//on reinitialise les munitions de l'unite
        grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getOptionalUnit().get().setFuel(grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getOptionalUnit().get().getFuelMax());
        //on reinitialise le carburant de l'unite
        finActivationUnitee();
    }

    /**
     * effectue les actions necessaire pour mettre fin a l'activation d'une unite
     */
    public void finActivationUnitee() {
        uniteSelectionne.setAvailable(false);//l'unite devient indisponible
        finActivation(uniteSelectionne);
        uniteSelectionne = null;//plus aucune unite n'est selectionnee
        stateOfTheGame = "SelectionUnite";//on remet le jeu en mode selection d'une unite
        display();//on affiche la carte actualisee
    }

    /**
     * effectue les actions necessaire pour commencer une attaque
     */
    public void debutattaque() {
        stateOfTheGame = "Selection Attaque";
        display();
    }

    /**
     *
     * @param uniteAttaquante une unite attaquant
     * @param uniteDefense une unite se defendant
     * realise l'attaque d'une unite sur l'autre et si il ya lieu d'en avoir la riposte de l'unitee attaquee
     */
    public void attaque(Unit uniteAttaquante, Unit uniteDefense) {
        uniteDefense.setLifePoints((int) (uniteDefense.getLifePoints() - Math.ceil((uniteAttaquante.getLifePoints() * meilleurArme(uniteAttaquante, uniteDefense)[0]) / 100.0)));
        //on reduit les pv de l'unite defensive
        uniteAttaquante.getWeaponery()[meilleurArme(uniteAttaquante, uniteDefense)[1]].setAmmunitions(uniteAttaquante.getWeaponery()[meilleurArme(uniteAttaquante, uniteDefense)[1]].getAmmunitions() - 1);
        //on reduit les munitions de l'arme utilisee de 1
        if (uniteDefense.getLifePoints() <= 0) {
            suppressionUnite(uniteDefense);
        } else if (uniteAPortee(uniteDefense, uniteAttaquante)//on verifie que l'unite en defense ait une arme a portee de l'unite attaquante
                && uniteAttaquante.getWeaponery()[meilleurArme(uniteAttaquante, uniteDefense)[1]].getMaxrange() == 1) {//et que l'attaque n'etait pas a distance
            uniteAttaquante.setLifePoints((int) (uniteAttaquante.getLifePoints() - Math.ceil((uniteDefense.getLifePoints() * meilleurArme(uniteDefense, uniteAttaquante)[0]) / 100.0)));
            //l'unite en defense replique si il lui reste des points de vie
            uniteDefense.getWeaponery()[meilleurArme(uniteDefense, uniteAttaquante)[1]].setAmmunitions(uniteDefense.getWeaponery()[meilleurArme(uniteDefense, uniteAttaquante)[1]].getAmmunitions() - 1);
            //ses munitions baissent
            if (uniteAttaquante.getLifePoints() <= 0) {
            }
        }
        finActivationUnitee();
    }

    /**
     *
     * @param unite l'unite a supprimer
     * supprime l'unite
     */
    public void suppressionUnite(Unit unite) {
        grid.getTile(unite.getLocation().getX(), unite.getLocation().getY()).setOptionalUnit(Optional.empty());
    }

    /**
     *
     * @param weapon une arme
     * @param distance la distance entre l'utilisateur de l'arme
     * @param efficacite l'efficacite maximale des autres armes de l'utilisateur de weapon
     * @param uniteDefense l'unite sur laquelle tire l'utilisateur de weapon
     * @return si weapon peut tirer sur uniteDefense et si elle est plus efficace que efficacite
     */
    public boolean peuAttaquerArmeDistance(Weapon weapon, int distance, int efficacite, Unit uniteDefense) {
        return (weapon.getMaxrange() <= distance
                && weapon.getMinrange() >= distance
                && efficacite < weapon.efficiencyWeapon(uniteDefense)
                && weapon.getAmmunitions() > 0);
        //on verifie que l'arme a la portee necessaire pour tirer sur l'unite defense
        //et que elle est la plus efficace des armes vues pour le moment et qu'elle possede encore des munitions
    }

    /**
     *
     * @param uniteAttaquante l'unite procedant a l'attaque
     * @param uniteDefense l'unite se defendant
     * @return le degat de la meilleur arme et le rang de l'arme dans cet ordre
     */
    public int[] meilleurArme(Unit uniteAttaquante, Unit uniteDefense) {
        int efficacite = 0;
        int rangArme = 0;
        int distance = Math.abs((uniteDefense.getLocation().getX() - uniteAttaquante.getLocation().getX()) + (uniteDefense.getLocation().getY() - uniteAttaquante.getLocation().getY()));
        for (int i = 0; i < uniteAttaquante.getWeaponery().length; i++) {
            if (peuAttaquerArmeDistance(uniteAttaquante.getWeaponery()[i], distance, efficacite, uniteDefense)) {
                efficacite = uniteAttaquante.getWeaponery()[i].efficiencyWeapon(uniteDefense);
                rangArme = i;
            }
        }
        int[] a = {efficacite, rangArme};
        return a;
    }

    /**
     * effectue les actions necessaire pour commencer une capture
     */
    public void capture() {
        int pointsdeCapture = grid.getTile(uniteSelectionne.getLocation().getX(), uniteSelectionne.getLocation().getY()).getBiome().getPC();
        pointsdeCapture -= uniteSelectionne.getLifePoints();//on soustrait aux points de capture
        //de la propriete les points de vie de l'unite la capturant
        if (pointsdeCapture <= 0) {//on regarde si la propriete a ete capturee
            Tile tile = grid.getTile(uniteSelectionne.getLocation().getX(), uniteSelectionne.getLocation().getY());
            tile.getBiome().setPC(10);
            //on reinitialise ses points de capture 
            tile.getBiome().setPlayer((byte) indexJoueurActif);
            if (indexJoueurActif == 1) player1.getProprietes().add(tile);
            else player2.getProprietes().add(tile);
            verificationFinPartie();//on verifie si la capture de la case amene a la fin de la partie
            //on change le proprietaire de la case
        } else {
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
    public boolean uniteAPortee(Unit uniteattaquante, Unit unitedefense) {
        boolean aPortee = false;
        int distance2 = Math.abs(unitedefense.getLocation().getX() - uniteattaquante.getLocation().getX()) + Math.abs(unitedefense.getLocation().getY() - uniteattaquante.getLocation().getY());
        //on clacule la distance separant les deux unites
        for (int i = 0; i < uniteattaquante.getWeaponery().length; i++) {
            if (uniteattaquante.getWeaponery()[i].getAmmunitions() > 0//on verifie que l'arme atoujours des munitions
                    && distance2 <= uniteattaquante.getWeaponery()[i].getMaxrange() && distance2 >= uniteattaquante.getWeaponery()[i].getMinrange()//et que l'arme a la bonne portee pour attaquer
                    && possibleAttaqueDistance(uniteattaquante, uniteattaquante.getWeaponery()[i])) {
                //on verifie que si l'unite attaque avec une arme a distance elle n'aie pas bouge ce tour ci
                aPortee = true;
                //TODO afficher la case en rouge transparant
            }
        }
        return aPortee;
    }

    /**
     * @param unite une unite attaquant avec une arme
     * @param weapon une arme attaquant
     * @return true si l'attaque est au corp a corp et sinon
     * renvoie si l'unite n'a pas bouge ce tour
     */
    public boolean possibleAttaqueDistance(Unit unite, Weapon weapon) {
        if (weapon.getMaxrange() > 1) {
            return unite.getMovementPoints() == unite.getMaxMouvementPoint();
        } else {
            return true;
        }
    }

    /**
     * modifie player1 et player2 pour representer leur situation au debut de la 
     * partie en fonction de la grille
     */
    public void debutJeu() {
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {//on parcourt toutes les cases de la grille
                Tile tile = grid.getTile(i, j);
                if (!tile.getOptionalUnit().isEmpty()) {//on regarde si il y a une unite sur la case
                    if (tile.getOptionalUnit().get().getPlayer() == 1) {
                        //si l'unite est au joueur 1 on l'ajoute a sa liste d'unite disponible
                        player1.getUnitesDisponibles().add(grid.getTile(i, j).getOptionalUnit().get());
                    } else {//sinon on l'ajoute a celle du joueur 2
                        player2.getUnitesDisponibles().add(grid.getTile(i, j).getOptionalUnit().get());
                    }
                }
                if (tile.getBiome().getBiomeType().isProperty()) {//on regarde si la case est une propriete
                    if (tile.getBiome().getBiomeType().isQG()) {//on regarde si la ces est un qg
                        listQG.add(tile);
                    }
                    if (tile.getBiome().getPlayer() == 1) {//on verifie si la propriete est au joueur 1
                        player1.getProprietes().add(grid.getTile(i, j));//on l'ajoute a la liste de propriete du joueur 1
                    } else if (tile.getBiome().getPlayer() == 2) {//on verifie si la propriete est au joueur 2
                        player2.getProprietes().add(tile);//on l'ajoute a la liste de propriete du joueur 1
                    }
                }
            }
        }
    }

    /**
     *
     *
     * @param unite une unite qui a fini son activation
     * change son statut de disponible a indisponible
     */
    public void finActivation(Unit unite) {
        if (unite.getPlayer() == 1) {
            player1.getUnitesDisponibles().remove(unite);
            player1.getUnitesNonDisponible().add(unite);
        } else {
            player2.getUnitesDisponibles().remove(unite);
            player2.getUnitesNonDisponible().add(unite);

        }
    }

    /**
     * on effectue les actions necessaire au debut de la phase de mouvement d'une unite
     */
    public void initialisationMouvement() {

        cheminParcouru.setAbscisse(curseur.getAbscisse());
        cheminParcouru.setOrdonnee(curseur.getOrdonnee());
        cheminParcouru.setPrecedent(null);
        cheminParcouru.setSuivant(null);//on initialise un nouveau chemin
        stateOfTheGame = "Mouvement";//on met le jeu dans le mode deplacement d'unite
    }

    /**
     *
     * @param indexPlayer le joueur actif
     * verifie qu'il reste des possibilitees d'action au joueur actif
     * et si ce n'est pas le cas passe le tour automatiquement
     */
    public void verificationFinTourAuto() {
        if ((indexJoueurActif == 1 && player1.getUnitesDisponibles().isEmpty())
                || (indexJoueurActif == 2 && player2.getUnitesDisponibles().isEmpty())) {
            //on verifie que le joueur actif ne peut plus actionner une unite
            changementTour();
        }
    }

    /**
     *
     * @return si l'unitee selectionnee peut etre deplacee a la fin du chemin
     */
    public boolean peutDeplacerUnite() {
        return stateOfTheGame.equals("Mouvement") //on verifie qu'on est en deplacement
                && (grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getOptionalUnit().isEmpty()  //et que la case est disponible
                || grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).getOptionalUnit().get().equals(uniteSelectionne)) //ou qu'elle soit occupee par l'unitee selectionnee
                && cheminParcouru.getAbscisse() == curseur.getAbscisse()//et que le curseur se situe la ou l'unite va se deplacer
                && cheminParcouru.getOrdonnee() == curseur.getOrdonnee();
    }

    /**
     * effectue le deplacement de l'unite et demande à l'utilisateur ce qu'il veut faire ensuite
     */
    public void deplacementEffectifUnite() {
        grid.getTile(cheminParcouru.coordonneesOrigineDuChemin()[0], cheminParcouru.coordonneesOrigineDuChemin()[1]).setOptionalUnit(Optional.empty());
        //on enleve l'unite de sa case d'origine
        grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee()).setOptionalUnit(Optional.ofNullable(uniteSelectionne));
        //on deplace l'unite vers sa destination
        uniteSelectionne.setLocation(new Location(curseur.getAbscisse(), curseur.getOrdonnee()));
        choixAction();
    }

    /**
     *
     * @param unitee
     * @return si l'unitee est disponible
     */
    public boolean isAvailable(Unit unitee) {
        switch (indexJoueurActif) {
            case 1:
                return (player1.getUnitesDisponibles().contains(unitee));
            default:
                return (player2.getUnitesDisponibles().contains(unitee));
        }
    }

    /**
     *
     * @return si le curseur est sur une usine pouvant produire des unites pour le joueur actif
     */
    public boolean UsineAvailable() {
        Tile tile = grid.getTile(curseur.getAbscisse(), curseur.getOrdonnee());
        //et qu'elle appartient au joueur actif
        return tile.getOptionalUnit().isEmpty()//on verifie qu'il n'y a pas d'unite sur la case
                && tile.getBiome().getBiomeType() == TileBiomeType.PROPERTY_FACTORY//que la case soit une usine
                && tile.getBiome().getPlayer() == indexJoueurActif;
    }

    /**
     * demande au joueur quelle unite il veut construire
     */
    public void creationUniteUsine() {
        String[] options = {"artillery", "bazooka", "Bombardier", "DCA", "Lance-missile Sol-Air", "helicopter", "infantry", "convoi", "tank"};
        switch (Affichage.popup("creation unite?", options, true, 1)) {
            case 0:
                if (peutConstruireUnite(6000)) {//on verifie que le joueur a asser de credit pour creer l'unite selectionnee
                    creationUnite(UnitType.ARTILLERY.getRawName());//on cree l'unite voulue
                } else {
                    creationUniteUsine();//si la creation de cette unite n'est pas possible alors on redemande au joueur quelle unite il veut creer
                }
                ;
            case 1:
                if (peutConstruireUnite(3500)) {
                    creationUnite(UnitType.BAZOOKA.getRawName());
                } else {
                    creationUniteUsine();
                }
                ;
                break;
            case 2:
                if (peutConstruireUnite(20000)) {
                    creationUnite(UnitType.BOMBER.getRawName());
                } else {
                    creationUniteUsine();
                }
                ;
                break;
            case 3:
                if (peutConstruireUnite(6000)) {
                    creationUnite(UnitType.DAA.getRawName());
                } else {
                    creationUniteUsine();
                }
                ;
                break;
            case 4:
                if (peutConstruireUnite(12000)) {
                    creationUnite(UnitType.GROUND_AIR_ML.getRawName());
                } else {
                    creationUniteUsine();
                }
                ;
                break;
            case 5:
                if (peutConstruireUnite(12000)) {
                    creationUnite(UnitType.HELICOPTER.getRawName());
                } else {
                    creationUniteUsine();
                }
                ;
                break;
            case 6:
                if (peutConstruireUnite(1500)) {
                    creationUnite(UnitType.INFANTRY.getRawName());
                } else {
                    creationUniteUsine();
                }
                ;
                break;
            case 7:
                if (peutConstruireUnite(4000)) {
                    creationUnite(UnitType.CONVOY.getRawName());
                } else {
                    creationUniteUsine();
                }
                ;
                break;
            default:
                if (peutConstruireUnite(6000)) {
                    creationUnite(UnitType.TANK.getRawName());
                } else {
                    creationUniteUsine();
                }
                ;
        }
        display();
    }

    /**
     *
     * @param credit les credits necessaires a construire une unite donnee
     * @return si le joueur actif a asser de credits pour acheter l'unite
     */
    public boolean peutConstruireUnite(int credit) {
        if (indexJoueurActif == 1 && player1.getCredit() >= credit) {
            return true;
        } else if (indexJoueurActif == 2 && player2.getCredit() >= credit) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param typeUniteNom le nom du type de l'unite a cree
     * cree l'unite voulue du joueur actif sur la case ou se situe le curseur
     * elle doit etre deja activee
     */
    public void creationUnite(String typeUniteNom) {
        Unit unit = UnitType.instantiateUnit(typeUniteNom, new Location(curseur.getAbscisse(), curseur.getOrdonnee()),
                (byte) this.indexJoueurActif, false);
        this.grid.addUnit(unit);
        if (this.indexJoueurActif == 1) addUnitForPlayer(this.player1, unit);
        else addUnitForPlayer(this.player2, unit);
    }

    /**
     * Ajoute une unite a un joueur
     * @param player joueur vise
     * @param unit unite a ajouter
     */
    private void addUnitForPlayer(Player player, Unit unit) {
        player.getUnitesNonDisponible().add(unit);
    }

    /**
     * on regarde si la partie est finie et si c'est le cas on la finit
     */
    public void verificationFinPartie() {
        if (!listQG.isEmpty() && QGTousJoueurActif()) {
            stateOfTheGame = "Fin de la Partie";
            gameIsOver = true;
        }
    }

    /**
     *
     * @return si tous les QG appartiennent au joueur actif
     */
    public boolean QGTousJoueurActif() {
        boolean QGTousJoueurActif = true;
        for (int i = 0; i < listQG.size(); i++) {
            if (!(listQG.get(i).getBiome().getPlayer() == indexJoueurActif)) {//si un QG n'appartient aps au joueur actif on renvoie false
                QGTousJoueurActif = false;
            }
        }
        return QGTousJoueurActif;
    }
}
