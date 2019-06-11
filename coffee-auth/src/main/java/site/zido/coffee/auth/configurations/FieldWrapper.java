package site.zido.coffee.auth.configurations;

import java.lang.reflect.Field;

public class FieldWrapper {
    private String name;
    private Field field;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }
}