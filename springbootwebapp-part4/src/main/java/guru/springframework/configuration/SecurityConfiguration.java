package guru.springframework.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
//@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests().antMatchers("/").permitAll().and()
                .authorizeRequests().antMatchers("/console/**").permitAll();

        
//        httpSecurity.authorizeRequests().antMatchers("/").permitAll()
//        	.anyRequest().authenticated()
//        	.and()
//        .formLogin()
//        	.loginPage("/")
//        	.permitAll()
//        	.and()
//        .logout()
//        	.permitAll()
//        .and().csrf();
       
        
        
        
        httpSecurity.csrf().disable();
        httpSecurity.headers().frameOptions().disable();
    }
    
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//    	auth.inMemoryAuthentication()
//    		.withUser("user").password("password").roles("USER");
//    }

}