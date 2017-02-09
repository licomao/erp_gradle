<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />


<#escape x as x?html>
  <@main.frame>
<script>
    $('#collapsePayment').collapse('show');
    $(function () {
        getOperationPrice();
        showWorkHours();
        showProject();
    });

    var operationPrice = 0;

    function getOperationPrice() {
        $.ajax({
            url: '/salenote/getoperationprice',
            dataType:"json",
            type:"post",
            success: function(ret) {
                operationPrice = ret;
            }
        });
    }

    //订单详情
    function showWorkHours() {
        $("#workHoursList").jqGrid({
//            url: '/salenote/workhours',
            pager : '#gridpager',
            colModel: [
                { label: 'ID', name: 'id', hidden:true,align:"center"},
                { label: '作业项目', name: 'name', width: 80,align:"center"},
                { label: '工时', name: 'laborHours', width: 80,align:"center"},
                { label: '单价', name: 'cost', width: 80,align:"center",
                    formatter:function(id) {
                        return operationPrice;
                    }
                },
                { label: '总工时费', name: 'sum', width: 80,
                    formatter:function(cellvalue, options, rowObject) {
                        var costs = rowObject["laborHours"] * operationPrice;

                        return  '<input type="text" id="sum-'+rowObject['id'] +'" value="'+ costs +'" class="content"' +
                                ' ' +
                                'size="13" maxlength="20" onblur="getAmount()" />';}
                }
            ],
            ondblClickRow: function(id){//双击行
                var rowData = $("#workHoursList").jqGrid('getRowData',id);
                $("#workHoursList").jqGrid("delRowData", id);
                //获取合计总和
                getAmount();
            },
            toolbar: [false,"both"]
        });
    };

    function showProject() {
        var name = $("#name").val();
        var engineDisplacement = $("#engineDisplacement").val();
        var operationType = $("#operationType").val();
        $("#projectList").jqGrid({
            postData: { name: name, operationType: operationType, engineDisplacement: engineDisplacement},
            url: '/salenote/project',
            colModel: [
                { label: 'ID', name: 'id', hidden:true},
                { label: '作业项目', name: 'name', width: 60 ,align:"center"},
                { label: '工时(H)', name: 'laborHours', width: 30 ,align:"center"},
                { label: '排量', name: 'carLevel', width: 30 ,align:"center"},
                { label: '分类', name: 'strType', width: 50 ,align:"center"}
            ],
            ondblClickRow: function(id){//双击行
                var row = $("#workHoursList").getGridParam("reccount") + 1;
                var rowData = $("#projectList").jqGrid('getRowData',id);
                var obj = $("#workHoursList").jqGrid("getRowData");
                var flag = true;

                jQuery(obj).each(function(){
                    if( this['id'] == rowData['id'] ){
                        alert("该商品已经选择了");
                        flag = false;
                    }
                });
                if (flag) {
                    $("#workHoursList").addRowData(row,
                            {
                                "id": rowData.id,
                                "name": rowData.name,
                                "laborHours": rowData.laborHours
                            },
                            "last");
                    //获取合计总和
                    getAmount();
                }
            }
        });
    }

    function reloadGrid() {
        var name = $("#name").val();
        var operationType = $("#operationType").val();
        var engineDisplacement = $("#engineDisplacement").val();
        $("#projectList").jqGrid('setGridParam',{
            postData: { name: name, operationType: operationType, engineDisplacement: engineDisplacement}
        }).trigger("reloadGrid");
    }

    //获取总和
    function getAmount(){
        var amount=0;
        var obj = $("#workHoursList").jqGrid("getRowData");
        jQuery(obj).each(function(){
            var sum = $("#sum-" + this["id"]).val();//总工时费用
                amount +=parseFloat(sum);
        });
        $("#sum").html(amount);
    }

    //改变现金和post机金额
    function validate(obj){
        var reg = new RegExp("^[0-9]+([.]{1}[0-9]+){0,1}$");
        if(!reg.test(obj.value)){
            alert("请输入数字!");
            obj.focus();
            return;
        }
    }

    //获取界面数据集，传到后台
    function getWorkHoursInfo() {
        var lines = $("#workHoursList").getGridParam("reccount");        //检测是否选择商品
        if(lines <= 0) {
            alert("请选择至少一个作业项目。");
            return;
        }

        var cash = document.getElementById("payment.cashAmount").value;
        if(cash == null || cash == "") {
            document.getElementById("payment.cashAmount").focus();
            alert("请输入现金额。");
            return;
        }

        var post = document.getElementById("payment.posAmount").value;
        if(post == null || post == "") {
            document.getElementById("payment.posAmount").focus();
            alert("请输入POS额。");
            return;
        }

//        var app = document.getElementById("payment.appAmount").value;
//        if(parseFloat(cash) + parseFloat(post) + parseFloat(app) != parseFloat($("#sum").html())) {
        if(parseFloat(cash) + parseFloat(post) != parseFloat($("#sum").html())) {
            alert("输入金额总和与合计不符，请确认。");
            return;
        }

        var oneData = "";
        for (var i = 1; i <= lines; i++) {
            var rowData = $("#workHoursList").jqGrid('getRowData', i);
            oneData += "" + rowData['id'] + "," + $("#sum-" + rowData['id']).val();
            oneData += ";";
        }

        $("#workhoursdata").val(oneData);
        $("#fm").submit();
    }

    function deleteRows() {
        var selectedIds = $("#workHoursList").jqGrid("getGridParam", "selarrrow");

        if(eval(selectedIds) == 0) {
            alert("请选择行。")
        }else {
            for (i=selectedIds.length; i > 0; i--) {
                $("#workHoursList").jqGrid("delRowData", selectedIds[i-1]);
            }
        }
    }

    function back() {
        window.location = "/salenote/shownotsettleinfo?saleNoView=" + $("#saleNo").val();
    }

