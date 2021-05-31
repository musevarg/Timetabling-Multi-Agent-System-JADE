package timetabling.ontology.elements;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;
import jade.core.AID;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tutorial implements Concept {
   
    private String id;
    private Timeslot timeslot;
    private String module;
    private int numberOfStudents;
    private List<AID> students = new ArrayList<AID>();
    private final int duration = 1;

    @Slot(mandatory = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    @Slot(mandatory = true)
    public int getDuration() {
        return duration;
    }
    
    @Slot(mandatory = true)
    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
    
    @Slot(mandatory = true)
    public Timeslot getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    @Slot(mandatory = true)
    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public List<AID> getStudents() {
        return students;
    }

    public void setStudents(List<AID> students) {
        this.students = students;
    }
    
    public String studentList(){
        if (this.students != null){
            String output = this.id + ": [";
            for(AID aid:this.students){
                output += aid.getLocalName() + ", ";
            }
            output = output.substring(0, output.length()-2) + "]";
            return output;
        } else {
            return "No students enrolled in this tutorial";
        }
    }

    @Override
    public String toString() {
        return this.id + ", " + this.timeslot.getDay() + ", " + this.timeslot.getHour();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
        hash = 79 * hash + Objects.hashCode(this.timeslot);
        hash = 79 * hash + Objects.hashCode(this.module);
        hash = 79 * hash + this.numberOfStudents;
        hash = 79 * hash + Objects.hashCode(this.students);
        hash = 79 * hash + this.duration;
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
        final Tutorial other = (Tutorial) obj;
        if (this.numberOfStudents != other.numberOfStudents) {
            return false;
        }
        if (this.duration != other.duration) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.module, other.module)) {
            return false;
        }
        if (!Objects.equals(this.timeslot, other.timeslot)) {
            return false;
        }
        if (!Objects.equals(this.students, other.students)) {
            return false;
        }
        return true;
    }
    
    
    
        
}