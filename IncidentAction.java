/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.tx.state.tdcj.tcoommi.actions;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.validator.GenericValidator;
import us.tx.state.tdcj.tcoommi.beans.DisplayClass;
import us.tx.state.tdcj.tcoommi.beans.Email;
import us.tx.state.tdcj.tcoommi.beans.EmailEndPoint;
import us.tx.state.tdcj.tcoommi.beans.Offender;
import us.tx.state.tdcj.tcoommi.beans.UserInfo;
import us.tx.state.tdcj.tcoommi.beans.incident.Followup;
import us.tx.state.tdcj.tcoommi.beans.incident.Incident;
import us.tx.state.tdcj.tcoommi.beans.incident.TRAS;
import us.tx.state.tdcj.tcoommi.beans.referral.Referral;
import us.tx.state.tdcj.tcoommi.beans.referral.Vendor;
import us.tx.state.tdcj.tcoommi.cache.CacheMaster;
import us.tx.state.tdcj.tcoommi.cache.LookupMaster;
import us.tx.state.tdcj.tcoommi.constants.TCOOMMIConstants;
import us.tx.state.tdcj.tcoommi.framework.reports.ReportAction;
import us.tx.state.tdcj.tcoommi.service.CodeService;
import us.tx.state.tdcj.tcoommi.service.IncidentService;
import us.tx.state.tdcj.tcoommi.service.OffenderService;
import us.tx.state.tdcj.tcoommi.service.ReferralService;
import us.tx.state.tdcj.tcoommi.service.UserService;
import us.tx.state.tdcj.tcoommi.util.EmailUtil;
import us.tx.state.tdcj.tcoommi.util.IncidentUtil;

/**
 * @desc This class is the the main controller for the Incident report tracking
 * @author nThompson
 */
public class IncidentAction extends ReportAction {

    public IncidentAction() {
        this.incident = new Incident();
    }

    private final String INCIDENT_DETAIL = "incidentDetail";

    private final String FLAG_YES = "Y";
    private final String FLAG_NO = "N";
    private String sid;
    private String outputFileName;
    private String reportRevisionType;
    private String selectedVendors;
    private Referral referral;
    private Incident incident;
    private int referralId;
    private int incidentNo;
    private String caseWorkerId;
    private int revisionNo;
    private boolean validIncident;
    boolean openFollowupFlag;
    private Date incidentDateFrom;
    private Date incidentDateTo;
    private Date submitDateFrom;
    private Date submitDateTo;
    private Offender offender;

    IncidentService service = new IncidentService();
    UserService userService = new UserService();
    CodeService codeService = new CodeService();

