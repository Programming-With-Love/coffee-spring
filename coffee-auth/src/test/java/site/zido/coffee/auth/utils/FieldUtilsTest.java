package site.zido.coffee.auth.utils;

import org.junit.Assert;
import org.junit.Test;
import site.zido.coffee.auth.utils.pojo.PrivateFieldPOJO;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FieldUtilsTest {
    @Test
    public void testGetSetterMethodByField() throws NoSuchFieldException {
        Field nameField = PrivateFieldPOJO.class.getDeclaredField("name");
        Method nameSetterMethod = FieldUtils.getSetterMethodByField(nameField, PrivateFieldPOJO.class);
        Assert.assertNotNull("the setter method of name is null", nameSetterMethod);
        Assert.assertEquals(nameSetterMethod.getName(), "setName");

        Field camelCaseField = PrivateFieldPOJO.class.getDeclaredField("privateField");
        Method camelCaseFieldSetterMethod = FieldUtils.getSetterMethodByField(camelCaseField, PrivateFieldPOJO.class);
        Assert.assertNotNull("the setter method of camel case field is null", camelCaseFieldSetterMethod);
        Assert.assertEquals(camelCaseFieldSetterMethod.getName(), "setPrivateField");
    }

    @Test
    public void testGetGetterMethodByField() throws NoSuchFieldException {
        Field nameField = PrivateFieldPOJO.class.getDeclaredField("name");
        Method nameGetterMethod = FieldUtils.getGetterMethodByField(nameField, PrivateFieldPOJO.class);
        Assert.assertNotNull("the getter method of name is null", nameGetterMethod);
        Assert.assertEquals(nameGetterMethod.getName(), "getName");

        Field camelCaseField = PrivateFieldPOJO.class.getDeclaredField("privateField");
        Method camelCaseFieldGetterMethod = FieldUtils.getGetterMethodByField(camelCaseField, PrivateFieldPOJO.class);
        Assert.assertNotNull("the getter method of camel case field is null", camelCaseFieldGetterMethod);
        Assert.assertEquals(camelCaseFieldGetterMethod.getName(), "getPrivateField");
    }

    @Test
    public void testInjectField() throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        PrivateFieldPOJO pojo = PrivateFieldPOJO.class.newInstance();
        Field nameField = PrivateFieldPOJO.class.getDeclaredField("name");
        String nameValue = "xx";
        FieldUtils.injectField(nameField, pojo, nameValue);
        Assert.assertEquals("inject xx to name failed", pojo.getName(), nameValue);
    }

    @Test
    public void testInjectFieldBySetter() throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        PrivateFieldPOJO pojo = PrivateFieldPOJO.class.newInstance();
        Field nameField = PrivateFieldPOJO.class.getDeclaredField("name");
        String nameValue = "xx";
        FieldUtils.injectFieldBySetter(nameField, pojo, nameValue);
        Assert.assertEquals("inject xx to name failed", pojo.getName(), nameValue);
    }

}
