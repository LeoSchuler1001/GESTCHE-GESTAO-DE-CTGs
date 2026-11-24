-- =====================================================
-- GESTCHÊ - ESTRUTURA DAS TABELAS DO BANCO DE DADOS
-- Documento para consulta
-- =====================================================


-- =====================================================
-- 1. ENDERECO
-- =====================================================

CREATE TABLE endereco (
    pk_idEndereco SERIAL PRIMARY KEY,
    rua VARCHAR(100),
    numero INT,
    cep CHAR(8),
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    estado CHAR(2),

    CONSTRAINT cep_ck CHECK (
        cep ~ '^[0-9]{8}$'
    ),

    CONSTRAINT estado_ck CHECK (
        estado IN (
            'AC','AL','AP','AM','BA','CE','DF','ES','GO','MA',
            'MT','MS','MG','PA','PB','PR','PE','PI','RJ','RN',
            'RS','RO','RR','SC','SP','SE','TO',
            'US','UK','AU','UY','AR','DK'
        )
    )
);


-- =====================================================
-- 2. USUARIO
-- =====================================================

CREATE TABLE usuario (
    pk_idUsuario SERIAL PRIMARY KEY,
    cpfUsuario CHAR(11) NOT NULL UNIQUE,
    nomeUsuario VARCHAR(100) NOT NULL,
    telefoneUsuario VARCHAR(11),
    cargoUsuario VARCHAR(100) NOT NULL,
    senhaHash VARCHAR(64) NOT NULL,
    respostaSeguranca VARCHAR(100) NOT NULL,
    ativoUsuario BOOLEAN NOT NULL DEFAULT TRUE,
    fk_idEndereco INT,

    CONSTRAINT cpf_ck CHECK (
        cpfUsuario ~ '^[0-9]{11}$'
    ),

    CONSTRAINT fone_ck CHECK (
        telefoneUsuario IS NULL
        OR telefoneUsuario ~ '^[0-9]{10,11}$'
    ),

    CONSTRAINT fk_usuario_endereco
        FOREIGN KEY (fk_idEndereco)
        REFERENCES endereco(pk_idEndereco)
        ON DELETE SET NULL
);


-- =====================================================
-- 3. LOGAUDITORIA
-- =====================================================

CREATE TABLE logAuditoria (
    pk_idLog SERIAL PRIMARY KEY,
    dataHoraLog TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    descricaoLog TEXT NOT NULL,
    fk_idUsuario INT,
    nomeUsuario VARCHAR(100) NOT NULL,

    CONSTRAINT fk_log_usuario
        FOREIGN KEY (fk_idUsuario)
        REFERENCES usuario(pk_idUsuario)
        ON DELETE SET NULL
);


-- =====================================================
-- 4. SOCIO
-- =====================================================

CREATE TABLE socio (
    pk_idSocio SERIAL PRIMARY KEY,
    cpfSocio CHAR(11) NOT NULL UNIQUE,
    nomeSocio VARCHAR(100) NOT NULL,
    telefoneSocio VARCHAR(11),
    dataNascSocio DATE NOT NULL,
    emailSocio VARCHAR(100),
    ativoSocio BOOLEAN NOT NULL DEFAULT TRUE,
    fk_idEndereco INT,
    fk_idUsuario INT,
    sexoSocio VARCHAR(9) NOT NULL,
    corSocio VARCHAR(8) NOT NULL,
    pagaMensalidade BOOLEAN NOT NULL DEFAULT FALSE,
    valorMensalidade NUMERIC(10,2),

    CONSTRAINT fk_socio_endereco
        FOREIGN KEY (fk_idEndereco)
        REFERENCES endereco(pk_idEndereco)
        ON DELETE SET NULL,

    CONSTRAINT fk_socio_usuario
        FOREIGN KEY (fk_idUsuario)
        REFERENCES usuario(pk_idUsuario)
        ON DELETE SET NULL
);


-- =====================================================
-- 5. DEPENDENTE
-- =====================================================

CREATE TABLE dependente (
    pk_idDependente SERIAL PRIMARY KEY,
    nomeDependente VARCHAR(100) NOT NULL,
    cpfDependente CHAR(11) NOT NULL UNIQUE,
    dataNascDependente DATE NOT NULL,
    fk_idSocio INT NOT NULL,
    sexoDependente VARCHAR(9) NOT NULL,
    corDependente VARCHAR(8) NOT NULL,

    CONSTRAINT fk_dependente_socio
        FOREIGN KEY (fk_idSocio)
        REFERENCES socio(pk_idSocio)
        ON DELETE CASCADE
);


-- =====================================================
-- 6. DEPARTAMENTO
-- =====================================================

CREATE TABLE departamento (
    pk_idDepartamento SERIAL PRIMARY KEY,
    nomeDepartamento VARCHAR(100) NOT NULL,
    descricaoDepartamento TEXT
);


-- =====================================================
-- 7. SOCIO_DEPARTAMENTO
-- =====================================================

CREATE TABLE socio_departamento (
    fk_idSocio INT NOT NULL,
    fk_idDepartamento INT NOT NULL,

    CONSTRAINT fk_socio
        FOREIGN KEY (fk_idSocio)
        REFERENCES socio(pk_idSocio)
        ON DELETE CASCADE,

    CONSTRAINT fk_departamento
        FOREIGN KEY (fk_idDepartamento)
        REFERENCES departamento(pk_idDepartamento)
        ON DELETE CASCADE,

    PRIMARY KEY (fk_idSocio, fk_idDepartamento)
);


-- =====================================================
-- 8. DEBITO
-- =====================================================

