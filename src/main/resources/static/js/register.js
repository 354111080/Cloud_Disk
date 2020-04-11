function checkEmail() {
    var email = $("#email").val();
    var reg = /^[a-z0-9A-Z]+[- | a-z0-9A-Z . _]{2,31}$/;
    if(!reg.test(email)){
        $("#email_text").css("display","inline");
        $("#email_text").css("color","red");
    }else {
        $("#email_text").css("display","none");
    }
}

function checkPassword(){
    var password = $("#password").val();
    var reg = /^[^\u4e00-\u9fa5]+$/;
    if(!reg.test(password) || password.length<8 || password.length>12){
        $("#password_text").css("display","inline");
        $("#password_text").css("color","red");
    }else {
        $("#password_text").css("display","none");
    }
}

function exchangePwdStatus(){
    var passwordStatus = $("#password").attr("type");
    if(passwordStatus == "password"){
        $("#password").attr("type","text");
        $("#control_password").html("Hide");
    }else if(passwordStatus == "text"){
        $("#password").attr("type","password");
        $("#control_password").html("Show");
    }
}

function checkUsername(){
    var username = $("#username").val();
    if(username.length<1 || username.length>14){
        $("#username_text").css("color","red");
        $("#username_text").css("display","inline");
    }else {
        $("#username_text").css("display","none");
    }
}

function resetSendVerifyCode(){
    $("#getCodeButton").attr("onclick","sendVerifyCode()");
    $("#getCodeButton").removeAttr('disabled');
    $("#getCodeText").css("display","inline");
    $("#loadImg").css("display","none");
}

//秒数递减
function subSecond(){
    var count = $("#getCodeText").html();
    if(count > 0){
        $("#getCodeText").html(--count);
    }else{
        $("#getCodeText").html("Send Again");
        $("#getCodeButton").removeAttr('disabled');
        $("#getCodeButton").attr("onclick","sendVerifyCode()");
        clearInterval(countSub);
    }

}

//倒计时
var countSub;
function countDown(){
    var count = 60;
    $("#getCodeText").html(count);
    $("#getCodeText").css("display","inline");
    $("#loadImg").css("display","none");
    countSub = setInterval("subSecond()",1000);
}

function sendVerifyCode() {
    var prefix = $("#email").val();
    var suffix = $("#suffix option:selected").val();
    var reg = /^[a-z0-9A-Z]+[- | a-z0-9A-Z . _]{2,31}@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
    if(!reg.test(prefix+suffix)){
        $("#email_text").css("display","inline");
        $("#email_text").css("color","red");
        $("#dialog_tips").css("color","red");
        $("#dialog_tips").html("邮箱地址不符合要求");
        $('#myModal').modal();
    }else{
        $("#getCodeButton").attr("onclick","");
        $("#getCodeButton").attr("disabled","true");
        $("#getCodeText").css("display","none");
        $("#loadImg").css("display","inline");
        var email = prefix+suffix;
        $.get("checkEmail?email="+email,function(data,status){
            if(data == 0){
                $("#email").attr("disabled","true");
                $("#suffix").attr("disabled","true");
                $("#dialog_tips").css("color","blue");
                $("#dialog_tips").html("邮件已发送，有效时间为30分钟，请注意查收！");
                $('#myModal').modal();
                countDown();
            }else if(data == 1){
                $("#dialog_tips").css("color","red");
                $("#dialog_tips").html("该邮箱地址已被注册！");
                $('#myModal').modal();
                resetSendVerifyCode();//重置发送验证码按钮
            }else if(data == -1 || status != "success"){
                $("#dialog_tips").css("color","red");
                $("#dialog_tips").html("发送失败！");
                $('#myModal').modal();
                resetSendVerifyCode();//重置发送验证码按钮
            }else if(data == 2){
                $("#dialog_tips").css("color","red");
                $("#dialog_tips").html("发送过于频繁，请稍后再试！");
                $('#myModal').modal();
                resetSendVerifyCode();//重置发送验证码按钮
            }
        });
    }
}

function submitData(){
    var flag = true;

    //用户名
    var username = $("#username").val();
    if(username.length<1 || username.length>14){
        $("#username_text").css("color","red");
        $("#username_text").css("display","inline");
        flag = false;
    }else {
        $("#username_text").css("display","none");
    }

    //邮箱
    var email = $("#email").val();
    var reg = /^[a-z0-9A-Z]+[- | a-z0-9A-Z . _]{2,31}$/;
    if(!reg.test(email)){
        $("#email_text").css("display","inline");
        $("#email_text").css("color","red");
        flag = false;
    }else {
        $("#email_text").css("display","none");
    }

    //密码
    var password = $("#password").val();
    var reg = /^[^\u4e00-\u9fa5]+$/;
    if(!reg.test(password) || password.length<8 || password.length>12){
        $("#password_text").css("display","inline");
        $("#password_text").css("color","red");
        flag = false;
    }else {
        $("#password_text").css("display","none");
    }

    //验证码
    var reg = /^\d{6}$/;
    if(!reg.test($("#verifyCode").val())){
        $("#dialog_tips").css("color","red");
        $("#dialog_tips").html("请输入合理的验证码！");
        $('#myModal').modal();
        flag = false;
    }

    if(flag){
        var postFlag = true;
        $.get("checkVerifyCode?verifyCode="+$("#verifyCode").val(),function (data,status){
            if(data == -1){
                $("#dialog_tips").css("color","red");
                $("#dialog_tips").html("请获取验证码！");
                $('#myModal').modal();
                postFlag = false;
            }else if(data == 0 ){
                $("#dialog_tips").css("color","red");
                $("#dialog_tips").html("验证码错误！");
                $('#myModal').modal();
                postFlag = false;
            }
        });
        if(postFlag){
            $.ajax({
                type: "post",
                dataType: "text",
                url: "signup",
                data: $('#register_form').serialize(),
                success: function (result) {
                    if(result == 2){
                        window.location.href = 'register_success';
                    } else if(result == -1){
                        $("#dialog_tips").css("color","red");
                        $("#dialog_tips").html("您的输入信息不符合要求，请重新填写！");
                        $('#myModal').modal();
                    }else if(result == 0){
                        $("#dialog_tips").css("color","red");
                        $("#dialog_tips").html("抱歉，注册失败！");
                        $('#myModal').modal();
                    }else if(result == -2){
                        $("#dialog_tips").css("color","red");
                        $("#dialog_tips").html("该邮箱已被注册！");
                        $('#myModal').modal();
                    }
                },
                error: function(){
                    $("#dialog_tips").css("color","red");
                    $("#dialog_tips").html("网络错误！");
                    $('#myModal').modal();
                }
            });
        }
    }
}