package be.uantwerpen.sc.configurations;

import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Created by Thomas on 14/04/2016.
 */
@Profile("bot")
@Configuration
@Import(EmbeddedServletContainerAutoConfiguration.class)
public class BotConfiguration
{
	//Configurations for Bot (default) mode
}
