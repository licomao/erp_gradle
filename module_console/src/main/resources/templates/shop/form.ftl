<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>

<@main.frame>

<meta http-equiv="Windows-Target" contect="_top">
<script src="/javascripts/jquery-ui-1.9.2.min.js" type="text/javascript"></script>
<script src="/javascripts/cndate.js" type="text/javascript"></script>
<script src="/javascripts/jquery.ui.widget.js" type="text/javascript"></script>
<script src="/javascripts/jquery.iframe-transport.js" type="text/javascript"></script>
<script src="/javascripts/jquery.fileupload.js" type="text/javascript"></script>
<script src="http://api.map.baidu.com/api?v=2.0&ak=nw1UmSkHYuT3amwlrdznQhEn" type="text/javascript"></script>

<script type="text/javascript">
    $(function () {
        $('#collapseShop').collapse('show');
        $("#shopCode").blur(function(){
            checkShopCode();
        });
        fileUp();
        initImg();
        initShopType();
        initMap();
        init();
    });

    function initMap(){
        var imark = 0;
        var shleg = 121.48115; //上海经度  默认值
        var shlat = 31.236681; //上海纬度  默认值
        // 百度地图API功能
        var map = new BMap.Map("allmap");  // 创建Map实例
        map.addControl(new BMap.NavigationControl());   //放缩条
        map.addControl(new BMap.OverviewMapControl()); //小地图
        map.addControl(new BMap.MapTypeControl());   //地图类型
        map.setCurrentCity("上海");   // 仅当设置城市信息时，MapTypeControl的切换功能才能可用

        var point = new BMap.Point(shleg, shlat);  //暂时给默认值
        var marker = new BMap.Marker(point);        // 创建标注
        marker.addEventListener("click", function(){
        //  点击红标时的方法预留
        });
        marker.enableDragging();
        marker.addEventListener("dragend", function(e){
            $("#latitude").val(e.point.lat);
            $("#longitude").val(e.point.lng);
        })

        var lat = $("#latitude").val();
        var lng = $("#longitude").val();
        if(lng > 73){
            point = new BMap.Point(lng, lat);
            marker = new BMap.Marker(point);        // 创建标注
            map.addOverlay(marker);                     // 将标注添加到地图中
            imark = 1;
            map.centerAndZoom(new BMap.Point(lng, lat), 11); //默认居中点 坐标
        }else{
            map.centerAndZoom(new BMap.Point(shleg, shlat), 11); //默认居中点 上海 坐标
        }

        map.addEventListener("click", function(e){
            $("#latitude").val(e.point.lat);
            $("#longitude").val(e.point.lng);
            if(imark == 1){
                map.removeOverlay(marker);
                imark = 0;
            }else{
                point = new BMap.Point(e.point.lng, e.point.lat);
                marker = new BMap.Marker(point);        // 创建标注
                marker.addEventListener("click", function(){
                //  点击红标时的方法预留
                });
                marker.enableDragging();
                marker.addEventListener("dragend", function(e){
                    //获取当前经纬度;
                    $("#latitude").val(e.point.lat);
                    $("#longitude").val(e.point.lng);
                })
                map.addOverlay(marker);  // 将标注添加到地图中
                imark = 1;
            }
        });
    }


    function initImg(){
        var dbimgurl = $("#imageUrl").val();
        if(dbimgurl != null || dbimgurl.trim() != ''){
            $("#shopImg").attr("src",dbimgurl);
        }
    }

    function checkShopCode(){
        var murl = "/shop/isCodeRepeat?shopcode=";
        var code = $("#shopCode").val();
        var id = $("#id").val();
        if(code == null || code.trim() == ''){
            return;
        }
        if(id == null || id.trim() == ''){
            id = 0;
        }
        murl = murl + code + "&id=" + id;
        $.ajax({
            url:murl,
            dataType:"json", //返回的数据类型,text 或者 json数据，建议为json
            type:"post", //传参方式，get 或post
            success: function(ret) { //若Ajax处理成功后的回调函数，text是返回的页面信息
                if(ret.data != 1){
                    alert("该门店编码已经存在，请重新输入");
                    $("#shopCode").val("");
                }
            }
        });
    }

    function getUrl(key){
        var url = "";
        if(key == ''){
            url = window.location.href;
            url = url.substr(0,url.lastIndexOf('/'));
            url = url.substr(0,url.lastIndexOf('/'));
            url += "/html/result.html?%s";
        }else{
            url = "http://test.zhaitech.com/images" + key;
        }
        return url;
    }

    function fileUp(){
        var url = getUrl('');
        $(function () {
            $('#fileupload').fileupload({
                dataType: 'iframe',
                redirect: url,
                forceIframeTransport: true,
                done: function (e, data) {
                    var imgUrl = getUrl(data.result[0].body.innerText);
                    $("#shopImg").attr("src",imgUrl);
                    $("#imageUrl").val(imgUrl);
                    $("#mimageUrl").val($("#imageUrl").val());
                },
                fail: function (e, data) {
                    alert("图片上传失败");
                }
            });
        });
    }

    function initShopType(){
        var dbtype = $("#shopType").val();
        if(dbtype !=null && dbtype.trim() != "" && dbtype == "1"){
            $("#sshopType").val("1");
        }
        $("#sshopType").change(function () {
            $("#shopType").val($("#sshopType").val());
        });
    }

    function init(){
        $("#orgName").val($("[id='organization.name']").val());
        $("#mimageUrl").val($("#imageUrl").val());
    }

