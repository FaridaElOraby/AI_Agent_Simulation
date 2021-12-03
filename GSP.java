package code;

public abstract class GSP {
	public State initialState;
	public Action[] actions;
	
	public abstract State generalSearch(Matrix x, searchType strategy, boolean visualize);
	public abstract boolean goalTest(State s);

}
