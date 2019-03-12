/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.tx.state.tdcj.tcoommi.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import us.tx.state.tdcj.tcoommi.beans.UserInfo;
import us.tx.state.tdcj.tcoommi.beans.incident.Followup;
import us.tx.state.tdcj.tcoommi.beans.incident.Incident;
import us.tx.state.tdcj.tcoommi.beans.incident.TRAS;
import us.tx.state.tdcj.tcoommi.beans.referral.Vendor;
import us.tx.state.tdcj.tcoommi.beans.security.RoleDetail;
import us.tx.state.tdcj.tcoommi.cache.CacheMaster;
import us.tx.state.tdcj.tcoommi.constants.TCOOMMIConstants;
import us.tx.state.tdcj.tcoommi.data.dao.IncidentDao;
import us.tx.state.tdcj.tcoommi.data.dao.context.DaoFactory;
import us.tx.state.tdcj.tcoommi.data.dao.util.UserDao;
import us.tx.state.tdcj.tcoommi.data.mappers.IncidentMapper;
import us.tx.state.tdcj.tcoommi.data.mappers.UserMapper;
import us.tx.state.tdcj.tcoommi.exception.DAOException;
import us.tx.state.tdcj.tcoommi.reports.scripts.tatcmi30.IncidentTypeStatRecord;

/**
 * This class provides the service for Incident and Followup reports
 *
 * @author nThompson
 */
public class IncidentService extends BaseService {

