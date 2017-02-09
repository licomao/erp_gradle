<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>

    <@main.frame>

    <meta http-equiv="Windows-Target" contect="_top">
    <script src="/javascripts/jquery.ui.widget.js" type="text/javascript"></script>
    <script src="/javascripts/jquery.iframe-transport.js" type="text/javascript"></script>
    <script src="/javascripts/jquery.fileupload.js" type="text/javascript"></script>
    <script src="http://api.map.baidu.com/api?v=2.0&ak=nw1UmSkHYuT3amwlrdznQhEn" type="text/javascript"></script>

    <script type="text/javascript">
        $('#collapsePt').collapse('show');
        $(function(){
            $('.Wdate').datepicker();
            $('.Wdate').datepicker("option",$.datepicker.regional["zh-TW"]);

            //选择初始化推广类型
            <#if campaign.compaignType??>
                $("#compaignType" + ${campaign.compaignType}).attr("checked", "checked");
            </#if>

            <#if campaign.onBanner>
                $("#onBanner" ).attr("checked", "checked");
            </#if>

            <#if campaign.shop??>
                $("#showType1").attr("checked", "checked");
            <#else>
                $("#showType0").attr("checked", "checked");
            </#if>

            fileUp();
            initImg();
            changeCompaignType($("input[name='compaignType']:checked ").val());
            changeShowType($("input[name='showType']:checked ").val());

        });

        function subForm(){
            if (confirm("是否确认保存!")){
                var bannerImageUrl = $("#bannerImageUrl").val();
                if (bannerImageUrl == null || bannerImageUrl == "") {
                    alert("banner图片url必填");
                    return;
                }
                var summary = $("#summary").val();
                if (summary == null || summary == "") {
                    alert("文字摘要必填");
                    return;
                }
                var compaignType = $("input[name='compaignType']:checked ").val();
                if (compaignType == 2 && ($("#latitude").val()=="0" || $("#longitude").val()=="0")) {
                    alert("推广类型为中心时，经度纬度必填");
                    return;
                }
                var showType = $("input[name='showType']:checked ").val();
                if (showType == 0 && ($("#url").val()=="")) {
                    alert("展示内容为url时，展示内容的url必填");
                    return;
                }
                $("#fm").submit();
            }
        }

        function validate(obj){
            var reg = new RegExp("^[1-9]\d*|0$");
            if (!reg.test(obj.value)) {
                alert("请输入非负整数!");
                obj.focus();
                return;
            }
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
                        $("#bannerImg").attr("src",imgUrl);
                        $("#bannerImageUrl").val(imgUrl);
                        $("#mimageUrl").val($("#bannerImageUrl").val());
                    },
                    fail: function (e, data) {
                        alert("图片上传失败");
                    }
                });
            });
        }

        function initImg(){
            var dbimgurl = $("#bannerImageUrl").val();
            if(dbimgurl != null || dbimgurl.trim() != ''){
                $("#bannerImg").attr("src",dbimgurl);
            }
        }

        //推广类型change
        function changeCompaignType(compaignType) {
            var city = $("#city");
            if (compaignType == 0) {
                city.empty();
                $("#longitude").val("0");
                $("#latitude").val("0");
                $("#cityDiv").hide();
                $("#mapDiv").hide();
            } else if  (compaignType == 1) {
                $("#longitude").val("0");
                $("#latitude").val("0");
                $("#cityDiv").show();
                $("#mapDiv").hide();
                jQuery.ajax({
                    url:"/campaign/getcity",
                    dataType:"json",
                    type:"get",
                    success: function(data) {
                        city.empty();
                        $.each(data, function(i, item){
                            city.append('<option value= "' + item.id + '">' + item.name + '</option>');
                            <#if campaign.city??>
                                city.val(${campaign.city.id});
                            </#if>
                        });
                    }
                });
            } else if  (compaignType == 2) {
                city.empty();
                $("#cityDiv").hide();
                $("#mapDiv").show();
                initMap()
            }
        }

        //展示方式change
        function changeShowType(showType) {
            var organization = $("#organization");
            var shop = $("#shop");
            if (showType == 0) {
                organization.empty();
                shop.empty();
                $("#url").attr("disabled", false);
                organization.attr("disabled", true);
                shop.attr("disabled", true);
            } else if  (showType == 1) {
                jQuery.ajax({
                    url:"/campaign/getorganization",
                    dataType:"json",
                    type:"get",
                    success: function(data) {
                        organization.empty();
                        $.each(data, function(i, item){
                            organization.append('<option value= "' + item.id + '">' + item.name + '</option>');
                        });
                        <#if campaign.shop??>
                            organization.val(${campaign.shop.organization.id});
                        </#if>
                        changeOrganization($("#organization").val());
                        $("#url").attr("disabled", true);
                        organization.attr("disabled", false);
                        shop.attr("disabled", false);
                    }
                });
            }
        }

        function changeOrganization(id) {
            var shop = $("#shop");
            jQuery.ajax({
                url:"/campaign/getshop?id=" + id,
                dataType:"json",
                type:"get",
                success: function(data) {
                    shop.empty();
                    $.each(data, function(i, item){
                        shop.append('<option value= "' + item.id + '">' + item.name + '</option>');
                    });
                    <#if campaign.shop??>
                        shop.val(${campaign.shop.id});
                    </#if>
                }
            });
        }

        var map;
        function initMap(){
            var imark = 0;
            var shleg = 121.48115; //上海经度  默认值
            var shlat = 31.236681; //上海纬度  默认值
            // 百度地图API功能
            map = new BMap.Map("allmap");  // 创建Map实例
            map.addControl(new BMap.NavigationControl());   //放缩条
            map.addControl(new BMap.OverviewMapControl()); //小地图
            map.addControl(new BMap.MapTypeControl());   //地图类型
            map.setCurrentCity("上海"); // 仅当设置城市信息时，MapTypeControl的切换功能才能可用
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


        function search(val, needShowPosition){
            var local = new BMap.LocalSearch(map, {
                renderOptions:{map: map}
            });
            if (needShowPosition == 0) {
                // 创建地址解析器实例
                var myGeo = new BMap.Geocoder();
                // 填写经度纬度
                myGeo.getPoint(val, function(point){
                    if (point) {
                        $("#longitude").val(point.lng);
                        $("#latitude").val(point.lat);
                    }
                });
            }
            local.search(val);
        }

        function backList(){
            window.location = "/campaign/list";
        }

    </script>
    <div class="row" style="margin-top: -80px">
        <div class="col-md-8 col-md-offset-2">
            <div id="actions" class="form-action">
                <form class="" id="fm" action='<@spring.url relativeUrl = "/campaign/save"/>' method="post">
                    <@form.textInput "campaign.id" "" "hidden"/>
                    <@form.textInput "campaign.bannerImageUrl" "" "hidden" />
                    <br><br>

                    <#if campaign.id?? && campaign.id != 0>
                        <legend>APP公告管理 -> 修改APP公告</legend>
                    <#else>
                        <legend>APP公告管理 -> 新增APP公告</legend>
                    </#if>

                    <div class="row">
                            <div class="col-md-3">
                                <label class="control-label" for="compaignType">推广类型：</label>
                            </div>
                            <div class="col-md-9">
                                <div class="col-md-3">
                                    <input type="radio" id="compaignType0" name="compaignType" value="0" onchange="changeCompaignType(this.value)"/>全平台
                                </div>
                                <div class="col-md-3">
                                    <input type="radio" id="compaignType1" name="compaignType" value="1" onchange="changeCompaignType(this.value)"/>城市
                                </div>
                                <div class="col-md-3">
                                    <input type="radio" id="compaignType2" name="compaignType" value="2" onchange="changeCompaignType(this.value)"/>附近
                                </div>
                            </div>
                    </div>
                    <br/>

                    <div class="row" id="cityDiv">
                        <div class="col-md-5">
                            <select class="form-control" type="search" id="city" name="city">
                            </select>
                        </div>
                    </div>

                    <div class="row" id="mapDiv">
                        地图定位中心地址：
                        <input type="input" id="address" name ="address"  onblur="search(this.value,0);"/>
                            <div id="allmap" style="width: 600px;height: 200px; border:1px solid #000;" >
                            <#--<img src="/stylesheets/images/staticMap.png"  alt="定位"  id="staticMap" style="width: 100%;height: 100%;"/>-->
                            </div>
                            <div class="col-md-5">
                                <@form.textInput "campaign.longitude" "class='form-control' readonly" "text" "经度"/>
                            </div>
                            <div class="col-md-5 col-md-offset-2">
                                <@form.textInput "campaign.latitude" "class='form-control' readonly" "text" "纬度"/>
                            </div>
                    </div>
                    <br/>
                    <div class="row">
                        <div class="col-md-12">
                            <input type="checkbox" id="onBanner" name ="onBanner" value="true" /> <label class="control-label">是否显示于App首页Banner</label>
                        </div>
                    </div>
                    <br/>
                    <div class="row">
                        <div class="col-md-5">
                            <label class="form-group">Banner图片URL</label><br>
                            <input type="text" id="mimageUrl" readonly style="width: 100%" class="required" value="<#if campaign.bannerImageUrl??>${campaign.bannerImageUrl}</#if>"/><br/>
                            <label class="form-group">上传Banner图片</label>
                            <input id="fileupload" type="file"   name="fileUpload" data-url="http://test.zhaitech.com/upload/"   multiple>
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <label class="form-group">Banner图片预览</label><br>
                            <div style="width: 100px;height: 100px; border:1px solid #000;" >
                                <img src=""  alt="Banner图片预览"  id="bannerImg" style="width: 100%;height: 100%;"/>
                            </div>
                        </div>
                    </div>
                    <br/>
                    <div class="row">
                            <div class="col-md-3">
                                <label class="control-label" for="type">展示内容：</label>
                            </div>
                            <div class="col-md-9">
                                <div class="row">
                                    <div class="col-md-3">
                                        <input type="radio" id="showType0" name="showType" value="0" onchange="changeShowType(this.value)"/>展示内容的url
                                    </div>
                                    <div class="col-md-6">
                                        <input type="text" id="url" name="url" value="<#if campaign.url??>${campaign.url}</#if>" class='form-control'>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-md-3">
                                        <input type="radio" id="showType1" name="showType" value="1" onchange="changeShowType(this.value)"/>展示门店
                                    </div>
                                    <div class="col-md-6">
                                        <select class="form-control" type="search" id="organization" name="organization" onchange="changeOrganization(this.value)">
                                        </select>
                                        <select class="form-control" type="search" id="shop" name="shop">
                                        </select>
                                    </div>
                                </div>
                            </div>
                    </div>
                    <br/>
                    <div class="row">
                            <@form.textArea "campaign.summary" "class='form-control' style='width:615px;height:80px;'"  "文字摘要" true/>
                    </div>
                        <@form.btn_save "onclick='subForm()'" "确认保存"/>
                    <@form.btn_back "onclick='backList()'" "返回"/>

                </form>
            </div>
        </div>
    </div>
    </@main.frame>

</#escape>