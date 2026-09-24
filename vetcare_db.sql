-- ============================================================
-- VetCare -- Base de datos MySQL
-- Ejecutar en MySQL Workbench o phpMyAdmin antes de correr la app
-- ============================================================

CREATE DATABASE IF NOT EXISTS vetcare_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_spanish_ci;

USE vetcare_db;

CREATE TABLE IF NOT EXISTS duenos (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    dni       VARCHAR(20)  NOT NULL UNIQUE,
    telefono  VARCHAR(20),
    correo    VARCHAR(100),
    direccion VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS veterinarios (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    dni          VARCHAR(20)  NOT NULL,
    telefono     VARCHAR(20),
    correo       VARCHAR(100),
    especialidad VARCHAR(100),
    colegiatura  VARCHAR(50),
    activo       BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS mascotas (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    especie        VARCHAR(20)  NOT NULL,
    nombre         VARCHAR(100) NOT NULL,
    raza           VARCHAR(100),
    edad           INT,
    sexo           VARCHAR(10),
    peso           DOUBLE,
    dni_dueno      VARCHAR(20),
    observaciones  TEXT,
    atributo_extra VARCHAR(100),
    FOREIGN KEY (dni_dueno) REFERENCES duenos(dni)
);

CREATE TABLE IF NOT EXISTS citas (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    id_mascota          INT  NOT NULL,
    id_veterinario      INT  NOT NULL,
    fecha               VARCHAR(20) NOT NULL,
    hora                VARCHAR(10) NOT NULL,
    motivo              TEXT,
    estado              VARCHAR(20) DEFAULT 'PENDIENTE',
    motivo_cancelacion  TEXT,
    FOREIGN KEY (id_mascota)     REFERENCES mascotas(id),
    FOREIGN KEY (id_veterinario) REFERENCES veterinarios(id)
);

CREATE TABLE IF NOT EXISTS consultas (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    id_mascota    INT NOT NULL,
    fecha         VARCHAR(20) NOT NULL,
    diagnostico   TEXT NOT NULL,
    tratamiento   TEXT,
    observaciones TEXT,
    FOREIGN KEY (id_mascota) REFERENCES mascotas(id)
);

-- Usuarios del sistema (RF-01, RF-03, RF-04). La clave se guarda cifrada (PBKDF2).
-- La aplicacion crea el usuario inicial admin / vetcare2026 la primera vez que se ejecuta.
CREATE TABLE IF NOT EXISTS usuarios (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50)  NOT NULL UNIQUE,
    clave   VARCHAR(200) NOT NULL,
    rol     VARCHAR(20)  NOT NULL
);

-- ============================================================
-- DATOS DE PRUEBA
-- ============================================================

INSERT INTO duenos (nombre, dni, telefono, correo, direccion) VALUES
 ('Carmen Rosa Delgado Vega', '45782301', '987451220', 'carmen.delgado@gmail.com', 'Av. Larco 1245, Trujillo'),
 ('Jorge Luis Mendoza Rios',  '41203956', '944870115', 'jl.mendoza@hotmail.com',   'Jr. Bolivar 380, Trujillo'),
 ('Ana Lucia Paredes Soto',   '70918342', '912663408', 'ana.paredes@outlook.com',  'Urb. El Golf Mz. F Lt. 12'),
 ('Miguel Angel Castro Nunez','09876543', '956112789', 'macastro@gmail.com',       'Av. Espana 755, Trujillo');

INSERT INTO veterinarios (nombre, dni, telefono, correo, especialidad, colegiatura, activo) VALUES
 ('Dra. Patricia Salazar Huaman', '18204517', '949220118', 'p.salazar@vetcare.pe', 'Medicina Interna',   'CMVP-4821', TRUE),
 ('Dr. Ricardo Flores Aguilar',   '43015678', '987330294', 'r.flores@vetcare.pe',  'Cirugia de Tejidos', 'CMVP-5307', TRUE),
 ('Dra. Elena Quiroz Tapia',      '72564890', '933507162', 'e.quiroz@vetcare.pe',  'Animales Exoticos',  'CMVP-6014', TRUE);

INSERT INTO mascotas (especie, nombre, raza, edad, sexo, peso, dni_dueno, observaciones, atributo_extra) VALUES
 ('Perro', 'Rocky',   'Labrador Retriever', 4, 'Macho',  28.5, '45782301', 'Vacunas al dia. Alergia leve al polen.', 'Grande'),
 ('Perro', 'Luna',    'Shih Tzu',           2, 'Hembra',  6.2, '41203956', 'Control de peso mensual.',              'Pequeno'),
 ('Gato',  'Michi',   'Siames',             3, 'Macho',   4.8, '45782301', 'Castrado en marzo de 2025.',            'true'),
 ('Gato',  'Nala',    'Mestizo',            1, 'Hembra',  3.1, '70918342', 'Ingreso por rescate. Sin castrar.',     'false'),
 ('Ave',   'Kiko',    'Periquito Australiano', 2, 'Macho', 0.04,'70918342', 'Muda de plumaje normal.',              'Corto conico'),
 ('Ave',   'Paco',    'Loro Cabeza Roja',   5, 'Macho',  0.35, '09876543', 'Vocaliza con frecuencia. Dieta mixta.', 'Curvo fuerte');

INSERT INTO citas (id_mascota, id_veterinario, fecha, hora, motivo, estado, motivo_cancelacion) VALUES
 (1, 1, '25/09/2026', '09:00', 'Control anual y refuerzo de vacuna antirrabica', 'PENDIENTE', NULL),
 (3, 1, '25/09/2026', '10:30', 'Revision post castracion',                       'ATENDIDA',  NULL),
 (2, 2, '26/09/2026', '11:00', 'Evaluacion de sobrepeso y plan nutricional',     'PENDIENTE', NULL),
 (5, 3, '26/09/2026', '16:00', 'Consulta por muda anormal de plumaje',           'ATENDIDA',  NULL),
 (4, 2, '24/09/2026', '15:30', 'Primera consulta tras rescate',                  'CANCELADA', 'El dueno reprogramo por viaje imprevisto'),
 (6, 3, '27/09/2026', '09:30', 'Control de pico y unas',                         'PENDIENTE', NULL);

INSERT INTO consultas (id_mascota, fecha, diagnostico, tratamiento, observaciones) VALUES
 (3, '25/09/2026', 'Cicatrizacion correcta de herida quirurgica', 'Retiro de puntos. Collar isabelino por 3 dias mas.', 'Sin signos de infeccion.'),
 (5, '26/09/2026', 'Muda estacional dentro de parametros normales', 'Suplemento vitaminico A y D por 30 dias.',        'Se recomienda bano de sol diario.'),
 (1, '18/09/2026', 'Dermatitis alergica estacional leve',          'Antihistaminico oral por 7 dias.',                 'Reevaluar si persiste el prurito.'),
 (2, '12/09/2026', 'Sobrepeso grado 1',                            'Dieta hipocalorica y 20 min de caminata diaria.',  'Control de peso en 30 dias.');
