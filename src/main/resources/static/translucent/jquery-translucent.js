var transportInfo = '<table class="table table-hover table-condensed">\n' +
    '\t\t\t<thead>\n' +
    '\t\t\t<tr>\n' +
    '\t\t\t    <th>File</th>\n' +
    '\t\t\t    <th>Size</th>\n' +
    '\t\t\t    <th>Status</th>\n' +
    '\t\t\t</tr>\n' +
    '\t\t\t</thead>\n' +
    '\t\t\t<tbody id="transport_content">\n' +
    '\t\t\t\t\n' +
    '\t\t\t</tbody>\n' +
    '\t\t</table>';

(function($, window, document, undefined){
    var Translucent = function (elem,options) {
        this.elem = elem;
        this.$elem = $(elem);
        this.options = options;
        this.$win = $(window);
        this.$doc = $(document);
        this.docHeight = this.$doc.height();
    };
    Translucent.prototype = {
        defaults: {
            target:"translucentDefultId",
            width:500, //默认宽度
            height:500, //默认高度
            drag:false, //启动拖拽
            opacity:0.8, //透明度
            border:"1px solid #ddd",
            borderRadius:8,
            wallGlass:false, //启动毛玻璃效果
            backgroundColor:"rgb(225, 225, 225)",//默认背景色
            titleHeight:"40px",//title高度
            titleGroundColor:"#999",//title默认背景色
            shadow:true,//开启阴影
            positionTop:460,
            positionRight:180,
            titleText:"传输列表",
            titleFontSize:12,
            titleFontColor:"#000",
            titleFontFamily:"微软雅黑",
            textHtml:transportInfo,
            titleTextCenter:false,
            close:null,
            zIndex:10,
            //私有的属性
            _isScale:true,
            _isMax:true,
            _width:500
        },
        init:function () {
            this.config = $.extend({}, this.defaults, this.options);
            //this._width = this.config.width;
            this.drawInfoWindow();
            this.smallWindow();
        },
        drawInfoWindow:function () {
            var context = this;
            context.$elem.addClass("translucent-relative");
            var html = '';
                html += '<div id="'+context.config.target+'" class="translucent-container">';
                    html += '<div class="translucent-title"><span>'+context.config.titleText+'</span><div class="translucent-control"><img id="translucent_small" title="最小化" src="/translucent/icon/small.png"/></div></div>';
                    html += '<div class="translucent-content" style="height:460px;overflow-y: auto;">'+context.config.textHtml+'</div>';
                html += '</div>';
            if($(".translucent-container").length > 0){
                $(".translucent-container").remove();
                // context.config._isMax = false;
            }
            context.$elem.append(html);
            var translucentContainer = $(".translucent-container");
            var translucentTitle = $(".translucent-title");
            translucentContainer.css({
                width:context.config.width,
                height:context.config.height,
                backgroundColor:context.config.backgroundColor,
                top:context.config.positionTop,
                right:context.config.positionRight,
                opacity:context.config.opacity,
                border:context.config.border,
                borderRadius:context.config.borderRadius,
                zIndex:context.config.zIndex
            }).addClass("translucent-absolute");
            if(context.config.shadow){
                translucentContainer.addClass("translucent-shadow");
            }
            translucentTitle.css({
                height:context.config.titleHeight,
                backgroundColor:context.config.titleGroundColor,
                fontSize:context.config.titleFontSize,
                color:context.config.titleFontColor,
                fontFamily:context.config.titleFontFamily,
                borderTopLeftRadius:context.config.borderRadius,
                borderTopRightRadius:context.config.borderRadius
            });
            if(context.config.titleTextCenter){
                translucentTitle.addClass("translucent-center");
            }
            translucentTitle.find("span").css({
                lineHeight:context.config.titleHeight,
                marginLeft:10
            });
            var _titleHeight = translucentTitle.height();
            var _top = (_titleHeight-20)/2;
            $(".translucent-control").css({
                top:0,
                height:context.config.titleHeight,
                lineHeight:context.config.titleHeight
            });
        },
        smallWindow:function () {
            var context = this;
            var translucentContainer = $(".translucent-container");
            var translucentContent = $(".translucent-content");
            var translucentControl = $(".translucent-control");
            var translucentTitle = $(".translucent-title");
            translucentControl.bind("click",function (e) {
                e.preventDefault();  //阻止默认事件
                e.stopPropagation();    //阻止冒泡事件
            });
            context.defaults._isScale = true;
            $("#translucent_small").bind("click",function (e) {
                var top = $("body").height() - parseInt(context.config.titleHeight)-6;
                if(context.defaults._isScale){
                    translucentContainer.animate({
                        height:context.config.titleHeight,
                        top:925,
                        right:180,
                        width:"180px",
                        opacity:1,
                        borderTopRightRadius:0,
                        borderTopLeftRadius:0
                    });
                    translucentTitle.css({
                        borderTopRightRadius:0,
                        borderTopLeftRadius:0
                    });
                    translucentContent.hide();
                    $(this).attr("src","/translucent/icon/fangda.png");
                    $(this).attr("title","还原");
                    context.defaults._isScale = false;
                    context.defaults._isMax = true;
                }else{
                    translucentContainer.animate({
                        height:context.config.height,
                        right:context.config.positionRight,
                        top:context.config.positionTop,
                        width:context.config.width,
                        opacity:context.config.opacity,
                        borderTopRightRadius:context.config.borderRadius,
                        borderTopLeftRadius:context.config.borderRadius
                    });
                    translucentTitle.css({
                        borderTopRightRadius:context.config.borderRadius,
                        borderTopLeftRadius:context.config.borderRadius
                    });
                    translucentContent.show();
                    $(this).attr("src","/translucent/icon/small.png");
                    $(this).attr("title","最小化");
                    context.defaults._isScale = true;
                }
                e.preventDefault();  //阻止默认事件
                e.stopPropagation();    //阻止冒泡事件
            })
        },
        getPath:function () {
            var scripts = document.scripts;
            for(var i = 0;i < scripts.length;i ++){
                var item = scripts[i];
                var name = $(item).attr("src").split("/").reverse()[0];
                if(name === "jquery-translucent.js"){
                    var pathArr = $(item).attr("src").split("/");
                    pathArr.reverse();
                    var pathString = '';
                    for(var k = 0;k < pathArr.length;k ++){
                        var len = pathArr.length;
                        if(k !== len-1){
                            pathString += (pathArr[i] + "/");
                        }
                    }
                    return pathString;
                }
            }
        }
    };
    $.fn.translucent = function(options) {
        new Translucent(this, options).init();
        return this;
    };
    
})( jQuery, window , document );
