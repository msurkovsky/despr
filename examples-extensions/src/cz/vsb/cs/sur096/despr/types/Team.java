package cz.vsb.cs.sur096.despr.types;

import cz.vsb.cs.sur096.despr.utils.ID;
import java.util.ArrayList;
import java.util.List;

public class Team {

    private String name;
    private List<Person> people;
    
    public Team() { 
        int teamIDGenerator = ID.createNewIDGenerator();
        name = "team_" + ID.getNextID(teamIDGenerator);
        people = new ArrayList<Person>();
    }
    
    public void addPerson(Person person) { people.add(person); }
    public void removePerson(Person person) { people.remove(person); }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public List<Person> getPeople() { return people; }
    public void setPeople(List<Person> people) { this.people = people; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s:[", name));
        for (Person p : people) {
            sb.append(String.format("%s,", p.toString()));
        }
        sb.append("]");
        return sb.toString();
    }
}