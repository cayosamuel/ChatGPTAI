package AstarAlgorithm;
import java.util.*;
import model.Position;
import model.state.State;
import AstarAlgorithm.Chemin;
import actions.Actions;




public class AStarAlgorithm {
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

    public static List<String> astar(char[][] grid, State etat, List<Chemin> maisonNonConnecter, List<Position> interrupteurs,List<Position> conducteursbriser,List<String>solutionfinal) {
        List<Position> parcoursEquipe = new ArrayList<>();
        //Reprend la solution qu'il vient de trouver apres avoir connecter une maison
        List<String> solution = solutionfinal;
        Position specialNode = findClosesNoneConnectedHouse(etat);

        // debugger prints to see if the special node is found
        System.out.println("Starting astar algorithm...");
        System.out.println("Special node: " + specialNode);

        // Si un brise est trouvé dans le chemin
        if (brisFound(specialNode, maisonNonConnecter,conducteursbriser, grid, etat)) {
            //Trouver le chemin qui match la position du special node (maison non connectée)
            Chemin chemin = new Chemin(null);
                for (Chemin c : maisonNonConnecter) {
                    if (c.getMaison().equals(specialNode)) {
                        chemin = c;
                        break;
                    } 
                }
                //debugger to see if the chemin is found
                System.out.println("Found chemin: " + chemin);
            //Si un interrupteur est ouvert dans le chemin
            if (interrupteurState(specialNode, maisonNonConnecter,interrupteurs, grid, etat)) {
                //Trouver le chemin qui match la position du special node (maison non connectée)
                    Chemin distanceInterupmaison = chemin;
                    //Pour l'interrupteur ouvert dans le chemin
                    for (Position cell : chemin) {
                            //une fois l'interrupteur trouvé
                        if (grid[cell.getLigne()][cell.getColonne()] == 'i') {
                            parcoursEquipe = findPathEquipe(grid, etat.getEqPos(), cell);
                            //debugger to see the path to the interrupteur
                            System.out.println("Path to interrupteur: " + parcoursEquipe);
                            //Ajoute le chemin à la solution
                            solution.addAll(Actions.transformpath(parcoursEquipe));
                            //Change la position de l'équipe à la position de l'interrupteur dans l'état initial
                            etat.setEqPos(cell);
                            int index = findIndexinterrupteur(interrupteurs, cell);
                            //Fermer l'interrupteur
                            Actions.switchOff(etat, index, solution);
                            //Réparer le conducteur brisé dans le chemin a partir de la position de l'interrupteur
                            for(Position cell2 : distanceInterupmaison.getChemin()) {
                                        //conducteur brisé dans le chemin trouvé
                                if (grid[cell2.getLigne()][cell2.getColonne()] == 'b') {
                                    parcoursEquipe = findPathEquipe(grid, etat.getEqPos(), cell2);
                                    //debugger to see the path to the broken conductor
                                    System.out.println("Path to broken conductor: " + parcoursEquipe);
                                    solution.addAll(Actions.transformpath(parcoursEquipe));
                                    //Change la position de l'équipe à la position du conducteur brisé dans l'état initial
                                    etat.setEqPos(cell2);
                                    int index2 = findIndexconducteurbriser(conducteursbriser, cell2);
                                    Actions.repair(etat, index2, solution);
                                    //Retirer la maison de la liste des maisons non connectées
                                    maisonNonConnecter.remove(chemin);
                                    //Retirer la maison de la liste des maisons non connectées dans l'état
                                    etat.getMaisonPasconnecter().remove(chemin.getMaison());
                                    
                                    //Verifier si de la position du conducteur brisé si de nouvelle maison non connecter sont maintenant connecter
                                    etat.setMaisonPasconnecter(updatelistmaisonsnonbrancher(maisonNonConnecter, cell, etat, grid, interrupteurs, conducteursbriser));
                                    break;
                                }
                                }
                            break;
                        }//Enleve la cellule de la liste distanceInterupmaison
                        else {
                            distanceInterupmaison.getChemin().remove(cell);
                        }
                        
                        distanceInterupmaison.getChemin().remove(cell);
                    }
                    // Recurisivement appeler la fonction pour trouver le chemin de la maison non connectée suivante
                    astar(grid, etat, maisonNonConnecter, interrupteurs, conducteursbriser, solution);
                }
                // si l'interrupteur est fermer
                else {
                for (Position b : chemin) {
                    if (grid[b.getLigne()][b.getColonne()] == 'b') {
                        parcoursEquipe = findPathEquipe(grid, etat.getEqPos(), b);
                        //
                        System.out.println("Path to broken conductor without interrupteur: " + parcoursEquipe);
                        solution.addAll(Actions.transformpath(parcoursEquipe));
                        //Change la position de l'équipe à la position du conducteur brisé dans l'état initial
                        etat.setEqPos(b);
                        int indexconducteur = findIndexconducteurbriser(conducteursbriser, b);
                        Actions.repair(etat, indexconducteur, solution);
                        //Retirer la maison de la liste des maisons non connectées
                        maisonNonConnecter.remove(chemin);
                        //Retirer la maison de la liste des maisons non connectées dans l'état
                        etat.getMaisonPasconnecter().remove(chemin.getMaison());

                     }
                }  // Recurisivement appeler la fonction pour trouver le chemin de la maison non connectée suivante
                astar(grid, etat, maisonNonConnecter, interrupteurs, conducteursbriser, solution);

               }
            } 
            
        return solution;
    }

    private static List<Position> updatelistmaisonsnonbrancher(List<Chemin>maisonsNonConnecter, Position interrupteurcommun, State etat, char[][] grid, List<Position> interrupteurs, List<Position> conducteursbriser){
        List<Position> updatedList = etat.getMaisonPasconnecter();
        //Trouver toutes les chemins des maisonsNonConnecter qui ont la meme position pour interrupteur que la maison qui vient d'etre connecter
        List<Chemin> listmaisonavecmemeinterrupteur = new ArrayList<>();
        for (Chemin chemin : maisonsNonConnecter) {
            for (Position cell2 : chemin) {
                if (cell2.equals(interrupteurcommun)) {
                    listmaisonavecmemeinterrupteur.add(chemin);
                     break;
                }
            }
        }
        //For each house in listmaisonavecmemeinterrupteur, check if there's a broken conductor or closed interrupteur 
        for (Chemin maison : listmaisonavecmemeinterrupteur) {
                boolean hasIssue = brisFound(maison.getMaison(), maisonsNonConnecter,conducteursbriser, grid, etat) ||
                                   !interrupteurState(maison.getMaison(), maisonsNonConnecter,interrupteurs,  grid, etat);

                if (!hasIssue) {
                    //Enlever la maison de la etat.maisonPasconnecter et de la liste des chemin des maisonsNonConnecter
                   maisonsNonConnecter.remove(maison);
                   updatedList.remove(maison.getMaison());
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
