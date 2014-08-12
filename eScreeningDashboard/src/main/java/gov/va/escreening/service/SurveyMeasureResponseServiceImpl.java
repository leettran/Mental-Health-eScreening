package gov.va.escreening.service;

import gov.va.escreening.domain.MeasureTypeEnum;
import gov.va.escreening.entity.Measure;
import gov.va.escreening.entity.MeasureAnswer;
import gov.va.escreening.entity.Survey;
import gov.va.escreening.entity.SurveyMeasureResponse;
import gov.va.escreening.entity.SurveyPage;
import gov.va.escreening.repository.SurveyMeasureResponseRepository;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ListMultimap;

@Transactional
@Service
public class SurveyMeasureResponseServiceImpl implements SurveyMeasureResponseService {
    private static final Logger logger = LoggerFactory.getLogger(SurveyMeasureResponseServiceImpl.class);

    @Autowired
    private SurveyMeasureResponseRepository surveyMeasureResponseRepository;

    @Transactional(readOnly = true)
    @Override
    public Hashtable<Integer, List<SurveyMeasureResponse>> findForVeteranAssessmentId(int veteranAssessmentId) {

        List<SurveyMeasureResponse> resultList = surveyMeasureResponseRepository
                .findForVeteranAssessmentId(veteranAssessmentId);

        Hashtable<Integer, List<SurveyMeasureResponse>> surveyMeasureResponseMap = new Hashtable<Integer, List<SurveyMeasureResponse>>();

        for (SurveyMeasureResponse surveyMeasureResponse : resultList) {

            if (surveyMeasureResponseMap.containsKey(surveyMeasureResponse.getMeasureAnswer().getMeasureAnswerId())) {
                List<SurveyMeasureResponse> responses = surveyMeasureResponseMap.remove(surveyMeasureResponse
                        .getMeasureAnswer().getMeasureAnswerId());
                responses.add(surveyMeasureResponse);
                surveyMeasureResponseMap.put(surveyMeasureResponse.getMeasureAnswer().getMeasureAnswerId(), responses);
            }
            else {
                List<SurveyMeasureResponse> responses = new ArrayList<SurveyMeasureResponse>();
                responses.add(surveyMeasureResponse);
                surveyMeasureResponseMap.put(surveyMeasureResponse.getMeasureAnswer().getMeasureAnswerId(), responses);
            }
        }
        return surveyMeasureResponseMap;
    }

    @Override
    @Transactional
    public String generateQuestionsAndAnswers(Survey survey, Integer veteranAssessmentId) {

        StringBuilder sb = new StringBuilder();
        sb.append(survey.getName() + "\n");

        ListMultimap<Integer, SurveyMeasureResponse> resp = surveyMeasureResponseRepository
                .getForVeteranAssessmentAndSurvey(
                        veteranAssessmentId, survey.getSurveyId());

        for (SurveyPage page : survey.getSurveyPageList())
        {
            int index =1;
            for (gov.va.escreening.entity.Measure m : page.getMeasures())
            {
                if(m==null) continue;
                 
                appendMeasure(String.valueOf(index++) + ". ", sb, resp, m, "");
                if (m.getChildren() != null)
                {
                    int childIndex = 1;
                    for (Measure measure : m.getChildren())
                    {
                        appendMeasure(String.valueOf(childIndex++) + ". ", sb, resp, measure, "  ");
                    }
                }
            }
        }

        return wrapLines(sb.toString()) + "\n";
    }

