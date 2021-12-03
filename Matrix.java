package code;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Matrix extends GSP {
	private static LinkedList<State> queue = new LinkedList<State>();
	private static HashSet<String> visitedStates = new HashSet<String>();
	static Stack<State> stk = new Stack<>();
	private static Grid searchGrid;
	private static int expandedNodes;

	public boolean exceededDepth = false;

	public Matrix(State initialState, Action[] actions) {
		this.initialState = initialState;
		this.actions = actions;
	}

	public static String sortString(String inputString) {
		// Converting input string to character array
		char tempArray[] = inputString.toCharArray();

		// Sorting temp array using
		Arrays.sort(tempArray);

		// Returning new sorted string
		return new String(tempArray);
	}

	private static int[] stringToInt(String array) {
		String[] arr = array.split(",");
		int[] intArr = new int[arr.length];

		for (int i = 0; i < intArr.length; i++) {
			intArr[i] = Integer.parseInt(arr[i]);
		}
		return intArr;
	}

	private static boolean visited(State s) {

		if (visitedStates.contains(s.toString()))
			return true;
		return false;
	}

	private static int[][] stringToInt(String array, int splits) {
		String[] arr = array.split(",");
		int[][] intArr = new int[arr.length / splits][splits];
		int count = 0;

		for (int i = 0; i < intArr.length; i++) {
			for (int j = 0; j < intArr[i].length; j++) {
				intArr[i][j] = Integer.parseInt(arr[count]);
				count++;
			}
		}

		return intArr;
	}

	private static List<int[]> array2DToList(int[][] array2D) {
		List<int[]> x = new ArrayList<int[]>();

		for (int[] array : array2D) {
			x.add(array);
		}

		return x;
	}

	public static String solve(String grid, String strategy, boolean visualize) {

		String[] gridArray = grid.split(";", 8);

		int[] gridSize = stringToInt(gridArray[0]);
		int carryMax = Integer.parseInt(gridArray[1]);
		int[] neoXY = stringToInt(gridArray[2]);
		int[] telephoneXY = stringToInt(gridArray[3]);
		int[][] agents = stringToInt(gridArray[4], 2);
		int[][] pills = stringToInt(gridArray[5], 2);
		int[][] pads = stringToInt(gridArray[6], 4);
		int[][] hostages = stringToInt(gridArray[7], 3);

		searchType search = searchType.valueOf(strategy);
		searchGrid = new Grid(telephoneXY, carryMax, gridSize, pads);
		State initialState = new State("", searchGrid, neoXY, 0, new ArrayList<int[]>(), 0, array2DToList(pills),
				array2DToList(hostages), new ArrayList<int[]>(), 0, array2DToList(agents));
		Action[] actionList = { Action.LEFT, Action.RIGHT, Action.UP, Action.DOWN, Action.CARRY, Action.DROP,
				Action.FLY, Action.KILL, Action.TAKE_PILL };
		Matrix problem = new Matrix(initialState, actionList);
		State goal = problem.generalSearch(problem, search, visualize);

		if (goal == null)
			return "No Solution";
		else {
			String goalString = goal.leadingActions.substring(1) + ";" + goal.deadHostages + ";" + goal.killedAgents
					+ ";" + expandedNodes;
			return goalString;
		}
	}

	private List<Object> BF(State state, boolean b) {
		queue.clear();
		visitedStates.clear();
		int count = 1;
		for (Action action : state.getAvailableActions()) {
			State next = state.nextState(action);
			queue.add(next);
		}

		while (!queue.isEmpty()) {
			State s = queue.poll();
			if (goalTest(s)) {
				List<Object> goal = new ArrayList<Object>();
				goal.add(s);
				goal.add(count);
				return goal;
			}
			if (!visited(s)) {
				count++;
				visitedStates.add(s.toString());
				List<Action> availableActions = s.availableActions;
				for (Action action : availableActions) {
					State next = s.nextState(action);
					if (!visited(next))
						queue.add(next);
				}
			}
		}
		return null;
	}

	private List<Object> UC(State state, boolean b) {
		queue.clear();
		visitedStates.clear();
		int count = 1;
		for (Action action : state.getAvailableActions()) {
			State next = state.nextState(action);
			int index = getIndexActual(queue, next);
			queue.add(index, next);
		}

		while (!queue.isEmpty()) {
			State s = queue.poll();
			if (goalTest(s)) {
				List<Object> goal = new ArrayList<Object>();
				goal.add(s);
				goal.add(count);
				return goal;
			}
			if (!visited(s)) {
				count++;
				visitedStates.add(s.toString());
				List<Action> availableActions = s.availableActions;
				for (Action action : availableActions) {
					State next = s.nextState(action);
					if (!visited(next)) {
						int index = getIndexActual(queue, next);
						queue.add(index, next);
					}
				}
			}
		}
		return null;
	}

	private List<Object> GR1(State state, boolean b) {
		queue.clear();
		visitedStates.clear();
		int count = 1;
		for (Action action : state.getAvailableActions()) {
			State next = state.nextState(action);
			int index = getIndexH1(queue, next);
			queue.add(index, next);
		}

		while (!queue.isEmpty()) {
			State s = queue.poll();
			if (goalTest(s)) {
				List<Object> goal = new ArrayList<Object>();
				goal.add(s);
				goal.add(count);
				return goal;
			}
			if (!visited(s)) {
				count++;
				visitedStates.add(s.toString());
				List<Action> availableActions = s.availableActions;
				for (Action action : availableActions) {
					State next = s.nextState(action);
					if (!visited(next)) {
						int index = getIndexH1(queue, next);
						queue.add(index, next);
					}
				}
			}
		}
		return null;
	}
	
	private List<Object> GR2(State state, boolean b) {
		queue.clear();
		visitedStates.clear();
		int count = 1;
		for (Action action : state.getAvailableActions()) {
			State next = state.nextState(action);
			int index = getIndexH2(queue, next);
			queue.add(index, next);
		}

		while (!queue.isEmpty()) {
			State s = queue.poll();
			if (goalTest(s)) {
				List<Object> goal = new ArrayList<Object>();
				goal.add(s);
				goal.add(count);
				return goal;
			}
			if (!visited(s)) {
				count++;
				visitedStates.add(s.toString());
				List<Action> availableActions = s.availableActions;
				for (Action action : availableActions) {
					State next = s.nextState(action);
					if (!visited(next)) {
						int index = getIndexH2(queue, next);
						queue.add(index, next);
					}
				}
			}
		}
		return null;
	}
	
	private List<Object> ID(State state, boolean b) {
		int level = 1;
		List<Object> goal = null;
		State goalState;
		while (!exceededDepth) {
			goal = DL(state, level, b);
			if (goal != null) {
				goalState = (State) goal.get(0);
				if (goalTest(goalState))
					break;
			}
			level++;
		}
		exceededDepth = false;
		return goal;
	}

	private List<Object> DL(State state, int depth, boolean b) {

		stk.clear();
		visitedStates.clear();
//		System.out.println(state.availableActions);
//		System.out.println(state);
//		System.out.println(state.checkLeft());
//		State next = state.nextState(Action.KILL);
//		System.out.println(state.checkLeft());
//		System.out.println(state);
//		State next2 = state.nextState(Action.KILL);
//		System.out.println(state);
//		System.out.println(next.checkLeft());
//		System.out.println(next2.checkLeft());
//		System.out.println(next);
//		System.out.println(next2);

		int count = 1;
		for (Action action : state.availableActions) {
			State next = state.nextState(action);
			next.level = state.level + 1;
			stk.add(next);
		}

		boolean lastNode = true;

		while (!stk.isEmpty()) {
			State s = stk.pop();
			if (goalTest(s)) {
				List<Object> goal = new ArrayList<Object>();
				goal.add(s);
				goal.add(count);
				return goal;
			}
//			System.out.println(s.availableActions);
//			System.out.println("UUU");
			if (!visited(s)) {
//				System.out.println(s.leadingActions);
//				System.out.println(s.level);
//				System.out.println(s.availableActions);
				if (s.level < depth) {
					count++;
//					System.out.println(s.availableActions);
					visitedStates.add(s.toString());
					List<Action> availableActions = s.availableActions;
					for (Action action : availableActions) {
						State next = s.nextState(action);
						next.level = s.level+1;

						if (!visited(next))
							stk.add(next);
					}
				} else {
					lastNode = false;
				}
			}
		}
		if (lastNode) {
			exceededDepth = true;
		}

		return null;
	}

	private List<Object> DF(State state, boolean b) {
		stk.clear();
		visitedStates.clear();
		int count = 1;
		for (Action action : state.getAvailableActions()) {
			State next = state.nextState(action);
			stk.add(next);
		}

		while (!stk.isEmpty()) {
			State s = stk.pop();
			if (goalTest(s)) {
				List<Object> goal = new ArrayList<Object>();
				goal.add(s);
				goal.add(count);
				return goal;
			}
			if (!visited(s)) {
				count++;
				visitedStates.add(s.toString());
				List<Action> availableActions = s.availableActions;
				for (Action action : availableActions) {
					State next = s.nextState(action);
					if (!visited(next))
						stk.add(next);
				}
			}
		}
		return null;
	}

	public static void main(String[] args) {
		try {
			String grid0 = "5,5;2;3,4;1,2;0,3,1,4;2,3;4,4,0,2,0,2,4,4;2,2,91,2,4,62";
			String grid1 = "5,5;1;1,4;1,0;0,4;0,0,2,2;3,4,4,2,4,2,3,4;0,2,32,0,1,38";
			String grid2 = "5,5;2;3,2;0,1;4,1;0,3;1,2,4,2,4,2,1,2,0,4,3,0,3,0,0,4;1,1,77,3,4,34";
			String grid3 = "5,5;1;0,4;4,4;0,3,1,4,2,1,3,0,4,1;4,0;2,4,3,4,3,4,2,4;0,2,98,1,2,98,2,2,98,3,2,98,4,2,98,2,0,1";
			String grid4 = "5,5;1;0,4;4,4;0,3,1,4,2,1,3,0,4,1;4,0;2,4,3,4,3,4,2,4;0,2,98,1,2,98,2,2,98,3,2,98,4,2,98,2,0,98,1,0,98";
			String grid5 = "5,5;2;0,4;3,4;3,1,1,1;2,3;3,0,0,1,0,1,3,0;4,2,54,4,0,85,1,0,43";
			String grid6 = "5,5;2;3,0;4,3;2,1,2,2,3,1,0,0,1,1,4,2,3,3,1,3,0,1;2,4,3,2,3,4,0,4;4,4,4,0,4,0,4,4;1,4,57,2,0,46";
			String grid7 = "5,5;3;1,3;4,0;0,1,3,2,4,3,2,4,0,4;3,4,3,0,4,2;1,4,1,2,1,2,1,4,0,3,1,0,1,0,0,3;4,4,45,3,3,12,0,2,88";
			String grid8 = "5,5;2;4,3;2,1;2,0,0,4,0,3,0,1;3,1,3,2;4,4,3,3,3,3,4,4;4,0,17,1,2,54,0,0,46,4,1,22";
			String grid9 = "5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;0,0,30,3,0,80,4,4,80";
			String grid10 = "5,5;4;1,1;4,1;2,4,0,4,3,2,3,0,4,2,0,1,1,3,2,1;4,0,4,4,1,0;2,0,0,2,0,2,2,0;0,0,62,4,3,45,3,3,39,2,3,40";

			String p = solve(grid0, "BF", false);
			System.out.println(p);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public State generalSearch(Matrix x, searchType strategy, boolean visualize) {
		List<Object> goal = new ArrayList<Object>();
		switch (strategy) {
		case BF:
			goal = x.BF(initialState, visualize);
			break;
		case DF:
			goal = x.DF(initialState, visualize);
			break;
		case UC:
			goal = x.UC(initialState, visualize);
			break;
		case ID:
			goal = x.ID(initialState, visualize);
			break;
		case GR1:
			goal = x.GR1(initialState, visualize);
			break;
		case GR2:
			goal = x.GR2(initialState, visualize);
			break;
		default:
			goal = x.DF(initialState, visualize);
		}

		if (goal != null) {
			expandedNodes = (int) goal.get(1);
			return (State) goal.get(0);
		}
		return null;
	}

	@Override
	public boolean goalTest(State s) {
		int[] telephoneXY = s.grid.getTelephoneXY();
		if (s.neoXY[0] == telephoneXY[0] && s.neoXY[1] == telephoneXY[1] && s.remainingConvertedAgents.size() == 0
				&& s.remainingHostages.size() == 0 && s.carriedHostages.size() == 0)
			return true;

		return false;
	}

	public static int getIndexActual(List<State> queue, State state) {
		int low = 0;
		int high = queue.size();

		while (low < high) {
			int mid = (low + high) >>> 1;
			List<Object> queueStateCost = queue.get(mid).pathCost();
			List<Object> stateCost = state.pathCost();
			if ((int) queueStateCost.get(0) < (int) stateCost.get(0)
					|| ((int) queueStateCost.get(0) == (int) stateCost.get(0)
							&& (int) queueStateCost.get(1) < (int) stateCost.get(1))
					|| ((int) queueStateCost.get(0) == (int) stateCost.get(0)
							&& (int) queueStateCost.get(1) == (int) stateCost.get(1)
							&& (int) queueStateCost.get(2) < (int) stateCost.get(2)))
				low = mid + 1;
			else
				high = mid;
		}
		return low;
	}
	
	public static int getIndexH1(List<State> queue, State state) {
		int low = 0;
		int high = queue.size();
	
//		System.out.println(state.leadingActions);
//		String[] actions = state.leadingActions.split(",");
//		List<String> resultList = Arrays.asList(actions);
//		int freq = Collections.frequency(resultList, "takePill");
//		System.out.println(freq);
		
		String str = state.leadingActions;
		String findStr = "takePill";
		System.out.println(str.split(findStr, -1).length-1);
		System.out.println(str);
		
		System.out.println(state.remainingPills.size());
		
		

		while (low < high) {
			int mid = (low + high) >>> 1;
			List<Object> queueStateCost = queue.get(mid).pathCost();
			List<Object> stateCost = state.pathCost();
			if ((int) queueStateCost.get(0) < (int) stateCost.get(0)
					|| ((int) queueStateCost.get(0) == (int) stateCost.get(0)
							&& (int) queueStateCost.get(1) < (int) stateCost.get(1)))
				low = mid + 1;
			else
				high = mid;
		}
		return low;
	}
	
	public static int getIndexH2(List<State> queue, State state) {
		int low = 0;
		int high = queue.size();

		while (low < high) {
			int mid = (low + high) >>> 1;
			List<Object> queueStateCost = queue.get(mid).pathCost();
			List<Object> stateCost = state.pathCost();
			if ((int) queueStateCost.get(0) < (int) stateCost.get(0)
					|| ((int) queueStateCost.get(0) == (int) stateCost.get(0)
							&& (int) queueStateCost.get(2) < (int) stateCost.get(2)))
					
				low = mid + 1;
			else
				high = mid;
		}
		return low;
	}
	
	public static int getIndexA2(List<State> queue, State state) {
		int low = 0;
		int high = queue.size();
		

		while (low < high) {
			int mid = (low + high) >>> 1;
			List<Object> queueStateCost = new ArrayList<Object>();
			queueStateCost.add((int)queue.get(mid).pathCost().get(0)*2);
			queueStateCost.add((int)queue.get(mid).pathCost().get(2)*2);
			
			List<Object> stateCost = state.pathCost();
			if ((int) queueStateCost.get(0) < (int) stateCost.get(0)
					|| ((int) queueStateCost.get(0) == (int) stateCost.get(0)
							&& (int) queueStateCost.get(1) < (int) stateCost.get(1)))
					
				low = mid + 1;
			else
				high = mid;
		}
		return low;
	}
	
	public static int getIndexA1(List<State> queue, State state) {
		int low = 0;
		int high = queue.size();
		

		while (low < high) {
			int mid = (low + high) >>> 1;
			List<Object> queueStateCost = new ArrayList<Object>();
			queueStateCost.add((int)queue.get(mid).pathCost().get(0)*2);
			queueStateCost.add((int)queue.get(mid).pathCost().get(2)*2);
			
			List<Object> stateCost = state.pathCost();
			if ((int) queueStateCost.get(0) < (int) stateCost.get(0)
					|| ((int) queueStateCost.get(0) == (int) stateCost.get(0)
							&& (int) queueStateCost.get(2) > (int) stateCost.get(2)))
					
				low = mid + 1;
			else
				high = mid;
		}
		return low;
	}
}