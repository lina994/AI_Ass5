
public class Edge {
	int weight;
	String name;
	int v1; //smaller
	int v2; //larger
	double blockageProbability;
	
	public Edge( String name, int v1, int v2, int weight,double blockageProbability) {
		super();
		this.weight = weight;
		this.name = name;
		this.v1 = v1;
		this.v2 = v2;
		this.blockageProbability = blockageProbability;
	}
	
	
	public Edge deepEdgeCopy() {
		Edge copy = new Edge(name, v1, v2, weight, blockageProbability);
		return copy;
	}
	
	public static Edge[] deepEdgeArrCopy(Edge[] origianl) {
		Edge[] copy = new Edge[origianl.length];
		for (int i = 0; i < copy.length; i++) 
			copy[i]=new Edge(origianl[i].name, origianl[i].v1, origianl[i].v2, origianl[i].weight, origianl[i].blockageProbability);
		return copy;
	}
	
	public static int getIndex(Edge e, Edge[] arr) {
		for (int i = 0; i < arr.length; i++) 
			if(e.name.equals(arr[i].name))
				return i;
		return -1;
	}
	
}
