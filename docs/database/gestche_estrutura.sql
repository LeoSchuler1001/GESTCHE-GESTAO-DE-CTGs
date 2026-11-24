--
-- PostgreSQL database dump
--

\restrict WrA2rZusQMhi6upyEdY5nzMh6LRiXQsYfBmZdfcSPUvZKdChIC4T4okK7LJ5U5U

-- Dumped from database version 17.6
-- Dumped by pg_dump version 18.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA public;


--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- Name: gerar_mensalidades(date); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.gerar_mensalidades(p_competencia date) RETURNS TABLE(socio_id integer, nome_socio text, valor_gerado numeric, situacao text)
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_competencia DATE;
    v_vencimento DATE;
    v_socio RECORD;
    v_linhas INTEGER;

    v_quantidade_gerada INTEGER := 0;
    v_quantidade_existente INTEGER := 0;
    v_situacao TEXT;
    v_valor NUMERIC;
    v_descricao TEXT;
    v_nome_mes TEXT;
BEGIN

    -- Normalizar competência para o primeiro dia do mês
    v_competencia :=
        DATE_TRUNC('month', p_competencia)::DATE;

    -- Vencimento no dia 10
    v_vencimento :=
        (v_competencia + INTERVAL '9 days')::DATE;

    -- Nome do mês em português
    v_nome_mes := CASE EXTRACT(MONTH FROM v_competencia)
        WHEN 1 THEN 'Janeiro'
        WHEN 2 THEN 'Fevereiro'
        WHEN 3 THEN 'Março'
        WHEN 4 THEN 'Abril'
        WHEN 5 THEN 'Maio'
        WHEN 6 THEN 'Junho'
        WHEN 7 THEN 'Julho'
        WHEN 8 THEN 'Agosto'
        WHEN 9 THEN 'Setembro'
        WHEN 10 THEN 'Outubro'
        WHEN 11 THEN 'Novembro'
        WHEN 12 THEN 'Dezembro'
    END;

    v_descricao :=
        'Mensalidade - ' ||
        v_nome_mes || '/' ||
        EXTRACT(YEAR FROM v_competencia)::INTEGER;

    -- Percorrer sócios elegíveis
    FOR v_socio IN
        SELECT
            s.pk_idsocio,
            s.nomesocio,
            s.valormensalidade
        FROM public.socio s
        WHERE s.ativosocio = TRUE
          AND s.pagamensalidade = TRUE
          AND s.valormensalidade > 0
        ORDER BY s.nomesocio
    LOOP

        -- Tentar inserir a mensalidade
        INSERT INTO public.debito (
            tipodebito,
            valordebito,
            vencimentodebito,
            fk_idsocio,
            competenciamensalidade
        )
        VALUES (
            v_descricao,
            v_socio.valormensalidade,
            v_vencimento,
            v_socio.pk_idsocio,
            v_competencia
        )
        ON CONFLICT (
            fk_idsocio,
            competenciamensalidade
        )
        WHERE competenciamensalidade IS NOT NULL
        DO NOTHING;

        GET DIAGNOSTICS v_linhas = ROW_COUNT;

        IF v_linhas = 1 THEN
            v_situacao := 'GERADO';
            v_valor := v_socio.valormensalidade;
            v_quantidade_gerada :=
                v_quantidade_gerada + 1;
        ELSE
            v_situacao := 'JÁ EXISTIA';
            v_valor := 0;
            v_quantidade_existente :=
                v_quantidade_existente + 1;
        END IF;

        socio_id := v_socio.pk_idsocio;
        nome_socio := v_socio.nomesocio;
        valor_gerado := v_valor;
        situacao := v_situacao;

        RETURN NEXT;

    END LOOP;

    -- Registrar execução concluída
    INSERT INTO public.historico_automacao (
        tipo_automacao,
        competencia,
        quantidade_gerada,
        quantidade_ja_existente,
        status,
        mensagem
    )
    VALUES (
        'Mensalidades',
        v_competencia,
        v_quantidade_gerada,
        v_quantidade_existente,
        'CONCLUIDO',
        'Processamento de mensalidades finalizado.'
    );

