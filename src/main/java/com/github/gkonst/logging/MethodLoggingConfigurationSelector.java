package com.github.gkonst.logging;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;

public class MethodLoggingConfigurationSelector extends AdviceModeImportSelector<EnableMethodLoggingFeature> {
    @Override
    protected String[] selectImports(AdviceMode adviceMode) {
        switch (adviceMode) {
            case PROXY:
                return new String[]{AutoProxyRegistrar.class.getName(), AutoProxyMethodLoggingConfiguration.class.getName()};
            case ASPECTJ:
                return new String[]{AspectJMethodLoggingConfiguration.class.getName()};
            default:
                return null;
        }
    }
}
