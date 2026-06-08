function abrirConfiguracoesAvancadas() {
    const idFuncionario = $('#idmodal').val();
        if (!idFuncionario) {
            alert("Funcionário não identificado.");
            return;
        }
        window.open(
            `/security/funcionario/avancado?id=${idFuncionario}`,
            '_blank'
        );
}

function salvarConfiguracoesAvancadas() {
    const id = $('#idFuncionarioAvancado').val();
    const role = $('#funcaoAvancada').val();
    const valorHora = $('#valorHora').val();
    $.ajax({
        type: "PUT",
        contentType: "application/json",
        url: "/security/funcionarios/configuracoes-avancadas",
        data: JSON.stringify({
            id: id,
            role: role,
            valorHora: valorHora
        }),
        success: function () {
            alert("Configurações avançadas atualizadas com sucesso!");
            $('#modalConfiguracoesAvancadas').modal('hide');
        },
        error: function () {
            alert("Você não tem permissão para essa ação.");
        }
    });
}

$.ajax({
    type: "GET",
    contentType: "application/json",
    url: "/security/funcionarios",
    success: function (data) {
        $('#contador').text(data.length);
        $('#qtdFiltrados').text(data.length + ' registros');

        if (!data || data.length === 0) {
            $('#tabela-usuarios tbody').html('<tr><td colspan="6" class="text-center text-muted py-4"><i class="bi bi-people"></i> Nenhum funcionário cadastrado</td></tr>');
            return;
        }

        data.map(i => {
            var status, ausente, mfa;

            if(i.ativo == true){
                status = '<span class="badge bg-success">Ativado</span>';
            }else{
                status = '<span class="badge bg-danger">Desativado</span>';
            }

            if(i.ausente == true){
                ausente = '<span class="badge bg-danger">Ausente</span>';
            }else{
                ausente = '<span class="badge bg-success">Presente</span>';
            }

            if(i.mfa == true){
                mfa = '<span class="badge bg-success">On</span>';
            }else{
                mfa = '<span class="badge bg-danger">Off</span>';
            }

            var itens =
            '<tr id=linha' + i.id + '>'
            + '<td id=name' + i.id + '>' + i.nomeFuncionario + '</td>'
            + '<td>' + ausente + '</td>'
            + '<td>' + status + '</td>'
            + '<td>' + mfa + '</td>'
            + '<td>' + i.role + '</td>'
            + `<td class="acoes-cliente">
                <div class="acoes-wrapper">
                <button
                    class="btn-action btn btn-outline-warning"
                    title="Alterar senha"
                    data-bs-toggle="modal"
                    data-bs-target="#exampleModalSenha"
                    onclick="preencheModalPassword(${i.id})">
                    <img src="../assets/img/key-change.png" style="height: 16px">
                </button>		    
                <button
                    class="btn-action btn btn-primary"
                    data-bs-toggle="modal"
                    data-bs-target="#exampleModal"
                    onclick="editar(${i.id})">
                    <i class="bi bi-pencil-square"></i>
                </button>
                <button
                    class="btn-action btn btn-danger"
                    onclick="excluir(${i.id})">
                    <i class="bi bi-trash"></i>
                </button>
                </div>
            </td>`
            + '</tr>'
        $('#tabela-usuarios').append(itens);
        });
    },
    statusCode: {
        500: function() { alert('Erro! Contate o desenvolvedor!'); },
        401: function() { alert('Realize login novamente!'); }
    }
});

function filtrarStatus(tipo) {
    var rows = $('#tabela-usuarios tbody tr');
    var count = 0;
    rows.each(function() {
        var row = $(this);
        var show = false;
        if (tipo === 'ativo') {
            show = row.find('td:eq(2) .badge').hasClass('bg-success');
        } else if (tipo === 'inativo') {
            show = row.find('td:eq(2) .badge').hasClass('bg-danger');
        } else if (tipo === 'ausente') {
            show = row.find('td:eq(1) .badge').hasClass('bg-danger');
        }
        if (show) { row.show(); count++; } else { row.hide(); }
    });
    $('#qtdFiltrados').text(count + ' registros');
}

