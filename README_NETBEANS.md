# VetCare_Desarrollo — Proyecto NetBeans

Proyecto Java Swing convertido a estructura compatible con **NetBeans + Ant**.

## Requisitos
- JDK 17 o superior.
- NetBeans con soporte para proyectos Java/Ant.
- MySQL Server.

## 1. Crear la base de datos

Abrir MySQL Workbench (o phpMyAdmin) y ejecutar el archivo:

`vetcare_db.sql`

Esto crea la base de datos `vetcare_db` y las tablas necesarias.

## 2. Configuración de MySQL

La aplicación está configurada actualmente con:

- Host: `localhost`
- Puerto: `3306`
- Base de datos: `vetcare_db`
- Usuario: `root`
- Contraseña: `vetcare2026`

Si tu usuario `root` tiene otra contraseña, modifica `src/vetcare/db/ConexionDB.java`.

## 3. Abrir en NetBeans

1. Abrir NetBeans.
2. Seleccionar **File > Open Project**.
3. Seleccionar la carpeta `Desarrollo`.
4. NetBeans debe reconocer `VetCare_Desarrollo` como proyecto Java con Ant.
5. Presionar **Run / F6**.

La clase principal configurada para ejecutar la interfaz gráfica es:

`vetcare.gui.MainGUI`

## 4. Credenciales de acceso

- Usuario: `admin`
- Contraseña: `vetcare2026`

## 5. Si NetBeans no encuentra el JDK

Ir a **Tools > Java Platforms** y registrar el JDK instalado. El proyecto está configurado para Java 17 y también puede compilar con JDK superiores compatibles.

## 6. Usuarios y roles (RF-01 a RF-04)

- Al primer inicio la aplicación crea la tabla `usuarios` (también está en `vetcare_db.sql`) y el usuario `admin` / `vetcare2026` con rol administrador.
- Las contraseñas se guardan cifradas (PBKDF2). El administrador crea usuarios con **Nuevo usuario**; todos pueden usar **Cambiar contraseña** y **Cerrar sesión**.

## 7. Compilar y ejecutar el .jar

- En NetBeans: clic derecho en el proyecto > **Clean and Build**. Se genera `dist/VetCare_Desarrollo.jar` y `dist/lib/mysql-connector-j-8.0.33.jar`.
- Ejecutar: `java -jar dist/VetCare_Desarrollo.jar` (la carpeta `lib` debe quedar junto al .jar).
- Versión de consola: clase `vetcare.Main`.

## 8. Requerimientos

Los 40 requerimientos funcionales (RF-01 a RF-40) están implementados en la interfaz gráfica; ver el informe final para la matriz de trazabilidad.
