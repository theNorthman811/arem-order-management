package com.arem.api;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages={"com.arem.api.controllers", "com.arem.dataservice.services", "com.arem.framework.runtime",
"com.arem.dataservice.repositories", "com.arem.api.providers", "com.arem.api.security", "com.arem.productInput.rules"})

@EntityScan(basePackages = {"com.arem.core"})
@EnableJpaRepositories("com.arem.dataservice.repositories")
public class App 
{
    public static void main( String[] args )
    {      	
        SpringApplication.run(App.class, args);
    }
}
