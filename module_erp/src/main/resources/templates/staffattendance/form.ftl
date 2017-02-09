<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>

    <@main.frame>

    <meta http-equiv="Windows-Target" contect="_top">

    <script type="text/javascript">
        $('#collapseStaff').collapse('show');
        $(function(){
            if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_DOSTAFFATTENDANCE)?c}) {
                window.location = "/noauthority"
            }
        });

        function findStaff(){
            var phone = $("[id='staff.phone']").val();
            var shopId = $("#shopId").val();
            if(phone != "") {
                var murl = "/staffattendance/findStaff?phone=" + phone + "&shopId=" + shopId;
                $.ajaxSetup({ cache: false });
                $.ajax({
                    url:murl,
                    dataType:"json",
                    type:"get",
                    success: function(data) {
                        if (data.staffattendance != null) {
                            if (data.testvalue != null) {
                                $("#leave").attr("disabled", false);
                                $("#arrive").attr("disabled", false);
                                $("[id='staff.name']").val(data.staffattendance.staff.name);
                                $("[id='staff.job.name']").val(data.staffattendance.staff.job.name);
                                $("[id='staff.shop.name']").val(data.staffattendance.staff.shop.name);
                                $("[id='staff.id']").val(data.staffattendance.staff.id);

                                if (data.staffattendance.arriveDate == null) {
                                    $("#leave").attr("disabled", true);
                                    $("#arrive").attr("disabled", false);
                                }
                                else {
                                    $("#arriveDate").val(formatterDateWithSecond(data.staffattendance.arriveDate));
                                    if (data.staffattendance.leaveDate == null) {
                                        $("#arrive").attr("disabled", true);
                                        $("#leave").attr("disabled", false);
                                    } else {
                                        $("#leaveDate").val(formatterDateWithSecond(data.staffattendance.leaveDate));
                                        $("#leave").attr("disabled", true);
                                        $("#arrive").attr("disabled", true);
                                    }
                                }
                            }else{

                            }

                        } else {
                            alert("根据手机号未查到员工信息");
                            $("[id='staff.name']").val("");
                            $("[id='staff.job.name']").val("");
                            $("[id='staff.shop.name']").val("");
                            $("[id='staff.id']").val("");
                            $("#arriveDate").val("");
                            $("#leaveDate").val("");
                            $("#leave").attr("disabled", "true");
                            $("#arrive").attr("disabled", "true");
                        }


                    }
                });
            } else {
                $("[id='staff.name']").val("");
                $("[id='staff.job.name']").val("");
                $("[id='staff.shop.name']").val("");
                $("[id='staff.id']").val("");
                $("#arriveDate").val("");
                $("#leaveDate").val("");
                $("#leave").attr("disabled", "true");
                $("#arrive").attr("disabled", "true");
            }
        }

        function subForm(type){
            if(type == 1){
                if(confirm("亲爱的小伙伴,新的一天开始了!确认上班了吗?")){
                    $("#fm").submit();
                }
            }else{
                if (confirm("亲爱的小伙伴,忙碌了一天很辛苦吧,确认要下班了吗?")){
//                  if (confirm("是否确认打卡!")){
                    $("#fm").submit();
                }
            }

        }

    </script>
    <div class="row" style="margin-top: -80px">
        <div class="col-md-5 col-md-offset-2">
            <div id="actions" class="form-action">
                <form class="" id="fm" action='<@spring.url relativeUrl = "/staffattendance/save"/>' method="post">
                    <@form.textInput "staffattendance.staff.id" "" "hidden"/>
                    <input type="hidden" id="shopId" value="${loginShop.id}">
                    <br><br>
                    <@form.textInput "staffattendance.id" "" "hidden"/>

                        <legend>员工考勤 -> 上下班考勤</legend>

                    <div class="row">
                        <div class="col-md-7">
                            <p style="font-size: 10px;color: red">注:手机号输入完毕后请点击手机号码输入框以外的地方进行查询</p>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput "staffattendance.staff.phone" "class='form-control' onblur='findStaff()'" "text" "手机号码：" true/>
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <@form.textInput "staffattendance.staff.name" "class='form-control' readonly" "text" "姓名：" />
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput "staffattendance.staff.job.name" "class='form-control' readonly" "text" "职位（工种）：" />
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                                <@form.textInput  "staffattendance.staff.shop.name" "class='form-control' readonly" "text" "所属门店："  />
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput "staffattendance.arriveDate" "class='form-control' readonly" "text" "上班时间：" />
                        </div>
                        <div class="col-md-5 col-md-offset-2">
                            <@form.textInput "staffattendance.leaveDate" "class='form-control' readonly" "text" "下班时间：" />
                        </div>
                    </div>

                    <br/>
                    <br/>
                    <a href="####" class="btn btn-primary btn_erp "  onclick='subForm(1)' id="arrive" disabled="disabled" >
                        <img src="/stylesheets/images/erp/search.png">&nbsp;上班打卡</a>&nbsp;&nbsp;
                    <a href="####" class="btn btn-primary btn_erp "  onclick='subForm(2)' id="leave" disabled="disabled" >
                        <img src="/stylesheets/images/erp/search.png">&nbsp;下班打卡</a>

                </form>
            </div>
        </div>
    </div>
    </@main.frame>

</#escape>