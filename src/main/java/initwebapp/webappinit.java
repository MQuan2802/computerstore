package initwebapp;

import org.springframework.web.*;
import javax.servlet.*;
import org.springframework.web.context.support.*;
import org.springframework.web.context.*;
import org.springframework.web.servlet.*;

import org.springframework.core.annotation.*;

public class webappinit implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext container) {
        // Create the 'root' Spring application context
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();

        rootContext.register(initwebapp.webappconfig.class);
        FilterRegistration.Dynamic charencode = container
                .addFilter("CharacterEncodingFilter",
                        org.springframework.web.filter.CharacterEncodingFilter.class);
        charencode.addMappingForUrlPatterns(null, false, "/*");
        charencode.setInitParameter("encoding", "UTF-8");
        charencode.setInitParameter("forceEncoding", "true");
        charencode.setAsyncSupported(true);
        // Manage the lifecycle of the root application context
        container.addListener(new ContextLoaderListener(rootContext));

        // Create the dispatcher servlet's Spring application context
        AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
        // dispatcherContext.register(appconfig.class);

        dispatcherContext.register(initwebapp.webappconfig.class);

        ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcher",
                new DispatcherServlet(dispatcherContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}