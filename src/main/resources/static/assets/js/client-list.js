
listarTodos();

function buscaPorPalavra(){
    $('#corpoClientes').empty();
    $('#paginas').empty();
    $('#contador').text("");
    $('#qtdFiltrados').text("");			
    let palavra = document.getElementById('palavraChave').value;
    
    if(palavra != ''){
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "/security/clientes/nome/"+palavra,
            success: function (data) {
                $('#contador').text(data.totalElements);
                $('#qtdFiltrados').text(data.totalElements);

                if (!data.content || data.content.length === 0) {
                    $('#corpoClientes').html('<tr><td colspan="3" class="text-center text-muted py-4"><i class="bi bi-search"></i> Nenhum cliente encontrado</td></tr>');
                    return;
                }

                data.content.map(i => {
                    var status;
                    if(i.ativo == true){
                        status = '<span class="badge bg-success">Ativado</span>';
                    }else{
                        status = '<span class="badge bg-danger">Desativado</span>';
                    }
                    
                    var vip =" ";
                    var redFlag =" ";
                    if(i.vip == true){
                        vip = '<span class="badge rounded-pill bg-primary-subtle text-primary border border-primary-subtle">VIP</span>';
                    }

                    if(i.redFlag == true){
                        redFlag = '<span class="badge rounded-pill bg-danger-subtle text-danger border border-danger-subtle">RED FLAG</span>';
                    }
                    
                    var itens = 
                    '<tr id=linha' + i.id + '>'
                    + `<td>
                            <div class="cliente-info">
                                <div class="cliente-nome">
                                    ${i.nomeCliente} ${vip} ${redFlag}
                                </div>
                            </div>
                        </td>`
                    + '<td>' + status + '</td>'
                    + `
                    <td class="acoes-cliente">
                        <div class="acoes-wrapper">		    
                        <button
                            class="btn-action btn btn-primary"
                            data-bs-toggle="modal"
                            data-bs-target="#exampleModal"
                            onclick="editar(${i.id})">
                            <i class="bi bi-pencil-square"></i>
                        </button>						   
                        <button class="btn-action btn btn-danger"
                            onclick="excluir(${i.id})">
                            <i class="bi bi-trash"></i>
                        </button>
                        </div>
                    </td>`
                    + '</tr>'
                $('#corpoClientes').append(itens);
                });
            },
            statusCode: {
                500: function() { alert('Erro! Contate o desenvolvedor!'); },
                401: function() { 
                    alert('Realize login novamente!'); 
                    window.location.href = "/sistech/login"; 
                }
            }
        });
    }else{
        listarTodos();
    }
}