function verificaForcaSenha() {
    var numeros = /([0-9])/;
    var alfabeto = /([a-zA-Z])/;
    var chEspeciais = /([~,!,@,#,$,%,^,&,*,-,_,+,=,?,>,<])/;

    if ($('#idpasswordmodal').val().length < 8) {
        $('#password-status').html("<span style='color:red'>Fraco, insira no mínimo 8 caracteres</span>");
    } else {
        if ($('#idpasswordmodal').val().match(numeros) && $('#idpasswordmodal').val().match(alfabeto) && $('#idpasswordmodal').val().match(chEspeciais)) {
            $('#password-status').html("<span style='color:green'><b>Forte</b></span>");
        } else {
            $('#password-status').html("<span style='color:orange'>Médio, insira um caracter especial</span>");
        }
    }
}
function changeviewer() {
    var tipo = $('#idpasswordmodal').attr("type").toString();
    if (tipo == 'password' && tipo != '') {
        $('#idpasswordmodal').attr("type", 'text');
    } else {
        $('#idpasswordmodal').attr("type", 'password');
    }
}
function excluir(id) {
    if (confirm("Tem certeza que deseja excluir?" + id) == true) {
        var t = "#linha" + id;
        var remove = $(t).closest('tr');
        $.ajax({
            type: "DELETE",
            contentType: "application/json",
            url: "/security/funcionarios/" + id,
            success: function () {
                remove.fadeOut(1500, function () {
                    remove.remove();
                });
            },
            error: function (e) { alert("Não foi possível excluir"); }
        });
    }
}
function editar(id) {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/security/funcionarios/" + id,
        success: function (data) {

            $('#idmodal').val("");
            $('#nomemodal').val("");
            $('#usuariomodal').val("");
            $('#senhamodal').val("");
            $('#funcaomodal').empty();
            $('#statusModal').empty();
            $('#mfaModal').empty();
            $('#dataUltimoLoginModal').text("");
            $('#dataUltimaTrocaSenhaModal').text("");
            $('#ausenteModal').empty();
            $('#emailmodal').val("");

            $('#idmodal').val(data.id);
            $('#nomemodal').val(data.nomeFuncionario);
            $('#usuariomodal').val(data.username);
            $('#senhamodal').val(data.password);
            $('#dataUltimoLoginModal').text(data.dataUltimoLogin);
            $('#dataUltimaTrocaSenhaModal').text(data.dataAtualizacaoSenha);
            $('#emailmodal').val(data.email);

            var ausente, status;

            if(data.ausente == true ){ ausente = "Ausente"; }
            else{ ausente = "Presente"; }

            if(data.ativo == true ){ status = "Ativado"; }
            else{ status = "Desativado"; }

            if(data.trocaSenha == true){ document.getElementById("trocaSenha").checked=true; }
            else{ document.getElementById("trocaSenha").checked=false; }

            if (data.ausente == true) {
                $('#ausenteModal').append("<option value=" + data.ausente + ">" + ausente + "</option>");
                $('#ausenteModal').append("<option value=false>Presente</option>");
            } else {
                $('#ausenteModal').append("<option value=" + data.ausente + ">" + ausente + "</option>");
                $('#ausenteModal').append("<option value=true>Ausente</option>");
            }

            if (data.ativo == true) {
                $('#statusModal').append("<option value=" + data.ativo + ">" + status + "</option>");
                $('#statusModal').append("<option value=false>Desativado</option>");
            } else {
                $('#statusModal').append("<option value=" + data.ativo + ">" + status + "</option>");
                $('#statusModal').append("<option value=true>Ativado</option>");
            }

            if (data.mfa == true) {
                $('#mfaModal').append("<option value=" + data.mfa + ">" + data.mfa + "</option>");
                $('#mfaModal').append("<option value=false>false</option>");
            } else {
                $('#mfaModal').append("<option value=" + data.mfa + ">" + data.mfa + "</option>");
                $('#mfaModal').append("<option value=true>true</option>");
            }
        },
        statusCode: {
            500: function() { alert('Erro! Contate o desenvolvedor!'); },
            401: function() { alert('Realize login novamente!'); },
            405: function() { alert('Voce não tem permissão para exclusão!'); }
        }
    });
}
function preencheModalPassword(id) {
    $('#idmodalsenha').val("");
    $('#idpasswordmodal').val("");
    $('#nomeUsuarioModal').val("");
    $('#password-status').text('');
    $('#idmodalsenha').val(id);
    $('#nomeUsuarioModal').text($('#name' + id).text());
    $('#btnSave').removeAttr('disabled');
}

