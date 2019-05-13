
public class StateArr {
	BeliefState arr[];
	double prob[];
	int nextIndex;
	double utility=Double.NEGATIVE_INFINITY;

	public StateArr(int size) {
		super();
		this.arr = new BeliefState[size];
		this.prob = new double[size];
	}
	
}