    /**
     * This method validates all the required fields and other validation
     * condition for the Incident Report Detail screen
     *
     * @param incident
     * @throws Exception
     * @return boolean
     */
    public boolean validateIncidentDetails(Incident incident) throws Exception {

        boolean isCleanDataProvided = true;

        if (incident.getProgramType() == 0) {
            this.addActionMessage("Please select the required field, Program Type");
            isCleanDataProvided = false;
        }

        if (GenericValidator.isBlankOrNull(incident.getChargesCJstatus())) {
            this.addActionMessage("Please select the required field, Type of charges");
            isCleanDataProvided = false;
        }

        if (incident.getIncidentDate() == null) {
            this.addActionMessage("Please enter value for the required field, Date of Incident");
            isCleanDataProvided = false;
        }

        if (incident.getIncidentDate() != null && incident.getIncidentDate().after(new Date())) {
            this.addActionMessage("Date of Incident should not be past today's date");
            isCleanDataProvided = false;
        }

        if (incident.getIncidentDate() != null && !IncidentUtil.isValidDate(incident.getIncidentDate())) {
            this.addActionMessage("Enter a valid date in MM/dd/yyyy format, Date of Incident");
            isCleanDataProvided = false;
        }

        if (incident.getArrestType() == null) {
            this.addActionMessage("Please select the required field, Arrest/Detention Type");
            isCleanDataProvided = false;
        }

        if (GenericValidator.isBlankOrNull(incident.getIncidentDescription())) {
            this.addActionMessage("Please enter value for the required field, Incident Description");
            isCleanDataProvided = false;
        }

        if (incident.getIncidentDate() != null && !IncidentUtil.isValidDate(incident.getIncidentDate())) {
            this.addActionMessage("Enter a valid date in MM/dd/yyyy format, Date of Incident");
            isCleanDataProvided = false;
        }

        if (incident.isIncidentCarriedByMedia() && GenericValidator.isBlankOrNull(incident.getMediaName())) {
            this.addActionMessage("Please enter value for the required field, If Yes, What Media?");
            isCleanDataProvided = false;
        }

        if (!GenericValidator.isBlankOrNull(incident.getTcoommiNotifiedByPhoneFlag()) && incident.getTcoommiNotifiedByPhoneFlag().equals(FLAG_YES) && incident.getTcoommiNotifiedPhoneDate() == null) {
            this.addActionMessage("Please enter value for the required field, Date/Time TCOOMMI Notified-Phone");
            isCleanDataProvided = false;
        }
        
        if (!GenericValidator.isBlankOrNull(incident.getTcoommiNotifiedByPhoneFlag()) && incident.getTcoommiNotifiedByPhoneFlag().equals(FLAG_YES) && GenericValidator.isBlankOrNull(incident.getTcoommiNotifiedPhoneTime())) {
            this.addActionMessage("Please enter value for the required field, Time in HH:MM format for the Date/Time TCOOMMI Notified-Phone");
            isCleanDataProvided = false;
        }

        if (incident.getCaseWorkerNotifiedDate() == null) {
            this.addActionMessage("Please enter value for the required field, Date/Time Case Worker Notified");
            isCleanDataProvided = false;
        }
        
        if (incident.getProgramType() == 4 && incident.getHssNotifiedDate() == null) {
            this.addActionMessage("Please enter value for the required field, HSS Notify Date/Time");
            isCleanDataProvided = false;
        }
        
        if (incident.getProgramType() == 4 && GenericValidator.isBlankOrNull(incident.getHssNotifiedTime())) {
            this.addActionMessage("Please enter value for the required field, Time in HH:MM format for the HSS Notify Date/Time");
            isCleanDataProvided = false;
        }
        
        if (GenericValidator.isBlankOrNull(incident.getCwNotifytimeString())) {
            this.addActionMessage("Please enter value for the required field, Time in HH:MM format for the Date/Time Case Worker Notified");
            isCleanDataProvided = false;
        }

        if (incident.getCaseWorkerNotifiedDate() != null && incident.getCaseWorkerNotifiedDate().after(new Date())) {
            this.addActionMessage("Date/Time Case Worker Notified should not be past today's date");
            isCleanDataProvided = false;
        }

        if (incident.getCaseWorkerNotifiedDate() != null && !IncidentUtil.isValidDate(incident.getCaseWorkerNotifiedDate())) {
            this.addActionMessage("Enter a valid date in MM/dd/yyyy format, Date/ Time Case Worker Notified");
            isCleanDataProvided = false;
        }

        if (!GenericValidator.isBlankOrNull(incident.getCurrentStatusStr()) && incident.getCurrentStatusStr().equals("0")) {
            this.addActionMessage("Please select the required field, Current Incident Status");
            isCleanDataProvided = false;
        }

        if (incident.getTcoommiNotifiedPhoneDate() != null && !IncidentUtil.isValidDate(incident.getTcoommiNotifiedPhoneDate())) {
            this.addActionMessage("Enter a valid date in MM/dd/yyyy format, Date TCOOMMI Notified-Phone");
            isCleanDataProvided = false;
        }

        if (incident.getTcoommiNotifiedPhoneDate() != null && incident.getTcoommiNotifiedPhoneDate().after(new Date())) {
            this.addActionMessage("Date TCOOMMI Notified-Phone should not be past today's date");
            isCleanDataProvided = false;
        }

        if ((incident.getProgramType() == 1 || incident.getProgramType() == 2) && (!GenericValidator.isBlankOrNull(incident.getIncidentType())
                && incident.getIncidentType().equals("8")) && incident.getRefusalOfServiceForm() == ' ') {
            this.addActionMessage("Please select the required field, Refusal of Service Form Indicator");
            isCleanDataProvided = false;
        }

        if (!GenericValidator.isBlankOrNull(incident.getIncidentType()) && incident.getIncidentType().equals("0")) {
            this.addActionMessage("Please select the required field, Incident Type");
            isCleanDataProvided = false;
        }

        if (!GenericValidator.isBlankOrNull(incident.getIncidentType()) && incident.getIncidentType().equals("2")) {
            if (!GenericValidator.isBlankOrNull(incident.getArrestType()) && incident.getArrestType().equals("0")) {
                this.addActionMessage("Please select the required field, Arrest Type");
                isCleanDataProvided = false;
            }
        }

        if (!GenericValidator.isBlankOrNull(incident.getIncidentType()) && incident.getIncidentType().equals("14")
                && GenericValidator.isBlankOrNull(incident.getIncidentOtherComments())) {
            this.addActionMessage("Please enter value for the required field, Incident Type Other Comments");
            isCleanDataProvided = false;
        }

        if (!GenericValidator.isBlankOrNull(incident.getCurrentStatusStr()) && incident.getCurrentStatusStr().equals("2")
                && incident.getJailDate() == null) {
            this.addActionMessage("Please enter value for the required field, Jail Date");
            isCleanDataProvided = false;
        }

        if (!GenericValidator.isBlankOrNull(incident.getCurrentStatusStr()) && incident.getCurrentStatusStr().equals("3")
                && incident.getHospitalDate() == null) {
            this.addActionMessage("Please enter value for the required field, Hospital Date");
            isCleanDataProvided = false;
        }

        if (!GenericValidator.isBlankOrNull(incident.getCurrentStatusStr())
                && (incident.getCurrentStatusStr().equals("6") || incident.getCurrentStatusStr().equals("12"))
                && GenericValidator.isBlankOrNull(incident.getResiFacilityName())) {
            this.addActionMessage("Please enter value for the required field, Residential Facility Name");
            isCleanDataProvided = false;
        }

        if (!GenericValidator.isBlankOrNull(incident.getCurrentStatusStr())
                && (incident.getCurrentStatusStr().equals("6") || incident.getCurrentStatusStr().equals("12"))
                && GenericValidator.isBlankOrNull(incident.getLocation())) {
            this.addActionMessage("Please enter value for the required field, Residential Facility Location");
            isCleanDataProvided = false;
        }

        if (!GenericValidator.isBlankOrNull(incident.getCurrentStatusStr()) && incident.getProgramType() == 2 && incident.getCurrentStatusStr().equals("7")
                && incident.getDetentionDate() == null) {
            this.addActionMessage("Please enter value for the required field, Detention Date");
            isCleanDataProvided = false;
        }

        if (!GenericValidator.isBlankOrNull(incident.getCurrentStatusStr()) && incident.getCurrentStatusStr().equals("11")
                && GenericValidator.isBlankOrNull(incident.getCurrStatusComments())) {
            this.addActionMessage("Please enter value for the required field, Current Incident Status Comments");
            isCleanDataProvided = false;
        }

        if ((incident.getProgramType() == 1 || incident.getProgramType() == 2 || incident.getProgramType() == 3)
                && (!GenericValidator.isBlankOrNull(incident.getIncidentType()) && incident.getIncidentType().equals("6"))
                && GenericValidator.isBlankOrNull(incident.getPsychHospType())) {
            this.addActionMessage("Please select the required field, Psych Hospitalization Type");
            isCleanDataProvided = false;
        }

        if ((incident.getProgramType() == 1 || incident.getProgramType() == 2) && incident.getCaseCloseDate() == null
                && !GenericValidator.isBlankOrNull(incident.getIncidentType()) && incident.getIncidentType().equals("4")) {
            this.addActionMessage("Please enter value for the required field, Case Closure Date");
            isCleanDataProvided = false;
        }

        if (!GenericValidator.isBlankOrNull(incident.getIncidentType()) && incident.getIncidentType().equals("13")
                && GenericValidator.isBlankOrNull(incident.getDischargeReason())) {
            this.addActionMessage("Please enter value for the required field, Discharge Reason");
            isCleanDataProvided = false;
        }

        if (incident.getJailDate() != null && !IncidentUtil.isValidDate(incident.getJailDate())) {
            this.addActionMessage("Enter a valid date in MM/dd/yyyy format, Jail Date");
            isCleanDataProvided = false;
        }

        if (incident.getJailDate() != null && incident.getJailDate().after(new Date())) {
            this.addActionMessage("Jail Date should not be past today's date");
            isCleanDataProvided = false;
        }

        if (incident.getHospitalDate() != null && !IncidentUtil.isValidDate(incident.getHospitalDate())) {
            this.addActionMessage("Enter a valid date in MM/dd/yyyy format, Hospital Date");
            isCleanDataProvided = false;
        }

        if (incident.getHospitalDate() != null && incident.getHospitalDate().after(new Date())) {
            this.addActionMessage("Hospital Date should not be past today's date");
            isCleanDataProvided = false;
        }

        if (GenericValidator.isBlankOrNull(incident.getCaseSummary())) {
            this.addActionMessage("Please enter value for the required field, Brief Case Summary");
            isCleanDataProvided = false;
        }

        return isCleanDataProvided;
    }