END;
$$;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: categoria; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.categoria (
    pk_idcategoria integer NOT NULL,
    nomecategoria character varying(100) NOT NULL,
    tipocategoria character varying(10) NOT NULL
);


--
-- Name: categoria_pk_idcategoria_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.categoria_pk_idcategoria_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: categoria_pk_idcategoria_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.categoria_pk_idcategoria_seq OWNED BY public.categoria.pk_idcategoria;


--
-- Name: conta; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.conta (
    pk_idconta integer NOT NULL,
    nomeconta character varying(100) NOT NULL,
    saldo numeric(10,2) DEFAULT 0.00 NOT NULL
);


--
-- Name: conta_pk_idconta_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.conta_pk_idconta_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: conta_pk_idconta_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.conta_pk_idconta_seq OWNED BY public.conta.pk_idconta;


--
-- Name: debito; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.debito (
    pk_iddebito integer NOT NULL,
    tipodebito character varying(100) NOT NULL,
    valordebito numeric(10,2) NOT NULL,
    vencimentodebito date NOT NULL,
    dtpgmtdebito date,
    fk_idsocio integer,
    competenciamensalidade date,
    CONSTRAINT valor_debito_ck CHECK ((valordebito > (0)::numeric))
);


--
-- Name: debito_pk_iddebito_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.debito_pk_iddebito_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: debito_pk_iddebito_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.debito_pk_iddebito_seq OWNED BY public.debito.pk_iddebito;


--
-- Name: departamento; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.departamento (
    pk_iddepartamento integer NOT NULL,
    nomedepartamento character varying(100) NOT NULL,
    descricaodepartamento text
);


--
-- Name: departamento_pk_iddepartamento_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.departamento_pk_iddepartamento_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: departamento_pk_iddepartamento_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.departamento_pk_iddepartamento_seq OWNED BY public.departamento.pk_iddepartamento;


--
-- Name: dependente; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dependente (
    pk_iddependente integer NOT NULL,
    nomedependente character varying(100) NOT NULL,
    cpfdependente character(11) NOT NULL,
    datanascdependente date NOT NULL,
    fk_idsocio integer NOT NULL,
    sexodependente character varying(9) NOT NULL,
    cordependente character varying(8) NOT NULL
);


--
-- Name: dependente_pk_iddependente_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.dependente_pk_iddependente_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dependente_pk_iddependente_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.dependente_pk_iddependente_seq OWNED BY public.dependente.pk_iddependente;


--
-- Name: endereco; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.endereco (
    pk_idendereco integer NOT NULL,
    rua character varying(100),
    numero integer,
    cep character(8),
    bairro character varying(100),
    cidade character varying(100),
    estado character(2),
    CONSTRAINT cep_ck CHECK ((cep ~ '^[0-9]{8}$'::text)),
    CONSTRAINT estado_ck CHECK ((estado = ANY (ARRAY['AC'::bpchar, 'AL'::bpchar, 'AP'::bpchar, 'AM'::bpchar, 'BA'::bpchar, 'CE'::bpchar, 'DF'::bpchar, 'ES'::bpchar, 'GO'::bpchar, 'MA'::bpchar, 'MT'::bpchar, 'MS'::bpchar, 'MG'::bpchar, 'PA'::bpchar, 'PB'::bpchar, 'PR'::bpchar, 'PE'::bpchar, 'PI'::bpchar, 'RJ'::bpchar, 'RN'::bpchar, 'RS'::bpchar, 'RO'::bpchar, 'RR'::bpchar, 'SC'::bpchar, 'SP'::bpchar, 'SE'::bpchar, 'TO'::bpchar, 'US'::bpchar, 'UK'::bpchar, 'AU'::bpchar, 'UY'::bpchar, 'AR'::bpchar, 'DK'::bpchar])))
);


--
-- Name: endereco_pk_idendereco_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.endereco_pk_idendereco_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: endereco_pk_idendereco_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.endereco_pk_idendereco_seq OWNED BY public.endereco.pk_idendereco;


