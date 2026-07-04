CREATE DATABASE IF NOT EXISTS ventas_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ventas_db;

DROP TABLE IF EXISTS Auditoria;
DROP TABLE IF EXISTS Kardex;
DROP TABLE IF EXISTS VentaDetalle;
DROP TABLE IF EXISTS VentaCabecera;
DROP TABLE IF EXISTS IngresoProducto;
DROP TABLE IF EXISTS Correlativo;
DROP TABLE IF EXISTS Producto;
DROP TABLE IF EXISTS Categoria;
DROP TABLE IF EXISTS Cliente;
DROP TABLE IF EXISTS TipoDocumento;
DROP TABLE IF EXISTS RolFuncionalidad;
DROP TABLE IF EXISTS Funcionalidad;
DROP TABLE IF EXISTS Usuario;
DROP TABLE IF EXISTS Rol;
DROP TABLE IF EXISTS TipoOperacion;

CREATE TABLE Rol (
    idRol INT AUTO_INCREMENT PRIMARY KEY,
    nombreRol VARCHAR(40) NOT NULL UNIQUE,
    estado BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Funcionalidad (
    idFuncionalidad INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    icono VARCHAR(60),
    padre INT,
    FOREIGN KEY (padre) REFERENCES Funcionalidad(idFuncionalidad)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE RolFuncionalidad (
    idRolFuncionalidad INT AUTO_INCREMENT PRIMARY KEY,
    idRol INT NOT NULL,
    idFuncionalidad INT NOT NULL,
    ver BOOLEAN NOT NULL DEFAULT FALSE,
    crear BOOLEAN NOT NULL DEFAULT FALSE,
    editar BOOLEAN NOT NULL DEFAULT FALSE,
    eliminar BOOLEAN NOT NULL DEFAULT FALSE,
    imprimir BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (idRol) REFERENCES Rol(idRol),
    FOREIGN KEY (idFuncionalidad) REFERENCES Funcionalidad(idFuncionalidad),
    UNIQUE KEY uk_rol_funcionalidad (idRol, idFuncionalidad)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Usuario (
    idUsuario INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    idRol INT NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fechaRegistro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuarioCreacion INT,
    fechaModificacion TIMESTAMP NULL,
    secretKey2fa VARCHAR(255),
    FOREIGN KEY (idRol) REFERENCES Rol(idRol),
    FOREIGN KEY (usuarioCreacion) REFERENCES Usuario(idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE TipoDocumento (
    codTipoDocumento INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(40) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Cliente (
    codCliente INT AUTO_INCREMENT PRIMARY KEY,
    codTipoDocumento INT NOT NULL,
    numeroDocumento VARCHAR(255) NOT NULL,
    razonSocial VARCHAR(150),
    nombreCliente VARCHAR(150),
    fechaNacimiento VARCHAR(255),
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (codTipoDocumento) REFERENCES TipoDocumento(codTipoDocumento),
    UNIQUE KEY uk_tipo_numero (codTipoDocumento, numeroDocumento(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Categoria (
    codCategoria INT AUTO_INCREMENT PRIMARY KEY,
    nombreCategoria VARCHAR(80) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Producto (
    codProducto INT AUTO_INCREMENT PRIMARY KEY,
    nombreProducto VARCHAR(120) NOT NULL,
    codCategoria INT NOT NULL,
    precioUnitario DECIMAL(10,2) NOT NULL DEFAULT 0,
    precioFraccion DECIMAL(10,2) NOT NULL DEFAULT 0,
    cantidadUnidad INT NOT NULL DEFAULT 0,
    cantidadFraccion INT NOT NULL DEFAULT 0,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    version INT NOT NULL DEFAULT 0,
    FOREIGN KEY (codCategoria) REFERENCES Categoria(codCategoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE TipoOperacion (
    codTipoOperacion INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Correlativo (
    codCorrelativo INT AUTO_INCREMENT PRIMARY KEY,
    tipoComprobante VARCHAR(20) NOT NULL,
    serie VARCHAR(10) NOT NULL,
    numeroActual INT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_tipo_serie (tipoComprobante, serie)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE VentaCabecera (
    codVenta INT AUTO_INCREMENT PRIMARY KEY,
    fechaHora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    codTipoDocumento INT NOT NULL,
    codCliente INT NOT NULL,
    serie VARCHAR(10) NOT NULL,
    numeroCorrelativo INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0,
    igv DECIMAL(10,2) NOT NULL DEFAULT 0,
    total DECIMAL(10,2) NOT NULL DEFAULT 0,
    tipoComprobante VARCHAR(10) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fechaRegistro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuarioRegistro INT,
    version INT NOT NULL DEFAULT 0,
    FOREIGN KEY (codTipoDocumento) REFERENCES TipoDocumento(codTipoDocumento),
    FOREIGN KEY (codCliente) REFERENCES Cliente(codCliente),
    FOREIGN KEY (usuarioRegistro) REFERENCES Usuario(idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE VentaDetalle (
    codDetalle INT AUTO_INCREMENT PRIMARY KEY,
    codVenta INT NOT NULL,
    codProducto INT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precioUnitario DECIMAL(10,2) NOT NULL DEFAULT 0,
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0,
    tipoVenta VARCHAR(10) NOT NULL DEFAULT 'UNIDAD',
    FOREIGN KEY (codVenta) REFERENCES VentaCabecera(codVenta),
    FOREIGN KEY (codProducto) REFERENCES Producto(codProducto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IngresoProducto (
    codIngreso INT AUTO_INCREMENT PRIMARY KEY,
    fechaHora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    codProducto INT NOT NULL,
    cantidadUnidad INT NOT NULL DEFAULT 0,
    cantidadFraccion INT NOT NULL DEFAULT 0,
    observacion VARCHAR(200),
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fechaRegistro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuarioRegistro INT,
    version INT NOT NULL DEFAULT 0,
    FOREIGN KEY (codProducto) REFERENCES Producto(codProducto),
    FOREIGN KEY (usuarioRegistro) REFERENCES Usuario(idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Kardex (
    codKardex INT AUTO_INCREMENT PRIMARY KEY,
    codProducto INT NOT NULL,
    codTipoOperacion INT NOT NULL,
    cantidadInicial INT NOT NULL DEFAULT 0,
    cantidadMovimiento INT NOT NULL DEFAULT 0,
    cantidadFinal INT NOT NULL DEFAULT 0,
    saldoUnitario INT NOT NULL DEFAULT 0,
    saldoFraccionario INT NOT NULL DEFAULT 0,
    fechaHora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    codDocumento VARCHAR(20),
    observacion VARCHAR(200),
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fechaRegistro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuarioRegistro INT,
    FOREIGN KEY (codProducto) REFERENCES Producto(codProducto),
    FOREIGN KEY (codTipoOperacion) REFERENCES TipoOperacion(codTipoOperacion),
    FOREIGN KEY (usuarioRegistro) REFERENCES Usuario(idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Auditoria (
    codAuditoria INT AUTO_INCREMENT PRIMARY KEY,
    codUsuario INT NOT NULL,
    modulo VARCHAR(50) NOT NULL,
    tablaAfectada VARCHAR(50) NOT NULL,
    operacion VARCHAR(20) NOT NULL,
    codigoRegistro INT,
    valorAnterior TEXT,
    valorNuevo TEXT,
    fechaHora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ipOrigen VARCHAR(45),
    equipo VARCHAR(100),
    navegador VARCHAR(150),
    FOREIGN KEY (codUsuario) REFERENCES Usuario(idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO TipoOperacion (codTipoOperacion, descripcion) VALUES
(1, 'Ingreso'),
(2, 'Venta'),
(3, 'Extorno'),
(4, 'Ajuste');

INSERT INTO Rol (idRol, nombreRol, estado) VALUES
(1, 'Superusuario', TRUE),
(2, 'Vendedor', TRUE),
(3, 'Almacen', TRUE),
(4, 'Contabilidad', TRUE);
