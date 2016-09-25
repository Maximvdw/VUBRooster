package be.vubrooster.ejb.persistence;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import java.util.List;

public class GroupConcatFunction implements SQLFunction {

    @Override
    public boolean hasArguments() {
        return true;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return true;
    }

    @Override
    public Type getReturnType(Type type, Mapping mapping) throws QueryException {
        return StandardBasicTypes.STRING;
    }

    @Override
    public String render(Type type, List list, SessionFactoryImplementor sessionFactoryImplementor) throws QueryException {
        if (list.size() != 1) {
            throw new QueryException(new IllegalArgumentException(
                    "group_concat should have one arg"));
        }
        return "group_concat(" + list.get(0) + ")";
    }
}