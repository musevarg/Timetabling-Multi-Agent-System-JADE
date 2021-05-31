package timetabling.ontology.elements;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;
import java.util.ArrayList;
import java.util.List;

public class MessageBoard implements Concept {
    
    private List<SwapWish> module1Swaps = new ArrayList<>();
    private List<SwapWish> module2Swaps = new ArrayList<>();
    private List<SwapWish> module3Swaps = new ArrayList<>();

    @Slot(mandatory = true)
    public List<SwapWish> getModule1Swaps() {
        return module1Swaps;
    }

    public void setModule1Swaps(List<SwapWish> module1Swaps) {
        this.module1Swaps = module1Swaps;
    }

    @Slot(mandatory = true)
    public List<SwapWish> getModule2Swaps() {
        return module2Swaps;
    }

    public void setModule2Swaps(List<SwapWish> module2Swaps) {
        this.module2Swaps = module2Swaps;
    }

    @Slot(mandatory = true)
    public List<SwapWish> getModule3Swaps() {
        return module3Swaps;
    }

    public void setModule3Swaps(List<SwapWish> module3Swaps) {
        this.module3Swaps = module3Swaps;
    }
    
    public String studentsModule1(){
        String output = "[";
        for(SwapWish sw:module1Swaps){
            output += sw.getStudent().getLocalName() + ", ";
        }
        output = output.substring(0, output.length()-2) + "]";
        return output;
    }
    
        public String studentsModule2(){
        String output = "[";
        for(SwapWish sw:module2Swaps){
            output += sw.getStudent().getLocalName() + ", ";
        }
        output = output.substring(0, output.length()-2) + "]";
        return output;
    }
        
    public String studentsModule3(){
        String output = "[";
        for(SwapWish sw:module3Swaps){
            output += sw.getStudent().getLocalName() + ", ";
        }
        output = output.substring(0, output.length()-2) + "]";
        return output;
    }
    
}
