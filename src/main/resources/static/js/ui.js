/* ============================================================
   HardSoft — UI Helpers
   ============================================================ */

// ====================== TOAST ======================
function toast(message, type = 'success', title = null) {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const titles = { success: 'Sucesso', error: 'Erro', warning: 'Atencao', info: 'Info' };
    const el = document.createElement('div');
    el.className = `toast toast-${type}`;
    el.innerHTML = `
        <div class="toast-title">${title || titles[type] || 'Aviso'}</div>
        <div class="toast-message">${message}</div>
    `;
    container.appendChild(el);
    setTimeout(() => {
        el.style.opacity = '0';
        el.style.transition = 'opacity 0.3s';
        setTimeout(() => el.remove(), 300);
    }, 4000);
}

// ====================== MODAL ======================
function openModal(id) {
    const m = document.getElementById(id);
    if (m) m.classList.add('active');
}

function closeModal(id) {
    const m = document.getElementById(id);
    if (m) m.classList.remove('active');
}

// Fechar modal clicando no overlay
document.addEventListener('click', (e) => {
    if (e.target.classList.contains('modal-overlay')) {
        e.target.classList.remove('active');
    }
});

// ESC fecha qualquer modal aberto
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        document.querySelectorAll('.modal-overlay.active').forEach(m => m.classList.remove('active'));
    }
});

// ====================== CONFIRMACAO ======================
function confirmar(mensagem) {
    return window.confirm(mensagem);
}

// ====================== FORMATACAO ======================
function formatarMoeda(valor) {
    if (valor === null || valor === undefined) return 'R$ 0,00';
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency', currency: 'BRL'
    }).format(Number(valor));
}

function formatarData(dataIso) {
    if (!dataIso) return '-';
    const d = new Date(dataIso);
    return d.toLocaleDateString('pt-BR');
}

function formatarDataHora(dataIso) {
    if (!dataIso) return '-';
    const d = new Date(dataIso);
    return d.toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });
}

function formatarCpfCnpj(valor) {
    if (!valor) return '-';
    const num = String(valor).replace(/\D/g, '');
    if (num.length === 11) {
        return num.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
    } else if (num.length === 14) {
        return num.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5');
    }
    return valor;
}

function badgeStatus(status) {
    if (!status) return '';
    const classe = `badge-${status.toLowerCase()}`;
    const texto = status.replace('_', ' ');
    return `<span class="badge ${classe}">${texto}</span>`;
}

// ====================== SIDEBAR ======================
function marcarMenuAtivo(href) {
    document.querySelectorAll('.sidebar-nav a').forEach(a => {
        a.classList.toggle('active', a.getAttribute('href') === href);
    });
}

function renderizarSidebar(paginaAtual) {
    return `
    <aside class="sidebar">
        <div class="sidebar-logo">
            <h1>HardSoft</h1>
            <p>Gestao de OS</p>
        </div>
        <ul class="sidebar-nav">
            <li><a href="../index.html" data-page="dashboard">
                <span class="nav-icon">📊</span> Dashboard
            </a></li>
            <li><a href="ordens-servico.html" data-page="os">
                <span class="nav-icon">📋</span> Ordens de Servico
            </a></li>
            <li><a href="clientes.html" data-page="clientes">
                <span class="nav-icon">👥</span> Clientes
            </a></li>
            <li><a href="admins.html" data-page="admins">
                <span class="nav-icon">👤</span> Admins
            </a></li>
            <li><a href="setups.html" data-page="setups">
                <span class="nav-icon">💻</span> Setups</a></li>
            <li><a href="pecas.html" data-page="pecas">
                <span class="nav-icon">🔧</span> Pecas
            </a></li>
            <li><a href="tipos-servico.html" data-page="tipos">
                <span class="nav-icon">⚙️</span> Tipos de Servico
            </a></li>
        </ul>
    </aside>`;
}

// ====================== HELPERS ======================
function preencherSelect(selectEl, items, valueKey, labelFn, placeholder = 'Selecione...') {
    selectEl.innerHTML = `<option value="">${placeholder}</option>` +
        items.map(item => `<option value="${item[valueKey]}">${labelFn(item)}</option>`).join('');
}

function $(selector) { return document.querySelector(selector); }
function $$(selector) { return document.querySelectorAll(selector); }

// Coleta valores de um <form> em um objeto
function coletarForm(formEl) {
    const data = {};
    new FormData(formEl).forEach((value, key) => {
        data[key] = value === '' ? null : value;
    });
    return data;
}
