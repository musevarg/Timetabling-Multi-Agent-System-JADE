package timetabling.ontology.elements;

import jade.content.Predicate;
import jade.content.onto.annotations.Slot;
import jade.core.AID;
import java.util.ArrayList;
import java.util.List;

public class ProposeSwaps implements Predicate {
    
    private String id;
    private AID student;
    private List<SwapWish> swapWishes = new ArrayList<>();

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
    public List<SwapWish> getSwapWishes() {
        return swapWishes;
    }

    public void setSwapWishes(List<SwapWish> swapWishes) {
        this.swapWishes = swapWishes;
    }
    
    
    
}
