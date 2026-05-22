package com.ptcg.server.factory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CardDefinition {
    /** Single card key (convenience, use cardKeys for multiple). */
    String cardKey() default "";

    /** All card keys that map to this class (for rarities/prints of the same card). */
    String[] cardKeys() default {};

    /** Default set for seeding (overridden by CardMetadata DB). */
    String set() default "";

    /** Default name for seeding (overridden by CardMetadata DB). */
    String name() default "";
}