CREATE TABLE debito (
    pk_idDebito SERIAL PRIMARY KEY,
    tipoDebito VARCHAR(100) NOT NULL,
    valorDebito NUMERIC(10,2) NOT NULL,
    vencimentoDebito DATE NOT NULL,
    dtPgmtDebito DATE,
    fk_idSocio INT,
    competenciaMensalidade DATE,

    CONSTRAINT valor_debito_ck CHECK (
        valorDebito > 0
    ),

    CONSTRAINT fk_debito_socio
        FOREIGN KEY (fk_idSocio)
        REFERENCES socio(pk_idSocio)
        ON DELETE SET NULL
);


-- =====================================================
-- 9. CONTA
-- =====================================================

CREATE TABLE conta (
    pk_idConta SERIAL PRIMARY KEY,
    nomeConta VARCHAR(100) NOT NULL,
    saldo NUMERIC(10,2) NOT NULL DEFAULT 0.00
);


-- =====================================================
-- 10. CATEGORIA
-- =====================================================

CREATE TABLE categoria (
    pk_idCategoria SERIAL PRIMARY KEY,
    nomeCategoria VARCHAR(100) NOT NULL,
    tipoCategoria VARCHAR(10) NOT NULL
);


-- =====================================================
-- 11. LEMBRETE
-- =====================================================

CREATE TABLE lembrete (
    pk_idLembrete SERIAL PRIMARY KEY,
    nomeLembrete VARCHAR(100) NOT NULL,
    dataInicioLembrete DATE NOT NULL,
    periodicidadeLembrete VARCHAR(100) NOT NULL,
    descricaoLembrete TEXT,
    horarioLembrete TIME NOT NULL,
    ativoLembrete BOOLEAN NOT NULL DEFAULT TRUE,
    fk_idUsuario INT,
    valorLembrete NUMERIC(10,2),

    CONSTRAINT fk_lembrete_usuario
        FOREIGN KEY (fk_idUsuario)
        REFERENCES usuario(pk_idUsuario)
        ON DELETE SET NULL
);


-- =====================================================
-- 12. MOVIMENTACAO
-- =====================================================

CREATE TABLE movimentacao (
    pk_idMovimentacao SERIAL PRIMARY KEY,
    valorMovimentacao NUMERIC(10,2) NOT NULL,
    dataMovimentacao DATE DEFAULT CURRENT_DATE,
    comentarioMovimentacao TEXT,
    tipoMovimentacao VARCHAR(100) NOT NULL,
    fk_idUsuario INT,
    fk_idCategoria INT,
    fk_idConta INT,
    fk_idLembrete INT,

    CONSTRAINT fk_movimentacao_usuario
        FOREIGN KEY (fk_idUsuario)
        REFERENCES usuario(pk_idUsuario)
        ON DELETE SET NULL,

    CONSTRAINT fk_movimentacao_categoria
        FOREIGN KEY (fk_idCategoria)
        REFERENCES categoria(pk_idCategoria)
        ON DELETE SET NULL,

    CONSTRAINT fk_movimentacao_conta
        FOREIGN KEY (fk_idConta)
        REFERENCES conta(pk_idConta)
        ON DELETE SET NULL,

    CONSTRAINT fk_movimentacao_lembrete
        FOREIGN KEY (fk_idLembrete)
        REFERENCES lembrete(pk_idLembrete)
        ON DELETE SET NULL
);


-- =====================================================
-- 13. RECEBIMENTO_PENDENTE
-- =====================================================

CREATE TABLE recebimento_pendente (
    pk_idRecebimento SERIAL PRIMARY KEY,
    dataRecebimento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dataConfirmacao TIMESTAMP,
    statusRecebimento VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    fk_idDebito INT NOT NULL,
    fk_idSecretario INT NOT NULL,
    fk_idTesoureiro INT,
    fk_idConta INT,
    fk_idCategoria INT,
    fk_idMovimentacao INT,

    CONSTRAINT fk_rec_debito
        FOREIGN KEY (fk_idDebito)
        REFERENCES debito(pk_idDebito)
        ON DELETE CASCADE,

    CONSTRAINT fk_rec_secretario
        FOREIGN KEY (fk_idSecretario)
        REFERENCES usuario(pk_idUsuario)
        ON DELETE RESTRICT,

    CONSTRAINT fk_rec_tesoureiro
        FOREIGN KEY (fk_idTesoureiro)
        REFERENCES usuario(pk_idUsuario)
        ON DELETE SET NULL,

    CONSTRAINT fk_rec_conta
        FOREIGN KEY (fk_idConta)
        REFERENCES conta(pk_idConta)
        ON DELETE SET NULL,

    CONSTRAINT fk_rec_categoria
        FOREIGN KEY (fk_idCategoria)
        REFERENCES categoria(pk_idCategoria)
        ON DELETE SET NULL,

    CONSTRAINT fk_rec_movimentacao
        FOREIGN KEY (fk_idMovimentacao)
        REFERENCES movimentacao(pk_idMovimentacao)
        ON DELETE SET NULL,

    CONSTRAINT status_recebimento_ck CHECK (
        statusRecebimento IN (
            'PENDENTE',
            'CONFIRMADO',
            'REJEITADO'
        )
    )
);


-- =====================================================
-- 14. HISTORICO_AUTOMACAO
-- =====================================================

CREATE TABLE historico_automacao (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo_automacao VARCHAR(100) NOT NULL,
    competencia DATE NOT NULL,
    data_execucao TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    quantidade_gerada INT NOT NULL DEFAULT 0,
    quantidade_ja_existente INT NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL,
    mensagem TEXT
);