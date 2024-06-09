package AstarAlgorithm;
import java.util.*;
import model.Position;
import model.state.State;
import AstarAlgorithm.Chemin;
import actions.Actions;




public class AStarAlgorithm {
    private static final int MAX_RECURSION_DEPTH = 1000;
    static class Node implements Comparable<Node> {
        Position position;
        boolean noeudSpecialVisite;
        double gCost; // Coût depuis le début
        double hCost; // Heuristique (distance estimée jusqu'à l'objectif)
        Node parent;

        public Node(Position position, boolean noeudSpecialVisite, double gCost, double hCost, Node parent) {
            this.position = position;
            this.noeudSpecialVisite = noeudSpecialVisite;
            this.gCost = gCost;
            this.hCost = hCost;
            this.parent = parent;
        }

        public double fCost() {
            return gCost + hCost;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.fCost(), other.fCost());
        }
    }
 

    public static List<Position> findPath(char[][] grid, Position start, Position goal) {
        char[][] transformedGrid = transformGrid(grid);

        PriorityQueue<Node> openList = new PriorityQueue<>();
        HashSet<Position> closedList = new HashSet<>();

        openList.add(new Node(start, false, 0, heuristic(start, goal), null));

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();

            if (currentNode.position.equals(goal) && currentNode.noeudSpecialVisite) {
                List<Position> path = constructPath(currentNode);
                //debugger to see if the path is found
                System.out.println("Path found: " + path);
                return path;
            }

            closedList.add(currentNode.position);

            for (Node neighbor : getNeighbors(currentNode, transformedGrid, goal)) {
                if (closedList.contains(neighbor.position)) {
                    continue;
                }

                if (!openList.contains(neighbor)) {
                    openList.add(neighbor);
                }
            }
        }
        //debugger to see if the path is found
        System.out.println("No path found from " + start + " to " + goal);
        return Collections.emptyList(); // Aucun chemin trouvé
    }
    
    public static List<Position> findPathEquipe(char[][] grid, Position start, Position goal) {
        char[][] transformedGrid = transformGridEquipe(grid);

        PriorityQueue<Node> openList = new PriorityQueue<>();
        HashSet<Position> closedList = new HashSet<>();

        openList.add(new Node(start, false, 0, heuristic(start, goal), null));

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();

            if (currentNode.position.equals(goal) && currentNode.noeudSpecialVisite) {
                return constructPath(currentNode);
            }

            closedList.add(currentNode.position);

            for (Node neighbor : getNeighbors(currentNode, transformedGrid, goal)) {
                if (closedList.contains(neighbor.position)) {
                    continue;
                }

                if (!openList.contains(neighbor)) {
                    openList.add(neighbor);
                }
            }
        }

        return Collections.emptyList(); // Aucun chemin trouvé
    }
    
    //Trouver la maison non connectée la plus proche de l'équipe la position de l'équipe
    
    
    public static Position findClosesNoneConnectedHouse (State etat) {
      //Position de equipe dans l'état
      Position position = etat.getEqPos();
      //Position de la maison la plus proche
      Position specialNode = null;
      //Distance entre la position de l'équipe et la maison la plus proche
      double minDistance = Double.MAX_VALUE;
    
      for (Position maison : etat.getMaisonPasconnecter()) {
        double currentDistance = heuristic(position, maison);
        // Si la distance entre la position de l'équipe et la maison actuelle est plus petite que la distance minimale
        if (currentDistance < minDistance) {
          minDistance = currentDistance;
          specialNode = maison;
        }
      }
      //Debugger pour voir la maison la plus proche est trouvée
      System.out.println("Closest non-connected house: " + specialNode);
      return specialNode;
    }

    public static boolean brisFound(Position specialNode, List<Chemin> chemins,List<Position>conducteurs, char[][] grid, State etat) {
        boolean BrisFound = false;
        //Le chemin qui match la position du special node
        Chemin chemin = new Chemin(new ArrayList<>());
        for (Chemin c : chemins) {
            if (c.getMaison().equals(specialNode)) {
                chemin = c;
                break;
            } 
            int index = 0;
         for (Position cell : chemin) {
                if (grid[cell.getLigne()][cell.getColonne()] == 'b') {
                    // Trouver l'index du conducteur brisé dans l'état initial
                    index = findIndexconducteurbriser(conducteurs, cell);
                    // Si le conducteur est brisé dans l'état initial alors le conducteur est brisé
                    if (etat.getConducteursbriser().get(index)) 
                    BrisFound = true;
                     break;
                 }
             }
            
        }
        return BrisFound;
    }
    public static boolean interrupteurState (Position specialNode,  List<Chemin> chemins, List<Position> interrupteurs, char[][] grid, State etat) {
        boolean interrupteurState = false;
        Chemin chemin = new Chemin(null);
        for (Chemin c : chemins) {
            if (c.getMaison().equals(specialNode)) {
                chemin = c;
                break;
            } 
            int index = 0;
         for (Position cell : chemin) {
                if (grid[cell.getLigne()][cell.getColonne()] == 'i') {
                    //Trouver l'index de l'interrupteur dans l'état initial
                    index = findIndexinterrupteur(interrupteurs, cell);
                    //Si l'interrupteur est ouvert dans l'état initial alors l'interrupteur est ouvert 
                    if (etat.getInterrupteurs().get(index)) interrupteurState = true;
                     break;
                 } else if (grid[cell.getLigne()][cell.getColonne()] == 'j'){
                    //Trouver l'index de l'interrupteur dans l'état initial
                    index = findIndexinterrupteur(interrupteurs, cell);
                    //Si l'interrupteur est fermé dans l'état initial alors l'interrupteur est fermé 
                    if (!etat.getInterrupteurs().get(index))  break;
                 }

             } 
            }
            return interrupteurState;
    }
    
    //Debugger pour empecher une boucle infinie
    
    public static List<String> astar(char[][] grid, State etat, List<Chemin> maisonNonConnecter, List<Position> interrupteurs, List<Position> conducteursbriser, List<String> solutionfinal) {
        return astar(grid, etat, maisonNonConnecter, interrupteurs, conducteursbriser, solutionfinal, 0);
    }
    private static List<String> astar(char[][] grid, State etat, List<Chemin> maisonNonConnecter, List<Position> interrupteurs, List<Position> conducteursbriser, List<String> solutionfinal, int depth) {
        if (depth > MAX_RECURSION_DEPTH) {
            System.out.println("Max recursion depth reached, terminating.");
            return solutionfinal;
        }
 
        List<Position> parcoursEquipe = new ArrayList<>();
        List<String> solution = solutionfinal;
        Position specialNode = findClosesNoneConnectedHouse(etat);

        System.out.println("Starting astar algorithm... Depth: " + depth);
        System.out.println("Special node: " + specialNode);

        if (specialNode == null) {
            System.out.println("No special node found, returning empty solution.");
            return solution; // No special node found
        }

        Chemin chemin = null;
        for (Chemin c : maisonNonConnecter) {
            if (c.getMaison().equals(specialNode)) {
                chemin = c;
        // Ensure the path to the specialNode is correctly calculated and assigned
        List<Position> pathToSpecialNode = findPathEquipe(grid, etat.getEqPos(), specialNode);
        chemin.setChemin(pathToSpecialNode); // Ensure this method exists in the Chemin class
        
                break;
            }
        }

        if (chemin == null) {
            System.out.println("No valid chemin found for the special node.");
            return solution;
        }

        System.out.println("Found chemin: " + chemin);

        // Debugging the conditions for invoking actions
        boolean isBrisFound = brisFound(specialNode, maisonNonConnecter, conducteursbriser, grid, etat);
        boolean isInterrupteurState = interrupteurState(specialNode, maisonNonConnecter, interrupteurs, grid, etat);

        System.out.println("brisFound: " + isBrisFound);
        System.out.println("interrupteurState: " + isInterrupteurState);

        if (isBrisFound || isInterrupteurState) {
            for (Position cell : chemin.getChemin()) {
                if (isInterrupteurState && grid[cell.getLigne()][cell.getColonne()] == 'i') {
                    parcoursEquipe = findPathEquipe(grid, etat.getEqPos(), cell);
                    System.out.println("Path to interrupteur: " + parcoursEquipe);
                    solution.addAll(Actions.transformpath(parcoursEquipe));
                    etat.setEqPos(cell);
                    int index = findIndexinterrupteur(interrupteurs, cell);
                    Actions.switchOff(etat, index, solution);
                    System.out.println("Switch off interrupteur at index: " + index);
                    System.out.println("State after switch off: " + etat);
                } else if (isBrisFound && grid[cell.getLigne()][cell.getColonne()] == 'b') {
                    parcoursEquipe = findPathEquipe(grid, etat.getEqPos(), cell);
                    System.out.println("Path to broken conductor: " + parcoursEquipe);
                    solution.addAll(Actions.transformpath(parcoursEquipe));
                    etat.setEqPos(cell);
                    int index = findIndexconducteurbriser(conducteursbriser, cell);
                    Actions.repair(etat, index, solution);
                    System.out.println("Repair conductor at index: " + index);
                    System.out.println("State after repair: " + etat);
                }
            }

            maisonNonConnecter.remove(chemin);
            etat.getMaisonPasconnecter().remove(chemin.getMaison());
            maisonNonConnecter = updatelistmaisonsnonbrancher(maisonNonConnecter, specialNode, etat, grid, interrupteurs, conducteursbriser);
        } else {
            for (Position cell : chemin.getChemin()) {
                if (grid[cell.getLigne()][cell.getColonne()] == 'j') {
                    parcoursEquipe = findPathEquipe(grid, etat.getEqPos(), cell);
                    System.out.println("Path to interrupteur: " + parcoursEquipe);
                    solution.addAll(Actions.transformpath(parcoursEquipe));
                    etat.setEqPos(cell);
                    int index = findIndexinterrupteur(interrupteurs, cell);
                    Actions.switchOn(etat, index, solution);
                    System.out.println("Switch on interrupteur at index: " + index);
                    System.out.println("State after switch on: " + etat);
                    break;
                }
            }
        }

        // Recalculate the special node after actions
        Position newSpecialNode = findClosesNoneConnectedHouse(etat);
        System.out.println("New special node: " + newSpecialNode);

        if (newSpecialNode.equals(specialNode)) {
            System.out.println("Special node did not change, terminating recursion to avoid infinite loop.");
            return solution;
        }

        return astar(grid, etat, maisonNonConnecter, interrupteurs, conducteursbriser, solution, depth + 1);
    }
    private static List<Chemin> updatelistmaisonsnonbrancher(List<Chemin> maisonsNonConnecter, Position interrupteurcommun, State etat, char[][] grid, List<Position> interrupteurs, List<Position> conducteursbriser) {
        List<Chemin> updatedList = new ArrayList<>(maisonsNonConnecter);
        List<Chemin> listmaisonavecmemeinterrupteur = new ArrayList<>();
        for (Chemin chemin : maisonsNonConnecter) {
            for (Position cell2 : chemin.getChemin()) {
                if (cell2.equals(interrupteurcommun)) {
                    listmaisonavecmemeinterrupteur.add(chemin);
                    break;
                }
            }
        }
        for (Chemin maison : listmaisonavecmemeinterrupteur) {
            boolean hasIssue = brisFound(maison.getMaison(), maisonsNonConnecter, conducteursbriser, grid, etat) ||
                               !interrupteurState(maison.getMaison(), maisonsNonConnecter, interrupteurs, grid, etat);

            if (!hasIssue) {
                updatedList.remove(maison);
                etat.getMaisonPasconnecter().remove(maison.getMaison());
                System.out.println("Removing connected house: " + maison.getMaison());
            }
        }
        return updatedList;
    }

    private static int findIndexinterrupteur(List<Position> interrupteurs, Position cell) {
        int index = 0;
        for (Position interrupteur : interrupteurs) {
            if (interrupteur.equals(cell)) {
                break;
            }
            index++;
        }
        return index;
    }

    private static int findIndexconducteurbriser(List<Position> conducteursbriser, Position cell) {
        int index = 0;
        for (Position conducteur : conducteursbriser) {
            if (conducteur.equals(cell)) {
                break;
            }
            index++;
        }
        return index;
    }

    
    


    private static List<Node> getNeighbors(Node node, char[][] grid, Position goal) {
        List<Node> neighbors = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int newL = node.position.getLigne() + dir[0];
            int newC = node.position.getColonne() + dir[1];

            if (newL >= 0 && newL < grid.length && newC >= 0 && newC < grid[0].length) {
                char cell = grid[newL][newC];
                if (cell != '#') {
                    boolean noeudSpecialVisite = node.noeudSpecialVisite || cell == 'x';
                    double gCost = node.gCost + 1;
                    double hCost = heuristic(new Position(newL, newC), goal);

                    if (cell == 'x' && !node.noeudSpecialVisite) {
                        gCost += 0.1;
                    }

                    neighbors.add(new Node(new Position(newL, newC), noeudSpecialVisite, gCost, hCost, node));
                }
            }
        }

        return neighbors;
    }

    private static double heuristic(Position a, Position b) {
        return Math.abs(a.getLigne() - b.getLigne()) + Math.abs(a.getColonne() - b.getColonne()); // Distance de Manhattan
    }

    private static List<Position> constructPath(Node node) {
        List<Position> path = new ArrayList<>();
        while (node != null) {
            path.add(node.position);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    public static char[][] transformGrid(char[][] grid) {
        char[][] newGrid = new char[grid.length][grid[0].length]; // Créer une nouvelle grille pour éviter de modifier l'original

        for (int i = 0; i < newGrid.length; i++) {
            for (int j = 0; j < newGrid[i].length; j++) {
                switch (grid[i][j]) {
                    case ' ':
                    case '*':
                        newGrid[i][j] = '#'; // Obstacle
                        break;
                    case 'i':
                    case 'c':
                    case 'b':
                    case 'j':
                        newGrid[i][j] = 'x'; // Noeud spécial
                        break;
                    default:
                        newGrid[i][j] = grid[i][j]; // Conserve les autres caractères (S, m, #)
                }
            }
        }
        return newGrid;
    }
    public static char[][] transformGridEquipe(char[][] grid) {
        char[][] newGrid = new char[grid.length][grid[0].length]; // Créer une nouvelle grille pour éviter de modifier l'original

        for (int i = 0; i < newGrid.length; i++) {
            for (int j = 0; j < newGrid[i].length; j++) {
                switch (grid[i][j]) {
                    case ' ':
                    case '*':
                    case 'i':
                    case 'c':
                    case 'b':
                    case 'j':
                    case 'm':
                    case 'S':
                        newGrid[i][j] = 'x'; // Noeud spécial
                        break;
                    default:
                        newGrid[i][j] = grid[i][j]; // Conserve les obstacles (#)
                }
            }
        }
        return newGrid;
    }

}
