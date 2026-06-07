/* ============================================================
   HardSoft — API Client
   Como o frontend e servido pelo proprio Spring Boot, usamos
   URL relativa: '/api' vai bater no host atual automaticamente.
   ============================================================ */

const API_BASE_URL = '/api';

/**
 * Funcao generica pra chamar a API.
 */
async function apiRequest(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const config = {
        method: options.method || 'GET',
        headers: { 'Content-Type': 'application/json', ...options.headers },
    };
    if (options.body) config.body = JSON.stringify(options.body);

    try {
        const response = await fetch(url, config);
        if (response.status === 204) return null;
        const data = await response.json().catch(() => null);

        if (!response.ok) {
            let msg = 'Erro desconhecido';
            if (data) {
                if (data.mensagem) msg = data.mensagem;
                else if (data.erros) {
                    msg = Object.entries(data.erros)
                        .map(([campo, erro]) => `${campo}: ${erro}`)
                        .join('; ');
                }
            }
            throw new Error(msg);
        }
        return data;
    } catch (err) {
        if (err.message === 'Failed to fetch') {
            throw new Error('Erro de conexao com o servidor.');
        }
        throw err;
    }
}

// ============================================================
// ADMIN
// ============================================================
const adminAPI = {
    listar:    ()           => apiRequest('/admins'),
    buscar:    (id)         => apiRequest(`/admins/${id}`),
    criar:     (dto)        => apiRequest('/admins', { method: 'POST', body: dto }),
    atualizar: (id, dto)    => apiRequest(`/admins/${id}`, { method: 'PUT', body: dto }),
    deletar:   (id)         => apiRequest(`/admins/${id}`, { method: 'DELETE' }),
};

// ============================================================
// CLIENTE
// ============================================================
const clienteAPI = {
    listar:    ()           => apiRequest('/clientes'),
    buscar:    (id)         => apiRequest(`/clientes/${id}`),
    criar:     (dto)        => apiRequest('/clientes', { method: 'POST', body: dto }),
    atualizar: (id, dto)    => apiRequest(`/clientes/${id}`, { method: 'PUT', body: dto }),
    deletar:   (id)         => apiRequest(`/clientes/${id}`, { method: 'DELETE' }),
};

// ============================================================
// SETUP
// ============================================================
const setupAPI = {
    listar:           ()           => apiRequest('/setups'),
    listarPorCliente: (id)         => apiRequest(`/setups/cliente/${id}`),
    buscar:           (id)         => apiRequest(`/setups/${id}`),
    criar:            (dto)        => apiRequest('/setups', { method: 'POST', body: dto }),
    atualizar:        (id, dto)    => apiRequest(`/setups/${id}`, { method: 'PUT', body: dto }),
    deletar:          (id)         => apiRequest(`/setups/${id}`, { method: 'DELETE' }),
};

// ============================================================
// PECA
// ============================================================
const pecaAPI = {
    listar:        (nome)         => apiRequest(nome ? `/pecas?nome=${encodeURIComponent(nome)}` : '/pecas'),
    estoqueBaixo:  (limite = 5)   => apiRequest(`/pecas/estoque-baixo?limite=${limite}`),
    buscar:        (id)           => apiRequest(`/pecas/${id}`),
    criar:         (dto)          => apiRequest('/pecas', { method: 'POST', body: dto }),
    atualizar:     (id, dto)      => apiRequest(`/pecas/${id}`, { method: 'PUT', body: dto }),
    deletar:       (id)           => apiRequest(`/pecas/${id}`, { method: 'DELETE' }),
};

// ============================================================
// TIPO SERVICO
// ============================================================
const tipoServicoAPI = {
    listar:    ()        => apiRequest('/tipos-servico'),
    buscar:    (id)      => apiRequest(`/tipos-servico/${id}`),
    criar:     (dto)     => apiRequest('/tipos-servico', { method: 'POST', body: dto }),
    atualizar: (id, dto) => apiRequest(`/tipos-servico/${id}`, { method: 'PUT', body: dto }),
    deletar:   (id)      => apiRequest(`/tipos-servico/${id}`, { method: 'DELETE' }),
};

// ============================================================
// ORDEM SERVICO
// ============================================================
const osAPI = {
    listar:           (status)     => apiRequest(status ? `/ordens-servico?status=${status}` : '/ordens-servico'),
    listarPorCliente: (id)         => apiRequest(`/ordens-servico/cliente/${id}`),
    buscar:           (id)         => apiRequest(`/ordens-servico/${id}`),
    criar:            (dto)        => apiRequest('/ordens-servico', { method: 'POST', body: dto }),
    atualizar:        (id, dto)    => apiRequest(`/ordens-servico/${id}`, { method: 'PUT', body: dto }),
    mudarStatus:      (id, status) => apiRequest(`/ordens-servico/${id}/status`, { method: 'PATCH', body: { status } }),
    deletar:          (id)         => apiRequest(`/ordens-servico/${id}`, { method: 'DELETE' }),
};

// ============================================================
// SERVICO (dentro da OS)
// ============================================================
const servicoAPI = {
    listarPorOS: (idOS)     => apiRequest(`/servicos/ordem-servico/${idOS}`),
    buscar:      (id)       => apiRequest(`/servicos/${id}`),
    criar:       (dto)      => apiRequest('/servicos', { method: 'POST', body: dto }),
    atualizar:   (id, dto)  => apiRequest(`/servicos/${id}`, { method: 'PUT', body: dto }),
    deletar:     (id)       => apiRequest(`/servicos/${id}`, { method: 'DELETE' }),
};

// ============================================================
// PECA SERVICO
// ============================================================
const pecaServicoAPI = {
    listarPorServico: (idServico)  => apiRequest(`/pecas-servico/servico/${idServico}`),
    criar:            (dto)        => apiRequest('/pecas-servico', { method: 'POST', body: dto }),
    deletar:          (id)         => apiRequest(`/pecas-servico/${id}`, { method: 'DELETE' }),
};
