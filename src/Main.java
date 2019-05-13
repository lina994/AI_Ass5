
import java.util.Scanner;
import java.util.LinkedList;

public class Main {
    static Scanner scan = new Scanner(System.in);

    // from parser
    static Edge[][] vertexMatrix;
    static int[] people;
    static int deadline;
    static boolean[] shelters;
    static int numOfEdges = 0;
    static int numOfuncertainEdges = 0;
    static int numOfVertices;

    static Edge[] uncertainEdges; // initially just uncertain edges
    static int startVertex; // get from user
    static int utilityTypeCalc;
    static boolean first_run = true;

    static double numOfState = 0; // increase in BeliefState constructor
    static StateArr head;
    static LinkedList<BeliefState> states = new LinkedList<BeliefState>();

    static LinkedList<StateArr> statesArr = new LinkedList<StateArr>();

    static LinkedList<BeliefState> realPath = new LinkedList<BeliefState>();

    // for 10,000 returns
    static int[] isBlockage;
    static double averageScore = 0;

    public static void main(String[] args) {
        // parse
        FileParser parser = new FileParser();
        parser.parse("file.txt");

        // uncertainEdges - over on all edges and add edges with prob>0
        int k = 0;
        uncertainEdges = new Edge[numOfuncertainEdges];
        for (int i = 0; i < vertexMatrix.length; i++) {
            for (int j = i + 1; j < vertexMatrix[i].length; j++) {
                if (vertexMatrix[i][j] != null && vertexMatrix[i][j].blockageProbability != 0) {
                    uncertainEdges[k] = vertexMatrix[i][j].deepEdgeCopy();
                    k++;
                }
            }
        }

        // get "start" vertex from user
        System.out.println("Enter start vertex");
        while (true) {
            try {
                startVertex = Integer.parseInt(scan.next());
                startVertex--;
                if (startVertex >= 0 && startVertex <= numOfVertices - 1)
                    break;
                System.out.println("Wrong input, enter natural number between 1 and " + numOfVertices);
            } catch (Exception e) {
                System.out.println("Wrong input, enter natural number between 1 and " + numOfVertices);
            }
        }

        // get type of utility calc from user
        System.out.println("Enter type of utility calculation");
        System.out.println("for max tree press 1");
        System.out.println("for value iteration press 2");
        while (true) {
            try {
                utilityTypeCalc = Integer.parseInt(scan.next());
                if (utilityTypeCalc == 1 || utilityTypeCalc == 2)
                    break;
                System.out.println("Wrong input, enter 1 or 2");
            } catch (Exception e) {
                System.out.println("Wrong input, enter 1 or 2");
            }
        }

        // create states and calculate utility
        if (utilityTypeCalc == 1) {
            createStateArrUsingMinMax(null, startVertex, people.clone(), 0, 0, Edge.deepEdgeArrCopy(uncertainEdges), 0);
            System.out.println(states.size() + " states are in states list");
        } else {
            createStateArr(null, startVertex, people.clone(), 0, 0, Edge.deepEdgeArrCopy(uncertainEdges), 0);
            iterate();
            System.out.println(states.size() + " states are in states list");
        }

        // print result after calc utility
        System.out.println("First state utility is: " + head.utility);

        // create counter for probability result
        isBlockage = new int[uncertainEdges.length];

        // 3 10000 times
        int times = 10000;
        for (int t = 0; t < times; t++) {

            // call random
            Edge[] probEdge = Edge.deepEdgeArrCopy(uncertainEdges);
            for (int i = 0; i < probEdge.length; i++) {
                probEdge[i].blockageProbability = randomBlockage(probEdge[i].blockageProbability);
            }

            // calculate real score
            realPath = new LinkedList<BeliefState>();
            calcPathAndScore(Main.head, probEdge);

            if (first_run) {
                // print real roads state and count % that road is blocked
                for (int i = 0; i < probEdge.length; i++) {
                    System.out.print("Edge " + probEdge[i].name + " is ");
                    if (probEdge[i].blockageProbability == 0) {
                        System.out.println("unblocked");
                    } else {
                        System.out.println("blocked");
                        isBlockage[i] = isBlockage[i] + 1;
                    }
                }
                // test for print path
                for (int i = 0; i < realPath.size(); i++) {
                    realPath.get(i).print();
                }
                first_run = false;
            }

        }

        // print real average result
        Main.averageScore /= times;
        System.out.println("averageScore is: " + Main.averageScore);

    }// end of main

