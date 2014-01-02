package org.kframework.backend.java.kil;

import com.google.common.collect.ImmutableList;

import org.kframework.backend.java.symbolic.Unifier;
import org.kframework.backend.java.symbolic.Transformer;
import org.kframework.backend.java.symbolic.Visitor;
import org.kframework.kil.ASTNode;


/**
 * Represents a list of K, or {@link KSequence} in the Java backend. Or in the
 * usual syntax of K, it can be defined as the following:
 * <p>
 * <blockquote>
 * 
 * <pre>
 * syntax KList ::= List{K}{","}
 * </pre>
 * 
 * </blockquote>
 * <p>
 * 
 * @author AndreiS
 */
@SuppressWarnings("serial")
public class KList extends KCollection {

    private static final String SEPARATOR_NAME = ",, ";
    private static final String IDENTITY_NAME = "." + Kind.KLIST;

    public KList(ImmutableList<Term> items, Variable frame) {
        super(items, frame, Kind.KLIST);
    }

    /*
    public KList(ListIterator<Term> itemsIterator, Variable frame) {
        super(itemsIterator, frame, "KList");
    }
    */

    public KList(Variable frame) {
        super(frame, Kind.KLIST);
    }

    public KList(ImmutableList<Term> items) {
        super(items, null, Kind.KLIST);
    }

    /*
    public KList(ListIterator<Term> itemsIterator) {
        super(itemsIterator, null, "KList");
    }
    */

    public KList() {
        super(null, Kind.KLIST);
    }

    @Override
    public KCollection fragment(int fromIndex) {
        return new KList(contents.subList(fromIndex, contents.size()), frame);
    }

    @Override
    public String getSeparatorName() {
        return KList.SEPARATOR_NAME;
    }

    @Override
    public String getIdentityName() {
        return KList.IDENTITY_NAME;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof KList)) {
            return false;
        }

        KList kList = (KList) object;
        return (super.frame == null ? kList.frame == null : frame.equals(kList.frame))
                && super.contents.equals(kList.contents);
    }

    @Override
    public void accept(Unifier unifier, Term patten) {
        unifier.unify(this, patten);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ASTNode accept(Transformer transformer) {
        return transformer.transform(this);
    }

}
