package io.bootique.jersey.client;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import io.bootique.ConfigModule;
import io.bootique.config.ConfigurationFactory;

import javax.ws.rs.core.Feature;
import java.util.Set;

public class JerseyClientModule extends ConfigModule {

    /**
     * Returns an instance of {@link JerseyClientModuleExtender} used by downstream modules to load custom extensions of
     * services declared in the JerseyClientModule. Should be invoked from a downstream Module's "configure" method.
     *
     * @param binder DI binder passed to the Module that invokes this method.
     * @return an instance of {@link JerseyClientModuleExtender} that can be used to load JerseyClientModule extensions.
     * @since 0.9
     */
    public static JerseyClientModuleExtender extend(Binder binder) {
        return new JerseyClientModuleExtender(binder);
    }

    /**
     * @param binder DI binder passed to the Module that invokes this method.
     * @return returns a {@link Multibinder} for client-side JAX-RS Features.
     * @since 0.3
     * @deprecated since 0.21 call {@link #extend(Binder)} and then call
     * {@link JerseyClientModuleExtender#addFeature(Feature)}.
     */
    @Deprecated
    public static Multibinder<Feature> contributeFeatures(Binder binder) {
        return Multibinder.newSetBinder(binder, Feature.class, JerseyClientFeatures.class);
    }

    @Override
    public void configure(Binder binder) {
        extend(binder).initAllExtensions();
    }

    @Provides
    @Singleton
    HttpClientFactory createClientFactory(ConfigurationFactory configurationFactory, Injector injector,
                                          @JerseyClientFeatures Set<Feature> features) {
        return configurationFactory.config(HttpClientFactoryFactory.class, configPrefix).createClientFactory(injector,
                features);
    }
}
