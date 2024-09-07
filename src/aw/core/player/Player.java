package aw.core.player;

import java.util.List;

import aw.common.unit.Unit;
import aw.core.game.grid.Tile;

public class Player {
	private int playerNumber;
	private List<Unit> unitesDisponibles;
	private List<Unit> unitesNonDisponible;
	private List<Tile> proprietes;
	private int credit;
	public Player(int playerNumber, List<Unit> unitesDisponibles, List<Unit> unitesNonDisponible, List<Tile> proprietes, int credit) {
		this.playerNumber = playerNumber;
		this.unitesDisponibles = unitesDisponibles;
		this.unitesNonDisponible = unitesNonDisponible;
		this.proprietes = proprietes;
		this.credit = credit;
	}
	public int getCredit() {
		return credit;
	}
	public void setCredit(int credit) {
		this.credit = credit;
	}
	public int getPlayerNumber() {
		return playerNumber;
	}
	public void setPlayerNumber(int playerNumber) {
		this.playerNumber = playerNumber;
	}
	public List<Unit> getUnitesDisponibles() {
		return unitesDisponibles;
	}
	public void setUnitesDisponibles(List<Unit> unitesDisponibles) {
		this.unitesDisponibles = unitesDisponibles;
	}
	public List<Unit> getUnitesNonDisponible() {
		return unitesNonDisponible;
	}
	public void setUnitesNonDisponible(List<Unit> unitesNonDisponible) {
		this.unitesNonDisponible = unitesNonDisponible;
	}
	public List<Tile> getProprietes() {
		return proprietes;
	}
	public void setProprietes(List<Tile> proprietes) {
		this.proprietes = proprietes;
	}
	
}
