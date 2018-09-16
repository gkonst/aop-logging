package com.github.gkonst.logging;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

@Configuration
public abstract class AbstractMethodLoggingConfiguration implements ImportAware {

    protected AnnotationAttributes enableLoggingFeature;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableLoggingFeature = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableMethodLoggingFeature.class.getName(), false));
        if (this.enableLoggingFeature == null) {
            throw new IllegalArgumentException(
                    "@EnableMethodLoggingFeature is not present on importing class " + importMetadata.getClassName());
        }
    }
}
