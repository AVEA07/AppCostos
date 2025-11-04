CREATE DATABASE IF NOT EXISTS SIGECO;
USE SIGECO;

-- 1. Rango
CREATE TABLE rango (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rango VARCHAR(50) NOT NULL,
    nivel INT NOT NULL
);

-- 2. Usuarios
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100),
    usuario VARCHAR(50) UNIQUE NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    rango_id INT,
    correo VARCHAR(100),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rango_id) REFERENCES rango(id)
);

-- 3. Programador
CREATE TABLE programador (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuarios_id INT NOT NULL,
    costo_hora DECIMAL(10,2),
    FOREIGN KEY (usuarios_id) REFERENCES usuarios(id)
);

-- 4. Tecnologia
CREATE TABLE tecnologia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    suma_porcentaje DECIMAL(5,2) DEFAULT 0
);

-- 5. Skill
CREATE TABLE skill (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tecnologia_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    suma_porcentaje DECIMAL(5,2) DEFAULT 0,
    FOREIGN KEY (tecnologia_id) REFERENCES tecnologia(id)
);

-- 6. Programador_Tecnologia (muchos a muchos)
CREATE TABLE programador_tecnologia (
    programador_id INT NOT NULL,
    tecnologia_id INT NOT NULL,
    nivel_conocimiento VARCHAR(50),
    PRIMARY KEY (programador_id, tecnologia_id),
    FOREIGN KEY (programador_id) REFERENCES programador(id),
    FOREIGN KEY (tecnologia_id) REFERENCES tecnologia(id)
);

-- 7. Complejidad
CREATE TABLE complejidad (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nivel ENUM('baja','media','alta') NOT NULL,
    cantidad_dias INT
);

-- 8. Proyecto
CREATE TABLE proyecto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_proyecto VARCHAR(100) NOT NULL,
    descripcion TEXT,
    complejidad_id INT,
    fecha_inicializacion DATE,
    FOREIGN KEY (complejidad_id) REFERENCES complejidad(id)
);

-- 9. Modulo
CREATE TABLE modulo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proyecto_id INT NOT NULL,
    nombre_modulo VARCHAR(100) NOT NULL,
    fecha_creacion DATE,
    FOREIGN KEY (proyecto_id) REFERENCES proyecto(id)
);

-- 10. Asignacion_Proyecto
CREATE TABLE asignacion_proyecto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuarios_id INT NOT NULL,
    proyecto_id INT NOT NULL,
    horas_trabajadas DECIMAL(10,2),
    FOREIGN KEY (usuarios_id) REFERENCES usuarios(id),
    FOREIGN KEY (proyecto_id) REFERENCES proyecto(id)
);

-- 11. Tipo_Host
CREATE TABLE tipo_host (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_host VARCHAR(100) NOT NULL
);

-- 12. Nombre_Host
CREATE TABLE nombre_host (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo_host_id INT NOT NULL,
    costo DECIMAL(10,2),
    FOREIGN KEY (tipo_host_id) REFERENCES tipo_host(id)
);

-- 13. Almacenamiento
CREATE TABLE almacenamiento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_host_id INT NOT NULL,
    detalles TEXT,
    FOREIGN KEY (nombre_host_id) REFERENCES nombre_host(id)
);

-- 14. Tipo_Documentacion
CREATE TABLE tipo_documentacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_documentacion VARCHAR(50) NOT NULL
);

-- 15. Documentacion
CREATE TABLE documentacion (
    proyecto_id INT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    ruta_archivo VARCHAR(255),
    fecha_creacion DATE,
    FOREIGN KEY (proyecto_id) REFERENCES proyecto(id)
);

-- 16. Asignacion_Documentacion
CREATE TABLE asignacion_documentacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proyecto_id INT NOT NULL,
    usuarios_id INT NOT NULL,
    rol VARCHAR(50),
    horas_trabajadas DECIMAL(10,2),
    tipo_documentacion_id INT,
    FOREIGN KEY (proyecto_id) REFERENCES proyecto(id),
    FOREIGN KEY (usuarios_id) REFERENCES usuarios(id),
    FOREIGN KEY (tipo_documentacion_id) REFERENCES tipo_documentacion(id)
);

-- 17. Tipo_Cobro
CREATE TABLE tipo_cobro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    tarifa DECIMAL(10,2)
);

-- 18. Modalidad
CREATE TABLE modalidad (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    porcentaje DECIMAL(5,2) DEFAULT 0
);

-- 19. Capacitacion
CREATE TABLE capacitacion (
    proyecto_id INT PRIMARY KEY,
    tipo_cobro_id INT,
    duracion DECIMAL(10,2),
    horas_dia DECIMAL(5,2),
    modalidad_id INT,
    logistica DECIMAL(10,2),
    fecha_inicio DATE,
    fecha_finalizacion DATE,
    FOREIGN KEY (proyecto_id) REFERENCES proyecto(id),
    FOREIGN KEY (tipo_cobro_id) REFERENCES tipo_cobro(id),
    FOREIGN KEY (modalidad_id) REFERENCES modalidad(id)
);

-- 20. Seguimiento
CREATE TABLE seguimiento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    capacitacion_id INT NOT NULL,
    tipo VARCHAR(50),
    tipo_cobro_id INT,
    duracion_horas DECIMAL(10,2),
    FOREIGN KEY (capacitacion_id) REFERENCES capacitacion(proyecto_id),
    FOREIGN KEY (tipo_cobro_id) REFERENCES tipo_cobro(id)
);

-- 21. Asignacion_Capacitacion
CREATE TABLE asignacion_capacitacion (
    capacitacion_id INT NOT NULL,
    usuarios_id INT NOT NULL,
    rol VARCHAR(50),
    horas_trabajadas DECIMAL(10,2),
    PRIMARY KEY (capacitacion_id, usuarios_id),
    FOREIGN KEY (capacitacion_id) REFERENCES capacitacion(proyecto_id),
    FOREIGN KEY (usuarios_id) REFERENCES usuarios(id)
);

-- 22. Costos
CREATE TABLE costos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proyecto_id INT NOT NULL,
    almacenamiento_id INT,
    costo_proyecto DECIMAL(15,2),
    fecha_finalizacion DATE,
    FOREIGN KEY (proyecto_id) REFERENCES proyecto(id),
    FOREIGN KEY (almacenamiento_id) REFERENCES almacenamiento(id)
);

-- 23. Proyecto_log
CREATE TABLE proyecto_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proyecto_id INT NOT NULL,
    usuario_id INT NOT NULL,
    accion VARCHAR(255) NOT NULL,
    detalle TEXT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (proyecto_id) REFERENCES proyecto(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);