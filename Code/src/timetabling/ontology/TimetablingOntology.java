package timetabling.ontology;

import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.Ontology;

public class TimetablingOntology extends BeanOntology {
    
    	private static Ontology theInstance = new TimetablingOntology("my_ontology");
	
	public static Ontology getInstance(){
		return theInstance;
	}
	//singleton pattern
	private TimetablingOntology(String name) {
		super(name);
		try {
                    add("timetabling.ontology.elements");
		} catch (BeanOntologyException e) {
			e.printStackTrace();
		}
	}
    
}




	


