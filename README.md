# App Ventas

Sistema de gestión de ventas con control de stock, kardex, facturación y auditoría.

## Stack

- Java 21 + Spring Boot 4.1
- Thymeleaf + Bootstrap 5.3.2
- MariaDB / MySQL
- Maven

## Requisitos

- Java 21+ (para ejecución local)
- Docker + Docker Compose (para ejecución con contenedores)
- MariaDB 10.6+ (si no usas Docker)

## Ejecutar con Docker (recomendado)

```bash
docker compose up -d --build
```

La app estará en `http://localhost:8080`.

> **Nota:** Si solo cambias código fuente, `docker compose up -d --build` recompila desde cero. Para desarrollo más rápido usa `./mvnw spring-boot:run` local (con MariaDB corriendo).

Para detener:

```bash
docker compose down
```

## Ejecutar localmente

1. Asegúrate de tener MariaDB corriendo en `localhost:3306`
2. Crea la base de datos `ventas_db`
3. Ejecuta:

```bash
./mvnw spring-boot:run
```

O construye el WAR y ejecútalo:

```bash
./mvnw package -DskipTests
java -jar target/app-ventas-1.0.0.war
```

## Credenciales por defecto

| Usuario | Password | Rol |
|---------|----------|-----|
| admin | admin | Superusuario |

## Puerto

Por defecto corre en `http://localhost:8080`.

## Funcionalidades

- Login con Spring Security + BCrypt
- Gestión de roles y permisos (árbol de funcionalidades con Ver/Crear/Editar/Eliminar/Imprimir)
- CRUD de clientes, categorías, productos
- Ingreso de productos (actualiza stock y kardex)
- Ventas con factura (IGV 18%) y boleta, correlativos automáticos (F001/B001)
- Control de stock (unidades y fracciones) con conversión automática
- Kardex (trazabilidad de movimientos)
- Auditoría (JSON, IP, navegador, equipo)
- Cifrado DES en datos sensibles del cliente
- Docker Compose listo para producción
