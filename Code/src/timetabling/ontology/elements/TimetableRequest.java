package timetabling.ontology.elements;

import jade.content.AgentAction;
import jade.content.onto.annotations.Slot;
import jade.core.AID;

public class TimetableRequest implements AgentAction {
    
    private AID student;

    @Slot(mandatory=true)
    public AID getStudent() {
        return student;
    }

    public void setStudent(AID student) {
        this.student = student;
    }        
    
}
