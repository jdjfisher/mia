package io.github.mianalysis.mia.Object.Refs.Abstract;

import org.apache.poi.ss.usermodel.Sheet;
import io.github.mianalysis.mia.Object.Workspace;

import java.util.LinkedHashMap;

public interface SpreadsheetWriter {
    public void addSummaryXLSX(Sheet sheet, LinkedHashMap<Integer, Workspace> workspaces);
}
