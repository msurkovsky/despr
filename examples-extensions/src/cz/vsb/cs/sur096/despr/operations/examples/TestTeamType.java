
package cz.vsb.cs.sur096.despr.operations.examples;

import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.types.Contact;
import cz.vsb.cs.sur096.despr.types.ESex;
import cz.vsb.cs.sur096.despr.types.Person;
import cz.vsb.cs.sur096.despr.types.Team;

public class TestTeamType implements IOperation {

    public TestTeamType() {
        team = new Team();
        team.setName("The A-Team");
        Person p1 = new Person("Josef", "Novák", ESex.MALE, 26, new Contact(
                "josef.novak@ateam.cz", "123456789"));
        team.addPerson(p1);
        Person p2 = new Person("Klára", "Malá", ESex.FEMALE, 24, new Contact(
                "klara.mala@ateam.cz", null));
        team.addPerson(p2);
    }
    
    @AInputParameter(EInputParameterType.INNER)
    private Team team;
    
    @Override
    public void execute() throws Exception { }

    @Override
    public String getLocalizeMessage(String string) { return null; }
    
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    
}
