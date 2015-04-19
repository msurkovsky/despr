package cz.vsb.cs.sur096.despr.operations.examples;


import cz.vsb.cs.sur096.despr.model.operation.IRootOperation;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import java.util.Random;

public class RandomValues implements IRootOperation {

	private Random rnd;
	private boolean wasInit;
	private int idx;
	public RandomValues() {
		rnd = new Random();
		wasInit = false;
		modulo = size = 10; //default values
	}

	@AInputParameter(value=EInputParameterType.INNER)
	private Integer size;
	@AInputParameter(value=EInputParameterType.INNER)
	private Integer modulo;
	@AOutputParameter
	private Integer rndValue;

    @Override
	public void execute() throws Exception {
		if (!wasInit) init();
		rndValue = rnd.nextInt() % modulo;
        rndValue = rndValue < 0 ? -rndValue : rndValue;
	}
    @Override
	public String getLocalizeMessage(String key) {return null;}

    @Override
	public boolean hasNext() {return idx < size;}
    @Override
	public void setNext() {idx++;}
    @Override
	public void resetIterator() {idx = 0;}
    @Override
	public int getCount() {return size;}
    @Override
	public void init() {
		resetIterator();
		wasInit = true;
	}
    @Override
    public boolean wasInit() {return wasInit;}

	public Integer getModulo() {return modulo;}
	public void setModulo(Integer modulo) {this.modulo = modulo; wasInit = false;}
	public Integer getSize() {return size;}
	public void setSize(Integer size) {this.size = size; wasInit = false;}
	public Integer getRndValue() {return rndValue;}
	public void setRndValue(Integer rndValue) {this.rndValue = rndValue;}
}