package com.re.paas.api.realms;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface RealmSpec {

	/**
	 * This specifies the parent realm from which functionalities are inherited
	 * Also, a given realm can only inherit sections from it's parent
	 * 
	 * @return
	 */
	Class<Realm> parent() default Realm.class;

	/**
	 * Indicates whether this realm exists as a stand-alone realm or is designed to
	 * add sections and functionalities to an existing realm
	 * 
	 * @return
	 */
	boolean standalone() default true;
}
