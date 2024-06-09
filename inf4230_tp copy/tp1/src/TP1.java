/*
  UQAM -- Département d'informatique
  INF4230 -- Intelligence artificielle
  Hiver 2020
  TP1 -- Squelette de départ Java
  
  Auteur: Prénom Nom (CODE01019900)
*/

import model.Position;

import java.io.*;
import java.util.*;
import actions.*;
import model.state.*;
import AstarAlgorithm.*;

// Tableau boolean pour interrupteurs et conducteurs. Faut savoir le premier est lequel et le deuxieme est lequel. 
// En dehors de la classe etat : tableau pour interrupteurs et conducteurs.

public class TP1{

    public static void main(String[] args) throws IOException {

        // Initialiser les données

        // Position de l'équipe
        Position equipe = null;

        // Positions des Sources, interrupteurs, conducteurs, bris, maisons et obstacles

        ArrayList<Position> sources = new ArrayList<>();
        ArrayList<Position> interrupteurs = new ArrayList<>();
        ArrayList<Position> conducteurs = new ArrayList<>();
        ArrayList<Position> bris = new ArrayList<>();
        ArrayList<Position> maisons = new ArrayList<>();
        ArrayList<Position> obstacles = new ArrayList<>();

        char[][] grid = null;
        String nomfichiergrille = "/Users/sammycayo/Desktop/TP-AI/inf4230_tp/tp1/tests/grille00.txt";
        //boolean M2 = false;

      /*  for (String a : args) {
            if (a.equalsIgnoreCase("-m2")) M2 = true;
            else {
                if (nomfichiergrille != null) throw new IllegalArgumentException("Trop d'arguments!");
                nomfichiergrille = a;
            }
        }
*/ 
        BufferedReader br = new BufferedReader(new FileReader(nomfichiergrille));

        // Créer un état initial 
        List<Boolean> etatInterrupteurs = new ArrayList<>();
        List<Position> maisonPasconnecter = new ArrayList<>();
        List<Boolean> conducteursbriser = new ArrayList<>();

        

        // Lire la carte et initialiser les données
        List<String> lignes = new ArrayList<>();
        String ligne;
        while ((ligne = br.readLine()) != null) {
            lignes.add(ligne);
        }
        br.close();
        int rows = lignes.size();
        int cols = lignes.get(0).length();
        grid = new char[rows][cols];

        for (int l = 0; l < rows; l++) {
            ligne = lignes.get(l);
            for (int c = 0; c < ligne.length(); c++) {
                grid[l][c] = ligne.charAt(c);
                switch (grid[l][c]) {
                    case '*':
                        equipe = new Position(l, c);
                        break;
                    case 'c':
                        conducteurs.add(new Position(l, c));
                        break;
                    case 'i':
                        interrupteurs.add(new Position(l, c));
                        etatInterrupteurs.add(true);
                        break;
                    case 'S':
                        sources.add(new Position(l, c));
                        break;
                    case 'j':
                        interrupteurs.add(new Position(l, c));
                        etatInterrupteurs.add(false);
                        break;
                    case 'b':
                        bris.add(new Position(l, c));
                        conducteursbriser.add(true);
                        break;
                    case 'm':
                        maisons.add(new Position(l, c));
                        break;
                    case '#':
                        obstacles.add(new Position(l, c));
                        break;
                }
            }
        }
        
        // debugger set up print grid
        printGrid(grid);
        
        List<Chemin> listeChemins = new ArrayList<>();

        // Vérifier si l'équipe a été trouvée
        if (equipe == null) {
            throw new IllegalStateException("La position de l'équipe n'a pas été trouvée dans la grille.");
        }
        // Vérifier s'il y a des sources d'électricité
        if (sources.isEmpty()) {
            throw new IllegalStateException("Il n'y a pas de sources d'électricité dans la grille.");
        }

        // Trouver les maisons non connectées. On vérifie si chaque maison dans les 4 cellules voisines a au moins un conducteur ou une source ou un interrupteur ON
        for (Position maison : maisons) {
            List<Position> neighbors = getNeighbors(maison, grid);
            boolean isConnected = false;
            for (Position neighbor : neighbors) {
                if (grid[neighbor.getLigne()][neighbor.getColonne()] == 'c' || grid[neighbor.getLigne()][neighbor.getColonne()] == 'S' || (grid[neighbor.getLigne()][neighbor.getColonne()] == 'i')) {
                    isConnected = true;
                    break;
                }
            }
            // Si la maison n'est pas connectée, l'ajouter à la liste des maisons non connectées
            if (!isConnected) {
                maisonPasconnecter.add(maison);
            }
        }


        // debbuger print maisonPasconnecter
        for (Position maison : maisonPasconnecter) {
            System.out.println(maison.getLigne() + " 1." + maison.getColonne());
        }
        // Initialiser l'état initial
        State etatInitial = new State(equipe, etatInterrupteurs, bris, maisonPasconnecter, conducteursbriser);
        
        // Trouver le chemin de chaque source vers chaque maison et vérifier si la maison est connectée
        for (Position source : sources) {
            for (Position maison : maisons) {
                List<Position> chemin = AStarAlgorithm.findPath(grid, source, maison);

                // Si le chemin n'est pas trouvé, la maison n'est pas connectée
                if (chemin.isEmpty()) {
                    // Cas d'absence de chemin = cas d'absence de solution. Donc on doit arrêter lancer une exception et arrêter l'exécution
                    throw new IllegalStateException("IMPOSSIBLE");
                } else { // Pour chaque chemin trouvé, vérifier si la maison est non connectée. C'est à dire si dans le chemin on trouve un j ou un b, la maison n'est pas connectée
                    boolean maisonNonConnecter = false;
                    int countInterrupteurs = 0;

                    for (Position cell : chemin) {
                        if (grid[cell.getLigne()][cell.getColonne()] == 'i' || grid[cell.getLigne()][cell.getColonne()] == 'j') {
                            countInterrupteurs++;
                        }

                        if (grid[cell.getLigne()][cell.getColonne()] == 'j' || grid[cell.getLigne()][cell.getColonne()] == 'b') {
                            maisonNonConnecter = true;
                            break;
                        }
                    }
                    if (maisonNonConnecter) {
                        maisonPasconnecter.add(maison);
                        listeChemins.add(new Chemin(source, maison, chemin));
                    }
                    if (countInterrupteurs == 0) {
                        throw new IllegalStateException("IMPOSSIBLE");
                    }
                }
            }
        }
        //debugger print maisonPasconnecter This prints twice for some reason
        for (Position maison : maisonPasconnecter) {
            System.out.println(maison.getLigne() + " 2." + maison.getColonne());
        }


        // Exécuter l'algorithme A*
        List<String> solution = AStarAlgorithm.astar(grid, etatInitial, listeChemins, interrupteurs, conducteurs, new ArrayList<>());

        //debugger print solution
        printSolution(solution);

        // Afficher la solution
        for (String action : solution) {
            System.out.print(action + " ");
        }
        System.out.println();
    }

