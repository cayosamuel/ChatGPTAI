package model.state;
import java.util.ArrayList;
import java.util.Objects;
import model.Position;
import java.util.List;

public class State {

    private Position eqPos;
    private List<Boolean> interrupteurs;
    private List<Position> briser;
    private List<Position> maisonsNonConnecter; // maisons non connectées. La liste doit être vide pour que l'état soit final soit toutes les maisons sont connectées
    private List<Boolean> conducteursbriser;
    
    

    private State parent;  // État précédent
    private String action; // Action entreprise pour atteindre cet état

    //constructeur
    public State (Position eqPos, List<Boolean> interrupteurs, List<Position> briser, List<Position> maisonPasconnecter, List<Boolean> conducteursbriser){
        this.eqPos = eqPos;
        this.briser = briser;
        this.interrupteurs = new ArrayList<>(interrupteurs);
        this.maisonsNonConnecter = new ArrayList<>(maisonPasconnecter);
        this.conducteursbriser = new ArrayList<>(conducteursbriser);
    
    }

    // Getters
    public Position getEqPos() {
        return eqPos;
    }

    public State getParent() {
        return parent;
    }

    public List<Boolean> getInterrupteurs() {
        return interrupteurs;
    }

    public List<Position> getBriser() {
        return briser;
    }

    public List<Position> getMaisonPasconnecter() {
        return maisonsNonConnecter;
    }
    public List<Boolean> getConducteursbriser() {
        return conducteursbriser;
    }

    public String getAction() {
        return action;
    }

    // Setters

    public void setEqPos(Position eqPos) {
        this.eqPos = eqPos;
    }

    public void setInterrupteurs(List<Boolean> interrupteurs) {
        this.interrupteurs = interrupteurs;
    }

    public void setBriser(List<Position> briser) {
        this.briser = briser;
    }

    public void setMaisonPasconnecter(List<Position> maisonsNonConnecter) {
        this.maisonsNonConnecter = maisonsNonConnecter;
    }
    public void setConducteursbriser(List<Boolean> conducteursbriser) {
        this.conducteursbriser = conducteursbriser;
    }

    public void setParent(State parent) {
        this.parent = parent;
    }


    public void setAction(String action) {
        this.action = action;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return eqPos == state.eqPos &&
               interrupteurs.equals(state.interrupteurs) &&
               maisonsNonConnecter.equals(state.maisonsNonConnecter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eqPos, interrupteurs, maisonsNonConnecter);
    }

}
