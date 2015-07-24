package com.airbusds.idea.gui;


public interface TableValidator {

	public ValidationResult validateTableData(Object[][] data, String[] colNames);

	public static class ValidationResult {
		public String message;
		public boolean isError;
		public int errRow;
		public int errCol;
	}

}
