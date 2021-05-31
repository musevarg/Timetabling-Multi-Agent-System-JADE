package timetabling.main;

// 40430624

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import timetabling.ontology.elements.Module;
import timetabling.ontology.elements.Preferences;
import timetabling.ontology.elements.Timeslot;
import timetabling.ontology.elements.Tutorial;


public class SET1011140430624Coursework {
    
    public static List<Module> modules = new ArrayList<>();

    public static void main(String[] args) {
        
        createTutorials();
        
        Profile myProfile = new ProfileImpl();
        jade.core.Runtime myRuntime = jade.core.Runtime.instance();
	try
        {           
            ContainerController myContainer = myRuntime.createMainContainer(myProfile);	
            AgentController rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
            rma.start();
            
            AgentController timetablingAgent = myContainer.createNewAgent("TimetablingAgent", TimetablingAgent.class.getCanonicalName(), null);
            timetablingAgent.start(); 
            
            createStudents(95, myContainer);
        }
	catch(Exception e){
            System.out.println("Exception starting agent: " + e.toString());
	}
        
    }
    
    private static void createStudents(int numberOfAgents, ContainerController container){
        try{
            AgentController[] student = new AgentController[numberOfAgents];
            for (int i=0; i<numberOfAgents; i++)
            {
                String agentId = (i+1)<10?"0"+(i+1):"" + (i+1);
                String agentName = "Student" + agentId;
                Preferences[] pref = new Preferences[1]; // arguments passed to StudentAgent.java
                
                // Remember to change the tutorial settings in createTutorial() according to their Test Case
                
                // Use this for Test Case 1, 2, 3 and 4 (One module, 2 tutorials)
                // If an even number of students is chosen on line 39
                // All students should be able to swap
                //Preferences[] hardcoded = createPreferences();          
                //if(i%2==0){pref[0]=hardcoded[0];}else{pref[0]=hardcoded[2];}
                // End Test Case 1, 2, 3, 4
                
                // Use this for Test Case 5 (a random pref from 1 of 5 pre-defined set of preferences)
                Preferences[] randomSetPreferences = createPreferences();
                Random random = new Random();
                int index = random.nextInt(5);
                pref[0] = randomSetPreferences[index];
                // End Test Case 5
                
                // Use this for Test Case 6
                //pref[0] = randomPref();
                // End Test Case 6
                
                
                student[i] = container.createNewAgent(agentName, StudentAgent.class.getCanonicalName(), pref);
                student[i].start();
                
            }
        } catch (Exception e)
        {
            System.out.println("Error: " + e.toString());
        }

    }
    
