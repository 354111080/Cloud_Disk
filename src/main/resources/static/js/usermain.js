//获取用户信息
$.ajax({
    type: "get",
    dataType: "json",
    url:"getUserInfo",
    success: function (result) {
        if(result.profile_pic != null && result.profile_pic != ""){
            $("#userBarProfilePic").removeAttr("src");
            $("#userBarProfilePic").attr("src",result.profile_pic+'?t='+new Date());
            $("#profilePic").removeAttr("src");
            $("#profilePic").attr("src",result.profile_pic+'?t='+new Date());
        }else {
            $("#userBarProfilePic").attr("src","/img/avatar/avatar-1.jpeg");
            $("#profilePic").attr("src","/img/avatar/avatar-1.jpeg");
        }
        $("#username").html(result.username);
        $("#useremail").html(result.email);
        $("#changeUserName").val(result.username);
    }
});

//获取用户路径信息
function getFolders(father_id){
    $.ajax({
        type: "get",
        dataType : "text",
        url: "getFolders",
        data: {"father_id":father_id},
        cache: false,
        success: function (result) {
            if(result != "0"){
                $("#folderZone").html(result);
            }
        },
        error: function () {
            alert("error");
        }
    });
}

getFolders("");

//获取当前用户的文件信息
function getFiles(){
    $.ajax({
        type: "get",
        dataType: "text",
        url:"getFiles",
        cache: false,
        success: function (result) {
            if(result != ""){
                $("#isFileEmpty").css("display","none");
                $("#fileZone").html(result);
            }else{
                $("#fileZone").html("");
                $("#isFileEmpty").css({"display":"","text-align":"center"});
            }
        }
    });
}

//进入主页直接调用获取文件
getFiles();


function getUsage() {
    $.get("getUsage",function (result) {
        if(result != "-1"){
            $("#usageProgress").css("width",result*100/1073741824+"%");
            $("#usageNum").html(dealFileSize(result));
        }else {
            alert("Error");
        }
    })
}

getUsage();

//连接文件上传
function upload(){
    $("#btn_upload").attr("enctype","multipart/form-data");
    $("#btn_upload").attr("multiple","multiple");
    $("#btn_upload").click();
}

function dealFileSize(size){
    if(size>=1024 && size<1048576){
        return (size/1024).toFixed(1)+'KB';
    }else if(size > 1048576){
        return (size/1048576).toFixed(1)+'MB';
    }else {
        return size+'B';
    }
}

function dealFileName(name){
    if(name.length>10){
        return name.substring(0,10)+"..";
    }
    return name;
}

var formData = new FormData();//建立上传队列

var fileNum = 0;//上传文件编号

var fileCount = 0;//上传文件数量

var executeUpload = true;

var createUploadPanel = true;

//建立信息
function createUploadInfo(){
    if(createUploadPanel){
        showUpload();
    }
    if($("#translucent_small").attr("title") == "还原"){//如果上传文件时传输列表收起，则触发弹出
        $("#translucent_small").trigger("click");
    }
    var obj = $("#btn_upload")[0];
    var length = obj.files.length;
    for (var i = 0; i < length; i++) {
        if(obj.files[i].size<1){
            alert("Error,The file is empty!");
            continue;
        }
        var name = dealFileName(obj.files[i].name);
        var size = dealFileSize(obj.files[i].size);
        fileNum++;
        var transpaortElement='<tr id="">\t\n' +
            '\t\t\t\t\t\t<td>'+name+'</td>\n' +
            '\t\t\t\t\t\t<td>'+size+'</td>\n' +
            '\t\t\t\t\t\t<td>\n' +
            '\t\t\t\t\t\t\t<div class="progress">\n' +
            '\t\t\t\t\t\t\t  <div id="'+'progress'+fileNum+'" class="progress-bar bg-info" ></div>\n' +
            '\t\t\t\t\t\t\t</div>\n' +
            '\t\t\t\t\t\t</td>\n' +
            '\t\t\t\t\t</tr>\t'
        $("#transport_content").append(transpaortElement);
        formData.append(fileNum+"",$('#btn_upload')[0].files[i]);
        ++fileCount;
    }
    if(executeUpload && fileCount>0){
        setInterval(adjustUpload,100);
    }

}