--
-- Name: historico_automacao; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.historico_automacao (
    id bigint NOT NULL,
    tipo_automacao character varying(100) NOT NULL,
    competencia date NOT NULL,
    data_execucao timestamp with time zone DEFAULT now() NOT NULL,
    quantidade_gerada integer DEFAULT 0 NOT NULL,
    quantidade_ja_existente integer DEFAULT 0 NOT NULL,
    status character varying(30) NOT NULL,
    mensagem text
);


--
-- Name: historico_automacao_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.historico_automacao ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.historico_automacao_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: lembrete; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lembrete (
    pk_idlembrete integer NOT NULL,
    nomelembrete character varying(100) NOT NULL,
    datainiciolembrete date NOT NULL,
    periodicidadelembrete character varying(100) NOT NULL,
    descricaolembrete text,
    horariolembrete time without time zone NOT NULL,
    ativolembrete boolean DEFAULT true NOT NULL,
    fk_idusuario integer,
    valorlembrete numeric(10,2)
);


--
-- Name: lembrete_pk_idlembrete_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.lembrete_pk_idlembrete_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: lembrete_pk_idlembrete_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.lembrete_pk_idlembrete_seq OWNED BY public.lembrete.pk_idlembrete;


--
-- Name: logauditoria; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.logauditoria (
    pk_idlog integer NOT NULL,
    datahoralog timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    descricaolog text NOT NULL,
    fk_idusuario integer,
    nomeusuario character varying(100) NOT NULL
);


--
-- Name: logauditoria_pk_idlog_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.logauditoria_pk_idlog_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: logauditoria_pk_idlog_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.logauditoria_pk_idlog_seq OWNED BY public.logauditoria.pk_idlog;


--
-- Name: movimentacao; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.movimentacao (
    pk_idmovimentacao integer NOT NULL,
    valormovimentacao numeric(10,2) NOT NULL,
    datamovimentacao date DEFAULT CURRENT_DATE,
    comentariomovimentacao text,
    tipomovimentacao character varying(100) NOT NULL,
    fk_idusuario integer,
    fk_idcategoria integer,
    fk_idconta integer,
    fk_idlembrete integer
);


--
-- Name: movimentacao_pk_idmovimentacao_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.movimentacao_pk_idmovimentacao_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: movimentacao_pk_idmovimentacao_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.movimentacao_pk_idmovimentacao_seq OWNED BY public.movimentacao.pk_idmovimentacao;


--
-- Name: recebimento_pendente; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.recebimento_pendente (
    pk_idrecebimento integer NOT NULL,
    datarecebimento timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    dataconfirmacao timestamp without time zone,
    statusrecebimento character varying(20) DEFAULT 'PENDENTE'::character varying NOT NULL,
    fk_iddebito integer NOT NULL,
    fk_idsecretario integer NOT NULL,
    fk_idtesoureiro integer,
    fk_idconta integer,
    fk_idcategoria integer,
    fk_idmovimentacao integer,
    CONSTRAINT status_recebimento_ck CHECK (((statusrecebimento)::text = ANY ((ARRAY['PENDENTE'::character varying, 'CONFIRMADO'::character varying, 'REJEITADO'::character varying])::text[])))
);


--
-- Name: recebimento_pendente_pk_idrecebimento_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.recebimento_pendente_pk_idrecebimento_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: recebimento_pendente_pk_idrecebimento_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.recebimento_pendente_pk_idrecebimento_seq OWNED BY public.recebimento_pendente.pk_idrecebimento;


--
-- Name: socio; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.socio (
    pk_idsocio integer NOT NULL,
    cpfsocio character(11) NOT NULL,
    nomesocio character varying(100) NOT NULL,
    telefonesocio character varying(11),
    datanascsocio date NOT NULL,
    emailsocio character varying(100),
    ativosocio boolean DEFAULT true NOT NULL,
    fk_idendereco integer,
    fk_idusuario integer,
    sexosocio character varying(9) NOT NULL,
    corsocio character varying(8) NOT NULL,
    pagamensalidade boolean DEFAULT false NOT NULL,
    valormensalidade numeric(10,2)
);


