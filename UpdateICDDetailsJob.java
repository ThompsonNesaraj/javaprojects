/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.tx.state.tdcj.tcoommi.batch.jobs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.validator.GenericValidator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import static us.tx.state.tdcj.tcoommi.batch.jobs.BaseCocJob.validMedicalBoardVotes;
import us.tx.state.tdcj.tcoommi.beans.CocShell;
import us.tx.state.tdcj.tcoommi.beans.OffenderStatusShell;
import us.tx.state.tdcj.tcoommi.beans.helpers.DiagnosisInfo;
import us.tx.state.tdcj.tcoommi.beans.helpers.SidDate;
import us.tx.state.tdcj.tcoommi.beans.referral.Referral;
import us.tx.state.tdcj.tcoommi.constants.BoardVoteCoreConstants;
import us.tx.state.tdcj.tcoommi.constants.TCOOMMIConstants;
import us.tx.state.tdcj.tcoommi.data.dao.OffenderDao;
import us.tx.state.tdcj.tcoommi.data.dao.OffenderStatusDao;
import us.tx.state.tdcj.tcoommi.data.dao.coc.CocListDao;
import us.tx.state.tdcj.tcoommi.data.dao.context.DaoFactory;
import us.tx.state.tdcj.tcoommi.data.mappers.CocListMapper;
import us.tx.state.tdcj.tcoommi.data.mappers.OffenderMapper;
import us.tx.state.tdcj.tcoommi.data.mappers.OffenderStatusMapper;
import us.tx.state.tdcj.tcoommi.service.CocListService;
import us.tx.state.tdcj.tcoommi.service.ReferralService;

/**
 * This nightly batch job updates the the fields of records
 * on the COC List (ie, TATCMI_COC): 
 *
 * @author Thompson
 */
public class UpdateICDDetailsJob extends BaseCocJob implements Job {

