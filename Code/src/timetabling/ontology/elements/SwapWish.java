package timetabling.ontology.elements;

import jade.content.Concept;
import jade.content.Predicate;
import jade.content.onto.annotations.Slot;
import jade.core.AID;
import java.util.ArrayList;
import java.util.List;

public class SwapWish implements Concept {
    
    private String id;
    private AID student;
    private double utility;
    private List<Tutorial> badTutorials = new ArrayList<>();
    private List<Tutorial> goodTutorials = new ArrayList<>();

    @Slot(mandatory = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
        
    @Slot(mandatory = true)
    public AID getStudent() {
        return student;
    }

    public void setStudent(AID student) {
        this.student = student;
    }

    @Slot(mandatory = true)
    public double getUtility() {
        return utility;
    }

    public void setUtility(double utility) {
        this.utility = utility;
    }
        
    @Slot(mandatory = true)
    public List<Tutorial> getBadTutorials() {
        return badTutorials;
    }

    public void setBadTutorials(List<Tutorial> badTutorials) {
        this.badTutorials = badTutorials;
    }

    @Slot(mandatory = true)
    public List<Tutorial> getGoodTutorials() {
        return goodTutorials;
    }

    public void setGoodTutorials(List<Tutorial> goodTutorials) {
        this.goodTutorials = goodTutorials;
    }

    @Override
    public String toString() {
        String output = "";
        if (!this.badTutorials.isEmpty())
        {
            output = "\n" + this.student.getLocalName() + " would like to swap:\n";
            for(Tutorial t:this.badTutorials){
                output += t.getId() + ", " + t.getTimeslot().getDay() + ", " + t.getTimeslot().getHour() + "\t|\t";
            }
            output += "\nWITH:\n";
            for(Tutorial t:this.goodTutorials){
                output += t.getId() + ", " + t.getTimeslot().getDay() + ", " + t.getTimeslot().getHour() + "\t|\t";
            }
        }
        else
        {
            String module = this.goodTutorials.get(0).getId().substring(1, this.goodTutorials.get(0).getId().length() - 3);
            output = this.student.getLocalName() + " is happy with his timetable for Module " + module;
        }

        return output;
    }
    
    
    
}

