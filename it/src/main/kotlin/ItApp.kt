package github.chickenbane.floo

import org.junit.platform.console.ConsoleLauncher
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ItApp {
    @Bean
    fun runner() = ApplicationRunner {
        val argList: List<String> = mutableListOf(*it.sourceArgs).apply {
            add("-p")
            add("github.chickenbane.floo.it")

            add("--details")
            add("verbose")
        }
        val args: Array<out String> = argList.toTypedArray()
        ConsoleLauncher.main(*args)
    }
}

fun main(args: Array<String>) {
    runApplication<ItApp>(*args)
}


