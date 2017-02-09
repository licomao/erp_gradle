<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>

    <@main.frame>

    <meta http-equiv="Windows-Target" contect="_top">
    <script src="/javascripts/jquery.ui.widget.js" type="text/javascript"></script>
    <script src="/javascripts/jquery.iframe-transport.js" type="text/javascript"></script>
    <script src="/javascripts/jquery.fileupload.js" type="text/javascript"></script>

    <script type="text/javascript">
        $('#collapsePt').collapse('show');
        $(function(){
            if ($("#doType").val() == 2) {
                $(":input").attr("readonly", true);
                $("select").attr("disabled", true);
            }
            var url = "/saleshelf/list/form/skuitem?name=" + $("#shopName").val()
                    + "&rootCategory=" + $("#rootCategory").val()
                    + "&orgId=" +  $("#orgId").val() ;
            url = encodeURI(url,"UTF-8");
            querySkuItem(url);

            changeShops();
        });

        function subForm(){
            if (confirm("是否确认保存!")){
                $("#fm").submit();
            }
        }

        function validate(obj){
            var reg = /^[0-9]+([.]{1}[0-9]{1,2})?$/;
            if (!reg.test(obj.value)) {
                alert("价格必须为大于等于0的金额");
                $(obj).val("0");
                obj.focus();
                return;
            }
        }

        function backList(){
            window.location = "/saleshelf/list";
        }

        function querySkuItem(url) {
            $("#skuItemBody").jqGrid({
//                pager : '#gridpager',
                url: url,
                colModel: [
                    {name:'skuItemId',hidden:true},
                    {label: '商品名称', name: 'name', width: 80, align: "center"},
                    {label: '品牌名称', name: 'brandName', width: 80, align: "center"},
                    {label: '商品描述', name: 'description', width: 80, align: "center"},
                    {label: '顶级分类', name: 'rootCategory', width: 50, align: "center",
                        formatter: "select", editoptions:{value:"1:机油;2:机滤;3:轮胎;4:电瓶;5:电子类产品;6:美容类产品;7:汽车用品;8:养护产品;9:耗材类产品;10:灯具类产品;" +
                    "11:雨刮类产品;12:发动机配件类;13:底盘配件类;14:变速箱类;15:电气类;16:车身覆盖类;17:服务类;0:临时分类"}},
                    {
                        label: '操作', name: 'do', width: 40, align: "center",
                        formatter: function (cellvalue, options, rowObject) {
                            return "<a href='####'style='text-decoration:underline;color:blue' onclick='deleteRow(" + options.rowId + ")'>添 加</a>";
                        }
                    }
                ],
                rownumbers: true
            });
        }

        function getSkuInfo() {
            var url = "/saleshelf/list/form/skuitem?name=" + $("#shopName").val()
                    + "&rootCategory=" + $("#rootCategory").val()
                    + "&orgId=" +  $("#orgId").val() ;
            url = encodeURI(url,"UTF-8");
            jQuery("#skuItemBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
        }

        function changeShops(){
            var url = "/saleshelf/list/form/shops?orgId=" + $("#orgId").val() ;
            $.get(url, function (resultMap) {
                var scb = $("#shopCheckBoxes");
                scb.empty();
                var shops = resultMap['shopList'];
                for(var i = 0;i < shops.length;i ++){
                    scb.append("<div class='col-md-2'><input id='shopchk'name='shopchk' type='checkbox' value=" + shops[i].id + "  class='form-control' /></div>");
                    scb.append("<div class='col-md-4'><label class='control-label' >"+ shops[i].name +"</label></div>");
                    scb.append("&nbsp;");
                }
            },"json");
        }

    </script>
    <div class="row" style="">
        <div class="col-md-10 col-md-offset-1">
            <#if doType?? && doType == 0>
                <legend>APP上架管理 → 新增APP上架信息</legend>
            <#elseif doType?? && doType == 1>
                <legend>APP上架管理 → 修改APP上架信息</legend>
            <#else>
                <legend>APP上架管理 → 查看APP上架信息</legend>
            </#if>
            <div id="actions" class="form-action col-md-6">
                <form class="" id="fm" action='<@spring.url relativeUrl = "/saleshelf/save"/>' method="post">
                    <@form.textInput "saleShelf.id" "" "hidden"/>
                    <input type="hidden" id="doType" name="doType" value="${doType}" />


                    <div class="row">
                        <div class="col-md-5">
                            <@form.labelAndSelect "saleShelf.saleCategory" {"1":"洗车","2":"美容","3":"保养","4":"配件","5":"维修","6":"会员"} " class='form-control'" "上架分类：" />
                        </div>
                        <div class="col-md-3 col-md-offset-2">
                            <@form.textInput "saleShelf.price" "class='form-control' onblur='validate(this)'" "text" "出售价格(元)：" true/>
                        </div>
                    </div>
                    <br>
                    <div class="row">
                        <div class="col-md-5">
                           <label>所属组织：</label>
                            <select id="orgId" class="form-control" onchange="getSkuInfo();changeShops();">
                                <#list organizations as org>
                                    <option value="${org.id}">
                                    ${org.name}
                                    </option>
                                </#list>
                            </select>
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                        </div>
                    </div>
                    <br>
                    <div class="row">
                        <div class="col-md-5">
                        <label>所属门店：</label>
                        </div>

                    </div>
                    <div class="row">
                        <div id="shopCheckBoxes" class="col-md-10">
                        </div>
                    </div>
                    <br/>
                    <#if doType?? && doType == 2>
                        <@form.btn_back "onclick='backList()'" "返回"/>
                    <#else>
                        <@form.btn_save "onclick='subForm()'" "确认保存"/>
                    </#if>

                </form>
            </div>

            <div class="col-md-6 ">
                <div id="actions" class="form-action">
                    <div class="row">
                        <div class="col-md-9">
                            <label  class="control-label">商品名称: </label>
                            <input type="text" id="shopName" value="">
                            <label  class="control-label">顶级分类: </label>
                            <@form.topCategory "rootCategory" "" />&nbsp;
                        </div>
                        <div class="col-md-3">
                            <@form.btn_search "onclick='getSkuInfo()'" "查 询"/>
                        </div>
                    </div>
                    <br>
                    <table id="skuItemBody" class="scroll" cellpadding="0" cellspacing="0"></table>
                    <div id="toolBar"></div>
                </div>
            </div>

        </div>
    </div>
    </@main.frame>

</#escape>