</script>


<div class="row" style="margin-top: -170px;">
    <div class="col-md-5 col-md-offset-2">
        <div id="actions" class="form-action">
            <form class="" action='<@spring.url relativeUrl = "/shop/new"/>' method="post">
                <@form.textInput "shop.id" "" "hidden"/>
                <@form.textInput "shop.ver" "" "hidden"/>
                <@form.textInput "shop.organization.id" "" "hidden"/>
                <@form.textInput "shop.imageUrl" "" "hidden" />
                <@form.textInput "shop.organization.name" "" "hidden"/>
                <#if pageContent?? && pageContent == "更新">
                    <legend>更新门店</legend>
                <#else>
                    <legend>新增门店</legend>
                </#if>
                <span style=" color: red">请完整填写如下信息</span>

                <div class="row">
                    <div class="col-md-5">
                        <@form.textInput "shop.shopCode" "class='form-control'" "text" "门店代号" true/>
                    </div>
                    <div class="col-md-5 col-md-offset-2">
                        <@form.textInput "shop.name" "class='form-control' style=''" "text" "门店名称" true/>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-5">
                        <@form.textInput "shop.phone" "class='form-control'" "text" "联系电话" true/>
                    </div>
                    <div class="col-md-5 col-md-offset-2">
                         所属组织
                         <input type="text" id="orgName" disabled="disabled" style="width: 100%">
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-5">
                        <@form.textInput "shop.openingHours"  "class='form-control'" "text" "营业时间" true/>
                    </div>
                    <div class="col-md-5 col-md-offset-2">
                        <label style="margin-top: 30px;margin-left: -120px;"> 例：8:00-18:00</label>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-5">
                        <@form.textInput "shop.shopType" "class='form-control'" "hidden" "店类型" true/>
                        <select id="sshopType">
                            <option value ="0">总店</option>
                            <option value ="1">分店</option>
                        </select>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-5">
                        <br/>
                        <@form.textInput "shop.address" "class='form-control' style='width:615px'"  "text" "门店地址" true/>
                    </div>
                </div>
                <div class="row" style="margin-left: 2px;">
                    地图定位
                    <div id="allmap" style="width: 600px;height: 200px; border:1px solid #000;" >
                        <#--<img src="/stylesheets/images/staticMap.png"  alt="定位"  id="staticMap" style="width: 100%;height: 100%;"/>-->
                    </div>
                    <div class="col-md-5">
                        <@form.textInput "shop.longitude" "class='form-control'" "text" "经度" true/>
                    </div>
                    <div class="col-md-5 col-md-offset-2">
                        <@form.textInput "shop.latitude" "class='form-control'" "text" "纬度" true/>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-5">
                        <@form.textArea "shop.description" "class='form-control' style='width:615px'"  "门店描述" />
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-5">
                        <@form.textInput "shop.promotionTag" "class='form-control' style='width:615px'"  "text" "特色标签" />
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-5">

                        门店照片地址
                        <input type="text" id="mimageUrl" disabled="disabled" style="width: 100%" /><br/>
                        <span>上传营业执照</span>
                        <input id="fileupload" type="file"   name="fileUpload" data-url="http://test.zhaitech.com/upload/"   multiple>
                    </div>
                    <div class="col-md-5 col-md-offset-2">
                         预览
                        <div style="width: 100px;height: 100px; border:1px solid #000;" >
                            <img src=""  alt="门店照片预览"  id="shopImg" style="width: 100%;height: 100%;"/>
                        </div>
                    </div>
                </div>

                <#if pageContent?? && pageContent == "更新">
                    <input type="submit" id="searchsubmit" value="更新" class="btn btn-primary ">
                <#else>
                    <input type="submit" id="searchsubmit" value="新增" class="btn btn-primary ">
                </#if>
            </form>
        </div>
    </div>
</div>
</@main.frame>

</#escape>