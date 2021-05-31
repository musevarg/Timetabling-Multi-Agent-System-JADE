package timetabling.ontology.elements;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;
import java.util.Objects;

public class Timeslot implements Concept {
    
    private String day;
    private int hour;

    @Slot(mandatory = true)
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Slot(mandatory = true)
    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
        int result = 1;
        result = prime * result + ((day == null) ? 0 : day.hashCode());
        result = prime * result + (int) (hour ^ (hour >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Timeslot other = (Timeslot) obj;
        if (this.hour != other.hour) {
            return false;
        }
        if (!Objects.equals(this.day, other.day)) {
            return false;
        }
        if(this.hour == other.hour & Objects.equals(this.day, other.day)){
            return true;
        }
        return true;
    }
    
    
    
}
