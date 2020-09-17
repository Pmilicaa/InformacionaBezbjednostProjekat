package ib.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ib.project.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();

        http.authorizeRequests()
                .antMatchers("/admin").hasAuthority("ADMIN")
                .antMatchers("/user").hasAnyAuthority("ADMIN", "REGULAR")
                .antMatchers("/all").permitAll()
                .antMatchers("/active").hasAuthority("ADMIN")
                .antMatchers("/users/register").permitAll()
                .antMatchers("/users.html").hasAnyAuthority("ADMIN", "REGULAR")
                .antMatchers("/login.html").hasAnyAuthority("ADMIN", "REGULAR")
                .antMatchers("/register.html").hasAnyAuthority("ADMIN", "REGULAR")
                .antMatchers("/download/*").hasAnyAuthority("ADMIN", "REGULAR")
                .antMatchers("/login").permitAll()
                .antMatchers("/login.html").permitAll()
                .and().formLogin().defaultSuccessUrl("/users.html", true);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}