CREATE DATABASE SINUELO;
USE SINUELO;
CREATE TABLE USUARIO(
	ID_USUARIO INT NOT NULL AUTO_INCREMENT,
	LOGIN VARCHAR(200) NOT NULL,
	SENHA VARCHAR(200) NOT NULL,
    ADM TINYINT(1) NOT NULL DEFAULT FALSE,
    ATIVO TINYINT(1) NOT NULL DEFAULT FALSE,
    PRIMARY KEY (ID_USUARIO)
);

CREATE TABLE ANIMAL (
	ID_ANIMAL INT NOT NULL AUTO_INCREMENT,
	COD_BRINCO VARCHAR(20),
	NOME VARCHAR(200),
    DATA_NASCIMENTO DATE,
    PESO_ORIGINAL DECIMAL(15,2),
    PESO_ATUAL DECIMAL(15,2),
    OBSERVACOES VARCHAR(500),
    DATA_CADASTRO DATE,
	PRIMARY KEY (ID_ANIMAL)
);

CREATE TABLE TIPO_ALERTA(
	ID_TIPO_ALERTA INT NOT NULL AUTO_INCREMENT,
	DESCRICAO VARCHAR(200),	
    PRIMARY KEY(ID_TIPO_ALERTA)
);  

CREATE TABLE ALERTA(
	ID_ALERTA INT NOT NULL AUTO_INCREMENT,
    ID_ANIMAL INT NOT NULL,  
    ID_TIPO_ALERTA INT NOT NULL,
    ID_USUARIO INT NOT NULL,
    DATA_ALERTA DATE, 
    HORA_ALERTA TIME,
    FOREIGN KEY (ID_ANIMAL) REFERENCES ANIMAL(ID_ANIMAL) ON UPDATE RESTRICT ON DELETE RESTRICT,
	FOREIGN KEY (ID_TIPO_ALERTA) REFERENCES TIPO_ALERTA(ID_TIPO_ALERTA) ON UPDATE RESTRICT ON DELETE RESTRICT,
    FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO(ID_USUARIO) ON UPDATE RESTRICT ON DELETE RESTRICT,
    PRIMARY KEY(ID_ALERTA)
); 
 
INSERT INTO USUARIO (LOGIN, SENHA, ADM)
VALUES('user', '$2a$10$3uWpzvxOyiu4H1qizqhOYuL5Vye44rDJ4QF41nJyDHOxSoFpdfb.q', TRUE);