package ai;

import java.util.*;

public class Puzzle {
	
	//static String Init_Config = "1348627_5";
	
	//static String Init_Config = "281_43765";
	
	//static String Init_Config = "281463_75";
	static String Init_Config = "5874123_6";
	//static String Goal_Config = "1238_4765";
	static String Goal_Config = "_12345678"; 
	
	static private Deque<State> Container = new LinkedList<State>();
	static private HashSet<State> VisitedStates = new HashSet<State>();
	
	static private long startTime = 0;
	static private long endTime = 0;
	static private long totalNodes = 0;
	
	static enum Action {
		Start, Left, Right, Up, Down, UNDEFINED
	}
	
	private static interface Search {
		State run();
	}
	
	private static void init(String start, String end) {
		Init_Config = start; 
		Goal_Config = end;
		if (new State(start).parity() != new State(end).parity()) {
			System.out.println("parity check failed\n" + "No Solution :-(");
			System.exit(0);
		}
		Container.add(new State(start.toCharArray(), null, Action.Start));
	}
	
	private static void report(State solution) {
		if(solution != null) {
			System.out.println("Solution Found!");
			State current = solution;
			int steps = -1;
			while(current != null ) {
				System.out.println(current);
				current = current.getParent();
				steps++;
			}
			System.out.println("\nsteps taken: " + steps);
			System.out.println("Total nodes: " + totalNodes);
			System.out.println("Time taken: " + (endTime - startTime)/1000000.0 + " ms");
		}
		else
			System.out.println("No Solution :-(");
	}
	
	private static void search(Search algo, String[] args) {
		if(args.length != 2) {
			init(Init_Config, Goal_Config);
		} else
			init(args[0], args[1]);
		startTime = System.nanoTime();
		State solution = algo.run();
		endTime = System.nanoTime();
		report(solution);
	}
	
	/* BFS search */
	static public class BFS implements Search {
		
		public static void main(String[] args) {
			search(new BFS(), args);
		}
		
		
		
		public State run() {
			State current = null;
			while((current = Container.poll()) != null) {
				if(String.valueOf(current.getState()).equals(Goal_Config))
					return current;
				//System.out.println("Expanding state: " + current);
				totalNodes--;
				VisitedStates.add(current);
				ExpandIntoQueue(current);
			}
			return null;
		}
		
		private void ExpandIntoQueue(State c_state) {
			
			State left = c_state.moveSpaceLeft();
			if(left != null && !VisitedStates.contains(left)) {
				Container.add(left);
				totalNodes++;
			}
		
			State right = c_state.moveSpaceRight();
			if(right != null && !VisitedStates.contains(right)) {
				Container.add(right);
				totalNodes++;
			}
			State up = c_state.moveSpaceUp();
			if(up != null && !VisitedStates.contains(up)) {
				Container.add(up);
				totalNodes++;
			}
			State down = c_state.moveSpaceDown();
			if(down != null && !VisitedStates.contains(down)) {
				Container.add(down);
				totalNodes++;
			}
		}
	}
	
	/* DFS Search */
	static public class DFS implements Search{
		
		
		public static void main(String[] args) {
			search(new DFS(), args);
		}
		
		
		public State run() {
			State current = null;
			while(!Container.isEmpty()) {
				current = Container.pop();
				if(String.valueOf(current.getState()).equals(Goal_Config))
					return current;
				//System.out.println("Expanding state: " + current);
				totalNodes--;
				VisitedStates.add(current);
				ExpandIntoStack(current);
			}
			return null;
		}
		
		private void ExpandIntoStack(State c_state) {
			
			State left = c_state.moveSpaceLeft();
			if(left != null && !VisitedStates.contains(left)) {
				Container.push(left);
				totalNodes++;
			}
		
			State right = c_state.moveSpaceRight();
			if(right != null && !VisitedStates.contains(right)) {
				Container.push(right);
				totalNodes++;
			}
			State up = c_state.moveSpaceUp();
			if(up != null && !VisitedStates.contains(up)) {
				Container.push(up);
				totalNodes++;
			}
			State down = c_state.moveSpaceDown();
			if(down != null && !VisitedStates.contains(down)) {
				Container.push(down);
				totalNodes++;
			}			
		}
	}

	static private class State {
		private char[] state = null;
		private int space_idx = 0;
		private State parent = null;
		private Action action;
		
		State(char[] str_state, State s_parent, Action e_action) {
			state = str_state;
			space_idx = String.valueOf(str_state).indexOf('_');
			parent = s_parent;
			action = e_action;
		}
		
		State(String str_state){
			state = str_state.toCharArray();
			space_idx = str_state.indexOf('_');
			parent = null;
			action = Action.UNDEFINED;
		}

		char[] getState() {
			return state;
		}
		
		State moveSpaceLeft() {
			if(space_idx % 3 != 0) {
				char[] newState = state.clone();
				newState[space_idx] = newState[space_idx-1];
				newState[space_idx-1] = '_';
				return new State(newState, this, Action.Left);
			}
			return null;
		}
		
		State moveSpaceRight() {
			if(space_idx % 3 != 2) {
				char[] newState = state.clone();
				newState[space_idx] = newState[space_idx+1];
				newState[space_idx+1] = '_';
				return new State(newState, this, Action.Right);
			}
			return null;
		}
		
		State moveSpaceUp() {
			if(space_idx >= 3 ) {
				char[] newState = state.clone();
				newState[space_idx] = newState[space_idx-3];
				newState[space_idx-3] = '_';
				return new State(newState, this, Action.Up);
			}
			return null;
		}
		
		State moveSpaceDown() {
			if(space_idx <= 5 ) {
				char[] newState = state.clone();
				newState[space_idx] = newState[space_idx+3];
				newState[space_idx+3] = '_';
				return new State(newState, this, Action.Down);
			}
			return null;
		}

		@Override
		public String toString() {
			return "State [state=" + Arrays.toString(state) + ", action=" + action + "]";
		}

		public State getParent() {
			return parent;
		}


		public int parity() {
	        int total = 0;

	        for(int i = 0; i < state.length; i++) {
	            if(state[i] == '_') 
	                continue;
	            
	            for(int j = i+1; j < state.length; j++)
	                if(state[i] > state[j])
	                    total++;
	        }
	        return total % 2;
	    }


		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(state);
			return result;
		}


		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			State other = (State) obj;
			if (!Arrays.equals(state, other.state))
				return false;
			return true;
		}
		
	}
	
}