    /**
     * @desc This method will be invoked from the Referral detail screen to
     * bring up the incident detail entry form with pre populated
     * values from Referral 
     * @exception Exception
     * @return String
     */
    public String addIncidentReport() throws Exception {
        incident = new Incident();
        this.referral = new ReferralService().fetchReferralById(this.referralId);
        int serviceType = this.referral.getService().getType();
        sid = this.referral.getSid();
        incident.setRecentRevison(true);
        Date serviceBeginDate = this.referral.getService().getStartDate();
        if (serviceBeginDate != null) {
            String timeInCurrentService = IncidentUtil.getTimeInCurrentService(serviceBeginDate);
            incident.setTimeInCurrentServiceStr(timeInCurrentService);
        }
        try{
            TRAS tras = service.getTRASDetailsBySid(sid);
            if(tras != null){
                incident.setTrasAssessmentTool(tras.getTrasAssessmentTool());
                incident.setTrasRiskLevel(GenericValidator.isBlankOrNull(tras.getTrasRiskLevel())?tras.getOriginalRiskLevel() :tras.getTrasRiskLevel());
            }
        }catch(Exception e){
            System.out.println("Problem caused in TRAS "+e.toString());
        }
      if(getCurrentUser().isVendor() || getCurrentUser().isVendorDirector()){
            incident.setEligibleVendor(IncidentUtil.isEligibleVendor(getCurrentUser(), this.referral.getVendor()));
        }else if(getCurrentUser().isComplianceMonitor()){
            String vendorKey = this.referral.getVendor().getRegion() + "_" + this.getReferral().getVendor().getDistrict();
            String complianceMonitorId = codeService.getVendorComplianceMonitor(vendorKey);
            if(getCurrentUser().getUserId().equalsIgnoreCase(complianceMonitorId)){
                incident.setEligibleVendor(true);
            }
        }else{
            incident.setEligibleVendor(true);
         }
        
        this.setReferral(this.referral);
        incident.setProgramType(IncidentUtil.getProgramType(serviceType));
        incident.setRefNo(this.referral.getRefNo());
        incident.setCriminalStatus(IncidentUtil.getCriminalJusticeStatus(this.referral));
        incident.setReportType(TCOOMMIConstants.REPORT_TYPE_INCIDENT);
        incident.setReportRevisionType(TCOOMMIConstants.REPORT_REVISED);
        incident.setReportStatus(TCOOMMIConstants.REPORT_STATUS_INPROGRESS);
        incident.setCurrentStatusDate(new Date());
        incident.setCaseWorkerId(getCurrentUser().getUserId());
        incident.setUserId(getCurrentUser().getUserId());
       
        this.setIncident(incident);
        return INCIDENT_DETAIL;
    }

    /**
     * @desc This method is used to reset the incident to the initial after an
     * exceptional validation condition
     * @exception Exception
     * @return String
     */
    public String resetIncident() throws Exception {
        incident.setReportType(TCOOMMIConstants.REPORT_TYPE_INCIDENT);
        incident.setReportRevisionType(this.reportRevisionType);
        if (this.incidentNo == 0) {
            incident.setReportStatus(TCOOMMIConstants.REPORT_STATUS_INPROGRESS);
        } else {
            Incident incTemp = service.getIncident(this.incidentNo, this.revisionNo, TCOOMMIConstants.REPORT_REVISED);
            if (incTemp != null) {
                incident.setReportStatus(IncidentUtil.getReportStatus(incTemp.getReportStatusCode()));
                incident.setRevisionNo(this.revisionNo);
                incident.setLastUpdatedBy(incTemp.getLastUpdatedBy());
                incident.setLastUpdatedTimestamp(incTemp.getLastUpdatedTimestamp());
                incident.setAddTimestamp(incTemp.getAddTimestamp());
            }
        }
        incident.setIncidentNo(this.incidentNo);
        int recentRevNo = service.getMaxIncidentRevisionNo(this.incidentNo);
        if (this.revisionNo >= recentRevNo) {
            incident.setRecentRevison(true);
        } else {
            incident.setRecentRevison(false);
        }
        restoreInputData();
        return INCIDENT_DETAIL;
    }

    /**
     * @desc This method will fetch the original incident report
     * @exception Exception
     * @return String
     */
    public String viewOriginalIR() throws Exception {
        incident = service.getIncident(this.incidentNo, 0, TCOOMMIConstants.REPORT_REVISED);
        refreshIncidentDetails();
        return INCIDENT_DETAIL;
    }

    /**
     * @desc This method will fetch the last updated incident report
     * @exception Exception
     * @return String
     */
    public String viewLastUpdatedIR() throws Exception {
        int maxRevNo = service.getMaxIncidentRevisionNo(incidentNo);
        incident = service.getIncident(this.incidentNo, maxRevNo, TCOOMMIConstants.REPORT_REVISED);
        setCurrentOffenderInfo(incident.getSid());
        this.referralId = incident.getRefNo();
        this.referral = new ReferralService().fetchReferralById(this.referralId);
        getActiveUsers();
        refreshIncidentDetails();
        incident.setReportRevisionType(TCOOMMIConstants.REPORT_REVISED);
        incident.setRecentRevison(true);
        if(getCurrentUser().isVendor() || getCurrentUser().isVendorDirector()){
            incident.setEligibleVendor(IncidentUtil.isEligibleVendor(getCurrentUser(), this.referral.getVendor()));
        }else if(getCurrentUser().isComplianceMonitor()){
            String vendorKey = this.referral.getVendor().getRegion() + "_" + this.getReferral().getVendor().getDistrict();
            String complianceMonitorId = codeService.getVendorComplianceMonitor(vendorKey);
            if(getCurrentUser().getUserId().equalsIgnoreCase(complianceMonitorId)){
                incident.setEligibleVendor(true);
            }
        }else{
            incident.setEligibleVendor(true);
         }
        return INCIDENT_DETAIL;
    }

    /**
     * @desc This method will fetch the incident report by the revision No
     * @exception Exception
     * @return String
     */
    public String viewIncidentByRevision() throws Exception {
        incident = service.getIncident(incidentNo, revisionNo, TCOOMMIConstants.REPORT_REVISED);
        getActiveUsers();
        refreshIncidentDetails();
        int recentRevNo = service.getMaxIncidentRevisionNo(incidentNo);
        if (this.revisionNo >=recentRevNo) {
            incident.setRecentRevison(true);
        } else {
            incident.setRecentRevison(false);
        }
        incident.setReportRevisionType(TCOOMMIConstants.REPORT_REVISED);
        this.referral = new ReferralService().fetchReferralById(incident.getRefNo());
        
       if(getCurrentUser().isVendor() || getCurrentUser().isVendorDirector()){
            incident.setEligibleVendor(IncidentUtil.isEligibleVendor(getCurrentUser(), this.referral.getVendor()));
        }else if(getCurrentUser().isComplianceMonitor()){
            String vendorKey = this.referral.getVendor().getRegion() + "_" + this.getReferral().getVendor().getDistrict();
            String complianceMonitorId = codeService.getVendorComplianceMonitor(vendorKey);
            if(getCurrentUser().getUserId().equalsIgnoreCase(complianceMonitorId)){
                incident.setEligibleVendor(true);
            }
        }else{
            incident.setEligibleVendor(true);
         }
        return INCIDENT_DETAIL;
    }

