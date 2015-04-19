package cz.vsb.cs.sur096.despr.operations.examples;


import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import java.util.Random;

public class RandomValue implements IOperation {

	private Random rnd;
	public RandomValue() {
		rnd = new Random();
		modulo = 10;
	}

	@AInputParameter(EInputParameterType.INNER)
	private Integer modulo;

	@AOutputParameter
	private Double rndValue;

	@Override
	public void execute() throws Exception {
		rndValue = rnd.nextDouble() * 360;
//        rndValue = rndValue < 0 ? -rndValue : rndValue;
	}
	
	@Override
	public String getLocalizeMessage(String key) {
		return null;
	}

	public Integer getModulo() {return modulo;}
	public void setModulo(Integer modulo) {this.modulo = modulo;}

	public Double getRndValue() {return rndValue;}
	public void setRndValue(Double rndValue) {this.rndValue = rndValue;}
}