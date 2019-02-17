# Hurricane Evacuation Problem

Sequential decision making under uncertainty using belief-state MDP for decision-making. We are given a weighted undirected graph, and the problem is to find a policy that saves (in expectation) as many people as possible before the deadline.

Using the following principles:

* Artificial Intelligence:
    * Decision making under uncertainty
    * Utilities
    * Minimax tree
    * Value iteration algorithm
    * Optimal policy
* OOP, Files I/O


## Domain Description

* The domain description is similar to that described in [assignment 1](https://github.com/lina994/AI_Ass1 "assignment description").
* We do not know the locations of the blocakges.
* Blockages occur independently, with a known given probability.
* Blockages are revealed with certainty when the agent reaches a neigbouring vertex. 
* The number of evacuees at each vertex is known.
* Agent's actions are traveling between vertices.
* Traversal times are the weight of the edge.
* One agent

## Running

### Input

file.txt file include graph description and parameters such as P(Fl(v)=true))
For example:

    #T 11             ; number of vertices n in graph (from 1 to n)
    #V 8 P 1          ; Vertex 8, has 1 evacuees
    #V 9 P 1          ; Vertex 9, has 1 evacuee
    #E1 1 2 W1        ; Edge 1 between vertices 1 and 2, weight 1
    #E2 1 5 W1        ; Edge 2 between vertices 1 and 5, weight 1
    #E3 2 3 W1 B0.5   ; Edge 3 between vertices 2 and 3, weight 1, probability of blockage 0.5
    #E4 2 4 W1 B0.5   ; Edge 4 between vertices 2 and 4, weight 1, probability of blockage 0.5
    #E5 3 8 W1        ; Edge 5 between vertices 3 and 8, weight 1
    #E6 4 8 W1        ; Edge 6 between vertices 4 and 8, weight 1
    #E7 5 6 W1 B0.5   ; Edge 7 between vertices 5 and 6, weight 1, probability of blockage 0.5
    #E8 5 7 W1 B0.5   ; Edge 8 between vertices 5 and 7, weight 1, probability of blockage 0.5
    #E9 6 9 W1        ; Edge 9 between vertices 6 and 9, weight 1
    #E10 7 9 W1       ; Edge 10 between vertices 7 and 9, weight 1
    #E11 9 10 W1      ; Edge 10 between vertices 9 and 10, weight 1
    #E12 10 11 W1     ; Edge 12 between vertices 10 and 11, weight 1
    #E13 11 8 W1      ; Edge 13 between vertices 11 and 8, weight 1
    #D 11             ; Deadline is at time 11
    #V 1 S            ; Vertex 1, has shelter



<br>

### Graph visualization

![graph](https://github.com/lina994/AI_Ass5/blob/master/resources/input_example.png?raw=true "graph")

<br>

Additional input will be provided by the user via the terminal:

* Start vertex.
* Type of utility calculation.

For example:

    Enter start vertex
    > 1
    Enter type of utility calculation
    for max tree press 1
    for value iteration press 2
    > 2


### Output

* Detailed output will be displayed in the terminal
* A summary will be saved in the results.txt  file


## Authors

* Alina
    * [github](https://github.com/lina994 "github")
* Elina
    * [github](https://github.com/ElinaS21 "github")


## Official assignment description
[assignment 5](https://www.cs.bgu.ac.il/~shimony/AI2019/AIass5_2019.html "assignment description")




