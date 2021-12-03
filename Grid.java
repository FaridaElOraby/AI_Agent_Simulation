package code;

public class Grid {

	private int[] gridSize;
	private int[] telephoneXY;
	private int carryMax;
	private int[][] pads;
	
	public Grid(int[] telephoneXY, int carryMax, int[] gridSize, int[][] pads) {
		this.gridSize = gridSize;
		this.telephoneXY = telephoneXY;
		this.carryMax = carryMax;
		this.pads = pads;
	}

	public int[] getGridSize() {
		return gridSize;
	}

	public int[] getTelephoneXY() {
		return telephoneXY;
	}

	public int getCarryMax() {
		return carryMax;
	}

	public int[][] getPads() {
		return pads;
	}
}
