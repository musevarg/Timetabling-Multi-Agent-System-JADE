package timetabling.main;

import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import timetabling.ontology.TimetablingOntology;
import timetabling.ontology.elements.*;

public class TimetablingAgent extends Agent {
    
    private final Codec codec = new SLCodec();
    private final Ontology ontology = TimetablingOntology.getInstance();
    private AID timetablingAID;
    private AID[] studentAgents;
    private List<Module> modules = SET1011140430624Coursework.modules;
    private MessageBoard messageBoard = new MessageBoard();
    private List<Timetable> timetables = new ArrayList<>();
    SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private int[] moduleSwaps = new int[3];
    private int[] noChangeCounter = new int[]{0,0,0};
    private boolean keepGoing = true;
    
    @Override
    protected void setup(){
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);
        timetablingAID = new AID("Timetabling", AID.ISLOCALNAME);
        
        // Register the timetabling agent in the yellow pages, so students can find it
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Timetabling");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        
        // Wait 10 seconds for Student Agents to be created
        doWait(15000);
        
        SequentialBehaviour sb = new SequentialBehaviour();
        
        // Add a Behaviour that checks the DF for students
        sb.addSubBehaviour(new GetStudentAgents(this));
        // Add a Behaviour that creates a timetable for all students
        // Without taking care of preferences
        sb.addSubBehaviour(new MakeInitialTimetable(this, 3));
        // Add a Behaviour that handles Timetable Requests from Student Agents
        sb.addSubBehaviour(new HandleTimetableRequest(this));
        
        addBehaviour(sb);
        // Add a Behaviour that listens for swap requests from Student Agents
        // and broadcast them to the Message board Concept
        addBehaviour(new PopulateMessageBoard(this));
        
