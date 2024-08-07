package io.onedev.server.model.support.issue.field.spec;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import io.onedev.server.OneDev;
import io.onedev.server.buildspecmodel.inputspec.workingperiodinput.WorkingPeriodInput;
import io.onedev.server.buildspecmodel.inputspec.workingperiodinput.defaultvalueprovider.DefaultValueProvider;
import io.onedev.server.entitymanager.SettingManager;
import io.onedev.server.annotation.Editable;

@Editable(order=700, name=FieldSpec.WORKING_PERIOD)
public class WorkingPeriodField extends FieldSpec {

	private static final long serialVersionUID = 1L;

	private DefaultValueProvider defaultValueProvider;
	
	@Editable(order=1000, name="Default Value", placeholder="No default value")
	@Valid
	public DefaultValueProvider getDefaultValueProvider() {
		return defaultValueProvider;
	}

	public void setDefaultValueProvider(DefaultValueProvider defaultValueProvider) {
		this.defaultValueProvider = defaultValueProvider;
	}

	@Override
	public String getPropertyDef(Map<String, Integer> indexes) {
		return WorkingPeriodInput.getPropertyDef(this, indexes, defaultValueProvider);
	}

	@Override
	public Object convertToObject(List<String> strings) {
		return WorkingPeriodInput.convertToObject(strings);
	}

	@Editable
	@Override
	public boolean isAllowMultiple() {
		return false;
	}

	@Override
	public List<String> convertToStrings(Object value) {
		return WorkingPeriodInput.convertToStrings(value);
	}

	@Override
	public long getOrdinal(String fieldValue) {
		if (fieldValue != null) {
			var timeTrackingSetting = OneDev.getInstance(SettingManager.class).getIssueSetting().getTimeTrackingSetting();
			return timeTrackingSetting.parseWorkingPeriod(fieldValue);
		} else {
			return super.getOrdinal(fieldValue);
		}
	}

	@Override
	protected void runScripts() {
		if (getDefaultValueProvider() != null)
			getDefaultValueProvider().getDefaultValue();
	}

}
