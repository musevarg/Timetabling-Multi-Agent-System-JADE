# Timetabling Multi-Agent System
A multi-agent system that aims to help students swap tutorial/practical slots with each other based on their own preference.

![](https://github.com/musevarg/Timetabling-Multi-Agent-System-JADE/blob/main/Documents/pic0.PNG?raw=true)

## Ontology

The [ontology](https://github.com/musevarg/Timetabling-Multi-Agent-System-JADE/tree/main/Code/src/timetabling/ontology/elements) used attempts to represent reality as much as possible. A number of concepts,
predicates and agent actions have been defined to represent real life situations or actors (such as the staff
involved in making timetables and students).

#### Concepts
The concepts here represent real-life elements: modules, preferences, timetables, swap wishes and the message board. Each come with their own propertie.

#### Predicates
Two predicates are used here: 'swap proposal' and 'send timetable'. These two predicates are responsible for carrying the relevant concepts in messages between agents (a swap proposal contains a swap wish and a send timetable contains a timetable).

#### Agent Actions
Only one action is needed in this implementation, which is a timetable request. It was possible to use only one action as the timetabling agent handles the reception of all requests and attempts to swap tutorial slots in order to maximize the utility of each student agent, before letting students know whether their tutorial has been swapped or not. Therefore, the only action that is requested from an external agent is a student agent asking for its timetable. Upon software start, all students that don't have a timetable ask for one, then compare their assigned timesots to their own preferences, and send back a swap proposal (if necessary) as a predicate. If the timetabling agent sends a succesful swap response, the students then request for their updated timetable.

## Communication Protocol

The image below shows the communication protocol in the JADE sniffer with 10 students, the timetabling agent and the directory facilitator (df).

![JADE Sniffer - Timetabling MAS](https://github.com/musevarg/Timetabling-Multi-Agent-System-JADE/blob/main/Documents/pic1.PNG?raw=true)

## Utility Function
The timetabling agent works towards maximizing the utility of each student agent, prioritizing students with a lower utility.
Student preferences have 4 categories, each one is assigned a different utility value.

No preference: 1

Would like to attend: 1

Would not like attend: 0.25

Cannot attend: 0

Since there are 3 tutorials, the best utility a student can have is 3.

## Console Output
Below is a short sample of a console output at runtime.

![Console Output - Timetabling MAS](https://github.com/musevarg/Timetabling-Multi-Agent-System-JADE/blob/main/Documents/pic2.png?raw=true)
