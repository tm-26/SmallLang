package Nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ExpressionNode implements Visitable {
    Object[] values;

    public ExpressionNode(Object[] values){
        this.values = values;
    }

    public Object[] getValues() {
        return values;
    }

    public Object[] getSimpleExpressions(){
        return values;
    }

    public Object[] getTerms(){
        ArrayList<Object> terms = new ArrayList<>();
        for(Object value : values){
            if(value instanceof SimpleExpressionNode){
                terms.addAll(Arrays.asList(((SimpleExpressionNode) value).getValues()));
            }
        }
        return terms.toArray();
    }

    public Object[] getFactors(){
        ArrayList<Object> factors = new ArrayList<>();
        Object[] terms = this.getTerms();
        for(Object term : terms){
            if(term instanceof TermNode){
                factors.addAll(Arrays.asList(((TermNode) term).getValues()));
            } else{
              factors.add(term);
            }
        }
        return factors.toArray();
    }

    @Override
    public void accept(Visitor v)  {
        v.visit(this);
    }

    public String[] getLiteralValues(){
        ArrayList<Object> values = new ArrayList<>();
        for(Object simpleExpression: getValues()) {
            if (simpleExpression instanceof SimpleExpressionNode) {
                for (Object term : ((SimpleExpressionNode) simpleExpression).getValues()) {
                    if (term instanceof TermNode) {
                        for (Object factor : ((TermNode) term).getValues()) {
                            if (factor instanceof FactorNode) {
                                Object factors = ((FactorNode) factor).getLiteralValue();
                                if(factors.getClass().isArray()) {
                                    values.add("(");
                                    values.addAll(Arrays.asList((String[]) factors));
                                    values.add(")");
                                } else if(factors instanceof ArrayList) {
                                    values.addAll((Collection<?>) factors);
                                    // Add end of parameters token
                                    values.add("<EOP>");
                                } else {
                                    values.add(factors);
                                }
                            } else {
                                values.add(factor);
                            }
                        }
                    } else {
                        values.add(term);
                    }
                }
            } else{
                values.add(simpleExpression);
            }
        }
        String[] sValues = new String[values.size()];
        for(int i = 0; i < values.size(); i++){
            sValues[i] = (String) values.get(i);
        }
        return sValues;
    }
}