var num = 1;

var executeNext = true;//锁住submitFile(排队上传)

//调度submitFile
function adjustUpload() {
    executeUpload = false;

    if(executeNext && fileCount>0){
        executeNext = false;
        submitFile(num);
    }
    if(fileCount == 0){
        executeUpload = true;
        clearInterval(adjustUpload);
        $('#btn_upload').val("");
    }
}

//上传文件到服务器
function submitFile(fileNumParam) {
    var formDataUpload = new FormData();
    formDataUpload.append("fileQue",formData.get(fileNumParam));
    $.ajax({
        type: "post",
        dataType: "text",
        url: "uploadFile",
        data: formDataUpload,
        processData: false,			//对数据不做处理
        cache:false,                //上传文件不需要缓存
        contentType: false,
        success: function (result) {
            if(result == 1){
                getFiles();
                getUsage();
            }else if(result == -1){
                alert("上传失败！");
            }else if(result == 0){
                alert("请先登录！")
            }else if(result == -2){
                alert("容量不足！");
            }
        },
        error: function () {
            alert("文件上传失败，网络错误！");
            clearInterval(getUploadProgress);
            ++num;
            --fileCount;
            executeNext = true;
        }
    });

    var getUploadProgress = setInterval(getUploadProgress,100);

    function getUploadProgress() {
        $.ajax({
            type: "get",
            dataType: "text",
            url: "getUploadProgress",
            success: function (result) {
                $("#progress"+fileNumParam).css("width",result+"%");
                if(result == 100){
                    clearInterval(getUploadProgress);
                    ++num;
                    --fileCount;
                    executeNext = true;
                }
            },
            error: function () {
                alert("网络错误！");
                clearInterval(getUploadProgress);
                ++num;
                --fileCount;
                executeNext = true;
            }
        });
    }

}

//上传列表
function showUpload(){
    $("body").translucent({
        titleGroundColor: "#5396BA",
        backgroundColor: "#ffffff",
        titleFontColor: "#ffffff",
        titleFontSize: 14,
        opacity: 1,
        zIndex: 100,
    });
    createUploadPanel=false;
}



//下载文件
function download(fileId) {
    window.location.href="downLoadFile?fileId="+fileId.value;
}

//删除文件
function deleteFile(fileId) {
    $.ajax({
        type: "get",
        dataType: "text",
        data:{"fileId":fileId.value},
        url:"deleteFile",
        success: function (result) {
            if(result == "1"){
                if(typeChecked != "allFileOp"){
                    getFilesByType(typeChecked);
                    getUsage();
                }else {
                    getFiles();
                    getUsage();
                }
            }else{
                alert("File deletion failed!");
            }
        },
        error: function () {
            alert("error!");
        }
    });
}

function enterDir(folderId) {
    $.ajax({
        type: "get",
        dataType: "json",
        data:{"folderId":folderId.value},
        url:"enterDir",
        success: function (result) {
            if(result != null){
                getFolders(folderId.value);//获取路径信息
                getFiles();//获取文件
                var navId = "nav"+folderId.value;
                var navHTML = '<li id='+navId+'> \n' +
                    '               <a href="javascript:void(0);" style="cursor: pointer;" id="'+folderId.value+'" onclick="skipDir(this)">'+result.name+'</a> <span class="divider">/</span>\n' +
                    '          </li>'
                $("#dirNav").append(navHTML);
            }else {
                alert("操作失败");
            }
        },
        error: function () {
            alert("error!");
        }
    });
}

//路径导航栏点击跳转路径
function skipDir(dir) {
    $.get("skipDir",{"folderId":dir.id},function (result) {
        if(result == 1){
            if(dir.id == "root"){
                getFolders("");//获取路径信息
                getFiles();//获取文件
                $("#navRoot").nextAll().remove();//删除当前路径后的所有子路径
            }else{
                getFolders(dir.id);//获取路径信息
                getFiles();//获取文件
                $("#nav"+dir.id).nextAll().remove();//删除当前路径后的所有子路径
            }
        }
    },"text");
}

