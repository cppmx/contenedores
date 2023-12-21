package com.unir.actividad1.app4

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class App1Application

fun main(args: Array<String>) {
	runApplication<App1Application>(*args)
}

fun App1Application.module() {
    Controller()
}
