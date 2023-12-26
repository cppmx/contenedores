package com.unir.actividad1.app4

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller {

    @GetMapping("/")
    @ResponseBody
    fun hello(): Map<String, String> {
        return mapOf(
            "alumno" to "Carlos Colón",
            "materia" to "Contenedores",
            "maestria" to "Desarrollo y Operaciones de Software (DevOps)",
            "mensaje" to "Hello from Kotlin API!",
            "universidad" to "UNIR"
        )
    }
}
