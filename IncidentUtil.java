/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.tx.state.tdcj.tcoommi.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.validator.GenericValidator;
import us.tx.state.tdcj.tcoommi.beans.DisplayClass;
import us.tx.state.tdcj.tcoommi.beans.Offender;
import us.tx.state.tdcj.tcoommi.beans.UserInfo;
import us.tx.state.tdcj.tcoommi.beans.incident.Followup;
import us.tx.state.tdcj.tcoommi.beans.incident.Incident;
import us.tx.state.tdcj.tcoommi.beans.referral.Referral;
import us.tx.state.tdcj.tcoommi.beans.referral.Vendor;
import us.tx.state.tdcj.tcoommi.cache.LookupMaster;
import us.tx.state.tdcj.tcoommi.constants.TCOOMMIConstants;
import us.tx.state.tdcj.tcoommi.service.OffenderService;

/**
 * This class is the utility class for incident report
 *
 * @author nThompson
 */
public class IncidentUtil {

    /**
     * @desc This method will be called to get the criminal justice status from
     * the referral
     * @param referral
     * @return String
     */
    public static String getCriminalJusticeStatus(Referral referral) {
        String criminalJusticeStatus = null;

        ArrayList<DisplayClass> dcList = (ArrayList<DisplayClass>) ((LookupMaster.getCriminalJusticeStatuses()));

        if (referral != null) {

            String currCriminalJusticeStatus = referral.getCrimJusticeStatus();

            for (DisplayClass dc : dcList) {
                if (dc.getCode().equals(currCriminalJusticeStatus)) {
                    criminalJusticeStatus = dc.getCodeDesc().substring(2, dc.getCodeDesc().length());
                    break;
                }
            }
        }
        return criminalJusticeStatus;
    }

    /**
     * @desc This method gets the program type description from the code
     * @param programTypeCode
     * @return String
     */
    public static String getProgramTypeDesc(int programTypeCode) {

        String desc = "";
        switch (programTypeCode) {
            case 1:
                desc = "Adult";
                break;
            case 2:
                desc = "Juvenile";
                break;
            case 3:
                desc = "Residential";
                break;
            case 4:
                desc = "MRIS/Medical";
                break;
            default:
                break;
        }
        return desc;
    }

    /**
     * @desc This method gets the program Sub type description from the code
     * @param programSubTypeCode
     * @return String
     */
    public static String getProgramSubTypeDesc(int programSubTypeCode) {

        String desc = "";
        switch (programSubTypeCode) {
            case 1:
                desc = "SNDP";
                break;
            case 2:
                desc = "TJJD";
                break;
            case 3:
                desc = "Non-SNDP";
                break;
            default:
                break;
        }
        return desc;
    }

    /**
     * @desc This method gets thechargesToCJStatus description from the code
     * @param chargesToCJStatus
     * @return String
     */
    public static String getChargestoCJStatusDesc(String chargesToCJStatus) {

        String desc = "";
        if (chargesToCJStatus != null && chargesToCJStatus.equals("1")) {
            desc = "Felony";
        } else if (chargesToCJStatus != null && chargesToCJStatus.equals("2")) {
            desc = "Misdemeanor";
        }
        return desc;
    }

    /**
     * @desc This method determines the program type based on service type
     * @param serviceType
     * @return int
     */
    public static int getProgramType(int serviceType) {
        int programType = 0;
        if (serviceType == 1
                || serviceType == 2
                || serviceType == 3
                || serviceType == 4
                || serviceType == 12) {
            programType = 3;
        } else if (serviceType == 1
                || serviceType == 2
                || serviceType == 3
                || serviceType == 4
                || serviceType == 13) {
            programType = 1;
        } else if (serviceType == 5
                || serviceType == 6) {
            programType = 2;
        } else if (serviceType == 7
                || serviceType == 10
                || serviceType == 11) {
            programType = 4;
        }
        return programType;
    }

    /**
     * @desc This method calculates the months/days in service based on
     * startDate
     * @param startDate
     * @return String
     */
    public static String getTimeInCurrentService(Date startDate) {

        String timeInService = "";

        if (startDate != null) {
            Calendar startCalendar = new GregorianCalendar();
            startCalendar.setTime(startDate);
            Calendar endCalendar = new GregorianCalendar();
            endCalendar.setTime(new Date());
            int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
            int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
            if (diffMonth > 0) {
                timeInService = String.valueOf(diffMonth) + (diffMonth ==1?" month":" months");
            }
            if (diffMonth == 0) {
                int diffDays = endCalendar.get(Calendar.DATE) - startCalendar.get(Calendar.DATE);
                timeInService = String.valueOf(diffDays) + (diffDays == 1?" day":" days");
            }
        }
        return timeInService;
    }

