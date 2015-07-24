package com.airbusds.idea.model.validation;

import com.airbusds.idea.model.InputFile;

public interface DocumentValidator {
	
	public ValidationResults validate(InputFile document);
	
}
