import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileParser {

    public void parse(String fileName) {
        File file = new File("file.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(file));) {
            String line;
            while ((line = br.readLine()) != null) {

                if (!(line.length() == 0) && line.charAt(0) == '#') {
                    switch (line.charAt(1)) {
                    case 'T':
                        parseNumOfVertex(line.substring(3));
                        break;
                    case 'E':
                        parseEdge(line.substring(2));
                        break;
                    case 'V':
                        parseVertex(line.substring(3));
                        break;
                    case 'D':
                        parseDeadLine(line.substring(3));
                        break;
                    }
                }

            }
        } catch (IOException e) {
            System.out.println("Unable to open file");
            e.printStackTrace();
        } finally {
            System.out.println("***read of file is completed***");
        }
    }

    private void parseNumOfVertex(String input) { // #T 4
        String[] array = input.split(" ");
        String s = array[0];
        Main.numOfVertices = Integer.parseInt(s);
        Main.vertexMatrix = new Edge[Main.numOfVertices][Main.numOfVertices];
        Main.people = new int[Main.numOfVertices];
        Main.shelters = new boolean[Main.numOfVertices];
    }

    private void parseEdge(String input) {// #E4 2 4 W4
        // System.out.println(input); //TODO delete
        String[] array = input.split(" ");
        String edgeName = array[0];
        int firstV = Integer.parseInt(array[1]);
        int secondV = Integer.parseInt(array[2]);
        int vertexWeight = 0;
        float vertexBlockage = 0;

        if (array[3].charAt(0) != 'W' || array[3].length() < 2)
            System.out.println("bad file syntax: " + array[3]);
        else
            vertexWeight = Integer.parseInt(array[3].substring(1));

        if (array.length >= 5 && array[4].length() > 0) {// blockage-optional

            if (array[4].charAt(0) != 'B' || array[4].length() < 2)
                System.out.println("bad file syntax: " + array[4]);
            else {
                vertexBlockage = Float.parseFloat(array[4].substring(1));

                if (vertexBlockage != 0 && vertexBlockage != 1)
                    Main.numOfuncertainEdges++;
            }
        }
        if (vertexBlockage != 1) {
            if (firstV < secondV)
                Main.vertexMatrix[firstV - 1][secondV - 1] = new Edge(edgeName, firstV - 1, secondV - 1, vertexWeight,
                        vertexBlockage);
            else
                Main.vertexMatrix[secondV - 1][firstV - 1] = new Edge(edgeName, secondV - 1, firstV - 1, vertexWeight,
                        vertexBlockage);
            Main.numOfEdges++;
        }

    }

    private void parseVertex(String input) { // #V 2 P 1 #V 1 S
        String[] array = input.split(" ");
        int vertexIndex = Integer.parseInt(array[0]);
        if (array[1].charAt(0) == 'P')
            Main.people[vertexIndex - 1] = Integer.parseInt(array[2]);

        else if (array[1].charAt(0) == 'S')
            Main.shelters[vertexIndex - 1] = true;

        else
            System.out.println("bad file syntax: " + array[1]);

    }

    private void parseDeadLine(String input) { // #D 10
        String[] array = input.split(" ");
        String s = array[0];
        Main.deadline = Integer.parseInt(s);
    }
}
