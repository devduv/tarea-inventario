## Supuestos

- Cuando la app reenvía un pedido con el mismo orderId, entonces el servicio devuelve la reserva existente y no un mensaje de error.
- Cuando no encuentra un producto por sku, entonces el servicio devuelve un mensaje de error.
- Cuando se realiza una reserva, se valida el límite de órdenes de la categoría del producto, en caso de exceder, entonces el servicio devuelve un mensaje de error.
- Cuando se realiza una reserva, una confirmación o una consulta de disponibilidad del producto, se revisa si la reserva ya expiró su tiempo y se libera las unidades disponibles.
- Cuando se realiza una reserva y no ya ha sido confirmada, entonces el servicio devuelve la misma reserva.
- Cuando se confirma una reserva otra vez sobre una reserva ya confirmada, entonces no se ejecuta nada y el servicio no devuelve nada.
- Cuando se dispara una alerta se ejecuta sin enviarlo a un tópico.
- Cuando se calcula el stock disponible, se validan las órdenes disponibles del producto, si alguna orden ya fue confirmada o ha expirado, entonces se elimina de la lista de órdenes disponibles. Si no ha sido confirmada y ha expirado se elimina de la lista del estado de órdenes.
- Cuando se realizan varias solicitudes de reservación o confirmación de forma concurrente, se usa un bloque sincronizado para evitar que múltiples hilos procesen simultáneamente esta acción.
## Lo que dejé fuera
- Una base de datos para la persistencia.
- Redis u otra forma de almacenar memoria en caché.
- Base de datos en memoria, solo se usó ConcurrentHashMap para la memoria en tiempo de ejecución. La memoria en tiempo de ejecución es segura cuando se usa una sola instancia, pero no se recomendaría para un entorno productivo con múltiples instancias.
- Uso de eventos para enviar las alertas.
- Crear y exponer una API con los métodos REST respectivos.
- Uso de SPRING para la construcción de un web service.
- Usar clases Entity para mapearlo en las clases repository.
- Usar Mappers, no se agregó debido a que se está usando la misma clase modelo.
- Persistencia de las órdenes por separado, se persiste el estado de las órdenes según su confirmación (pagado).
- Actualizar los estados de las reservaciones y no eliminar las reservaciones expiradas.

## Qué cambiaría antes de producción

- Usaría una base de datos relacional y una restricción única sobre orderId.
- Las reservas vencidas deberían liberarse de forma asíncrona con un job que se ejecuta cada cierta hora.
- El envío de alertas debería manejarlo otro servicio, publicando el evento en una cola (Kafka, RabbitMQ, etc.) y otro proceso consumiría ese evento y enviaría el correo.
- Con una arquitectura orientada a eventos, en caso de error del envío de la alerta, no afectará el flujo de reserva. }
- Las reglas de las categorías podrían migrar a una base de datos para evitar tenerlas hard-codeadas.
- Agregaría clases entity y mappers para mantener limpio el código al momento de crear de instancias.
- Crearía los métodos REST respectivos para cada caso. 
- Para un entorno distribuido, reemplazaría el bloque sincronizado por otro mecanismo de concurrencia, ejm: RxJava o WebFlux.
- Con una base de datos usaríamos @Transactional para manejar las transacciones y asegurar la consistencia.
- Para alto tráfico se podría migrar a una arquitectura de microservicios para separar las responsabilidades:
- API de inventarios sería una API de experiencia para front-end y este llamaría a las API's que llevarían la lógica de negocio: API Reservaciones y API Productos y API Alertas.
- API de inventario no se conectaría con la base de datos, solo las API con lógica de negocio.
- API de inventario podría usar Redis como caché para almacenar temporalmente datos durante la sesión del usuario.
- Guardaría las órdenes actualizando el estado a "EXPIRADO" para mantener el histórico de reservaciones realizadas.