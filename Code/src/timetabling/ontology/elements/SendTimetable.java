package timetabling.ontology.elements;

import jade.content.Predicate;
import jade.content.onto.annotations.Slot;
import jade.core.AID;

public class SendTimetable implements Predicate {
    
    private AID student;
    private Timetable timetable;

    @Slot(mandatory=true)
    public AID getStudent() {
        return student;
    }

    public void setStudent(AID student) {
        this.student = student;
    }

    @Slot(mandatory=true)
    public Timetable getTimetable() {
        return timetable;
    }

    public void setTimetable(Timetable timetable) {
        this.timetable = timetable;
    }
    
    
}