    private static void createTutorials(){
        
        //Use this for test case 1 and 2 (one module, 2 tutorials)
        
//        Module module1 = new Module();
//        module1.setId(1);
//        module1.setName("Module 1");
//        
//        Tutorial tutorial1 = new Tutorial();
//        tutorial1.setModule("Module1");
//        tutorial1.setId("M1-T1");
//        tutorial1.setNumberOfStudents(0);
//        Timeslot ts1 = new Timeslot();
//        ts1.setDay("Monday");
//        ts1.setHour(9);
//        tutorial1.setTimeslot(ts1);
//        module1.getTutorials().add(tutorial1);
//        
//        Tutorial tutorial3 = new Tutorial();
//        tutorial3.setModule("Module1");
//        tutorial3.setId("M1-T2");
//        tutorial3.setNumberOfStudents(0);
//        Timeslot ts3 = new Timeslot();
//        ts3.setDay("Tuesday");
//        ts3.setHour(9);
//        tutorial3.setTimeslot(ts3);
//        module1.getTutorials().add(tutorial3);
//        
//        modules.add(module1);

        // END TEST CASE 1 and 2
          
          
        //TEST CASE 3 (2 Modules, 2 Tutorials per module)
              
        // Create tutorials for Module 1
//        Module module1 = new Module();
//        module1.setId(1);
//        module1.setName("Module 1");
//        
//        Tutorial tutorial1 = new Tutorial();
//        tutorial1.setModule("Module1");
//        tutorial1.setId("M1-T1");
//        tutorial1.setNumberOfStudents(0);
//        Timeslot ts1 = new Timeslot();
//        ts1.setDay("Monday");
//        ts1.setHour(9);
//        tutorial1.setTimeslot(ts1);
//        module1.getTutorials().add(tutorial1);
//        
//        Tutorial tutorial3 = new Tutorial();
//        tutorial3.setModule("Module1");
//        tutorial3.setId("M1-T3");
//        tutorial3.setNumberOfStudents(0);
//        Timeslot ts3 = new Timeslot();
//        ts3.setDay("Tuesday");
//        ts3.setHour(9);
//        tutorial3.setTimeslot(ts3);
//        module1.getTutorials().add(tutorial3);
//        
//        modules.add(module1);
//        
//        // Module 2
//        Module module2 = new Module();
//        module2.setId(2);
//        module2.setName("Module 2");
//        
//        Tutorial tutorial4 = new Tutorial();
//        tutorial4.setModule("Module2");
//        tutorial4.setId("M2-T1");
//        tutorial4.setNumberOfStudents(0);
//        Timeslot ts4 = new Timeslot();
//        ts4.setDay("Monday");
//        ts4.setHour(15);
//        tutorial4.setTimeslot(ts4);
//        module2.getTutorials().add(tutorial4);
//        
//        
//        Tutorial tutorial6 = new Tutorial();
//        tutorial6.setModule("Module2");
//        tutorial6.setId("M2-T2");
//        tutorial6.setNumberOfStudents(0);
//        Timeslot ts6 = new Timeslot();
//        ts6.setDay("Wednesday");
//        ts6.setHour(10);
//        tutorial6.setTimeslot(ts6);
//        module2.getTutorials().add(tutorial6);
//        
//        modules.add(module2);
        
        // END TEST CASE 3
        
        //TEST CASE 4
        
        // Create tutorials for Module 1
//        Module module1 = new Module();
//        module1.setId(1);
//        module1.setName("Module 1");
//        
//        Tutorial tutorial1 = new Tutorial();
//        tutorial1.setModule("Module1");
//        tutorial1.setId("M1-T1");
//        tutorial1.setNumberOfStudents(0);
//        Timeslot ts1 = new Timeslot();
//        ts1.setDay("Monday");
//        ts1.setHour(9);
//        tutorial1.setTimeslot(ts1);
//        module1.getTutorials().add(tutorial1);
//        
//        Tutorial tutorial3 = new Tutorial();
//        tutorial3.setModule("Module1");
//        tutorial3.setId("M1-T3");
//        tutorial3.setNumberOfStudents(0);
//        Timeslot ts3 = new Timeslot();
//        ts3.setDay("Tuesday");
//        ts3.setHour(9);
//        tutorial3.setTimeslot(ts3);
//        module1.getTutorials().add(tutorial3);
//        
//        modules.add(module1);
//        
//        // Module 2
//        Module module2 = new Module();
//        module2.setId(2);
//        module2.setName("Module 2");
//        
//        Tutorial tutorial4 = new Tutorial();
//        tutorial4.setModule("Module2");
//        tutorial4.setId("M2-T1");
//        tutorial4.setNumberOfStudents(0);
//        Timeslot ts4 = new Timeslot();
//        ts4.setDay("Monday");
//        ts4.setHour(15);
//        tutorial4.setTimeslot(ts4);
//        module2.getTutorials().add(tutorial4);
//        
//        
//        Tutorial tutorial6 = new Tutorial();
//        tutorial6.setModule("Module2");
//        tutorial6.setId("M2-T2");
//        tutorial6.setNumberOfStudents(0);
//        Timeslot ts6 = new Timeslot();
//        ts6.setDay("Wednesday");
//        ts6.setHour(10);
//        tutorial6.setTimeslot(ts6);
//        module2.getTutorials().add(tutorial6);
//        
//        modules.add(module2);
//        
//       // Create tutorials for Module 3  
//        Module module3 = new Module();
//        module3.setId(3);
//        module3.setName("Module 2");
//        
//        Tutorial tutorial8 = new Tutorial();
//        tutorial8.setModule("Module3");
//        tutorial8.setId("M3-T1");
//        tutorial8.setNumberOfStudents(0);
//        Timeslot ts8 = new Timeslot();
//        ts8.setDay("Thursday");
//        ts8.setHour(16);
//        tutorial8.setTimeslot(ts8);
//        module3.getTutorials().add(tutorial8);
//                
//        Tutorial tutorial9 = new Tutorial();
//        tutorial9.setModule("Module3");
//        tutorial9.setId("M3-T2");
//        tutorial9.setNumberOfStudents(0);
//        Timeslot ts9 = new Timeslot();
//        ts9.setDay("Friday");
//        ts9.setHour(14);
//        tutorial9.setTimeslot(ts9);
//        module3.getTutorials().add(tutorial9);
//        
//        modules.add(module3);
        
        // END TEST CASE 4
        
        // TEST CASE 5
        
        // Create tutorials for Module 1
        Module module1 = new Module();
        module1.setId(1);
        module1.setName("Module 1");
        
        Tutorial tutorial1 = new Tutorial();
        tutorial1.setModule("Module1");
        tutorial1.setId("M1-T1");
        tutorial1.setNumberOfStudents(0);
        Timeslot ts1 = new Timeslot();
        ts1.setDay("Monday");
        ts1.setHour(9);
        tutorial1.setTimeslot(ts1);
        module1.getTutorials().add(tutorial1);
        
        Tutorial tutorial2 = new Tutorial();
        tutorial2.setModule("Module1");
        tutorial2.setId("M1-T2");
        tutorial2.setNumberOfStudents(0);
        Timeslot ts2 = new Timeslot();
        ts2.setDay("Monday");
        ts2.setHour(11);
        tutorial2.setTimeslot(ts2);
        module1.getTutorials().add(tutorial2);
        
        Tutorial tutorial3 = new Tutorial();
        tutorial3.setModule("Module1");
        tutorial3.setId("M1-T3");
        tutorial3.setNumberOfStudents(0);
        Timeslot ts3 = new Timeslot();
        ts3.setDay("Tuesday");
        ts3.setHour(9);
        tutorial3.setTimeslot(ts3);
        module1.getTutorials().add(tutorial3);
        
        modules.add(module1);
        
        // Create tutorials for Module 2   
        Module module2 = new Module();
        module2.setId(2);
        module2.setName("Module 2");
        
        Tutorial tutorial4 = new Tutorial();
        tutorial4.setModule("Module2");
        tutorial4.setId("M2-T1");
        tutorial4.setNumberOfStudents(0);
        Timeslot ts4 = new Timeslot();
        ts4.setDay("Monday");
        ts4.setHour(15);
        tutorial4.setTimeslot(ts4);
        module2.getTutorials().add(tutorial4);
        
        Tutorial tutorial5 = new Tutorial();
        tutorial5.setModule("Module2");
        tutorial5.setId("M2-T2");
        tutorial5.setNumberOfStudents(0);
        Timeslot ts5 = new Timeslot();
        ts5.setDay("Monday");
        ts5.setHour(16);
        tutorial5.setTimeslot(ts5);
        module2.getTutorials().add(tutorial5);
        
        Tutorial tutorial6 = new Tutorial();
        tutorial6.setModule("Module2");
        tutorial6.setId("M2-T3");
        tutorial6.setNumberOfStudents(0);
        Timeslot ts6 = new Timeslot();
        ts6.setDay("Wednesday");
        ts6.setHour(10);
        tutorial6.setTimeslot(ts6);
        module2.getTutorials().add(tutorial6);
        
        modules.add(module2);
     
        // Create tutorials for Module 3  
        Module module3 = new Module();
        module3.setId(3);
        module3.setName("Module 2");
        
        Tutorial tutorial7 = new Tutorial();
        tutorial7.setModule("Module3");
        tutorial7.setId("M3-T1");
        tutorial7.setNumberOfStudents(0);
        Timeslot ts7 = new Timeslot();
        ts7.setDay("Thursday");
        ts7.setHour(11);
        tutorial7.setTimeslot(ts7);
        module3.getTutorials().add(tutorial7);
        
        Tutorial tutorial8 = new Tutorial();
        tutorial8.setModule("Module3");
        tutorial8.setId("M3-T2");
        tutorial8.setNumberOfStudents(0);
        Timeslot ts8 = new Timeslot();
        ts8.setDay("Thursday");
        ts8.setHour(16);
        tutorial8.setTimeslot(ts8);
        module3.getTutorials().add(tutorial8);
        
        Tutorial tutorial9 = new Tutorial();
        tutorial9.setModule("Module3");
        tutorial9.setId("M3-T3");
        tutorial9.setNumberOfStudents(0);
        Timeslot ts9 = new Timeslot();
        ts9.setDay("Friday");
        ts9.setHour(14);
        tutorial9.setTimeslot(ts9);
        module3.getTutorials().add(tutorial9);
        
        modules.add(module3);
        
    }
    
