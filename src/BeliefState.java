import java.util.LinkedList;

public class BeliefState {

	boolean isNew = true;
	int location;
	int[] people;
	int saved;
	int peopleInCar;
	Edge[] uncertainEdges;
	int time;
	LinkedList<StateArr> children = new LinkedList<StateArr>();
	StateArr next;
	double utility;
	double utility_prevIter;
	
	 
	public BeliefState(int location, int[] people, int saved, int peopleInCar, Edge[] uncertainEdges, int time, double utility){
		super();
		this.location = location;
		this.people = people;
		this.saved = saved;
		this.peopleInCar=peopleInCar;
		this.uncertainEdges = uncertainEdges;
		this.time = time;
		this.utility = utility;
		this.utility_prevIter = utility;
		Main.numOfState++;
	}
	 
	public void print() {
		System.out.print("("+(location+1)+",");
		for (int i = 0; i < people.length; i++) 
			System.out.print(people[i]+",");
		System.out.print(saved+",");
		System.out.print(peopleInCar+",");
		for (int i = 0; i < uncertainEdges.length; i++) {
			if(uncertainEdges[i].blockageProbability==0)
				System.out.print("E"+uncertainEdges[i].name+"=U,");
			else if(uncertainEdges[i].blockageProbability==1)
				System.out.print("E"+uncertainEdges[i].name+"=B,");
			else
				System.out.print("E"+uncertainEdges[i].name+"=?,");
		}
		System.out.print(time+",");
		System.out.println(utility+")");
		
	}
	
	
}
