document.addEventListener("DOMContentLoaded", () => {
    iniciarSistema();
});

function showHomeLoading() {
    const overlay = document.getElementById("homeLoadingOverlay");
    if (!overlay) return;
    overlay.style.display = "flex";
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
    window.setTimeout(() => { overlay.style.display = "none"; }, 240);
}

// ======================================================
// INICIALIZAÇÃO
// ======================================================
async function iniciarSistema() {
    showHomeLoading();
    atualizarDataHero();
    try {
        await carregarHome();
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
        if (response.status === 401) {
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
function preencherHome(data) {
    window._homeData = data;

    if (window.navbarInit) window.navbarInit(data.nomeFuncionario);

    const primeiroNome = (data.nomeFuncionario || "Usuário").split(" ")[0];
    const heroNome = document.getElementById("heroNome");
    if (heroNome) heroNome.textContent = primeiroNome;

    const kpiClientes = document.getElementById("kpiClientes");
    const kpiFuncionarios = document.getElementById("kpiFuncionarios");
    const kpiCampanhas = document.getElementById("kpiCampanhas");
    if (kpiClientes)     animarNumero(kpiClientes,    data.totalClientes    || 0);
    if (kpiFuncionarios) animarNumero(kpiFuncionarios, data.totalFuncionarios || 0);
    if (kpiCampanhas)    animarNumero(kpiCampanhas,    data.totalCampanhas   || 0);

    preencherTaxaCliques(data);
    preencherEstatisticas(data);
    iniciarGraficoPhishing(data);
}

function preencherTaxaCliques(data) {
    const totalAlvos  = data.totalAlvos    || 0;
    const clicados    = data.totalClicados || 0;
    const taxa        = totalAlvos > 0 ? ((clicados / totalAlvos) * 100).toFixed(1) : 0;

    const kpiTaxa = document.getElementById("kpiTaxa");
    if (kpiTaxa) kpiTaxa.textContent = taxa + "%";

    const badge = document.getElementById("kpiRiskBadge");
    if (!badge || totalAlvos === 0) return;

    let label, bg, color;
    if      (taxa >= 30) { label = "CRÍTICO"; bg = "rgba(239,68,68,.15)";    color = "#f87171"; }
    else if (taxa >= 15) { label = "ALTO";    bg = "rgba(249,115,22,.15)";   color = "#fb923c"; }
    else if (taxa >= 5)  { label = "MÉDIO";   bg = "rgba(234,179,8,.15)";    color = "#facc15"; }
    else                 { label = "BAIXO";   bg = "rgba(16,185,129,.15)";   color = "#34d399"; }

    badge.textContent = label;
    badge.style.background = bg;
    badge.style.color = color;
}

// ======================================================
// ESTATÍSTICAS E POSTURA
// ======================================================
function preencherEstatisticas(data) {
    const totalAlvos  = data.totalAlvos    || 0;
    const enviados    = data.totalEnviados  || 0;
    const clicados    = data.totalClicados  || 0;
    const resistentes = Math.max(0, enviados - clicados);

    const pctEnvio  = totalAlvos > 0 ? (enviados    / totalAlvos * 100) : 0;
    const pctClique = enviados   > 0 ? (clicados    / enviados   * 100) : 0;
    const pctResist = enviados   > 0 ? (resistentes / enviados   * 100) : 0;

    _setStatBar("barEnvio",  pctEnvio,  "statPctEnvio",  "detEnvio",  `${enviados} de ${totalAlvos} alvos atingidos`);
    _setStatBar("barClique", pctClique, "statPctClique",  "detClique", `${clicados} de ${enviados} clicaram no link`);
    _setStatBar("barResist", pctResist, "statPctResist",  "detResist", `${resistentes} de ${enviados} resistiram ao phishing`);

    _preencherPostura(pctClique, totalAlvos);
}

function _setStatBar(barId, pct, pctElId, detElId, detail) {
    const pctEl = document.getElementById(pctElId);
    const detEl = document.getElementById(detElId);
    const bar   = document.getElementById(barId);
    if (pctEl) pctEl.textContent = pct.toFixed(1) + "%";
    if (detEl) detEl.textContent = detail;
    if (bar)   setTimeout(() => { bar.style.width = Math.min(pct, 100) + "%"; }, 200);
}

function _preencherPostura(taxaClique, totalAlvos) {
    const iconEl  = document.getElementById("posturaIcon");
    const labelEl = document.getElementById("posturaLabel");
    const descEl  = document.getElementById("posturaDesc");
    const recsEl  = document.getElementById("posturaRecs");
    if (!iconEl || !labelEl) return;

    let label, iconClass, bg, color, desc, recs;

    if (totalAlvos === 0) {
        label = "Indeterminado"; iconClass = "bi-shield-fill";
        bg = "rgba(100,116,139,.14)"; color = "#64748b";
        desc = "Nenhuma campanha de phishing realizada ainda.";
        recs = ["Inicie uma campanha para avaliar a postura de segurança."];
    } else if (taxaClique >= 30) {
        label = "Crítico"; iconClass = "bi-shield-x";
        bg = "rgba(239,68,68,.14)"; color = "#ef4444";
        desc = `Taxa de cliques em ${taxaClique.toFixed(1)}% — vulnerabilidade crítica detectada.`;
        recs = [
            "Reforce o treinamento de conscientização imediatamente.",
            "Aumente a frequência das campanhas de simulação.",
            "Revise as políticas de e-mail e filtragem de spam."
        ];
    } else if (taxaClique >= 15) {
        label = "Alto"; iconClass = "bi-shield-exclamation";
        bg = "rgba(249,115,22,.14)"; color = "#f97316";
        desc = `Taxa de cliques em ${taxaClique.toFixed(1)}% — atenção redobrada necessária.`;
        recs = [
            "Intensifique treinamentos nas equipes com maior taxa de cliques.",
            "Implante autenticação multifator nos sistemas críticos.",
            "Aumente a frequência das campanhas de simulação."
        ];
    } else if (taxaClique >= 5) {
        label = "Médio"; iconClass = "bi-shield-half";
        bg = "rgba(234,179,8,.14)"; color = "#eab308";
        desc = `Taxa de cliques em ${taxaClique.toFixed(1)}% — há espaço para melhorias.`;
        recs = [
            "Continue campanhas regulares de conscientização.",
            "Analise os resultados por departamento.",
            "Reconheça as equipes com melhor desempenho."
        ];
    } else {
        label = "Baixo"; iconClass = "bi-shield-fill-check";
        bg = "rgba(16,185,129,.14)"; color = "#10b981";
        desc = `Taxa de cliques em ${taxaClique.toFixed(1)}% — excelente postura de segurança.`;
        recs = [
            "Mantenha campanhas regulares para sustentar o resultado.",
            "Compartilhe os dados como referência de boas práticas.",
            "Continue monitorando e celebre os avanços com as equipes."
        ];
    }

    iconEl.style.background = bg;
    const iconI = iconEl.querySelector("i");
    if (iconI) { iconI.className = `bi ${iconClass}`; iconI.style.color = color; }
    labelEl.textContent = label;
    labelEl.style.color = color;
    if (descEl) descEl.textContent = desc;
    if (recsEl) {
        recsEl.innerHTML = recs.map(r =>
            `<li class="postura-rec-item"><i class="bi bi-chevron-right" style="font-size:.7rem;color:#475569;"></i>${escapeHtml(r)}</li>`
        ).join("");
    }
}

// ── Gráfico: estado global ──────────────────────────────
let _phishingData            = null;
let _phishingChart           = null;
let _chartListenersAttached  = false;

const CHART_PALETAS = {
    danger: ["#ef4444", "#f97316"],
    ocean:  ["#3b82f6", "#06b6d4"],
    mint:   ["#10b981", "#84cc16"]
};

function iniciarGraficoPhishing(data) {
    _phishingData = data;

    const totalAlvos     = data.totalAlvos || 0;
    const chartEmpty     = document.getElementById("chartEmpty");
    const chartContainer = document.getElementById("chartContainer");
    const chartControls  = document.getElementById("chartControls");

    if (totalAlvos === 0) {
        if (chartEmpty)     chartEmpty.style.display = "flex";
        if (chartContainer) chartContainer.style.display = "none";
        if (chartControls)  chartControls.style.visibility = "hidden";
        return;
    }

    const tipo   = localStorage.getItem("chartTipo")   || "donut";
    const paleta = localStorage.getItem("chartPaleta") || "danger";

    _marcarAtivoCtrl("chartTypeGroup",    `[data-chart-type="${tipo}"]`);
    _marcarAtivoCtrl("chartPaletteGroup", `[data-palette="${paleta}"]`);

    if (!_chartListenersAttached) {
        _chartListenersAttached = true;

        document.querySelectorAll("[data-chart-type]").forEach(btn => {
            btn.addEventListener("click", () => {
                const t = btn.dataset.chartType;
                localStorage.setItem("chartTipo", t);
                _marcarAtivoCtrl("chartTypeGroup", `[data-chart-type="${t}"]`);
                _renderizarGrafico(t, localStorage.getItem("chartPaleta") || "danger");
            });
        });

        document.querySelectorAll("[data-palette]").forEach(btn => {
            btn.addEventListener("click", () => {
                const p = btn.dataset.palette;
                localStorage.setItem("chartPaleta", p);
                _marcarAtivoCtrl("chartPaletteGroup", `[data-palette="${p}"]`);
                _renderizarGrafico(localStorage.getItem("chartTipo") || "donut", p);
            });
        });
    }

    _renderizarGrafico(tipo, paleta);
}

function _marcarAtivoCtrl(groupId, activeSelector) {
    const group = document.getElementById(groupId);
    if (!group) return;
    group.querySelectorAll("button").forEach(b => b.classList.remove("active"));
    const active = group.querySelector(activeSelector);
    if (active) active.classList.add("active");
}

function _renderizarGrafico(tipo, paleta) {
    if (_phishingChart) { _phishingChart.destroy(); _phishingChart = null; }

    const el = document.getElementById("chartPhishing");
    if (!el || !_phishingData) return;

    const totalAlvos          = _phishingData.totalAlvos    || 0;
    const enviados            = _phishingData.totalEnviados  || 0;
    const clicados            = _phishingData.totalClicados  || 0;
    const enviadosNaoClicados = Math.max(0, enviados - clicados);
    const naoEnviados         = Math.max(0, totalAlvos - enviados);
    const isDark              = document.body.classList.contains("dark-mode");

    const base2     = CHART_PALETAS[paleta] || CHART_PALETAS.danger;
    const neutro    = isDark ? "#334155" : "#cbd5e1";
    const colors    = [...base2, neutro];
    const textMuted = isDark ? "#94a3b8" : "#64748b";
    const textMain  = isDark ? "#f1f5f9" : "#0f172a";
    const themeName = isDark ? "dark" : "light";
    const gridColor = isDark ? "rgba(255,255,255,.05)" : "rgba(0,0,0,.05)";

    const baseChart = {
        height: "100%", background: "transparent",
        toolbar: { show: false },
        animations: { enabled: true, speed: 500 },
        fontFamily: "inherit"
    };

    let options;

    if (tipo === "bar") {
        options = {
            chart: { ...baseChart, type: "bar" },
            series: [{
                name: "Quantidade",
                data: [
                    { x: "Clicados",       y: clicados,            fillColor: colors[0] },
                    { x: "Enviados",       y: enviadosNaoClicados, fillColor: colors[1] },
                    { x: "Não enviados",   y: naoEnviados,         fillColor: colors[2] }
                ]
            }],
            plotOptions: { bar: { horizontal: true, borderRadius: 5, distributed: true, barHeight: "52%" } },
            colors,
            legend: { show: false },
            dataLabels: { enabled: true, style: { fontSize: "12px", fontWeight: 600, colors: [textMain] } },
            xaxis: { labels: { style: { colors: textMuted, fontSize: "12px" } } },
            yaxis: { labels: { style: { colors: textMuted, fontSize: "12px" } } },
            grid: { borderColor: gridColor },
            tooltip: { theme: themeName, y: { title: { formatter: () => "" } } },
            theme: { mode: themeName }
        };
    } else if (tipo === "radialBar") {
        const pctC = totalAlvos > 0 ? +((clicados / totalAlvos * 100).toFixed(1)) : 0;
        const pctE = totalAlvos > 0 ? +((enviados  / totalAlvos * 100).toFixed(1)) : 0;
        options = {
            chart: { ...baseChart, type: "radialBar" },
            series: [pctC, pctE],
            labels: ["Clicados", "Enviados"],
            colors: [colors[0], colors[1]],
            plotOptions: {
                radialBar: {
                    startAngle: -90, endAngle: 270,
                    hollow: { size: "28%", background: "transparent" },
                    track: { background: isDark ? "#1e293b" : "#f1f5f9", strokeWidth: "90%" },
                    dataLabels: {
                        name:  { fontSize: "12px", color: textMuted },
                        value: { fontSize: "16px", fontWeight: 700, color: textMain, formatter: v => v + "%" },
                        total: { show: true, label: "Alvos", fontSize: "12px", color: textMuted, formatter: () => totalAlvos }
                    }
                }
            },
            legend: { show: false },
            tooltip: { theme: themeName },
            theme: { mode: themeName }
        };
    } else {
        options = {
            chart: { ...baseChart, type: "donut" },
            series: [clicados, enviadosNaoClicados, naoEnviados],
            labels: ["Clicados", "Enviados (não clicou)", "Não enviados"],
            colors,
            plotOptions: {
                pie: {
                    donut: {
                        size: "70%",
                        labels: {
                            show: true,
                            total: { show: true, label: "Alvos", fontSize: "13px", fontWeight: 600, color: textMuted, formatter: () => totalAlvos },
                            value: { fontSize: "20px", fontWeight: 700, color: textMain }
                        }
                    }
                }
            },
            dataLabels: { enabled: false },
            stroke:  { width: 0 },
            legend:  { show: false },
            tooltip: { theme: themeName, y: { formatter: v => { const p = totalAlvos > 0 ? ` (${((v / totalAlvos) * 100).toFixed(1)}%)` : ""; return v + p; } } },
            theme:   { mode: themeName }
        };
    }

    _phishingChart = new ApexCharts(el, options);
    _phishingChart.render();

    _atualizarLegendaGrafico(tipo, colors, { totalAlvos, enviados, clicados, enviadosNaoClicados, naoEnviados });
}

function _atualizarLegendaGrafico(tipo, colors, v) {
    const legendEl = document.getElementById("chartLegend");
    if (!legendEl) return;

    const items = tipo === "radialBar"
        ? [
            { color: colors[0], label: "Clicados", value: `${v.clicados} (${v.totalAlvos > 0 ? ((v.clicados / v.totalAlvos) * 100).toFixed(1) : 0}%)` },
            { color: colors[1], label: "Enviados",  value: `${v.enviados} (${v.totalAlvos > 0 ? ((v.enviados  / v.totalAlvos) * 100).toFixed(1) : 0}%)` }
          ]
        : [
            { color: colors[0], label: "Clicados",              value: v.clicados },
            { color: colors[1], label: "Enviados (não clicou)", value: v.enviadosNaoClicados },
            { color: colors[2], label: "Não enviados",          value: v.naoEnviados }
          ];

    legendEl.innerHTML = items.map(i =>
        `<div class="legend-item"><div class="legend-dot" style="background:${i.color}"></div>${escapeHtml(i.label)} (${i.value})</div>`
    ).join("");
}

// ======================================================
// UTILITÁRIOS
// ======================================================
function atualizarDataHero() {
    const el = document.getElementById("heroDate");
    if (!el) return;
    const now = new Date();
    el.textContent = now.toLocaleDateString("pt-BR", {
        weekday: "long", year: "numeric", month: "long", day: "numeric"
    });
}

function animarNumero(el, alvo) {
    const duracao = 600;
    const passo   = 16;
    const passos  = duracao / passo;
    let atual = 0;
    const incremento = alvo / passos;
    const timer = setInterval(() => {
        atual = Math.min(atual + incremento, alvo);
        el.textContent = Math.round(atual);
        if (atual >= alvo) clearInterval(timer);
    }, passo);
}

function inserirInputId(id) {
    const inputId = document.getElementById("inputId");
    if (!inputId) return;
    inputId.innerHTML = `<input type="hidden" id="id" name="id" value="${id}">`;
}

// ======================================================
// TOAST
// ======================================================
function mostrarToast(mensagem, tipo) {
    const cfg = {
        warning: { bg: "#fff7ed", border: "#fed7aa", color: "#ea580c", icon: "bi-exclamation-triangle-fill" },
        success: { bg: "#f0fdf4", border: "#bbf7d0", color: "#16a34a", icon: "bi-check-circle-fill"         },
        danger:  { bg: "#fef2f2", border: "#fecaca", color: "#dc2626", icon: "bi-x-circle-fill"             }
    };
    const c  = cfg[tipo] || cfg.warning;
    const id = "toast_" + Date.now();
    const el = document.createElement("div");
    el.id = id;
    el.className = "toast show mb-2";
    el.setAttribute("role", "alert");
    el.style.cssText = `background:${c.bg};border:1.5px solid ${c.border};border-radius:12px;min-width:280px;max-width:340px;box-shadow:0 4px 16px rgba(0,0,0,.1);`;
    el.innerHTML = `
        <div class="d-flex align-items-start gap-2 p-3" style="color:${c.color}">
            <i class="bi ${c.icon} fs-5 flex-shrink-0 mt-1"></i>
            <span class="me-auto" style="font-size:.87rem;line-height:1.45;">${mensagem}</span>
            <button class="btn-close ms-1 mt-1" style="font-size:.65rem;opacity:.5;" onclick="this.closest('.toast').remove()"></button>
        </div>`;
    document.getElementById("toastContainer").appendChild(el);
    setTimeout(() => { if (el.parentNode) el.remove(); }, 5000);
}

function escapeHtml(str) {
    return String(str)
        .replace(/&/g,  "&amp;")
        .replace(/</g,  "&lt;")
        .replace(/>/g,  "&gt;")
        .replace(/"/g,  "&quot;")
        .replace(/'/g,  "&#039;");
}