//新建文件加
function createFolder() {
    var folderUnit = "<tr class='file-item folder' id='newFolder'>";
    folderUnit+="<td>";
    folderUnit+="<i style='font-size: 20px;color: #FFB90F;' class='icon ion-android-folder'>&nbsp;";
    folderUnit+="<input type='text' maxlength='12' onblur='submitFolder()' id='newFolderName'/>";
    folderUnit += "</i>";
    folderUnit +="<td>-";
    folderUnit +="</td>";
    folderUnit +="<td>-";
    folderUnit +="</td>";
    folderUnit +="<td>-";
    folderUnit +="</td>";
    folderUnit +="</td>";
    folderUnit +="<td>-";
    folderUnit +="</td>";
    folderUnit +="</tr>";
    $("#folderZone").append(folderUnit);
    $("#newFolderName").focus();
}

function submitFolder() {
    if($("#newFolderName").val() == ""){
        $("#newFolder").remove();
    }else {
        $.ajax({
            type: "get",
            url: "createFolder",
            data: {"folderName":$("#newFolderName").val()},
            dataType: "text",
            success: function (result) {
                if(result != ""){
                    $("#newFolder").html(result);
                    $("#newFolder").removeAttr("id");
                }else{
                    alert("建立文件夹失败！");
                    $("#newFolder").remove();
                }
            },
            error:function () {
                alert("error");
                $("#newFolder").remove();
            }
        });
    }
}


function deleteDir(dir) {
    $(dir).attr("disabled","disabled");
    $.ajax({
        type:"get",
        url: "deleteDir",
        data: {"folderId":dir.value},
        dataType: "text",
        success: function (result) {
            if(result != "-1"){
                getFolders(result);
                getUsage();
            }else{
                alert("操作失败");
            }
        },
        error:function () {
            $(dir).removeAttr("disabled");
            alert("error");
        }
    });
}

var typeChecked = "allFileOp";

function getFilesByType(type) {
    $.ajax({
        url: "getFilesByType",
        data: {"type":type},
        type: "get",
        dataType: "text",
        success: function (result) {
            dealResult(result);
        },
        error: function () {
            alert("error");
        }
    });
    $("#fileUploadButton").css("display","none");
    $("#createNewFolderButton").css("display","none");
    $("#dir_nav").css("display","none");
    switch (type) {
        case "image":
            changeTypeBar(type);
            $("#resultTitle").html("Image");
            break;
        case "document":
            changeTypeBar(type);
            $("#resultTitle").html("Document");
            break;
        case "video":
            changeTypeBar(type);
            $("#resultTitle").html("Video");
            break;
        case "audio":
            changeTypeBar(type);
            $("#resultTitle").html("Audio");
            break;
        case "other":
            changeTypeBar(type);
            $("#resultTitle").html("Other");
            break;
    }

}

function changeTypeBar(option) {
    if(typeChecked != option){
        $("#"+typeChecked).removeAttr("class");
        $("#"+option).attr("class","active");
        typeChecked = option;
    }
}

function searchFile(){
    var key = $("#searchKey").val();
    if(key != null){
        $.ajax({
            type: "get",
            url: "searchFileByKey",
            data: {"key":key},
            dataType: "text",
            success: function (result) {
                dealResult(result);
            },
            error:function () {
                alert("error");
            }
        });
        $("#fileUploadButton").css("display","none");
        $("#createNewFolderButton").css("display","none");
        $("#dir_nav").css("display","inline");
        $("#dir_nav").html("Search by:"+key);
        $("#resultTitle").html("Search Results");
        $("#"+typeChecked).removeAttr("class");
    }
}

function dealResult(result) {
    $("#folderZone").html("");
    $("#fileZone").html(result);
    if(result != ""){
        $("#isFileEmpty").css("display","none");
    }else {
        $("#isFileEmpty").css({"display":"","text-align":"center"});
        $("#isFileEmpty").html("The result is empty!");
    }
}

