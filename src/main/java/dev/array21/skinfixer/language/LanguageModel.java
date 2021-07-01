package dev.array21.skinfixer.language;

import dev.array21.classvalidator.annotations.Required;

public class LanguageModel {

	// SkinChangeHandler
	@Required
	public String skinFetching;
	@Required
	//Variable: %ERROR%
	public String skinApplyFailed;
	@Required
	public String skinApplying;
	@Required
	public String skinApplied;
	
	//General commands
	@Required
	public String commandPlayerOnly;
	@Required
	public String commandNoPermission;
	
	//GetCodeCommandExecutor
	@Required
	public String getCodeUrlRequired;
	@Required
	//Variable: %CODE%
	public String getCodeSkinAdded;
	
	//SetSkinCommandExecutor
	@Required
	public String setSkinCodeRequired;
	@Required
	public String setSkinCodeNotANumber;
	@Required
	public String setSkinCodeUnknown;
	
	//SkinFixerCommandExecutor
	@Required
	public String skinFixerNoOptionProvided;
	@Required
	public String skinFixerSetSkinHelp;
	@Required
	public String skinFixerGetCodeHelp;
	@Required
	public String skinFixerShowHelp;
	@Required
	public String skinFixerVersionHelp;
	@Required
	//Variable: %VERSION%
	public String skinFixerVersion;
	
	//MessageReceivedEventListener
	@Required
	//Variable: %CODE%
	public String discordSetSkin;
}