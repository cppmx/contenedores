use Dancer2;
use JSON;

get '/' => sub {
    my $response_data = {
        alumno => 'Carlos Colón',
        materia => 'Contenedores',
        maestria => 'Desarrollo y Operaciones de Software (DevOps)',
        mensaje => 'Hello from Perl API!',
        universidad => 'UNIR'
    };

    # Configura el tipo de contenido a JSON
    content_type 'application/json; charset=utf-8';

    # Serializa la estructura de datos a JSON y devuelve la respuesta
    return to_json($response_data);
};

start;