    public static void printGrid(char[][] grid) {
        for (char[] row : grid) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    public static void printSolution(List<String> solution) {
        for (String action : solution) {
            System.out.print(action + " solution");
        }
        System.out.println();
    }

    


    
    
    public static void deplacer(Position origine, Position dest){
        if(origine.getLigne() < dest.getLigne())
            for(int l = origine.getLigne(); l< dest.getLigne(); l++)
                System.out.print("S ");
        if(origine.getLigne() > dest.getLigne())
            for(int l = origine.getLigne(); l> dest.getLigne(); l--)
                System.out.print("N ");
        if(origine.getColonne() < dest.getColonne())
            for(int c = origine.getColonne(); c< dest.getColonne(); c++)
                System.out.print("E ");
        if(origine.getColonne() > dest.getColonne())
            for(int c = origine.getColonne(); c> dest.getColonne(); c--)
                System.out.print("W ");
    }

    public static List<Position> getNeighbors(Position maisons, char[][] grid) {
        List<Position> neighbors = new ArrayList<>();
        Position pos = maisons;

        if (isValidPosition(new Position(pos.getLigne() - 1, pos.getColonne()), grid))
            neighbors.add(pos);
        if (isValidPosition(new Position(pos.getLigne() + 1, pos.getColonne()), grid))
            neighbors.add(pos);
        if (isValidPosition(new Position(pos.getLigne(), pos.getColonne() + 1), grid))
            neighbors.add(pos);
        if (isValidPosition(new Position(pos.getLigne(), pos.getColonne() - 1), grid))
            neighbors.add(pos);
        return neighbors;
    }


    public static List<State> getNeighbors(State current, char[][] grid) {
        List<State> neighbors = new ArrayList<>();
        Position pos = current.getEqPos();

        if (isValidPosition(new Position(pos.getLigne() - 1, pos.getColonne()), grid))
            neighbors.add(Actions.moveNorth(current));
        if (isValidPosition(new Position(pos.getLigne() + 1, pos.getColonne()), grid))
            neighbors.add(Actions.moveSouth(current));
        if (isValidPosition(new Position(pos.getLigne(), pos.getColonne() + 1), grid))
            neighbors.add(Actions.moveEast(current));
        if (isValidPosition(new Position(pos.getLigne(), pos.getColonne() - 1), grid))
            neighbors.add(Actions.moveWest(current));
        return neighbors;
    }

    public static boolean isValidPosition(Position pos, char[][] grid) {
        if (pos.getLigne() < 0 || pos.getLigne() >= grid.length || pos.getColonne() < 0 || pos.getColonne() >= grid[0].length) {
            return false;
        }
        return grid[pos.getLigne()][pos.getColonne()] != '#';
    }

    /* 
    public static boolean allHousesPowered(State state, char[][] grid) {
        // Dimensions de la grille
        int rows = grid.length;
        int cols = grid[0].length;

        // Marquer les cellules visitées
        boolean[][] visited = new boolean[rows][cols];

        // Liste des positions des sources d'électricité
        List<Position> sources = new ArrayList<>();

        // Trouver toutes les sources d'électricité
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 'S') {
                    sources.add(new Position(i, j));
                }
            }
        }

        // Effectuer un BFS à partir de chaque source d'électricité
        Queue<Position> queue = new LinkedList<>();
        for (Position source : sources) {
            queue.add(source);
            visited[source.getLigne()][source.getColonne()] = true;
        }

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            int x = current.getLigne();
            int y = current.getColonne();

            // Vérifier les 4 directions (N, S, E, W)
            int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (newX >= 0 && newX < rows && newY >= 0 && newY < cols && !visited[newX][newY]) {
                    char cell = grid[newX][newY];
                    // Si c'est un conducteur sain ou une maison
                    if (cell == 'c' || cell == 'm') {
                        queue.add(new Position(newX, newY));
                        visited[newX][newY] = true;
                    }
                    // Si c'est un interrupteur ON et alimenté
                    else if ((cell == 'i' || cell == 'j') && state.getInterrupteurs()[newX][newY]) {
                        queue.add(new Position(newX, newY));
                        visited[newX][newY] = true;
                    }
                }
            }
        }

        // Vérifier si toutes les maisons sont visitées
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 'm' && !visited[i][j]) {
                    return false; // Une maison n'est pas alimentée
                }
            }
        }

        return true; // Toutes les maisons sont alimentées
    }




    public static List<String> reconstructPath(State goalState) {
        List<String> path = new ArrayList<>();
        State current = goalState;
        while (current != null) {
            path.add(current.getAction());
            current = current.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    public static boolean[][] initialInterrupteurs(char[][] grid) {
        // Initialiser l'état des interrupteurs
        return new boolean[grid.length][grid[0].length];
    }

    public static boolean[][] initialConductors(char[][] grid) {
        // Initialiser l'état des conducteurs
        return new boolean[grid.length][grid[0].length];
    }

    public static int heuristic(State state, char[][] grid) {
        // Liste des maisons qui doivent être alimentées
        List<Position> houses = new ArrayList<>();
        // Liste des interrupteurs qui sont dans l'état OFF
        List<Position> offInterrupters = new ArrayList<>();
        // Liste des conducteurs brisés
        List<Position> brokenConductors = new ArrayList<>();

        // Parcourir la grille pour trouver les maisons, interrupteurs OFF et conducteurs brisés
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                char cell = grid[i][j];
                switch (cell) {
                    case 'm':
                        houses.add(new Position(i, j));
                        break;
                    case 'j':
                        offInterrupters.add(new Position(i, j));
                        break;
                    case 'b':
                        brokenConductors.add(new Position(i, j));
                        break;
                }
            }
        }

        // Calculer la distance de Manhattan entre la position actuelle de l'équipe et chaque maison
        int totalDistance = 0;
        for (Position house : houses) {
            totalDistance += manhattanDistance(state.getEqPos(), house);
        }

        // Ajouter la distance pour atteindre les interrupteurs OFF
        for (Position interrupter : offInterrupters) {
            totalDistance += manhattanDistance(state.getEqPos(), interrupter);
        }

        // Ajouter la distance pour atteindre les conducteurs brisés
        for (Position broken : brokenConductors) {
            totalDistance += manhattanDistance(state.getEqPos(), broken);
        }

        return totalDistance;
    }

    public static int manhattanDistance(Position p1, Position p2) {
        return Math.abs(p1.getLigne() - p2.getLigne()) + Math.abs(p1.getColonne() - p2.getColonne());
    }

*/

}
