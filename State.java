package code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class State {

	public int[] neoXY;
	public int neoH;
	public List<int[]> remainingConvertedAgents;
	public int killedAgents;
	public List<int[]> remainingPills;
	public List<int[]> remainingHostages;
	public List<int[]> carriedHostages;
	public int deadHostages;
	public List<Action> availableActions = new ArrayList<Action>();
	public List<int[]> remainingNormalAgents;
	public Grid grid;
	public String leadingActions;
	public int availablePill;
	public int[] availablePad;
	public LinkedList<Integer> cagentsToKill = new LinkedList<Integer>();
	public LinkedList<Integer> nagentsToKill = new LinkedList<Integer>();
	public int level = 0;
	

	boolean hostageChecker = false;

	public State(String leadingActions, Grid grid, int[] neoXY, int neoH, List<int[]> remainingConvertedAgents,
			int killedAgents, List<int[]> remainingPills, List<int[]> remainingHostages, List<int[]> carriedHostages,
			int deadHostages, List<int[]> remainingNormalAgents) {
		this.grid = grid;
		this.neoXY = neoXY;
		this.neoH = neoH;
		this.remainingConvertedAgents = remainingConvertedAgents;
		this.killedAgents = killedAgents;
		this.remainingPills = remainingPills;
		this.remainingHostages = remainingHostages;
		this.carriedHostages = carriedHostages;
		this.deadHostages = deadHostages;
		this.remainingNormalAgents = remainingNormalAgents;
		this.leadingActions = leadingActions;

		computeAvailableActions();
		if (this.gameOver())
			this.availableActions.clear();
	}

	public List<Action> getAvailableActions() {
		return availableActions;
	}

	private void computeAvailableActions() {
		if (checkLeft())
			this.availableActions.add(Action.LEFT);
		if (checkRight())
			this.availableActions.add(Action.RIGHT);
		if (checkUp())
			this.availableActions.add(Action.UP);
		if (checkDown())
			this.availableActions.add(Action.DOWN);
		if (checkCarry()) {
			this.availableActions.add(Action.CARRY);
		} else {
			if (checkTakePill())
				this.availableActions.add(Action.TAKE_PILL);
			else {
				if (checkCarry())
					this.availableActions.add(Action.CARRY);
				else {
					if (checkDrop())
						this.availableActions.add(Action.DROP);
					else if (checkFly())
						this.availableActions.add(Action.FLY);
				}
			}
		}

		if (!hostageChecker) {
			if (checkKill())
				this.availableActions.add(Action.KILL);
		} else {
			hostageChecker = false;
		}
	}

	public State nextState(Action action) {
		if (!this.availableActions.contains(action))
			return null;

		switch (action) {
		case KILL:
			return this.kill();
		case CARRY:
			return this.carry();
		case TAKE_PILL:
			return this.takePill();
		case DROP:
			return this.drop();
		case FLY:
			return this.fly();
		case DOWN:
			return this.down();
		case UP:
			return this.up();
		case LEFT:
			return this.left();
		case RIGHT:
			return this.right();
		default:
			return null;
		}

	}

	private State carry() {

		List<int[]> newRemainingConvertedAgents = new ArrayList<int[]>(this.remainingConvertedAgents);
		List<int[]> newRemainingHostages = new ArrayList<int[]>();
		int newDeadHostages = this.deadHostages;
		List<int[]> newCarriedHostages = new ArrayList<int[]>();

		for (int[] h : this.remainingHostages) {
			int[] hostage = { h[0], h[1], h[2] };
			if (this.neoXY[0] == h[0] && this.neoXY[1] == h[1]) {
				hostage[2] +=2;
				newCarriedHostages.add(hostage);
				if (h[2] > 97)
					hostageChecker = true;
			} else {
				if (h[2] < 98) {
					hostage[2] += 2;
					newRemainingHostages.add(hostage);
				} else {
					newDeadHostages += 1;
					int[] newAgent = { h[0], h[1] };
					newRemainingConvertedAgents.add(newAgent);
				}
			}
		}

		for (int[] h : this.carriedHostages) {
			int[] hostage = { h[0], h[1], h[2] };
			if (hostage[2] < 98)
				hostage[2] += 2;
			else {
				if(hostage[2]<200) {
					hostage[2] += 200;
					newDeadHostages += 1;
				}
			}		
			newCarriedHostages.add(hostage);
		}
		
		int[] newStateNeo = { this.neoXY[0], this.neoXY[1] };

		State newState = new State(this.leadingActions + ",carry", this.grid, newStateNeo, this.neoH,
				newRemainingConvertedAgents, this.killedAgents, new ArrayList<int[]>(this.remainingPills),
				newRemainingHostages, newCarriedHostages, newDeadHostages,
				new ArrayList<int[]>(this.remainingNormalAgents));
		return newState;
	}

	private State kill() {

		List<int[]> newRemainingConvertedAgents = new ArrayList<int[]>(this.remainingConvertedAgents);
		int newKilledAgents = this.killedAgents;
		List<int[]> newRemainingNormalAgents = new ArrayList<int[]>(this.remainingNormalAgents);
		List<int[]> newRemainingHostages = new ArrayList<int[]>();
		int newDeadHostages = this.deadHostages;
		List<int[]> newCarriedHostages = new ArrayList<int[]>();
		
		LinkedList<Integer> tempCagents = new LinkedList<Integer>(this.cagentsToKill);
		LinkedList<Integer> tempNagents = new LinkedList<Integer>(this.nagentsToKill);

		newKilledAgents += tempCagents.size();
		int count = 0;
		while (!tempCagents.isEmpty()) {
			newRemainingConvertedAgents.remove((int) tempCagents.poll() - count);
			count++;
		}

		count = 0;
		newKilledAgents += tempNagents.size();
		while (!tempNagents.isEmpty()) {
			newRemainingNormalAgents.remove((int) tempNagents.poll() - count);
			count++;
		}

		for (int[] h : this.remainingHostages) {
			if (h[2] < 98) {
				int[] hostage = { h[0], h[1], h[2] };
				hostage[2] += 2;
				newRemainingHostages.add(hostage);
			} else {
				newDeadHostages += 1;
				int[] newAgent = { h[0], h[1] };
				newRemainingConvertedAgents.add(newAgent);
			}
		}

		for (int[] h : this.carriedHostages) {
			int[] hostage = { h[0], h[1], h[2] };
			if (hostage[2] < 98)
				hostage[2] += 2;
			else {
				if(hostage[2]<200) {
					hostage[2] += 200;
					newDeadHostages += 1;
				}
			}		
			newCarriedHostages.add(hostage);
		}
		
		

		int newNeoH = this.neoH + 20;

		State newState = new State(this.leadingActions + ",kill", this.grid, new int[] { this.neoXY[0], this.neoXY[1] },
				newNeoH, newRemainingConvertedAgents, newKilledAgents, new ArrayList<int[]>(this.remainingPills),
				newRemainingHostages, newCarriedHostages, newDeadHostages, newRemainingNormalAgents);
		return newState;
	}

	private State takePill() {
		int newDeadHostages = this.deadHostages;
		List<int[]> newRemainingHostages = new ArrayList<int[]>();
		List<int[]> newRemainingPills = new ArrayList<int[]>(this.remainingPills);
		List<int[]> newRemainingConvertedAgents = new ArrayList<int[]>(this.remainingConvertedAgents);
		List<int[]> newCarriedHostages = new ArrayList<int[]>();
		int newNeoH = this.neoH;

		if (newNeoH < 20)
			newNeoH = 0;
		else
			newNeoH -= 20;

		newRemainingPills.remove(this.availablePill);

		for (int[] h : this.remainingHostages) {
			if (h[2] < 100) {
				int[] hostage = { h[0], h[1], h[2] };
				if (hostage[2] < 20)
					hostage[2] = 0;
				else
					hostage[2] -= 20;
				newRemainingHostages.add(hostage);
			} else {
				newDeadHostages += 1;
				int[] newAgent = { h[0], h[1] };
				newRemainingConvertedAgents.add(newAgent);
			}
		}

		for (int[] h : this.carriedHostages) {
			int[] hostage = { h[0], h[1], h[2] };
			if (hostage[2] < 100) {
				if (hostage[2] < 20)
					hostage[2] = 0;
				else
					hostage[2] -= 20;
			}
			else {
				if(hostage[2]<200) {
					hostage[2] += 200;
					newDeadHostages += 1;
				}
			}		
			newCarriedHostages.add(hostage);
		}

		State newState = new State(this.leadingActions + ",takePill", this.grid,
				new int[] { this.neoXY[0], this.neoXY[1] }, newNeoH, newRemainingConvertedAgents, this.killedAgents,
				newRemainingPills, newRemainingHostages, newCarriedHostages, newDeadHostages,
				new ArrayList<int[]>(this.remainingNormalAgents));
		return newState;
	}

	private State drop() {
		int newDeadHostages = this.deadHostages;
		List<int[]> newRemainingHostages = new ArrayList<int[]>();
		List<int[]> newRemainingConvertedAgents = new ArrayList<int[]>(this.remainingConvertedAgents);

		for (int[] h : this.remainingHostages) {
			if (h[2] < 98) {
				int[] hostage = { h[0], h[1], h[2] };
				hostage[2] += 2;
				newRemainingHostages.add(hostage);
			} else {
				newDeadHostages += 1;
				int[] newAgent = { h[0], h[1] };
				newRemainingConvertedAgents.add(newAgent);
			}
		}
				
		State newState = new State(this.leadingActions + ",drop", this.grid, new int[] { this.neoXY[0], this.neoXY[1] },
				this.neoH, newRemainingConvertedAgents, this.killedAgents, new ArrayList<int[]>(this.remainingPills),
				newRemainingHostages, new ArrayList<int[]>(), newDeadHostages,
				new ArrayList<int[]>(this.remainingNormalAgents));
		return newState;
	}

	private State fly() {
		
		int newDeadHostages = this.deadHostages;
		List<int[]> newRemainingHostages = new ArrayList<int[]>();
		List<int[]> newRemainingConvertedAgents = new ArrayList<int[]>(this.remainingConvertedAgents);
		List<int[]> newCarriedHostages = new ArrayList<int[]>();
		int[] newNeoXY = { this.neoXY[0], this.neoXY[1] };

		for (int[] h : this.remainingHostages) {
			if (h[2] < 98) {
				int[] hostage = { h[0], h[1], h[2] };
				hostage[2] += 2;
				newRemainingHostages.add(hostage);
			} else {
				newDeadHostages += 1;
				int[] newAgent = { h[0], h[1] };
				newRemainingConvertedAgents.add(newAgent);
			}
		}

		for (int[] h : this.carriedHostages) {
			int[] hostage = { h[0], h[1], h[2] };
			if (hostage[2] < 98)
				hostage[2] += 2;
			else {
				if(hostage[2]<200) {
					hostage[2] += 200;
					newDeadHostages += 1;
				}
			}		
			newCarriedHostages.add(hostage);
		}

		int[] startPadXY = new int[] { this.availablePad[0], this.availablePad[1] };
		int[] endPadXY = new int[] { this.availablePad[2], this.availablePad[3] };
		if (this.neoXY[0] == startPadXY[0] && this.neoXY[1] == startPadXY[1]) {
			newNeoXY[0] = endPadXY[0];
			newNeoXY[1] = endPadXY[1];
		} else {
			newNeoXY[0] = startPadXY[0];
			newNeoXY[1] = startPadXY[1];
		}

		State newState = new State(this.leadingActions + ",fly", this.grid, newNeoXY, this.neoH,
				newRemainingConvertedAgents, this.killedAgents, new ArrayList<int[]>(this.remainingPills),
				newRemainingHostages, newCarriedHostages, newDeadHostages,
				new ArrayList<int[]>(this.remainingNormalAgents));
		return newState;
	}

	private State down() {
		List<int[]> newRemainingHostages = new ArrayList<int[]>();
		int newDeadHostages = this.deadHostages;
		List<int[]> newRemainingConvertedAgents;
		List<int[]> newCarriedHostages = new ArrayList<int[]>();
		newRemainingConvertedAgents = new ArrayList<int[]>(this.remainingConvertedAgents);

		int[] newNeoXY = new int[2];
		newNeoXY[0] = this.neoXY[0] + 1;
		newNeoXY[1] = this.neoXY[1];
		
		for (int[] h : this.remainingHostages) {
			if (h[2] < 98) {
				int[] hostage = { h[0], h[1], h[2] };
				hostage[2] += 2;
				newRemainingHostages.add(hostage);
			} else {
				newDeadHostages += 1;
				int[] newAgent = { h[0], h[1] };
				newRemainingConvertedAgents.add(newAgent);
			}
		}

		for (int[] h : this.carriedHostages) {
			int[] hostage = { h[0], h[1], h[2] };
			if (hostage[2] < 98)
				hostage[2] += 2;
			else {
				if(hostage[2]<200) {
					hostage[2] += 200;
					newDeadHostages += 1;
				}
			}		
			newCarriedHostages.add(hostage);
		}

		State newState = new State(this.leadingActions + ",down", this.grid, newNeoXY, this.neoH,
				new ArrayList<int[]>(newRemainingConvertedAgents), this.killedAgents,
				new ArrayList<int[]>(this.remainingPills), new ArrayList<int[]>(newRemainingHostages),
				new ArrayList<int[]>(newCarriedHostages), newDeadHostages,
				new ArrayList<int[]>(this.remainingNormalAgents));
		return newState;
	}

	private State up() {
		List<int[]> newRemainingHostages = new ArrayList<int[]>();
		int newDeadHostages = this.deadHostages;
		List<int[]> newRemainingConvertedAgents;
		List<int[]> newCarriedHostages = new ArrayList<int[]>();
		newRemainingConvertedAgents = new ArrayList<int[]>(this.remainingConvertedAgents);
		
		int[] newNeoXY = new int[2];
		newNeoXY[0] = this.neoXY[0] - 1;
		newNeoXY[1] = this.neoXY[1];
		
		for (int[] h : this.remainingHostages) {
			if (h[2] < 98) {
				int[] hostage = { h[0], h[1], h[2] };
				hostage[2] += 2;
				newRemainingHostages.add(hostage);
			} else {
				newDeadHostages += 1;
				int[] newAgent = { h[0], h[1] };
				newRemainingConvertedAgents.add(newAgent);
			}
		}

		for (int[] h : this.carriedHostages) {
			int[] hostage = { h[0], h[1], h[2] };
			if (hostage[2] < 98)
				hostage[2] += 2;
			else {
				if(hostage[2]<200) {
					hostage[2] += 200;
					newDeadHostages += 1;
				}
			}		
			newCarriedHostages.add(hostage);
		}

		State newState = new State(this.leadingActions + ",up", this.grid, newNeoXY, this.neoH,
				new ArrayList<int[]>(newRemainingConvertedAgents), this.killedAgents,
				new ArrayList<int[]>(this.remainingPills), new ArrayList<int[]>(newRemainingHostages),
				new ArrayList<int[]>(newCarriedHostages), newDeadHostages,
				new ArrayList<int[]>(this.remainingNormalAgents));
		return newState;
	}

	private State left() {
		List<int[]> newRemainingHostages = new ArrayList<int[]>();
		int newDeadHostages = this.deadHostages;
		List<int[]> newRemainingConvertedAgents;
		List<int[]> newCarriedHostages = new ArrayList<int[]>();
		newRemainingConvertedAgents = new ArrayList<int[]>(this.remainingConvertedAgents);
		
		for (int[] h : this.remainingHostages) {
			if (h[2] < 98) {
				int[] hostage = { h[0], h[1], h[2] };
				hostage[2] += 2;
				newRemainingHostages.add(hostage);
			} else {
				newDeadHostages += 1;
				int[] newAgent = { h[0], h[1] };
				newRemainingConvertedAgents.add(newAgent);
			}
		}

		for (int[] h : this.carriedHostages) {
			int[] hostage = { h[0], h[1], h[2] };
			if (hostage[2] < 98)
				hostage[2] += 2;
			else {
				if(hostage[2]<200) {
					hostage[2] += 200;
					newDeadHostages += 1;
				}
			}		
			newCarriedHostages.add(hostage);
		}
		
		int[] newNeoXY = new int[2];
		newNeoXY[0] = this.neoXY[0];
		newNeoXY[1] = this.neoXY[1] - 1;

		State newState = new State(this.leadingActions + ",left", this.grid, newNeoXY, this.neoH,
				new ArrayList<int[]>(newRemainingConvertedAgents), this.killedAgents,
				new ArrayList<int[]>(this.remainingPills), new ArrayList<int[]>(newRemainingHostages),
				new ArrayList<int[]>(newCarriedHostages), newDeadHostages,
				new ArrayList<int[]>(this.remainingNormalAgents));
		return newState;
	}

	private State right() {
		List<int[]> newRemainingHostages = new ArrayList<int[]>();
		int newDeadHostages = this.deadHostages;
		List<int[]> newRemainingConvertedAgents;
		List<int[]> newCarriedHostages = new ArrayList<int[]>();
		newRemainingConvertedAgents = new ArrayList<int[]>(this.remainingConvertedAgents);
		
		int[] newNeoXY = new int[2];
		newNeoXY[0] = this.neoXY[0];
		newNeoXY[1] = this.neoXY[1] + 1;
		
		for (int[] h : this.remainingHostages) {
			if (h[2] < 98) {
				int[] hostage = { h[0], h[1], h[2] };
				hostage[2] += 2;
				newRemainingHostages.add(hostage);
			} else {
				newDeadHostages += 1;
				int[] newAgent = { h[0], h[1] };
				newRemainingConvertedAgents.add(newAgent);
			}
		}

		for (int[] h : this.carriedHostages) {
			int[] hostage = { h[0], h[1], h[2] };
			if (hostage[2] < 98)
				hostage[2] += 2;
			else {
				if(hostage[2]<200) {
					hostage[2] += 200;
					newDeadHostages += 1;
				}
			}		
			newCarriedHostages.add(hostage);
		}
		

		State newState = new State(this.leadingActions + ",right", this.grid, newNeoXY, this.neoH,
				new ArrayList<int[]>(newRemainingConvertedAgents), this.killedAgents,
				new ArrayList<int[]>(this.remainingPills), new ArrayList<int[]>(newRemainingHostages),
				new ArrayList<int[]>(newCarriedHostages), newDeadHostages,
				new ArrayList<int[]>(this.remainingNormalAgents));
		return newState;
	}

	private boolean checkTakePill() {
		if(leadingActions.endsWith("fly") || leadingActions.endsWith("carry") || leadingActions.endsWith("drop") || leadingActions.endsWith("Pill") ) {
			return false;
		}
		
		for (int i = 0; i < this.remainingPills.size(); i++) {
			int[] p = this.remainingPills.get(i);
			if (this.neoXY[0] == p[0] && this.neoXY[1] == p[1]) {
				this.availablePill = i;
				return true;
			}
		}
		return false;
	}

	private boolean checkKill() {
		this.cagentsToKill.clear();
		this.nagentsToKill.clear();
		if(leadingActions.endsWith("kill")) {
			return false;
		}
		
		if (this.neoH > 79)
			return false;

		for (int i = 0; i < this.remainingConvertedAgents.size(); i++) {
			int[] a = this.remainingConvertedAgents.get(i);
			int distanceX = Math.abs(a[0] - this.neoXY[0]);
			int distanceY = Math.abs(a[1] - this.neoXY[1]);
			if ((distanceX == 1 && distanceY == 0) || (distanceX == 0 && distanceY == 1))
				this.cagentsToKill.add(i);
		}

		for (int i = 0; i < this.remainingNormalAgents.size(); i++) {
			int[] a = this.remainingNormalAgents.get(i);
			int distanceX = Math.abs(a[0] - this.neoXY[0]);
			int distanceY = Math.abs(a[1] - this.neoXY[1]);
			if ((distanceX == 1 && distanceY == 0) || (distanceX == 0 && distanceY == 1))
				this.nagentsToKill.add(i);
		}

		if (this.nagentsToKill.size() > 0 || this.cagentsToKill.size() > 0)
			return true;
		return false;
	}

	private boolean checkDrop() {
		if(leadingActions.endsWith("fly") || leadingActions.endsWith("carry") || leadingActions.endsWith("drop") || leadingActions.endsWith("Pill") ) {
			return false;
		}
		
		int[] telephoneXY = grid.getTelephoneXY();

		if (this.neoXY[0] == telephoneXY[0] && this.neoXY[1] == telephoneXY[1] && this.carriedHostages.size() > 0)
			return true;

		return false;
	}

	private boolean checkCarry() {		
		if(leadingActions.endsWith("fly") || leadingActions.endsWith("carry") || leadingActions.endsWith("drop") || leadingActions.endsWith("Pill") ) {
			return false;
		}		
		
		for (int[] h : this.remainingHostages) {
			int[] hostageXY = new int[] { h[0], h[1] };
			if (this.neoXY[0] == hostageXY[0] && this.neoXY[1] == hostageXY[1] && h[2] < 98
					&& this.grid.getCarryMax() > this.carriedHostages.size())
				return true;
		}
		return false;
	}

	private boolean checkFly() {
		
		if(leadingActions.endsWith("fly") || leadingActions.endsWith("carry") || leadingActions.endsWith("drop") || leadingActions.endsWith("Pill") ) {
			return false;
		}
		
		int[][] pads = this.grid.getPads();

		for (int[] p : pads) {
			if (this.neoXY[0] == p[0] && this.neoXY[1] == p[1] || this.neoXY[0] == p[2] && this.neoXY[1] == p[3]) {
				this.availablePad = p;
				return true;
			}
		}
		return false;
	}

	private boolean checkDown() {
		if(leadingActions.endsWith("up")) {
			return false;
		}
		
		if (this.neoXY[0] == grid.getGridSize()[0] - 1)
			return false;

		for (int[] a : this.remainingConvertedAgents) {
			int distanceX = a[0] - this.neoXY[0];
			if (distanceX == 1 && a[1] == this.neoXY[1])
				return false;
		}

		for (int[] a : this.remainingNormalAgents) {
			int distanceX = a[0] - this.neoXY[0];
			if (distanceX == 1 && a[1] == this.neoXY[1])
				return false;
		}
		return true;
	}

	private boolean checkRight() {
		if(leadingActions.endsWith("left")) {
			return false;
		}
		
		if (this.neoXY[1] == grid.getGridSize()[1] - 1)
			return false;

		for (int[] a : this.remainingConvertedAgents) {
			int distanceY = a[1] - this.neoXY[1];
			if (distanceY == 1 && a[0] == this.neoXY[0])
				return false;
		}

		for (int[] a : this.remainingNormalAgents) {
			int distanceY = a[1] - this.neoXY[1];
			if (distanceY == 1 && a[0] == this.neoXY[0])
				return false;
		}

		return true;
	}

	public boolean checkLeft() {
		if(leadingActions.endsWith("right")) {
			return false;
		}
		
		if (this.neoXY[1] == 0)
			return false;

		for (int[] a : this.remainingConvertedAgents) {
			int distanceY = this.neoXY[1] - a[1];
			if (distanceY == 1 && a[0] == this.neoXY[0])
				return false;
		}

		for (int[] a : this.remainingNormalAgents) {
			int distanceY = this.neoXY[1] - a[1];
			if (distanceY == 1 && a[0] == this.neoXY[0])
				return false;
		}

		return true;
	}

	private boolean checkUp() {
		if(leadingActions.endsWith("down")) {
			return false;
		}
		
		if (this.neoXY[0] == 0)
			return false;

		for (int[] a : this.remainingConvertedAgents) {
			int distanceX = this.neoXY[0] - a[0];
			if (distanceX == 1 && a[1] == this.neoXY[1])
				return false;
		}

		for (int[] a : this.remainingNormalAgents) {
			int distanceX = this.neoXY[0] - a[0];
			if (distanceX == 1 && a[1] == this.neoXY[1])
				return false;
		}

		return true;
	}

	public boolean isGoalState() {
		int[] telephoneXY = this.grid.getTelephoneXY();
		if (this.neoXY[0] == telephoneXY[0] && this.neoXY[1] == telephoneXY[1]
				&& this.remainingConvertedAgents.size() == 0 && this.remainingHostages.size() == 0
				&& this.carriedHostages.size() == 0)
			return true;

		return false;
	}

	public boolean gameOver() {
		if (this.neoH > 99)
			return true;
		return false;
	}

	public String toString() {// overriding the toString() method
		return neoXY[0] + "," + neoXY[1] + "," + this.neoH + ";" + printRemainingConvertedAgents() + ";"
				+ printRemainingNormalAgents() + ";" + printRemainingPills() + ";" + killedAgents + ";"
				+ printCarriedHostages() + ";" + printRemainingHostages() + ";" + deadHostages;
	}

	public String printRemainingHostages() {
		String s = "";
		for (int[] h : this.remainingHostages) {
			s += h[0] + "," + h[1] + ",";
		}
		return s;
	}

	public String printCarriedHostages() {
		String s = "";
		for (int[] h : this.carriedHostages) {
			s += h[0] + "," + h[1] + ",";
		}
		return s;
	}

	public String printRemainingConvertedAgents() {
		String s = "";
		for (int[] a : this.remainingConvertedAgents) {
			s += a[0] + "," + a[1] + ",";
		}
		return s;
	}

	public String printRemainingNormalAgents() {
		String s = "";
		for (int[] a : this.remainingNormalAgents) {
			s += a[0] + "," + a[1] + ",";
		}
		return s;
	}

	public String printRemainingPills() {
		String s = "";
		for (int[] p : this.remainingPills) {
			s += p[0] + "," + p[1] + ",";
		}
		return s;
	}
	
	 public static String sortString(String inputString)
	    {
	        // Converting input string to character array
	        char tempArray[] = inputString.toCharArray();
	 
	        // Sorting temp array using
	        Arrays.sort(tempArray);
	 
	        // Returning new sorted string
	        return new String(tempArray);
	    }
	 
	 public List<Object> pathCost() {
		 List<Object> cost = new ArrayList<Object>();
		 cost.add(this.deadHostages);
		 cost.add(this.killedAgents);
		 String[] depth = this.leadingActions.split(",");
		 cost.add(depth.length);
		 
		 return cost;
	 }
}
