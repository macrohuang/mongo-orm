package github.macrohuang.orm.mongo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used in the _id filed, instead of ObjectId, it must be an String, with the
 * value of ObjectId.
 * 
 * @author Macro Huang
 * 
 */
@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MongoId {

}