        addBehaviour(new CheckSwapRequestsModule1(this, 6000));
        addBehaviour(new CheckSwapRequestsModule2(this, 6000));
        addBehaviour(new CheckSwapRequestsModule3(this, 6000));
        
        
        System.out.println(getLocalName() + " created.");
    }
        
    private String printArray(AID[] studentAgents){
        String output = "[";
        for (AID aid:studentAgents){
            output += aid.getLocalName() + ", ";
        }
        output = output.substring(0, output.length()-2);
        output += "]";
        return output;
    }
    
   
    private class GetStudentAgents extends OneShotBehaviour {

        private GetStudentAgents(TimetablingAgent agent) {
            super(agent);
        }

        @Override
        public void action() {
            // Update the list of student agents
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("Student");
            template.addServices(sd);

            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);
                studentAgents = new AID[result.length];
                for (int i = 0; i < result.length; ++i) {
                    studentAgents[i] = result[i].getName();
                }
                System.out.println("List of students (" + studentAgents.length + "): " + printArray(studentAgents));
             }
             catch (FIPAException fe) {
                fe.printStackTrace();
             }
                    
        }
        
    }
    
    //Important: this only works if each module has the same number of tutorials
    private class MakeInitialTimetable extends OneShotBehaviour {

        private final int numberOfTutorials;
        private int maxStudents;
        private int leftoverStudents;
        
        private MakeInitialTimetable(TimetablingAgent agent, int numberOfTutorials) {
            super(agent);
            this.numberOfTutorials = numberOfTutorials;
        }

        @Override
        public void action() {          
            
            // Assign students to tutorials
            for(int m=0; m<modules.size(); ++m){
                
                // Calculate maximum number of students per tutorial to ensure balanced groups
                maxStudents = studentAgents.length/modules.get(m).getTutorials().size();
                leftoverStudents = studentAgents.length-(maxStudents*modules.get(m).getTutorials().size());
                
                for (int t=0; t<modules.get(m).getTutorials().size(); ++t){
                    for(int i=0; i<maxStudents; ++i){
                        modules.get(m).getTutorials().get(t).getStudents().add(studentAgents[i+maxStudents*t]);
                    }
                }
                
                if(leftoverStudents!=0){
                      for (int t=0; t<leftoverStudents; ++t){
                          modules.get(m).getTutorials().get(t).getStudents().add(studentAgents[studentAgents.length-t-1]);
                      }   
                }
            }
                            
            // Print number of students per tutorial
            for(Module m:modules){
                String output = "Students in each tutorial for " + m.getName() + ": ";
                for(Tutorial t:m.getTutorials()){
                    output += t.getStudents().size() + " ";
                }
                System.out.println(output);
            }

            // Create Student Timetables and keep a copy of them
            for(AID aid:studentAgents){
                timetables.add(createStudentTimetable(aid));
            }
                                
        }
        
    }
    
    private class HandleTimetableRequest extends CyclicBehaviour {
        
        private HandleTimetableRequest(TimetablingAgent agent) {
            super(agent);
        }
        
        @Override
        public void action() {
                       
            try {            
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = myAgent.receive(mt); 

                if(msg != null) {
                    if(msg.getPerformative() == ACLMessage.REQUEST) {

                        ContentElement ce = getContentManager().extractContent(msg);
                        if(ce instanceof Action) {
                                Concept action = ((Action)ce).getAction();
                                
                                if (action instanceof TimetableRequest) {
                                    
                                    SendTimetable req = new SendTimetable();
                                    req.setStudent(msg.getSender());
                                    req.setTimetable(timetables.get(studentTimetable(req.getStudent())));
                                    
                                    ACLMessage reply = msg.createReply();
                                    reply.setPerformative(ACLMessage.INFORM);
                                    reply.setLanguage(codec.getName());
                                    reply.setOntology(ontology.getName());
                                    getContentManager().fillContent(reply, req);
                                    send(reply);
                                }
                                
                                //System.out.println("Received timetable request from " + req.getStudent().getLocalName());
                        }
                    }
                }
                else{
                    block();
                }
            } catch (Exception e){
                System.out.println("TimetablingAgent.java: " + e);
                e.printStackTrace();
            }
            
        }    
    }
    
    private Timetable createStudentTimetable(AID studentID){
        
        Timetable t = new Timetable();
        t.setStudent(studentID);
        
        for(Module m:modules){
            for(Tutorial tut:m.getTutorials()){
                for(AID aid:tut.getStudents()){
                   if (studentID.getLocalName().equals(aid.getLocalName())){
                       t.getTutorials().add(tut);
                   }
                }   
            }
        }

        //System.out.println(t.getStudent().getLocalName() + " " + t.getTutorials());
        return t;
    }
    
    private class PopulateMessageBoard extends CyclicBehaviour {
        
        private PopulateMessageBoard(TimetablingAgent agent) {
            super(agent);
        }

        @Override
        public void action() {
             try {            
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
                ACLMessage msg = myAgent.receive(mt); 

                if(msg != null) {
                    if(msg.getPerformative() == ACLMessage.PROPOSE) {
                        ContentElement content = getContentManager().extractContent(msg);
                        ProposeSwaps swap = (ProposeSwaps) content;
                        
                        boolean same = false;
                                               
                        for(int i=0; i<swap.getSwapWishes().size(); ++i){
                            String module = swap.getSwapWishes().get(i).getBadTutorials().get(0).getModule();
                            switch(module){
                                case "Module1":
                                    same = false;
                                    for(SwapWish sw:messageBoard.getModule1Swaps()){
                                        if(sw.getId().equals(swap.getSwapWishes().get(i).getId())){
                                            sw.setGoodTutorials(swap.getSwapWishes().get(i).getGoodTutorials());
                                            System.out.println("Updated Swap Request from " + swap.getStudent().getLocalName());
                                            same = true;
                                        }
                                    }
                                    if (!same){
                                        messageBoard.getModule1Swaps().add(swap.getSwapWishes().get(i));
                                        System.out.println("Received Swap Request from " + swap.getStudent().getLocalName());
                                    }  
                                    break;
                                case "Module2":
                                    same = false;
                                    for(SwapWish sw:messageBoard.getModule2Swaps()){
                                        if(sw.getId().equals(swap.getSwapWishes().get(i).getId())){
                                            sw.setGoodTutorials(swap.getSwapWishes().get(i).getGoodTutorials());
                                            System.out.println("Updated Swap Request from " + swap.getStudent().getLocalName());
                                            same = true;
                                        }
                                    }
                                    if (!same){
                                        messageBoard.getModule2Swaps().add(swap.getSwapWishes().get(i));
                                        System.out.println("Received Swap Request from " + swap.getStudent().getLocalName());
                                    }  
                                    break;
                                case "Module3":
                                    same = false;
                                    for(SwapWish sw:messageBoard.getModule3Swaps()){
                                        if(sw.getId().equals(swap.getSwapWishes().get(i).getId())){
                                            sw.setGoodTutorials(swap.getSwapWishes().get(i).getGoodTutorials());
                                            System.out.println("Updated Swap Request from " + swap.getStudent().getLocalName());
                                            same = true;
                                        }
                                    }
                                    if (!same){
                                        messageBoard.getModule3Swaps().add(swap.getSwapWishes().get(i));
                                        System.out.println("Received Swap Request from " + swap.getStudent().getLocalName());
                                    }  
                                    break;
                            } 
                        }
                    }
                }
                else{
                    block();
                }
            } catch (Exception e){
                System.out.println("TimetablingAgent.java: " + e);
                e.printStackTrace();
            }
        }
        
    }
    
    // Return a student timetable
    private int studentTimetable(AID student){
        
        for(int i=0; i<timetables.size(); ++i){
            if (timetables.get(i).getStudent().getLocalName().equals(student.getLocalName())){
                return i;
            }
        }
        return 666;
    }
    
    // Sort swapWishes based on student utility
    private List<SwapWish> orderedSwapWishes(List<SwapWish> list){
        int n = list.size();  
        SwapWish tempSw = new SwapWish();  
         for(int i=0; i < n; ++i){  
                 for(int j=1; j < (n-i); ++j){  
                          if(list.get(j-1).getUtility() > list.get(j).getUtility()){  
                                 //swap elements  
                                 tempSw = list.get(j-1);                              
                                 list.set(j-1, list.get(j));  
                                 list.set(j, tempSw);  
                         }  
                 }  
         }  
        
        return list;
    }
    
    
    // Perform Swaps for Module 1
    private class CheckSwapRequestsModule1 extends TickerBehaviour {
        
        private CheckSwapRequestsModule1(TimetablingAgent agent, long duration) {
            super(agent, duration);
        }

        @Override
        protected void onTick() {
                     
            messageBoard.setModule1Swaps(orderedSwapWishes(messageBoard.getModule1Swaps()));
            
            int s = messageBoard.getModule1Swaps().size();
            int s2=666;
            System.out.println("Module 1 Message Board Size: " + s);
                                    
            AID student1 = new AID();
            AID student2 = new AID();
            SwapWish sw1 = new SwapWish();
            SwapWish sw2 = new SwapWish();
            
            if (s>1){
                boolean foundSwap = false;
                while(!foundSwap){
           
                    for(int i=0; i<s; i++){
                        for(int j=0; j<s; ++j){

                            sw1 = messageBoard.getModule1Swaps().get(i);
                            sw2 = messageBoard.getModule1Swaps().get(j);
                            student1 = messageBoard.getModule1Swaps().get(i).getStudent();
                            student2 = messageBoard.getModule1Swaps().get(j).getStudent();
                            
                            if (!student1.equals(student2)){
                                
                                // Compare good and bad tutorials in two different student agents
                                for (Tutorial t1:sw1.getGoodTutorials()){
                                    if (sw2.getBadTutorials().get(0).equals(t1)){

                                        for (Tutorial t2:sw2.getGoodTutorials()){
                                            if (sw1.getBadTutorials().get(0).equals(t2)){

                                                System.out.println("Timetabling Agent: I can swap " + student1.getLocalName() + " " + t2.toString() + " WITH " + student2.getLocalName() + " " + t1.toString());

                                                int tt1 = studentTimetable(student1);
                                                int tt2 = studentTimetable(student2);

                                                //System.out.println(t1.studentList());
                                                //System.out.println(t2.studentList());
                                                List<Tutorial> temp = new ArrayList<>();
                                                for(Tutorial t:timetables.get(tt1).getTutorials()){
                                                    if(t.getModule().equals(t1.getModule())){
                                                        temp.add(t1);
                                                    } else {
                                                        temp.add(t);
                                                    }
                                                }
                                                timetables.get(tt1).setTutorials(temp);
                                                
                                                temp = new ArrayList<>();
                                                for(Tutorial t:timetables.get(tt2).getTutorials()){
                                                    if(t.getModule().equals(t2.getModule())){
                                                        temp.add(t2);
                                                    } else {
                                                        temp.add(t);
                                                    }
                                                }
                                                timetables.get(tt2).setTutorials(temp);
                                                //timetables.get(tt1).setModule1Tutorial(t1);
                                                //timetables.get(tt2).setModule1Tutorial(t2);
                                                t1.getStudents().remove(student2);
                                                t1.getStudents().add(student1);
                                                t2.getStudents().remove(student1);
                                                t2.getStudents().add(student2);
                                                //System.out.println(t1.studentList());
                                                //System.out.println(t2.studentList());

                                                //System.out.println(student1.getLocalName() + ": " + timetables.get(tt1));
                                                //System.out.println(student2.getLocalName() + ": " + timetables.get(tt2));
                                                
                                                System.out.println("MODULE 1 CHECK/  " + student1.getLocalName() + " in only one tutorial: " + studentIsInOneTutorial(student1, modules.get(0)));
                                                System.out.println("MODULE 1 CHECK/  " + student2.getLocalName() + " in only one tutorial: " + studentIsInOneTutorial(student2, modules.get(0)));

                                                messageBoard.getModule1Swaps().remove(sw1);
                                                messageBoard.getModule1Swaps().remove(sw2);

                                                try{
                                                    send(swapReply(student1, ACLMessage.ACCEPT_PROPOSAL, t1.getModule(), sw1.getId()));
                                                    send(swapReply(student2, ACLMessage.ACCEPT_PROPOSAL, t2.getModule(), sw2.getId()));
                                                } catch(Exception e){e.printStackTrace();}  

                                                foundSwap = true;
                                                moduleSwaps[0]++;
                                                break;
                                            }
                                        }
                                    } else {

                                    }
                                    if(foundSwap){break;}
                                }
                                
                            }
                            if(foundSwap){break;}
                        }
                        if(foundSwap){break;}
                    }
                    s2 = messageBoard.getModule1Swaps().size();
                    if(s==s2){
                        noChangeCounter[0]++;
                        for(SwapWish sw:messageBoard.getModule1Swaps()){
                            try{
                                send(swapReply(sw.getStudent(), ACLMessage.REJECT_PROPOSAL, sw.getBadTutorials().get(0).getModule(), sw.getId()));
                            } catch(Exception e){e.printStackTrace();} 
                        }
                        System.out.println("\n" + ANSI_BLUE + ft.format(new Date()) + " Timetabling Agent: I cannot swap any more tutorials for Module 1." + ANSI_RESET);
                        System.out.println("Swaps performed: " + moduleSwaps[0] + " (Satisfying " + moduleSwaps[0]*2 + " students)");
                        System.out.println("Couldn't swap (" + messageBoard.getModule1Swaps().size() + "): " + messageBoard.studentsModule1() + "\n");
                        if(noChangeCounter[0] > 2){this.stop();}
                        break;
                    }   
                }
            } else {
               
                    System.out.println("\n" + ANSI_BLUE + ft.format(new Date()) + " Timetabling Agent: I cannot swap any more tutorials for Module 1." + ANSI_RESET);
                    System.out.println("Swaps performed: " + moduleSwaps[0] + " (Satisfying " + moduleSwaps[0]*2 + " students)");
                    if(s!=0){
                        System.out.println("Couldn't swap (" + messageBoard.getModule1Swaps().size() + "): " + messageBoard.studentsModule1() + "\n");
                    } else {
                        System.out.println("No unsatisfied students.\n");
                    }
                    for(SwapWish sw:messageBoard.getModule1Swaps()){
                        try{
                            send(swapReply(sw.getStudent(), ACLMessage.REJECT_PROPOSAL, sw.getBadTutorials().get(0).getModule(), sw.getId()));
                        } catch(Exception e){e.printStackTrace();} 
                    }
                    this.stop();
            }
        }        
    }
    
     // Perform Swaps for Module 1
    private class CheckSwapRequestsModule2 extends TickerBehaviour {
        
        private CheckSwapRequestsModule2(TimetablingAgent agent, long duration) {
            super(agent, duration);
        }

        @Override
        protected void onTick() {
            
            messageBoard.setModule2Swaps(orderedSwapWishes(messageBoard.getModule2Swaps()));
            
            int s = messageBoard.getModule2Swaps().size();
            int s2=666;
            System.out.println("Module 2 Message Board Size: " + s);
                                    
            AID student1 = new AID();
            AID student2 = new AID();
            SwapWish sw1 = new SwapWish();
            SwapWish sw2 = new SwapWish();
            
            if (s>1){
                boolean foundSwap = false;
                while(!foundSwap){
           
                    for(int i=0; i<s; ++i){
                        for(int j=0; j<s; ++j){

                            sw1 = messageBoard.getModule2Swaps().get(i);
                            sw2 = messageBoard.getModule2Swaps().get(j);
                            student1 = messageBoard.getModule2Swaps().get(i).getStudent();
                            student2 = messageBoard.getModule2Swaps().get(j).getStudent();
                            
                            if (!student1.equals(student2)){
                                
                                // Compare good and bad tutorials in two different student agents
                                for (Tutorial t1:sw1.getGoodTutorials()){
                                    if (sw2.getBadTutorials().get(0).equals(t1)){

                                        for (Tutorial t2:sw2.getGoodTutorials()){
                                            if (sw1.getBadTutorials().get(0).equals(t2)){

                                                System.out.println("Timetabling Agent: I can swap " + student1.getLocalName() + " " + t2.toString() + " WITH " + student2.getLocalName() + " " + t1.toString());

                                                int tt1 = studentTimetable(student1);
                                                int tt2 = studentTimetable(student2);

                                                //System.out.println(t1.studentList());
                                                //System.out.println(t2.studentList());
                                                //timetables.get(tt1).setModule2Tutorial(t1);
                                                //timetables.get(tt2).setModule2Tutorial(t2);
                                                List<Tutorial> temp = new ArrayList<>();
                                                for(Tutorial t:timetables.get(tt1).getTutorials()){
                                                    if(t.getModule().equals(t1.getModule())){
                                                        temp.add(t1);
                                                    } else {
                                                        temp.add(t);
                                                    }
                                                }
                                                timetables.get(tt1).setTutorials(temp);
                                                
                                                temp = new ArrayList<>();
                                                for(Tutorial t:timetables.get(tt2).getTutorials()){
                                                    if(t.getModule().equals(t2.getModule())){
                                                        temp.add(t2);
                                                    } else {
                                                        temp.add(t);
                                                    }
                                                }
                                                timetables.get(tt2).setTutorials(temp);
                                                t1.getStudents().remove(student2);
                                                t1.getStudents().add(student1);
                                                t2.getStudents().remove(student1);
                                                t2.getStudents().add(student2);
                                                //System.out.println(t1.studentList());
                                                //System.out.println(t2.studentList());

                                                //System.out.println(student1.getLocalName() + ": " + timetables.get(tt1));
                                                //System.out.println(student2.getLocalName() + ": " + timetables.get(tt2));
                                                
                                                System.out.println("MODULE 2 CHECK/  " + student1.getLocalName() + " in only one tutorial: " + studentIsInOneTutorial(student1, modules.get(1)));
                                                System.out.println("MODULE 2 CHECK/  " + student2.getLocalName() + " in only one tutorial: " + studentIsInOneTutorial(student2, modules.get(1)));

                                                messageBoard.getModule2Swaps().remove(sw1);
                                                messageBoard.getModule2Swaps().remove(sw2);
                                                
                                                try{
                                                    send(swapReply(student1, ACLMessage.ACCEPT_PROPOSAL, t1.getModule(), sw1.getId()));
                                                    send(swapReply(student2, ACLMessage.ACCEPT_PROPOSAL, t2.getModule(), sw2.getId()));
                                                } catch(Exception e){e.printStackTrace();}  

                                                moduleSwaps[1]++;
                                                foundSwap = true;
                                                break;
                                            }
                                        }
                                    } else {
//                                        System.out.println(ft.format(new Date()) + " Couldn't swap");
//                                        System.out.println(student1.getLocalName() + ", Bad: " + sw1.getBadTutorials().toString() + ", Good: " + sw1.getGoodTutorials().toString());
//                                        System.out.println(student2.getLocalName() + ", Bad: " + sw2.getBadTutorials().toString() + ", Good: " + sw2.getGoodTutorials().toString());
                                    }
                                    if(foundSwap){break;}
                                }
                                
                            }
                            if(foundSwap){break;}
                        }
                        if(foundSwap){break;}
                    }
                    s2 = messageBoard.getModule2Swaps().size();
                    if(s==s2){
                        noChangeCounter[1]++;
                        for(SwapWish sw:messageBoard.getModule2Swaps()){
                            try{
                                send(swapReply(sw.getStudent(), ACLMessage.REJECT_PROPOSAL, sw.getBadTutorials().get(0).getModule(), sw.getId()));
                            } catch(Exception e){e.printStackTrace();} 
                        }
                        System.out.println("\n" + ANSI_BLUE + ft.format(new Date()) + " Timetabling Agent: I cannot swap any more tutorials for Module 2" + ANSI_RESET);
                        System.out.println("Swaps performed: " + moduleSwaps[1] + " (Satisfying " + moduleSwaps[1]*2 + " students)");
                        System.out.println("Couldn't swap (" + messageBoard.getModule2Swaps().size() + "): " + messageBoard.studentsModule2() + "\n");
                        if(noChangeCounter[1] > 2){this.stop();}
                        break;
                    }
                }
            } else {
                
                    System.out.println("\n" + ANSI_BLUE + ft.format(new Date()) + " Timetabling Agent: I cannot swap any more tutorials for Module 2." + ANSI_RESET);
                    System.out.println("Swaps performed: " + moduleSwaps[1] + " (Satisfying " + moduleSwaps[1]*2 + " students)");
                    if (s!=0){
                        System.out.println("Couldn't swap (" + messageBoard.getModule2Swaps().size() + "): " + messageBoard.studentsModule2() + "\n");
                    } else {
                        System.out.println("No unsatisfied students.\n");
                    }
                    for(SwapWish sw:messageBoard.getModule2Swaps()){
                        try{
                            send(swapReply(sw.getStudent(), ACLMessage.REJECT_PROPOSAL, sw.getBadTutorials().get(0).getModule(), sw.getId()));
                        } catch(Exception e){e.printStackTrace();} 
                    }
                    this.stop();
                
            }
        }        
    }

    // Perform Swaps for Module 1
    private class CheckSwapRequestsModule3 extends TickerBehaviour {
        
        private CheckSwapRequestsModule3(TimetablingAgent agent, long duration) {
            super(agent, duration);
        }

        @Override
        protected void onTick() {
            
            messageBoard.setModule3Swaps(orderedSwapWishes(messageBoard.getModule3Swaps()));
            
            int s = messageBoard.getModule3Swaps().size();
            int s2=666;
            System.out.println("Module 3 Message Board Size: " + s);
                                    
            AID student1 = new AID();
            AID student2 = new AID();
            SwapWish sw1 = new SwapWish();
            SwapWish sw2 = new SwapWish();
            
            if (s>1){
                boolean foundSwap = false;
                while(!foundSwap){
                    
                    for(int i=0; i<s; ++i){
                        for(int j=0; j<s; ++j){

                            sw1 = messageBoard.getModule3Swaps().get(i);
                            sw2 = messageBoard.getModule3Swaps().get(j);
                            student1 = messageBoard.getModule3Swaps().get(i).getStudent();
                            student2 = messageBoard.getModule3Swaps().get(j).getStudent();
                            
                            if (!student1.equals(student2)){
                                
                                // Compare good and bad tutorials in two different student agents
                                for (Tutorial t1:sw1.getGoodTutorials()){
                                    if (sw2.getBadTutorials().get(0).equals(t1)){

                                        for (Tutorial t2:sw2.getGoodTutorials()){
                                            if (sw1.getBadTutorials().get(0).equals(t2)){

                                                System.out.println("Timetabling Agent: I can swap " + student1.getLocalName() + " " + t2.toString() + " WITH " + student2.getLocalName() + " " + t1.toString());

                                                int tt1 = studentTimetable(student1);
                                                int tt2 = studentTimetable(student2);

                                                //System.out.println(t1.studentList());
                                                //System.out.println(t2.studentList());
                                                //timetables.get(tt1).setModule3Tutorial(t1);
                                                //timetables.get(tt2).setModule3Tutorial(t2);
                                                List<Tutorial> temp = new ArrayList<>();
                                                for(Tutorial t:timetables.get(tt1).getTutorials()){
                                                    if(t.getModule().equals(t1.getModule())){
                                                        temp.add(t1);
                                                    } else {
                                                        temp.add(t);
                                                    }
                                                }
                                                timetables.get(tt1).setTutorials(temp);
                                                
                                                temp = new ArrayList<>();
                                                for(Tutorial t:timetables.get(tt2).getTutorials()){
                                                    if(t.getModule().equals(t2.getModule())){
                                                        temp.add(t2);
                                                    } else {
                                                        temp.add(t);
                                                    }
                                                }
                                                timetables.get(tt2).setTutorials(temp);
                                                t1.getStudents().remove(student2);
                                                t1.getStudents().add(student1);
                                                t2.getStudents().remove(student1);
                                                t2.getStudents().add(student2);
                                                //System.out.println(t1.studentList());
                                                //System.out.println(t2.studentList());

                                                //System.out.println(student1.getLocalName() + ": " + timetables.get(tt1));
                                                //System.out.println(student2.getLocalName() + ": " + timetables.get(tt2));
                                                
                                                System.out.println("MODULE 3 CHECK/  " + student1.getLocalName() + " in only one tutorial: " + studentIsInOneTutorial(student1, modules.get(2)));
                                                System.out.println("MODULE 3 CHECK/  " + student2.getLocalName() + " in only one tutorial: " + studentIsInOneTutorial(student2, modules.get(2)));

                                                messageBoard.getModule3Swaps().remove(sw1);
                                                messageBoard.getModule3Swaps().remove(sw2);

                                                try{
                                                    send(swapReply(student1, ACLMessage.ACCEPT_PROPOSAL, t1.getModule(), sw1.getId()));
                                                    send(swapReply(student2, ACLMessage.ACCEPT_PROPOSAL, t2.getModule(), sw2.getId()));
                                                } catch(Exception e){e.printStackTrace();} 

                                                moduleSwaps[2]++;
                                                foundSwap = true;
                                                break;
                                            }
                                        }
                                    } else {
//                                        System.out.println(ft.format(new Date()) + " Couldn't swap");
//                                        System.out.println(student1.getLocalName() + ", Bad: " + sw1.getBadTutorials().toString() + ", Good: " + sw1.getGoodTutorials().toString());
//                                        System.out.println(student2.getLocalName() + ", Bad: " + sw2.getBadTutorials().toString() + ", Good: " + sw2.getGoodTutorials().toString());
                                    }
                                    if(foundSwap){break;}
                                }
                                
                            }
                            if(foundSwap){break;}
                        }
                        if(foundSwap){break;}
                    }
                    s2 = messageBoard.getModule3Swaps().size();
                    if(s==s2){
                        noChangeCounter[2]++;
                        for(SwapWish sw:messageBoard.getModule3Swaps()){
                            try{
                                send(swapReply(sw.getStudent(), ACLMessage.REJECT_PROPOSAL, sw.getBadTutorials().get(0).getModule(), sw.getId()));
                            } catch(Exception e){e.printStackTrace();} 
                        }
                        System.out.println("\n" + ANSI_BLUE + ft.format(new Date()) + " Timetabling Agent: I cannot swap any more tutorials for Module 3" + ANSI_RESET);
                        System.out.println("Swaps performed: " + moduleSwaps[2] + " (Satisfying " + moduleSwaps[2]*2 + " students)");
                        System.out.println("Couldn't swap (" + messageBoard.getModule3Swaps().size() + "): " + messageBoard.studentsModule3() + "\n");
                        if(noChangeCounter[2] > 2){this.stop();}
                        break;
                    }
                }
            } else {
                    System.out.println("\n" + ANSI_BLUE + ft.format(new Date()) + " Timetabling Agent: I cannot swap any more tutorials for Module 3." + ANSI_RESET);
                    System.out.println("Swaps performed: " + moduleSwaps[2] + " (Satisfying " + moduleSwaps[2]*2 + " students)");
                    if(s!=0){
                        System.out.println("Couldn't swap (" + messageBoard.getModule3Swaps().size() + "): " + messageBoard.studentsModule3() + "\n");
                    } else {
                        System.out.println("No unsatisfied students.\n");
                    }
                    for(SwapWish sw:messageBoard.getModule3Swaps()){
                        try{
                            send(swapReply(sw.getStudent(), ACLMessage.REJECT_PROPOSAL, sw.getBadTutorials().get(0).getModule(), sw.getId()));
                        } catch(Exception e){e.printStackTrace();} 
                    }
                    this.stop();
                
            }
        }        
    }
    
    private boolean studentIsInOneTutorial(AID student, Module module){
        int count = 0;
        for(Tutorial t:module.getTutorials()){
            for(AID aid:t.getStudents()){
                if (aid.getLocalName().equals(student.getLocalName())){ 
                    count ++;
                }
            }
        }
        if (count==1){
            return true;
        } else {
            return false;
        }
    }
    
    private int getStudentIndex(AID student){
        for(int i=0; i<studentAgents.length; ++i){
            if (studentAgents[i].getLocalName().equals(student.getLocalName())){
                return i;
            }
        }
        return 666;
    }
    
    private ACLMessage swapReply(AID student, int performative, String module, String id){
            if (performative == ACLMessage.ACCEPT_PROPOSAL){
                ACLMessage msg = new ACLMessage(performative);
                msg.setLanguage(codec.getName());
                msg.setOntology(ontology.getName());
                msg.setConversationId(id); 
                msg.setContent(module);
                msg.addReceiver(student);
                return msg;
            } else {
                ACLMessage msg = new ACLMessage(performative);
                msg.setLanguage(codec.getName());
                msg.setOntology(ontology.getName());
                msg.setConversationId(id); 
                msg.setContent(module);
                msg.addReceiver(student);
                return msg;
            }
    }
    
}
