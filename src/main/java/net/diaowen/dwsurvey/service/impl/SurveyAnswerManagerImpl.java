package net.diaowen.dwsurvey.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hazelcast.logging.ILogger;
import net.diaowen.common.QuType;
import net.diaowen.common.base.entity.User;
import net.diaowen.common.base.service.AccountManager;
import net.diaowen.common.plugs.page.Page;
import net.diaowen.common.service.BaseServiceImpl;
import net.diaowen.common.utils.RunAnswerUtil;
import net.diaowen.common.utils.excel.XLSXExportUtil;
import net.diaowen.common.utils.parsehtml.HtmlUtil;
import net.diaowen.dwsurvey.config.DWSurveyConfig;
import net.diaowen.dwsurvey.dao.SurveyAnswerDao;
import net.diaowen.dwsurvey.entity.*;
import net.diaowen.dwsurvey.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.util.FileUtil;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;



/**
 * 问卷回答记录
 * @author keyuan(keyuan258@gmail.com)
 *
 * https://github.com/wkeyuan/DWSurvey
 * http://dwsurvey.net
 */
@Service
public class SurveyAnswerManagerImpl extends
		BaseServiceImpl<SurveyAnswer, String> implements SurveyAnswerManager {

	private static final Logger logger = LogManager.getLogger(ReportItemManagerImpl.class.getName());

	@Autowired
	private SurveyAnswerDao surveyAnswerDao;
	@Autowired
	private QuestionManager questionManager;
	@Autowired
	private AnYesnoManager anYesnoManager;
	@Autowired
	private AnRadioManager anRadioManager;
	@Autowired
	private AnFillblankManager anFillblankManager;
	@Autowired
	private AnEnumquManager anEnumquManager;
	@Autowired
	private AnDFillblankManager anDFillblankManager;
	@Autowired
	private AnCheckboxManager anCheckboxManager;
	@Autowired
	private AnAnswerManager anAnswerManager;
	@Autowired
	private AnScoreManager anScoreManager;
	@Autowired
	private AnOrderManager anOrderManager;
	@Autowired
	private AnUploadFileManager anUploadFileManager;
	@Autowired
	private SurveyDirectoryManager directoryManager;
	@Autowired
	private SurveyDirectoryManager surveyDirectoryManager;
	@Autowired
	private AccountManager accountManager;
	@Autowired
	private ReportItemManager reportItemManager;

	@Override
	public void setBaseDao() {
		this.baseDao = surveyAnswerDao;
	}

	@Override
	public void saveAnswer(SurveyAnswer surveyAnswer,
			Map<String, Map<String, Object>> quMaps) {
		surveyAnswerDao.saveAnswer(surveyAnswer, quMaps);
//		HashMap<String, HashMap<String, Object>> quAnswerInfo = buildQuAnswerInfo(surveyAnswer);
//		String jsonString = JSON.toJSONString(quAnswerInfo);
//		surveyAnswer.setQuAnswerInfo(jsonString);
//		surveyAnswerDao.save(surveyAnswer);
	}

	@Override
	@Transactional
	public List<Question> findAnswerDetail(SurveyAnswer answer) {
		String surveyId = answer.getSurveyId();
		String surveyAnswerId = answer.getId();
		List<Question> questions = questionManager.findDetails(surveyId, "2");
		for (Question question : questions) {
			getquestionAnswer(surveyAnswerId, question);
		}
		return questions;
	}


	/**
	 * 取问卷值方式
	 *
	 * @param surveyAnswerId
	 * @param question
	 * @return
	 */
	public int getquestionAnswer(String surveyAnswerId, Question question) {
		int score = 0;
		String quId = question.getId();
		// 查询每一题的答案,如果是主观题，则判断是否答对
		QuType quType = question.getQuType();

		//重置导出
		question.setAnAnswer(new AnAnswer());
		question.setAnCheckboxs(new ArrayList<AnCheckbox>());
		question.setAnDFillblanks(new ArrayList<AnDFillblank>());
		question.setAnEnumqus(new ArrayList<AnEnumqu>());
		question.setAnFillblank(new AnFillblank());
		question.setAnRadio(new AnRadio());
		question.setAnYesno(new AnYesno());
		question.setAnScores(new ArrayList<AnScore>());
		question.setAnOrders(new ArrayList<AnOrder>());

		if (quType == QuType.YESNO) {// 是非题答案
			AnYesno anYesno = anYesnoManager.findAnswer(surveyAnswerId, quId);
			if (anYesno != null) {
				question.setAnYesno(anYesno);
			}
		} else if (quType == QuType.RADIO || quType == QuType.COMPRADIO) {// 单选题答案
			// 复合
			AnRadio anRadio = anRadioManager.findAnswer(surveyAnswerId, quId);
			if (anRadio != null) {
				question.setAnRadio(anRadio);
			}
		} else if (quType == QuType.CHECKBOX || quType == QuType.COMPCHECKBOX) {// 多选题答案
			// 复合
			List<AnCheckbox> anCheckboxs = anCheckboxManager.findAnswer(
					surveyAnswerId, quId);
			if (anCheckboxs != null) {
				question.setAnCheckboxs(anCheckboxs);
			}
		} else if (quType == QuType.FILLBLANK) {// 单项填空题答案
			AnFillblank anFillblank = anFillblankManager.findAnswer(
					surveyAnswerId, quId);
			if (anFillblank != null) {
				question.setAnFillblank(anFillblank);
			}

		} else if (quType == QuType.MULTIFILLBLANK) {// 多项填空题答案
			List<AnDFillblank> anDFillblanks = anDFillblankManager.findAnswer(
					surveyAnswerId, quId);
			if (anDFillblanks != null) {
				question.setAnDFillblanks(anDFillblanks);
			}
		} else if (quType == QuType.ANSWER) {// 问答题答案
			AnAnswer anAnswer = anAnswerManager
					.findAnswer(surveyAnswerId, quId);
			if (anAnswer != null) {
				question.setAnAnswer(anAnswer);
			}
		} else if (quType == QuType.BIGQU) {// 大题答案
			// List<Question> childQuestions=question.getQuestions();
			// for (Question childQuestion : childQuestions) {
			// score=getquestionAnswer(surveyAnswerId, childQuestion);
			// }
		} else if (quType == QuType.ENUMQU) {
			List<AnEnumqu> anEnumqus = anEnumquManager.findAnswer(
					surveyAnswerId, quId);
			if (anEnumqus != null) {
				question.setAnEnumqus(anEnumqus);
			}
		} else if (quType == QuType.SCORE) {// 评分题
			List<AnScore> anScores = anScoreManager.findAnswer(surveyAnswerId,
					quId);
			if (anScores != null) {
				question.setAnScores(anScores);
			}
		} else if(quType == QuType.ORDERQU){
			List<AnOrder> anOrders = anOrderManager.findAnswer(surveyAnswerId,quId);
			if(anOrders!=null){
				question.setAnOrders(anOrders);
			}
		} else if (quType == QuType.UPLOADFILE) {
			List<AnUplodFile> anUplodFiles = anUploadFileManager.findAnswer(surveyAnswerId,quId);
			question.setAnUplodFiles(anUplodFiles);
		}
		return score;
	}

	@Override
	public SurveyAnswer getTimeInByIp(SurveyDetail surveyDetail, String ip) {
		String surveyId = surveyDetail.getDirId();
		Criterion eqSurveyId = Restrictions.eq("surveyId", surveyId);
		Criterion eqIp = Restrictions.eq("ipAddr", ip);

		int minute = surveyDetail.getEffectiveTime();
		Date curdate = new Date();
		Calendar calendarDate = Calendar.getInstance();
		calendarDate.setTime(curdate);
		calendarDate.set(Calendar.MINUTE, calendarDate.get(Calendar.MINUTE)
				- minute);
		Date date = calendarDate.getTime();

		Criterion gtEndDate = Restrictions.gt("endAnDate", date);
		return surveyAnswerDao.findFirst("endAnDate", true, eqSurveyId, eqIp,
				gtEndDate);

	}

	@Override
	public Long getCountByIp(String surveyId, String ip) {
		String hql = "select count(*) from SurveyAnswer x where x.surveyId=?1 and x.ipAddr=?2";
		Long count = (Long) surveyAnswerDao.findUniObjs(hql, surveyId, ip);
		return count;
	}

	@Override
	public Long getCountByUserId(String surveyId, String userId, Integer dateLimit){
		Long count = 0L;
		String hql = "select count(*) from SurveyAnswer x where x.surveyId=?1 and x.userId=?2";
		if (dateLimit != null && dateLimit > 0) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -dateLimit);
			hql += " and x.endAnDate >=?3";
			count = (Long) surveyAnswerDao.findUniObjs(hql, surveyId, userId, cal.getTime());
		}else {
			count = (Long) surveyAnswerDao.findUniObjs(hql, surveyId, userId);
		}
		return count;
	}

	@Override
	public List<SurveyAnswer> answersByIp(String surveyId, String ip) {
		Criterion criterionSurveyId = Restrictions.eq("surveyId", surveyId);
		Criterion criterionIp = Restrictions.eq("ipAddr", ip);
		List<SurveyAnswer> answers = surveyAnswerDao.find(criterionSurveyId,
				criterionIp);
		return answers;
	}


	@Override
	public String exportXLS(String surveyId, String savePath, boolean isExpUpQu) {
		String basepath = surveyId + "";
		String urlPath = "/webin/expfile/" + basepath + "/";// 下载所用的地址
		String path = urlPath.replace("/", File.separator);// 文件系统路径
		// File.separator +
		// "file" +
		// File.separator+basepath
		// + File.separator;
		savePath = savePath + path;
		File file = new File(savePath);
		if (!file.exists())
			file.mkdirs();

		SurveyDirectory surveyDirectory = directoryManager.getSurvey(surveyId);
		String fileName = "DWSurvey-"+surveyId + ".xlsx";

		XLSXExportUtil exportUtil = new XLSXExportUtil(fileName, savePath);
//		Criterion cri1 = Restrictions.eq("surveyId",surveyId);
//		Page<SurveyAnswer> page = new Page<SurveyAnswer>();
//		page.setPageSize(5000);
		try {
//			page = findPage(page,cri1);
//			int totalPage = page.getTotalPage();
			List<SurveyAnswer> answers = answerList(surveyId, 1);
			List<Question> questions = questionManager.findDetails(surveyId,"2");
			exportXLSTitle(exportUtil, questions);
			int answerListSize = answers.size();
			for (int j = 0; j < answerListSize; j++) {
				SurveyAnswer surveyAnswer = answers.get(j);
				String surveyAnswerId = surveyAnswer.getId();
				exportUtil.createRow(j+1);
				exportXLSRow(exportUtil, surveyAnswerId, questions, surveyAnswer, (j+1), savePath, isExpUpQu);
			}
			exportUtil.exportXLS();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return urlPath + fileName;
	}

	private void exportXLSRow(XLSXExportUtil exportUtil,String surveyAnswerId, List<Question> questions,SurveyAnswer surveyAnswer,int orderNum, String savePath, boolean isExpUpQu) {
		new RunAnswerUtil().getQuestionMap(questions,surveyAnswerId);
		int cellIndex = 0;
		int quNum=0;
		for (Question question : questions) {
			QuType quType = question.getQuType();
			if(quType==QuType.PAGETAG || quType==QuType.PARAGRAPH){
				continue;
			}
			quNum++;
			String quName = question.getQuName();
			String titleName = quType.getCnName();
			if (quType == QuType.YESNO) {// 是非题
				String yesnoAnswer = question.getAnYesno().getYesnoAnswer();
				if("1".equals(yesnoAnswer)){
					yesnoAnswer=question.getYesnoOption().getTrueValue();
				}else if("0".equals(yesnoAnswer)){
					yesnoAnswer=question.getYesnoOption().getFalseValue();
				}else{
					yesnoAnswer="";
				}
				exportUtil.setCell(cellIndex++, yesnoAnswer);
			} else if (quType == QuType.RADIO) {// 单选题
				String quItemId = question.getAnRadio().getQuItemId();
				List<QuRadio> quRadios=question.getQuRadios();
				String answerOptionName="";
				String answerOtherText="";
				boolean isNote = false;
				for (QuRadio quRadio : quRadios) {
					if (quRadio.getIsNote() == 1) {
						isNote = true;
						break;
					}
				}

				for (QuRadio quRadio : quRadios) {
					String quRadioId=quRadio.getId();
					if(quRadioId.equals(quItemId)){
						answerOptionName=quRadio.getOptionName();
						if(quRadio.getIsNote()==1){
							answerOtherText = question.getAnRadio().getOtherText();
						}
						break;
					}
				}
				answerOptionName=HtmlUtil.removeTagFromText(answerOptionName);
				answerOptionName = answerOptionName.replace("&nbsp;"," ");
				exportUtil.setCell(cellIndex++, answerOptionName);
				if(isNote) exportUtil.setCell(cellIndex++, answerOtherText);
			} else if (quType == QuType.CHECKBOX) {// 多选题
				List<AnCheckbox> anCheckboxs=question.getAnCheckboxs();
				List<QuCheckbox> checkboxs = question.getQuCheckboxs();
				for (QuCheckbox quCheckbox : checkboxs) {
					String quCkId=quCheckbox.getId();
					String answerOptionName="0";
					String answerOtherText="";
					for (AnCheckbox anCheckbox : anCheckboxs) {
						String anQuItemId=anCheckbox.getQuItemId();
						if(quCkId.equals(anQuItemId)){
							answerOptionName=quCheckbox.getOptionName();
							answerOptionName="1";
							if(quCheckbox.getIsNote() == 1){
								answerOtherText = anCheckbox.getOtherText();
							}
							break;
						}
					}
					answerOptionName=HtmlUtil.removeTagFromText(answerOptionName);
					answerOptionName = answerOptionName.replace("&nbsp;"," ");
					exportUtil.setCell(cellIndex++, answerOptionName);
					if(quCheckbox.getIsNote() == 1)
						exportUtil.setCell(cellIndex++, answerOtherText);
				}
			} else if (quType == QuType.FILLBLANK) {// 填空题
				AnFillblank anFillblank=question.getAnFillblank();
				exportUtil.setCell(cellIndex++, anFillblank.getAnswer());
			} else if (quType == QuType.ANSWER) {// 多行填空题
				AnAnswer anAnswer=question.getAnAnswer();
				exportUtil.setCell(cellIndex++, anAnswer.getAnswer());
			} else if (quType == QuType.COMPRADIO) {// 复合单选题
				AnRadio anRadio=question.getAnRadio();
				String quItemId = anRadio.getQuItemId();
				List<QuRadio> quRadios=question.getQuRadios();
				String answerOptionName="";
				String answerOtherText="";
				for (QuRadio quRadio : quRadios) {
					String quRadioId=quRadio.getId();
					if(quRadioId.equals(quItemId)){
						answerOptionName=quRadio.getOptionName();
						answerOtherText=anRadio.getOtherText();
						break;
					}
				}
				answerOptionName=HtmlUtil.removeTagFromText(answerOptionName);
				answerOtherText=HtmlUtil.removeTagFromText(answerOtherText);
				answerOptionName = answerOptionName.replace("&nbsp;"," ");
				exportUtil.setCell(cellIndex++, answerOptionName);
				exportUtil.setCell(cellIndex++, answerOtherText);
			} else if (quType == QuType.COMPCHECKBOX) {// 复合多选题
				List<AnCheckbox> anCheckboxs=question.getAnCheckboxs();
				List<QuCheckbox> checkboxs = question.getQuCheckboxs();
				for (QuCheckbox quCheckbox : checkboxs) {
					String quCkId=quCheckbox.getId();
					String answerOptionName="0";
					String answerOtherText="0";
					for (AnCheckbox anCheckbox : anCheckboxs) {
						String anQuItemId=anCheckbox.getQuItemId();
						if(quCkId.equals(anQuItemId)){
							answerOptionName=quCheckbox.getOptionName();
							answerOptionName="1";
							answerOtherText=anCheckbox.getOtherText();
							break;
						}
					}
					answerOptionName=HtmlUtil.removeTagFromText(answerOptionName);
					answerOptionName = answerOptionName.replace("&nbsp;"," ");
					exportUtil.setCell(cellIndex++, answerOptionName);
					if(1==quCheckbox.getIsNote()){
						answerOtherText=HtmlUtil.removeTagFromText(answerOtherText);
						exportUtil.setCell(cellIndex++, answerOtherText);
					}
				}
			} else if (quType == QuType.ENUMQU) {// 枚举题
				List<AnEnumqu> anEnumqus=question.getAnEnumqus();
				int enumNum = question.getParamInt01();
				for (int i = 0; i < enumNum; i++) {
					String answerEnum="";
					for (AnEnumqu anEnumqu : anEnumqus) {
						if(i==anEnumqu.getEnumItem()){
							answerEnum=anEnumqu.getAnswer();
							break;
						}
					}
					exportUtil.setCell(cellIndex++,  answerEnum);
				}
			} else if (quType == QuType.ORDERQU) {// 评分题
				List<QuOrderby> quOrderbys = question.getQuOrderbys();
				List<AnOrder> anOrders=question.getAnOrders();
				for (QuOrderby quOrderby : quOrderbys) {
					String quOrderbyId=quOrderby.getId();
					String answerOptionName="";
					for (AnOrder anOrder : anOrders) {
						if(quOrderbyId.equals(anOrder.getQuRowId())){
							answerOptionName=anOrder.getOrderyNum();
							break;
						}
					}
					answerOptionName = answerOptionName.replace("&nbsp;"," ");
					exportUtil.setCell(cellIndex++, answerOptionName);
				}
			} else if (quType == QuType.MULTIFILLBLANK) {// 组合填空题
				List<QuMultiFillblank> quMultiFillblanks = question.getQuMultiFillblanks();
				List<AnDFillblank> anDFillblanks=question.getAnDFillblanks();
				for (QuMultiFillblank quMultiFillblank : quMultiFillblanks) {
					String quMultiFillbankId=quMultiFillblank.getId();
					String answerOptionName="";
					for (AnDFillblank anDFillblank : anDFillblanks) {
						if(quMultiFillbankId.equals(anDFillblank.getQuItemId())){
							answerOptionName=anDFillblank.getAnswer();
							break;
						}
					}
					answerOptionName = answerOptionName.replace("&nbsp;"," ");
					exportUtil.setCell(cellIndex++, answerOptionName);
				}
			} else if (quType == QuType.SCORE) {// 评分题
				List<QuScore> quScores = question.getQuScores();
				List<AnScore> anScores=question.getAnScores();
				for (QuScore quScore : quScores) {
					String quScoreId=quScore.getId();
					String answerScore="";
					for (AnScore anScore : anScores) {
						if(quScoreId.equals(anScore.getQuRowId())){
							answerScore=anScore.getAnswserScore();
							break;
						}
					}
					exportUtil.setCell(cellIndex++, answerScore);
				}
			}else if (quType == QuType.UPLOADFILE) {
				//为导出文件
				String upFilePath = File.separator + "webin" + File.separator + "upload" + File.separator;
				List<AnUplodFile> anUplodFiles = question.getAnUplodFiles();
				String answerBuf = "";

				String toFilePath = "";
				if(isExpUpQu){
//					String toFilePath = savePath+File.separator+orderNum+File.separator+HtmlUtil.removeTagFromText(titleName);
//					String toFilePath = savePath + File.separator + orderNum + File.separator + quNum + "_" + HtmlUtil.removeTagFromText(titleName);
					toFilePath = savePath + File.separator + orderNum + File.separator + "Q_" + quNum;
					File file = new File(toFilePath);
					if (!file.exists()) file.mkdirs();
				}
				for (AnUplodFile anUplodFile : anUplodFiles) {
					answerBuf += anUplodFile.getFileName() + "      ";
					if(isExpUpQu){
						File fromFile = new File(DWSurveyConfig.DWSURVEY_WEB_FILE_PATH + anUplodFile.getFilePath());
						if (fromFile.exists()) {
							File toFile = new File(toFilePath + File.separator + anUplodFile.getFileName());
							try {
								FileUtil.copyFile(fromFile, toFile);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
//				answerBuf=answerBuf.substring(0,answerBuf.lastIndexOf("      "));
				exportUtil.setCell(cellIndex++, answerBuf);
			}
		}

		exportUtil.setCell(cellIndex++,  surveyAnswer.getIpAddr());
		exportUtil.setCell(cellIndex++,  surveyAnswer.getCity());
		exportUtil.setCell(cellIndex++,  new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒").format(surveyAnswer.getEndAnDate()));


	}

	private void exportXLSTitle(XLSXExportUtil exportUtil,
								List<Question> questions) {
		exportUtil.createRow(0);
		int cellIndex = 0;


		int quNum=0;
		for (Question question : questions) {
			QuType quType = question.getQuType();
			if(quType==QuType.PAGETAG || quType==QuType.PARAGRAPH){
				continue;
			}
			quNum++;

//			String quName = question.getQuName();
			String quName = question.getQuTitle();
			quName=HtmlUtil.removeTagFromText(quName);
			String titleName =quNum +"、" + quName + "[" + quType.getCnName() + "]";
			if (quType == QuType.YESNO) {// 是非题
				exportUtil.setCell(cellIndex++, titleName);
			} else if (quType == QuType.RADIO) {// 单选题
				List<QuRadio> quRadios=question.getQuRadios();
				boolean isNote = false;
				for (QuRadio quRadio : quRadios) {
					if(quRadio.getIsNote()==1){
						isNote = true;
						break;
					}
				}
				exportUtil.setCell(cellIndex++, titleName);
				if(isNote) exportUtil.setCell(cellIndex++, titleName + "选项说明");

			} else if (quType == QuType.CHECKBOX) {// 多选题
				List<QuCheckbox> checkboxs = question.getQuCheckboxs();
				for (QuCheckbox quCheckbox : checkboxs) {
					String optionName = quCheckbox.getOptionName();
					optionName=HtmlUtil.removeTagFromText(optionName);
					exportUtil.setCell(cellIndex++,titleName + "－" + optionName );
					if(quCheckbox.getIsNote()==1){
						exportUtil.setCell(cellIndex++, titleName+ "－" + optionName  + "－选项说明");
					}
				}
			} else if (quType == QuType.FILLBLANK) {// 填空题
				exportUtil.setCell(cellIndex++, titleName);
			} else if (quType == QuType.ANSWER) {// 多行填空题
				exportUtil.setCell(cellIndex++, titleName);
			} else if (quType == QuType.COMPRADIO) {// 复合单选题
				exportUtil.setCell(cellIndex++, titleName);
				exportUtil.setCell(cellIndex++, titleName+"-说明" );

			} else if (quType == QuType.COMPCHECKBOX) {// 复合多选题
				List<QuCheckbox> checkboxs = question.getQuCheckboxs();
				for (QuCheckbox quCheckbox : checkboxs) {
					String optionName = quCheckbox.getOptionName();
					exportUtil.setCell(cellIndex++, titleName + "－"
							+ optionName);
					int isNote = quCheckbox.getIsNote();
					if (isNote == 1) {
						optionName=HtmlUtil.removeTagFromText(optionName);
						exportUtil.setCell(cellIndex++, titleName +"－"+ optionName
								+ "－" + "说明" );
					}
				}
			} else if (quType == QuType.ENUMQU) {// 枚举题
				int enumNum = question.getParamInt01();
				for (int i = 0; i < enumNum; i++) {
					exportUtil.setCell(cellIndex++,  titleName + i + "－枚举");
				}
			} else if (quType == QuType.MULTIFILLBLANK) {// 组合填空题
				List<QuMultiFillblank> quMultiFillblanks = question
						.getQuMultiFillblanks();
				for (QuMultiFillblank quMultiFillblank : quMultiFillblanks) {
					String optionName = quMultiFillblank.getOptionName();

					optionName=HtmlUtil.removeTagFromText(optionName);
					exportUtil.setCell(cellIndex++, titleName + "－"
							+ optionName);
				}
			} else if (quType == QuType.ORDERQU) {// 评分题
				List<QuOrderby> quOrderbys = question.getQuOrderbys();
				for (QuOrderby quOrderby : quOrderbys) {
					String optionName = quOrderby.getOptionName();
					optionName=HtmlUtil.removeTagFromText(optionName);
					exportUtil.setCell(cellIndex++, titleName+"_"+optionName);
				}
			} else if (quType == QuType.SCORE) {// 评分题
				List<QuScore> quScores = question.getQuScores();
				for (QuScore quScore : quScores) {
					String optionName = quScore.getOptionName();
					optionName=HtmlUtil.removeTagFromText(optionName);
					exportUtil.setCell(cellIndex++, titleName+"－"+optionName);
				}
			} else if (quType == QuType.UPLOADFILE) {
				exportUtil.setCell(cellIndex++, titleName);
			}
		}

		exportUtil.setCell(cellIndex++,  "回答者IP");
		exportUtil.setCell(cellIndex++,  "IP所在地");
		exportUtil.setCell(cellIndex++,  "回答时间");

	}

	public void writeToXLS() {

	}

	@Override
	public SurveyStats surveyStatsData(SurveyStats surveyStats) {
		return surveyAnswerDao.surveyStatsData(surveyStats);
	}


	/**
	 * 取一份卷子回答的数据
	 */
	public Page<SurveyAnswer> answerPage(Page<SurveyAnswer> page,String surveyId){
		Criterion cri1=Restrictions.eq("surveyId", surveyId);
		Criterion cri2=Restrictions.lt("handleState", 2);
		page.setOrderBy("endAnDate");
		page.setOrderDir("desc");
		page=findPage(page, cri1, cri2);
		return page;
	}

	public Page<SurveyAnswer> answerPageByUserId(Page<SurveyAnswer> page, String userId, String surveyName) {
		Criterion cri1=Restrictions.eq("userId", userId);
		Criterion cri2=Restrictions.lt("handleState", 2);
		page.setOrderBy("endAnDate");
		page.setOrderDir("desc");
		page=findPage(page, cri1, cri2);
		List<String> surveyIds = page.getResult().stream().map(SurveyAnswer::getSurveyId).collect(Collectors.toList());
		HashMap<String, SurveyDirectory> surveyMap = new HashMap<>();
		List<SurveyDirectory> surveys = surveyDirectoryManager.findByIds(surveyIds);
		surveys.forEach(x-> surveyMap.put(x.getId(), x));
		page.getResult().stream()
				.filter(x -> surveyMap.containsKey(x.getSurveyId()))  // 要过滤已经被删除的问卷
				.forEach(x -> {
					SurveyDirectory surveyDirectory = new SurveyDirectory();
					surveyDirectory.setSurveyName(surveyMap.get(x.getSurveyId()).getSurveyName());
					x.setSurveyDirectory(surveyDirectory);
				});
		if (!Strings.isEmpty(surveyName)) {
			page.setResult(page.getResult().stream().filter(x-> x.getSurveyDirectory().getSurveyName().contains(surveyName)).collect(Collectors.toList()));
		}
		return page;
	}
	public List<SurveyAnswer> answerList(String surveyId,Integer isEff) {
		Criterion cri1=Restrictions.eq("surveyId", surveyId);
		Criterion cri2=Restrictions.lt("handleState", 2);
		Criterion cri3=Restrictions.eq("isEffective", 1);
		if(isEff!=null){
			cri3=Restrictions.eq("isEffective", isEff);
		}
		return surveyAnswerDao.findByOrder("endAnDate",false,cri1, cri2);
	}



	@Transactional
	public SurveyDirectory upAnQuNum(String surveyId){
		SurveyDirectory survey = directoryManager.get(surveyId);
		upAnQuNum(survey);
		return survey;
	}

	@Transactional
	public SurveyDirectory upAnQuNum(SurveyDirectory survey) {
		Long answerCount = surveyAnswerDao.countResult(survey.getId());
		if(answerCount!=null){
			survey.setAnswerNum(answerCount.intValue());
			directoryManager.saveByAdmin(survey);
		}
		return survey;
	}

	@Override
	public List<SurveyDirectory> upAnQuNum(List<SurveyDirectory> result) {
		if(result!=null){
			for (SurveyDirectory survey:result) {
				upAnQuNum(survey);
			}
		}
		return result;
	}

	@Transactional
	@Override
	public void deleteData(String[] ids) {
		String surveyId = null;
		for (String id:ids) {
			SurveyAnswer surveyAnswer = get(id);
			surveyId = surveyAnswer.getSurveyId();
			reportItemManager.archivedReportItemBySurveyAnswer(surveyAnswer);
			delete(surveyAnswer);
		}
		if(surveyId!=null) upAnQuNum(surveyId);
	}

	@Override
	@Transactional
	public void delete(SurveyAnswer t) {
		if(t!=null){
			String belongAnswerId=t.getId();
			t.setHandleState(2);
			surveyAnswerDao.save(t);
			//更新当前答卷的回答记录值
			// 得到题列表
			List<Question> questions = questionManager.findDetails(t.getSurveyId(),"2");
			for (Question question : questions) {
				String quId=question.getId();
				QuType quType = question.getQuType();

				if (quType == QuType.YESNO) {// 是非题

				} else if (quType == QuType.RADIO) {// 单选题
					AnRadio anRadio=anRadioManager.findAnswer(belongAnswerId, quId);
					if(anRadio!=null){
						anRadio.setVisibility(0);
						//是否显示  1显示 0不显示
						anRadioManager.save(anRadio);
					}
				} else if (quType == QuType.CHECKBOX) {// 多选题
					List<AnCheckbox> anCheckboxs=anCheckboxManager.findAnswer(belongAnswerId, quId);
					if(anCheckboxs!=null){
						for (AnCheckbox anCheckbox : anCheckboxs) {
							anCheckbox.setVisibility(0);
							//是否显示  1显示 0不显示
							anCheckboxManager.save(anCheckbox);
						}
					}
				} else if (quType == QuType.FILLBLANK) {// 填空题
					AnFillblank anFillblank=anFillblankManager.findAnswer(belongAnswerId, quId);
					if(anFillblank!=null){
						anFillblank.setVisibility(0);
						//是否显示  1显示 0不显示
						anFillblankManager.save(anFillblank);
					}
				} else if (quType == QuType.ANSWER) {// 多行填空题

					AnAnswer anAnswer=anAnswerManager.findAnswer(belongAnswerId, quId);
					if(anAnswer!=null){
						anAnswer.setVisibility(0);
						//是否显示  1显示 0不显示
						anAnswerManager.save(anAnswer);
					}

				} else if (quType == QuType.COMPRADIO) {// 复合单选题


				} else if (quType == QuType.COMPCHECKBOX) {// 复合多选题

				} else if (quType == QuType.ENUMQU) {// 枚举题

				} else if (quType == QuType.MULTIFILLBLANK) {// 组合填空题
					List<AnDFillblank> anDFillblanks=anDFillblankManager.findAnswer(belongAnswerId, quId);
					if(anDFillblanks!=null){
						for (AnDFillblank anDFillblank : anDFillblanks) {
							anDFillblank.setVisibility(0);
							//是否显示  1显示 0不显示
							anDFillblankManager.save(anDFillblank);
						}
					}
				} else if (quType == QuType.SCORE) {// 评分题

					List<AnScore> anScores=anScoreManager.findAnswer(belongAnswerId, quId);
					if(anScores!=null){
						for (AnScore anScore : anScores) {
							anScore.setVisibility(0);
							//是否显示  1显示 0不显示
							anScoreManager.save(anScore);
						}

					}

				}

			}
		}
		super.delete(t);
	}

	/**
	 * 构建一份答卷的问题答案信息
	 */
	private HashMap<String, HashMap<String, Object>> buildQuAnswerInfo(SurveyAnswer t) {
		HashMap<String, HashMap<String, Object>> result = new HashMap<>();
		// 答卷的题目及答案内容
		List<Question> questions = findAnswerDetail(t);
		for (Question question : questions) {
			result.put(question.getId(), getQuestionAnswer(question));
		}
		return result;
	}

//	public static Map<String, Map<String, Object>> getQuAnswerInfo(String quAnswerInfo) {
//		HashMap<String, Map<String, Object>> result = new HashMap<>();
//		Map<String, Object> jsonObject = JSONObject.parseObject(quAnswerInfo);
//		for (String s : jsonObject.keySet()) {
//			result.put(s, JSONObject.parseObject(jsonObject.get(s).toString()));
//		}
//		return result;
//	}

	@Override
	public Map<String, Map<String, Object>> parseQuAnswerInfo(SurveyAnswer t) {
		HashMap<String, Map<String, Object>> result = new HashMap<>();
		String quAnswerInfo = t.getQuAnswerInfo();
		Map<String, Object> jsonObject = JSONObject.parseObject(quAnswerInfo);
		for (String s : jsonObject.keySet()) {
			result.put(s, JSONObject.parseObject(jsonObject.get(s).toString()));
		}
		return result;
	}

	@Override
	public List<SurveyAnswer> findBySurveyId(String surveyId) {
		return surveyAnswerDao.findBySurveyId(surveyId);
	}

	@Override
	public List<SurveyAnswer> findByUserId(String userId) {
		Criterion criterion1=Restrictions.eq("userId", userId);
		return surveyAnswerDao.find(criterion1);
	}

    @Override
    public List<SurveyAnswer> findByCreateTimeAfter(Date time) {
		return surveyAnswerDao.findByCreateTimeAfter(time);
    }

	@Override
	public void parseUserFromSurveyAnswer(SurveyAnswer surveyAnswer) {
		if (surveyAnswer.getUserId() != null) {
			// 非匿名下的答卷
			return;
		}
		List<Question> questions = findAnswerDetail(surveyAnswer);
		String name = null;
		String email = null;
		String pwd = null;
		for (Question question : questions) {
			if (question.getQuTitle().contains("姓名")) {
				name = question.getAnFillblank().getAnswer();
			}
			if (question.getQuTitle().contains("邮箱")) {
				email = question.getAnFillblank().getAnswer();
			}
			if (question.getQuTitle().equals("学生证号")) {
				String studentId = question.getAnFillblank().getAnswer();
				// if (idCard.length() != 18) {
				// 	logger.info("surveyAnswer {} idCard.length() != 18", surveyAnswer.getId());
				// 	return;
				// }
				if (studentId.length() > 6) {
					pwd = studentId.substring(studentId.length()-6);  // 学生证号后6位作为默认密码
				} else {
					pwd = studentId;
				}
			}
		}
		// 匿名下的答卷根据问卷信息回填答卷用户id
		User user = accountManager.newTempUser(email, pwd, name);
		if (user != null) {
			surveyAnswer.setUserId(user.getId());
			save(surveyAnswer);
		}
	}

	/**
	 * 解析问题答案获取报告需要呈现的内容
	 */
	private HashMap<String, Object> getQuestionAnswer(Question question) {
		HashMap<String, Object> result = new HashMap<>();
		if (question.getQuType().equals(QuType.FILLBLANK)) {
			result.put("title", HtmlUtil.removeTagFromText(question.getQuTitle()));
			result.put("answer", question.getAnFillblank().getAnswer());
		}
		if (question.getQuType().equals(QuType.SCORE)) {
			List<AnScore> scores = question.getAnScores();
			HashMap<String, String> scoresResult = new HashMap<>();
			scores.forEach(x->scoresResult.put(x.getId(), x.getAnswserScore()));
			result.put("title", HtmlUtil.removeTagFromText(question.getQuTitle()));
			result.put("answer", scoresResult);
		}
		if (question.getQuType().equals(QuType.RADIO)) {
			String quItemId = question.getAnRadio().getQuItemId();
			QuRadio quRadio = question.getQuRadios().stream().filter(x -> x.getId().equals(quItemId)).findFirst().orElse(null);
			assert quRadio != null;
			result.put("title", HtmlUtil.removeTagFromText(question.getQuTitle()));
			result.put("answer", HtmlUtil.removeTagFromText(quRadio.getOptionName()));
		}
		// todo 其他题型
		return result;
	}
}
