<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>

    <@main.frame>

    <#--select2-->
    <link href="/stylesheets/select2.min.css" rel="stylesheet" />
    <script src="/javascripts/select2js/select2.min.js"></script>
    <script type="text/javascript">
        $('#collapseShop').collapse('show');
        var vehicleIndex = 1;


        $(function () {
            if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_CUSTOMER)?c}) {
                window.location = "/noauthority";
            }
            $(".select2").select2({
                placeholder: "请选择",
            });
            $(document).on('focus',".Wdate", function(){
                $(this).datepicker();
            })

            //选择性别
            <#if customerErpProfile.gender??>
            if (${customerErpProfile.gender} == 1)
            {
                $("#gender1").attr("checked", "checked");
            }
            else
            {
                $("#gender0").attr("checked", "checked");
            }
            </#if>
            <#if customerErpProfile.vehicles?? >
            //车数
            vehicleIndex = ${customerErpProfile.vehicles.size()};
            </#if>


            setLineAndVersion();
        });

        function setLineAndVersion() {

        <#if customerErpProfile.vehicles??>
            <#list customerErpProfile.vehicles as vehicle>
                <#if vehicle.model??>

                if($("#brand${vehicle_index}").val() != "请选择") {
                    var url = encodeURI("/customer/getlines?brand=" + $("#brand${vehicle_index}").val(), "UTF-8");
                    $.ajaxSetup({ cache: false });
                    $.ajax({
                        url:url,
                        dataType:"json",
                        type:"get",
                        success: function(ret) {
                            $("#line${vehicle_index}").empty();
                            $("#line${vehicle_index}").append('<option></option>');
                            for(var i = 0 ; i < ret.length;i++) {
                                if(ret[i] != '${vehicle.model.line}') {
                                    $("#line${vehicle_index}").append('<option value= "' + ret[i] + '">' + ret[i] + '</option>');
                                }else {
                                    $("#line${vehicle_index}").append('<option value= "' + ret[i] + '" selected>' + ret[i] + '</option>');
                                }
                            }
                            $("#line${vehicle_index}").select2();

                            if($("#brand${vehicle_index}").val() != "请选择") {
                                $.ajaxSetup({ cache: false });
                                var url2 = encodeURI("/customer/getversions?brand=" + $("#brand${vehicle_index}").val() + "&line=" + '${vehicle.model.line}', "UTF-8");
                                $.ajax({
                                    url:url2,
                                    dataType:"json",
                                    type:"get",
                                    success: function(ret) {
                                        $("#version${vehicle_index}").empty();
                                        $("#version${vehicle_index}").append('<option></option>');
                                        for(var i = 0 ; i < ret.length;i++) {
                                            if(ret[i][0] != '${vehicle.model.version}') {
                                                $("#version${vehicle_index}").append('<option value= "' + ret[i][1] + '">' + ret[i][0] + '</option>');
                                            }else {
                                                $("#version${vehicle_index}").append('<option value= "' + ret[i][1] + '" selected>' + ret[i][0] + '</option>');
                                            }
                                        }
                                        $("#version${vehicle_index}").select2();
                                    }
                                });
                            }else {//清空line
                                $("#version${vehicle_index}").empty();
                                $("#version${vehicle_index}").append('<option></option>');
                            }
                        }
                    });
                }else {//清空line and version
                    $("#line${vehicle_index}").empty();
                    $("#line${vehicle_index}").append('<option></option>');
                }
                </#if>
            </#list>
        </#if>
        }

        //添加车辆信息
        function addVehicleInfo() {
            var vehicle = $("#vehicleInfo").clone();
            vehicle.find("select").each(function(index,element) {
                var id = vehicle.find("select").eq(index).attr('id');
                var name = vehicle.find("select").eq(index).attr('name');
                vehicle.find("select").eq(index).attr('id',id + vehicleIndex).attr('name',name + vehicleIndex);

            });
            vehicle.find("input").each(function(index,element) {
                var id = vehicle.find("input").eq(index).attr('id');
                var name = vehicle.find("input").eq(index).attr('name');
                vehicle.find("input").eq(index).attr('id',id + vehicleIndex).attr('name',name + vehicleIndex);
                if (id == "vehicleId") {
                    vehicle.find("input").eq(index).val(0);
                }
            });

//
//            $("#select2-brand0-container").attr('id',"select2-brand01-container");
//            $("#brand0" + vehicleIndex).select2();


            $("#addBtn").before(vehicle);
            $("#mileageUpdatedDate0"+vehicleIndex).removeClass("hasDatepicker");
            $("#mileageUpdatedDate0"+vehicleIndex).datepicker();
            $("#lastMaintenanceDate0"+vehicleIndex).removeClass("hasDatepicker");
            $("#lastMaintenanceDate0"+vehicleIndex).datepicker();
            $("#plateNumber" + vehicleIndex).blur("checkPlateNumber(this);");
            $(".select2-container").remove();

//            $("#brand0" + vehicleIndex).remove();
//            $("#brand0" + vehicleIndex).select2({
//                placeholder: "请选择",
//            });
//            $("#line0" + vehicleIndex).select2({
//            placeholder: "请选择",
//            });

//            $("#brand0  option").each(function () {
//                var txt = $(this).text();
//                var val = $(this).val();
//                $("#brand0" + vehicleIndex).append('<option value= "' + val + '">' + txt + '</option>');
//
//            });


            $("#brand0" + vehicleIndex).width("176px");
            $("#line0" + vehicleIndex).width("176px");
            $("#version0" + vehicleIndex).width("523px");
            $(".select2").select2({
                placeholder: "请选择",
            });


            vehicleIndex++;
        }

        function deletedVehicle(obj){
            if (vehicleIndex > 1){
                jQuery.ajax({
                    url:"/customer/confirmissale?id=" + obj.parentNode.firstElementChild.value,
                    dataType:"json",
                    type:"get",
                    success: function(ret) {
                        if(ret) {
                            obj.parentNode.parentNode.parentNode.id = "del"
                            obj.parentNode.parentNode.parentNode.innerHTML="";
                            vehicleIndex--;
                        }else {
                            alert("该车辆已有销售开单，无法删除");
                        }
                    }
                });
            } else {
                alert("最少一辆车！");
            }
        }
        function getCarData() {
            var name = $("#realName").val();
            if(name==null || name == "") {
                document.getElementById("realName").focus();
                alert("请输入顾客姓名。");
                return;
            }
            var mobile = document.getElementById("customer.mobile").value;
            if(mobile==null || mobile == "") {
                document.getElementById("customer.mobile").focus();
                alert("请输入顾客手机号。");
                return;
            } else {
                if(mobile.length != 11) {
                    document.getElementById("customer.mobile").focus();
                    alert("请输入11位手机号。");
                    return;
                }

            }

            var plateNumber = $("input[name='plateNumber']");
            for(i = 0; i < plateNumber.size() ; i++) {
                if(plateNumber[i].value == null || plateNumber[i].value == "") {
                    plateNumber[i].focus();
                    alert("请输入车牌号。");
                    return;
                }else {
                    if(plateNumber.size() > 1) {
                        for(j = i + 1; j < plateNumber.size() ; j++ ) {
                            if(plateNumber[i].value == plateNumber[j].value) {
                                plateNumber[j].focus();
                                alert("车牌号不可重复，请重新输入。");
                                return;
                            }
                        }
                    }
                }
            }

            var vinCode = $("input[name='vinCode']");
            for(i = 0; i < vinCode.size() ; i++) {
                if(vinCode[i].value == null || vinCode[i].value == "") {
                    vinCode[i].focus();
                    alert("请输入vin码");
                    return;
                }
            }

            var version = $("select[name^='version']");
            for(i = 0; i < version.size() ; i++) {
                if(version[i].value == "" || version[i].value == "请选择") {
                    version[i].focus();
                    alert("请选择对应车型。");
                    return;
                }
            }

            var carsInfo = $("#cars :text");
            var hiddenInfo = $("#cars input:hidden");
            var selectedData = $("#cars option:selected");
            var datas = "";
            for(i=0; i<carsInfo.size() - 1; i++) {//忽略个添加按钮字符串
                if(i % 7 == 0) {//第一个时，插入id
                    datas += formatterPrice(hiddenInfo[parseInt(i/7)+1].value) + ",";//汽车id
                }else if(i % 7 == 2) {//当查询到每辆车第三个字符串的时候，插入汽车排量信息
                    datas += selectedData[parseInt(i/7)*4].value + ",";//汽车排量
                }else if(i % 7 == 3) {//当查询到每辆车第五个字符串的时候
                    datas += selectedData[parseInt(i/7)*4+1].value + ",";//品牌
                    datas += selectedData[parseInt(i/7)*4+2].value + ",";//车系
                    datas += selectedData[parseInt(i/7)*4+3].value + ",";//车型
                }
                datas += carsInfo[i].value + ",";
            }
            $("#datas").val(datas);

            <#--<#if !isUpdate?? || isUpdate != "true">-->
            $.ajax({
                url:"/customer/confirmmobile?mobile=" + mobile + "&profileId=" + $("#profileId").val(),
                dataType:"json",
                type:"get",
                success: function(ret) {
                    if(ret) {
                        document.getElementById("customer.mobile").focus();
                        alert("输入的手机号已重复，请重新输入。");
                    }else {

                        $("#fm").submit();
                    }
                }
            });
            <#--<#else>-->
                <#--$("#fm").submit();-->
            <#--</#if>-->
        }

        function validate(obj){
            var reg = new RegExp("^\\d+$");
            if(!reg.test(obj.value)){
                alert("请输入正整数!");
                obj.focus();
                return;
            }
        }

        //获取lines
        function changeBrand(index) {
            index = index.substr(5);
            var brand = $("#brand" + index).val();
            var line = $("#line" + index);
            var url = encodeURI("/customer/getlines?brand=" + brand, "UTF-8");
            if(brand != "请选择") {
                $.ajax({
                    url:url,
                    dataType:"json",
                    type:"get",
                    contentType: "application/x-www-form-urlencoded; charset=utf-8",
                    success: function(ret) {
                        line.empty();
                        line.append('<option></option>');
                        for(var i = 0 ; i < ret.length;i++) {
                            line.append('<option value= "' + ret[i] + '">' + ret[i] + '</option>');
                        }
                    }
                });
            }else {//清空line and version
                line.empty();
                line.append('<option></option>');
            }
            changeLine(index);
        }

        //获取version
        function changeLine(index) {
            index = index.substr(4);
            var brand = $("#brand" + index).val();
            var line = $("#line" + index).val();
            var version = $("#version" + index);
            var url = encodeURI("/customer/getversions?brand=" + brand + "&line=" + line, "UTF-8");
            if(brand != "请选择") {
                $.ajax({
                    url:url,
                    dataType:"json",
                    type:"get",
                    contentType: "application/x-www-form-urlencoded; charset=utf-8",
                    success: function(ret) {
                        version.empty();
                        version.append('<option></option>');
                        for(var i = 0 ; i < ret.length;i++) {
                            version.append('<option value= "' + ret[i][1] + '">' + ret[i][0] + '</option>');
                        }
                    }
                });
            }else {//清空line
                version.empty();
                version.append('<option></option>');
            }
        }

        function checkPlateNumber(obj) {
            var profileId = $("#profileId").val();
            var plateNumber = obj.value;

            $.ajax({
                url:"/customer/checkplatenumber?profileId=" + profileId + "&plateNumber=" + plateNumber,
                dataType:"json",
                type:"get",
                success: function(ret) {
                    if(ret) {
                        alert("该车牌号已经被其他顾客所使用，请验证信息！");
                        obj.focus();
                    }
                }
            });
        }
    </script>
    <legend>顾客管理 -> 顾客信息编辑</legend>
    <form id="fm" action='<@spring.url relativeUrl = "/customer/save"/>' method="post">
    <div class="row">
        <legend style="font-size: 17px;">顾客信息</legend>
    </div>
    <div class="row">
        <div class="col-md-3">
            <input type="hidden" name="profileId" id="profileId" value="${customerErpProfile.id?c}">
            <@form.labelAndTextInput "customerErpProfile.realName" "class='form-control'" "text" "姓名：" true/>
        </div>
        <div class="col-md-3">
            <div class="col-md-5">
                <label class="control-label" for="gender">性别：</label>
            </div>
            <div class="col-md-7">
                <div class="col-md-5">
                <input type="radio" id="gender0" name="gender" value="0" />男
                </div>
                <div class="col-md-5">
                <input type="radio" id="gender1" name="gender" value="1" />女
                </div>
            </div>
        </div>
    </div>
    <div class="row" style="margin-top: 1%">
        <div class="col-md-3">
            <@form.labelAndTextInput "customerErpProfile.customer.mobile" "class='form-control' onblur='validate(this)'" "text" "手机：" true/>
        </div>
        <div class="col-md-3">
            <@form.labelAndTextInput "customerErpProfile.organization.name" "class='form-control'; disabled='disabled'" "text" "所属公司："/>
        </div>
    </div>
    <div class="row">
        <legend style="font-size: 17px;">车辆信息</legend>
    </div>
    <div id="cars">
        <input type="hidden" id="datas" name="datas"/>
        <#if customerErpProfile.vehicles??>
            <#list customerErpProfile.vehicles as vehicle>
                <div id="vehicleInfo">
                    <br>
                    <div class="row ">
                        <div class="text-right col-md-6">
                            <input type="hidden" id="vehicleId" name="vehicleId" value="<#if vehicle.id??>${vehicle.id}</#if>" class='form-control'>
                        <a href="####" onclick="deletedVehicle(this)" >—删除车辆信息</a>
                        </div>
                        <br>
                    </div>
                    <div class="row">
                        <div class="col-md-3 form-group required">
                            <div class="col-md-5">
                                <label class="control-label" for="plateNumber">车牌号：</label>
                            </div>
                            <div class="col-md-7">
                                <input type="text" id="plateNumber" onblur="checkPlateNumber(this);" name="plateNumber" value="<#if vehicle.plateNumber??>${vehicle.plateNumber}</#if>" class='form-control'>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="col-md-5 form-group required">
                                <label class="control-label" for="vinCode">vin码：</label>
                            </div>
                            <div class="col-md-7">
                                <input type="text" id="vinCode" name="vinCode" value="<#if vehicle.vinCode??>${vehicle.vinCode}</#if>" class='form-control'>
                            </div>
                        </div>
                    </div>
                    <div class="row" style="margin-top: 1%">
                        <div class="col-md-3">
                            <div class="col-md-5">
                                <label class="control-label" for="engineDisplacement">汽车排量: <font color="red">*</font></label>
                            </div>
                            <div class="col-md-7">
                                <select class="form-control" type="search" id="engineDisplacement" name="vehicle.engineDisplacement">
                                    <option value= "A" <#if vehicle.engineDisplacement?? && ("${vehicle.engineDisplacement}" == "A")>selected</#if> >A (排量 < 1.6升)</option>
                                    <option value= "B" <#if vehicle.engineDisplacement?? && ("${vehicle.engineDisplacement}" == "B")>selected</#if> >B (1.6升 ≤ 排量 ≤ 1.8升)</option>
                                    <option value= "C" <#if vehicle.engineDisplacement?? && ("${vehicle.engineDisplacement}" == "C")>selected</#if> >C (1.8升 < 排量 ≤ 2.3升)</option>
                                    <option value= "D" <#if vehicle.engineDisplacement?? && ("${vehicle.engineDisplacement}" == "D")>selected</#if> >D (2.3升 < 排量 ≤ 3升)</option>
                                    <option value= "E" <#if vehicle.engineDisplacement?? && ("${vehicle.engineDisplacement}" == "E")>selected</#if> >E (3升 < 排量 ≤ 4升)</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="col-md-5">
                                <label class="control-label" for="obdSN">obd序列号：</label>
                            </div>
                            <div class="col-md-7">
                                <input type="text" id="obdSN" name="obdSN" value="<#if vehicle.obdSN??>${vehicle.obdSN}</#if>" class='form-control'>
                            </div>
                        </div>
                    </div>
                    <div class="row" style="margin-top: 1%">
                        <div class="col-md-3 form-group required">
                            <div class="col-md-5">
                                <label class="control-label" for="brand">品牌：</label>
                            </div>
                            <div class="col-md-7">
                                <select onchange="changeBrand(this.id)" class="form-control select2" type="search" id="brand${vehicle_index}" name="brand${vehicle_index}">
                                    <option></option>
                                    <#if vehicleModels??>
                                        <#list vehicleModels as vehiclesModel>
                                            <#if vehicle.model?? && "${vehicle.model.brand}" == "${vehiclesModel}">
                                            <option value= "${vehiclesModel}" selected>${vehiclesModel}</option>
                                            <#else>
                                                <option value= "${vehiclesModel}">${vehiclesModel}</option>
                                            </#if>
                                        </#list>
                                    </#if>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-3 form-group required">
                            <div class="col-md-5">
                                <label class="control-label" for="line">车系：</label>
                            </div>
                            <div class="col-md-7">
                                <select onchange="changeLine(this.id)" class="form-control select2" type="search" id="line${vehicle_index}" name="line${vehicle_index}">
                                    <option></option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="row" style="margin-top: 1%">
                        <div class="col-md-6 form-group required">
                            <div class="col-md-3">
                                <label class="control-label" for="version">车型：</label>
                            </div>
                            <div class="col-md-9">
                                <select class="form-control select2" type="search" id="version${vehicle_index}" name="version${vehicle_index}">
                                    <option></option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="row" style="margin-top: 1%">
                        <div class="col-md-3">
                            <div class="col-md-5">
                                <label class="control-label" for="mileage">已行驶里程：<font color="red">*</font></label>
                            </div>
                            <div class="col-md-7">
                                <input type="text" id="mileage" name="mileage" value="${vehicle.mileage?c}" onblur='validate(this)' class='form-control'>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="col-md-5">
                                <label class="control-label" for="mileageUpdatedDate">更新日期：</label>
                            </div>
                            <div class="col-md-7">
                            <#--<@form.textInput "staff.entryDate" "class='form-control Wdate' readonly" "text" "入职日期：" true/>-->
                                <input type="text" id="mileageUpdatedDate${vehicle_index}" name="mileageUpdatedDate${vehicle_index}"
                                       value="<#if vehicle.mileageUpdatedDate??>${vehicle.mileageUpdatedDate.toString().substring(0,10)}</#if>" class='form-control Wdate' readonly>
                            </div>
                        </div>
                    </div>
                    <div class="row" style="margin-top: 1%">
                        <div class="col-md-3">
                            <div class="col-md-5">
                                <label class="control-label" for="lastMaintenanceMileage">上次保养里程：<font color="red">*</font></label>
                            </div>
                            <div class="col-md-7">
                                <input type="text" id="lastMaintenanceMileage" name="lastMaintenanceMileage" value="${vehicle.lastMaintenanceMileage?c}" onblur='validate(this)' class='form-control'>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="col-md-5">
                                <label class="control-label" for="lastMaintenanceDate">更新日期：</label>
                            </div>
                            <div class="col-md-7">
                                <input type="text" id="lastMaintenanceDate${vehicle_index}" name="lastMaintenanceDate${vehicle_index}"
                                       value="<#if vehicle.lastMaintenanceDate??>${vehicle.lastMaintenanceDate.toString().substring(0,10)}</#if>" class='form-control Wdate' readonly>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                        <legend>&nbsp;</legend>
                        </div>
                    </div>
                </div>

            </#list>
        </#if>
        <div id="addBtn" class="row" style="margin-top: 1%">
            <div class="col-md-offset-3 col-md-4">
                <div class="col-md-offset-6 col-md--6">
                    <input class="btn btn-primary" onclick="addVehicleInfo();" value="+添加车辆信息"></input>
                </div>
            </div>
        </div>
    </div>
    <div class="row" style="margin-top: 1%">
        <div class="col-md-offset-3 col-md-3">
                <#--<input class="btn_erp button button-raised button-highlight button-pill" type="submit"-->
                       <#--onclick="getCarData()" img src="/stylesheets/images/erp/save.png" value="确 认 保 存"></input>-->
            <@form.btn_save 'onclick="getCarData()"' "确 认 保 存" />
        </div>
    </div>
    </form>
    </@main.frame>
</#escape>