    /**
     * @desc This method determines the report status based on the status code
     * @param statusCode
     * @return String
     */
    public static String getReportStatus(int statusCode) {

        switch (statusCode) {
            case 1:
                return TCOOMMIConstants.REPORT_STATUS_INPROGRESS;
            case 2:
                return TCOOMMIConstants.REPORT_STATUS_SUBMITTED;
            case 3:
                return TCOOMMIConstants.REPORT_STATUS_RE_SUBMITTED;
            case 5:
                return TCOOMMIConstants.REPORT_STATUS_ACCEPTED;
            case 6:
                return TCOOMMIConstants.REPORT_STATUS_REJECTED;
            case 7:
                return TCOOMMIConstants.REPORT_STATUS_ACCEPTED_PENDING_FOLLOWUP;
            case 8:
                return TCOOMMIConstants.REPORT_STATUS_ACCEPTED_REQUEST_FOLLOWUP;
            default:
                break;
        }
        return " ";
    }

    /**
     * @desc This method determines the Incident Type desc status based on the
     * code
     * @param incidentTypeCode
     * @return String
     */
    public static String getIncidentTypeDesc(int incidentTypeCode, String hospType) {
        String description = " ";
        String hospitalizationType = "";
        if (hospType != null && hospType.equals("V")) {
            hospitalizationType = "Vol Psych";
        } else if (hospType != null && hospType.equals("I")) {
            hospitalizationType = "Invol Psych";
        }
        switch (incidentTypeCode) {
            case 1:
                description = "Abscond";
                break;
            case 2:
                description = "Arrest";
                break;
            case 3:
                description = "APS";
                break;
            case 4:
                description = "Case Closure";
                break;
            case 5:
                description = "Death";
                break;
            case 6:
                description = hospitalizationType;
                break;
            case 7:
                description = "Revocation";
                break;
            case 8:
                description = "Refusal Svc";
                break;
            case 9:
                description = "Case Closure-NE";
                break;
            case 10:
                description = "Revoc to TJJD";
                break;
            case 11:
                description = "Detention";
                break;
            case 12:
                description = "Out of Home";
                break;
            case 13:
                description = "Discharge";
                break;
            case 14:
                description = "Other";
                break;
            default:
                break;
        }
        return description;
    }

    /**
     * @desc This method determines the Arrest Type description status based on
     * the arrestTypeCode
     * @param arrestTypeCode
     * @return String
     */
    public static String getArrestTypeDesc(int arrestTypeCode) {
        String description = " ";
        switch (arrestTypeCode) {
            case 1:
                description = "Motion to Revoke(CSCD)";
                break;
            case 2:
                description = "Violation of Conditions(Parole Only)";
                break;
            case 3:
                description = "Felony";
                break;
            case 4:
                description = "Misdemeanor";
                break;
            default:
                break;
        }
        return description;
    }
 
    /**
     * @desc This method determines the current incident status description
     * based on the current status code
     * @param currentStatusCode
     * @return String
     */
    public static String getCurrentStatusDesc(int currentStatusCode) {
        String description = " ";
        switch (currentStatusCode) {
            case 1:
                description = "TJJD";
                break;
            case 2:
                description = "Jail";
                break;
            case 3:
                description = "Hospital";
                break;
            case 4:
                description = "Community";
                break;
            case 5:
                description = "Home";
                break;
            case 6:
                description = "Residential Placement";
                break;
            case 7:
                description = "Detention";
                break;
            case 8:
                description = "Nursing Facility";
                break;
            case 9:
                description = "Assisted Living";
                break;
            case 10:
                description = "Private Residence";
                break;
            case 11:
                description = "Other";
                break;
            case 12:
                description = "Out of Home Placement";
                break;
            case 13:
                description = "Group Home";
                break;
            case 14:
                description = "Residential Program";
                break;
            default:
                break;
        }
        return description;
    }