    private static void calcPathAndScore(StateArr st, Edge[] probEdge) {
        // find index according to edge real state
        BeliefState bs = null;
        boolean isMatch = true;
        for (int i = 0; i < st.arr.length; i++) {
            isMatch = true;
            bs = st.arr[i];
            for (int j = 0; j < bs.uncertainEdges.length; j++) {
                if (bs.uncertainEdges[j].blockageProbability == 0 || bs.uncertainEdges[j].blockageProbability == 1) {
                    if (bs.uncertainEdges[j].blockageProbability != probEdge[j].blockageProbability) {
                        isMatch = false;
                        break;
                    }
                }
            }
            if (isMatch)
                break;
        }
        // add to path
        if (bs == null)
            System.out.println("problem, bs not initialized");
        else
            realPath.add(bs);

        // if next is null add score to averageScore
        if (bs.next == null) {
            averageScore += bs.saved;
            return;
        }

        // rec call on next
        calcPathAndScore(bs.next, probEdge);
    }

    private static void createStateArr(BeliefState st_parent, int st_nextVertex, int[] st_people, int st_saved,
            int st_peopleInCar, Edge[] st_uncertainEdges, int st_time) {

        // 1.0 handle shelter and people
        if (st_time <= Main.deadline) {
            if (st_people[st_nextVertex] != 0) {
                st_peopleInCar += st_people[st_nextVertex];
                st_people[st_nextVertex] = 0;
            }
            if (Main.shelters[st_nextVertex]) {
                st_saved += st_peopleInCar;
                st_peopleInCar = 0;
            }
        }

        // 1.1 calc num of neighbor uncertain edges
        int k = 0;
        for (int i = 0; i < st_uncertainEdges.length; i++) {
            if (st_uncertainEdges[i].v1 == st_nextVertex || st_uncertainEdges[i].v2 == st_nextVertex) {
                if (st_uncertainEdges[i].blockageProbability != 0 && st_uncertainEdges[i].blockageProbability != 1)
                    k++;
            }
        }

        // 1.2 create stateArr
        StateArr st;
        // there are not neighbor edges that uncertain
        if (k == 0) {
            st = new StateArr(1);
            statesArr.add(st);
            st.prob[0] = 1;
            st.arr[0] = new BeliefState(st_nextVertex, st_people.clone(), st_saved, st_peopleInCar,
                    Edge.deepEdgeArrCopy(st_uncertainEdges), st_time, 0);

            BeliefState prev = getStateFromList(st.arr[0]);
            if (prev == null)
                states.add(st.arr[0]);

            if (prev != null) {
                st.arr[0] = prev;
                st_parent.children.add(st);
                return;
            }
        }
        // there are neighbor edges that uncertain
        // num of belief state in state arr = 2^|uncertainEdges|
        else {
            int[][] arrTF = new int[(int) Math.pow(2, k)][k];
            setTandF(arrTF);

            st = new StateArr((int) Math.pow(2, k));
            statesArr.add(st);
            for (int i = 0; i < arrTF.length; i++) { // move on rows
                int ind = 0; // column index
                Edge[] un_ed = Edge.deepEdgeArrCopy(st_uncertainEdges);
                st.prob[i] = 1;

                for (int j = 0; j < st_uncertainEdges.length; j++) {
                    if (st_uncertainEdges[j].v1 == st_nextVertex || st_uncertainEdges[j].v2 == st_nextVertex) {
                        if (st_uncertainEdges[j].blockageProbability != 0
                                && st_uncertainEdges[j].blockageProbability != 1) {
                            if (arrTF[i][ind] == 0) { // not blockage
                                un_ed[j].blockageProbability = 0;
                                st.prob[i] *= (1 - st_uncertainEdges[j].blockageProbability);
                            } else { // blockage
                                un_ed[j].blockageProbability = 1;
                                st.prob[i] *= st_uncertainEdges[j].blockageProbability;
                            }
                            ind++;
                        }
                    }
                }
                st.arr[i] = new BeliefState(st_nextVertex, st_people.clone(), st_saved, st_peopleInCar, un_ed, st_time,
                        0);

                BeliefState prev = getStateFromList(st.arr[i]);
                if (prev == null)
                    states.add(st.arr[i]);
                else {
                    st.arr[i] = prev;
                    prev.isNew = false;
                }
            }
        }
        // 1.3 add stateArr to parents
        if (st_parent == null)
            head = st;
        else
            st_parent.children.add(st);

        // 1.5 recursive call on children
        for (int b = 0; b < st.arr.length; b++) {
            BeliefState bs = st.arr[b];
            if (!bs.isNew)
                continue;

            for (int i = 0; i < Main.vertexMatrix.length; i++) {
                for (int j = i + 1; j < vertexMatrix[i].length; j++) {
                    // check if vertex exist and not blocked
                    if ((i == bs.location || j == bs.location) && vertexMatrix[i][j] != null) {
                        int index = Edge.getIndex(vertexMatrix[i][j], bs.uncertainEdges);
                        if (index == -1 || bs.uncertainEdges[index].blockageProbability != 1) {
                            int newTime = st_time + Main.vertexMatrix[i][j].weight;
                            // if exist and not blocked call rec
                            if (i == bs.location && newTime <= Main.deadline) {
                                createStateArr(bs, j, st_people.clone(), st_saved, st_peopleInCar,
                                        Edge.deepEdgeArrCopy(bs.uncertainEdges), newTime);
                            } else if (newTime <= Main.deadline) {
                                createStateArr(bs, i, st_people.clone(), st_saved, st_peopleInCar,
                                        Edge.deepEdgeArrCopy(bs.uncertainEdges), newTime);
                            }
                        }
                    }
                }
            }
        }

    }