--
-- Name: socio_departamento; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.socio_departamento (
    fk_idsocio integer NOT NULL,
    fk_iddepartamento integer NOT NULL
);


--
-- Name: socio_pk_idsocio_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.socio_pk_idsocio_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: socio_pk_idsocio_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.socio_pk_idsocio_seq OWNED BY public.socio.pk_idsocio;


--
-- Name: usuario; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.usuario (
    pk_idusuario integer NOT NULL,
    cpfusuario character(11) NOT NULL,
    nomeusuario character varying(100) NOT NULL,
    telefoneusuario character varying(11),
    cargousuario character varying(100) NOT NULL,
    senhahash character varying(64) NOT NULL,
    respostaseguranca character varying(100) NOT NULL,
    ativousuario boolean DEFAULT true NOT NULL,
    fk_idendereco integer,
    CONSTRAINT cpf_ck CHECK ((cpfusuario ~ '^[0-9]{11}$'::text)),
    CONSTRAINT fone_ck CHECK (((telefoneusuario IS NULL) OR ((telefoneusuario)::text ~ '^[0-9]{10,11}$'::text)))
);


--
-- Name: usuario_pk_idusuario_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.usuario_pk_idusuario_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: usuario_pk_idusuario_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.usuario_pk_idusuario_seq OWNED BY public.usuario.pk_idusuario;


--
-- Name: categoria pk_idcategoria; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categoria ALTER COLUMN pk_idcategoria SET DEFAULT nextval('public.categoria_pk_idcategoria_seq'::regclass);


--
-- Name: conta pk_idconta; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.conta ALTER COLUMN pk_idconta SET DEFAULT nextval('public.conta_pk_idconta_seq'::regclass);


--
-- Name: debito pk_iddebito; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.debito ALTER COLUMN pk_iddebito SET DEFAULT nextval('public.debito_pk_iddebito_seq'::regclass);


--
-- Name: departamento pk_iddepartamento; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.departamento ALTER COLUMN pk_iddepartamento SET DEFAULT nextval('public.departamento_pk_iddepartamento_seq'::regclass);


--
-- Name: dependente pk_iddependente; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dependente ALTER COLUMN pk_iddependente SET DEFAULT nextval('public.dependente_pk_iddependente_seq'::regclass);


--
-- Name: endereco pk_idendereco; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.endereco ALTER COLUMN pk_idendereco SET DEFAULT nextval('public.endereco_pk_idendereco_seq'::regclass);


--
-- Name: lembrete pk_idlembrete; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lembrete ALTER COLUMN pk_idlembrete SET DEFAULT nextval('public.lembrete_pk_idlembrete_seq'::regclass);


--
-- Name: logauditoria pk_idlog; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.logauditoria ALTER COLUMN pk_idlog SET DEFAULT nextval('public.logauditoria_pk_idlog_seq'::regclass);


--
-- Name: movimentacao pk_idmovimentacao; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimentacao ALTER COLUMN pk_idmovimentacao SET DEFAULT nextval('public.movimentacao_pk_idmovimentacao_seq'::regclass);


--
-- Name: recebimento_pendente pk_idrecebimento; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.recebimento_pendente ALTER COLUMN pk_idrecebimento SET DEFAULT nextval('public.recebimento_pendente_pk_idrecebimento_seq'::regclass);


--
-- Name: socio pk_idsocio; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.socio ALTER COLUMN pk_idsocio SET DEFAULT nextval('public.socio_pk_idsocio_seq'::regclass);


--
-- Name: usuario pk_idusuario; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario ALTER COLUMN pk_idusuario SET DEFAULT nextval('public.usuario_pk_idusuario_seq'::regclass);


--
-- Name: categoria categoria_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categoria
    ADD CONSTRAINT categoria_pkey PRIMARY KEY (pk_idcategoria);


--
-- Name: conta conta_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.conta
    ADD CONSTRAINT conta_pkey PRIMARY KEY (pk_idconta);


--
-- Name: debito debito_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.debito
    ADD CONSTRAINT debito_pkey PRIMARY KEY (pk_iddebito);