    private final static String BATCH_USER_ID = "TCMIBT01";
    CocListService cocService = new CocListService();
    ReferralService refService = new ReferralService();
    OffenderStatusDao osd = null;
    CocListDao csd = null;
  
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        String processSID = null;
        try {

            osd = DaoFactory.getDao(OffenderStatusDao.class);
            csd = DaoFactory.getDao(CocListDao.class);

            List<SidDate> offenderICDetails = cocService.fetchOffenderICDDetails();
            List<String> deleteList = new ArrayList<>();// soft delete the old record
            Map<String,Date> srdListForIDandTF = null;
            Map<String,Date> srdListForSPandIS = null;
            Map<String,Date> srdListForJC = null;
            Map<String,Date> srdList = new HashMap<>();
           

            if (isEmpty(offenderICDetails)) {
                System.out.println("No TCOOMMI records eligible to process");
            } else {
                System.out.println("Total records to process : " + offenderICDetails.size());
              
                srdListForIDandTF = getScheduledReleaseDateList();
                srdListForJC = getSRDateListForJCOffenders();
                srdListForSPandIS = getSRDateListForSPandISOffenders();
                if(srdListForIDandTF != null){
                    srdList.putAll(srdListForIDandTF);
                }
                if(srdListForJC != null){
                    srdList.putAll(srdListForJC);
                }
               if(srdListForSPandIS != null){
                    srdList.putAll(srdListForSPandIS);
                }
                
                for (SidDate offenderICD : offenderICDetails) {
                   
                     if(!srdList.containsKey(offenderICD.getSid())){
                        continue;
                     }
                    if (!GenericValidator.isBlankOrNull(offenderICD.getSid())) {
                        processSID = offenderICD.getSid().trim();
                    } else {
                        continue;
                    }
                    CocShell coc = cocService.fetchCOCShellsBySID(processSID);

                    if (coc == null) {
                        processNewRecord(osd, csd, offenderICD,srdList, false);
                    }else if(!coc.getDeleteFlag().equals("Y") && !coc.getDeleteFlag().equals("P")) {
                        String maxTdcjNumber = cocService.fetchMaxTDCJNumberBySid(processSID);
                         String tdcjNumberAndSidInCoc = null;
                        if(!GenericValidator.isBlankOrNull(maxTdcjNumber)){
                                 tdcjNumberAndSidInCoc = cocService.fetchTDCJNumber(processSID, maxTdcjNumber.trim());
                        }
                              
                        if(GenericValidator.isBlankOrNull(tdcjNumberAndSidInCoc) && !GenericValidator.isBlankOrNull(coc.getTdcj())){  // repeat offender logic
                                    DiagnosisInfo mdDiagInfo = osd.singleObjectFetch(OffenderStatusMapper.QUERY.FETCH_OLDEST_DIAGINFO_FOR_SID_BY_LSTCODE, DiagnosisInfo.class, processSID, TCOOMMIConstants.COC_LST_CODE_MD);
                                    DiagnosisInfo mhDiagInfo = osd.singleObjectFetch(OffenderStatusMapper.QUERY.FETCH_OLDEST_DIAGINFO_FOR_SID_BY_LSTCODE, DiagnosisInfo.class, processSID, TCOOMMIConstants.COC_LST_CODE_MH);
                                   
                                   Referral referral = refService.fetchRefferalBySid(processSID);
                                   if(referral ==null){
                                      processNewRecord(osd, csd, offenderICD,srdList, true);
                                       String tdcj = GenericValidator.isBlankOrNull(coc.getTdcj())?" ":coc.getTdcj();
                                      deleteList.add("P," + coc.getSid()+","+tdcj);
                                      System.out.println("Repeat Offender - SID:[" + processSID + "]");
                                   }else if(referral.getEndDate()!=null && referral.getEndDate().compareTo(getDefaultDate())!=0){
                                            Date diagDate = offenderICD.getDate();
                                            String prevMdCode = coc.getMdIcdCode();
                                            String prevMhCode = coc.getMhIcdCode();

                                    if((mdDiagInfo!=null && !mdDiagInfo.getIcdCode().equals(prevMdCode)) || 
                                            (mhDiagInfo!=null &&!mhDiagInfo.getIcdCode().equals(prevMhCode)) ){
                                        if(isAllowedTimeFrame(diagDate)){
                                           processNewRecord(osd, csd, offenderICD,srdList, true);
                                            String tdcj = GenericValidator.isBlankOrNull(coc.getTdcj())?" ":coc.getTdcj();
                                           deleteList.add("P," + coc.getSid()+","+tdcj);
                                           System.out.println("Repeat Offender - SID:[" + processSID + "]");
                                        }
                                   }

                                   }else if(referral.getEndDate()!=null && referral.getEndDate().compareTo(getDefaultDate())==0){
                                   if(referral.getStatusCode()==1 && ((mhDiagInfo!=null && mhDiagInfo.getIcdCode()!=null) || 
                                          (mhDiagInfo!=null && mhDiagInfo.getIcdCode()!=null))){
                                            String tdcj = GenericValidator.isBlankOrNull(coc.getTdcj())?" ":coc.getTdcj();
                                            deleteList.add("P," + coc.getSid()+","+tdcj);
                                            System.out.println("Repeat Offender - SID:[" + processSID + "]");
                                   }
                                   }
                               }
                    }
                }
                
                if(!deleteList.isEmpty()){
                    updateCocRecord(deleteList, csd);
                }
                System.out.println("UpdateICDDetailsJob completed at :" + new Date());
            }
        } catch (Exception e) {
            System.out.println("Problem occured while inserting SID " + processSID);
            e.printStackTrace();
            throw new JobExecutionException(e);

        }
    }

    /**
     * This method process a new record
     * @param OffenderStatusDao 
     * @param CocListDao 
     * @param SidDate offenderICD
     * @param boolean repeatOffenderFlag
     * @return Hash table<String, String>
     */
    private void processNewRecord(OffenderStatusDao osd,
            CocListDao csd,
            SidDate offenderICD,
            Map<String,Date> srdList,
            boolean repeatOffenderFlag) throws Exception {
        CocShell coc = new CocShell();
        CocShell.initalizeCocShell(coc);
        OffenderStatusShell offenderStatus = getUpdatedOffenderStatus(osd, offenderICD.getSid(),srdList);
        offenderStatus = getBoardVoteInfo(offenderStatus);

        updateICDDetails(coc, offenderStatus, osd);

        if (offenderStatus.getExcludeFlag() != null && !offenderStatus.getExcludeFlag().equals(TCOOMMIConstants.Y)) {

            if (repeatOffenderFlag){
                cocService.updateCocListCodeBySid(coc.getSid(),coc.getAddTimeStamp()); //update listcode , so the past record won't showup 
               
            }
            insertCOCRecord(coc, offenderStatus, csd);
        }
    }

    private boolean insertCOCRecord(CocShell coc, OffenderStatusShell offenderStatus, CocListDao csd) {

        boolean insertRecordStatus = true;

        coc.setSid(offenderStatus.getSid());
        coc.setTdcj(offenderStatus.getTdcj());
        coc.setName(offenderStatus.getName());
        coc.setProgram(BATCH_USER_ID);
        coc.setBoardVoteChgFlag(offenderStatus.getBoardVoteChgFlag());
        if (offenderStatus.getLstCode() != null && (offenderStatus.getLstCode().equals("MH")
                || offenderStatus.getLstCode().equals("MD")
                || offenderStatus.getLstCode().equals("*")
                || offenderStatus.getLstCode().equals("!"))) {
            coc.setBoardVote(validMedicalBoardVotes.get(offenderStatus.getBoardVote()));
        } else if (offenderStatus.getLstCode() != null) {

            if (offenderStatus.getBoardVote().equals(BoardVoteCoreConstants.ACTION_CODE_CASEPULL)) {
                String casePullReviewType = offenderStatus.getBoardVoteReviewType();
                if (casePullReviewType != null && casePullReviewType.equals(TCOOMMIConstants.COC_CP_RVW_TYP_INIT)) {
                    coc.setBoardVote(TCOOMMIConstants.COC_CASE_PULL_INIT);
                } else if (casePullReviewType != null && casePullReviewType.equals(TCOOMMIConstants.COC_CP_RVW_TYP_SUBS)) {
                    coc.setBoardVote(TCOOMMIConstants.COC_CASE_PULL_SUBS);
                } else {
                    coc.setBoardVote(TCOOMMIConstants.COC_CASE_PULL);
                }
            } else {
                coc.setBoardVote(validMRISBoardVotes.get(offenderStatus.getBoardVote()));
            }
        }
        coc.setBoardVoteDate(coc.getBoardVote() != null && !coc.getBoardVote().equals("") ? offenderStatus.getBoardVoteDate() : null);
        coc.setBoardVoteReviewType(offenderStatus.getBoardVoteReviewType());
        coc.setExcludeFlag(offenderStatus.getExcludeFlag());
        coc.setPendListCode(offenderStatus.getPendListCode());
        coc.setLstCode(offenderStatus.getLstCode());
        coc.setReleaseDate(offenderStatus.getReleaseDate());
        coc.setScheduledReleaseDate(offenderStatus.getScheduledReleaseDate());
        coc.setUnit(offenderStatus.getUnit());
        coc.setRecordReason(offenderStatus.getRecordReason());

        try {
            csd.singleGenericCocListMapperUpdate(CocListMapper.QUERY.INSERT_REL2_COC_LIST_RECORD,
                    coc, BATCH_USER_ID);
           System.out.println("UpdateICDDetailsJob.insertCOCRecord - SID:[" + coc.getSid() + "]");
        } catch (Exception e) {
            System.out.println("UpdateICDDetailsJob.insertCOCRecord - Unable to insert CocShell for SID:[" + coc.getSid() + "]");
            System.out.println(ExceptionUtils.getStackTrace(e));
            insertRecordStatus = false;
        }
        return insertRecordStatus;
    }
    
    private boolean isAllowedTimeFrame(Date diagDate) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -6);
        Date cutOffDate = cal.getTime();
        if (diagDate.before(cutOffDate) || diagDate.after(new Date())) {
            return false;
        }
        return true;
    }
    
    private Date getDefaultDate(){
    String defaultDate = "0001-01-01"; 
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
    Date date = null; 
        try {
            date = formatter.parse(defaultDate);
        } catch (ParseException ex) {
            Logger.getLogger(UpdateICDDetailsJob.class.getName()).log(Level.SEVERE, null, ex);
        }
    return date; 
    }
    
    private boolean updateCocRecord(List<String> deleteList, CocListDao cld) {

        boolean updateRecordStatus = true;

        try {
            for (String del : deleteList) {
                String deleteFlag = del.split(",")[0];
                String sid = del.split(",")[1];
                String tdcj = del.split(",")[2];
                cld.singleGenericCocListMapperUpdate(CocListMapper.QUERY.UPDATE_DELETE_FLAG_FOR_COC, 
                                                        deleteFlag,sid,tdcj);
                System.out.println("UpdateICDDetailsJob.updateCocRecord - SID:[" + sid + "]");
            }

        } catch (Exception e) {
            System.out.println("NonTcoommiRecordHandlerJob.updateCocRecord -)");
            System.out.println(ExceptionUtils.getStackTrace(e));
            updateRecordStatus = false;
        }
        return updateRecordStatus;
    }
    
    public Map<String, Date> getScheduledReleaseDateList() throws Exception{
    
     OffenderDao od = DaoFactory.getDao(OffenderDao.class);
           
            Map<String, Date> sidDateMap = null;
            List<SidDate> sidDateList = od.genericListFetch(
                    OffenderMapper.QUERY.FETCH_SRD_FOR_NON_DELETED_COC_SIDS,
                    SidDate.class);

            if (isEmpty(sidDateList)) {
                System.out.println("No sidDates to process.");
            }else{

                sidDateMap = new HashMap<>();
                for (SidDate sd : sidDateList) {
                    sidDateMap.put(sd.getSid(), sd.getDate());
                }
    }
            return sidDateMap;
    }
    
     public Map<String, Date> getSRDateListForSPandISOffenders() throws Exception{
    
     OffenderDao od = DaoFactory.getDao(OffenderDao.class);
           
            Map<String, Date> sidDateMap = null;
            List<SidDate> sidDateList = od.genericListFetch(
                    OffenderMapper.QUERY.FETCH_SRD_FOR_SP_AND_IS_OFFENDERS,
                    SidDate.class);

            if (isEmpty(sidDateList)) {
                System.out.println("No sidDates to process.");
            }else{

                sidDateMap = new HashMap<>();
                for (SidDate sd : sidDateList) {
                    sidDateMap.put(sd.getSid(), sd.getDate());
                }
    }
            return sidDateMap;
    }
     
      public Map<String, Date> getSRDateListForJCOffenders() throws Exception{
    
     OffenderDao od = DaoFactory.getDao(OffenderDao.class);
           
            Map<String, Date> sidDateMap = null;
            List<SidDate> sidDateList = od.genericListFetch(
                    OffenderMapper.QUERY.FETCH_SRD_FOR_JC_OFFENDERS,
                    SidDate.class);

            if (isEmpty(sidDateList)) {
                System.out.println("No sidDates to process.");
            }else{

                sidDateMap = new HashMap<>();
                for (SidDate sd : sidDateList) {
                    sidDateMap.put(sd.getSid(), sd.getDate());
                }
    }
            return sidDateMap;
    }
}