    private static void setTandF(int[][] matrix) {
        for (int i = 0; i < matrix[0].length; i++) { // first line is 0...0
            matrix[0][i] = 0;
        }
        for (int i = 1; i < matrix.length; i++) {
            matrix[i] = matrix[i - 1].clone();
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][j] = 1;
                    break;
                } else if (matrix[i][j] == 1) {
                    matrix[i][j] = 0;
                } else
                    System.out.println("problem O_O");
            }
        }
    }

    public static int randomBlockage(double prob) {
        double x = Math.random();
        if (x < prob)
            return 1;
        else
            return 0;
    }

    public static boolean stateListContains(BeliefState s1) {

        for (int j = 0; j < states.size(); j++) {

            boolean equal = true;

            if (equal && (s1.location != states.get(j).location))
                equal = false;

            for (int i = 0; equal && i < s1.people.length; i++)
                if (s1.people[i] != states.get(j).people[i])
                    equal = false;

            if (equal && (s1.saved != states.get(j).saved))
                equal = false;

            if (equal && (s1.peopleInCar != states.get(j).peopleInCar))
                equal = false;

            for (int i = 0; equal && i < s1.uncertainEdges.length; i++)
                if (s1.uncertainEdges[i].blockageProbability != states.get(j).uncertainEdges[i].blockageProbability)
                    equal = false;

            if (equal && (s1.time != states.get(j).time))
                equal = false;

            if (equal && (s1.utility != states.get(j).utility))
                equal = false;

            if (equal)
                return true;
        }

        return false;

    }

    public static BeliefState getStateFromList(BeliefState s1) {

        for (int j = 0; j < states.size(); j++) {

            boolean equal = true;

            if (equal && (s1.location != states.get(j).location))
                equal = false;

            for (int i = 0; equal && i < s1.people.length; i++)
                if (s1.people[i] != states.get(j).people[i])
                    equal = false;

            if (equal && (s1.saved != states.get(j).saved))
                equal = false;

            if (equal && (s1.peopleInCar != states.get(j).peopleInCar))
                equal = false;

            for (int i = 0; equal && i < s1.uncertainEdges.length; i++)
                if (s1.uncertainEdges[i].blockageProbability != states.get(j).uncertainEdges[i].blockageProbability)
                    equal = false;

            if (equal && (s1.time != states.get(j).time))
                equal = false;

            if (equal)
                return states.get(j);
        }

        return null;

    }

    public static void iterate() {
        boolean changes = true;
        while (changes) {
            for (int i = 0; i < states.size(); i++)
                states.get(i).utility_prevIter = states.get(i).utility;

            for (int k = 0; k < statesArr.size(); k++) {
                statesArr.get(k).utility = 0;
                for (int i = 0; i < statesArr.get(k).arr.length; i++) {
                    BeliefState bs = statesArr.get(k).arr[i];
                    for (int j = 0; j < bs.children.size(); j++) {
                        if (bs.children.get(j).utility > bs.utility) {
                            bs.utility = bs.children.get(j).utility;
                            bs.next = bs.children.get(j);
                        }
                    }
                    if (bs.children.size() == 0) {
                        bs.utility = bs.saved;
                    }
                    statesArr.get(k).utility += (statesArr.get(k).arr[i].utility * statesArr.get(k).prob[i]);
                }
            }

            changes = false;
            for (int i = 0; i < states.size(); i++)
                if (states.get(i).utility != states.get(i).utility_prevIter) {
                    changes = true;
                }

        }

    }

    private static void createStateArrUsingMinMax(BeliefState st_parent, int st_nextVertex, int[] st_people,
            int st_saved, int st_peopleInCar, Edge[] st_uncertainEdges, int st_time) {

        // 1.0 handle shelter and people
        if (st_time <= Main.deadline) {
            if (st_people[st_nextVertex] != 0) {
                st_peopleInCar += st_people[st_nextVertex];
                st_people[st_nextVertex] = 0;
            }
            if (Main.shelters[st_nextVertex]) {
                st_saved += st_peopleInCar;
                st_peopleInCar = 0;
            }
        }

        // 1.1 calc num of neighbor uncertain edges
        int k = 0;
        for (int i = 0; i < st_uncertainEdges.length; i++) {
            if (st_uncertainEdges[i].v1 == st_nextVertex || st_uncertainEdges[i].v2 == st_nextVertex) {
                if (st_uncertainEdges[i].blockageProbability != 0 && st_uncertainEdges[i].blockageProbability != 1)
                    k++;
            }
        }

        // 1.2 create stateArr
        StateArr st;
        // there are not neighbor edges that uncertain
        if (k == 0) {
            st = new StateArr(1);
            st.prob[0] = 1;
            st.arr[0] = new BeliefState(st_nextVertex, st_people.clone(), st_saved, st_peopleInCar,
                    Edge.deepEdgeArrCopy(st_uncertainEdges), st_time, Double.NEGATIVE_INFINITY);

            BeliefState prev = getStateFromList(st.arr[0]);
            if (prev == null)
                states.add(st.arr[0]);

            if (prev != null) {
                st.arr[0] = prev;
                prev.isNew = false;
                st_parent.children.add(st);
            }

        }
        // there are neighbor edges that uncertain
        // num of belief state in state arr = 2^|uncertainEdges|
        else {
            int[][] arrTF = new int[(int) Math.pow(2, k)][k];
            setTandF(arrTF);

            st = new StateArr((int) Math.pow(2, k));
            for (int i = 0; i < arrTF.length; i++) { // move on rows
                int ind = 0; // column index
                Edge[] un_ed = Edge.deepEdgeArrCopy(st_uncertainEdges);
                st.prob[i] = 1;

                for (int j = 0; j < st_uncertainEdges.length; j++) {
                    if (st_uncertainEdges[j].v1 == st_nextVertex || st_uncertainEdges[j].v2 == st_nextVertex) {
                        if (st_uncertainEdges[j].blockageProbability != 0
                                && st_uncertainEdges[j].blockageProbability != 1) {
                            if (arrTF[i][ind] == 0) { // not blockage
                                un_ed[j].blockageProbability = 0;
                                st.prob[i] *= (1 - st_uncertainEdges[j].blockageProbability);
                            } else { // blockage
                                un_ed[j].blockageProbability = 1;
                                st.prob[i] *= st_uncertainEdges[j].blockageProbability;
                            }
                            ind++;
                        }
                    }
                }
                st.arr[i] = new BeliefState(st_nextVertex, st_people.clone(), st_saved, st_peopleInCar, un_ed, st_time,
                        Double.NEGATIVE_INFINITY);
                BeliefState prev = getStateFromList(st.arr[i]);
                if (prev == null)
                    states.add(st.arr[i]);
                else {
                    st.arr[i] = prev;
                    prev.isNew = false;
                }

            }
        }
        // 1.3 add stateArr to parents
        if (st_parent == null)
            head = st;
        else
            st_parent.children.add(st);

        // 1.4 check if goal state
        if (st_time >= Main.deadline) {
            st.utility = 0;
            for (int i = 0; i < st.arr.length; i++) {
                st.arr[i].utility = st.arr[i].saved;
                st.utility += (st.arr[i].utility * st.prob[i]);
            }
            return;
        }

        // 1.5 recursive call on children
        for (int b = 0; b < st.arr.length; b++) {
            BeliefState bs = st.arr[b];
            if (!bs.isNew)
                continue;
            for (int i = 0; i < Main.vertexMatrix.length; i++) {
                for (int j = i + 1; j < vertexMatrix[i].length; j++) {
                    // check if vertex exist and not blocked
                    if ((i == bs.location || j == bs.location) && vertexMatrix[i][j] != null) {
                        int index = Edge.getIndex(vertexMatrix[i][j], bs.uncertainEdges);
                        if (index == -1 || bs.uncertainEdges[index].blockageProbability != 1) {
                            int newTime = st_time + Main.vertexMatrix[i][j].weight;
                            // if exist and not blocked call rec
                            if (i == bs.location) {
                                createStateArrUsingMinMax(bs, j, st_people.clone(), st_saved, st_peopleInCar,
                                        Edge.deepEdgeArrCopy(bs.uncertainEdges), newTime);
                            } else {
                                createStateArrUsingMinMax(bs, i, st_people.clone(), st_saved, st_peopleInCar,
                                        Edge.deepEdgeArrCopy(bs.uncertainEdges), newTime);
                            }
                        }
                    }
                }
            }
        }

        // 1.6 update utility and update next
        st.utility = 0;
        for (int i = 0; i < st.arr.length; i++) {

            BeliefState bs = st.arr[i];
            for (int j = 0; j < bs.children.size(); j++) {
                if (bs.children.get(j).utility > bs.utility) {
                    bs.utility = bs.children.get(j).utility;
                    bs.next = bs.children.get(j);
                }
            }
            if (bs.children.size() == 0) {
                bs.utility = bs.saved;
            }
            st.utility += (st.arr[i].utility * st.prob[i]);
        }

    }

}