   /**
     * @desc This method determines the if there any prerelease Ref Source
     * @param refSource
     * @return boolean
     */
    public  static boolean isPreReleaseRefSource(int serviceType, int refSource) {
        Integer[] refSources = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10,11,12, 13,14,15,16};
        List<Integer> list = Arrays.asList(refSources);
        return (list.contains(refSource)?true:false);
    }

    /**
     * @desc This method determines the psychHospType description status based
     * on the psychHospTypeCode
     * @param psychHospTypeCode
     * @return String
     */
    public static String getPsychHospTypeDesc(String psychHospTypeCode) {
        String desc = "";

        if (!GenericValidator.isBlankOrNull(psychHospTypeCode)) {

            if (psychHospTypeCode.equals("V")) {
                desc = "Voluntary";
            } else {
                desc = "Involuntary";
            }
        }
        return desc;
    }

    /**
     * @desc This method formats the date in the given mm/dd/yyyy format
     * @param date
     * @return String
     */
    public static String getFormattedDate(Date date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            return sdf.format(date);
        }
        return null;
    }

    /**
     * @desc This method formats the date in the given mm/dd/yyyy HH:mm:ss a
     * format
     * @param timestamp
     * @return String
     */
    public static String getFormattedDateAndTime(Timestamp timestamp) {
        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
            return sdf.format(timestamp);
        }
        return null;
    }

    /**
     * @desc This method formats the date in the given MM/dd/yyyy format
     * @param date
     * @return String
     */
    public static Date getFormattedDate(String date) throws ParseException {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            return sdf.parse(date);
        }
        return null;
    }

    /**
     * @desc This method returns the date,time and ampmString in Timestamp
     * @param date
     * @param time
     * @param ampmStr
     * @return Timestamp
     */
    public static Timestamp getTimestamp(Date date, String time, String ampmStr) {

        if (date != null) {

            if (GenericValidator.isBlankOrNull(time)) {
                time = "00:00";
            }
            if (GenericValidator.isBlankOrNull(ampmStr)) {
                ampmStr = "AM";
            }
            int hour = Integer.parseInt(time.split(":")[0]);
            int min = Integer.parseInt(time.split(":")[1]);
            int ampm = ampmStr.equals("AM") ? 0 : 1;
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.HOUR, hour);
            c.set(Calendar.MINUTE, min);
            c.set(Calendar.AM_PM, ampm);
            Timestamp timeStamp = new java.sql.Timestamp(c.getTimeInMillis());
            return timeStamp;
        }
        return null;
    }

    /**
     * @desc This method formats the Timestamp
     * @param timestamp
     * @return String
     */
    public static String formatTimestamp(Timestamp timestamp) throws ParseException {
        String formattedTimestamp = null;
        if (timestamp != null) {
            DateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm a");
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formattedTimestamp = outputFormat.format(inputFormat.parse(timestamp.toString()));
        }
        return formattedTimestamp;
    }

    /**
     * @desc This method determines if an Incident can be added to a Referral
     * the Timestamp
     * @param referral
     * @param user
     * @return String
     */
    public static boolean isIncidentEligibleToAdd(Referral referral, UserInfo user) {
        
        if(referral == null || user == null){
                return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -3);
        int refSource = referral.getSource();
        int serviceTypeCode = referral.getService().getType();
        if(user !=null && (user.isComplianceMonitor() || user.isMaster() || user.isInquiry() || user.isStaff())){
            return false;
        }
        else if (user != null && (user.isHss() || user.isVendor() || user.isVendorDirector() || user.isManager() || user.isSupervisor())) {
            String vendorUserDistrict = !GenericValidator.isBlankOrNull(user.getOffDistrict()) ? user.getOffDistrict().trim() : null;
            String referralDistrict = !GenericValidator.isBlankOrNull(referral.getVendor().getDistrict()) ? referral.getVendor().getDistrict().trim() : null;

            if ((user.isVendor() || user.isVendorDirector()) && (vendorUserDistrict != null && !vendorUserDistrict.equals(referralDistrict))) {
                return false;
            } else if (user.isHss() && referralDistrict != null &&!(getHssLocations().containsKey(referralDistrict)) && (referral.getService().getType() != 7
                    || referral.getService().getType() != 10 || referral.getService().getType() != 11)) {
                return false;
            }

            if (referral.getEndDate() == null && referral.getService().getEndDate() == null && referral.getService().getDischargeDate() == null) {
                if (serviceTypeCode == 0 || serviceTypeCode == 11) {
                    if (isPreReleaseRefSource(serviceTypeCode, refSource)) {
                        return false;
                    }
                } 
            }else if (isAllowedTimeFrame(referral.getEndDate()) || isAllowedTimeFrame(referral.getService().getEndDate()) || isAllowedTimeFrame(referral.getService().getDischargeDate())) {
                    if (serviceTypeCode == 0 || serviceTypeCode == 11) {
                        if (isPreReleaseRefSource(serviceTypeCode, refSource)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

    /**
     * @desc This method determines eligibility criteria with respect to
     * referral. Referral/Service should be closed 3 months or less
     * @param refDate
     * @return boolean
     */
    private static boolean isAllowedTimeFrame(Date refDate) {

        if (refDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, -3);
            Date cutOffDate = cal.getTime();
            if (refDate.before(cutOffDate)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @desc This method validates if the date entered is valid in MM/dd/yyyy
     * format
     * @param validateDate
     * @return boolean
     */
    public static boolean isValidDate(Date validateDate) {
        try {
            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            String valDate = formatter.format(validateDate);
            formatter.setLenient(false);
            formatter.parse(valDate);
            if (valDate.length() != 10) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @desc This method builds the Email Body for the Incident Reporting
     * format
     * @param incident
     * @return String
     */
    public static String getIncidentEmailBody(Incident incident) throws Exception {
        OffenderService os = new OffenderService();
        Offender offender = os.fetchFullOffenderBySidOrTdc(incident.getSid(), null);
        StringBuilder builder = new StringBuilder();
       String submitDate = parseDateToString(incident.getAddTimestamp(),"MM/dd/yyyy");
        builder.append("Vendor/HSS: ").append(incident.getVendor()!=null?incident.getVendor().getName():" ").append("\r\n").
                append("Incident Report #: ").append(incident.getIncidentNo()).append("\r\n").
                append("Incident Report Program Type: ").append(getProgramTypeDesc(incident.getProgramType())).append("\r\n").
                append("Incident Type: ").append(getIncidentTypeDesc(incident.getIncidentTypeCode(), incident.getPsychHospType())).append("\r\n").
                append("Submit Date: ").append(submitDate).append("\r\n").
                append("SID #:").append(incident.getSid()).append("\r\n").
                append("TDCJ #:").append(offender != null && offender.getShell() != null?offender.getShell().getTdcj():"").append("\r\n").
                append("Name :").append(offender != null && offender.getShell() != null ? offender.getShell().getLastName() + ", " + offender.getShell().getFirstName() : "").append("\r\n").
                append("Referral #:").append(incident.getRefNo()).append("\r\n").
                append("Service Type:").append(incident.getServiceTypeName()).append("\r\n");
        System.out.println("Email Body:\n" + builder.toString());
        return builder.toString();
    }
    
     /**
     * @desc This method builds the Email Body for the Incident Reporting
     * format
     * @param followup
     * @return String
     */
    public static String getFollowupEmailBody(Followup followup) throws Exception {
        OffenderService os = new OffenderService();
        Offender offender = os.fetchFullOffenderBySidOrTdc(followup.getSid(), null);
        StringBuilder builder = new StringBuilder();
       String submitDate = parseDateToString(followup.getAddTimestamp(),"MM/dd/yyyy");
        builder.append("Vendor/HSS: ").append(followup.getVendor()!=null?followup.getVendor().getName():" ").append("\r\n").
                append("Incident Report #: ").append(followup.getIncidentNo()).append("\r\n").
                append("Followup Report #: ").append(followup.getFollowUpNo()).append("\r\n").
                append("Incident Report Program Type: ").append(getProgramTypeDesc(followup.getProgramType())).append("\r\n").
                append("Incident Type: ").append(getIncidentTypeDesc(followup.getIncidentTypeCode(), followup.getPsychHospType())).append("\r\n").
                append("Submit Date: ").append(submitDate).append("\r\n").
                append("SID #:").append(followup.getSid()).append("\r\n").
                append("TDCJ #:").append(offender != null && offender.getShell() != null?offender.getShell().getTdcj():"").append("\r\n").
                append("Name :").append(offender != null && offender.getShell() != null ? offender.getShell().getLastName() + ", " + offender.getShell().getFirstName() : "").append("\r\n").
                append("Referral #:").append(followup.getRefNo()).append("\r\n").
                append("Service Type:").append(followup.getServiceTypeName()).append("\r\n");
        System.out.println("Email Body:\n" + builder.toString());
        return builder.toString();
    }


   /**
     * @desc This method builds the Email Subject for the Incident Reporting
     * format
     * @param incident
     * @return String
     */
    public static String getIncidentEmailSubject(Incident incident) throws Exception {
        OffenderService os = new OffenderService();
        Offender offender = os.fetchFullOffenderBySidOrTdc(incident.getSid(), null);

        StringBuilder builder = new StringBuilder();
        builder.append("SID #").append(incident.getSid()).append(" ").
                append(offender != null && offender.getShell() != null ? offender.getShell().getLastName() + ", " + offender.getShell().getLastName().substring(0,1) : "").
                append("- IR ").
                append(getReportStatus(incident.getReportStatusCode())).
                append(" (").
                append(getIncidentTypeDesc(incident.getIncidentTypeCode(), incident.getPsychHospType())).
                append(")");

        return builder.toString();
    }
    
    
    
    /**
     * @desc This method builds the Email Subject for the Followup Reporting
     * format
     * @param followup
     * @return String
     */
    public static String getFollowupEmailSubject(Followup followup) throws Exception {
        OffenderService os = new OffenderService();
        Offender offender = os.fetchFullOffenderBySidOrTdc(followup.getSid(), null);

        StringBuilder builder = new StringBuilder();
        builder.append("SID #").append(followup.getSid()).append(" ").
                append(offender != null && offender.getShell() != null ? offender.getShell().getLastName() + ", " + offender.getShell().getLastName().substring(0,1) : "").
                append("- Follow Up ").
                append(getReportStatus(followup.getReportStatusCode())).
                append(" (").
                append(getIncidentTypeDesc(followup.getIncidentTypeCode(), followup.getPsychHospType())).
                append(")");

        return builder.toString();
    }
    
      public static String parseDateToString(Date date, String...formats)
    {
        String dateFormat =
                (formats == null || formats.length == 0)?"MM/dd/yyyy":formats[0];

        DateFormat df = new SimpleDateFormat(dateFormat);

        return (date!=null?df.format(date):" ");
    }
      
       
    /**
     * @desc This method determines whether the incident is allowed to edit
     * @param user
     * @param vendor
     * @return boolean
     */
    public static boolean isEligibleVendor(UserInfo user, Vendor vendor) {
        if (user != null && vendor != null) {
            String vendorUserDistrict = !GenericValidator.isBlankOrNull(user.getOffDistrict()) ? user.getOffDistrict().trim() : null;
            String referralDistrict = !GenericValidator.isBlankOrNull(vendor.getDistrict()) ? vendor.getDistrict().trim() : null;

            if (vendorUserDistrict != null && !user.isHss()) {
                if (!vendorUserDistrict.equals(referralDistrict)) {
                    return false;
                }
            } else if (referralDistrict != null && user.isHss()) {
                if (!getHssLocations().containsKey(referralDistrict)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * @desc This method lists all the HSS locations. When a new HSS location is addeded
     * that should be included in the list
     * @return Map
     */
    public static Map getHssLocations() {
      Map hssLocations = new HashMap<>();
      
      hssLocations.put("46", "46");
      hssLocations.put("47", "47");
      hssLocations.put("48", "48");
      hssLocations.put("49", "49");
      hssLocations.put("63", "63");
      hssLocations.put("64", "64");
      hssLocations.put("65", "65");
      hssLocations.put("66", "66");
      hssLocations.put("67", "67");
      return hssLocations;
    }
    
    public static String getCategoryDesc(String category){
    String description = " ";
        switch (category) {
            case "TCOOMMI_RESI_TYPES" :
                description = "Residence Type";
                 break;
                 case "REFERRAL_SOURCE" :
                description = "Referral Source";
                 break;
                 case "LVL_OF_CARE" :
                description = "Level of Care";
                 break;
                 case "DIAGNOSTIC_GROUP_PRIORITY" :
                description = "Diagnostic Groups -Prioritized";
                 break;
                 case "MRIS_CATEGORIES" :
                description = "MRIS Categories";
                 break;
                 case "SERVICE_TYPES" :
                description = "Service Types";
                 break;
                 case "SERVICE_STATUS" :
                description = "Service Status";
                 break;
                 case "DISCHARGE_REASONS" :
                description = "Discharge Reason";
                 break;
                 case "VA_VISN_DISTRICT" :
                description = "VA VISN District";
                 break;
                 case "CRIMINAL_JUSTICE_STATUS" :
                description = "Criminal Justice Status";
                 break;
                 case "AXIS_4_CODES" :
                description = "Axis 4 Codes";
                 break;
                 case "MEDICAL_PROVIDERS" :
                description = "Medical Providers";
                 break;
                 case "PHYSICIAN_TITLES" :
                description = "Physician Titles";
                 break;
                 case "TCOOMMI_INCOME_TYPES" :
                description = "TCOOMMI Income Types";
                 break;
                 default:
                     description= "";
                     break;
        }
       
    return description;
    }
     
							      
    
    
    
    
    
    
}
