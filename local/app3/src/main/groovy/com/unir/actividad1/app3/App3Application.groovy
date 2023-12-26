package com.unir.actividad1.app3

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class App3Application {

    static void main(String[] args) {
        SpringApplication.run(App3Application, args)
    }

}

@RestController
class MyController {

    @GetMapping("/")
    @ResponseBody
    Map<String, String> hello() {
        return [
            'alumno': 'Carlos Colón',
            'materia': 'Contenedores',
            'maestria': 'Desarrollo y Operaciones de Software (DevOps)',
            'mensaje': 'Hello from Groovy API!',
            'universidad': 'UNIR'
        ]
    }
}
