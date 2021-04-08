package com.ecomm.catalogservice

import com.ecomm.catalogservice.security.CustomUserDetailsService
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.PasswordEncoder

@EnableWebSecurity
@Configuration
class SecurityConfig(
    private val customUserDetailsService: CustomUserDetailsService,
    private val passwordEncoderAndMatcher: PasswordEncoder
    ): WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.httpBasic()
            .and()
            .authorizeRequests()
                // permit all methods
            //.antMatchers("/api/products/open/**").permitAll()
                // permit GET only
            .antMatchers(HttpMethod.GET,"/products").permitAll()
            .antMatchers(HttpMethod.GET,"/products/**").permitAll()
            .antMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")
            .antMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/orders/**").hasRole("CUSTOMER")
            .antMatchers(HttpMethod.POST, "/orders/**").hasRole("CUSTOMER")
            .antMatchers(HttpMethod.DELETE, "/orders/**").hasRole("CUSTOMER")
            .antMatchers(HttpMethod.GET, "/warehouses").hasRole("ADMIN")
            .antMatchers(HttpMethod.POST, "/warehouses/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/wallet").hasRole("CUSTOMER")
            .antMatchers(HttpMethod.GET, "/wallet/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.POST, "/wallet/**").hasRole("ADMIN")
            //.anyRequest().authenticated()
            .and()
            .csrf().disable()

        //permit all
        //http.authorizeRequests().anyRequest().authenticated();
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
		auth.userDetailsService(customUserDetailsService)
				.passwordEncoder(passwordEncoderAndMatcher)
	}

//    @Bean
//    fun encoder(): PasswordEncoder {
//        return BCryptPasswordEncoder()
//    }
//
//    override fun configure(auth: AuthenticationManagerBuilder) {
//        auth.inMemoryAuthentication()
//                .withUser("admin")
//                .password(encoder().encode("pass"))
//                .roles("USER", "ADMIN")
//                .and()
//                .withUser("user")
//                .password(encoder().encode("pass"))
//                .roles("USER")
//                .and()
//                .withUser("user2")
//                .password(encoder().encode("pass2"))
//                .roles("USER")
//    }


    // plug your UserDetailsService
    // https://prog.world/rest-api-authentication-with-spring-security-and-mongodb/
}

    @Configuration
    @EnableGlobalMethodSecurity(
        jsr250Enabled = true,
        prePostEnabled = true, // allow to preauth the request and
        securedEnabled = true,)
    class MethodSecurityConfig: GlobalMethodSecurityConfiguration() {}