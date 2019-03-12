<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sj" uri="/struts-jquery-tags"%>

<html>
    <head>
        <sj:head jqueryui="true" jquerytheme="mytheme1" customBasepath="template/themes"/>
        <script type="text/javascript" src="js/jquery.center.min.js"></script>
        <script type="text/javascript" src="js/jquery.msg.min.js"></script>
        <script type="text/javascript" src="js/offenderInfoScript.js"></script>
        <script type="text/javascript" src="js/jquery.maskedinput-1.3.min.js"></script>

        <script type="text/javascript">
            var prgType;
            function validateTime(timeField)
            {
                var timeVal = timeField.value;
                if (timeVal.length === 0) {
                    timeField.style.backgroundColor = "white";
                    return true;
                }

                var timeRegex = new RegExp("[0-1][0-9]:[0-5][0-9]");
                if (!timeRegex.test(timeVal))
                {
                    alert("Please enter a valid time in HH:MM 12 Hour format.");
                    timeField.style.backgroundColor = "yellow";
                    return false;
                } else
                {
                    timeField.style.backgroundColor = "white";
                    return true;
                }
            }

            function isNumber(evt) {
                evt = (evt) ? evt : window.event;
                var charCode = (evt.which) ? evt.which : evt.keyCode;
                if (charCode > 31 && (charCode < 48 || charCode > 57)) {
                    alert("Enter only numeric values for APS Report No field");
                    document.getElementById("apsRptNo").value = "";
                    document.getElementById("apsRptNo").focus();
                    return false;
                }
                return true;
            }

            $(document).ready(function () {
                $("#tcoommiNotifiedByPhoneFlag").bind('change', function () {
                    var phoneFlag = $(this).val();
                    if (phoneFlag == 'Y') {
                        $("div.TCByPhoneDateDiv").show();
                    } else {
                        $("div.TCByPhoneDateDiv").hide();
                    }
                }).trigger('change');
            });
           
           $(document).ready(function () {

                $("#incidentTypeGroup").bind('change', function () {
                    var incType = $(this).val();
                    if (incType == "0" || (incType != "2" && incType != "3" && incType != "4" && incType != "6" && incType != "8" && incType != "13" && incType != "14")) {
                        $("div.arrestTypeDiv").hide();
                        $("div.apsDiv").hide();
                        $("div.otherCommentsDiv").hide();
                        $("div.pshycHospDiv").hide();
                        $("div.refusalofServiceDiv").hide();
                        $("div.caseClosureDiv").hide();
                        $("div.dischargeReasonDiv").hide();
                    }
                    if (incType == 2) {
                        $("div.arrestTypeDiv").show();
                        $("div.apsDiv").hide();
                        $("div.otherCommentsDiv").hide();
                        $("div.pshycHospDiv").hide();
                        $("div.refusalofServiceDiv").hide();
                        $("div.caseClosureDiv").hide();
                        $("div.dischargeReasonDiv").hide();
                    }
                    else if (incType == 3) {
                        $("div.arrestTypeDiv").hide();
                        $("div.apsDiv").show();
                        $("div.otherCommentsDiv").hide();
                        $("div.pshycHospDiv").hide();
                        $("div.refusalofServiceDiv").hide();
                        $("div.caseClosureDiv").hide();
                        $("div.dischargeReasonDiv").hide();
                    } else if (incType == 4) {
                        $("div.arrestTypeDiv").hide();
                        $("div.caseClosureDiv").show();
                        $("div.pshycHospDiv").hide();
                        $("div.refusalofServiceDiv").hide();
                        $("div.otherCommentsDiv").hide();
                        $("div.dischargeReasonDiv").hide();
                        $("div.apsDiv").hide();
                    } else if (incType == 6 ) {
                        $("div.arrestTypeDiv").hide();
                        $("div.pshycHospDiv").show();
                        $("div.refusalofServiceDiv").hide();
                        $("div.caseClosureDiv").hide();
                        $("div.otherCommentsDiv").hide();
                        $("div.dischargeReasonDiv").hide();
                        $("div.apsDiv").hide();
                    } else if (incType == 8) {
                         $("div.arrestTypeDiv").hide();
                        $("div.refusalofServiceDiv").show();
                        $("div.pshycHospDiv").hide();
                        $("div.caseClosureDiv").hide();
                        $("div.otherCommentsDiv").hide();
                        $("div.dischargeReasonDiv").hide();
                        $("div.apsDiv").hide();
                    } else if (incType == 13) {
                         $("div.arrestTypeDiv").hide();
                        $("div.dischargeReasonDiv").show();
                        $("div.refusalofServiceDiv").hide();
                        $("div.pshycHospDiv").hide();
                        $("div.caseClosureDiv").hide();
                        $("div.otherCommentsDiv").hide();
                        $("div.apsDiv").hide();
                    } else if (incType == 14) {
                         $("div.arrestTypeDiv").hide();
                        $("div.otherCommentsDiv").show();
                        $("div.pshycHospDiv").hide();
                        $("div.refusalofServiceDiv").hide();
                        $("div.caseClosureDiv").hide();
                        $("div.dischargeReasonDiv").hide();
                        $("div.apsDiv").hide();
                    }
                }).trigger('change');
     });

            $(document).ready(function () {

                $("#currentStatusGroup").bind('change', function () {
                    var currStatus = $(this).val();
                    if (currStatus != "2" && currStatus != "3" && currStatus != "6" && currStatus != "7" && currStatus != "11" && currStatus != "12") {
                        $("div.currIncStatCmntDiv").hide();
                        $("div.jailDiv").hide();
                        $("div.detentionDiv").hide();
                        $("div.hospitalDiv").hide();
                        $("div.residentailDiv").hide();
                    }
                    if (currStatus == 2) {
                        $("div.jailDiv").show();
                        $("div.currIncStatCmntDiv").hide();
                        $("div.detentionDiv").hide();
                        $("div.hospitalDiv").hide();
                        $("div.residentailDiv").hide();
                    } else if (currStatus == 3 ) {
                        $("div.hospitalDiv").show();
                        $("div.currIncStatCmntDiv").hide();
                        $("div.jailDiv").hide();
                        $("div.detentionDiv").hide();
                        $("div.residentailDiv").hide();
                    } else if (currStatus == 6  || currStatus == 12 ) {
                        $("div.residentailDiv").show();
                        $("div.currIncStatCmntDiv").hide();
                        $("div.jailDiv").hide();
                        $("div.detentionDiv").hide();
                        $("div.hospitalDiv").hide();
                    } else if (currStatus == 7 ) {
                        $("div.detentionDiv").show();
                        $("div.currIncStatCmntDiv").hide();
                        $("div.jailDiv").hide();
                        $("div.hospitalDiv").hide();
                        $("div.residentailDiv").hide();
                    }else if (currStatus == 11 ) {
                        $("div.currIncStatCmntDiv").show();
                        $("div.jailDiv").hide();
                        $("div.detentionDiv").hide();
                        $("div.hospitalDiv").hide();
                        $("div.residentailDiv").hide();
                    }
                }).trigger('change');
     });


            function setSubmitAction() {

                if (confirm("Are you sure you wish to Submit the Incident report?"))
                {
                    var currentReportStatus = document.getElementById("currRptStatus").value;
                    if (currentReportStatus == "Rejected" || currentReportStatus == "Re-Submitted") {
                        document.getElementById("statusCde").value = "3";
                    } else {
                        document.getElementById("statusCde").value = "2";
                    }
                    document.getElementById("incidentForm").action = "submitIncidentIncidentAction";
                    document.getElementById("incidentForm").submit();
                    return true;
                } else
                {
                    return false;
                }
            }

            function setCancelAction() {

                if (confirm("Are you sure you wish to Cancel the Incident report?"))
                {
                    history.go(-1);
                    return true;
                } else
                {
                    return false;
                }
            }

            function setStatusAction(status) {
                var statusDesc = "";
                if (status == 5) {
                    statusDesc = "Accept";
                } else if (status == 6) {
                    statusDesc = "Reject";
                }else if (status == 7) {
                    statusDesc = "Accept -Pending Followup";
                }else if (status == 8) {
                    statusDesc = "Accept - Request Followup";
                }

                 if (confirm("Are you sure you wish to " + statusDesc + " the Incident report?")) {
                    document.getElementById("statusCde").value = status;
                    document.getElementById("incidentForm").action = "updateIncidentReportStatusIncidentAction";
                    document.getElementById("incidentForm").submit();
                    return true;
                } else {
                    return false;
                }
            }


            function setPrintAction() {
                document.getElementById("incidentForm").action = "generatePDFReportIncidentAction";
                document.getElementById("incidentForm").submit();
                return true;
            }

            function addFollowup() {
                var openFollowups = document.getElementById("openFollowups").value;

                if (openFollowups != null && openFollowups == "Y") {
                    alert("The current Incident Report has an open Follow-up that must be approved before adding a new Follow-up.");
                    return false;
                }
                return true;
            }

            function viewFollowupList(incidentNo)
            {
                var iMyWidth;
                var iMyHeight;
                //half the screen width minus half the new window width (plus 5 pixel borders).
                iMyWidth = (window.screen.width / 2) - (315 + 10);
                //half the screen height minus half the new window height (plus title and status bars).
                iMyHeight = (window.screen.height / 2) - (200 + 50);
                window.open("viewFollowupListByIncidentFollowUpAction?incidentNo=" + incidentNo, "Window2", "status=no,height=500,width=775,resizable=yes,left=" + iMyWidth + ",top=" + iMyHeight + ",screenX=" + iMyWidth + ",screenY=" + iMyHeight + ",toolbar=no,menubar=no,scrollbars=yes,location=no,directories=no");
            }

            function viewIncidentRevisionList(incidentNo, refNo)
            {
                var iMyWidth;
                var iMyHeight;
                //half the screen width minus half the new window width (plus 5 pixel borders).
                iMyWidth = (window.screen.width / 2) - (315 + 10);
                //half the screen height minus half the new window height (plus title and status bars).
                iMyHeight = (window.screen.height / 2) - (200 + 50);
                window.open("viewIncidentRevisionListIncidentListAction?incidentNo=" + incidentNo + "&referralId=" + refNo, "Window2", "status=no,height=500,width=775,resizable=yes,left=" + iMyWidth + ",top=" + iMyHeight + ",screenX=" + iMyWidth + ",screenY=" + iMyHeight + ",toolbar=no,menubar=no,scrollbars=yes,location=no,directories=no");
            }

        </script>

        <style type="text/css">
            span.bold-red {
                color: red;
                font-weight: bold;
            }
            .borderbold {  border-width: 0.5px;   border-color: black; border: 0.5px solid black; border-radius: 0.5px; } 
            .border {   border-width: 0.5px;   border-color: black; border:0.5px solid black;} 
            .linkFont { font-style: normal;font-size: 50px;} 
        </style>
    </head>
    <body>
        <div id="application">
            <div id="offenderInfoDiv">
                <%@ include file="/us/tx/state/tdcj/tcoommi/jsp/offender/stubs/offenderInfoInclude.jsp"%>
            </div>

            <br>
            <span class="screen_title">Incident Information (Referral# <s:property value="incident.refNo"/>, Incident# <s:if test='incident.incidentNo == 0'>
                    New )
                </s:if>   
                <s:else><s:property value="incident.incidentNo"/> , Revision# <s:property value="incident.revisionNo"/>)</s:else> </span>
            <span style="color:red;font-weight:bold;"> <s:actionmessage /></span>
            <div id="incidentErrorDiv" style="color: red; font-weight: bold;"></div>
            <div id="incidentDetailDiv" class="ui-widget-content">

                <s:form  theme="simple" id="incidentForm"  >

                    <s:hidden id="refNo" name="refNo" value="%{incident.refNo}"/>
                    <s:hidden id="revisionNo" name="revisionNo" value="%{incident.revisionNo}"/>
                    <s:hidden id="referralId" name="referralId" value="%{incident.refNo}"/>
                    <s:hidden id="incidentNo" name="incidentNo" value="%{incident.incidentNo}"/>
                    <s:hidden id="caseWorkerId" name="caseWorkerId" value="%{incident.caseWorkerId}"/>
                    <s:hidden id="currRptStatus" name="currRptStatus" value="%{incident.reportStatus}"/>
                    <s:hidden id="reportRevisionType" name="reportRevisionType" value="%{incident.reportRevisionType}"/>
                    <s:hidden id="statusCde" name="statusCde"/>
                    <s:hidden id="openFollowups" name="openFollowups" value="%{incident.openFollowups}"/>
                    <s:hidden id="serviceLength" name="serviceLength" value="%{incident.timeInCurrentService}"/>
                    <s:hidden id="delFlag" name="delFlag" value="%{incident.delFlag}"/>
                    <s:if test='incident.delFlag == "Y" || incident.reportStatus == "Accepted" || incident.reportStatus == "Accepted-Request Follow-up" || 
                          incident.reportStatus == "Accepted-Pending Follow-up"|| incident.recentRevison == false ||incident.eligibleVendor ==false || 
                          (currentUser.inquiry || currentUser.security || currentUser.staff || currentUser.complianceMonitor)'>
                        <fieldset disabled="disabled">
                        </s:if>
                        <table cellspacing="0" cellpadding="2" width="100%"><tr><td>
                                    <table cellspacing="0" cellpadding="2" width="100%" border="1">
                                        <br>
                                        <tr  height="35" style="font-weight: bold;vertical-align:middle;"><td>Referral End Date</td><td>Report Type</td><td>Criminal Justice Status</td><td>Service Type</td><td>Time in Current Service</td>
                                        </tr>
                                        <tr height="35" style="background-color:#FFFFFF;vertical-align:middle;">
                                            <td><s:date name="referral.endDate" format="MM/dd/yyyy"/></td>
                                            <td>Incident</td>
                                            <td><s:property value="incident.criminalStatus"/></td>
                                            <td><s:property value="referral.service.typeName"/></td>
                                            <td align="left"><s:property value="incident.timeInCurrentServiceStr"/></td>
                                        </tr>
                                    </table>
                                    <table cellspacing="0" cellpadding="2" width="100%">
                                        <tr  height="35" style="font-weight: bold;vertical-align:middle;">
                                            <td>Type of Charges</td>
                                            <td colspan="2">Date of Incident<span class="bold-red">*</span></td>
                                            <td>TRAS Risk Level </td>
                                            <td>TRAS Assessment Tool</td>
                                            <td>Substance Abuse Indicator</td>
                                        </tr>
                                        <tr height="35" style="background-color:#FFFFFF;vertical-align:middle;">
                                            <td>
                                                <s:select theme="simple" id="chargesCJstatus" name="incident.chargesCJstatus"
                                                          list="#{'1':'Felony',
                                                                  '2':'Misdemeanor'}"/>
                                            </td>
                                            <td colspan="2">
                                                <sj:datepicker id="incidentDate" name="incident.incidentDate" readonly="true" placeholder="MM/DD/YYYY" minDate="-3y" maxDate= "y" displayFormat="mm/dd/yy" changeMonth="true" changeYear="true" size="12"/>
                                            </td>
                                            <td align="center"><s:property value="incident.trasRiskLevel"/></td>
                                            <td style="background-color:#FFFFFF;vertical-align:middle;" align="center"><s:property value="incident.trasAssessmentTool"/></td>
                                            <td align="center"> <s:checkbox label="Substance Abuse Related?" name="incident.substanceAbuseRelated" value="incident.substanceAbuseRelated"/></td>
                                        </tr>

                                    </table>

                                    <table cellspacing="0" cellpadding="2" width="100%">
                                        <tr height="35" style="vertical-align:middle;">
                                            <td colspan="3"><b>Brief Case Summary<span class="bold-red">*</span> </b> &nbsp;(Maximum 1000 characters)</td>
                                        </tr>
                                        <tr height="35" style="background-color:#FFFFFF;vertical-align:middle;">
                                            <td colspan="3"><s:textarea name="incident.caseSummary" maxlength="999" rows="10" cols="85"/></td>
                                        </tr>

                                        <tr height="10" style="background-color:#FFFFFF;">
                                            <td>&nbsp;</td>
                                        </tr>  
                                    </table>
                                </td></tr></table> <br>  
                        <table cellspacing="0" cellpadding="2" width="100%" border="1">

                            <tr  height="35" style="font-weight: bold;">
                                <td>Program Type<span class="bold-red">*</span></td>
                                <s:if test="referral.service.type ==5 ||
                                      referral.service.type ==6">
                                    <td>Program Sub-Type<span class="bold-red">*</span></td></s:if>
                                <td><s:if test='incident.programType=="1" || incident.programType=="2" || incident.programType=="3"'>LMHA(Vendor) Name</s:if>
                                    <s:else>
                                        HSS
                                    </s:else>
                                </td>
                                <td>Revision Type</td>
                                <td>Report Status</td>
                                <td>Status Date</td>  

                            </tr>
                            <tr  height="35" style="background-color:#FFFFFF;vertical-align:middle;">
                                <td>
                                    <s:if test="referral.service.type ==1 ||
                                          referral.service.type ==2 ||
                                          referral.service.type ==3 ||
                                          referral.service.type ==4">
                                        <s:select theme="simple" id="programType" name="incident.programType"
                                                  list="#{'1':'Adult',
                                                          '3':'Residential'}"/>
                                    </s:if>
                                    <s:elseif test="referral.service.type ==13">
                                        <s:select theme="simple" id="programType" name="incident.programType"
                                                  list="#{'1':'Adult'}"/>
                                    </s:elseif>
                                    <s:elseif test="referral.service.type ==12">
                                        <s:select theme="simple" id="programType" name="incident.programType"
                                                  list="#{'3':'Residential'}"/>
                                    </s:elseif>
                                    <s:elseif test="referral.service.type ==5 ||
                                              referral.service.type ==6">
                                        <s:select theme="simple" id="programType" name="incident.programType"
                                                  list="#{'2':'Juvenile'}"/>
                                    </s:elseif>
                                    <s:elseif test="referral.service.type ==7 ||
                                              referral.service.type ==10 ||
                                              referral.service.type ==11">
                                        <s:select theme="simple" id="programType" name="incident.programType"
                                                  list="#{'4':'MRIS/Medical'}"/>
                                    </s:elseif>
                                </td>
                                <s:if test="referral.service.type ==5 ||
                                      referral.service.type ==6">
                                    <td>
                                        <s:select theme="simple" id="subProgramType" name="incident.programSubType"
                                                  list="#{'1':'SNDP',
                                                          '2':'TJJD',
                                                          '3':'Non-SNDP'}"/>
                                    </td>
                                </s:if>
                                <td>
                                    <s:property value="referral.vendor.name"/>
                                </td>
                                <td><s:if test='incident.reportRevisionType =="R" && incident.revisionNo == 0'>
                                        Original
                                    </s:if>
                                    <s:if test='incident.reportRevisionType== "R" && incident.revisionNo > 0'>
                                        Revised
                                    </s:if></td>
                                <td><s:property value="incident.reportStatus"/></td>
                                <td><s:date name="incident.currentStatusDate" format="MM/dd/yyyy"/></td>
                            </tr>

                        </table>

                        <table cellspacing="0" cellpadding="2" width="100%">
                            <tr height="10" style="background-color:#FFFFFF;">
                                <td colspan="3">&nbsp;</td>
                            </tr> 
                            <tr height="35" style="font-weight: bold;vertical-align:middle;">
                                <td width="25%">Current Incident Status<span class="bold-red">*</span></td>

                                <td width="25%">
                                    <div class="jailDiv">Jail Date<span class="bold-red">*</span></div>
                                    <div class="hospitalDiv">Hospital Date<span class="bold-red">*</span></div>
                                    <div class="detentionDiv">Detention Date<span class="bold-red">*</span></div>
                                </td>
                                <td width="50%" align="left"> 
                                    <div class="jailDiv">Jail - Current Medication List Provided?<span class="bold-red">*</span></div>
                                    <div class="hospitalDiv">Hospital - Current Medication List Provided?<span class="bold-red">*</span></div>
                                    <div class="detentionDiv">Detention-Current Medication List Provided?<span class="bold-red">*</span></div>
                                    <div class="currIncStatCmntDiv">Current Incident Status Comments<span class="bold-red">*</span></div>
                                </td>

                            </tr>

                            <tr style="background-color:#FFFFFF;vertical-align:middle;">
                                <td width="25%">  
                                            <s:select theme="simple" id="currentStatusGroup" name="incident.currentStatusStr"
                                                      list="#{'0':'Select',
                                                             '9':'Assisted Living',
                                                             '4':'Community',
                                                             '7':'Detention',
                                                             '13':'Group Home',
                                                             '5':'Home',
                                                             '3':'Hospital',
                                                             '2':'Jail',
                                                             '8':'Nursing Facility',
                                                             '11':'Other',
                                                             '12':'Out of Home Placement',
                                                             '10':'Private Residence',
                                                             '6':'Residential Placement',
                                                             '14':'Residential Program',
                                                             '1':'TJJD'}"/>
                                </td>

                                <td width="25%">
                                    <div class="jailDiv"><sj:datepicker readonly="true" id="jailDate" placeholder="MM/DD/YYYY" name="incident.jailDate" minDate="-3y" maxDate= "y" displayFormat="mm/dd/yy" changeMonth="true" changeYear="true" size="12"/></div>
                                    <div class="hospitalDiv"><sj:datepicker readonly="true" id="hospitalDate" placeholder="MM/DD/YYYY" name="incident.hospitalDate" minDate="-3y" maxDate= "y" displayFormat="mm/dd/yy" changeMonth="true" changeYear="true" size="12"/></div>
                                    <div class="detentionDiv"><sj:datepicker readonly="true" id="detDate" placeholder="MM/DD/YYYY" name="incident.detentionDate" minDate="-3y" maxDate= "y" displayFormat="mm/dd/yy" changeMonth="true" changeYear="true" size="12"/></div>
                                </td>
                                <td width="50%" align="left">
                                    <div class="jailDiv"><s:select theme="simple" id="currentJailMedListSent" name="incident.currentJailMedListSent" list="#{'Y':'Yes','N':'No'}"/></div>
                                    <div class="hospitalDiv"><s:select theme="simple" id="currentHospitalMedListSent" name="incident.currentHospitalMedListSent" list="#{'Y':'Yes','N':'No'}"/></div>               
                                    <div class="detentionDiv"><s:select theme="simple" id="currentDetentionMedListSent" name="incident.currentDetentionMedListSent" list="#{'Y':'Yes','N':'No'}"/></div>     
                                    <div class="currIncStatCmntDiv"> <s:textfield name="incident.currStatusComments" size="25" maxLength="25"/></div>
                                </td>

                            </tr>

                        </table>
                                
                                    <div class="residentailDiv">
                                <table cellspacing="0" cellpadding="2"  width="100%" >

                                    <tr height="35" style="font-weight: bold;vertical-align:middle;">
                                        <td width="30%"><div class="residentailDiv">Residential Facility Name<span class="bold-red">*</span></div></td>
                                        <td width="30%"><div class="residentailDiv">Residential Facility Location<span class="bold-red">*</span></div> </td>
                                        <td width="40%"><div class="residentailDiv">Residential - Current Medication List Provided?<span class="bold-red">*</span></div></td>

                                    </tr>

                                <tr style="background-color:#FFFFFF;vertical-align:middle;">
                                    <td width="30%"><div class="residentailDiv"><s:textfield theme="simple" id="resiFacilityName"  name="incident.resiFacilityName" size="15" maxlength="50"/></div></td>
                                    <td width="30%"><div class="residentailDiv"><s:textfield theme="simple" id="location"  name="incident.location" size="15" maxlength="75"/></div></td>
                                    <td width="40%"><div class="residentailDiv"><s:select theme="simple" id="resCurHospMedListSent" name="incident.currentResiFacilityMedListSent" list="#{'Y':'Yes','N':'No'}"/></div></td>

                                </tr>
                            </table>
                                    </div>
                        <table cellspacing="0" cellpadding="2" width="100%">

                            <tr height="35" style="font-weight: bold;vertical-align:middle;">
                                 <td width="25%">Incident Type<span class="bold-red">*</span></td>
                                   
                            <td width="75%">
                                <div class="arrestTypeDiv">
                                            Arrest Type<span class="bold-red">*</span>
                                    </div>
                                <div class="otherCommentsDiv">Incident Type Other Comments<span class="bold-red">*</span> &nbsp;(Maximum 150 characters)</div>
                                    <div class="dischargeReasonDiv">Discharge Reason<span class="bold-red">*</span> &nbsp;(Maximum 150 characters)</div>
                                    <div class="caseClosureDiv">Case Closure Date<span class="bold-red">*</span></div>
                                    <div class="pshycHospDiv">Psychiatric Hospitalization Type<span class="bold-red">*</span></div>
                                    <div class="refusalofServiceDiv">Refusal of Service Form Indicator<span class="bold-red">*</span></div>
                                    <div class="apsDiv">APS Report No</div>
                                </td>
                            </tr>
                            <tr style="background-color:#FFFFFF;vertical-align:middle;">
                                    <td valign="top" width="25%"> 
                                         <s:select theme="simple" id="incidentTypeGroup" name="incident.incidentType"
                                                      list="#{'0':'Select',
                                                              '1':'Abscond',
							      '3':'Adult Protective Services',
                                                              '2':'Arrest',                                                             
                                                              '4':'Case Closure',
 							      '9':'Case Closure(Non-engagement)',
                                                              '5':'Death',
                                                              '11':'Detention',
 							      '13':'Discharge',
						              '12':'Out of Home Placement(Not TJJD)',
							      '14':'Other',
                                                              '6':'Psychiatric Hospitalization',
                                                              '7':'Revocation',
                                                              '10':'Revocation to TJJD',
                                                              '8':'Refusal of Service'}"/>
                                </td>
                                 
                                    <td width="75%">
                                        <div class="arrestTypeDiv">
                                             <s:select theme="simple" id="arrestType" name="incident.arrestType"
                                                      list="#{'0':'Select',
                                                               '3':'Felony',
                                                               '4':'Misdemeanor',
                                                               '1':'Motion to Revoke(CSCD)',
                                                               '2':'Violation of Conditions(Parole Only)'}"/>
                                     </div>
                                    <div class="otherCommentsDiv"> <s:textarea maxlength="150" name="incident.incidentOtherComments" rows="5" cols="50"/></div>
                                    <div class="dischargeReasonDiv"><s:textarea name="incident.dischargeReason" maxlength="150" id="dischargeReason" rows="5" cols="50"/></div>
                                    <div class="caseClosureDiv"><sj:datepicker readonly="true" id="caseCloseDate" placeholder="MM/DD/YYYY" name="incident.caseCloseDate" minDate="-3y" maxDate= "y" displayFormat="mm/dd/yy" changeMonth="true" changeYear="true" size="12"/></div>
                                    <div class="pshycHospDiv"><s:select theme="simple" id="psychHospType" name="incident.psychHospType"
                                              list="#{'V':'Voluntary',
                                                      'I':'Involuntary'}"/></div>
                                    <div class="refusalofServiceDiv"><s:select theme="simple" id="refusalOfServiceForm" name="incident.refusalOfServiceForm"
                                              list="#{'Y':'Yes',
                                                      'N':'No'}"/></div>
                                    <div class="apsDiv"><s:textfield theme="simple" id="apsRptNo"  name="incident.apsReportNo" onkeypress="return isNumber(event);" size="10" maxlength="8"/></div>
                                </td>
                            </tr>

                            <tr height="35" style="font-weight: bold;vertical-align:middle;">
                                <td width="30%" align="left">Incident Carried By Media?<span class="bold-red">*</span></td>
                                <td width="30%">If Yes, What Media?<span class="bold-red">*</span></td>

                            </tr>
                            <tr height="35" style="background-color:#FFFFFF;vertical-align:middle;">
                                <td align="left" width="30%"><s:checkbox label="" id="incidentCarriedByMedia" name="incident.incidentCarriedByMedia" value="incident.incidentCarriedByMedia"/></td>
                                <td width="30%"><s:textfield name="incident.mediaName" maxLength="100"/></td>
                            </tr>

                            <tr  height="35" style="vertical-align:middle;">

                                <td valign=top" colspan="3"><b>Incident Description</b><span class="bold-red">*</span> &nbsp;(Maximum 1000 characters)</td>
                            </tr>
                            <tr  height="35" style="background-color:#FFFFFF;vertical-align:middle;">
                                <td valign=top" colspan="3"> <s:textarea name="incident.incidentDescription" maxlength="1000" rows="5" cols="85"/></td>
                            </tr>

                        </table>


                        <s:if test='incident.programType == "4"'>
                            <table cellspacing="0" cellpadding="2" width="100%">
                                <tr height="35" style="font-weight: bold;vertical-align:middle;">
                                    <td>HSS Notify Date/Time<span class="bold-red"> *</span></td>
                                    <td> TCOOMMI Supervisor Notify Date/Time(Written) &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                                </tr>
                                <tr height="35" style="background-color:#FFFFFF;vertical-align:middle;">
                                    <td>
                                         <sj:datepicker id="hssNotifiedDate" readonly="true" name="incident.hssNotifiedDate" placeholder="MM/DD/YYYY" minDate="-3y" maxDate= "y" displayFormat="mm/dd/yy" changeMonth="true" changeYear="true" size="12"/>&nbsp;
                                        <s:textfield theme="simple" id="hssNotifiedTime"  name="incident.hssNotifiedTime" placeholder="HH:MM" size="5" maxlength="5" onblur="return validateTime(this);"/>&nbsp;
                                        <s:select theme="simple" id="hssNotifiedampm" name="incident.hssNotifiedampm"
                                                  list="#{'AM':'AM',
                                                          'PM':'PM'}"/>
                                    </td>
                                    <td><s:date name="incident.addTimestamp" format="MM/dd/yyyy hh:mm a"/> </td>
                                </tr>

                            </table>
                        </s:if>

                        <table cellspacing="0" cellpadding="2"  width="100%" >
                            <tr height="35" style="vertical-align:middle;">
                                <td width="40%"><b>Reported within 24 hours?</b><span class="bold-red">*</span></td>
                                <td width="60%"><b>Reason not notified within 24 hours</b><span class="bold-red">*</span>&nbsp;(Maximum 250 characters)</td>
                            </tr>
                            <tr height="35" style="font-weight: bold;background-color:#FFFFFF;vertical-align:middle;">
                                <td width="40%" valign="top" halign="center">
                                    <s:select theme="simple" id="reportedIn24hrs" name="incident.reportedIn24hrs"
                                              list="#{'Y':'Yes',
                                                      'N':'No'}"/>
                                </td>
                                <td width="60%">
                                    <s:textarea rows="5" cols="50" maxlength="250" theme="simple" id="reasonNotNotified"  name="incident.reasonNotNotified"/>
                                </td>
                            </tr>
                        </table><br>
                                  <table cellspacing="0" cellpadding="2" width="100%" border="1">
                            <tr height="35" style="font-weight: bold;vertical-align:middle;">
                                <td>Date/Time Case Worker Notified<span class="bold-red">*</span></td>
                                <td>Case Worker Name/User ID <span class="bold-red">*</span></td>
                                <td>Case Worker Phone # / Ext</td>
                            </tr>
                            <tr height="35" style="background-color:#FFFFFF;vertical-align:middle;">
                                <td><sj:datepicker id="caseWorkerNotifiedDate" readonly="true" placeholder="MM/DD/YYYY" name="incident.caseWorkerNotifiedDate" minDate="-3y" maxDate= "y" displayFormat="mm/dd/yy" changeMonth="true" changeYear="true" size="12"/>&nbsp;
                                    <s:textfield theme="simple" id="cwNotifytimeString" placeholder="HH:MM" name="incident.cwNotifytimeString"  size="5" maxlength="5" onblur="return validateTime(this);"/>
                                    <s:select theme="simple" id="cwNotifyAMPM" name="incident.cwNotifyAMPM"
                                              list="#{'AM':'AM',
                                                      'PM':'PM'}"/>
                                </td>
                                <td>              
                                    <s:select theme="simple" id="caseWorkerId" name="incident.caseWorkerId" 
                                              list="activeUsers"
                                              listKey="code" listValue="desc" rod="1"/>               
                                </td>
                                <td><s:property  value="incident.caseWorkerPhone"/> <s:property  value="incident.caseWorkerPhoneExt"/></td>
                               
                                </tr>

                            </table>
