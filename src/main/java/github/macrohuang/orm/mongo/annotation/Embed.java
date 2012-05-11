package github.macrohuang.orm.mongo.annotation;

import github.macrohuang.orm.mongo.base.po.EmbedPO;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Support Embed document. Such as
 * DBObject[{"name":"name","age":12,"address":{"province"
 * :"province","city":"city"}}] can be mapped into a Simple Object
 * P[name,age,province,city]
 * 
 * @see EmbedPO for more details. {@link EmbedPO}
 * 
 * @author huangtianlai
 * 
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Embed {
	String parent();
}
