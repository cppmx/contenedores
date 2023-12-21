<?php

header('Content-Type: text/json; charset=utf-8');

echo json_encode([
    "alumno" => "Carlos Colón",
    "materia" => "Contenedores",
    "maestria" => "Desarrollo y Operaciones de Software (DevOps)",
    "mensaje" => "Hello from PHP API!",
    "universidad" => 'UNIR'
], JSON_UNESCAPED_UNICODE);
