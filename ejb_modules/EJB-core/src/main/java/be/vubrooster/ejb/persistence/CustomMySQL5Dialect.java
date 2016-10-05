package be.vubrooster.ejb.persistence;

import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

/**
 * CustomMySQL5Dialect
 * Created by maxim on 05-Oct-16.
 */
public class CustomMySQL5Dialect extends MySQL5Dialect {

    public CustomMySQL5Dialect() {
        super();
        this.registerFunction("group_concat", new SQLFunctionTemplate(StandardBasicTypes.STRING, "group_concat(?1)"));
    }
}