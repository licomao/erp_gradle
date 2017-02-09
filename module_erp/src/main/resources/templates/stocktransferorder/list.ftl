<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
<@main.frame>

<script type="text/javascript">
    $('#collapseStock').collapse('show');
    $(function () {
        if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_STOCKTRANSFERORDER)?c}) {
            window.location = "/noauthority"
        } else {
            var url = "/stocktransferorder/list/data?orderNumberView=" + $("#orderNumberView").val()
                    + "&deleted="+ $("#deleted").val()
                    + "&inShop.id=" + $("#shopId").val() + "&transferDate=" + $("#transferDate").val();
            showList(url);

            $('.Wdate').datepicker();
            $('.Wdate').datepicker("option",$.datepicker.regional["zh-TW"]);
            $('#transferDate').datepicker( "setDate", $('#transferDate').val());
        }
    });


    function showList(url) {
        $("#gridBody").jqGrid({
            url: url,
            colModel: [
                { label: '调拨单号', name: 'orderNumberView', width: 80, align:"center" },
                { label: '调入门店', name: 'inShop.name', width: 60, align:"center"},
                { label: '调出门店', name: 'outShop.name', width: 60, align:"center"},
                { name: 'outShop', hidden:true},
                { name: 'inShop', hidden:true},
                { label: '申请人', name: 'erpUser.realName', width: 50,align:"center"},
                { label: '申请日期', name: 'transferDate', width: 50, align:"center",
                    formatter:function(cellvalue, options, rowObject) {
                        if(cellvalue!=null){
                            return formatterDateWithSecond(cellvalue);
                        } else {
                            return "";
                        }
                    }
                },
                { label: '入库日期', name: 'stockDate', width: 50, align:"center",
                formatter:function(cellvalue, options, rowObject) {
                        if(cellvalue!=null){
                            return formatterDateWithSecond(cellvalue);
                        } else {
                            return "";
                        }
                    }
                },
                { label: '单据状态', name: 'deleted', width: 30, align:"center",
                    formatter:function(cellvalue, option, rowObject){
                        if (cellvalue == false){
                            return "有效";
                        }else{
                            return "无效";
                        }
                    }
                },
                { label: '调拨状态', name: 'transferStatus', width: 40, align:"center",
                    formatter:function(cellvalue, option, rowObject){
                        switch (cellvalue) {
                            case 0:
                                return "申请中";
                            case 1:
                                return "已退回";
                            case 2:
                                return "在途中";
                            case 3:
                                return "已入库";
                        }
                    }
                },
                { label: '操作', name: 'id', width: 60, align:"center",
                    formatter:function(cellvalue, options, rowObject){
                        var transferStatus = rowObject["transferStatus"];
                        var inShop = rowObject["inShop"];
                        var outShop = rowObject["outShop"];
                        var loginShop = ${SHOP.id};
                        var doType = 1 ;
                        var doMsg = "作废";
                        if (rowObject["deleted"]) {
                            doType = 0;
                            doMsg = "启用";
                        }
                        var view  = "<a onclick='toEdit("+ cellvalue +",4)' href='#' style='margin-left:30px;text-decoration:underline;color:blue'>"+"查看明细"+"</a>";
                        if(transferStatus == 0 || transferStatus == 1){
                            if (inShop.id == loginShop) {
                                return "<a onclick='toEdit("+ cellvalue +",0)' href='#' style='text-decoration:underline;color:blue'>"+"修改"+"</a>"
                                   + "<a onclick='deleteRow("+ cellvalue +","+options.rowId+","+doType+")' href='#' style='margin-left:30px;text-decoration:underline;color:blue'>" + doMsg +"</a>" + view;
                            } else if (outShop.id == loginShop) {
                                return "<a onclick='toEdit("+ cellvalue +",2)' href='#' style='text-decoration:underline;color:blue'>"+"答复"+"</a>" + view;
                            }
                        } else if (transferStatus == 2) {
                            if (inShop.id == loginShop) {
                                return  "<a onclick='toEdit("+ cellvalue +",3)' href='#' style='text-decoration:underline;color:blue'>"+"入库"+"</a>" + view;
                            } else {
                                return "<a onclick='toEdit("+ cellvalue +",4)' href='#' style='text-decoration:underline;color:blue'>"+"查看明细"+"</a>";
                            }
                        } else {
                            return "<a onclick='toEdit("+ cellvalue +",4)' href='#' style='text-decoration:underline;color:blue'>"+"查看明细"+"</a>";
                        }
                    }

                }

            ],

            loadError: function(xhr,status,error){
                alert(status + " loading data of " + $(this).attr("id") + " : " + error );    },
            //multiselect:true,
            rownumbers: true
        });
    }

    function deleteRow(id, rowId ,doType) {
//        $("#gridBody").jqGrid("delRowData",rowId);
        $.get("/stocktransferorder/deleted?id=" + id + "&type=" + doType, function (data) {
            if (doType ==1){
                alert("作废成功");
            } else {
                alert("启用成功");
            }
            $("#gridBody").jqGrid("delRowData", rowId);
        });
    }

    function toEdit(id,doType) {
        window.location = "/stocktransferorder/edit?id=" + id + "&doType=" + doType;
    }

    function queryList(){
        var url = "/stocktransferorder/list/data?orderNumberView=" + $("#orderNumberView").val()
                + "&deleted="+ $("#deleted").val()
                + "&inShop.id=" + $("#shopId").val() + "&transferDate=" + $("#transferDate").val();
        jQuery("#gridBody").setGridParam({url:url}).trigger("reloadGrid", [{ page: 1}]);
    }

    function toTransfer(){
        window.location = "/stocktransferorder/form";
    }


</script>


<div class="row">
    <legend>库存盘点管理 -> 库存调拨</legend>
    <form class="" id="fm" action='<@spring.url relativeUrl = "/fixedasset/tosave"/>' method="GET">
        <div class="col-md-7">
            <label  class="control-label">调拨单号: </label>
            <input type="text" name="orderNumberView" id="orderNumberView">&nbsp;

            &nbsp;
            <label  class="control-label">调拨门店: </label>
            <select name="shopId" id="shopId">
                <#list shops as shop>
                    <option value="${shop.id}">${shop.name}</option>
                </#list>
            </select>
            &nbsp;
            <label  class="control-label">申请日期: </label>
            <input type="text" class="Wdate" name="transferDate" id="transferDate" readonly>
            &nbsp;
            <label  class="control-label">单据状态: </label>
            <select name="deleted" id="deleted">
                <option value="false">有效</option>
                <option value="true">无效</option>
            </select>

        </div>
        <div class="col-md-4">
            <@form.btn_search "onclick='queryList();'" "查 询"/>&nbsp;&nbsp;&nbsp;
            <@form.btn_pages "onclick='toTransfer();'" "申请调拨"/>
        </div>
    </form>
</div>
<br>
<table id="gridBody" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="toolBar"></div>

</@main.frame>
</#escape>