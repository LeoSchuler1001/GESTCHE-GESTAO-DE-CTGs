require('dotenv').config();

const express = require('express');
const path = require('path');
const pool = require('./db');

const app = express();
const PORT = Number(process.env.PORT || 3000);

// Acesso somente pelo próprio notebook.
app.use((req, res, next) => {
    res.setHeader('X-Content-Type-Options', 'nosniff');
    next();
});

app.use(express.static(path.join(__dirname, '..', 'public')));

// Verificar conexão com o banco.
app.get('/api/conexao', async (req, res) => {
    try {
        await pool.query('SELECT 1');

        res.json({
            conectado: true,
            mensagem: 'Conexão com Supabase funcionando.'
        });
    } catch (error) {
        console.error(error.message);

        res.status(500).json({
            conectado: false,
            mensagem: 'Não foi possível conectar ao banco de dados.'
        });
    }
});

// Consultar o agendamento do Cron.
app.get('/api/cron', async (req, res) => {
    try {
        const resultado = await pool.query(`
            SELECT
                jobid,
                jobname,
                schedule,
                active
            FROM cron.job
            WHERE jobname = $1
        `, ['gerar-mensalidades-gestche']);

        const agendamento = resultado.rows[0] || null;

        let ultimaExecucao = null;

        if (agendamento) {
            const execucoes = await pool.query(`
                SELECT
                    status,
                    return_message,
                    start_time,
                    end_time
                FROM cron.job_run_details
                WHERE jobid = $1
                ORDER BY start_time DESC
                LIMIT 1
            `, [agendamento.jobid]);

            ultimaExecucao = execucoes.rows[0] || null;
        }

        res.json({
            agendamento,
            ultimaExecucao
        });

    } catch (error) {
        console.error('Erro ao consultar Cron:', error.message);

        res.status(500).json({
            erro: 'Não foi possível consultar o agendamento.'
        });
    }
});

// Consultar histórico das automações.
app.get('/api/historico', async (req, res) => {
    try {
        const resultado = await pool.query(`
            SELECT
                id,
                tipo_automacao,
                competencia,
                data_execucao,
                quantidade_gerada,
                quantidade_ja_existente,
                status,
                mensagem
            FROM public.historico_automacao
            ORDER BY data_execucao DESC
            LIMIT 100
        `);

        res.json(resultado.rows);

    } catch (error) {
        console.error('Erro ao consultar histórico:', error.message);

        res.status(500).json({
            erro: 'Não foi possível consultar o histórico.'
        });
    }
});

// Consultar mensalidades geradas.
app.get('/api/mensalidades', async (req, res) => {
    try {
        const resultado = await pool.query(`
            SELECT
                d.pk_iddebito,
                d.tipodebito,
                d.valordebito,
                d.vencimentodebito,
                d.dtpgmtdebito,
                d.competenciamensalidade,
                d.fk_idsocio,
                s.nomesocio
            FROM public.debito d
            LEFT JOIN public.socio s
                ON s.pk_idsocio = d.fk_idsocio
            WHERE d.competenciamensalidade IS NOT NULL
            ORDER BY
                d.competenciamensalidade DESC,
                s.nomesocio ASC
            LIMIT 500
        `);

        res.json(resultado.rows);

    } catch (error) {
        console.error('Erro ao consultar mensalidades:', error.message);

        res.status(500).json({
            erro: 'Não foi possível consultar as mensalidades.'
        });
    }
});

// Indicadores gerais.
app.get('/api/resumo', async (req, res) => {
    try {
        const resultado = await pool.query(`
            SELECT
                COUNT(*)::INTEGER AS total_mensalidades,
                COALESCE(SUM(valordebito), 0) AS valor_total,
                COUNT(*) FILTER (
                    WHERE dtpgmtdebito IS NOT NULL
                )::INTEGER AS pagas,
                COUNT(*) FILTER (
                    WHERE dtpgmtdebito IS NULL
                )::INTEGER AS pendentes
            FROM public.debito
            WHERE competenciamensalidade IS NOT NULL
        `);

        res.json(resultado.rows[0]);

    } catch (error) {
        console.error('Erro ao consultar resumo:', error.message);

        res.status(500).json({
            erro: 'Não foi possível consultar os indicadores.'
        });
    }
});

// Iniciar servidor local.
app.listen(PORT, '127.0.0.1', () => {
    console.log(`GesTchê Painel disponível em http://127.0.0.1:${PORT}`);
});