    /**
     * @desc This method gets the TRAS Details
     * @param sidNo
     * @throws us.tx.state.tdcj.tcoommi.exception.DAOException
     * @return TRAS
     */
    public TRAS getTRASDetailsBySid(String sidNo)
            throws DAOException, Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);

        return cld.singleObjectFetch(
                IncidentMapper.QUERY.FETCH_TRAS_DETAILS_BY_SID,
                TRAS.class,
                sidNo);
    }

    /**
     * @desc This method gets the Max Incident no from TATCMI_INCIDENT table
     * @throws us.tx.state.tdcj.tcoommi.exception.DAOException
     * @return Integer
     */
    public Integer getMaxIncidentNo()
            throws DAOException, Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);

        return cld.singleObjectFetch(
                IncidentMapper.QUERY.FETCH_MAX_INCIDENT_NO,
                Integer.class);
    }

    /**
     * @desc This method gets the Max Revision no for the given Incident
     * @throws us.tx.state.tdcj.tcoommi.exception.DAOException
     * @return Integer
     */
    public Integer getMaxIncidentRevisionNo(int incidentNo)
            throws DAOException, Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);

        return cld.singleObjectFetch(
                IncidentMapper.QUERY.FETCH_MAX_INCIDENT_REV_NO,
                Integer.class, incidentNo);
    }

    /**
     * @desc This method gets the Max Revision no for the given Incident
     * @throws us.tx.state.tdcj.tcoommi.exception.DAOException
     * @return followupNo
     */
    public Integer getMaxFollowUpRevisionNo(int followupNo) throws DAOException, Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);

        return cld.singleObjectFetch(
                IncidentMapper.QUERY.FETCH_MAX_FOLLOWUP_REV_NO,
                Integer.class, followupNo);
    }

    /**
     * @desc This method gets the Max Incident no from TATCMI_INCIDENT table
     * @throws us.tx.state.tdcj.tcoommi.exception.DAOException
     * @return Integer
     */
    public ArrayList<Incident> getTotalAwaitingReview(String userId)
            throws DAOException, Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        ArrayList<Incident> maxRevisionList = new ArrayList<Incident>();
        ArrayList<Incident> incList = cld.fetchReviewTotal(userId);
        for (Incident incident : incList) {
            ArrayList<Incident> temp = cld.fetchMaxRevisionByIncident(incident.getIncidentNo());
            if (temp.get(0).getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_SUBMITTED || temp.get(0).getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_RE_SUBMITTED) {
                maxRevisionList.add(temp.get(0));
            }
        }
        return maxRevisionList;
    }
    
    /**
     * @desc This method gets the Max Incident no from TATCMI_INCIDENT table
     * @throws us.tx.state.tdcj.tcoommi.exception.DAOException
     * @return Integer
     */
    public ArrayList<Followup> getTotalFollowupAwaitingReview(String userId)
            throws DAOException, Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        ArrayList<Followup> maxRevisionList = new ArrayList<Followup>();
        ArrayList<Followup> followupList = cld.fetchFollowupReviewTotal(userId);
        for (Followup followup : followupList) {
            ArrayList<Followup> temp = cld.fetchMaxRevisionByFollowup(followup.getFollowUpNo());
            if (temp.get(0).getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_SUBMITTED || temp.get(0).getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_RE_SUBMITTED) {
                maxRevisionList.add(temp.get(0));
            }
        }
        return maxRevisionList;
    }
    
    /**
     * @desc This method gets the Max Incident no from TATCMI_INCIDENT table
     * @throws us.tx.state.tdcj.tcoommi.exception.DAOException
     * @return Integer
     */
    public ArrayList<Incident> getTotalAwaitingRevision(String userId)
            throws DAOException, Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        ArrayList<Incident> maxRevisionList = new ArrayList<Incident>();
        ArrayList<Incident> incList =  cld.fetchRevisionTotal(userId);
         for(Incident incident: incList){
            ArrayList<Incident> temp = cld.fetchMaxRevisionByIncident(incident.getIncidentNo());
            if(temp.get(0).getReportStatusCode() == 6){
            maxRevisionList.add(temp.get(0));
            }
        }
        return maxRevisionList;
        
    }
    

    /**
     * @desc This method gets the Max Incident no from TATCMI_INCIDENT table
     * @throws us.tx.state.tdcj.tcoommi.exception.DAOException
     * @return Integer
     */
    public ArrayList<Followup> getTotalFollowupAwaitingRevision(String userId)
            throws DAOException, Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        ArrayList<Followup> maxRevisionList = new ArrayList<Followup>();
        ArrayList<Followup> followupList = cld.fetchFollowpRevisionTotal(userId);
        for (Followup followup : followupList) {
            ArrayList<Followup> temp = cld.fetchMaxRevisionByFollowup(followup.getFollowUpNo());
            if (temp.get(0).getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_REJECTED) {
                maxRevisionList.add(temp.get(0));
            }
        }
        return maxRevisionList;

    }

    /**
     * @desc This method gets the Max Incident no from TATCMI_INCIDENT table
     * @throws us.tx.state.tdcj.tcoommi.exception.DAOException
     * @return Integer
     */
    public ArrayList<Incident> getTotalAwaitingFollowup(String userId)
            throws DAOException, Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        ArrayList<Incident> maxRevisionList = new ArrayList<Incident>();
        ArrayList<Incident> incList = cld.fetchPendingFollowupTotal(userId);
        for (Incident incident : incList) {
            ArrayList<Incident> temp = cld.fetchMaxRevisionByIncident(incident.getIncidentNo());
            if (temp.get(0).getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_ACCEPTED_PENDING_FOLLOWUP) {
                maxRevisionList.add(temp.get(0));
            }
        }
        return maxRevisionList;
    }
    
     /**
     * @desc This method gets the Max Incident no from TATCMI_INCIDENT table
     * @throws us.tx.state.tdcj.tcoommi.exception.DAOException
     * @return Integer
     */
    public ArrayList<Incident> getTotalRequestingFollowup(String userId)
            throws DAOException, Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        ArrayList<Incident> maxRevisionList = new ArrayList<Incident>();
        ArrayList<Incident> incList = cld.fetchRequestFollowupTotal(userId);
        for (Incident incident : incList) {
            ArrayList<Incident> temp = cld.fetchMaxRevisionByIncident(incident.getIncidentNo());
            if (temp.get(0).getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_ACCEPTED_REQUEST_FOLLOWUP) {
                maxRevisionList.add(temp.get(0));
            }
        }
        return maxRevisionList;
    }

    /**
     * @desc This method Submits the Incident details
     * @param incident
     * @exception Exception
     * @return Integer
     */
    public Integer submitIncident(Incident incident) throws Exception {
        IncidentDao id = DaoFactory.getDao(IncidentDao.class);
        id.singleGenericUpdate(IncidentMapper.QUERY.INSERT_INCIDENT, incident);
        return incident.getIncidentNo();
    }

    /**
     * @desc This method gets the Incident Details
     * @param incidentId
     * @param revisionType
     * @exception Exception
     * @return Incident
     */
    public Incident getIncident(int incidentId, int revisionNo, String revisionType) throws Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        return cld.singleObjectFetch(
                IncidentMapper.QUERY.FETCH_INCIDENT,
                Incident.class,
                incidentId, revisionNo, revisionType);
    }

    /**
     * @desc This method update the Incident details
     * @exception Exception
     * @param incident
     */
    public void updateIncident(Incident incident) throws Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        cld.singleGenericUpdate(
                IncidentMapper.QUERY.UPDATE_INCIDENT,
                incident);
    }

    /**
     * @desc This method delete the Incident
     * @exception Exception
     * @param delFlag
     * @param userId
     * @param refNo
     */
    public void deleteIncidentByReferral(String delFlag, String userId, int refNo) throws Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        cld.singleGenericUpdate(
                IncidentMapper.QUERY.DELETE_INCIDENT_BY_REFERRAL,
                delFlag, userId, refNo);
    }

    /**
     * @desc This method updates the incident report status
     * @exception Exception
     * @param reportStatusCode
     * @param revComments
     * @param userId
     * @param incidentNo
     * @param caseWorkerId
     */
    public void updateIncidentReportStatus(int reportStatusCode, String revComments, String userId, int incidentNo, int revisionNo, String caseWorkerId) throws Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        cld.singleGenericUpdate(
                IncidentMapper.QUERY.UPDATE_INCIDENT_REPORT_STATUS,
                reportStatusCode, revComments, userId, incidentNo, revisionNo, caseWorkerId, "R");
    }
    
     /**
     * @desc This method updates the incident report status
     * @exception Exception
     * @param incidentNo
     * @param revisionNo
     */
    public void updateIncidentMaxRevInd(int incidentNo, int revisionNo) throws Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        cld.singleGenericUpdate(
                IncidentMapper.QUERY.UPDATE_INCIDENT_MAX_REV_IND,
                incidentNo, revisionNo);
    }


    /**
     * @desc This method gets the Max followupno no in the TATCMI_FOLLOW_UP
     * table
     * @exception Exception
     * @throws us.tx.state.tdcj.tcoommi.exception.DAOException
     * @return Integer
     */
    public Integer getMaxFollowUpNo()
            throws DAOException, Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);

        return cld.singleObjectFetch(
                IncidentMapper.QUERY.FETCH_MAX_FOLLOWUP_NO,
                Integer.class);
    }

    /**
     * @desc This method Submits the Followup details
     * @param followUp
     * @exception Exception
     * @return Integer
     */
    public Integer submitFollowUp(Followup followUp) throws Exception {
        IncidentDao id = DaoFactory.getDao(IncidentDao.class);
        id.singleGenericUpdate(IncidentMapper.QUERY.INSERT_FOLLOWUP, followUp);
        return followUp.getFollowUpNo();
    }

    /**
     * @desc This method gets the Followup
     * @param followUpNo
     * @param revisionType
     * @exception Exception
     * @return Followup
     */
    public Followup getFollowUp(int followUpNo, int revisionNo, String revisionType) throws Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        return cld.singleObjectFetch(
                IncidentMapper.QUERY.FETCH_FOLLOWUP,
                Followup.class,
                followUpNo, revisionNo, revisionType);

    }

    /**
     * @desc This method gets the list of Followup by Incident
     * @param incidentNo
     * @exception Exception
     * @return ArrayList
     */
    public ArrayList<Followup> getFollowUpListByIncident(int incidentNo) throws Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        return cld.fetchFollowupListByIncidentId(incidentNo);
    }

    /**
     * @desc This method update the Followup details
     * @param followUp
     * @exception Exception
     */
    public void updateFollowUp(Followup followUp) throws Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        cld.singleGenericUpdate(
                IncidentMapper.QUERY.UPDATE_FOLLOWUP,
                followUp);
    }

    /**
     * @desc This method updates the Followup status details
     * @param reportStatusCode
     * @param revComments
     * @param userId
     * @param followUpNo
     * @exception Exception
     */
    public void updateFollowUpReportStatus(int reportStatusCode, String revComments, String userId, int followUpNo, int revisionNo) throws Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        cld.singleGenericUpdate(
                IncidentMapper.QUERY.UPDATE_FOLLOWUP_REPORT_STATUS,
                reportStatusCode, revComments, userId, followUpNo, revisionNo, "R");
    }

    /**
     * @desc This method Deletes the Followup by Referral
     * @param delFlag
     * @param userId
     * @param refNo
     * @exception Exception
     */
    public void deleteFollowupByReferral(String delFlag, String userId, int refNo) throws Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        cld.singleGenericUpdate(
                IncidentMapper.QUERY.DELETE_FOLLOWUP_BY_REFERRAL,
                delFlag, userId, refNo);
    }

    /**
     * @desc This method gets the IncidentList by RefId
     * @param refNo
     * @exception Exception
     * @return ArrayList
     */
    public ArrayList<Incident> getIncidentListByRefId(int refNo) throws Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        ArrayList<Incident> maxRevisionList = new ArrayList<Incident>();
        ArrayList<Incident> incList = cld.fetchIncidentListByRefId(refNo);
        for (Incident incident : incList) {
            ArrayList<Incident> temp = cld.fetchMaxRevisionByIncident(incident.getIncidentNo());
            maxRevisionList.add(temp.get(0));
        }
        return maxRevisionList;
    }

    /**
     * @desc This method gets the IncidentList by caseWorkerId
     * @param caseWorkerId
     * @exception Exception
     * @return ArrayList
     */
    public ArrayList<Incident> getIncidentListByCaseWorkerId(String caseWorkerId) throws Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        ArrayList<Incident> incList = cld.fetchIncidentListByCaseWorker(caseWorkerId);
        return incList;
    }

    /**
     * @desc This method gets the IncidentList by Sid
     * @param sid
     * @exception Exception
     * @return ArrayList
     */
    public ArrayList<Incident> getIncidentListBySid(String sid) throws Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        ArrayList<Incident> maxRevisionList = new ArrayList<Incident>();
        ArrayList<Incident> incList = cld.fetchIncidentListBySid(sid);
        for (Incident incident : incList) {
            ArrayList<Incident> temp = cld.fetchMaxRevisionByIncident(incident.getIncidentNo());
            if (temp != null && !temp.isEmpty()) {
                maxRevisionList.add(temp.get(0));
            }
        }
        return maxRevisionList;
    }

    /**
     * @desc This method gets the IncidentRevisionList by incident No
     * @param incidentNo
     * @exception Exception
     * @return ArrayList
     */
    public ArrayList<Incident> getIncidentRevisionList(int incidentNo) throws Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        ArrayList<Incident> revisionList = cld.fetchIncidentRevisionList(incidentNo);
        return revisionList;
    }

    /**
     * @desc This method gets the IncidentRevisionList by incident No
     * @param incidentNo
     * @exception Exception
     * @return ArrayList
     */
    public ArrayList<Followup> getFollowupRevisionList(int incidentNo) throws Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        ArrayList<Followup> revisionList = cld.fetchFollowupRevisionList(incidentNo);
        return revisionList;
    }

    /**
     * @desc This method gets the IncidentList by Sid
     * @param region
     * @param district
     * @param incidentDateFrom
     * @param incidentDateTo
     * @param submittedDateFrom
     * @param submittedDateTo
     * @exception Exception
     * @return ArrayList
     */
    public ArrayList<Incident> getIncidentListByVendor(String region, String district, Date incidentDateFrom, Date incidentDateTo,
            Date submittedDateFrom, Date submittedDateTo, String reportStatus) throws Exception {
        ArrayList<Incident> maxRevisionList = new ArrayList<Incident>();
        List<Integer> tempList = new ArrayList<Integer>();
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
       
        ArrayList<Incident> incList = cld.fetchIncidentListByVendor(region, district, incidentDateFrom, incidentDateTo, submittedDateFrom, submittedDateTo, reportStatus);
        if (incList != null && !incList.isEmpty()) {
            Map<String, Vendor> vendorMap = getVendorMap();
            for (Incident incident : incList) {
                ArrayList<Incident> temp = cld.fetchMaxRevisionByIncident(incident.getIncidentNo());
                if (!tempList.contains(incident.getIncidentNo())) {
                    Vendor vendor = vendorMap.get(district);
                    temp.get(0).setVendorName(vendor != null ? vendor.getName() : "");
                    maxRevisionList.add(temp.get(0));
                    tempList.add(incident.getIncidentNo());
                }
            }
        }
        return maxRevisionList;
    }

    /**
     * @desc This method gets the Incident Review List by the Vendor
     * @param region
     * @param district
     * @exception Exception
     * @return ArrayList
     */
    public ArrayList<Incident> getIncidentReviewListByVendor(String region, String district,String vendorType) throws Exception {
        ArrayList<Incident> maxRevisionList = new ArrayList<Incident>();
        List<Integer> tempList = new ArrayList<Integer>();
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        ArrayList<Incident> incList = cld.fetchIncidentReviewListByVendor(region, district, vendorType);
        if (incList != null && !incList.isEmpty()) {
            for (Incident incident : incList) {
                Map<String, Vendor> vendorMap = getVendorMap();
                ArrayList<Incident> temp = cld.fetchMaxRevisionByIncident(incident.getIncidentNo());
                if (!tempList.contains(incident.getIncidentNo())) {
                    Vendor vendor = vendorMap.get(district);
                    temp.get(0).setVendorName(vendor != null ? vendor.getName() : "");
                    maxRevisionList.add(temp.get(0));
                    tempList.add(incident.getIncidentNo());
                }
            }
        }
        return maxRevisionList;
    }

    /**
     * @desc This method gets the Incident Review List reviewed by TCOOMMI users
     * @param region
     * @param district
     * @exception Exception
     * @return ArrayList
     */
    public ArrayList<Incident> getIncidentReviewListByTCOOMMI(String region, String district) throws Exception {
        ArrayList<Incident> maxRevisionList = new ArrayList<Incident>();
        List<Integer> tempList = new ArrayList<Integer>();
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        ArrayList<Incident> incList = cld.fetchIncidentReviewListByTCOOMMI(region, district);
        if (incList != null && !incList.isEmpty()) {
            Map<String, Vendor> vendorMap = getVendorMap();
            for (Incident incident : incList) {
                ArrayList<Incident> temp = cld.fetchMaxRevisionByIncident(incident.getIncidentNo());
                if (!tempList.contains(incident.getIncidentNo())) {
                    Vendor vendor = vendorMap.get(district);
                    temp.get(0).setVendorName(vendor != null ? vendor.getName() : "");
                    maxRevisionList.add(temp.get(0));
                    tempList.add(incident.getIncidentNo());
                }
            }
        }
        return maxRevisionList;
    }

    /**
     * @desc This method gets the Incident Review List reviewed by HSS users
     * @param region
     * @param district
     * @exception Exception
     * @return ArrayList
     */
    public ArrayList<Incident> getIncidentReviewListByHSS(String hssId, boolean isHss, String region, String district, String vendorType) throws Exception {
        ArrayList<Incident> maxRevisionList = new ArrayList<Incident>();
        List<Integer> tempList = new ArrayList<Integer>();
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        ArrayList<Incident> incList = cld.fetchIncidentReviewListByVendor(region, district, vendorType);
        if (incList != null && !incList.isEmpty()) {
            Map<String, Vendor> vendorMap = getVendorMap();
            for (Incident incident : incList) {

                ArrayList<Incident> temp = cld.fetchMaxRevisionByIncident(incident.getIncidentNo());
                if (!tempList.contains(incident.getIncidentNo())) {
                    Vendor vendor = vendorMap.get(district);
                    temp.get(0).setVendorName(vendor != null ? vendor.getName() : "");
                    if (isHss && temp.get(0).getSubmittedBy().equals(hssId)) {
                        maxRevisionList.add(temp.get(0));
                        tempList.add(incident.getIncidentNo());
                    } else if (!isHss) {
                        maxRevisionList.add(temp.get(0));
                        tempList.add(incident.getIncidentNo());
                    }

                }
            }
        }
        return maxRevisionList;
    }

    /**
     * @desc This method gets the worklist of the users
     * @param userId
     * @param listType
     * @exception Exception
     * @return ArrayList
     */
    public ArrayList<Incident> getWorkListByUser(String userId, String listType) throws Exception {
        ArrayList<Incident> maxRevisionList = new ArrayList<Incident>();
        List<Integer> tempList = new ArrayList<Integer>();

        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        ArrayList<Incident> incList = cld.getWorkListByUser(userId, listType);
        for (Incident incident : incList) {
            ArrayList<Incident> temp = cld.fetchMaxRevisionByIncident(incident.getIncidentNo());
            if (!tempList.contains(incident.getIncidentNo())) {
                if (listType.equals(TCOOMMIConstants.LIST_TYPE_AWAITING_REVIEW) && (temp.get(0).getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_SUBMITTED
                        || temp.get(0).getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_RE_SUBMITTED)) {
                    maxRevisionList.add(temp.get(0));
                    tempList.add(incident.getIncidentNo());
                } else if (listType.equals(TCOOMMIConstants.LIST_TYPE_NEEDS_REVISION) && (temp.get(0).getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_REJECTED)) {
                    maxRevisionList.add(temp.get(0));
                    tempList.add(incident.getIncidentNo());
                } else if (listType.equals(TCOOMMIConstants.LIST_TYPE_AWAITING_FOLLOWUP) && (temp.get(0).getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_ACCEPTED_PENDING_FOLLOWUP)) {
                    maxRevisionList.add(temp.get(0));
                    tempList.add(incident.getIncidentNo());
                }else if (listType.equals(TCOOMMIConstants.LIST_TYPE_REQUEST_FOLLOWUP) && (temp.get(0).getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_ACCEPTED_REQUEST_FOLLOWUP)) {
                    maxRevisionList.add(temp.get(0));
                    tempList.add(incident.getIncidentNo());
                }
            }
        }
        return maxRevisionList;
    }
    
     /**
     * @desc This method gets the worklist of the users
     * @param userId
     * @param listType
     * @exception Exception
     * @return ArrayList
     */
    public ArrayList<Followup> getFollowupWorkListByUser(String userId, String listType) throws Exception {
        ArrayList<Followup> maxRevisionList = new ArrayList<Followup>();
        List<Integer> tempList = new ArrayList<Integer>();

        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        ArrayList<Followup> followupList = cld.getFollowupWorkListByUser(userId, listType);
        for (Followup followup : followupList) {
            ArrayList<Followup> temp = cld.fetchMaxRevisionByFollowup(followup.getFollowUpNo());
            if (!tempList.contains(followup.getFollowUpNo())) {
                if (listType.equals(TCOOMMIConstants.LIST_TYPE_AWAITING_FOLLOWUP_REVIEW) && (temp.get(0).getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_SUBMITTED
                        || temp.get(0).getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_RE_SUBMITTED)) {
                    maxRevisionList.add(temp.get(0));
                    tempList.add(followup.getFollowUpNo());
                } else if (listType.equals(TCOOMMIConstants.LIST_TYPE_NEEDS_FOLLOWUP_REVISION) && (temp.get(0).getReportStatusCode() == TCOOMMIConstants.REPORT_STATUSCODE_REJECTED)) {
                    maxRevisionList.add(temp.get(0));
                    tempList.add(followup.getFollowUpNo());
                } 
            }
        }
        return maxRevisionList;
    }

     /**
     * @desc This method gets the Case Worker details based on the user id
     * @param userId
     * @exception Exception
     * @return String
     */
    public String getUserNameById(String userId) throws Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        return cld.fetchUserNameById(userId);
    }

    /**
     * @desc This method updates the incident report Assignment
     * @exception Exception
     * @param fromUserId
     * @param toUserId
     */
    public int updateIncidentAssignment(String fromUserId, String toUserId) throws Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        int updateCount = cld.singleGenericUpdateWithCount(IncidentMapper.QUERY.UPDATE_INCIDENT_ASSIGNMENTS,
                fromUserId, toUserId);
        return updateCount;
    }

    /**
     * @desc This method updates the Followup report Assignment
     * @exception Exception
     * @param fromUserId
     * @param toUserId
     */
    public int updateFollowupAssignment(String fromUserId, String toUserId) throws Exception {
        IncidentDao cld = DaoFactory.getDao(IncidentDao.class);
        int updateCount = cld.singleGenericUpdateWithCount(IncidentMapper.QUERY.UPDATE_FOLLOWUP_ASSIGNMENTS,
                fromUserId, toUserId);
        return updateCount;
    }

    /**
     * @desc This method deletes the incident report
     * @exception Exception
     * @param incidentMap
     */
    public void deleteIncidentReport(List<Integer> incidentMap)
            throws Exception {
        if (empty(incidentMap)) {
            return;
        }
        IncidentDao id = DaoFactory.getDao(IncidentDao.class);
        for (Integer incidentNo : incidentMap) {
            System.out.println("IncidentService.deleteIncidentReport " + incidentNo);
            id.singleGenericUpdate(IncidentMapper.QUERY.DELETE_INCIDENT_REPORT, incidentNo);
        }
    }

    /**
     * @desc This method deletes the Followup report
     * @exception Exception
     * @param followupMap
     */
    public void deleteFollowupReport(List<Integer> followupMap)
            throws Exception {
        if (empty(followupMap)) {
            return;
        }

        IncidentDao id = DaoFactory.getDao(IncidentDao.class);

        for (Integer followupNo : followupMap) {
            System.out.println("IncidentService.deleteFollowupReport " + followupNo);
            id.singleGenericUpdate(IncidentMapper.QUERY.DELETE_FOLLOWUP_REPORT, followupNo);
        }
    }
    
    /**
     * @desc This method builds the Vendor list
     * @return List
     */
    public Map<String, Vendor> getVendorMap() {

        HashMap<String, Vendor> vendorMap = new HashMap<String, Vendor>();
        List<Vendor> vendors = CacheMaster.getVendors();
        for (Vendor vendor : vendors) {
            vendorMap.put(vendor.getDistrict(), vendor);
        }
        return vendorMap;
    }
    
    public RoleDetail getUserRoleDetail(String userId) throws Exception{
        
        UserDao ud = DaoFactory.getDao(UserDao.class);
        
        UserInfo userInfo = ud.singleObjectUserMapperFetch(
                    UserMapper.QUERY.FETCH_USER_INFO_SHELL,
                    UserInfo.class,
                    userId);
     
        RoleDetail rd = ud.singleObjectUserMapperFetch(
                UserMapper.QUERY.FETCH_USER_ROLE_DETAIL_BY_USERID_NO,
                RoleDetail.class,
                userInfo.getUserIdNo());
        
        return rd;
    }
}
