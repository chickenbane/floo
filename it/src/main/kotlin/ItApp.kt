package github.chickenbane.floo

import org.junit.platform.console.ConsoleLauncher
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SpringBootApplication
class ItApp

fun main(args: Array<String>) {
    runApplication<ItApp>(*args)
}

@Configuration
class ItConfig {
    @Bean
    fun runner() = ApplicationRunner { ConsoleLauncher.main(*it.sourceArgs) }
}