</script>
  <legend>销售开单 -> 项目内容</legend>
  <form id="fm" class="" action='<@spring.url relativeUrl = "/salenote/workhours/save"/>' method="post">
  <input type="hidden" id="workhoursdata" name="workhoursdata"/>
  <@form.labelAndTextInput "settleOrder.id" "class='form-control'" "hidden" ""/>

  <spring:bind path="settleOrder">
  <div class="row" style="margin-top: 1%">
      <div class="col-md-offset-6 col-md-1">
          <label class="control-label">销售单号</label>
      </div>
      <div class=" col-md-2">
          <label id="saleID"class="control-label" name="saleNoView" id="saleNoView"><#if settleOrder.saleNoView??>${settleOrder.saleNoView}</#if></label>
          <input type="hidden" id="saleNo" name="saleNo" value="<#if settleOrder.saleNoView??>${settleOrder.saleNoView}</#if>"/>
      </div>
      <#--<div class="col-md-2">-->
          <#--<button class="btn btn-primary" onclick="">打印委托书（销售单）</button>-->
      <#--</div>-->
      <div class="col-md-1">
          <#--<button class="btn btn-primary" onclick="">上一步</button>-->
      </div>
  </div>

  <!-- 中间表格 -->
  <div class="row">
      <div>
      <table id="workHoursList" class="scroll" cellpadding="0" cellspacing="0"  border="1px">
      </table>
      <div id="toolBar"></div>
      </div>
  </div>

  <div class="row" style="border:1px #000000 solid; padding-top: 0.5%;padding-bottom: 0.5%">
      <div class="col-md-4">
          <div class=" col-md-6">
              <label class="control-label">合计：</label>
          </div>
          <div class=" col-md-6">
              <label class="control-label" id="sum" name="sum"></label>
          </div>
      </div>
      <div class="col-md-3">
          <div class="col-md-3">
              <label class="control-label">现金：</label>
          </div>
          <div class=" col-md-3">
              <input class="form-control" type="text" name="payment.cashAmount" id="payment.cashAmount" onblur="validate(this)" />
          </div>
          <div class="col-md-3">
              <label class="control-label">pos机：</label>
          </div>
          <div class=" col-md-3">
              <input class="form-control" type="text" name="payment.posAmount" id="payment.posAmount" onblur="validate(this)" />
          </div>
      </div>
      <#--<div class="col-md-5">-->
          <#--<div class="col-md-2">-->
              <#--<label class="control-label">App费用：</label>-->
          <#--</div>-->
          <#--<div class=" col-md-2">-->
              <#--<input class="form-control" type="text" name="payment.appAmount" id="payment.appAmount" value="0.00" readonly="readonly">-->
          <#--</div>-->
          <div class=" col-md-3">
              <@form.btn_save 'id="settle" onclick="getWorkHoursInfo();"' "结 算" />
              &nbsp;
              <@form.btn_back "onclick='back()'" "返 回" />
          </div>
      <#--</div>-->
  </div>


  <div class="row" style="margin-top: 1%">
    <div class=" col-md-8">
     <div class="row" style="margin-bottom: 1%">
         <div class=" col-md-2">
           <label class="control-label">作业项目：</label>
         </div>
         <div class=" col-md-2">
             <input class="form-control" type="text" name="name" id="name">
         </div>
         <div class=" col-md-2">
             <label  class="control-label">分类: </label>&nbsp;
         </div>
         <div class=" col-md-3">
             <select name="operationType" id="operationType" class="form-control">
                 <option value="0">请选择</option>
                 <option value="1">维护</option>
                 <option value="2">大修和全车喷漆</option>
                 <option value="3">发动机机械</option>
                 <option value="4">发动机电气</option>
                 <option value="5">变速箱</option>
                 <option value="6">转向系统</option>
                 <option value="7">悬挂系统</option>
                 <option value="8">驱动桥</option>
                 <option value="9">制动系统</option>
                 <option value="10">电气</option>
                 <option value="11">空调</option>
                 <option value="12">钣金</option>
                 <option value="13">喷漆</option>
             </select>
         </div>
         <input type="hidden" name="engineDisplacement" id="engineDisplacement" value="${engineDisplacement}" />
         <div class=" col-md-2">
             <@form.btn_search "onclick='reloadGrid()'" "搜 索" />
         </div>
     </div>
      <div class="row">
          <table id="projectList" class="scroll" cellpadding="0" cellspacing="0"></table>
          <div id="toolBar"></div>
      </div>
    </div>
  </div>
  </spring:bind>
    </form>
  </@main.frame>
</#escape>