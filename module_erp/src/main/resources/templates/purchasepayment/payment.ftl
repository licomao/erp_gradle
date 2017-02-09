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
        $('#collapsePurchase').collapse('show');
        $(function(){
            $('.Wdate').datepicker();
            $('.Wdate').datepicker("option", $.datepicker.regional["zh-TW"]);
            $('#payDate').datepicker("setDate", new Date());
//            $('#payDate').datepicker( "setDate", $('#payDateHid').val());
            changeJieYu($("#payment"));
        });



        function validate(obj, id, price){
            var uid = id + "_" + price;
            var reg = new RegExp("^[0-9]*$");
            var result = 0;
            var oldTotalPrice = $("input[id='totalPrice_" + uid + "']").val();
            if(!reg.test(obj.value)){
              obj.focus();
              $(obj).val(0);
//                var id1 = "totalPrice_"+uid;
              $("input[id='totalPrice_" + uid +"']").val(0);
            }else{
                result = obj.value * $("input[id='currentPrice_" + uid + "']").val();// $("#currentPrice_"+uid).val();
//              $("#totalPrice_"+uid).val(result);

                $("input[id='totalPrice_" + uid + "']").val(result);
            }
        }

        function validateDouble(obj){
            var reg = /^[0-9]+([.]{1}[0-9]{1,2})?$/;
            if(!reg.test(obj.value)){
//                $(obj).val(0);
                alert("请输入金额数字!")
                obj.focus();
            }
            changeJieYu(obj);
        }

        function changeJieYu(obj){
            var sy = $("#costSum").val() - $("#payCost").val();
            var unspentBalance = sy - $("#payment").val() - $("#deductionPayment").val();
            if(unspentBalance.toFixed(2) < 0){
                alert("付款金额与实际需付金额不符，请重新填写!");
                obj.focus();
                return;
            }
            $("#unspentBalance").val(unspentBalance.toFixed(2));
        }

        function subForm(){
            if ($("#payType").val() == 2){
                if($("#payAccount").val() == ""){
                    alert("付款账号不准未空");
                    return;
                }
            }
            if(confirm("是否确认保存付款信息，确认后不可修改!")){
                $("#fm").submit();
            }
        }
    </script>
    <div class="row">
        <div class="col-md-10">
            <legend>采购单管理 -> 采购付款单记录填写</legend>
            <div id="actions" class="form-action">
                <form class="" id="fm" action='<@spring.url relativeUrl = "/purchasepayment/save"/>' method="post">
                    <div class="col-md-10">
                        <div class="row">
                            <div class="col-md-4">
                                <@form.textInput "purchasePayment.purchaseOrder.orderNumberView" "class='form-control'readonly " "text" "采购单号"  />
                                <@form.textInput "purchasePayment.purchaseOrder.id" "class='form-control'readonly " "hidden" ""  />
                            </div>
                            <div class="col-md-4 col-md-offset-1">
                                <@form.textInput "purchasePayment.supplier.name" "class='form-control' readonly " "text" "供应商"  />
                                <@form.textInput "purchasePayment.supplier.id" "class='form-control' readonly " "hidden" ""  />
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-4">
                                <label class="control-label"  >付款方式 </label><br>
                                <select name="payType" class="form-control" id="payType">
                                    <option value="1">现金</option>
                                    <option value="2">银行汇款</option>
                                    <option value="3">支票</option>
                                </select>
                            </div>
                            <div class="col-md-4 col-md-offset-1">
                                <label class="control-label"  >付款日期</label><br>
                                <input type="text" name="payDate" id="payDate" readonly value="" class="Wdate">
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-md-4">
                                <label class="control-label"  >应付总额</label><br>
                                <input class="form-control" name="accountPayable" id="costSum" readonly value="${purchasePayment.purchaseOrder.costSum?c}"/>
                            </div>
                            <div class="col-md-4 col-md-offset-1">
                                <label class="control-label"  >已付金额</label><br>
                                <input type="text" id="payCost" value="${unspentBalance?c}" readonly class="form-control">
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-md-4">
                                <label class="control-label"  >本次付款</label><br>
                                <input class="form-control" id="payment" name="payment" onblur="validateDouble(this);"  value="0"/>
                            </div>
                            <div class="col-md-4 col-md-offset-1">
                                <label class="control-label"  >结余款</label><br>
                                <input type="text" id="unspentBalance" readonly name="unspentBalance" value="" class="form-control">
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-md-4">
                                <label class="control-label"  >抵扣付款</label><br>
                                <input class="form-control" id="deductionPayment" name="deductionPayment" onblur="validateDouble(this);"  value="0"/>
                            </div>
                            <div class="col-md-4 col-md-offset-1">
                                <label class="control-label"  >付款账号</label><br>
                                <input type="text" id="payAccount" name="payAccount" value="" class="form-control">
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-md-8" >
                                <@form.textArea "purchasePayment.payWay" "class='form-control' style='height:90px;'" "付款去向"/>
                            </div>
                        </div>

                        <br>
                        <div class="span2 text-center">
                            <@form.btn_save "onclick='subForm();'" "确认付款"/>
                        </div>
                    </div>

                </form>
            </div>
        </div>
    </div>
    </@main.frame>

</#escape>