    private void appendMeasure(String indexStr, StringBuilder sb, ListMultimap<Integer, SurveyMeasureResponse> resp,
            gov.va.escreening.entity.Measure m, String indentDelta) {
    	String indent = "  " + indentDelta;
    	String ques = m.getVistaText() == null ? m.getMeasureText() : m.getVistaText();
    	ques = ques.trim().isEmpty() ? indent : indent + indexStr + ques;
    	
        String answer = null;
        if (m.getMeasureType().getMeasureTypeId() == MeasureTypeEnum.FREETEXT.getMeasureTypeId())
        {
            MeasureAnswer ma = m.getMeasureAnswerList().get(0);
            if (resp.containsKey(ma.getMeasureAnswerId()))
            {
            	SurveyMeasureResponse mar = resp.get(ma.getMeasureAnswerId()).get(0);
                answer = mar.getTextValue();
                if(answer == null){
                	Long l = mar.getNumberValue();
                	if(l != null)
                		answer = l.toString(); 
                }
                	
            }
        }
        else if (m.getMeasureType().getMeasureTypeId() == MeasureTypeEnum.SELECTONE.getMeasureTypeId() ||
                m.getMeasureType().getMeasureTypeId() == MeasureTypeEnum.SELECTONEMATRIX.getMeasureTypeId())
        {
            for (MeasureAnswer ma : m.getMeasureAnswerList())
            {
                if (resp.containsKey(ma.getMeasureAnswerId()))
                {
                    for (SurveyMeasureResponse smr : resp.get(ma.getMeasureAnswerId()))
                    {
                        if (smr.getBooleanValue() != null && smr.getBooleanValue())
                        {
                            answer = ma.getVistaText() == null ? ma.getAnswerText() : ma.getVistaText();
                            break;
                        }
                    }
                }
            }
        }
        else if (m.getMeasureType().getMeasureTypeId() == MeasureTypeEnum.SELECTMULTI.getMeasureTypeId() ||
                m.getMeasureType().getMeasureTypeId() == MeasureTypeEnum.SELECTMULTIMATRIX.getMeasureTypeId())
        {
            StringBuilder answerStr = new StringBuilder();
            for (MeasureAnswer ma : m.getMeasureAnswerList())
            {
                if (resp.containsKey(ma.getMeasureAnswerId()))
                {
                    SurveyMeasureResponse smr = resp.get(ma.getMeasureAnswerId()).get(0);
                    if (smr.getBooleanValue() != null && smr.getBooleanValue())
                    {
                        answerStr.append("\n    " + indent).append(ma.getVistaText() == null ? ma.getAnswerText() : ma.getVistaText());
                    }
                }
            }
            answer = answerStr.toString();
        }

        sb.append(ques);
        if (answer != null)
        {
            sb.append(" ").append(answer);
        }
        sb.append("\n\n");
    }
    
    /**
     * Wraps given text to 80 columns including a 4 space indent on everything.
     * @param text
     * @return
     */
    private String wrapLines(String text){
    	String newLine = "\n    ";
    	Pattern prefixSpace = Pattern.compile("^(\\s+).*");
    	Pattern newLineReplace = Pattern.compile("\n");
    	
        StringBuilder wrappedText = new StringBuilder();
        String[] lines = text.split("\n");
        for(String line : lines){
        	logger.debug("wrapping line:\n{}", line);
        	Matcher m = prefixSpace.matcher(line);
        	String indent = m.find() ? m.group(1) : "";
        	
        	String wrappedLine = WordUtils.wrap(line, 70, "\n", true);
        	
        	//wrap method removes space if it wraps but doen't if no wrap was needed.
        	if(!indent.isEmpty())
        		wrappedLine = wrappedLine.replaceFirst("^\\s+", "");
        	
        	String margin = newLine + indent;
        	logger.debug("margin size: {}", margin.length());
        	
        	logger.debug("wrapped by itself:\n{}", wrappedLine);
        	
        	//add margin to wrapped lines
        	wrappedLine = newLineReplace.matcher(wrappedLine).replaceAll(margin);
        	
        	logger.debug("wrapped with replaced for each line margin: \n{}", wrappedLine);
        	
        	wrappedText.append(margin).append(wrappedLine);
        }
        
        String wrapped = wrappedText.toString();
        //logger.debug("wrapped text:\n{}", wrapped);
        return wrapped;
    }
}