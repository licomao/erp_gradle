<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />

<#escape x as x?html>
  <@main.frame>

<script type="text/javascript">
        <#if pdCheck??>
            <#if pdCheck>
            alert("您已经很久没盘点库存了，请盘点完再来操作！");
            window.history.back();
            </#if>
        </#if>
    $('#collapsePayment').collapse('show');
    $().ready(function () {
        if (!${user.checkAuthority(AUTHORITY.MANAGE_ORG_SEARCHCUSTOMINFO)?c}) {
            window.location = "/noauthority"
        } else {
            //预约单
            var number = "${plateNumber}";
            if (number != null && number != "") {
                $("#plateNumber").val(number);
            }
            searchCustomer();
            showCustomCardList();
//        $('input').attr("disabled",true)//将input元素设置为disabled
//        $('input').attr("disabled",false)//去除input元素的disabled属性

            $("#next").attr("disabled", true);
            $("#newcustomer").attr("disabled", true)
        }
    });

function showCustomCardList() {
    var plateNumber = $("#plateNumber").val();
    $("#customCardList").jqGrid({
        postData: { plateNumber: plateNumber},
        url: '/salenote/customersuite/list/data',
        colModel: [
            { label: 'id',index:'id', name: 'id',  hidden:true },
//            { label: '',index:'radio', name: '', width: 4 ,
//                formatter:function(cellvalue, options, rowObject) {
//                    return '<input type="radio" style="　text-align: center; height:20px; line-height:20px" name="customRadio" />';
//            }},
            { label: '会员卡名称', index:'suite.name', name: 'suite.name',align:"center", width: 60 },
            { label: '套餐购买门店', index:'shop.name', name: 'shop.name',align:"center", width: 40 },
            { label: '套餐购买日期', index:'startDate', name: 'startDate',align:"center", width: 60,
                formatter:function(cellvalue, options, rowObject) {
                    if(cellvalue!=null){
                        return formatterDateWithSecond(cellvalue);
                    } else {
                        return "";
                    }
                }
            },
            { label: '会员卡描述', index:'suite.description', name:'suite.description',align:"center", width:100 }
        ],
        multiselect:true
    });
}

function searchCustomer() {
    var plateNumber = $("#plateNumber").val();

    if(plateNumber == null || plateNumber =="") {
//        alert("请输入车牌号");
        return;
    }
    else {
        $.ajax({
            url: "/salenote/list/data",
            data: {plateNumber: plateNumber},
            dataType:"json", //返回的数据类型,text 或者 json数据，建议为json
            success: function (ret) { //若Ajax处理成功后的回调函数，text是返回的页面信息
                var customerERPProfile = ret;
                $("#name").val(customerERPProfile.realName);
                $("#tel").val(customerERPProfile.customer.mobile);

                $("#customCardList").jqGrid('setGridParam', {
                    postData: {plateNumber: plateNumber},
                    page: 1
                }).trigger("reloadGrid");
                $("#next").attr("disabled",false);
                $("#newcustomer").attr("disabled",true)
//                $("#next").show();
//                $("#newcustomer").hide();
            },
            error: function(err) {
                alert("未找到客户信息，请新增该客户。");
                $("#next").attr("disabled",true);
                $("#newcustomer").attr("disabled",false)
            }
        });
    }
}

function gotoCustomInfo() {
    var plateNumber = $("#plateNumber").val();
    var row = $("#customCardList").jqGrid('getGridParam','selrow');

    if(row != null && row > 0) {
        var rowData = $("#customCardList").jqGrid('getRowData',row);
        var id = rowData["id"];
        window.location = encodeURI("/salenote/customcard/tosave?plateNumber="+ plateNumber + "&customsuiteid=" + id, "UTF-8");;
    } else {

        if($("#preSaleId").val() != "" && $("#preSaleId").val() != null) {
            window.location = encodeURI("/salenote/salenoteapp?plateNumber="+ plateNumber + "&preSaleId=" + $("#preSaleId").val(), "UTF-8");;
        } else {
            window.location = encodeURI("/salenote/salenote?plateNumber="+ plateNumber, "UTF-8");;
        }
    }
}

    function addcustomer() {
        window.location = "/customer/tosave/";
    }

</script>

  <legend>销售开单 -> 顾客及会员信息查询</legend>
  <input type="hidden" name="preSaleId" id="preSaleId"  value="${preSaleId}"/>

  <div class="row">
      <div class="col-md-offset-2 col-md-1">
          <label class="control-label">车牌号:</label>
      </div>
      <div class="col-md-2">
          <input id="plateNumber" class="form-control" type="text" name="plateNumber" onblur="searchCustomer()" onkeydown='if
          (event.keyCode==13){searchCustomer()}'>
      </div>
      <div class="col-md-2">
          <p style="font-size: 10px;color: red">注:输入完毕后请按回车键进行查询或者鼠标点击空白区域也可以进行查询</p>
      </div>
  </div>
  <div class="row" style="margin-top: 1%">
      <div class="col-md-offset-2 col-md-1">
          <label class="control-label">姓名:</label>
      </div>
      <div class="col-md-2">
        <input class="form-control" type="text" name="name" id="name" disabled>
      </div>
      <div class="col-md-1">
          <label class="control-label">手机号:</label>
      </div>
      <div class="col-md-2">
          <input class="form-control" type="text" name="tel " id="tel" disabled>
      </div>
  </div>
  <div class="row" style="margin-top: 1%; margin-bottom: 1%">
      <div class="text-center  col-md-10">
          <input id="next" class="button button-raised button-highlight button-pill" onclick="gotoCustomInfo()" value="下一步"/> &nbsp; &nbsp;
          <input id="newcustomer" class="button button-raised button-highlight button-pill" onclick="addcustomer()" value="新增客户信息"/>
      </div>

  </div>
  <div class="col-md-offset-2 col-md-6">
      <table id="customCardList" class="scroll" cellpadding="0" cellspacing="0"></table>
      <div id="toolBar"></div>
  </div>
  </@main.frame>
</#escape>