function listarVip(filtro){
    $('#corpoClientes').empty();
    $('#paginas').empty();
    $('#qtdFiltrados').text("");
    $('#contador').empty();
    $('#palavraChave').val("");
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/security/clientes/filtro/vip/"+filtro,
        success: function (data) {
            $('#qtdFiltrados').text(data.length);
            if (!data || data.length === 0) {
                $('#corpoClientes').html('<tr><td colspan="3" class="text-center text-muted py-4"><i class="bi bi-filter"></i> Nenhum cliente encontrado com este filtro</td></tr>');
                return;
            }
            data.map(i => {
                var status;
                var vip =" ";
                var redFlag =" ";
                if(i.ativo == true){
                    status = '<span class="badge bg-success">Ativado</span>';
                }else{
                    status = '<span class="badge bg-danger">Desativado</span>';
                }
                
                if(i.vip == true){
                    vip = '<span class="badge rounded-pill bg-primary-subtle text-primary border border-primary-subtle">VIP</span>';
                }

                if(i.redFlag == true){
                    redFlag = '<span class="badge rounded-pill bg-danger-subtle text-danger border border-danger-subtle">RED FLAG</span>';
                }
                
                var itens = 
                '<tr id=linha' + i.id + '>'
                    + `<td>
                        <div class="cliente-info">
                            <div class="cliente-nome">
                                ${i.nomeCliente} ${vip} ${redFlag}
                            </div>
                        </div>
                    </td>`
                + '<td>' + status + '</t}>'
                + `
                <td class="acoes-cliente">
                    <div class="acoes-wrapper">					
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
            $('#corpoClientes').append(itens);
        });
    },
    statusCode: {
            500: function() { alert('Erro! Contate o desenvolvedor!'); },
            401: function() { 
                alert('Realize login novamente!'); 
                window.location.href = "/sistech/login"; 
            }
    }
    });
}	
    
$(document).ready(function () {
    var divQtdItens = $("#bairro");
    divQtdItens.on("change", function () {
        $('#corpoClientes').empty();
        $('#paginas').empty();
        if($('#bairro').val() == ""){
            listarTodos();
        }else{
            listarPorBairro($('#bairro').val());
        }
    });
});

function filtros(){
    $("#opcoesFiltro").stop().slideToggle(800);
}

function listarTodos(){
    $.ajax({
    type: "GET",
    contentType: "application/json",
    url: "/security/clientes",
    success: function (data) {
        $('#qtdFiltrados').text(data.totalElements);
        $('#contador').text(data.totalElements);

        if (!data.content || data.content.length === 0) {
            $('#corpoClientes').html('<tr><td colspan="3" class="text-center text-muted py-4"><i class="bi bi-buildings"></i> Nenhum cliente cadastrado</td></tr>');
            return;
        }

        if(data.totalPages > 1){
            for(var i=0; i < data.totalPages ; i++ ){
                    $('#paginas').append('<li class="page-item" id=numPag'+ i+'"><a id=numPag'+ i+' class="page-link" onclick="paginacao('+ i +')">'+ (i + 1) +'</a></li>');
            }
            $("#numPag0").attr("style","color:red");
        }

        data.content.map(i => {
            var status;
            if(i.ativo == true){
                status = '<span class="badge bg-success">Ativado</span>';
            }else{
                status = '<span class="badge bg-danger">Desativado</span>';
            }
            
            var vip =" ";
            var redFlag =" ";
            if(i.vip == true){
                vip = '<span class="badge rounded-pill bg-primary-subtle text-primary border border-primary-subtle">VIP</span>';
            }

            if(i.redFlag == true){
                redFlag = '<span class="badge rounded-pill bg-danger-subtle text-danger border border-danger-subtle">RED FLAG</span>';
            }
            
            var itens = 
            '<tr id=linha' + i.id + '>'
                + `<td>
                    <div class="cliente-info">
                        <div class="cliente-nome">
                            ${i.nomeCliente} ${vip} ${redFlag}
                        </div>
                    </div>
                </td>`
            + '<td>' + status + '</t}>'
            + `
            <td class="acoes-cliente">
                <div class="acoes-wrapper">				    
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
        $('#corpoClientes').append(itens);
        });
    },
    statusCode: {
            500: function() { alert('Erro! Contate o desenvolvedor!'); },
            401: function() { 
                alert('Realize login novamente!'); 
                window.location.href = "/sistech/login"; 
            }
    }
    });
}

$.ajax({
    type: "GET",
    contentType: "application/json",
    url: "/security/clientes/bairros",
    success: function (data) {
        for (var i = 0; i < data.length; i++) {
            $('#bairro').append('<option value="' + data[i] + '">' + data[i] + '</option>');
        }
    },
    statusCode: {
            500: function() { alert('Erro! Contate o desenvolvedor!'); },
            401: function() { 
                alert('Realize login novamente!'); 
                window.location.href = "/sistech/login"; 
            }
    }
});

const handlePhone = (event) => {
    let input = event.target
    input.value = phoneMask(input.value)
}

const phoneMask = (value) => {
    if (!value) return ""
    value = value.replace(/\D/g,'')
    value = value.replace(/(\d{2})(\d)/,"($1) $2")
    value = value.replace(/(\d)(\d{4})$/,"$1-$2")
    return value
}

const handleComa = (event) => {
    let input = event.target
    input.value = horaMask(input.value)
}

const horaMask = (value) => {
    if (!value) return ""
    value = value.replace(/[^0-9\.]/g,'');
    return value;
}

