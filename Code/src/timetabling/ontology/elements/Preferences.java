package timetabling.ontology.elements;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;
import jade.core.AID;
import java.util.ArrayList;

public class Preferences implements Concept {
    
    private AID student;
    private ArrayList<Timeslot> cannotAttend = new ArrayList<>();
    private ArrayList<Timeslot> wouldNotAttend = new ArrayList<>();
    private ArrayList<Timeslot> wouldLikeToAttend = new ArrayList<>();
    private ArrayList<Timeslot> noPreference = new ArrayList<>();

    @Slot(mandatory = true)
    public AID getStudent() {
        return student;
    }

    public void setStudent(AID student) {
        this.student = student;
    }

    @Slot(mandatory = true)
    public ArrayList<Timeslot> getCannotAttend() {
        return cannotAttend;
    }

    public void setCannotAttend(ArrayList<Timeslot> cannotAttend) {
        this.cannotAttend = cannotAttend;
    }

    @Slot(mandatory = true)
    public ArrayList<Timeslot> getWouldNotAttend() {
        return wouldNotAttend;
    }

    public void setWouldNotAttend(ArrayList<Timeslot> wouldNotAttend) {
        this.wouldNotAttend = wouldNotAttend;
    }

    @Slot(mandatory = true)
    public ArrayList<Timeslot> getWouldLikeToAttend() {
        return wouldLikeToAttend;
    }

    public void setWouldLikeToAttend(ArrayList<Timeslot> wouldLikeToAttend) {
        this.wouldLikeToAttend = wouldLikeToAttend;
    }

    @Slot(mandatory = true)
    public ArrayList<Timeslot> getNoPreference() {
        return noPreference;
    }

    public void setNoPreference(ArrayList<Timeslot> noPreference) {
        this.noPreference = noPreference;
    }
    
    

    @Override
    public String toString() {
        String outputWouldLike = "WOULD LIKE:\t|\t";
        String outputWouldNot = "WOULD NOT:\t|\t";
        String outputCannot = "CANNOT:\t\t|\t";
        String outputNoPref = "NO PREFERENCE:\t|\t";
        
        if(this.wouldLikeToAttend != null){
            for (Timeslot t:this.wouldLikeToAttend){
                outputWouldLike +=  t.getDay() + ", " + t.getHour() + "\t|\t";
            }
            outputWouldLike += "\n";
        }

        if(this.wouldNotAttend != null){
            for (Timeslot t:this.wouldNotAttend){
                outputWouldNot +=  t.getDay() + ", " + t.getHour() + "\t|\t";
            }
            outputWouldNot += "\n";
        }

        if(this.cannotAttend != null){
            for (Timeslot t:this.cannotAttend){
                outputCannot +=  t.getDay() + ", " + t.getHour() + "\t|\t";
            }
            outputCannot += "\n";
        }

        if(this.noPreference != null){
            for (Timeslot t:this.noPreference){
                outputNoPref +=  t.getDay() + ", " + t.getHour() + "\t|\t";
            }
            outputNoPref += "\n";
        }
        
        return this.student.getLocalName() + " preferences:\n" + outputWouldLike + outputWouldNot + outputCannot + outputNoPref;
 }


    
    
}
