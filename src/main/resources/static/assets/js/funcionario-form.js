 const btnSalvar = document.getElementById('btnSalvar');
const passwordInput = document.getElementById('password');
const passwordStatus = document.getElementById('password-status');
const successToast = new bootstrap.Toast(
    document.getElementById('successToast')
);

// ########################################
// EVENTOS
// ########################################
btnSalvar.addEventListener('click', salvar);
passwordInput.addEventListener('input', verificaForcaSenha);
document.getElementById('togglePassword').addEventListener('click', togglePassword);

// ########################################
// SALVAR
// ########################################
async function salvar() {
    try {
        toggleLoading(true);
        const payload = {
            nomeFuncionario:
                getValue('nomeFuncionario'),

            username:
                getValue('username'),

            password:
                getValue('password'),

            role:
                document.getElementById('role').value
        };

        const response = await fetch('/security/funcionarios', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            successToast.show();
            clearForm();
            setTimeout(() => {
                window.location.href =
                    '/security/funcionario/list';
            }, 1200);
            return;
        }
        alert('Você não tem permissão!');

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

function clearForm() {
    document.getElementById('nomeFuncionario').value = '';
    document.getElementById('username').value = '';
    document.getElementById('password').value = '';
}

function toggleLoading(status) {
    btnSalvar.disabled = status;
    btnSalvar.innerHTML = status
        ? '<span class="spinner-border spinner-border-sm"></span> Salvando...'
        : '<i class="bi bi-check-circle"></i> Salvar Funcionário';
}

// ########################################
// SENHA
// ########################################
function togglePassword() {
    const type = passwordInput.getAttribute('type');
    if (type === 'password') {
        passwordInput.setAttribute('type', 'text');
    } else {
        passwordInput.setAttribute('type', 'password');
    }
}

// ########################################
// FORÇA SENHA
// ########################################
function verificaForcaSenha() {
    const senha = passwordInput.value;
    const numeros = /([0-9])/;
    const alfabeto = /([a-zA-Z])/;
    const especiais = /([~,!,@,#,$,%,^,&,*,-,_,+,=,?,>,<])/;

    if (senha.length < 8) {
        passwordStatus.innerHTML =
            '<span style="color:#dc3545;">Fraca • mínimo 8 caracteres</span>';
        return;
    }

    if (
        senha.match(numeros)
        && senha.match(alfabeto)
        && senha.match(especiais)
    ) {
        passwordStatus.innerHTML =
            '<span style="color:#198754;font-weight:600;">Forte</span>';
        return;
    }
    passwordStatus.innerHTML = '<span style="color:#fd7e14;">Média • adicione caracteres especiais</span>';
}