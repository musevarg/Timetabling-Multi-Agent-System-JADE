package timetabling.ontology.elements;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;
import java.util.ArrayList;
import java.util.List;

public class Module implements Concept {
    
    private int id;
    private String name;
    private List<Tutorial> tutorials = new ArrayList<>();

    @Slot(mandatory = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Slot(mandatory = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Slot(mandatory = true)
    public List<Tutorial> getTutorials() {
        return tutorials;
    }

    public void setTutorials(List<Tutorial> tutorials) {
        this.tutorials = tutorials;
    }
    
    
    
}
