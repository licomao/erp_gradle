<#--<#import "/macros/generalFrame.ftl" as general />-->
<#--<#import "/macros/formMacros.ftl" as form />-->
<#--<#import "/spring.ftl" as spring />-->
<#import "../macros/mainFrame.ftl" as main />
<#import "../macros/formMacros.ftl" as form />
<#import "/spring.ftl" as spring />
<#escape x as x?html>
    <@main.frame>
    <#--<@general.frame title="登录">-->

    <script type="text/javascript">
        var matx;  //控件
        var fpcHandle;  //指纹缓冲区
        var myString = "";

        var machineNum = 0;

        var zkInfoArr = [];

        var locator = new ActiveXObject ("WbemScripting.SWbemLocator");
        var service = locator.ConnectServer(".");

        function prList(){
            window.location='/fingerprint/list';
        }

        function t1() {
            try {
                matx = document.getElementById("myativx");
                matx.FPEngineVersion = "9";
                matx.SensorIndex = 0;
                matx.EnrollCount = 2;   //登记指纹的次数
                if (matx.InitEngine() == 0) {
//                    $("#serial").val(matx.SensorSN);
                    $("#serialLabel").text(matx.SensorSN);
                    //alert("初始化成功!");
                } else {
                    alert("初始化控件失败了");
                    return ;
                }
                $.post('/fingerprint/save',{sensorSN:matx.SensorSN},function(data){

                    if(data.result){
                        alert("录入成功");
                        machineNum = 0;
                    }/*else if(!data.result){
                        alert("")
                    }*/
                },'json');
            }
            catch (e) {
                alert(e.message);
            }//alert("成功");
        }

        //**********************************************//
        function insert(){
            var info="<table border=1>";
            var properties = service.ExecQuery("SELECT * FROM  Win32_USBHub");
            var e = new Enumerator (properties);
            info+="<tr bgcolor='#CDEDED' style='font-weight: bold;'><td width='450'>ZK4500指纹机信息：</td></tr>";
            for (;!e.atEnd();e.moveNext ()) {
                var p = e.item ()
                var iscontaintest = (p.Caption).indexOf("ZK4500") == -1 ? false : true;
                if(iscontaintest){

                    machineNum += 1;

                    var zkInfo = {};
                    var strSN;
                    var uSerialNum;
                    var p = e.item ();
                    var sn=p.DeviceID;
//                GetUsbSN(sn)
                    strSN=GetUsbSN(sn);

                    uSerialNum=(strSN.usbSn).substring(8);
                    info+="<tr style='color:red'><td >Caption：" + p.Caption + "</td></tr>";
//                info+="<tr style='color:red'><td >guid：" + p.getUID() + "</td></tr>";
                    info+="<tr style='color:red'><td >GetUsbSN：" + uSerialNum + "</td></tr>";
                    info+="<tr style='color:red'><td >pid：" + strSN.pid + "</td></tr>";
                    info+="<tr style='color:red'><td >vid：" + strSN.vid + "</td></tr>";
                    info+="<tr><td >CreationClassName：" + p.CreationClassName + "</td></tr>";
                    info+="<tr><td >Description：" + p.Description + "</td></tr>";
                    info+="<tr><td >DeviceID：" + p.DeviceID + "</td></tr>";
                    info+="<tr><td >InstallDate：" + p.InstallDate + "</td></tr>";
                    info+="<tr><td >Name：" + p.Name + "</td></tr>";
                    info+="<tr><td >ProtocolSupported：" + p.ProtocolSupported+ "</td></tr>";
                    info+="<tr><td >Status：" + p.Status+ "</td></tr>";
                    info+="<tr><td >SystemName：" + p.SystemName+ "</td></tr>";

                    zkInfo.usbSn = uSerialNum;
                    zkInfo.pid = strSN.pid;
                    zkInfo.vid = strSN.vid;
                    zkInfoArr.push(zkInfo);

                }

            }

            info+="</table>";

            var json_data = JSON.stringify(zkInfoArr);

            $("#prTest").append(info);

            if(machineNum == 0 ){
                alert("未识别到ZK4500指纹仪,请确认接入电脑后再操作!")
                $("#prTest").empty();
            }else{
                $.post('/baseset/insettest',{zkInfoArr:json_data},function(data){

                    if(data.result){
                        alert("录入成功");
                        machineNum = 0;
                    }
                },'json');
            }
        }

        //**************************************//
        function USBController() {//USB控制器
            var properties = service.ExecQuery("SELECT * FROM  Win32_USBHub");
            var e = new Enumerator (properties);
            var info="<table border=1>";
            info+="<tr bgcolor='#CDEDED' style='font-weight: bold;'><td width='450'>USB控制器信息：</td></tr>";
            for (;!e.atEnd();e.moveNext ()) {

                var strSN;
                var uSerialNum;
                var p = e.item ();
                var sn=p.DeviceID;
//                GetUsbSN(sn)
                strSN=GetUsbSN(sn);

                uSerialNum=strSN.usbSn.substring(8);
                info+="<tr style='color:red'><td >Caption：" + p.Caption + "</td></tr>";
//                info+="<tr style='color:red'><td >guid：" + p.getUID() + "</td></tr>";
                info+="<tr style='color:red'><td >GetUsbSN：" + uSerialNum + "</td></tr>";
                info+="<tr style='color:red'><td >pid：" + strSN.pid + "</td></tr>";
                info+="<tr style='color:red'><td >vid：" + strSN.vid + "</td></tr>";
                info+="<tr><td >CreationClassName：" + p.CreationClassName + "</td></tr>";
                info+="<tr><td >Description：" + p.Description + "</td></tr>";
                info+="<tr><td >DeviceID：" + p.DeviceID + "</td></tr>";
                info+="<tr><td >InstallDate：" + p.InstallDate + "</td></tr>";
                info+="<tr><td >Name：" + p.Name + "</td></tr>";
                info+="<tr><td >ProtocolSupported：" + p.ProtocolSupported+ "</td></tr>";
                info+="<tr><td >Status：" + p.Status+ "</td></tr>";
                info+="<tr><td >SystemName：" + p.SystemName+ "</td></tr>";
            }
            info+="</table>";
            return info;
        }

        function printUsb(){
            var test = USBController();
//            alert(test);
            $("#prTest").append(test);
        }

        //****************************//
        function GetDIVORPIV(DIV){
            var str;
            var arrayDIV=new Array();
            if(DIV.indexOf("_")>0)
            {
                arrayDIV=DIV.split("_");
                str=arrayDIV[1];
            }
            else
            {
                str="";
            }
            return str;
        }

        function GetDIVandPIV(DIVPIV,idArr){
            var strDIVandPIV;
            var strDIVPIV = DIVPIV;
            var arrayDIVPIV = new Array();
            if(strDIVPIV.indexOf("&")>0)
            {
                arrayDIVPIV = strDIVPIV.split("&");
                strDIVandPIV = GetDIVORPIV(arrayDIVPIV[0])+GetDIVORPIV(arrayDIVPIV[1]);
                idArr.vid = GetDIVORPIV(arrayDIVPIV[0]);
                idArr.pid = GetDIVORPIV(arrayDIVPIV[1]);

//                alert(GetDIVORPIV(arrayDIVPIV[0])+"..."+GetDIVORPIV(arrayDIVPIV[1]))
            } else {
                strDIVandPIV="";
            }
            idArr.strDIVandPIV = strDIVandPIV
//            alert(strDIVandPIV);
            return idArr;
        }

        function GetUsbSN(DIVPIVSN){
            var idArr = {};
            var UsbSN;
            var strSN = DIVPIVSN ;
            var arraySN = new Array();
            if(strSN.indexOf("&")>0){
                arraySN = strSN.split("\\");
                if(arraySN.length > 2){
                    idArr = GetDIVandPIV(arraySN[1],idArr);
                    UsbSN=idArr.strDIVandPIV + arraySN[2];

//                    UsbSN=GetDIVandPIV(arraySN[1])+arraySN[2];
                }
                else{
                    UsbSN = "";
                }
            } else {
                UsbSN = "";
            }
            idArr.usbSn = UsbSN;
            return idArr;
        }
        function CheckUsb(){
            var e,x;
            var ForReading = 1,RorWriting = 2;
            var fso = new ActiveXObject("Scripting.FileSystemObject");
//            var fso = new ActiveXObject("ZKFPEngX Control");
            var bfResult = false ;
            e = new Enumerator(fso.Drives)
            for(;!e.atEnd();e.moveNext())
            {
                x=e.item();
                if(x.DriveType == 1)
                {
                    if(x.Path != "A:")
                    {
                        bfResult = true ;
                    }
                }
            }
            return bfResult;
        }
        function ReadUsbSN(){
            var bfUsb;
            bfUsb = CheckUsb();
            if(bfUsb == true)
            {
                var locator = new ActiveXObject ("ZKFPEngX Control");
//                var locator = new ActiveXObject ("WbemScripting.SWbemLocator");
                var computer = locator.ConnectServer(".");
                var properties = computer.ExecQuery("SELECT * FROM Win32_USBHub");
//                var properties = computer.ExecQuery("SELECT * FROM Win32_USBHub");
                var e = new Enumerator (properties);
                var arrayUsbSN=new Array();//存放序列号
                var intCount=0;
                for (;!e.atEnd();e.moveNext ())
                {
                    var p = e.item ();
                    var strSN;
                    var uSerialNum;
                    var sn=p.DeviceID
                    if(sn.indexOf("VID")>0)
                    {
                        strSN=GetUsbSN(sn);
                        uSerialNum=strSN.substring(8);
                        arrayUsbSN[intCount]=strSN;
                        intCount=intCount+1;
                        alert("你的U盘系列号为："+ uSerialNum);
                    }
                }
            }else{
                alert("请插入U盘");
            }
        }
        //****************************//



    </script>




    <div class="row" >
        <div class="col-md-2 col-md-offset-3">
            <#if error?? && (error.isPresent())>
                <p class="text-danger"><@spring.message code="login.error"/></p>
            </#if>


        <#--测试代码开始-->
            <#--<br/><br/><br/>
            <input class="form-control" type="text" name="serial" id="serial">
            <br/><br/>
            <button class="btn btn-primary  btn-block" type="button" onclick="minit()">初始化</button>
            <br/>

            <button type="button" onclick="pRegist()">登记指纹</button>
            <input type="text" name="forReg" id="forReg" style="width: 300px;">

            <button type="button" onclick="validFgp()">识别指纹</button>
            <input type="text" name="validationFgp" id="validationFgp" style="width: 300px;">
            <br/><br/>

            <button type="button" onclick="tcmp()">1：N对比</button>
            <input type="text" name="fortst" id="fortst" style="width: 205px;">
            <br/><br/>

            <button type="button" onclick="ctlBeep()">测试蜂鸣器</button>
            <br/><br/>

            <form id="fingerprintForm" method="post" action="/baseset/saveFingerprint" >
            <input name="name" id="name" type="text"  >
            &lt;#&ndash;<input name="password" >&ndash;&gt;-->

            <#--<input type="text" id="fingerprint" name="fingerprint"  >-->

                <#--<input type="button" onclick="ReadUsbSN()" value="测试按钮" >-->
                <#--<input type="button" onclick="printUsb()" value="测试按钮2" >-->
                <#--<input type="button" onclick="insert()" value="录入系统" >-->
            <label>请将需要录入的ZK4500指纹机插入,再点击录入系统</label>
            <#--<@form.btn_search "onclick='insert()'" "录入系统"/>&nbsp;-->
            <@form.btn_search "onclick='t1()'" "录入系统"/>&nbsp;
            <@form.btn_back "onclick='prList()'" "返回"/>&nbsp;
            <#--<input class="form-control" type="text" name="serial" id="serial">-->
            <#--<label>指纹机识别码:</label>-->
            <label id="serialLabel" ></label>

            </form>

            <div id="prTest" >

            </div>
            <div style="display: none" >

            <object
                    id="myativx"
                    classid="clsid:CA69969C-2F27-41D3-954D-A48B941C3BA7"
            <#--codebase="<%=request.getContextPath()%>/ocx/TableListX.ocx#version=1,0,0,5"-->

                    width=100%
                    height=2100
                    align=middle
                    hspace=0
                    vspace=0
                    onerror="onObjectError();">
            </object>
            </div>
        <#--测试代码结束-->

        </div>
    </div>
    <#--</@general.frame>-->
    </@main.frame>
</#escape>