function listarPorBairro(bairro){
    $('#qtdFiltrados').text("");
    $('#contador').empty();
    $('#palavraChave').val("");
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/security/clientes/filtro/"+bairro,
        success: function (data) {
            $('#qtdFiltrados').text(data.length);
            if (!data || data.length === 0) {
                $('#corpoClientes').html('<tr><td colspan="3" class="text-center text-muted py-4"><i class="bi bi-geo-alt"></i> Nenhum cliente neste bairro</td></tr>');
                return;
            }
            data.map(i => {
                var status;
                var vip =" ";
                var redFlag =" ";
                if(i.ativo == true){
                    status = '<span class="badge bg-success">Ativado</span>';
                }else{
                    status = '<span class="badge bg-danger">Desativado</span>';
                }					
                
                if(i.vip == true){
                    vip = '<span class="badge rounded-pill bg-primary-subtle text-primary border border-primary-subtle">VIP</span>';
                }

                if(i.redFlag == true){
                    redFlag = '<span class="badge rounded-pill bg-danger-subtle text-danger border border-danger-subtle">RED FLAG</span>';
                }
                
                var itens = 
                '<tr id=linha' + i.id + '>'
                    + `<td>
                        <div class="cliente-info">
                            <div class="cliente-nome">
                                ${i.nomeCliente} ${vip} ${redFlag}
                            </div>
                        </div>
                    </td>`
                + '<td>' + status + '</t}>'
                + `
                <td class="acoes-cliente">
                    <div class="acoes-wrapper">				    
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
            $('#corpoClientes').append(itens);
        });
        },
        statusCode: {
                500: function() { alert('Erro! Contate o desenvolvedor!'); },
                401: function() { 
                    alert('Realize login novamente!'); 
                    window.location.href = "/sistech/login"; 
                }
        }
    });	
}

function paginacao(num){
    $.ajax({
    type: "GET",
    contentType: "application/json",
    url: "/security/clientes?page="+num,
    success: function (data) {
        $('#corpoClientes').empty();
        $('#paginas').empty();
        
        for(var i=0; i < data.totalPages ; i++ ){
            $('#paginas').append('<li class="page-item"><a id=numPag'+ i+' class="page-link" onclick="paginacao('+ i +')">'+ (i + 1) +'</a></li>');
        }
        $("#numPag"+num).attr("style","color:red");

        data.content.map(i => {
            var status,vip="",redFlag="";
            if(i.ativo == true){ status = "<FONT COLOR='#008000'>Ativado</FONT>"; }
            else{ status = "<FONT COLOR='#ff0000'>Desativado</FONT>"; }
            
            if(i.vip == true){
                vip = '<span class="badge rounded-pill bg-primary-subtle text-primary border border-primary-subtle">VIP</span>';
            }

            if(i.redFlag == true){
                redFlag = '<span class="badge rounded-pill bg-danger-subtle text-danger border border-danger-subtle">RED FLAG</span>';
            }
            
            var itens = 
            '<tr id=linha' + i.id + '>'
                + `<td>
                    <div class="cliente-info">
                        <div class="cliente-nome">
                            ${i.nomeCliente} ${vip} ${redFlag}
                        </div>
                    </div>
                </td>`
            + '<td>' + status + '</t>'
            + '<td>'
            + `
            <td class="acoes-cliente">
                <div class="acoes-wrapper">			    
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
            + '</td>'
            + '</tr>'
        $('#corpoClientes').append(itens);
        });
        },
        statusCode: {
                500: function() { alert('Erro! Contate o desenvolvedor!'); },
                401: function() { 
                    alert('Realize login novamente!'); 
                    window.location.href = "/sistech/login"; 
                }
        }
    });
}

function excluir(id) {
    if (confirm("Tem certeza que deseja excluir o cliente de ID: " + id + " ?") == true) {
        var t = "#linhaModalColaborador" + id;
        var remove = $(t).closest('tr');
        $.ajax({
            type: "DELETE",
            contentType: "application/json",
            url: "/security/clientes/delete/" + id,
            success: function () {
                if (confirm("Tem certeza que deseja excluir?") == true) {
                    remove.fadeOut(1500, function () {
                        remove.remove();
                    });
                }
            },
            error: function (e) { alert("Erro: Esse cliente possui solicitações cadastradas!"); }
        });
    }
}

