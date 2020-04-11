function dropLangbar(){
    var langbar = $("#langbar").html();
    if(langbar == "繁體中文"){
        $("#lang").html("简体中文");
    }else if(langbar == "简体中文"){
        $("#lang").html("繁體中文");
    }
}
function exchangeLanguage() {
    var langbar = $("#langbar").html();
    if(langbar == "繁體中文"){
        $.get("language?lang=zh_CN",function () {
            window.location.reload();
        });
    }else if(langbar == "简体中文"){
        $.get("language?lang=zh_TW",function () {
            window.location.reload();
        });
    }
}

function checkLogin(){
    var email = $("#email").val();
    if(email.length<1){
        $("#login_msg").css("color","red");
        $("#login_msg").html("请您输入邮箱地址");
    }else if($("#password").val()<1){
        $("#login_msg").css("color","red");
        $("#login_msg").html("请您输入密码");
    }else {
        $.ajax({
            type: "post",
            dataType: "text",
            url: "login",
            data: $('#login_form').serialize(),
            success: function (result) {
                if(result == 1){
                    window.location.href = 'main';
                }else{
                    $("#login_msg").css("color","red");
                    $("#login_msg").html("用户名或密码有误，请重新输入");
                }
            },
            error: function(){
                $("#login_msg").css("color","red");
                $("#login_msg").html("error:网络错误");
            }
        });
    }
}