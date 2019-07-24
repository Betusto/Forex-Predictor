import com.fuzzylite.*;
import com.fuzzylite.activation.*;
import com.fuzzylite.defuzzifier.*;
import com.fuzzylite.norm.s.*;
import com.fuzzylite.norm.t.*;
import com.fuzzylite.rule.*;
import com.fuzzylite.term.*;
import com.fuzzylite.variable.*;

public class SistemaDifuso{
public double Conveniencia, Tendencia, Inflacion;

public SistemaDifuso(double Conveniencia, double Tendencia, double Inflacion){
    this.Conveniencia = Conveniencia;
    this.Tendencia = Tendencia;
    this.Inflacion = Inflacion;
}
       
    
    public double getOportunidad(){
        Engine engine = new Engine();
        engine.setName("Inversion");
        engine.setDescription("Inversion en Forex");
        InputVariable Tendencia = new InputVariable();
        Tendencia.setName("Tendencia");
        Tendencia.setDescription("Temperatura alcista o bajista");
        Tendencia.setEnabled(true);
        Tendencia.setRange(-2, 2);
        Tendencia.setLockValueInRange(false);
        Tendencia.addTerm(new Ramp("BAJISTA", -0.02, -2));
        Tendencia.addTerm(new Trapezoid("CONTRACCION", -0.04, -0.01, 0.01, 0.04));
        Tendencia.addTerm(new Ramp("ALCISTA", 0.02, 2));
        engine.addInputVariable(Tendencia);

        InputVariable Conveniencia = new InputVariable();
        Conveniencia.setName("Conveniencia");
        Conveniencia.setDescription("Noticias");
        Conveniencia.setEnabled(true);
        Conveniencia.setRange(0, 100);
        Conveniencia.setLockValueInRange(false);
        Conveniencia.addTerm(new Ramp("MALA", 55, 0));
        Conveniencia.addTerm(new Ramp("BUENA", 45, 100));
        engine.addInputVariable(Conveniencia);

        InputVariable Inflacion = new InputVariable();
        Inflacion.setName("Inflacion");
        Inflacion.setDescription("Inflacion del IPC");
        Inflacion.setEnabled(true);
        Inflacion.setRange(0, 1000); //Inflacion debe estar no definida en su rango
        Inflacion.setLockValueInRange(false);
        Inflacion.addTerm(new Ramp("INFLACION_MODERADA", 30, 0));
        Inflacion.addTerm(new Trapezoid("INFLACION_GALOPEANTE", 25, 80, 120, 240));
        Inflacion.addTerm(new Ramp("HIPERINFLACION", 200, 600));
        engine.addInputVariable(Inflacion);

        OutputVariable Decision = new OutputVariable();
        Decision.setName("Decision");
        Decision.setDescription("Oportunidad de inversion");
        Decision.setEnabled(true);
        Decision.setRange(0, 100);
        Decision.setLockValueInRange(false);
        Decision.setAggregation(new Maximum());
        Decision.setDefuzzifier(new Centroid(100));
        Decision.setDefaultValue(Double.NaN);
        Decision.setLockPreviousValue(false);
        Decision.addTerm(new Ramp("PUT", 35, 0));
        Decision.addTerm(new Trapezoid("NO", 30, 40, 60, 70));
        Decision.addTerm(new Ramp("CALL", 65, 100));
        engine.addOutputVariable(Decision);

        RuleBlock mamdani = new RuleBlock();
        mamdani.setName("mamdani");
        mamdani.setDescription("Min Max");
        mamdani.setEnabled(true);
        mamdani.setConjunction(new Minimum());
        mamdani.setDisjunction(null);
        mamdani.setImplication(new Minimum());
        mamdani.setActivation(new General());
        mamdani.addRule(Rule.parse("if Tendencia is BAJISTA and Conveniencia is MALA and Inflacion is INFLACION_MODERADA then Decision is PUT", engine));
        mamdani.addRule(Rule.parse("if Tendencia is BAJISTA and Conveniencia is MALA and Inflacion is INFLACION_GALOPEANTE then Decision is PUT", engine));
        mamdani.addRule(Rule.parse("if Tendencia is BAJISTA and Conveniencia is MALA and Inflacion is HIPERINFLACION then Decision is NO", engine));
        mamdani.addRule(Rule.parse("if Tendencia is BAJISTA and Conveniencia is BUENA and Inflacion is INFLACION_MODERADA then Decision is CALL", engine));
        mamdani.addRule(Rule.parse("if Tendencia is BAJISTA and Conveniencia is BUENA and Inflacion is INFLACION_GALOPEANTE then Decision is CALL", engine));
        mamdani.addRule(Rule.parse("if Tendencia is BAJISTA and Conveniencia is BUENA and Inflacion is HIPERINFLACION then Decision is NO", engine));
        mamdani.addRule(Rule.parse("if Tendencia is CONTRACCION and Conveniencia is MALA and Inflacion is INFLACION_MODERADA then Decision is PUT", engine));
        mamdani.addRule(Rule.parse("if Tendencia is CONTRACCION and Conveniencia is MALA and Inflacion is INFLACION_GALOPEANTE then Decision is NO", engine));
        mamdani.addRule(Rule.parse("if Tendencia is CONTRACCION and Conveniencia is MALA and Inflacion is HIPERINFLACION then Decision is NO", engine));
        mamdani.addRule(Rule.parse("if Tendencia is CONTRACCION and Conveniencia is BUENA and Inflacion is INFLACION_MODERADA then Decision is CALL", engine));
        mamdani.addRule(Rule.parse("if Tendencia is CONTRACCION and Conveniencia is BUENA and Inflacion is INFLACION_GALOPEANTE then Decision is NO", engine));
        mamdani.addRule(Rule.parse("if Tendencia is CONTRACCION and Conveniencia is BUENA and Inflacion is HIPERINFLACION then Decision is NO", engine));
        mamdani.addRule(Rule.parse("if Tendencia is ALCISTA and Conveniencia is MALA and Inflacion is INFLACION_MODERADA then Decision is CALL", engine));
        mamdani.addRule(Rule.parse("if Tendencia is ALCISTA and Conveniencia is MALA and Inflacion is INFLACION_GALOPEANTE then Decision is CALL", engine));
        mamdani.addRule(Rule.parse("if Tendencia is ALCISTA and Conveniencia is MALA and Inflacion is HIPERINFLACION then Decision is NO", engine));
        mamdani.addRule(Rule.parse("if Tendencia is ALCISTA and Conveniencia is BUENA and Inflacion is INFLACION_MODERADA then Decision is PUT", engine));
        mamdani.addRule(Rule.parse("if Tendencia is ALCISTA and Conveniencia is BUENA and Inflacion is INFLACION_GALOPEANTE then Decision is PUT", engine));
        mamdani.addRule(Rule.parse("if Tendencia is ALCISTA and Conveniencia is BUENA and Inflacion is HIPERINFLACION then Decision is NO", engine));
        engine.addRuleBlock(mamdani);
        //double number1 = Double.parseDouble(conve);
        //double number2 = Double.parseDouble(tende);
        //double number3 = Double.parseDouble(infla);
        
        engine.setInputValue("Tendencia", this.Tendencia);
        engine.process();
        engine.setInputValue("Conveniencia", this.Conveniencia);
        engine.process();
        engine.setInputValue("Inflacion", this.Inflacion);
        engine.process();
        double decision = engine.getOutputValue("Decision");
        
        return decision;

}
}