function changePassword() {
    var idModalSenha = $('#idmodalsenha').val();
    var senhaModal = $('#idpasswordmodal').val();

    if (senhaModal.length >= 8) {
        $.ajax({
            type: "PUT",
            contentType: "application/json",
            url: "/security/funcionarios/senha",
            data: JSON.stringify({
                "id": idModalSenha,
                "password": senhaModal
            }),
            success: function (data) {
                $('#password-status').text('');
                if(data){
                    $('#password-status').append("<b style='color: rgb(38, 162, 105);'> Senha atualizada com sucesso!</b>");
                    $('#btnSave').attr("disabled", "disabled");
                }else{
                    $('#password-status').append("<b style='color: red;'> Senha não atualizada!</b>");
                }
            },
            error: function (e) { alert("Você não tem permissão !"); }
        });
    } else {
        $('#password-status').text('');
        $('#password-status').append("<b style='color: red;'>Senha com menos de 8 caracteres</b>");
    }
}
function atualizar() {
    var id = $('#idmodal').val();
    var nomeFuncionario = $('#nomemodal').val();
    var username = $('#usuariomodal').val();
    var ativo = $('#statusModal').val();
    var mfa = $('#mfaModal').val();
    var ausente = $('#ausenteModal').val();
    var email = $('#emailmodal').val();

    var trocaSenha = document.getElementById("trocaSenha");
    if(trocaSenha.checked){ trocaSenha=true } else { trocaSenha=false }

    $.ajax({
        type: "PUT",
        contentType: "application/json",
        url: "/security/funcionarios",
        data: JSON.stringify({
            "id": id,
            "nomeFuncionario": nomeFuncionario,
            "username": username,
            "ativo": ativo,
            "mfa": mfa,
            "ausente": ausente,
            "trocaSenha":trocaSenha,
            "email":email
        }),
        success: function (data) {
            var status, ausente;

            if(data.ativo == true){
                status = '<span class="badge bg-success">Ativado</span>';
            }else{
                status = '<span class="badge bg-danger">Desativado</span>';
            }

            if(data.ausente == true){
                ausente = '<span class="badge bg-danger">Ausente</span>';
            }else{
                ausente = '<span class="badge bg-success">Presente</span>';
            }

            if(data.mfa == true){
                mfa = '<span class="badge bg-success">On</span>';
            }else{
                mfa = '<span class="badge bg-danger">Off</span>';
            }

            var itens =
                '<tr id=linha' + data.id + '>'
                + '<td id=name' + data.id + '>' + data.nomeFuncionario + '</td>'
                + '<td>' + ausente + '</td>'
                + '<td>' + status + '</td>'
                + '<td>' + mfa + '</td>'
                + '<td>' + data.role + '</td>'
                + `<td class="acoes-cliente">
                    <div class="acoes-wrapper">
                    <button
                        class="btn-action btn btn-outline-warning"
                        title="Alterar senha"
                        data-bs-toggle="modal"
                        data-bs-target="#exampleModalSenha"
                        onclick="preencheModalPassword(${data.id})">
                        <img src="../assets/img/key-change.png" style="height: 16px">
                    </button>					   
                    <button
                        class="btn-action btn btn-primary"
                        data-bs-toggle="modal"
                        data-bs-target="#exampleModal"
                        onclick="editar(${data.id})">
                        <i class="bi bi-pencil-square"></i>
                    </button>
                    <button
                        class="btn-action btn btn-danger"
                        onclick="excluir(${data.id})">
                        <i class="bi bi-trash"></i>
                    </button>
                    </div>
                </td>`
                + '</tr>';

            $('#linha' + data.id).remove();
            $('#tabela-usuarios').append(itens);
            alert("atualizado com sucesso");
        },
        error: function (e) { alert("Você não tem permissão !"); }
    });
}

$(document).ready(function() {
    $(".search").keyup(function () {
    var searchTerm = $(".search").val();
    var listItem = $('.results tbody').children('tr');
    var searchSplit = searchTerm.replace(/ /g, "'):containsi('")

    $.extend($.expr[':'], {'containsi': function(elem, i, match, array){
        return (elem.textContent || elem.innerText || '').toLowerCase().indexOf((match[3] || "").toLowerCase()) >= 0;
    }
    });

    $(".results tbody tr").not(":containsi('" + searchSplit + "')").each(function(e){
    $(this).attr('visible','false');
    });

    $(".results tbody tr:containsi('" + searchSplit + "')").each(function(e){
    $(this).attr('visible','true');
    });

    var jobCount = $('.results tbody tr[visible="true"]').length;
    $('#qtdFiltrados').text(jobCount + ' registros');

    if(jobCount == '0') {$('.no-result').show();}
    else {$('.no-result').hide();}
            });
});