    // Generate a bunch of preferences that will be
    // attributed randomly to Student Agents
    private static Preferences[] createPreferences(){
        Preferences[] pref = new Preferences[5];
        
        // Case 1
        pref[0] = new Preferences();
        pref[0].getCannotAttend().addAll(morning("Monday"));
        pref[0].getWouldLikeToAttend().addAll(afternoon("Monday"));
        pref[0].getWouldLikeToAttend().addAll(morning("Tuesday"));
        pref[0].getNoPreference().addAll(afternoon("Tuesday"));
        pref[0].getCannotAttend().addAll(morning("Wednesday"));
        pref[0].getCannotAttend().addAll(afternoon("Wednesday"));
        pref[0].getWouldNotAttend().addAll(morning("Thursday"));
        pref[0].getCannotAttend().addAll(afternoon("Thursday"));
        pref[0].getNoPreference().addAll(morning("Friday"));
        pref[0].getNoPreference().addAll(afternoon("Friday"));
        
        // Case 2
        pref[1] = new Preferences();
        pref[1].getWouldLikeToAttend().addAll(morning("Monday"));
        pref[1].getWouldLikeToAttend().addAll(afternoon("Monday"));
        pref[1].getWouldNotAttend().addAll(morning("Tuesday"));
        pref[1].getCannotAttend().addAll(afternoon("Tuesday"));
        pref[1].getCannotAttend().addAll(morning("Wednesday"));
        pref[1].getCannotAttend().addAll(afternoon("Wednesday"));
        pref[1].getCannotAttend().addAll(morning("Thursday"));
        pref[1].getWouldNotAttend().addAll(afternoon("Thursday"));
        pref[1].getWouldLikeToAttend().addAll(morning("Friday"));
        pref[1].getWouldLikeToAttend().addAll(afternoon("Friday"));
        
        // Case 3
        pref[2] = new Preferences();
        pref[2].getWouldLikeToAttend().addAll(morning("Monday"));
        pref[2].getCannotAttend().addAll(afternoon("Monday"));
        pref[2].getCannotAttend().addAll(morning("Tuesday"));
        pref[2].getWouldNotAttend().addAll(afternoon("Tuesday"));
        pref[2].getWouldLikeToAttend().addAll(morning("Wednesday"));
        pref[2].getWouldNotAttend().addAll(afternoon("Wednesday"));
        pref[2].getWouldLikeToAttend().addAll(morning("Thursday"));
        pref[2].getNoPreference().addAll(afternoon("Thursday"));
        pref[2].getCannotAttend().addAll(morning("Friday"));
        pref[2].getCannotAttend().addAll(afternoon("Friday"));
        
        // Case 4
        pref[3] = new Preferences();
        pref[3].getCannotAttend().addAll(morning("Monday"));
        pref[3].getCannotAttend().addAll(afternoon("Monday"));
        pref[3].getNoPreference().addAll(morning("Tuesday"));
        pref[3].getNoPreference().addAll(afternoon("Tuesday"));
        pref[3].getWouldLikeToAttend().addAll(morning("Wednesday"));
        pref[3].getWouldLikeToAttend().addAll(afternoon("Wednesday"));
        pref[3].getWouldNotAttend().addAll(morning("Thursday"));
        pref[3].getWouldNotAttend().addAll(afternoon("Thursday"));
        pref[3].getWouldLikeToAttend().addAll(morning("Friday"));
        pref[3].getWouldLikeToAttend().addAll(afternoon("Friday"));
        
        // Case 5
        pref[4] = new Preferences();
        pref[4].getWouldLikeToAttend().addAll(morning("Monday"));
        pref[4].getCannotAttend().addAll(afternoon("Monday"));
        pref[4].getWouldLikeToAttend().addAll(morning("Tuesday"));
        pref[4].getCannotAttend().addAll(afternoon("Tuesday"));
        pref[4].getWouldLikeToAttend().addAll(morning("Wednesday"));
        pref[4].getCannotAttend().addAll(afternoon("Wednesday"));
        pref[4].getWouldLikeToAttend().addAll(morning("Thursday"));
        pref[4].getWouldNotAttend().addAll(afternoon("Thursday"));
        pref[4].getWouldLikeToAttend().addAll(morning("Friday"));
        pref[4].getWouldNotAttend().addAll(afternoon("Friday"));
        
        return pref;
    }
    