    /**
     * @desc This method restores the data for the incident detail screen
     * @exception Exception
     */
    public void restoreInputData() throws Exception {
        this.referral = new ReferralService().fetchReferralById(this.referralId);
        int serviceType = this.referral.getService().getType();
        Date serviceBeginDate = this.referral.getService().getStartDate();
        if (serviceBeginDate != null) {
            String timeInCurrentService = IncidentUtil.getTimeInCurrentService(serviceBeginDate);
            incident.setTimeInCurrentServiceStr(timeInCurrentService);
        }
        sid = this.referral.getSid();
        this.setReferral(this.referral);
        incident.setProgramType(IncidentUtil.getProgramType(serviceType));
        incident.setRefNo(referral.getRefNo());
        incident.setCurrentStatusStr(String.valueOf(incident.getCurrentStatusStr()));
        incident.setArrestType(incident.getArrestType());
        incident.setIncidentType(incident.getIncidentType());
        incident.setUserId(getCurrentUser().getUserId());
        incident.setCriminalStatus(IncidentUtil.getCriminalJusticeStatus(referral));
        incident.setCurrentStatusDate(new Date());
       if(getCurrentUser().isVendor() || getCurrentUser().isVendorDirector()){
            incident.setEligibleVendor(IncidentUtil.isEligibleVendor(getCurrentUser(), this.referral.getVendor()));
        }else if(getCurrentUser().isComplianceMonitor()){
            String vendorKey = this.referral.getVendor().getRegion() + "_" + this.getReferral().getVendor().getDistrict();
            String complianceMonitorId = codeService.getVendorComplianceMonitor(vendorKey);
            if(getCurrentUser().getUserId().equalsIgnoreCase(complianceMonitorId)){
                incident.setEligibleVendor(true);
            }
        }else{
            incident.setEligibleVendor(true);
         }
        this.setIncident(incident);
    }

    /**
     * @desc This method will submit the incident report and create the
     * following two records in TATCMI_INCIDENT table 1.Original Report - No
     * update will be performed on this record 2.Revision Report - All further
     * updates will be on this record
     * @exception Exception
     * @return String
     */
    public String submitIncident() throws Exception {
      
        boolean isNewIncident = false;
        String complianceMonitorId = null;
        String currRptStatusCode = request.getParameter("statusCde");
        int reportStatusCode = 0;
        if (!GenericValidator.isBlankOrNull(currRptStatusCode)) {
            reportStatusCode = Integer.parseInt(currRptStatusCode);
        }
        incident.setReportStatusCode(reportStatusCode);
        this.referral = new ReferralService().fetchReferralById(this.referralId);

        String vendorKey = this.referral.getVendor().getRegion() + "_" + this.getReferral().getVendor().getDistrict();
        if (IncidentUtil.getHssLocations().containsKey(this.getReferral().getVendor().getDistrict())) {
            complianceMonitorId = codeService.getHssLocationByVendor(vendorKey);
        } else {
            complianceMonitorId = codeService.getVendorComplianceMonitor(vendorKey);
        }
        incident.setComplianceMonitorId(complianceMonitorId);
        boolean isValidInputs = validateIncidentDetails(incident);
        if (!isValidInputs) {
            incident.setRecentRevison(true);
            return resetIncident();
        }

        if (this.incidentNo == 0 && this.revisionNo == 0) {
            isNewIncident = true;
        }

        buildIncidentToSave();

        if (isNewIncident) {
            Integer maxIncidentNo = service.getMaxIncidentNo();
            incident.setIncidentNo(maxIncidentNo + 1);
            incident.setReportRevisionType(TCOOMMIConstants.REPORT_REVISED);
            service.submitIncident(incident);

            this.addActionMessage("Incident Report(Original) # " + incident.getIncidentNo() +" successfully submitted");
            System.out.println("Incident #:"+incident.getIncidentNo() +" created");
        } else if (this.incidentNo != 0) {
            System.out.println("Processing Incident #:"+incidentNo);
            int maxRevNo = service.getMaxIncidentRevisionNo(incidentNo);
            Incident tempIncident = service.getIncident(this.incidentNo,maxRevNo, TCOOMMIConstants.REPORT_REVISED);
            incident.setRevisionComments(tempIncident.getRevisionComments());
            if (incident.getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_REJECTED) {
                incident.setReportStatusCode(TCOOMMIConstants.REPORT_STATUSCODE_RE_SUBMITTED);
            }
            incident.setIncidentNo(this.incidentNo);
            incident.setRevisionNo(maxRevNo + 1);
            incident.setReportRevisionType(TCOOMMIConstants.REPORT_REVISED);
            service.submitIncident(incident);
            service.updateIncidentMaxRevInd(this.incidentNo, maxRevNo);
            incident = service.getIncident(incident.getIncidentNo(), incident.getRevisionNo(), TCOOMMIConstants.REPORT_REVISED);
            
            this.addActionMessage("Incident Report(Revised) # " + incident.getIncidentNo() + " (Revision # " + incident.getRevisionNo() + " )" + " successfully submitted");
        }

        refreshIncidentDetails();
        incident.setRecentRevison(true);
        sendEmail(incident);
        this.setIncident(incident);
       
        return INCIDENT_DETAIL;
    }

