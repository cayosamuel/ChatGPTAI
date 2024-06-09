package AstarAlgorithm;

import java.util.List;

import model.Position;
import java.util.ArrayList;



public class Chemin  implements Iterable<Position> {
        private Position source;
        private Position maison;
        private List<Position> chemin;
        private double cout;

        public Chemin(Position source, Position maison, List<Position> chemin) {
            chemin = new ArrayList<>();
            this.source = source;
            this.maison = maison;
            this.chemin = chemin != null ? chemin : new ArrayList<>();
        }

        

        public Chemin (List<Position> chemin){
            this.chemin = chemin;
        }

        public Position getSource() {
            return source;
        }

        public Position getMaison() {
            return maison;
        }


        public List<Position> getChemin() {
            return chemin;
        }

        public double getCout() {
            return cout;
        }

        public void setsource(Position position) {
            this.source = position;
        }

        public void setmaison(Position position) {
            this.maison = position;
        }
        public void setChemin(List<Position> chemin) {
            this.chemin = chemin;
        }
        @Override
        public java.util.Iterator<Position> iterator() {
            return chemin.iterator();
        }
    }
    

