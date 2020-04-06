package test.dropwizard.config;

import test.dropwizard.resources.HelloResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import test.dropwizard.resources.HelloSubResource;
import test.dropwizard.resources.HelloSubSubResource;

public class DropwizardApplication extends Application<DropwizardConfig> {
    public static void main(final String[] args) throws Exception {
        new DropwizardApplication().run(args);
    }

    @Override
    public String getName() {
        return "DropwizardApplication";
    }

    @Override
    public void run(DropwizardConfig configuration, Environment environment) {
        environment.jersey().register(new HelloResource());
    }

    @Override
    public void initialize(Bootstrap bootstrap) {}
}