    /**
     * @desc This method will build the Incident object to store in the database
     * @exception Exception
     */
    public void buildIncidentToSave() throws Exception {
        Date serviceBeginDate = this.referral.getService().getStartDate();
        if (serviceBeginDate != null) {
            String timeInCurrentService = IncidentUtil.getTimeInCurrentService(serviceBeginDate);
            incident.setTimeInCurrentServiceStr(timeInCurrentService);
        }
        
        try{
            TRAS tras = service.getTRASDetailsBySid(this.referral.getSid());
            if(tras != null){
                incident.setTrasAssessmentTool(tras.getTrasAssessmentTool());
                incident.setTrasRiskLevel(GenericValidator.isBlankOrNull(tras.getTrasRiskLevel())?tras.getOriginalRiskLevel() :tras.getTrasRiskLevel());
            }
        }catch(Exception e){
            System.out.println("Problem caused in TRAS"+e.toString());
        }
        
        incident.setCriminalStatusCode(Integer.parseInt(GenericValidator.isBlankOrNull(this.referral.getCrimJusticeStatus())?"0":this.referral.getCrimJusticeStatus())); //fix
        incident.setSid(this.referral.getSid());
        incident.setUserId(getCurrentUser().getUserId());
        if (incident.isSubstanceAbuseRelated()) {
            incident.setSubstanceAbuseRelatedStr(FLAG_YES);
        } else {
            incident.setSubstanceAbuseRelatedStr(FLAG_NO);
        }

        if (incident.isIncidentCarriedByMedia()) {
            incident.setIncidentCarriedByMediaStr(FLAG_YES);
        } else {
            incident.setIncidentCarriedByMediaStr(FLAG_NO);
        }

        if (!GenericValidator.isBlankOrNull(incident.getCurrentStatusStr())) {
            incident.setCurrentStatusCode(Integer.parseInt(GenericValidator.isBlankOrNull(incident.getCurrentStatusStr())?"0":incident.getCurrentStatusStr()));
        }

        switch (incident.getCurrentStatusCode()) {
            case 2:
                incident.setHospitalDate(null);
                incident.setCurrentHospitalMedListSent(TCOOMMIConstants.NO);
                incident.setCurrStatusComments("");
                incident.setResiFacilityName(null);
                incident.setLocation(null);
                incident.setCurrentResiFacilityMedListSent(TCOOMMIConstants.NO);
                incident.setDetentionDate(null);
                incident.setCurrentDetentionMedListSent(TCOOMMIConstants.NO);
                break;
            case 3:
                incident.setJailDate(null);
                incident.setCurrentJailMedListSent(TCOOMMIConstants.NO);
                incident.setCurrStatusComments("");
                incident.setResiFacilityName(null);
                incident.setLocation(null);
                incident.setCurrentResiFacilityMedListSent(TCOOMMIConstants.NO);
                incident.setDetentionDate(null);
                incident.setCurrentDetentionMedListSent(TCOOMMIConstants.NO);
                break;
            case 6:
            case 12:
                incident.setJailDate(null);
                incident.setCurrentJailMedListSent(TCOOMMIConstants.NO);
                incident.setCurrStatusComments("");
                incident.setHospitalDate(null);
                incident.setCurrentHospitalMedListSent(TCOOMMIConstants.NO);
                incident.setDetentionDate(null);
                incident.setCurrentDetentionMedListSent(TCOOMMIConstants.NO);
                break;
            case 11:
                incident.setHospitalDate(null);
                incident.setCurrentHospitalMedListSent(TCOOMMIConstants.NO);
                incident.setJailDate(null);
                incident.setCurrentJailMedListSent(TCOOMMIConstants.NO);
                incident.setDetentionDate(null);
                incident.setCurrentDetentionMedListSent(TCOOMMIConstants.NO);
                incident.setResiFacilityName(null);
                incident.setLocation(null);
                incident.setCurrentResiFacilityMedListSent(TCOOMMIConstants.NO);
                break;
            default:
                break;
        }
        if (!GenericValidator.isBlankOrNull(incident.getArrestType())) {
            incident.setArrestTypeCode(Integer.parseInt(GenericValidator.isBlankOrNull(incident.getArrestType())?"0":incident.getArrestType()));
        }

        if (!GenericValidator.isBlankOrNull(incident.getIncidentType())) {
            incident.setIncidentTypeCode(Integer.parseInt(GenericValidator.isBlankOrNull(incident.getIncidentType())?"0":incident.getIncidentType()));
        }

        switch (incident.getIncidentTypeCode()) {
            case 3:
                incident.setIncidentOtherComments(null);
                incident.setPsychHospType(null);
                incident.setRefusalOfServiceForm(TCOOMMIConstants.NO);
                incident.setDischargeReason(null);
                break;
            case 4:
                incident.setApsReportNo("0");
                incident.setIncidentOtherComments(null);
                incident.setPsychHospType(null);
                incident.setRefusalOfServiceForm(TCOOMMIConstants.NO);
                incident.setDischargeReason(null);
                break;
            case 6:
                incident.setApsReportNo("0");
                incident.setIncidentOtherComments(null);
                incident.setRefusalOfServiceForm(TCOOMMIConstants.NO);
                incident.setDischargeReason(null);
                break;
            case 8:
                incident.setApsReportNo("0");
                incident.setIncidentOtherComments(null);
                incident.setPsychHospType(null);
                incident.setDischargeReason(null);
                break;
            case 13:
                incident.setApsReportNo("0");
                incident.setIncidentOtherComments(null);
                incident.setPsychHospType(null);
                incident.setRefusalOfServiceForm(TCOOMMIConstants.NO);
                break;
            case 14:
                incident.setApsReportNo("0");
                incident.setPsychHospType(null);
                incident.setRefusalOfServiceForm(TCOOMMIConstants.NO);
                incident.setDischargeReason(null);
                break;
            default:
                break;
        }

        if (GenericValidator.isBlankOrNull(incident.getApsReportNo())) {
            incident.setApsReportNo("0");
        }
        incident.setUserId(getCurrentUser().getUserId());

        Timestamp cwNotifytimestamp = IncidentUtil.getTimestamp(incident.getCaseWorkerNotifiedDate(),
                incident.getCwNotifytimeString(),
                incident.getCwNotifyAMPM());
        incident.setCaseWorkerNotifiedTimestamp(cwNotifytimestamp);

        Timestamp tcoommiNfyPhoneTimestamp = IncidentUtil.getTimestamp(incident.getTcoommiNotifiedPhoneDate(),
                incident.getTcoommiNotifiedPhoneTime(),
                incident.getTcoommiNotifiedPhoneampm());
        incident.setTcoommiNfyPhTimestamp(tcoommiNfyPhoneTimestamp);

        Timestamp tcoommiNfyWritingTimestamp = IncidentUtil.getTimestamp(incident.getTcoommiNotifiedWritingDate(),
                incident.getTcoommiNotifiedWritingTime(),
                incident.getTcoommiNotifiedWritingampm());
        incident.setTcoommiNfyWritTimestamp(tcoommiNfyWritingTimestamp);

        Timestamp hssNfyWritingTimestamp = IncidentUtil.getTimestamp(incident.getHssNotifiedDate(),
                incident.getHssNotifiedTime(),
                incident.getHssNotifiedampm());
        incident.setHssNotifiedTimestamp(hssNfyWritingTimestamp);

        Timestamp tcoommiSupvrNfyTimestamp = IncidentUtil.getTimestamp(incident.getTcoommiSupvrNotifiedDate(),
                incident.getTcoommiSupvrNotifiedTime(),
                incident.getTcoommiSupvrNotifiedampm());
        incident.setTcoommiSupvrNfyTimestamp(tcoommiSupvrNfyTimestamp);

        incident.setSubmittedBy(getCurrentUser().getUserId());
        
        incident.setMaxRevInd("Y");

        System.out.println(this.incident.toString());
    }

