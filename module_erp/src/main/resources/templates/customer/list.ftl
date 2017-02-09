<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>
<script src="/javascripts/jquery-ui-1.9.2.min.js" type="text/javascript"></script>
<script src="/javascripts/jquery.ui.widget.js" type="text/javascript"></script>
<script src="/javascripts/cndate.js" type="text/javascript"></script>
<script type="text/javascript">
    var exportMobile = "";   //生效的查询手机条件
    var exportCarNum = "";   //生效的查询车牌号条件
    $('#collapseShop').collapse('show');
    $(function () {
        if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_CUSTOMER)?c}) {
            window.location = "/noauthority";
        } else {
            showList();
        }

    });

    function showList() {
     var url = encodeURI('/customer/list/data?tel=' + $("#tel").val() + "&carNum=" + $("#carNum").val(), "UTF-8");
    $("#customerList").jqGrid({
        url: url,
        colModel: [
            { label: '车牌号', name: '0.plateNumber', width: 70, align:"center" },
            { label: '姓名', name: '1', width: 50, align:"center"},
            { label: '性别', name: '2', width: 40, align:"center",
                formatter:function(cellvalue, options, rowObject){
                    if(cellvalue == "0") {
                        return "男";
                    }else if(cellvalue == "1"){
                        return "女";
                    }else {
                        return "";
                    }
                }},
            { label: '联系方式', name: '3', width: 30,align:"center"},
            { label: '品牌', name: '0.brandName', width: 30,align:"center"},
            { label: '车型', name: '0.versionName', width: 80, align:"center"
            },
            { label: '操作', name: '3', width: 50 ,align:"center",
                formatter:function(cellvalue, options, rowObject){
                    var modify = "<a onclick=\"update("+ cellvalue +")\" href='#' style='text-decoration:underline;color:blue'>"+"修改"+"</a>";
                    var plateNumber = rowObject["0"];
                    if (plateNumber != "" && plateNumber != null) {
                        plateNumber=plateNumber["plateNumber"];
                        modify += "    ";
                        modify += "<a onclick=\"toSalenote(\'"+ plateNumber +"\')\" href='#' style='text-decoration:underline;color:blue'>"+"开单"+"</a>";
                    }
                    return modify;
                }
            }
        ],
        rownumbers: true ,
        gridComplete : function(){
            exportMobile = $("#tel").val() ;
            exportCarNum = $("#carNum").val();
//            alert(exportMobile + ";;;" + exportCarNum);
        }
    });
}

    function queryList(){
        var url = '/customer/list/data?tel=' + $("#tel").val() + "&carNum=" + $("#carNum").val();
        url = encodeURI(url,"UTF-8");
        jQuery("#customerList").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
    }

    function update(tel) {
        window.location = "/customer/edit?tel=" + tel;
    }
    function tosave() {
        window.location = "/customer/tosave";
    }

    function toSalenote(plateNumber) {
        window.location = encodeURI("/salenote/searchcustominfo?plateNumber=" + plateNumber, "UTF-8");
    }

    function deleted(tel){
        $.ajax( {
            url:"/customer/savedelete",
            data:{
                tel : tel
            },
            type:'post',
            success:function(data) {
                if(data.msg == true ){
                    alert("删除成功！");
                    queryList();
                }
            },
            error : function() {
                alert("操作异常！");
            }
        });
    }

    function uploadExcel (){
        var a = $("#excelUpload").val()
        if(a == null || a == ""){
            alert("请选择文件!");
            return ;
        }
        $("#uploadForm").submit();
    }

    function exportExcel (){
        window.location = encodeURI("/vehicleinfo/excel/export", "UTF-8");
//        window.location = encodeURI("/erpuser/excel/export?tel=" + exportMobile + "&carNum=" + exportCarNum, "UTF-8");
    }
</script>

<style></style>
<legend>顾客管理 -> 顾客信息查询</legend>
<div class="row">
    <div class="col-md-5">
        <div class="col-md-2">
            <label class="control-label"> 手机： </label>
        </div>
        <div class="col-md-3">
            <input class="form-control" type="search" id="tel" name="tel" value=""/>
        </div>
        <div class="col-md-2">
            <label class="control-label">车牌号：</label>
        </div>
        <div class="col-md-4">
            <input class="form-control" type="search" id="carNum" name="carNum" value=""/>
        </div>
    </div>
    <div class="col-md-3">
        <@form.btn_search "onclick='queryList()'" "搜 索" />
        <@form.btn_add "onclick='tosave()'" "新 增" />
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <@form.btn_add "onclick='uploadExcel()'" "导 入" />


    </div>
    <div class="col-md-4" >

        <form action="/erpuser/excel/upload" enctype="multipart/form-data" method="post" id="uploadForm">
            <input type="file" id="excelUpload" style="width: 200px;" name="file"  ><#--<input type="submit" value="提交" >-->

        </form>

    </div>
</div>
<br>
<table id="customerList" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="toolBar"></div>
</br>
<div class="row">
    <div class="col-md-5"></div>
    <div class="col-md-2" >
        <@form.btn_print "onclick='exportExcel()' align='center'" "客 户 导 出" />
    </div>
</div>
</@main.frame>
</#escape>