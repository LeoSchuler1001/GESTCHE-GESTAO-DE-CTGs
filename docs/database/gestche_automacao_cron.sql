-- ============================================================
-- GesTchê - AGENDAMENTO DA GERAÇÃO AUTOMÁTICA DE MENSALIDADES
-- PostgreSQL / Supabase
--
-- Executar depois de restaurar a estrutura public e confirmar
-- que a função public.gerar_mensalidades(date) existe.
--
-- Agenda: dia 1 de cada mês, às 08:00 no horário de São Paulo.
-- O cron usa 11:00 UTC (GMT), conforme configuração validada.
-- ============================================================

-- A extensão pg_cron precisa estar disponível no projeto Supabase.
CREATE EXTENSION IF NOT EXISTS pg_cron;

-- Evita criar agendamentos duplicados com o mesmo nome.
SELECT cron.unschedule(jobid)
FROM cron.job
WHERE jobname = 'gerar-mensalidades-gestche';

-- Cria novamente o agendamento.
SELECT cron.schedule(
    'gerar-mensalidades-gestche',
    '0 11 1 * *',
    $$
    SELECT *
    FROM public.gerar_mensalidades(
        DATE_TRUNC(
            'month',
            NOW() AT TIME ZONE 'America/Sao_Paulo'
        )::DATE
    );
    $$
);