    /**
     * @desc This method will refresh the Incident detail screen with the
     * updated information after save
     * @exception Exception
     */
    public void refreshIncidentDetails() throws Exception {
        this.referral = new ReferralService().fetchReferralById(incident.getRefNo());
        int serviceType = this.referral.getService().getType();
        if (incident.getProgramType() == 0) {
            incident.setProgramType(IncidentUtil.getProgramType(serviceType));
        }
        Date serviceBeginDate = this.referral.getService().getStartDate();
        if (serviceBeginDate != null) {
            String timeInCurrentService = IncidentUtil.getTimeInCurrentService(serviceBeginDate);
            incident.setTimeInCurrentServiceStr(timeInCurrentService);
        }
        incident.setVendor(this.referral.getVendor());
        incident.setServiceTypeName(this.referral.getService().getTypeName());
        incident.setCriminalStatus(IncidentUtil.getCriminalJusticeStatus(this.referral));
        incident.setReportRevisionType(TCOOMMIConstants.REPORT_REVISED);
        incident.setReportType(TCOOMMIConstants.REPORT_TYPE_INCIDENT);
        incident.setIncidentNo(this.incidentNo == 0 ? incident.getIncidentNo() : this.incidentNo);
        incident.setReportStatus(IncidentUtil.getReportStatus(incident.getReportStatusCode()));
        incident.setCurrentStatusDate(incident.getLastUpdatedTimestamp() == null ? new Date() : incident.getLastUpdatedTimestamp());
        incident.setLastUpdatedTimestamp(incident.getLastUpdatedTimestamp() == null
                ? new Timestamp(new Date().getTime()) : incident.getLastUpdatedTimestamp());
        incident.setAddTimestamp(incident.getAddTimestamp() == null ? new Timestamp(new Date().getTime()) : incident.getAddTimestamp());
        incident.setCurrentStatusStr(incident.getCurrentStatusCode() == 0 ? String.valueOf(incident.getCurrentStatusStr())
                : String.valueOf(incident.getCurrentStatusCode()));
        incident.setArrestType(incident.getArrestTypeCode() == 0 ? String.valueOf(incident.getArrestType())
                : String.valueOf(incident.getArrestTypeCode()));
        incident.setIncidentType(incident.getIncidentTypeCode() == 0 ? String.valueOf(incident.getIncidentType())
                : String.valueOf(incident.getIncidentTypeCode()));
   
        try{
                TRAS tras = service.getTRASDetailsBySid(incident.getSid());
                if(tras != null){
                    incident.setTrasAssessmentTool(tras.getTrasAssessmentTool());
                    incident.setTrasRiskLevel(GenericValidator.isBlankOrNull(tras.getTrasRiskLevel())?tras.getOriginalRiskLevel() :tras.getTrasRiskLevel());
                }
          }catch(Exception e){
            System.out.println("Problem caused in TRAS"+e.toString());
        }
        
        if (incident.isSubstanceAbuseRelated()
                || (incident.getSubstanceAbuseRelatedStr() != null && incident.getSubstanceAbuseRelatedStr().equals(FLAG_YES))) {
            incident.setSubstanceAbuseRelated(true);
        } else {
            incident.setSubstanceAbuseRelated(false);
        }

        if (incident.isIncidentCarriedByMedia() || (incident.getIncidentCarriedByMediaStr() != null && incident.getIncidentCarriedByMediaStr().equals(FLAG_YES))) {
            incident.setIncidentCarriedByMedia(true);
        } else {
            incident.setIncidentCarriedByMedia(false);
        }

        if (incident.getReportedIn24hrs() != ' ' && incident.getReportedIn24hrs() == 'Y') {
            incident.setReportedIn24hrs('Y');
        } else {
            incident.setReportedIn24hrs('Y');
        }

        incident.setCriminalStatus(IncidentUtil.getCriminalJusticeStatus(this.referral));
        String cwTime = IncidentUtil.formatTimestamp(incident.getCaseWorkerNotifiedTimestamp());
        if (!GenericValidator.isBlankOrNull(cwTime)) {
            incident.setCaseWorkerNotifiedDate(IncidentUtil.getFormattedDate(cwTime.split(" ")[0]));
            incident.setCwNotifytimeString(cwTime.split(" ")[1]);
            incident.setCwNotifyAMPM(cwTime.split(" ")[2]);
        }
        String tcoommiNfyPhoneTS = IncidentUtil.formatTimestamp(incident.getTcoommiNfyPhTimestamp());
        if (!GenericValidator.isBlankOrNull(tcoommiNfyPhoneTS)) {
            incident.setTcoommiNotifiedPhoneDate(IncidentUtil.getFormattedDate(tcoommiNfyPhoneTS.split(" ")[0]));
            incident.setTcoommiNotifiedPhoneTime(tcoommiNfyPhoneTS.split(" ")[1]);
            incident.setTcoommiNotifiedPhoneampm(tcoommiNfyPhoneTS.split(" ")[2]);
        }
        String tcoommiNfyWritingTS = IncidentUtil.formatTimestamp(incident.getTcoommiNfyWritTimestamp());
        if (!GenericValidator.isBlankOrNull(tcoommiNfyWritingTS)) {
            incident.setTcoommiNotifiedWritingDate(IncidentUtil.getFormattedDate(tcoommiNfyWritingTS.split(" ")[0]));
            incident.setTcoommiNotifiedWritingTime(tcoommiNfyWritingTS.split(" ")[1]);
            incident.setTcoommiNotifiedWritingampm(tcoommiNfyWritingTS.split(" ")[2]);
        }
        String hssNfyTS = IncidentUtil.formatTimestamp(incident.getHssNotifiedTimestamp());
        if (!GenericValidator.isBlankOrNull(hssNfyTS)) {
            incident.setHssNotifiedDate(IncidentUtil.getFormattedDate(hssNfyTS.split(" ")[0]));
            incident.setHssNotifiedTime(hssNfyTS.split(" ")[1]);
            incident.setHssNotifiedampm(hssNfyTS.split(" ")[2]);
        }
        String tcoommiSupvrNfyTS = IncidentUtil.formatTimestamp(incident.getTcoommiSupvrNfyTimestamp());
        if (!GenericValidator.isBlankOrNull(tcoommiSupvrNfyTS)) {
            incident.setTcoommiSupvrNotifiedDate(IncidentUtil.getFormattedDate(tcoommiSupvrNfyTS.split(" ")[0]));
            incident.setTcoommiSupvrNotifiedTime(tcoommiSupvrNfyTS.split(" ")[1]);
            incident.setTcoommiSupvrNotifiedampm(tcoommiSupvrNfyTS.split(" ")[2]);
        }

        ArrayList<Followup> followupList = service.getFollowUpListByIncident(incident.getIncidentNo());
         incident.setOpenFollowups(FLAG_NO);
        if (followupList != null && !followupList.isEmpty()) {
             for (Followup followup : followupList) {
                int maxRevNo = service.getMaxFollowUpRevisionNo(followup.getFollowUpNo());
                if (followup.getRevisionNo() == maxRevNo && followup.getReportStatusCode() != TCOOMMIConstants.REPORT_STATUSCODE_ACCEPTED) {
                    incident.setOpenFollowups(FLAG_YES);
                    break;
                }
            }
            incident.setHasFollowup(FLAG_YES);
           
        } else {
            incident.setHasFollowup(FLAG_NO);
        }

        int recentRevNo = service.getMaxIncidentRevisionNo(incident.getIncidentNo()== 0?this.incidentNo:incident.getIncidentNo());
        if (this.revisionNo >=recentRevNo) {
            incident.setRecentRevison(true);
        } else {
            incident.setRecentRevison(false);
        }
        UserInfo submittedUser = userService.getUserInfoByUserId(incident.getSubmittedBy());
        UserInfo caseWorker = userService.getUserInfoByUserId(incident.getCaseWorkerId());
        incident.setCaseWorkerName(caseWorker != null ? caseWorker.getUsrFirstName() + " " + caseWorker.getUsrLastName() : "");
        incident.setCaseWorkerPhone(caseWorker != null ? caseWorker.getPhoneNo() : "");
        incident.setCaseWorkerPhoneExt(caseWorker != null ? caseWorker.getPhoneExtn() : " ");
        incident.setSubmittedUserName(service.getUserNameById(incident.getSubmittedBy()));
        incident.setLastUpdatedUserName(service.getUserNameById(incident.getLastUpdatedBy()));
        incident.setSubmittedByPhone(submittedUser != null ? submittedUser.getPhoneNo() : "");
        incident.setSubmittedByPhoneExtn(submittedUser != null ? submittedUser.getPhoneExtn() : "");
    }