<br>
                        <table cellspacing="0" cellpadding="2"  width="100%" >
                            <s:if test='incident.programType == "1" || incident.programType == "2" || incident.programType == "3"'>
                                <tr height="35" style="font-weight: bold;vertical-align:middle;">
                                    <td>TCOOMMI Notified by Phone?<span class="bold-red">*</span></td>
                                    <td colspan="2"><div class="TCByPhoneDateDiv">Date/Time TCOOMMI Notified-Phone<span class="bold-red">*</span></div></td>
                                    <td>Date/Time TCOOMMI Notified-Written</td>
                                </tr>
                                <tr height="35" style="background-color:#FFFFFF;vertical-align:middle;"> 
                                    <td><s:select theme="simple" id="tcoommiNotifiedByPhoneFlag" name="incident.tcoommiNotifiedByPhoneFlag"
                                              list="#{'Y':'Yes',
                                                      'N':'No'}"/></td>
                                                                 
                                    <td colspan="2">
                                         <div class="TCByPhoneDateDiv"> <sj:datepicker readonly="true" placeholder="MM/DD/YYYY" id="tcoommiNotifiedPhoneDate" name="incident.tcoommiNotifiedPhoneDate" minDate="-3y" maxDate= "y" displayFormat="mm/dd/yy" changeMonth="true" changeYear="true" size="12"/>&nbsp;
                                          <s:textfield theme="simple" id="tcoommiNotifiedPhoneTime"  placeholder="HH:MM" name="incident.tcoommiNotifiedPhoneTime" size="5" maxlength="5" onblur="return validateTime(this);"/>&nbsp;
                                          <s:select theme="simple" id="tcoommiNotifiedPhoneampm" name="incident.tcoommiNotifiedPhoneampm"
                                                  list="#{'AM':'AM',
                                                          'PM':'PM'}"/></div>
                                    </td>
                                    <td><s:date name="incident.addTimestamp" format="MM/dd/yyyy hh:mm a"/></td>
                                </tr>
                            </s:if> 
                        </table>
                        
                      
                        <s:if test='incident.delFlag == "Y" || incident.reportStatus == "Accepted" || incident.reportStatus == "Accepted-Request Follow-up" || 
                          incident.reportStatus == "Accepted-Pending Follow-up" || incident.recentRevison == false || incident.eligibleVendor ==false || 
                          (currentUser.inquiry || currentUser.security || currentUser.staff || currentUser.complianceMonitor)'>
                        </fieldset>
                    </s:if>
                    <table cellspacing="0" cellpadding="2" width="100%">
                        <s:if test='(currentUser.supervisor || currentUser.complianceMonitor || currentUser.manager) && incident.reportStatus != "In Progress"'>
                            <tr height="35" style="vertical-align:middle;">
                                <td colspan="3"><b>Revision Comments</b><span class="bold-red">*</span>&nbsp;(Maximum 1000 characters)</td>
                            </tr>
                            <tr height="35" style="background-color:#FFFFFF;vertical-align:middle;">
                                <s:if test='incident.reportStatus != "Accepted" && incident.reportStatus != "Rejected" && incident.reportStatus != "Accepted-Pending Follow-up" && incident.reportStatus !="Accepted-Request Follow-up" && incident.recentRevison && incident.eligibleVendor'>
                                <td colspan="3"><s:textarea name="incident.revisionComments" maxlength="1000" rows="10" cols="85" /></td>
                                </s:if>
                                <s:if test='incident.reportStatus == "Accepted" || incident.reportStatus == "Rejected" || incident.reportStatus != "Accepted-Pending Follow-up" ||  incident.reportStatus !="Accepted-Request Follow-up" || incident.recentRevison ==false || incident.eligibleVendor==false'>
                                <td colspan="3"><s:textarea name="incident.revisionComments" maxlength="1000" rows="10" cols="85" disabled="true"/></td>
                                </s:if>
                            </tr>
                        </s:if>
                            
                            <s:if test='incident.reportStatus != "In Progress" && (currentUser.supervisor == false && currentUser.complianceMonitor == false && currentUser.manager == false)'>
                            <tr height="35" style="font-weight: bold;vertical-align:middle;">
                                <td colspan="3">Revision Comments </td>
                            </tr>
                            <tr height="35" style="background-color:#FFFFFF;vertical-align:middle;">
                                <td colspan="3"><s:textarea name="incident.revisionComments" maxlength="1000" rows="10" cols="85" disabled="true"/></td>
                            </tr>
                        </s:if>
                        <s:if test="%{incident.incidentNo !=null && incident.incidentNo!=0}">
                            <tr height="35" style="font-weight: bold;vertical-align:middle;">
                                <td>Submitted by</td>
                                <td>Submitted Date/ Time</td>
                                <td>Submitted by Phone # / Ext</td>
                            </tr>
                            <tr height="35" style="background-color:#FFFFFF;vertical-align:middle;">
                                <s:if test="incident.submittedBy != null">
                                    <td><s:property  value="incident.submittedUserName"/> (<s:property value="incident.submittedBy"/>)
                                    <td><s:date name="incident.addTimestamp" format="MM/dd/yyyy hh:mm a"/></td>
                                </s:if>
                                <td><s:property  value="incident.submittedByPhone"/>  <s:property  value=" incident.submittedByPhoneExtn"/></td>
                                </tr>
                                <tr height="35" style="font-weight: bold;vertical-align:middle;">
                                    <td>Last updated by</td>
                                    <td colspan="2">Last updated Date</td>
                                </tr>
                                <tr height="35" style="background-color:#FFFFFF;vertical-align:middle;">
                                <s:if test="incident.lastUpdatedBy != null">
                                    <td><s:property  value="incident.lastUpdatedUserName"/> (<s:property value="incident.lastUpdatedBy"/>)</td>
                                    <td colspan="2"><s:date name="incident.lastUpdatedTimestamp" format="MM/dd/yyyy hh:mm a"/></td>
                                </s:if>
                            </tr>
                        </table>

                        <table cellspacing="0" cellpadding="2" width="100%">
                            <tr height="35" style="font-weight: bold;vertical-align:middle;">
                                <s:if test='(incident.reportStatus != "Accepted" && incident.delFlag !="Y" && currentUser.inquiry == false) && incident.recentRevison==true
                                      && (currentUser.hss  ||currentUser.supervisor || currentUser.manager || currentUser.vendor ||currentUser.vendorDirector) && (incident.openFollowups != null && incident.openFollowups == "N")'>
                                    <td>
                                        <s:a action="addFollowupReportFollowUpAction" cssClass="headerMenuLink" onclick="return addFollowup();">
                                            <s:param name="incidentNo" value="%{incident.incidentNo}"/>
                                            <s:param name="referralId" value="incident.refNo"/>
                                            <font size="2">Add Follow-up</font></s:a></td>
                                    </s:if>
                                    <s:if test='incident.hasFollowup == "Y"'>
                                    <td><s:a href='#' onclick='viewFollowupList(%{incident.incidentNo});'><font size="2">View Follow-up List</font>
                                        </s:a></td>
                                    </s:if>                       
                                <td><s:a action="viewOriginalIRIncidentAction" cssClass="headerMenuLink">
                                        <s:param name="incidentNo" value="%{incident.incidentNo}"/>
                                        <s:param name="referralId" value="incident.refNo"/><font size="2">View Original IR</font>
                                    </s:a></td>
                                <td><s:a href='#' onclick='viewIncidentRevisionList(%{incident.incidentNo},%{incident.refNo});'><font size="2">View IR Revisions</font>                                   
                                    </s:a></td></tr>
                                </s:if>
                    </table>

                    <table cellspacing="0" cellpadding="2" width="100%">
                        <tr><td><hr></td></tr>
                        <tr height="35" style="background-color:#FFFFFF;vertical-align:middle;"><td align="center"> 
                                <s:hidden id="referralId" name="incident.refNo"/>

                                <s:if test='incident.reportStatus != "Accepted" && incident.delFlag !="Y" && currentUser.inquiry == false
                                      && (currentUser.hss || currentUser.supervisor || currentUser.manager ||  
                                      currentUser.vendor || currentUser.vendorDirector) && incident.recentRevison && incident.eligibleVendor'>
                                    <s:if test='(incident.reportStatus == "Submitted" || incident.reportStatus == "Rejected" || incident.reportStatus == "Re-Submitted") || (incident.revisionNo == 0 && incident.reportStatus != "Submitted")'>
                                        <s:submit value="Submit" onclick="return setSubmitAction();">
                                            <s:param name="reportRevisionType" value="%{incident.reportRevisionType}"/>
                                            <s:param name="referralId" value="%{incident.refNo}"/>
                                        </s:submit> 
                                      
                                    </s:if>
                                </s:if>  
                                
                                <s:if test='incident.incidentNo !=null  &&
                                      incident.incidentNo!=0 && incident.delFlag !="Y" && currentUser.inquiry == false
                                      && (currentUser.complianceMonitor || currentUser.supervisor || currentUser.manager) && incident.recentRevison && incident.eligibleVendor'>
                                    <s:if test='incident.reportStatus != "Accepted" && incident.reportStatus != "Rejected"'>
                                       <s:if test='incident.openFollowups !=null && incident.openFollowups == "Y" && incident.reportStatus != "Accepted-Pending Follow-up" && incident.reportStatus !="Accepted-Request Follow-up"'>
                                           <s:submit theme="simple" id="accept" value="Accept - Pending Followup" onclick="return setStatusAction(7);"/>
                                       </s:if>
                                       <s:if test='incident.openFollowups !=null && incident.openFollowups == "N" && incident.reportStatus != "Accepted-Request Follow-up"'>
                                            <s:submit theme="simple" id="accept" value="Accept -  Request Followup" onclick="return setStatusAction(8)"/> 
                                       </s:if>
                                        <s:if test='(incident.openFollowups !=null && incident.openFollowups == "N")&& incident.reportStatus != "Rejected" && incident.reportStatus != "Accepted-Pending Follow-up" && incident.reportStatus !="Accepted-Request Follow-up"'>
                                            <s:submit theme="simple" id="accept" value="Accept" onclick="return setStatusAction(5);"/>
                                        </s:if>
                                       <s:if test='incident.reportStatus != "Accepted-Pending Follow-up" && incident.reportStatus !="Accepted-Request Follow-up"'>
                                            <s:submit theme="simple" id="reject" value="Reject" onclick="return setStatusAction(6);"/>
                                        </s:if>
                                        
                                    </s:if>
                                </s:if>
                                <s:if test='incident.reportStatus != "In Progress"'>
                                    <s:submit theme="simple" id="printIncident" value="Print"  onclick="return setPrintAction();"/>  
                                </s:if>
                                <input type="button" value="Cancel" onclick="return setCancelAction();">
                            </td>  
                        </tr>
                        <tr height="35" style="background-color:#FFFFFF;vertical-align:middle;"><td height="15"><span class="bold-red">*</span> Indicated fields are required</td></tr>
                    </table>
                </s:form>
            </div>
    </body>
</html>