--
-- Name: departamento departamento_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.departamento
    ADD CONSTRAINT departamento_pkey PRIMARY KEY (pk_iddepartamento);


--
-- Name: dependente dependente_cpfdependente_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dependente
    ADD CONSTRAINT dependente_cpfdependente_key UNIQUE (cpfdependente);


--
-- Name: dependente dependente_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dependente
    ADD CONSTRAINT dependente_pkey PRIMARY KEY (pk_iddependente);


--
-- Name: endereco endereco_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.endereco
    ADD CONSTRAINT endereco_pkey PRIMARY KEY (pk_idendereco);


--
-- Name: historico_automacao historico_automacao_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.historico_automacao
    ADD CONSTRAINT historico_automacao_pkey PRIMARY KEY (id);


--
-- Name: lembrete lembrete_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lembrete
    ADD CONSTRAINT lembrete_pkey PRIMARY KEY (pk_idlembrete);


--
-- Name: logauditoria logauditoria_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.logauditoria
    ADD CONSTRAINT logauditoria_pkey PRIMARY KEY (pk_idlog);


--
-- Name: movimentacao movimentacao_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimentacao
    ADD CONSTRAINT movimentacao_pkey PRIMARY KEY (pk_idmovimentacao);


--
-- Name: recebimento_pendente recebimento_pendente_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.recebimento_pendente
    ADD CONSTRAINT recebimento_pendente_pkey PRIMARY KEY (pk_idrecebimento);


--
-- Name: socio socio_cpfsocio_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.socio
    ADD CONSTRAINT socio_cpfsocio_key UNIQUE (cpfsocio);


--
-- Name: socio_departamento socio_departamento_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.socio_departamento
    ADD CONSTRAINT socio_departamento_pkey PRIMARY KEY (fk_idsocio, fk_iddepartamento);


--
-- Name: socio socio_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.socio
    ADD CONSTRAINT socio_pkey PRIMARY KEY (pk_idsocio);


--
-- Name: usuario usuario_cpfusuario_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_cpfusuario_key UNIQUE (cpfusuario);


--
-- Name: usuario usuario_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_pkey PRIMARY KEY (pk_idusuario);


--
-- Name: idx_debito_mensalidade_unica; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_debito_mensalidade_unica ON public.debito USING btree (fk_idsocio, competenciamensalidade) WHERE (competenciamensalidade IS NOT NULL);


--
-- Name: idx_debito_socio_venc; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_debito_socio_venc ON public.debito USING btree (fk_idsocio, dtpgmtdebito, vencimentodebito);


--
-- Name: idx_socio_ativo; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_socio_ativo ON public.socio USING btree (ativosocio);


--
-- Name: debito fk_debito_socio; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.debito
    ADD CONSTRAINT fk_debito_socio FOREIGN KEY (fk_idsocio) REFERENCES public.socio(pk_idsocio) ON DELETE SET NULL;


--
-- Name: socio_departamento fk_departamento; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.socio_departamento
    ADD CONSTRAINT fk_departamento FOREIGN KEY (fk_iddepartamento) REFERENCES public.departamento(pk_iddepartamento) ON DELETE CASCADE;


--
-- Name: dependente fk_dependente_socio; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dependente
    ADD CONSTRAINT fk_dependente_socio FOREIGN KEY (fk_idsocio) REFERENCES public.socio(pk_idsocio) ON DELETE CASCADE;


--
-- Name: lembrete fk_lembrete_usuario; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lembrete
    ADD CONSTRAINT fk_lembrete_usuario FOREIGN KEY (fk_idusuario) REFERENCES public.usuario(pk_idusuario) ON DELETE SET NULL;


--
-- Name: logauditoria fk_log_usuario; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.logauditoria
    ADD CONSTRAINT fk_log_usuario FOREIGN KEY (fk_idusuario) REFERENCES public.usuario(pk_idusuario) ON DELETE SET NULL;


--
-- Name: movimentacao fk_movimentacao_categoria; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimentacao
    ADD CONSTRAINT fk_movimentacao_categoria FOREIGN KEY (fk_idcategoria) REFERENCES public.categoria(pk_idcategoria) ON DELETE SET NULL;


