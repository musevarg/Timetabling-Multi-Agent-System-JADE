package timetabling.main;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ReceiverBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import timetabling.ontology.TimetablingOntology;
import timetabling.ontology.elements.*;

public class StudentAgent extends Agent {
    
    private final Codec codec = new SLCodec();
    private final Ontology ontology = TimetablingOntology.getInstance();
    private Timetable timetable;
    private Preferences preferences;
    private AID timetablingAgent;
    private AID[] studentAgents;
    private boolean updateTimetable = true;
    private List<Module> modules = SET1011140430624Coursework.modules;
    private SwapWish[] swapWishes = new SwapWish[3]; // one slot per module
    private double utility;
    private int round = 1;
    
    // put some color in the console
    private final String ANSI_GREEN = "\u001B[32m";
    private final String ANSI_RESET = "\u001B[0m";

    @Override
    protected void setup(){
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);
        
        // Get preferences from args
        Preferences[] args = (Preferences[])this.getArguments();    
        preferences = args[0];
        preferences.setStudent(getAID());
        System.out.println(preferences);
                
        // Register the student in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Student");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        
        //System.out.println(getLocalName() + " created.");
        
        doWait(10000);
        
        SequentialBehaviour sb = new SequentialBehaviour();
	//sub-behaviours will execute in the order they are added
	sb.addSubBehaviour(new GetTimetablingAgent(this));
	sb.addSubBehaviour(new RequestTimetable(this));
        sb.addSubBehaviour(new SwapRequestResponseListener(this, 5000));
        addBehaviour(sb);
        
        
        
        
    }
    
    
    // Get AID of Timetabling Agent to communicate with it
    private class GetTimetablingAgent extends OneShotBehaviour {
        
        private AID[] timetablingAgents;
        
        private GetTimetablingAgent(StudentAgent agent) {
            super(agent);
        }

        @Override
        public void action() {
            // Update the list of timetabling agents
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("Timetabling");
            template.addServices(sd);

            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);
                timetablingAgents = new AID[result.length];
                for (int i = 0; i < result.length; ++i) {
                    timetablingAgents[i] = result[i].getName();
                }
                timetablingAgent = timetablingAgents[0];
             }
             catch (FIPAException fe) {
                fe.printStackTrace();
             }
        }

    }
    
    // Ask the timetabling agent for the timetable
    private class RequestTimetable extends OneShotBehaviour{
               
        private RequestTimetable(StudentAgent agent) {
            super(agent);
        }
        
        @Override
        public void action() {
            try{
                updateTimetable = true;
                
                // Build timetable request Agent Action
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.setLanguage(codec.getName());
                msg.setOntology(ontology.getName());
                msg.setConversationId("Timetable Request " + getAID().getLocalName());
                msg.addReceiver(timetablingAgent);
                
                TimetableRequest tr = new TimetableRequest();
                tr.setStudent(getAID());
                
                Action request = new Action();
                request.setAction(tr);
                request.setActor(timetablingAgent); 
                
                getContentManager().fillContent(msg, request);
                
                send(msg);
                
                addBehaviour(new ReceiveTimetable((StudentAgent) myAgent));
            } catch(Exception e){
                System.out.println("StudentAgent.java:");
                e.printStackTrace();
            }      
        }        
    }
    

    
    // Receive the timetable from the timetabling agent
    private class ReceiveTimetable extends CyclicBehaviour {
                
        private ReceiveTimetable(StudentAgent agent) {
            super(agent);
        }

        @Override
        public void action() {
            if (updateTimetable){
                try{
                    MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                    ACLMessage msg = myAgent.receive(mt);
                    if(msg != null){
                        if(msg.getPerformative() == ACLMessage.INFORM) {
                            ContentElement content = getContentManager().extractContent(msg);
                            SendTimetable req = (SendTimetable) content;
                            if (timetable == null){
                                timetable = req.getTimetable();
                                updateTimetable = false;
                                utility = utility();
                                System.out.println(timetable.toString() + " (Utility: " + utility + ")");
                                addBehaviour(new DecideSwapProposals((StudentAgent) myAgent, round));
                                removeBehaviour(this);
                            } else {
                                if (!timetable.equals(req.getTimetable())){
                                    timetable = req.getTimetable();
                                    utility = utility();
                                    updateTimetable = false;
                                    System.out.println(timetable.toString() + " (Utility: " + utility() + ")");
                                    removeBehaviour(this);
                                }
                            }
                        }
                    }
                } catch (Exception e){
                    System.out.println("StudentAgent.java");
                    e.printStackTrace();
                }
            }
        }
    }
        
           
    // Check given Timetable VS preferences to identify bad slots
    // and if other Tutorial slots match a better option for the student
    // The utlit set below is the module utility, not the overall utility, and is
    // used by the timetabling agent to know who to prioritise
    private class DecideSwapProposals extends OneShotBehaviour  {
        
        private int round;
        
        private DecideSwapProposals(StudentAgent agent, int round) {
            super(agent);
            this.round = round;
        }

        @Override
        public void action() {
                   
            doWait(5000);

            swapWishes[0] = new SwapWish();
            swapWishes[0].setStudent(getAID());
            swapWishes[0].setId("Propose Swap " + getAID().getLocalName());
            swapWishes[0].setUtility(utility);
            swapWishes[1] = new SwapWish();
            swapWishes[1].setStudent(getAID());
            swapWishes[1].setId("Propose Swap " + getAID().getLocalName());
            swapWishes[1].setUtility(utility);
            swapWishes[2] = new SwapWish();
            swapWishes[2].setStudent(getAID());
            swapWishes[2].setId("Propose Swap " + getAID().getLocalName());
            swapWishes[2].setUtility(utility);

            
            for(Timeslot ts:preferences.getCannotAttend()){
                for (Tutorial tut:timetable.getTutorials()){
                    if (ts.equals(tut.getTimeslot())){
                        String tutID = tut.getId().substring(0, tut.getId().length() - 3);
                        switch(tutID){
                            case "M1":
                                swapWishes[0].getBadTutorials().add(tut);
                                break;
                            case "M2":
                                swapWishes[1].getBadTutorials().add(tut);
                                break;
                            case "M3":
                                swapWishes[2].getBadTutorials().add(tut);
                                break;
                        }
                    }
                }
            }



            if (round == 2){

                for(Timeslot ts:preferences.getWouldNotAttend()){
                    for (Tutorial tut:timetable.getTutorials()){
                        if (ts.equals(tut.getTimeslot())){
                            String tutID = tut.getId().substring(0, tut.getId().length() - 3);
                            switch(tutID){
                                case "M1":
                                    swapWishes[0].getGoodTutorials().add(tut);
                                    break;
                                case "M2":
                                    swapWishes[1].getGoodTutorials().add(tut);
                                    break;
                                case "M3":
                                    swapWishes[2].getGoodTutorials().add(tut);
                                    break;
                            }
                        }
                    }
                }

            }



            for(Timeslot ts:preferences.getWouldLikeToAttend()){
                for(Module m:modules){
                    for (Tutorial tut:m.getTutorials()){
                        if (ts.equals(tut.getTimeslot())){
                            switch(m.getId()){
                                case 1:
                                    swapWishes[0].getGoodTutorials().add(tut);
                                    break;
                                case 2:
                                    swapWishes[1].getGoodTutorials().add(tut);
                                    break;
                                case 3:
                                    swapWishes[2].getGoodTutorials().add(tut);
                                    break;
                            }
                        }
                    }
                }
            }

            for(Timeslot ts:preferences.getNoPreference()){
                for(Module m:modules){
                    for (Tutorial tut:m.getTutorials()){
                        if (ts.equals(tut.getTimeslot())){
                            switch(m.getId()){
                                case 1:
                                    swapWishes[0].getGoodTutorials().add(tut);
                                    break;
                                case 2:
                                    swapWishes[1].getGoodTutorials().add(tut);
                                    break;
                                case 3:
                                    swapWishes[2].getGoodTutorials().add(tut);
                                    break;
                            }
                        }
                    }
                }
            }

            addBehaviour(new BroadcastSwapRequest((StudentAgent) myAgent));
        }
               
    }

    // Broadcast a swap request to the message board
    private class BroadcastSwapRequest extends OneShotBehaviour {
               
        private BroadcastSwapRequest(StudentAgent agent) {
            super(agent);
        }

        @Override
        public void action() {
            try{
                ProposeSwaps ps = new ProposeSwaps();
                ps.setStudent(getAID());
                ps.setId("Propose Swap " + getAID().getLocalName());
                for(int i=0; i<swapWishes.length; ++i){
                    if(swapWishes[i] != null){
                        if (!swapWishes[i].getBadTutorials().isEmpty())
                        {
                            ps.getSwapWishes().add(swapWishes[i]);
                        }
                    } 
                }
                // Build propose message for the message board in 
                // the Timetabling Agent and let other students know
                // which tutorials would like to be swaped with which
                ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
                msg.setLanguage(codec.getName());
                msg.setOntology(ontology.getName());
                msg.setConversationId(ps.getId());
                getContentManager().fillContent(msg, ps); 
                msg.addReceiver(timetablingAgent);
                send(msg);
                //System.out.println(getAID().getLocalName() + " Swap Request Sent");
             
            } catch(Exception e){
                System.out.println("StudentAgent.java:");
                e.printStackTrace();
            } 
        }
    }
    
    
    private class SwapRequestResponseListener extends TickerBehaviour {
        
        private SwapRequestResponseListener(StudentAgent agent, long duration) {
            super(agent, duration);
        }

        @Override
        protected void onTick() {
                try{
                    MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
                                                            MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL));
                    ACLMessage msg = myAgent.receive(mt);
                    if(msg != null){
                        String module = msg.getContent();
                        if(msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                            System.out.println(ANSI_GREEN + getAID().getLocalName() + " received succesful swap response for " + module + ANSI_RESET);
                            addBehaviour(new RequestTimetable((StudentAgent) myAgent));
                        } else if(msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                            System.out.println(getAID().getLocalName() + " received unsuccesful swap response for " + module + " (Utility: " + utility + ")");
                            if (round==1){
                                round=2;
                                addBehaviour(new DecideSwapProposals((StudentAgent) myAgent, round));
                            }
                        }
                    }
                } catch (Exception e){
                    System.out.println("StudentAgent.java");
                    e.printStackTrace();
                }
        }
    
    }
    
    // Calculates this student's utilty
    private double utility(){
        double utility = 0;
        
        for(Tutorial tut:timetable.getTutorials()){
            for (Timeslot t:preferences.getNoPreference()){
                if(t.equals(tut.getTimeslot())){
                    utility++;
                }
            }
        }

        for(Tutorial tut:timetable.getTutorials()){
            for (Timeslot t:preferences.getWouldLikeToAttend()){
                if(t.equals(tut.getTimeslot())){
                    utility++;
                }
            }
        }
        for(Tutorial tut:timetable.getTutorials()){
            for (Timeslot t:preferences.getWouldNotAttend()){
                if(t.equals(tut.getTimeslot())){
                    utility+=0.25;
                }
            }
        }
        
        return utility;
    }
}