    /**
     * @desc This method will be called to change the incident report status to
     * rejected,approval etc
     * @exception Exception
     * @return String
     */
    public String updateIncidentReportStatus() throws Exception {
        String approvalCode = request.getParameter("statusCde");
        int reportStatusCode = 0;
        if (!GenericValidator.isBlankOrNull(approvalCode)) {
            reportStatusCode = Integer.parseInt(approvalCode);
        }
      
        if (GenericValidator.isBlankOrNull(incident.getRevisionComments())
                && reportStatusCode == TCOOMMIConstants.REPORT_STATUSCODE_REJECTED) {
            this.addActionMessage("Please enter value for the required field, Revision Comments");
            incident = service.getIncident(this.incidentNo, this.revisionNo, TCOOMMIConstants.REPORT_REVISED);
            refreshIncidentDetails();
             this.setIncident(incident);
                return INCIDENT_DETAIL;
        }
         incident.setReportStatusCode(reportStatusCode);

        if (incident.getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_ACCEPTED_PENDING_FOLLOWUP) {
            ArrayList<Followup> followupList = service.getFollowUpListByIncident(incident.getIncidentNo());
            if (followupList != null && !followupList.isEmpty()) {
                for (Followup followup : followupList) {
                    if (followup.getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_SUBMITTED
                            || followup.getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_RE_SUBMITTED) {
                        incident.setCaseWorkerId(followup.getCaseWorkerId());
                        break;
                    }

                }
            }
        }
        service.updateIncidentReportStatus(reportStatusCode,  GenericValidator.isBlankOrNull(incident.getRevisionComments())?"":incident.getRevisionComments(),
                getCurrentUser().getUserId(), this.incidentNo, this.revisionNo, this.caseWorkerId);
        incident = service.getIncident(this.incidentNo, this.revisionNo, TCOOMMIConstants.REPORT_REVISED);
        refreshIncidentDetails();
        this.addActionMessage("Incident Report # " + this.incidentNo + " successfully " + incident.getReportStatus());
        this.setIncident(incident);
        if(incident.getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_REJECTED){
            sendEmail(incident);
        }
        return INCIDENT_DETAIL;
    }

    /**
     * @desc This method generate the printing the incident report in pdf format
     * @exception Exception
     * @return String
     */
    public String generatePDFReport()
            throws Exception {
        this.addParm(1, TCOOMMIConstants.REPORT_INCIDENT_NO, incidentNo);
        this.addParm(2, TCOOMMIConstants.REPORT_REVISION_NO, revisionNo);
        this.addParm(3, TCOOMMIConstants.REPORT_REVISION_TYPE, reportRevisionType);
        this.reportName = "incidentReport";
        setOutputFileName(this.generateReport());
        return "displayReport";
    }

    /**
     * @desc This method sets the offender demo information required for the
     * incident screen
     * @exception Exception
     * @return void
     */
    private void setCurrentOffenderInfo(String sid) throws Exception {
        if (getCurrentOffender() != null && sid.equals(getCurrentOffender().getShell().getSid())) {
            // Don't fetch the offender
        } else {
            OffenderService os = new OffenderService();
            this.offender = os.fetchFullOffenderBySidOrTdc(sid, null);
            this.setCurrentOffender(offender);
        }
    }

    /**
     * @desc This method pull the Comp monitor Vendor assignment screen
     * @exception Exception
     * @return String
     */
    public String viewCompMonitorVendorAssignment() throws Exception {
        return "viewCompMonitorVendorAssignment";
    }

    /**
     * @desc This method pull the Comp monitor Vendor assignment screen
     * @exception Exception
     * @return String
     */
    public String viewHSSTCSuprAssignment() throws Exception {
        return "viewHSSTCSuprAssignment";
    }

    /**
     * @desc This method pull the Comp monitor Vendor assignment screen
     * @exception Exception
     * @return String
     */
    public String viewIncidentReAssignment() throws Exception {
        return "viewIncidentReAssignment";
    }

