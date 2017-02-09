<#import "../macros/mainFrame.ftl" as main />

<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>

    <@main.frame>

    <meta http-equiv="Windows-Target" contect="_top">
    <script src="/javascripts/fingerprint-tool.js" type="text/javascript"></script>
    <script type="text/javascript">
       /* var matx;  //控件
        var fpcHandle;  //指纹缓冲区*/
        var myString = "";

        //stype=1时 上班打卡 stype=2时,下班打卡 stype默认=0 不执行打卡
        var stype = 0;

        var staffArr = {};
        $('#collapseStaff').collapse('show');

        /**
         * 验证指纹
         */
        function validFgp(type){
            stype = type
            var str = "亲爱的小伙伴,新的一天开始了!确认上班了吗?"
            if(type == 2)
                str = "亲爱的小伙伴,忙碌了一天很辛苦吧,确认要下班了吗?"


            if (confirm(str)){
                $('#fm')[0].reset();
                $("#fortst").text("现在可以开始验证指纹了");
                initFpcHandle();

                /*var fingerStr = "mspZVoOkm01qwQ+dzWPBE6TRYgEKodFugQ+pU2sBCZ5TcwENrtBKgQeo0FLBCqzJFwEg0socASHazhgBKb1EdcEMPkVwwQoxwWRBCp7BaMEHpDk9gQwPQWeBCC2iRUEPDi94AQWbpVNBCRU9cYEPLL1uQQylumQBC5kpdcEGJc0YgSjVrkyBCoc7OIEQf7ghgQvmLCkBDnCeR4EQgaNhwQmNHjEBCHO6UAEOJNJsAQspNHHBCie7fIERmrpNwRGNVFgBDS3UTgENL1E3wQ8sUy5BMpICEGltcXUDCA8UFxkbHyAjIgIQY2pvdQQJDhQZHB8hIiIjAhBscHJ0AQYNEhQXGh8gIiMBEF1iaW51BAkPFRofIiQkIyQCD21xc3V3BAsPExYaHSAhABBYXWNpcHYFChEWGyAkJycmJQMPcnV2AQQJDREVGR0gIQAQWV1iZnB3BQ0VGiAjJikpKSkEDnYBAwYKDhMWGRwfABBYW2BjbncGEBkfJCYpLC0tLwUNAgQGCg4SFRgbABBVVlthbHYJFR8lKCotLzEwMwYLBAYKDhMWABBVU1lgawERHCQpKywtLzMzNAAA/wAQRERNXm4DGSQqLS0tLS8zNDYAAP8AEEhDSVtyCiIqKy0tLCsuMTY5AAD/ABBBQ0ZUAxclLS4wLi4tMjVARQAA/wEPRP///yIqLS0wLzAuNDhEGPAEGbSpUBQhBijFM+LxiFiJS7BpctTfrXsj4r5lZ3P7x1JXk1fRc8tJCbvsRQWrs3zF7AD0lSV92216DPUbqFtE+/Lpfp5N2HxMXAllnyXj2Ga2/EY0gO4OiKj3uKQIkYdavjlj+tz1DYMLOo6gNrpJMncx5hwV1xxgmnf7K0V3S3SBVHsw3Qcynp8JgGWbCuyHHmYz3pioPyRphNa4oOzt1Ro6bKC4MguBtIn02O3tyiu7P+0s4/M3c5gTmn/ZP6KBeVX1bh+Ms45B5L19YkGE5tcCxog3HQdQRhKx573LMyKcO2FCMKvA0j8kpWuGY0+p3j5tyM275YIfIAqZ8abzcjWHYCjTGzxkCGKNW0mZPsTUidYK+SocrYY9uIt4dxgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBgYGBg="
                $.get('/staffattendance/findbyid2?stype=' + stype, function(data){
                    if(data.result){

                        var arriveDate = formatterDateWithSecond(data.staffAttendance.arriveDate);
                        var leaveDate = formatterDateWithSecond(data.staffAttendance.leaveDate);

                        $("#arriveDate").val(arriveDate);
                        $("#leaveDate").val(leaveDate);


                        $("#fortst").text("打卡成功!");

                    }else{
                        var arriveDate = formatterDateWithSecond(data.staffAttendance.arriveDate);
                        var leaveDate = formatterDateWithSecond(data.staffAttendance.leaveDate);
                        alert(data.message)
                        $("#arriveDate").val(arriveDate);
                        $("#leaveDate").val(leaveDate);
                        $("#fortst").text("");
                    }
                    $("input[id='staff.phone']").val(data.staffAttendance.staff.phone);
                    $("input[id='staff.name']").val(data.staffAttendance.staff.name);
                    $("input[id='staff.job.name']").val(data.staffAttendance.staff.job.name);
                    $("input[id='staff.shop.name']").val(data.staffAttendance.staff.shop.name);
                },'json');*/
            }


        }

       /* function success(){
            $("#fortst").text("验证指纹成功!");
        }*/

        $(function(){

            initFingerprint();

            if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_FINGERATTENDANCE)?c}) {
                window.location = "/noauthority"
            }
        });

        /*function onCaptureCallBack(ret){
            if (ret != -1) {

                var staff = staffArr[ret.toString()];
                $("input[id='staff.phone']").val(staff.phone);
                $("input[id='staff.name']").val(staff.name);
                $("input[id='staff.job.name']").val(staff.jobName);
                $("input[id='staff.shop.name']").val(staff.shopName);

                $.get('/staffattendance/findbyid/' + ret.toString(), function(data){
                    if(data.result){

                        var arriveDate = formatterDateWithSecond(data.staffAttendance.arriveDate);
                        var leaveDate = formatterDateWithSecond(data.staffAttendance.leaveDate);

                        $("#arriveDate").val(arriveDate);
                        $("#leaveDate").val(leaveDate);

                    }
                },'json');

                success();

                matx.EndEngine();

            } else {
                $("#fortst").text("验证失败,请重试!");

            }
        }*/

        function msleep(milliSeconds) {
            var startTime = new Date().getTime();  // get the current time
            while (new Date().getTime() < startTime + milliSeconds);  // hog cpu
        }

        /**
         * 初始化高速缓冲区
         */
        function initFpcHandle(){
            fpcHandle = matx.CreateFPCacheDB();
            <#if staffList??  >
                <#list staffList as obj >

                    var staff = {};
                    matx.AddRegTemplateStrToFPCacheDB(fpcHandle, ${obj.id}, "${obj.fingerPrint}");
                    staff.name = "${obj.name!}";
                    staff.jobName = "${obj.job.name!}";



                    staff.shopName = "${obj.shop.name!}";
                    staff.id = "${obj.id}";
                    staff.phone = "${obj.phone!}";
                    staffArr["${obj.id}"] = staff;
                </#list>

            </#if>
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
            /*if (confirm("是否确认打卡!")){
                $("#fm").submit();
            }*/

        }

        function ctlBeep() {
            try {
                matx.ControlSensor(13, 1);//控制绿灯闪一次，蜂鸣器长鸣一声
                msleep(1);
                matx.ControlSensor(13, 0);//控制绿灯闪一次，蜂鸣器长鸣一声
            } catch (e) {
                alert("控件还没有初始化");
            }
        }

    </script>

    <SCRIPT type="text/javascript" FOR="myativx" EVENT="OnImageReceived(istrue)">

    </SCRIPT>
    <#--<SCRIPT type="text/javascript" FOR="myativx" EVENT="OnFeatureInfo(qulity)">

        var str = "不合格";
        if (qulity == 0) {
            str = "合格";

        }
        if (qulity == 1) {
            str = "特征点不够";
            $("#fortst").text("验证失败,请重试!");
        }

        if (matx.IsRegister) {
            if (matx.EnrollIndex != 1) {
                var t = matx.EnrollIndex - 1;

                $("#fortst").text("登记状态：请再按 " + t.toString() + " 次指纹 ");
            }
        }

    </SCRIPT>-->
    <SCRIPT type="text/javascript" FOR="myativx" EVENT="OnCapture(ActionResult ,ATemplate)">
        var tmp = matx.GetTemplateAsString();

        var Parr = [9, 0];  //alert(arr[0]);

        ret = matx.IdentificationFromStrInFPCacheDB(fpcHandle, tmp, Parr[0], Parr[1]);
        if (ret != -1) {

            var staff = staffArr[ret.toString()];
            $("input[id='staff.phone']").val(staff.phone);
            $("input[id='staff.name']").val(staff.name);
            $("input[id='staff.job.name']").val(staff.jobName);
            $("input[id='staff.shop.name']").val(staff.shopName);
            $.ajaxSetup({ cache: false });
            $.get('/staffattendance/findbyid/' + ret.toString() + '?stype=' + stype, function(data){
                if(data.result){

                    var arriveDate = formatterDateWithSecond(data.staffAttendance.arriveDate);
                    var leaveDate = formatterDateWithSecond(data.staffAttendance.leaveDate);

                    $("#arriveDate").val(arriveDate);
                    $("#leaveDate").val(leaveDate);


                    $("#fortst").text("打卡成功!");

                }else{
                    if(data.staffAttendance != null){
                        //打卡失败, 员工未离职
                        var arriveDate = formatterDateWithSecond(data.staffAttendance.arriveDate);
                        var leaveDate = formatterDateWithSecond(data.staffAttendance.leaveDate);
                        $("#arriveDate").val(arriveDate);
                        $("#leaveDate").val(leaveDate);
                    }
                    $("#fortst").text("");
                    alert(data.message)
                }
            },'json');

//            success();

//            matx.EndEngine();

        } else {
            $("#fortst").text("打卡失败,请重试!");

        }

    </SCRIPT>
    <div class="row" style="margin-top: -80px">
        <div class="col-md-5 col-md-offset-2">
            <div id="actions" class="form-action">
                <form class="" id="fm" action='<@spring.url relativeUrl = "/staffattendance/save"/>' method="post">
                    <@form.textInput "staffattendance.staff.id" "" "hidden"/>
                    <br><br>
                    <@form.textInput "staffattendance.id" "" "hidden"/>

                        <legend>员工考勤 -> 上下班考勤</legend>

                    <div class="row">
                        <div class="col-md-7">
                            <p style="font-size: 10px;color: red">注:手机号输入完毕后请按回车键进行查询</p>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <@form.textInput "staffattendance.staff.phone" "class='form-control' readonly onkeydown='if(event.keyCode==13){findStaff()}'" "text" "手机号码：" />
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
                    <div id="fingerPrintDiv" >

                        <a href="####" class="btn btn-primary btn_erp "  onclick='validFgp(1)' id="arrive" >
                            <img src="/stylesheets/images/erp/search.png">&nbsp;上班打卡</a>&nbsp;&nbsp;
                        <a href="####" class="btn btn-primary btn_erp "  onclick='validFgp(2)' id="leave"  >
                            <img src="/stylesheets/images/erp/search.png">&nbsp;下班打卡</a>
                        <#--<a href="####" class="btn btn-primary btn_erp "  onclick='validFgp()' id="arrive" >
                            <img src="/stylesheets/images/erp/search.png">&nbsp;打卡</a>&nbsp;&nbsp;-->
                        <label id="fortst" ></label>

                    </div>
                </form>
                <div style="display: none" >
                    <object
                            id="myativx"
                            classid="clsid:CA69969C-2F27-41D3-954D-A48B941C3BA7"
                            width=100%
                            height=210
                            align=middle
                            hspace=0
                            vspace=0
                            onerror="onObjectError();">
                    </object>
                </div>
            </div>
        </div>
    </div>

    </@main.frame>

</#escape>
