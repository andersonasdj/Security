const btnSalvar = document.getElementById('btnSalvar');
const successToast = new bootstrap.Toast(
    document.getElementById('successToast')
);

// ########################################
// EVENTOS
// ########################################
btnSalvar.addEventListener('click', salvar);
document.getElementById('telefone').addEventListener('input', handlePhone);
document.getElementById('tempoContratado').addEventListener('input', handleHora);
document.getElementById('togglePassword').addEventListener('click', togglePassword);
document.getElementById('cnpj').addEventListener('input', handleCNPJ);

// ########################################
// SALVAR
// ########################################
async function salvar() {
    try {
        toggleLoading(true);
        const payload = {
            nomeCliente: getValue('nomeCliente'),
            endereco: getValue('endereco'),
            dominio: getValue('dominio'),
            telefone: getValue('telefone'),
            username: getValue('username'),
            password: getValue('password'),
            bairro: getValue('bairro'),
            cnpj: getValue('cnpj'),
            tempoContratado:
                parseFloat(getValue('tempoContratado') || 0) * 60
        };

        const response = await fetch('/security/clientes', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            successToast.show();
            setTimeout(() => {
                window.location.href = '/security/cliente/list';
            }, 1200);
            return;
        }

        if (response.status === 401) {
            alert('Realize login novamente!');
            return;
        }

        alert('Erro ao salvar cliente!');
    } catch (error) {
        console.error(error);
        alert('Falha na comunicação com o servidor.');
    } finally {
        toggleLoading(false);
    }
}

// ########################################
// HELPERS
// ########################################
function getValue(id) {
    return document.getElementById(id).value.trim();
}

function toggleLoading(status) {
    btnSalvar.disabled = status;
    btnSalvar.innerHTML = status
        ? '<span class="spinner-border spinner-border-sm"></span> Salvando...'
        : '<i class="bi bi-check-circle"></i> Salvar Cliente';
}

// ########################################
// TELEFONE
// ########################################
function handlePhone(event) {
    let value = event.target.value;
    value = value.replace(/\D/g, '');
    value = value.replace(/(\d{2})(\d)/, '($1) $2');
    value = value.replace(/(\d)(\d{4})$/, '$1-$2');
    event.target.value = value;
}

// ########################################
// HORAS
// ########################################
function handleHora(event) {
    let value = event.target.value;
    value = value.replace(/[^0-9\.]/g, '');
    event.target.value = value;
}

function handleCNPJ(event) {
    let value = event.target.value;
    // remove tudo que não for número
    value = value.replace(/\D/g, '');
    // limita em 14 dígitos
    value = value.substring(0, 14);
    // aplica máscara CNPJ
    value = value.replace(/^(\d{2})(\d)/, '$1.$2');
    value = value.replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3');
    value = value.replace(/\.(\d{3})(\d)/, '.$1/$2');
    value = value.replace(/(\d{4})(\d)/, '$1-$2');
    event.target.value = value;
}

// ########################################
// MOSTRAR SENHA
// ########################################
function togglePassword() {
    const input = document.getElementById('password');
    const icon = document.getElementById('togglePassword');
    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.remove('bi-eye');
        icon.classList.add('bi-eye-slash');
    } else {
        input.type = 'password';
        icon.classList.remove('bi-eye-slash');
        icon.classList.add('bi-eye');
    }
}