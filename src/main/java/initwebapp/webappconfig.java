package initwebapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import controller.jwt.*;
import controller.CustomerValidator;
import controller.ManageUser;
import controller.userloginmanage.*;;

@EnableWebMvc
// @Import({ securityconfig2.class })
@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = "controller")
public class webappconfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean(name = "tokenmanager")
    public Tokenmanager Tokenmanager() {
        return new Tokenmanager();
    }

    @Bean
    public jwtfilter jwtfilter() {
        return new jwtfilter();
    }

    @Bean
    public ManageUser manageuser() {
        return new ManageUser();
    }

    @Bean
    UserDetailImpl UserDetailImpl() {
        return new UserDetailImpl();
    }

    @Bean
    public jwtauthenticationentrypoint jwtauthenticationentrypoint() {
        return new jwtauthenticationentrypoint();
    }

    @Bean
    public DaoAuthenticationProvider authProvider(UserDetailImpl UserDetailImpl) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(UserDetailImpl);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChainjwt(HttpSecurity http) throws Exception {

        http.csrf().disable().authorizeRequests().antMatchers(
                "/register/registerform", "/register/submitregister")
                .anonymous().and()
                .authorizeRequests()
                .antMatchers("/register/verification", "/resources/static/*")
                .permitAll()
                .and().authorizeRequests().antMatchers("//getindex").hasAnyAuthority("customer").and()
                .formLogin().and()
                .authorizeRequests().antMatchers("//logincomputerstore").hasAnyAuthority("customer").and()
                .formLogin()
                .and()
                .sessionManagement().maximumSessions(1);

        // .exceptionHandling().authenticationEntryPoint(ne
        // LoginUrlAuthenticationEntryPoint("/login"));// .and()
        //
        // .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // http
        // .addFilterBefore(jwtfilter(),
        // UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        SessionRegistry sessionRegistry = new SessionRegistryImpl();
        return sessionRegistry;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    @Bean(name = "CustomerValidator")
    public CustomerValidator validator() {
        return new CustomerValidator();
    }
}