    /**
     * @desc This method pull the Comp monitor Vendor assignment screen
     * @exception Exception
     * @return String
     */
    public String viewDashBoard() throws Exception {
        List<Incident> totalAwaitingRevision = service.getTotalAwaitingRevision(getCurrentUser().getUserId());
        List<Incident> totalAwaitingReview = service.getTotalAwaitingReview(getCurrentUser().getUserId());
        List<Incident> totalAwaitingFollowup = service.getTotalAwaitingFollowup(getCurrentUser().getUserId());

        incident.setTotalNeedsRevision((totalAwaitingRevision != null && !totalAwaitingRevision.isEmpty()) ? totalAwaitingRevision.size() - 1 : 0);
        incident.setTotalAwaitingReview((totalAwaitingReview != null && !totalAwaitingReview.isEmpty()) ? totalAwaitingReview.size() - 1 : 0);
        incident.setTotalAwaitingFU((totalAwaitingFollowup != null && !totalAwaitingFollowup.isEmpty()) ? totalAwaitingFollowup.size() - 1 : 0);
        return "viewDashBoard";
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Referral getReferral() {
        return referral;
    }

    public void setReferral(Referral referral) {
        this.referral = referral;
    }

    public boolean isValidIncident() {
        return validIncident;
    }

    public void setValidIncident(boolean validIncident) {
        this.validIncident = validIncident;
    }

    public int getReferralId() {
        return referralId;
    }

    public void setReferralId(int referralId) {
        this.referralId = referralId;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public ArrayList<DisplayClass> getIncidentTypes() {
        ArrayList<DisplayClass> dcList = (ArrayList<DisplayClass>) (LookupMaster.getIncidentTypes());
        Collections.sort(dcList);
        return dcList;
    }

    public ArrayList<DisplayClass> getArrestTypes() {
        ArrayList<DisplayClass> dcList = (ArrayList<DisplayClass>) (LookupMaster.getArrestTypes());
        Collections.sort(dcList);
        return dcList;
    }

    public ArrayList<DisplayClass> getIncidentStatuses() {
        ArrayList<DisplayClass> dcList = (ArrayList<DisplayClass>) (LookupMaster.getIncidentStatuses());
        Collections.sort(dcList);
        return dcList;
    }

    public List<DisplayClass> getActiveUsers() throws Exception {
        List<DisplayClass> dcList = new ArrayList<>();
        List<DisplayClass> loggedinUserList = new ArrayList<>();
        this.referral = new ReferralService().fetchReferralById(this.referralId);
        if(this.referral != null){
        int serviceType = this.referral.getService().getType();
        int programType = IncidentUtil.getProgramType(serviceType);
        if (programType == 4) {
            dcList = codeService.getUsersByGroup("HS");
        } else {
            dcList = codeService.getVendorLocationByVendor(this.referral.getVendor().getDistrict());
            if(!this.getCurrentUser().getRole().equals("V1") && !this.getCurrentUser().getRole().equals("VD")){
                 loggedinUserList = codeService.getUsersByGroup(this.getCurrentUser().getRole());
                 dcList.addAll(loggedinUserList);
            }
        }
        }else{
            dcList = codeService.getUsersByGroup(this.getCurrentUser().getRole());
        }
        Collections.sort(dcList);
        return dcList;
    }

    private void sendEmail(Incident incident) throws Exception {
        Email email = new Email();
        EmailUtil emailUtil = new EmailUtil();
        EmailEndPoint from = new EmailEndPoint();
        String supervisorEmailId = null;
        String complianceMonitorEmailId = null;
        from.setAddress(TCOOMMIConstants.TCOOMMI_IRT_EMAILID);
        from.setName(TCOOMMIConstants.TCOOMMI_IRT_EMAILNAME);
        ArrayList toList = new ArrayList<>();
        ArrayList<String> tempEmailList = new ArrayList<String>();
        this.referral = new ReferralService().fetchReferralById(incident.getRefNo());

        String complianceMonitor = codeService.getVendorComplianceMonitor("ZZ_" + this.referral.getVendor().getDistrict());
        List<DisplayClass> vendorDirectors = codeService.getVendorDirectorByVendor(this.referral.getVendor().getDistrict());

        if (complianceMonitor != null) {
            complianceMonitorEmailId = userService.getUserInfoByUserId(complianceMonitor).getEmailId();
        }

        if (incident.getProgramType() == 4) {
            String supervisor = codeService.getHssLocationByVendor("ZZ_" + this.referral.getVendor().getDistrict());
            if (supervisor != null) {
                supervisorEmailId = userService.getUserInfoByUserId(supervisor).getEmailId();
            }
        }

        if (incident.getProgramType() != 4 && (incident.getReportStatusCode() == 2 || incident.getReportStatusCode() == 3)) {
            EmailEndPoint eep = emailUtil.getEmailEndPoint(complianceMonitorEmailId);
            toList.add(eep);
            tempEmailList.add(complianceMonitorEmailId);
            for (DisplayClass vd : vendorDirectors) {
                EmailEndPoint vdEmail = emailUtil.getEmailEndPoint(vd.getEmail());
                if (!tempEmailList.contains(vd.getEmail())) {
                    toList.add(vdEmail);
                    tempEmailList.add(vd.getEmail());
                }
            }
           
            if (incident.getIncidentTypeCode() == 5 || incident.isIncidentCarriedByMedia() || 
                     (!GenericValidator.isBlankOrNull(incident.getApsReportNo()) && !incident.getApsReportNo().equals("0"))) {
                   emailUtil.getEmailListByRole(toList, tempEmailList, "MR");
            }
        } else if (incident.getProgramType() == 4 && (incident.getReportStatusCode() == 2 || incident.getReportStatusCode() == 3)) {
            EmailEndPoint eep = emailUtil.getEmailEndPoint(supervisorEmailId);
            toList.add(eep);
            tempEmailList.add(supervisorEmailId);
            if (incident.getIncidentTypeCode() == 5 || incident.isIncidentCarriedByMedia()|| 
                    (!GenericValidator.isBlankOrNull(incident.getApsReportNo()) && !incident.getApsReportNo().equals("0"))) {
                emailUtil.getEmailListByRole(toList, tempEmailList, "MR");
                
            }
        } else if (incident.getProgramType() != 4 && incident.getReportStatusCode() == 6) {
            UserInfo userInfo = userService.getUserInfoByUserId(incident.getCaseWorkerId());
            if (userInfo != null) {
                if (!GenericValidator.isBlankOrNull(userInfo.getEmailId())) {
                    EmailEndPoint eep = emailUtil.getEmailEndPoint(userInfo.getEmailId());
                    toList.add(eep);
                    tempEmailList.add(userInfo.getEmailId());
                }
            }
            for (DisplayClass vd : vendorDirectors) {
                EmailEndPoint vdEmail = emailUtil.getEmailEndPoint(vd.getEmail());
                if (!tempEmailList.contains(vd.getEmail())) {
                    toList.add(vdEmail);
                    tempEmailList.add(vd.getEmail());
                }
            }
        } else if (incident.getProgramType() == 4 && incident.getReportStatusCode() == 6) {
            UserInfo userInfo = userService.getUserInfoByUserId(incident.getCaseWorkerId());
            if (userInfo != null) {
                if (!GenericValidator.isBlankOrNull(userInfo.getEmailId())) {
                    EmailEndPoint eep = emailUtil.getEmailEndPoint(userInfo.getEmailId());
                    toList.add(eep);
                    tempEmailList.add(userInfo.getEmailId());
                }
            }
        }

        if (!toList.isEmpty()) {
            email.setFrom(from);
            email.setToList(toList);
            email.setSubject(IncidentUtil.getIncidentEmailSubject(incident));
            email.setBody(IncidentUtil.getIncidentEmailBody(incident));
            EmailUtil.send(email);
            System.out.println("Email Communication  sent to officers " + tempEmailList + " for Incident #" + incident.getIncidentNo());
        } else {
            System.out.println("Email Communication not sent to officers for Incident #" + incident.getIncidentNo());
        }
    }

    public ArrayList<DisplayClass> getCriminalJusticeStatuses() {
        return LookupMaster.getCriminalJusticeStatuses();
    }

    public int getIncidentNo() {
        return incidentNo;
    }

    public void setIncidentNo(int incidentNo) {
        this.incidentNo = incidentNo;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public String getReportRevisionType() {
        return reportRevisionType;
    }

    public void setReportRevisionType(String reportRevisionType) {
        this.reportRevisionType = reportRevisionType;
    }
 
    public Date getIncidentDateFrom() {
        return incidentDateFrom;
    }

    public void setIncidentDateFrom(Date incidentDateFrom) {
        this.incidentDateFrom = incidentDateFrom;
    }

    public Date getIncidentDateTo() {
        return incidentDateTo;
    }

    public void setIncidentDateTo(Date incidentDateTo) {
        this.incidentDateTo = incidentDateTo;
    }

    public Date getSubmitDateFrom() {
        return submitDateFrom;
    }

    public void setSubmitDateFrom(Date submitDateFrom) {
        this.submitDateFrom = submitDateFrom;
    }

    public Date getSubmitDateTo() {
        return submitDateTo;
    }

    public void setSubmitDateTo(Date submitDateTo) {
        this.submitDateTo = submitDateTo;
    }

    public List<Vendor> getVendors() {
        return CacheMaster.getVendors();
    }

    public String getSelectedVendors() {
        return selectedVendors;
    }

    public void setSelectedVendors(String selectedVendors) {
        this.selectedVendors = selectedVendors;
    }

    public int getRevisionNo() {
        return revisionNo;
    }

    public void setRevisionNo(int revisionNo) {
        this.revisionNo = revisionNo;
    }

    public Offender getOffender() {
        return offender;
    }

    public void setOffender(Offender offender) {
        this.offender = offender;
    }

    public String getCaseWorkerId() {
        return caseWorkerId;
    }

    public void setCaseWorkerId(String caseWorkerId) {
        this.caseWorkerId = caseWorkerId;
    }
    
    
    public ArrayList getEmailToList(List<DisplayClass> emailList) {
        ArrayList<EmailEndPoint> toList = new ArrayList<>();
        ArrayList<String> toListTemp = new ArrayList<>();

        for (DisplayClass toEmail : emailList) {

            if (!GenericValidator.isBlankOrNull(toEmail.getEmail())) {

                if (!toListTemp.contains(toEmail.getEmail())) {
                    EmailEndPoint to = new EmailEndPoint();
                    to.setAddress(toEmail.getEmail());
                    to.setName(toEmail.getDesc());
                    toList.add(to);
                    toListTemp.add(toEmail.getEmail());
                }
            }
        }
        return toList;
    }

}
