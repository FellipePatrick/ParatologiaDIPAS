document.querySelectorAll("li").forEach(function(item) {
    item.addEventListener("click", function() {
        item.classList.toggle("clicked");
    });
});
document.addEventListener("DOMContentLoaded", function () {
    const input = document.getElementById("searchInput");
    const resultsDiv = document.getElementById("searchResults");

    input.addEventListener("input", function () {
        const query = input.value.trim().toLowerCase();
        resultsDiv.innerHTML = "";

        if (query.length === 0) {
            resultsDiv.style.display = "none";
            return;
        }

        const resultados = chamadosSearch.filter(rel =>
            rel.titulo && rel.titulo.toLowerCase().includes(query)
        );

        if (resultados.length === 0) {
            resultsDiv.innerHTML = `<div class="text-muted small">Nenhum resultado encontrado</div>`;
        } else {
            resultados.forEach(rel => {
                const item = document.createElement("a");
                item.href = `/relatorios/${rel.id}`;
                item.className = "dropdown-item d-flex align-items-center";
                item.innerHTML = `
                    <div class="font-weight-bold">
                        <div class="text-truncate">${rel.titulo}</div>
                        <div class="small text-gray-500">${rel.status} · ${rel.data}</div>
                    </div>
                `;
                resultsDiv.appendChild(item);
            });
        }

        resultsDiv.style.display = "block";
    });
});




    document.addEventListener("DOMContentLoaded", function () {
        function configurarBusca(inputId, resultsId) {
            const input = document.getElementById(inputId);
            const resultsDiv = document.getElementById(resultsId);

            if (!input || !resultsDiv) return;

            input.addEventListener("input", function () {
                const query = input.value.trim().toLowerCase();
                resultsDiv.innerHTML = "";

                if (query.length === 0) {
                    resultsDiv.style.display = "none";
                    return;
                }

                const resultados = chamadosSearch.filter(rel =>
                    rel.titulo && rel.titulo.toLowerCase().includes(query)
                );

                if (resultados.length === 0) {
                    resultsDiv.innerHTML = `<div class="text-muted small">Nenhum resultado encontrado</div>`;
                } else {
                    resultados.forEach(rel => {
                        const item = document.createElement("a");
                        item.href = `/relatorios/${rel.id}`;
                        item.className = "dropdown-item d-flex align-items-center";
                        item.innerHTML = `
                            <div class="font-weight-bold">
                                <div class="text-truncate">${rel.titulo}</div>
                                <div class="small text-gray-500">${rel.status} · ${rel.data}</div>
                            </div>
                        `;
                        resultsDiv.appendChild(item);
                    });
                }

                resultsDiv.style.display = "block";
            });
        }

        // Aplica para mobile e desktop
        configurarBusca("searchInput", "searchResults");
        configurarBusca("searchInputDesktop", "searchResultsDesktop");
    });



document.addEventListener("DOMContentLoaded", function () {
    const relogioSpan = document.getElementById("relogio");
    if (!relogioSpan) return;

    const diasSemana = ["Domingo", "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado"];
    const meses = ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                   "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"];

    const dataInicialTexto = relogioSpan.getAttribute("data-inicial")?.trim();
    let dataAtual = new Date();

    if (dataInicialTexto && dataInicialTexto.includes(" ")) {
        const [data, hora] = dataInicialTexto.split(" ");
        const [dia, mes, ano] = data.split("/").map(num => parseInt(num));
        const [h, m, s] = hora.split(":").map(num => parseInt(num));
        
        if (!isNaN(dia) && !isNaN(mes) && !isNaN(ano)) {
            dataAtual = new Date(ano, mes - 1, dia, h || 0, m || 0, s || 0);
        }
    }
    function atualizarRelogio() {
        const diaSemana = diasSemana[dataAtual.getDay()];
        const mes = meses[dataAtual.getMonth()];
        const dia = dataAtual.getDate();
        const ano = dataAtual.getFullYear();

        const horas = String(dataAtual.getHours()).padStart(2, '0');
        const minutos = String(dataAtual.getMinutes()).padStart(2, '0');
        const segundos = String(dataAtual.getSeconds()).padStart(2, '0');

        const textoFinal = `${diaSemana} · ${mes} ${dia}, ${ano} · ${horas}:${minutos}:${segundos}`;

        relogioSpan.textContent = textoFinal;
        dataAtual.setSeconds(dataAtual.getSeconds() + 1);
    }

    atualizarRelogio();
    setInterval(atualizarRelogio, 1000);
});




(function ($) {
    "use strict";


    /*==================================================================
    [ Focus input ]*/
    $('.input100').each(function(){
        $(this).on('blur', function(){
            if($(this).val().trim() != "") {
                $(this).addClass('has-val');
            }
            else {
                $(this).removeClass('has-val');
            }
        })    
    })
  
  
    /*==================================================================
    [ Validate ]*/
    var input = $('.validate-input .input100');

    $('.validate-form').on('submit',function(){
        var check = true;

        for(var i=0; i<input.length; i++) {
            if(validate(input[i]) == false){
                showValidate(input[i]);
                check=false;
            }
        }

        return check;
    });


    $('.validate-form .input100').each(function(){
        $(this).focus(function(){
           hideValidate(this);
        });
    });

    function validate (input) {
        if($(input).attr('type') == 'email' || $(input).attr('name') == 'email') {
            if($(input).val().trim().match(/^([a-zA-Z0-9_\-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([a-zA-Z0-9\-]+\.)+))([a-zA-Z]{1,5}|[0-9]{1,3})(\]?)$/) == null) {
                return false;
            }
        }
        else {
            if($(input).val().trim() == ''){
                return false;
            }
        }
    }

    function showValidate(input) {
        var thisAlert = $(input).parent();

        $(thisAlert).addClass('alert-validate');
    }

    function hideValidate(input) {
        var thisAlert = $(input).parent();

        $(thisAlert).removeClass('alert-validate');
    }
    
    /*==================================================================
    [ Show pass ]*/
    var showPass = 0;
    $('.btn-show-pass').on('click', function(){
        if(showPass == 0) {
            $(this).next('input').attr('type','text');
            $(this).addClass('active');
            showPass = 1;
        }
        else {
            $(this).next('input').attr('type','password');
            $(this).removeClass('active');
            showPass = 0;
        }
        
    });


})(jQuery);