--
-- Name: movimentacao fk_movimentacao_conta; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimentacao
    ADD CONSTRAINT fk_movimentacao_conta FOREIGN KEY (fk_idconta) REFERENCES public.conta(pk_idconta) ON DELETE SET NULL;


--
-- Name: movimentacao fk_movimentacao_lembrete; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimentacao
    ADD CONSTRAINT fk_movimentacao_lembrete FOREIGN KEY (fk_idlembrete) REFERENCES public.lembrete(pk_idlembrete) ON DELETE SET NULL;


--
-- Name: movimentacao fk_movimentacao_usuario; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.movimentacao
    ADD CONSTRAINT fk_movimentacao_usuario FOREIGN KEY (fk_idusuario) REFERENCES public.usuario(pk_idusuario) ON DELETE SET NULL;


--
-- Name: recebimento_pendente fk_rec_categoria; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.recebimento_pendente
    ADD CONSTRAINT fk_rec_categoria FOREIGN KEY (fk_idcategoria) REFERENCES public.categoria(pk_idcategoria) ON DELETE SET NULL;


--
-- Name: recebimento_pendente fk_rec_conta; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.recebimento_pendente
    ADD CONSTRAINT fk_rec_conta FOREIGN KEY (fk_idconta) REFERENCES public.conta(pk_idconta) ON DELETE SET NULL;


--
-- Name: recebimento_pendente fk_rec_debito; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.recebimento_pendente
    ADD CONSTRAINT fk_rec_debito FOREIGN KEY (fk_iddebito) REFERENCES public.debito(pk_iddebito) ON DELETE CASCADE;


--
-- Name: recebimento_pendente fk_rec_movimentacao; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.recebimento_pendente
    ADD CONSTRAINT fk_rec_movimentacao FOREIGN KEY (fk_idmovimentacao) REFERENCES public.movimentacao(pk_idmovimentacao) ON DELETE SET NULL;


--
-- Name: recebimento_pendente fk_rec_secretario; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.recebimento_pendente
    ADD CONSTRAINT fk_rec_secretario FOREIGN KEY (fk_idsecretario) REFERENCES public.usuario(pk_idusuario) ON DELETE RESTRICT;


--
-- Name: recebimento_pendente fk_rec_tesoureiro; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.recebimento_pendente
    ADD CONSTRAINT fk_rec_tesoureiro FOREIGN KEY (fk_idtesoureiro) REFERENCES public.usuario(pk_idusuario) ON DELETE SET NULL;


--
-- Name: socio_departamento fk_socio; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.socio_departamento
    ADD CONSTRAINT fk_socio FOREIGN KEY (fk_idsocio) REFERENCES public.socio(pk_idsocio) ON DELETE CASCADE;


--
-- Name: socio fk_socio_endereco; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.socio
    ADD CONSTRAINT fk_socio_endereco FOREIGN KEY (fk_idendereco) REFERENCES public.endereco(pk_idendereco) ON DELETE SET NULL;


--
-- Name: socio fk_socio_usuario; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.socio
    ADD CONSTRAINT fk_socio_usuario FOREIGN KEY (fk_idusuario) REFERENCES public.usuario(pk_idusuario) ON DELETE SET NULL;


--
-- Name: usuario fk_usuario_endereco; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT fk_usuario_endereco FOREIGN KEY (fk_idendereco) REFERENCES public.endereco(pk_idendereco) ON DELETE SET NULL;


--
-- Name: historico_automacao; Type: ROW SECURITY; Schema: public; Owner: -
--

ALTER TABLE public.historico_automacao ENABLE ROW LEVEL SECURITY;

--
-- Name: recebimento_pendente; Type: ROW SECURITY; Schema: public; Owner: -
--

ALTER TABLE public.recebimento_pendente ENABLE ROW LEVEL SECURITY;

--
-- PostgreSQL database dump complete
--

\unrestrict WrA2rZusQMhi6upyEdY5nzMh6LRiXQsYfBmZdfcSPUvZKdChIC4T4okK7LJ5U5U

