package gov.va.escreening.dto.editors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;

import gov.va.escreening.entity.SurveyBaseProperties;
import gov.va.escreening.serializer.JsonDateSerializer;

@JsonRootName(value="survey")
@JsonIgnoreProperties(ignoreUnknown = true, value = {"isIncludedInBattery", "surveyStatusItemInfo"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyInfo implements Serializable, SurveyBaseProperties {

    private static final long serialVersionUID = 1L;

    private Integer surveyId;
    private String name;
    private String description;
    private Integer version;
    private boolean hasMha;
    private String mhaTestName;
    private Integer displayOrderForSection=1;
	private Date dateCreated;
    private Boolean isIncludedInBattery;
    private SurveySectionInfo surveySectionInfo;
    private SurveyStatusInfo surveyStatusInfo;
    private Integer clinicalReminderId;
    private List<Integer> clinicalReminderIdList=Lists.newArrayList();


    public List<Integer> getClinicalReminderIdList() {
		return clinicalReminderIdList;
	}

	public void setClinicalReminderIdList(List<Integer> clinicalReminderIdList) {
		this.clinicalReminderIdList = clinicalReminderIdList;
	}

	public Integer getClinicalReminderId() {
		return clinicalReminderId;
	}

	public void setClinicalReminderId(Integer clinicalReminderId) {
		this.clinicalReminderId = clinicalReminderId;
	}

	@JsonProperty("id")
    public Integer getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Integer surveyId) {
        this.surveyId = surveyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public boolean isMha() {
        return hasMha;
    }

    public void setMha(boolean hasMha) {
        this.hasMha = hasMha;
    }

    public String getMhaTestName() {
        return mhaTestName;
    }

    public void setMhaTestName(String mhaTestName) {
        this.mhaTestName = mhaTestName;
    }

    @JsonSerialize(using=JsonDateSerializer.class)
    @JsonProperty("createdDate")
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public void setDisplayOrderForSection(Integer displayOrder) {
        this.displayOrderForSection=displayOrder;
    }

    @Override
    public Integer getDisplayOrderForSection() {
        return this.displayOrderForSection;
    }

    public Boolean getIsIncludedInBattery() {
        return isIncludedInBattery;
    }

    public void setIsIncludedInBattery(Boolean isIncludedInBattery) {
        this.isIncludedInBattery = isIncludedInBattery;
    }

    @JsonProperty("surveySection")
    public SurveySectionInfo getSurveySectionInfo() {
        return surveySectionInfo;
    }

    public void setSurveySectionInfo(SurveySectionInfo surveySectionInfo) {
        this.surveySectionInfo = surveySectionInfo;
    }

    public SurveyStatusInfo getSurveyStatusInfo() {
        return surveyStatusInfo;
    }

    public void setSurveyStatusInfo(SurveyStatusInfo surveyStatusInfo) {
        this.surveyStatusInfo = surveyStatusInfo;
    }

    public SurveyInfo() {

    }

    @Override
    public String toString() {
        return "SurveyItem [surveyId=" + surveyId + ", name=" + name + ", description=" + description + ", version="
                + version + ", isMha=" + hasMha 
                + ", mhaTestName=" + mhaTestName
                + ", dateCreated=" + dateCreated
                + ", isIncludedInBattery=" + isIncludedInBattery
                + "]";
    }
}