    // Generate a list of timeslots for a morning of the given day
    private static List<Timeslot> morning(String day){
        List<Timeslot> tsList = new ArrayList<>();

        for(int i=9; i<14; ++i){
            tsList.add(new Timeslot());
            tsList.get(tsList.size()-1).setDay(day);
            tsList.get(tsList.size()-1).setHour(i);
        }
        
        return tsList;
    }
    
    // Generate a list of timeslots for an afternoon of the given day
    private static List<Timeslot> afternoon(String day){
        List<Timeslot> tsList = new ArrayList<>();

        for(int i=14; i<18; ++i){
            tsList.add(new Timeslot());
            tsList.get(tsList.size()-1).setDay(day);
            tsList.get(tsList.size()-1).setHour(i);
        }
        
        return tsList;
    }
    
    // This method generates completely random preferences for each timeslot
    private static Preferences randomPref(){
        
        Preferences pref = new Preferences();
        Random random = new Random();
        String[] days = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        ArrayList<Timeslot> tempTs = new ArrayList<Timeslot>();
        
        for(int i=0; i<5; ++i){
            for (int y=9; y<18; ++y){
                int rnd = random.nextInt(4);
                tempTs.add(new Timeslot());
                switch(rnd){
                    case 0:
                        tempTs.get(tempTs.size()-1).setDay(days[i]);
                        tempTs.get(tempTs.size()-1).setHour(y);
                        pref.getCannotAttend().add(tempTs.get(tempTs.size()-1));
                        break;
                    case 1:
                        tempTs.get(tempTs.size()-1).setDay(days[i]);
                        tempTs.get(tempTs.size()-1).setHour(y);
                        pref.getNoPreference().add(tempTs.get(tempTs.size()-1));
                        break;
                    case 2:
                        tempTs.get(tempTs.size()-1).setDay(days[i]);
                        tempTs.get(tempTs.size()-1).setHour(y);
                        pref.getWouldLikeToAttend().add(tempTs.get(tempTs.size()-1));
                        break;
                    case 3:
                        tempTs.get(tempTs.size()-1).setDay(days[i]);
                        tempTs.get(tempTs.size()-1).setHour(y);
                        pref.getWouldNotAttend().add(tempTs.get(tempTs.size()-1));
                        break;
                }
            }
        }

        return pref;
    }
    
}
