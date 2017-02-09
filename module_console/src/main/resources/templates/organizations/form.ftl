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

    <script type="text/javascript">
        $('#collapseOrgs').collapse('show');
        $(function(){

            $('.Wdate').datepicker();
            $('.Wdate').datepicker("option",$.datepicker.regional["zh-TW"]);
            $( ".Wdate" ).datepicker( "option", "dateFormat", "yy-mm-dd" );
            $('#validDate').datepicker( "setDate", $('#validDateStr').val() );



            var url = getUrl('');
            $(function () {
                $('#fileupload').fileupload({
                    dataType: 'iframe',
                    redirect: url,
                    forceIframeTransport: true,
                    done: function (e, data) {
                        //$('#mret').val(data.result[0].body.innerText);
                        var imgUrl = getUrl(data.result[0].body.innerText);
                        $("#optImg").attr("src",imgUrl);
                        $("#businessLicenseImageUrl").val(imgUrl);
                        $("#mimageUrl").val(imgUrl);
                    },
                    fail: function (e, data) {
                        console.debug(e);
                        console.debug(data);
                    }
                });
            });

            setIsTry();
            $("input[name=radio]").change(function() {
                var cval = $("input[name='radio']:checked").val();
                $("#tried").val(cval)
            });

            initImg();
        });

        function initImg() {
            var dbimgurl = $("#businessLicenseImageUrl").val();
            if(dbimgurl != null || dbimgurl.trim() != '') {
                $("#optImg").attr("src",dbimgurl);
            }
            $("#mimageUrl").val(dbimgurl);
        }

        function setIsTry() {
            var istry = $("#tried").val();
            if(istry == null || istry.trim() == '') {
                $("#tried").val("false");
            }else {
                if(istry.trim() == 'true')
                {
                    $("#notry").attr("checked",true);
                }
            }
        }

        function getUrl(key) {
            var url = "";
            if(key == '') {
                url = window.location.href;
                url = url.substr(0,url.lastIndexOf('/'));
                url = url.substr(0,url.lastIndexOf('/'));
                url += "/html/result.html?%s";
            }else {
                url = "http://test.zhaitech.com/images" + key;
            }
            return url;
        }

        function validate(obj){
            var reg = new RegExp("^[1-9]\d*|0$");
            if (!reg.test(obj.value)) {
                alert("请输入非负整数!");
                obj.focus();
                return;
            }
        }

        function subForm(){
            $("#fm").submit();
        }
    </script>
    <div class="row" style="margin-top: -80px">
        <div class="col-md-5 col-md-offset-2">
            <div id="actions" class="form-action">
                <form id="fm" action='<@spring.url relativeUrl = "/organizations/new"/>' method="post">
                    <@form.textInput "organization.id" "" "hidden"/>
                    <@form.textInput "organization.ver" "" "hidden"/>
                    <@form.textInput "organization.businessLicenseImageUrl" "" "hidden" />
                    <#if pageContent?? && pageContent == "更新">
                        <legend>更新连锁组织</legend>
                    <#else>
                        <legend>新建连锁组织</legend>
                    </#if>


                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput "organization.serialNum" "class='form-control'" "text" "注册号" true/>
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <@form.textInput "organization.name" "class='form-control'" "text" "名称" true/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput "organization.taxNumber" "class='form-control'" "text" "税号" true/>
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <@form.textInput "organization.shopQuota" "class='form-control' onblur='validate(this)'" "text" "门店配额" true/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput "organization.bankName" "class='form-control'" "text" "开户行" true/>
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <@form.textInput "organization.bankAccount" "class='form-control'" "text" "开户行账号" true/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput "organization.contact" "class='form-control'" "text" "联系人" true/>
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <@form.textInput "organization.contactPhone" "class='form-control'" "text" "联系人电话" true/>
                        </div>
                    </div>
                    <div class="row">
                        <@form.textInput "organization.contactAddress" "class='form-control'" "text" "联系人地址" true/>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <label class="form-group">试用账户</label><br>
                            <INPUT type=radio name="radio" id="istry" value="false"  checked="checked">否
                            <INPUT type=radio name="radio" id="notry" value="true">是
                            <@form.textInput "organization.tried" ""  "hidden" />
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <@form.textInput "organization.validDate" "class='Wdate form-control' readonly" "text" "有效期限"    true/>
                            <@form.textInput "organization.validDateStr" "" "hidden"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <label class="form-group">营业执照图片地址</label><br>
                            <input type="text" id="mimageUrl"  readonly style="width: 100%" /><br/>
                            <label class="form-group">上传营业执照</label>
                            <input id="fileupload" type="file"   name="fileUpload" data-url="http://test.zhaitech.com/upload/"   multiple>
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <label class="form-group">营业执照预览</label><br>
                            <div style="width: 200px;height: 200px; border:1px solid #000;" >
                                <img src=""  alt="营业执照"  id="optImg" style="width: 100%;height: 100%;"/>
                            </div>
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