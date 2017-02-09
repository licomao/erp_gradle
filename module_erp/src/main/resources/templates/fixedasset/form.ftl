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
        $('#collapseShop').collapse('show');
        $(function(){
//            $('.Wdate').datepicker();
//            $('.Wdate').datepicker("option",$.datepicker.regional["zh-TW"]);
//            $('#validDate').datepicker( "setDate", $('#validDate').val());
        });

        function subForm(){
            if(confirm("是否确认保存!")){
                $("#fm").submit();
            }
        }

        function validate(obj,num){
            var reg = new RegExp("^[0-9]*$");
            if(!reg.test(obj.value)){
                alert("请输入正整数!");
                obj.focus();
                return;
            }
        }
    </script>
    <div class="row" style="margin-top: -80px">
        <div class="col-md-5 col-md-offset-2">
            <div id="actions" class="form-action">
                <form class="" id="fm" action='<@spring.url relativeUrl = "/fixedasset/save"/>' method="post">
                    <@form.textInput "fixedAsset.id" "" "hidden"/>
                    <@form.textInput "fixedAsset.ver" "" "hidden"/>


                    <#if pageContent?? && pageContent == "update">
                        <legend>固定资产管理 -> 修改固定资产</legend>
                    <#else>
                        <legend>固定资产管理 -> 添加固定资产</legend>
                    </#if>


                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput "fixedAsset.name" "class='form-control'" "text" "固定资产名称" true/>
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <@form.textInput "fixedAsset.model" "class='form-control'" "text" "型号" true/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput "fixedAsset.price" "class='form-control' onblur='validate(this)'" "text" "单价" true/>
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <@form.textInput "fixedAsset.number" "class='form-control' onblur='validate(this)'" "text" "数量" false/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <label class="control-label">资产状态</label><br>
                            <select name="assetStatus">
                                <option value="0" <#if fixedAsset.assetStatus == 0> selected</#if>>在用</option>
                                <option value="1" <#if fixedAsset.assetStatus == -1> selected</#if>>报废</option>
                            </select>

                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <@form.textInput "fixedAsset.shop.name" "class='form-control' readonly" "text" "所属门店" true/>
                        </div>
                    </div>

                    <br/>
                        <@form.btn_save "onclick='subForm()'" "确认保存"/>
                </form>
            </div>
        </div>
    </div>
    </@main.frame>

</#escape>