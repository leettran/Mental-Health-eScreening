package gov.va.escreening.service;

import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.TemplateElement;
import freemarker.ext.beans.StringModel;
import gov.va.escreening.constants.TemplateConstants;
import gov.va.escreening.constants.TemplateConstants.TemplateType;
import gov.va.escreening.dto.TemplateDTO;
import gov.va.escreening.dto.TemplateTypeDTO;
import gov.va.escreening.dto.template.TemplateElementNodeDTO;
import gov.va.escreening.dto.template.TemplateFileDTO;
import gov.va.escreening.entity.Battery;
import gov.va.escreening.entity.Survey;
import gov.va.escreening.entity.Template;
import gov.va.escreening.entity.VariableTemplate;
import gov.va.escreening.repository.BatteryRepository;
import gov.va.escreening.repository.SurveyRepository;
import gov.va.escreening.repository.TemplateRepository;
import gov.va.escreening.repository.TemplateTypeRepository;
import gov.va.escreening.repository.VariableTemplateRepository;
import gov.va.escreening.templateprocessor.TemplateProcessorService;
import gov.va.escreening.transformer.TemplateTransformer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TemplateServiceImpl implements TemplateService {

	@Autowired
	private TemplateRepository templateRepository;

	@Autowired
	private TemplateTypeRepository templateTypeRepository;

	@Autowired
	private VariableTemplateRepository variableTemplateRepository;

	@Autowired
	private SurveyRepository surveyRepository;

	@Autowired
	private BatteryRepository batteryRepository;

	@Autowired
	private TemplateProcessorService templateProcessorService;

	@SuppressWarnings("serial")
	private static List<TemplateType> surveyTemplates = new ArrayList<TemplateType>() {
		{
			add(TemplateType.CPRS_ENTRY);
			add(TemplateType.VET_SUMMARY_ENTRY);
			add(TemplateType.VISTA_QA);

		}
	};

	@SuppressWarnings("serial")
	private static List<TemplateType> batteryTemplates = new ArrayList<TemplateType>() {
		{
			add(TemplateType.CPRS_HEADER);
			add(TemplateType.CPRS_FOOTER);
			add(TemplateType.ASSESS_SCORE_TABLE);
			add(TemplateType.ASSESS_CONCLUSION);
			add(TemplateType.VET_SUMMARY_HEADER);
			add(TemplateType.VET_SUMMARY_FOOTER);
		}
	};

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteTemplate(Integer templateId) {
		Template template = templateRepository.findOne(templateId);

		if (template == null) {
			throw new IllegalArgumentException();
		}

		if (surveyTemplates.contains(TemplateConstants.typeForId(template
				.getTemplateType().getTemplateTypeId()))) {
			// need to remove this template from associated survey
			List<Survey> surveys = surveyRepository
					.findByTemplateId(templateId);

			if (surveys != null && surveys.size() > 0) {
				for (Survey survey : surveys) {
					survey.getTemplates().remove(template);
					surveyRepository.update(survey);
				}
			}
		} else {
			// need to remove this template from associated battery

			// find the survey or battery
			List<Battery> batteries = batteryRepository
					.findByTemplateId(templateId);

			if (batteries != null && batteries.size() > 0) {
				for (Battery battery : batteries) {
					battery.getTemplates().remove(template);
					batteryRepository.update(battery);
				}
			}
		}

		templateRepository.deleteById(templateId);
	}

	@Override
	@Transactional(readOnly = true)
	public TemplateDTO getTemplate(Integer templateId) {
		Template template = templateRepository.findOne(templateId);

		if (template == null) {
			return null;
		} else {
			return TemplateTransformer.copyToTemplateDTO(template, null);
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public TemplateDTO updateTemplate(TemplateDTO templateDTO) {

		Template template = templateRepository.findOne(templateDTO
				.getTemplateId());
		if (template == null) {
			throw new IllegalArgumentException("Could not find template");
		}
		TemplateTransformer.copyToTemplate(templateDTO, template);
		templateRepository.update(template);
		return TemplateTransformer.copyToTemplateDTO(template, null);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public TemplateDTO createTemplate(TemplateDTO templateDTO,
			Integer parentId, boolean isSurvey) {
		Template template = TemplateTransformer.copyToTemplate(templateDTO,
				null);

		template.setTemplateType(templateTypeRepository.findOne(templateDTO
				.getTemplateTypeId()));

		if (parentId == null) {
			templateRepository.create(template);
		} else {
			if (isSurvey) {
				Survey survey = surveyRepository.findOne(parentId);
				Set<Template> templateSet = survey.getTemplates();
				survey.setTemplates(addTemplateToSet(templateSet, template,
						surveyTemplates));
				surveyRepository.update(survey);
			} else {
				Battery battery = batteryRepository.findOne(parentId);
				Set<Template> templateSet = battery.getTemplates();
				battery.setTemplates(addTemplateToSet(templateSet, template,
						batteryTemplates));
				batteryRepository.update(battery);
			}
		}

		return TemplateTransformer.copyToTemplateDTO(template, null);

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void addVariableTemplate(Integer templateId,
			Integer variableTemplateId) {
		Template template = templateRepository.findOne(templateId);
		VariableTemplate variableTemplate = variableTemplateRepository
				.findOne(variableTemplateId);
		template.getVariableTemplateList().add(variableTemplate);
		templateRepository.update(template);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void addVariableTemplates(Integer templateId,
			List<Integer> variableTemplateIds) {
		Template template = templateRepository.findOne(templateId);
		List<VariableTemplate> variableTemplates = variableTemplateRepository
				.findByIds(variableTemplateIds);
		template.getVariableTemplateList().addAll(variableTemplates);
		templateRepository.update(template);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void removeVariableTemplateFromTemplate(Integer templateId,
			Integer variableTemplateId) {
		Template template = templateRepository.findOne(templateId);
		VariableTemplate variableTemplate = variableTemplateRepository
				.findOne(variableTemplateId);
		template.getVariableTemplateList().remove(variableTemplate);
		templateRepository.update(template);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void removeVariableTemplatesFromTemplate(Integer templateId,
			List<Integer> variableTemplateIds) {
		Template template = templateRepository.findOne(templateId);
		List<VariableTemplate> variableTemplates = variableTemplateRepository
				.findByIds(variableTemplateIds);
		template.getVariableTemplateList().removeAll(variableTemplates);
		templateRepository.update(template);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public void setVariableTemplatesToTemplate(Integer templateId,
			List<Integer> variableTemplateIds) {
		Template template = templateRepository.findOne(templateId);
		List<VariableTemplate> variableTemplates = variableTemplateRepository
				.findByIds(variableTemplateIds);
		template.setVariableTemplateList(variableTemplates);
		templateRepository.update(template);
	}

	/**
	 * 
	 * Ensure the uniqueness of the template in either survey or battery
	 * 
	 * @param templateSet
	 * @param template
	 * @param uniquTemplateTypes
	 * @return
	 */
	private Set<Template> addTemplateToSet(Set<Template> templateSet,
			Template template, List<TemplateType> uniquTemplateTypes) {
		if (templateSet == null) {
			templateSet = new HashSet<Template>();
			templateSet.add(template);
		} else {
			// first we make sure
			boolean needsToBeUnique = false;

			for (TemplateType tt : uniquTemplateTypes) {
				if (tt.getId() == template.getTemplateType()
						.getTemplateTypeId()) {
					needsToBeUnique = true;
					break;
				}
			}

			if (needsToBeUnique) {
				for (Template t : templateSet) {
					if (t.getTemplateType().getTemplateTypeId() == template
							.getTemplateType().getTemplateTypeId()) {
						templateSet.remove(t);
						break;
					}
				}
			}

			templateSet.add(template);
		}

		return templateSet;
	}

	@Override
	public TemplateDTO getTemplateBySurveyAndTemplateType(Integer surveyId,
			Integer templateTypeId) {

		Template t = templateRepository.getTemplateByIdAndTemplateType(
				surveyId, templateTypeId);
		TemplateDTO dto = null;
		if (t != null) {
			dto = new TemplateDTO();
			dto.setTemplateId(t.getTemplateId());
			dto.setName(t.getName());
			dto.setDateCreated(t.getDateCreated());
			dto.setDescription(t.getDescription());
			dto.setGraphical(t.getIsGraphical());
			dto.setTemplateFile(t.getTemplateFile());
			dto.setTemplateType(templateTypeId);
		}
		return dto;
	}

	@Override
	public TemplateFileDTO getTemplateFileAsTree(Integer templateId) {
		Template t = templateRepository.findOne(templateId);

		if (t == null)
			return null;

		TemplateFileDTO dto = new TemplateFileDTO();

		dto.setTemplateId(templateId);
		dto.setIsGraphical(t.getIsGraphical());

		TemplateTypeDTO ttDTO = new TemplateTypeDTO();
		dto.setTemplateType(ttDTO);
		ttDTO.setName(t.getTemplateType().getName());
		ttDTO.setId(t.getTemplateType().getTemplateTypeId());
		ttDTO.setDescription(t.getTemplateType().getDescription());

		String templateFile = t.getTemplateFile();
		templateFile = templateFile.replace("${NBSP}", "&nbsp;")
				.replace("${LINE_BREAK}","<br/>")
				.replace("<#include \"clinicalnotefunctions\">", "");

		try {
			freemarker.template.Template fmTemplate = templateProcessorService
					.getTemplate(templateId, templateFile);
			for (int i = 0; i < fmTemplate.getRootTreeNode().getChildCount(); i++) {
				TemplateElementNodeDTO nod = nodeIterate(
						((TemplateElement) fmTemplate.getRootTreeNode()
								.getChildAt(i)), null);
				if (nod == null)
					continue;

			//TODO:	dto.getNodes().add(nod);
			}

		} catch (IOException e) {
			return null;
		}
		return dto;
	}

	private Properties parseMetaData() {
		String[] data = metaStr.replace("<#--", "").replace("-->", "").trim()
				.split(",");

		Properties p = new Properties();
		for (int i = 0; i < data.length; i++) {
			String dat[] = data[i].split("=");
			p.put(dat[0], dat[1]);
		}

		return p;

	}

	private String metaStr = null;

	private TemplateElementNodeDTO nodeIterate(TemplateElement node,
			List<Long> templateVariables) {
		TemplateElementNodeDTO nodeDTO = new TemplateElementNodeDTO();

		nodeDTO.setType(node.getClass().getSimpleName());

		String content = node.getCanonicalForm();

		if (!node.isLeaf()) {
			try {
				// nodeDTO.setContent(content.substring(0,
				// content.indexOf(((TemplateElement)node.getChildNodes().get(0)).getCanonicalForm())));
				if (nodeDTO.getType().equals("IfBlock")) {
					nodeDTO.setType("ConditionalBlock");
					// nodeDTO.setContent(content.substring(4,
					// content.indexOf(")>")+1));
				} else if (nodeDTO.getType().equals("ConditionalBlock")) {

					nodeDTO.setContent(content.substring(0, content
							.indexOf(((TemplateElement) node.getChildAt(0))
									.getCanonicalForm())));

					if (content.equals("<#else>")) {
						nodeDTO.setType("elseBlock");
					} else if (content.startsWith("<#if")) {
						nodeDTO.setType("ifBlock");
					} else {
						nodeDTO.setType("elseIfBlock");
					}
				} else
					nodeDTO.setContent(content);
				if (metaStr != null) {
					Properties p = parseMetaData();
					nodeDTO.setTitle(p.getProperty("TITLE"));
					nodeDTO.setSection(p.getProperty("SECTION"));
					metaStr = null;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			for (int i = 0; i < node.getChildCount(); i++) {
				TemplateElement childTemplateElement = (TemplateElement)node
						.getChildAt(i);
				TemplateElementNodeDTO n = nodeIterate(childTemplateElement,
						templateVariables);
				if (n != null)
					nodeDTO.getChildren().add(n);
			}
		} else {
			if (nodeDTO.getType().equals("Comment")) {
				metaStr = content;
				return null;
			}

			if (metaStr != null) {
				Properties p = parseMetaData();
				nodeDTO.setTitle(p.getProperty("TITLE"));
				nodeDTO.setSection(p.getProperty("SECTION"));
				metaStr = null;
			}

			if (nodeDTO.getType().equals("TextBlock"))
			{
				content = content.replace("${LINE_BREAK}", "<br/>");
			}
			nodeDTO.setContent(content);
		}

		return nodeDTO;
	}

}
