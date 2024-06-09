package actions;
import model.state.State;

import java.util.ArrayList;
import java.util.List;

import model.Position;

// public State (Position eqPos, List<Boolean> interrupteurs, List<Position> briser, List<Position> maisonPasconnecter)
public class Actions {
    public static State moveNorth(State currentState) {
        Position newPos = new Position(currentState.getEqPos().getLigne() - 1, currentState.getEqPos().getColonne());
        return new State(newPos, currentState.getInterrupteurs(), currentState.getBriser(), currentState.getMaisonPasconnecter(), currentState.getConducteursbriser());
    }

    public static State moveSouth(State currentState) {
        Position newPos = new Position(currentState.getEqPos().getLigne() + 1, currentState.getEqPos().getColonne());
        return new State(newPos, currentState.getInterrupteurs(), currentState.getBriser(), currentState.getMaisonPasconnecter(), currentState.getConducteursbriser());
    }

    public static State moveEast(State currentState) {
        Position newPos = new Position(currentState.getEqPos().getLigne(), currentState.getEqPos().getColonne() + 1);
        return new State(newPos, currentState.getInterrupteurs(), currentState.getBriser(), currentState.getMaisonPasconnecter(), currentState.getConducteursbriser());
    }

    public static State moveWest(State currentState) {
        Position newPos = new Position(currentState.getEqPos().getLigne(), currentState.getEqPos().getColonne() - 1);
        return new State(newPos, currentState.getInterrupteurs(), currentState.getBriser(), currentState.getMaisonPasconnecter(), currentState.getConducteursbriser());
    }

    public static State repair(State currentState, int index, List<String> deplacements) {
        List <Boolean> newConductors = deepCopy(currentState.getConducteursbriser());
       for (int i = 0; i < newConductors.size(); i++) {
            if (i == index) {
                newConductors.set(i, false);
                break;
            }
        }
        return new State(currentState.getEqPos(), currentState.getInterrupteurs(), currentState.getBriser(), currentState.getMaisonPasconnecter(), newConductors);
    } 

    public static State switchOn(State currentState,int index,List<String> deplacements) {
        List<Boolean> newInterrupteurs = deepCopy(currentState.getInterrupteurs());
        // Changer l'etat de l'interrupteur à la position de l'index
        for (int i = 0; i < newInterrupteurs.size(); i++) {
            if (i == index) {
                newInterrupteurs.set(i, true);
                break;
            }
        }
        deplacements.add("1");
        
        return new State(currentState.getEqPos(), newInterrupteurs, currentState.getBriser(), currentState.getMaisonPasconnecter(),currentState.getConducteursbriser());
    } 

    public static State switchOff(State currentState, int index, List<String> deplacements) {
        List<Boolean> newInterrupteurs = deepCopy(currentState.getInterrupteurs());
        // Changer l'etat de l'interrupteur à la position de l'index
        for (int i = 0; i < newInterrupteurs.size(); i++) {
            if (i == index) {
                newInterrupteurs.set(i, false);
                break;
            }
        }
        deplacements.add("0");
        return new State(currentState.getEqPos(), newInterrupteurs, currentState.getBriser(),currentState.getMaisonPasconnecter(),currentState.getConducteursbriser());
    } 

    public static List<String> transformpath(List<Position> chemin) {
        List<String> deplacements = new ArrayList<>();
        for (int i = 0; i < chemin.size() - 1; i++) {
            Position current = chemin.get(i);
            Position next = chemin.get(i + 1);
            if (current.getLigne() > next.getLigne()) {
                deplacements.add("N");
            } else if (current.getLigne() < next.getLigne()) {
                deplacements.add("S");
            } else if (current.getColonne() > next.getColonne()) {
                deplacements.add("W");
            } else if (current.getColonne() < next.getColonne()) {
                deplacements.add("E");
            }
        }
        return deplacements;
    
    }

    private static List<Boolean> deepCopy(List<Boolean> array) {
        if (array == null) return null;
        List<Boolean>result = new ArrayList<>();
        for (Boolean a : array) {
            result.add(a);
        }
        return result; 
    }

}
