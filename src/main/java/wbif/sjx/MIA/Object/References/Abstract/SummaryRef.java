package wbif.sjx.MIA.Object.References.Abstract;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public abstract class SummaryRef extends ExportableRef {
    private boolean exportMean = true;
    private boolean exportMin = true;
    private boolean exportMax = true;
    private boolean exportSum = true;
    private boolean exportStd = true;

    public SummaryRef(String name) {
        super(name);
    }

    public SummaryRef(NamedNodeMap attributes) {
        super(attributes);
    }

    public boolean isExportMean() {
        return exportMean;
    }

    public void setExportMean(boolean exportMean) {
        this.exportMean = exportMean;

    }

    public boolean isExportMin() {
        return exportMin;
    }

    public void setExportMin(boolean exportMin) {
        this.exportMin = exportMin;

    }

    public boolean isExportMax() {
        return exportMax;
    }

    public void setExportMax(boolean exportMax) {
        this.exportMax = exportMax;

    }

    public boolean isExportSum() {
        return exportSum;
    }

    public void setExportSum(boolean exportSum) {
        this.exportSum = exportSum;

    }

    public boolean isExportStd() {
        return exportStd;
    }

    public void setExportStd(boolean exportStd) {
        this.exportStd = exportStd;

    }

    @Override
    public void setAllExport(boolean export) {
        super.setAllExport(export);

        exportMax = export;
        exportMean = export;
        exportMin = export;
        exportStd = export;
        exportSum = export;

    }

    @Override
    public void appendXMLAttributes(Element element) {
        super.appendXMLAttributes(element);

        element.setAttribute("EXPORT_MEAN",String.valueOf(exportMean));
        element.setAttribute("EXPORT_MIN",String.valueOf(exportMin));
        element.setAttribute("EXPORT_MAX",String.valueOf(exportMax));
        element.setAttribute("EXPORT_SUM",String.valueOf(exportSum));
        element.setAttribute("EXPORT_STD",String.valueOf(exportStd));

    }

    @Override
    public void setAttributesFromXML(NamedNodeMap attributes) {
        super.setAttributesFromXML(attributes);

        if (attributes.getNamedItem("EXPORT_MEAN") != null) {
            this.exportMean = Boolean.parseBoolean(attributes.getNamedItem("EXPORT_MEAN").getNodeValue());
        }

        if (attributes.getNamedItem("EXPORT_MIN") != null) {
            this.exportMin = Boolean.parseBoolean(attributes.getNamedItem("EXPORT_MIN").getNodeValue());
        }

        if (attributes.getNamedItem("EXPORT_MAX") != null) {
            this.exportMax = Boolean.parseBoolean(attributes.getNamedItem("EXPORT_MAX").getNodeValue());
        }

        if (attributes.getNamedItem("EXPORT_SUM") != null) {
            this.exportSum = Boolean.parseBoolean(attributes.getNamedItem("EXPORT_SUM").getNodeValue());
        }

        if (attributes.getNamedItem("EXPORT_STD") != null) {
            this.exportStd = Boolean.parseBoolean(attributes.getNamedItem("EXPORT_STD").getNodeValue());
        }
    }
}
