const moeda = valor =>
    Number(valor || 0).toLocaleString('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    });

const dataHora = valor => {
    if (!valor) return '--';

    return new Date(valor).toLocaleString('pt-BR');
};

const data = valor => {
    if (!valor) return '--';

    const partes = String(valor).substring(0, 10).split('-');

    return `${partes[2]}/${partes[1]}/${partes[0]}`;
};

async function buscar(url) {
    const resposta = await fetch(url);

    if (!resposta.ok) {
        throw new Error('Falha ao consultar o servidor.');
    }

    return resposta.json();
}

async function carregarConexao() {
    const indicador = document.getElementById('indicadorConexao');
    const texto = document.getElementById('textoConexao');

    try {
        const resultado = await buscar('/api/conexao');

        indicador.className = 'bolinha online';
        texto.textContent = resultado.mensagem;

    } catch (error) {
        indicador.className = 'bolinha offline';
        texto.textContent = 'Banco de dados desconectado';
    }
}

async function carregarResumo() {
    const dados = await buscar('/api/resumo');

    document.getElementById('totalMensalidades').textContent =
        dados.total_mensalidades;

    document.getElementById('valorTotal').textContent =
        moeda(dados.valor_total);

    document.getElementById('pendentes').textContent =
        dados.pendentes;
}

async function carregarCron() {
    const dados = await buscar('/api/cron');

    const job = dados.agendamento;
    const ultima = dados.ultimaExecucao;

    document.getElementById('statusCron').textContent =
        job ? (job.active ? 'ATIVA' : 'INATIVA') : 'NÃO ENCONTRADA';

    document.getElementById('nomeCron').textContent =
        job?.jobname || '--';

    document.getElementById('programacao').textContent =
        job?.schedule || '--';

    document.getElementById('ultimaExecucao').textContent =
        ultima ? dataHora(ultima.start_time) : 'Nenhuma execução';

    document.getElementById('resultadoCron').textContent =
        ultima?.status || 'Aguardando primeira execução';
}

async function carregarHistorico() {
    const dados = await buscar('/api/historico');
    const tabela = document.getElementById('tabelaHistorico');

    tabela.innerHTML = '';

    if (!dados.length) {
        tabela.innerHTML = `
            <tr>
                <td colspan="6">Nenhuma execução registrada.</td>
            </tr>
        `;
        return;
    }

    dados.forEach(item => {
        const classe = item.status === 'CONCLUIDO' ? 'ok' : 'erro';

        tabela.innerHTML += `
            <tr>
                <td>${dataHora(item.data_execucao)}</td>
                <td>${data(item.competencia)}</td>
                <td>${item.tipo_automacao}</td>
                <td>${item.quantidade_gerada}</td>
                <td>${item.quantidade_ja_existente}</td>
                <td>
                    <span class="status ${classe}">
                        ${item.status}
                    </span>
                </td>
            </tr>
        `;
    });
}

async function carregarMensalidades() {
    const dados = await buscar('/api/mensalidades');
    const tabela = document.getElementById('tabelaMensalidades');

    tabela.innerHTML = '';

    if (!dados.length) {
        tabela.innerHTML = `
            <tr>
                <td colspan="6">Nenhuma mensalidade gerada.</td>
            </tr>
        `;
        return;
    }

    dados.forEach(item => {
        const pagamento = item.dtpgmtdebito
            ? `Pago em ${data(item.dtpgmtdebito)}`
            : 'Pendente';

        tabela.innerHTML += `
            <tr>
                <td>${item.nomesocio || 'Sócio não encontrado'}</td>
                <td>${item.tipodebito}</td>
                <td>${data(item.competenciamensalidade)}</td>
                <td>${data(item.vencimentodebito)}</td>
                <td>${moeda(item.valordebito)}</td>
                <td>${pagamento}</td>
            </tr>
        `;
    });
}

async function carregarPainel() {
    const botao = document.getElementById('atualizar');

    botao.disabled = true;
    botao.textContent = 'Atualizando...';

    try {
        await carregarConexao();

        const conectado = document
            .getElementById('indicadorConexao')
            .classList.contains('online');

        if (!conectado) {
            throw new Error('Sem conexão com o banco.');
        }

        await Promise.all([
            carregarResumo(),
            carregarCron(),
            carregarHistorico(),
            carregarMensalidades()
        ]);

        document.getElementById('ultimaAtualizacao').textContent =
            'Atualizado em ' + new Date().toLocaleTimeString('pt-BR');

    } catch (error) {
        console.error(error);
        alert('Não foi possível atualizar o painel. Verifique a conexão.');
    } finally {
        botao.disabled = false;
        botao.textContent = 'Atualizar dados';
    }
}

carregarPainel();

// Atualização automática a cada 60 segundos.
setInterval(carregarPainel, 60000);