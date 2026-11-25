-- Script de criação do banco de dados alinhado com os DAOs do projeto

CREATE TABLE IF NOT EXISTS paciente (
    cpf VARCHAR(14) PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    data_nascimento DATE NOT NULL,
    endereco VARCHAR(255),
    telefone VARCHAR(30),
    alergia VARCHAR(150)
);

CREATE TABLE IF NOT EXISTS contato_emergencia (
    paciente_cpf VARCHAR(14) PRIMARY KEY REFERENCES paciente(cpf) ON DELETE CASCADE,
    nome VARCHAR(150) NOT NULL,
    telefone VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS medicamento (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    dosagem VARCHAR(80) NOT NULL,
    posologia VARCHAR(150),
    laboratorio VARCHAR(120),
    paciente_cpf VARCHAR(14) NOT NULL REFERENCES paciente(cpf) ON DELETE CASCADE,
    CONSTRAINT uq_medicamento_paciente UNIQUE (paciente_cpf, nome, dosagem)
);

CREATE TABLE IF NOT EXISTS servico (
    id SERIAL PRIMARY KEY,
    data DATE NOT NULL,
    anotacao TEXT,
    resultado TEXT,
    tipo VARCHAR(60) NOT NULL,
    paciente_cpf VARCHAR(14) NOT NULL REFERENCES paciente(cpf) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS acompanhamento (
    id SERIAL PRIMARY KEY,
    data DATE NOT NULL,
    notas_clinicas TEXT,
    resultado TEXT,
    paciente_cpf VARCHAR(14) NOT NULL REFERENCES paciente(cpf) ON DELETE CASCADE,
    medicamento_id INTEGER REFERENCES medicamento(id) ON DELETE SET NULL
);