function preview(file) {
    window.open(file.value);
}

function choosePic() {
    $("#choosePic").click();
}

function previewPic() {
    // 获取上传文件对象
    var file = $("#choosePic")[0].files[0];
    if(file.size>2097152){
        alert("The picture's size can not over 2MB!");
    }else {
        // 读取文件URL
        var reader = new FileReader();
        reader.readAsDataURL(file);
        // 阅读文件完成后触发的事件
        reader.onload = function () {
            // 读取的URL结果：this.result
            $("#profilePic").attr("src", this.result);
        }
        $("#saveProfilePic").removeAttr("disabled");
    }
}


function submitProfilePic(){
    $("#saveProfilePic").attr("disabled","true");
    var formData = new FormData();
    formData.append("pic",$("#choosePic")[0].files[0]);
    $.ajax({
        url: "uploadProfilePic",
        type: "post",
        data: formData,
        dataType: "text",
        processData: false,			//对数据不做处理
        cache:false,                //上传文件不需要缓存
        contentType: false,
        success: function (result) {
            if(result != "0"){
                $("#userBarProfilePic").removeAttr("src");
                $("#userBarProfilePic").attr("src",result);
            }else if(result == "-1"){
                alert("The picture's size can not over 2MB!")
            } else {
                alert("Upload Failed!");
                $("#saveProfilePic").removeAttr("disabled");
            }
        },
        error:function () {
            alert("error!");
            $("#saveProfilePic").removeAttr("disabled");
        }
    });
}

function modifyUsername() {
    if($("#changeUserName").val() == ""){
        $("#modifyUsernameButton").attr("disabled","true");
    }else{
        $("#modifyUsernameButton").removeAttr("disabled");
    }
}

function saveUserName() {
    $.ajax({
        type: "get",
        url: "modifyUsername",
        data: {"username":$("#changeUserName").val()},
        dataType: "text",
        success: function (result) {
            if(result == "1"){
                getUsername();
                $("#modifyUsernameButton").attr("disabled","true");
            }else if(result == 0){
                $("#modifyUsernameButton").attr("disabled","true");
            }else {
                alert("Modify username failed!");
            }
        },
        error: function () {
            alert("Error!");
        }
    });
}

function getUsername() {
    $.ajax({
        type: "get",
        dataType: "json",
        url:"getUserInfo",
        success: function (result) {
            $("#username").html(result.username);
        }
    });
}

function submitNewPassword() {
    var newPassword = $("#newPass").val();
    var confirmPassword = $("#confirmPass").val();
    var reg = /^[^\u4e00-\u9fa5]+$/;
    if($("#originalPass").val() == null || newPassword == null || $("#confirmPass").val() == null){
        $("#passwordInfo").html("请输入完整信息");
    }else if (!reg.test(newPassword) || newPassword.length<8 || newPassword.length>12){
        $("#passwordInfo").html("新密码不符合规范");
    }else if(confirmPassword != newPassword){
        $("#passwordInfo").html("两次密码输入不一致");
    }else{
        $.ajax({
            url: "modifyPassword",
            type: "post",
            data: $("#modifyPasswordForm").serialize(),
            dataType: "text",
            success: function (result) {
                if(result == "1"){
                    window.location.href = 'success';
                }else if(result == "0"){
                    $("#passwordInfo").html("旧密码输入错误");
                }else if(result == "2"){
                    $("#passwordInfo").html("新密码与旧密码一致！");
                }else{
                    $("#passwordInfo").html("修改失败！");
                }
            },
            error: function () {
                alert("Error!");
            }
        });

    }
}

function dropLangbar(){
    var langbar = $("#langbar").html();
    if(langbar == "繁體中文"){
        $("#choose_lang").attr("href","main?lang=zh_CN");
        $("#lang").html("简体中文");
    }else if(langbar == "简体中文"){
        $("#choose_lang").attr("href","main?lang=zh_TW");
        $("#lang").html("繁體中文");
    }
}

function dropLangbarForProfile(){
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