function editar(id) {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/security/clientes/edit/" + id,
        success: function (data) {
            $('#idmodal').val("");
            $('#nomemodal').val("");
            $('#enderecomodal').val("");
            $('#ativoModal').empty();
            $('#vipModal').empty();
            $('#redFlagModal').empty();
            $('#bairroModal').val("");
            $('#tempoContratado').val("");
            $('#apitokenModal').val("");
            $('#dominiomodal').val("");
            $('#ultimologinModal').val("");
            $('#usernamemodal').val("");
            $('#passwordmodal').val("");

            $('#idmodal').val(data.id);
            $('#nomemodal').val(data.nomeCliente);
            $('#enderecomodal').val(data.endereco);
            $('#telefonemodal').val(data.telefone);
            $('#usuariomodal').val(data.username);
            $('#senhamodal').val(data.password);
            $('#cnpjmodal').val(data.cnpj);
            $('#bairroModal').val(data.bairro);
            $('#tempoContratado').val(data.tempoContratado/60);
            $('#apitokenModal').val(data.token);
            $('#dominiomodal').val(data.dominio);
            $('#ultimologinModal').val(data.dataUltimoLogin);
            $('#usernamemodal').val(data.username);
            $('#passwordmodal').val(data.password);
                                
            if (data.ativo == true) {
                $('#ativoModal').append("<option value=" + data.ativo + "> Ativado </option>");
                $('#ativoModal').append("<option value=false>Desativado</option>");
            } else {
                $('#ativoModal').append("<option value=" + data.ativo + "> Desativado</option>");
                $('#ativoModal').append("<option value=true>Ativado</option>");
            }
            
            if (data.vip == true) {
                $('#vipModal').append("<option value=" + data.vip + "> Ativado </option>");
                $('#vipModal').append("<option value=false>Desativado</option>");
            } else {
                $('#vipModal').append("<option value=" + data.vip + "> Desativado </option>");
                $('#vipModal').append("<option value=true>Ativado</option>");
            }
            
            if (data.redFlag == true) {
                $('#redFlagModal').append("<option value=" + data.redFlag + "> Ativado </option>");
                $('#redFlagModal').append("<option value=false>Desativado</option>");
            } else {
                $('#redFlagModal').append("<option value=" + data.redFlag + "> Desativado </option>");
                $('#redFlagModal').append("<option value=true>Ativado</option>");
            }
        },
        statusCode: {
                500: function() { alert('Erro! Contate o desenvolvedor!'); },
                401: function() { 
                    alert('Realize login novamente!'); 
                    window.location.href = "/sistech/login"; 
                },
                405: function() { alert('Você não possui permissão de edição!'); }
        }
    });
}

function atualiza() {
    var id = $('#idmodal').val(),
        nomeCliente = $('#nomemodal').val(),
        username = $('#usuariomodal').val(),
        password = $('#senhamodal').val(),
        telefone = $('#telefonemodal').val(),
        cnpj = $('#cnpjmodal').val(),
        endereco = $('#enderecomodal').val(),
        dominio = $('#dominiomodal').val(),
        ativo = $('#ativoModal').val(),
        vip = $('#vipModal').val(),
        redFlag = $('#redFlagModal').val();
        bairro = $('#bairroModal').val(),
        tempoContratado = $('#tempoContratado').val(),
        token = $('#apitokenModal').val(),
    
    $.ajax({
        type: "PUT",
        contentType: "application/json",
        url: "/security/clientes",
        data: JSON.stringify({
            "id": id,
            "nomeCliente": nomeCliente,
            "username": username,
            "password": password,
            "telefone": telefone,
            "cnpj": cnpj,
            "endereco": endereco,
            "dominio": dominio,
            "ativo": ativo,
            "vip": vip,
            "redFlag": redFlag,
            "bairro": bairro,
            "tempoContratado":tempoContratado*60,
            "token": token
        }),
        success: function (data) {
            var status, vipBadge = "", redFlagBadge = "";
            if(data.ativo == true){
                status = '<span class="badge bg-success">Ativado</span>';
            }else{
                status = '<span class="badge bg-danger">Desativado</span>';
            }

            if(data.vip == true){
                vipBadge = '<span class="badge rounded-pill bg-primary-subtle text-primary border border-primary-subtle">VIP</span>';
            }

            if(data.redFlag == true){
                redFlagBadge = '<span class="badge rounded-pill bg-danger-subtle text-danger border border-danger-subtle">RED FLAG</span>';
            }
            var itens =
                '<tr id=linha' + data.id + '>'
                    + `<td>
                        <div class="cliente-info">
                            <div class="cliente-nome">
                                ${data.nomeCliente} ${vipBadge} ${redFlagBadge}
                            </div>
                        </div>
                    </td>`
                + '<td>' + status + '</td>'
                + `
                <td class="acoes-cliente">
                    <div class="acoes-wrapper">					   
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
            $('#corpoClientes').append(itens);
            alert("atualizado com sucesso");
        },
        statusCode: {
            500: function() { alert('Erro! Contate o desenvolvedor!'); },
            401: function() { 
                alert('Realize login novamente!'); 
                window.location.href = "/sistech/login"; 
            }
        }
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
    $('.counter').text(jobCount + ' item');

    if(jobCount == '0') {$('.no-result').show();}
    else {$('.no-result').hide();}
            });
});