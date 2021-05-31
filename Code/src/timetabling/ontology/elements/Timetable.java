package timetabling.ontology.elements;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;
import jade.core.AID;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Timetable implements Concept {
    
    private AID student;
    private List<Tutorial> tutorials = new ArrayList<>();

    @Slot(mandatory = true)
    public AID getStudent() {
        return student;
    }

    public void setStudent(AID student) {
        this.student = student;
    }

    @Slot(mandatory = true)
    public List<Tutorial> getTutorials() {
        return tutorials;
    }

    public void setTutorials(List<Tutorial> tutorials) {
        this.tutorials = tutorials;
    }

    
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.student);
        hash = 41 * hash + Objects.hashCode(this.tutorials);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Timetable other = (Timetable) obj;
        if (!Objects.equals(this.student, other.student)) {
            return false;
        }
        if (!Objects.equals(this.tutorials, other.tutorials)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        String output = this.student.getLocalName() + " Timetable:\t|\t";
        for(Tutorial t:tutorials){
            output += t.getId() + ", " + t.getTimeslot().getDay() + ", " + t.getTimeslot().getHour() + "\t|\t";
        }
        return output;
    }
    
}
