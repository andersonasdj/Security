document.addEventListener("DOMContentLoaded", () => {
    iniciarSistema();
});

function showHomeLoading() {
    const overlay = document.getElementById("homeLoadingOverlay");
    if (!overlay) return;
    overlay.style.display = "block";
    requestAnimationFrame(() => {
        overlay.style.opacity = "1";
        overlay.style.pointerEvents = "auto";
    });
}

function hideHomeLoading() {
    const overlay = document.getElementById("homeLoadingOverlay");
    if (!overlay) return;
    overlay.style.opacity = "0";
    overlay.style.pointerEvents = "none";
    window.setTimeout(() => {
        overlay.style.display = "none";
    }, 240);
}

// ======================================================
// INICIALIZAÇÃO
// ======================================================
async function iniciarSistema() {
    showHomeLoading();
    try {
        await Promise.all([
            carregarHome(),
        ]);
    } catch (e) {
        console.error("Erro ao iniciar sistema", e);
    } finally {
        hideHomeLoading();
    }
}

// ======================================================
// HOME
// ======================================================
async function carregarHome() {
    try {
        const response = await fetch("/security/funcionarios/home");

        if(response.status === 401){
            alert("Realize login novamente!");
            window.location.href = "/security/login";
            return;
        }
        const data = await response.json();
        preencherHome(data);    

    } catch (e) {
        console.error("Erro ao carregar home", e);
    }
}

// ======================================================
// PREENCHIMENTO HOME
// ======================================================
function formatarNumero2(valor) {
    return String(valor).padStart(2, '0');
}

function preencherHome(data){
    _homeData = data;
    if (window.navbarInit) window.navbarInit(data.nomeFuncionario);
}

function inserirInputId(id){
    const inputId = document.getElementById("inputId");
    inputId.innerHTML = `
        <input type="hidden" id="id" name="id" value="${id}">
    `;
}

// ======================================================
// TOAST
// ======================================================
function mostrarToast(mensagem, tipo) {
    const cfg = {
        warning: { bg:'#fff7ed', border:'#fed7aa', color:'#ea580c', icon:'bi-exclamation-triangle-fill' },
        success: { bg:'#f0fdf4', border:'#bbf7d0', color:'#16a34a', icon:'bi-check-circle-fill'        },
        danger:  { bg:'#fef2f2', border:'#fecaca', color:'#dc2626', icon:'bi-x-circle-fill'            }
    };
    const c = cfg[tipo] || cfg.warning;
    const id = 'toast_' + Date.now();
    const el = document.createElement('div');
    el.id = id;
    el.className = 'toast show mb-2';
    el.setAttribute('role','alert');
    el.style.cssText = `background:${c.bg};border:1.5px solid ${c.border};border-radius:12px;min-width:280px;max-width:340px;box-shadow:0 4px 16px rgba(0,0,0,.1);`;
    el.innerHTML = `
        <div class="d-flex align-items-start gap-2 p-3" style="color:${c.color}">
            <i class="bi ${c.icon} fs-5 flex-shrink-0 mt-1"></i>
            <span class="me-auto" style="font-size:.87rem;line-height:1.45;">${mensagem}</span>
            <button class="btn-close ms-1 mt-1" style="font-size:.65rem;opacity:.5;" onclick="this.closest('.toast').remove()"></button>
        </div>
    `;
    document.getElementById('toastContainer').appendChild(el);
    setTimeout(() => { if(el.parentNode) el.remove(); }, 5000);
}

// iniciarLayout() e demais funções de sidebar estão em assets/js/sidebar.js
function escapeHtml(str) {
    return String(str)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}