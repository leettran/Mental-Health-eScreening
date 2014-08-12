package gov.va.escreening.service.export;

import gov.va.escreening.entity.MeasureAnswer;
import gov.va.escreening.entity.SurveyMeasureResponse;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

public interface DataExtractor {
	Map<String, String> apply(SurveyMeasureResponse smr);
}

@Component("smrExportName")
class ExportName implements DataExtractor {
	@Override
	public Map<String, String> apply(SurveyMeasureResponse smr) {
		MeasureAnswer ma = smr.getMeasureAnswer();

		// data export column we could be interested in
		String xportName = ma.getExportName();

		// user entered data
		String textValue = smr.getTextValue();
		Long numberValue = smr.getNumberValue();

		// marker to identify that this measure answer record was selected
		Boolean boolValue = smr.getBooleanValue();

		// both cannot be null, if it is then skip this
		// if (textValue == null && boolValue == null) {
		// continue;
		// }

		String exportableResponse = null;
		if (textValue != null && !textValue.trim().isEmpty()) {
			exportableResponse = textValue;
		} else if (numberValue != null) {
			exportableResponse = String.valueOf(numberValue);
		} else if (boolValue != null && boolValue) {
			exportableResponse = ma.getCalculationValue();
			if (exportableResponse == null) {
				exportableResponse = "1";
			}
		}

		if (exportableResponse != null) {
			Map<String, String> m = new HashMap<String, String>();
			m.put("exportName", xportName);
			m.put("exportableResponse", exportableResponse);
			return m;
		} else {
			return null;
		}
	}
}

@Component("smrExportOtherName")
class ExportOtherName implements DataExtractor {
	@Override
	public Map<String, String> apply(SurveyMeasureResponse smr) {
		MeasureAnswer ma = smr.getMeasureAnswer();

		// data export column we coudl be interested in
		String xportName = ma.getOtherExportName();
		String otherValue = smr.getOtherValue();

		String exportableResponse = null;
		if ("other".equals(ma.getAnswerType()) && otherValue != null && !otherValue.trim().isEmpty()) {
			exportableResponse = otherValue;
		}

		if (exportableResponse != null) {
			Map<String, String> m = new HashMap<String, String>();
			m.put("exportName", xportName);
			m.put("exportableResponse", exportableResponse);
			return m;
		} else {
			return null;
		}
	}
}