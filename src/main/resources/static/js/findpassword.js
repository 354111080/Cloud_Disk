function checkEmailExist() {
    $.get("checkEmailExist",{"email":$("#email").val()},function (result) {
        if (result == "1"){
            $("#email").css("display","none");
            $("#nextInput").css("display","inline");
            $('#myModal').modal();
            $("#tipButton").attr("onclick","submitPassword()");
            $("#tipButton").html("Submit");
        }else {
            alert("此邮箱尚未注册！");
        }
    },"text");
}

function submitPassword() {
    var reg = /^[^\u4e00-\u9fa5]+$/;
    if($("#verifyCode").val()!=null && reg.test($("#newPass").val())){
        $.ajax({
            url: "changeForgetPassword",
            data: $("#nextInput").serialize(),
            type: "post",
            success: function (result) {
                if(result == "1"){
                    alert("密码重置成功！");
                    window.location.href = '/';
                }else if(result == "-1"){
                    $("#dialog_tips").css("color","red");
                    $("#dialog_tips").html("验证码输入错误！");
                    $('#myModal').modal();
                } else {
                    $("#dialog_tips").css("color","red");
                    $("#dialog_tips").html("修改失败！");
                    $('#myModal').modal();
                }
            },
            error:function () {
                alert("error!");
            }
        });
    }else {
        $("#dialog_tips").css("color","red");
        $("#dialog_tips").html("您的输入信息不符合要求，请重新填写！");
        $('#myModal').modal();
    }

}