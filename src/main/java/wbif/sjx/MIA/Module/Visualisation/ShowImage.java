package wbif.sjx.MIA.Module.Visualisation;

import wbif.sjx.MIA.Module.Module;
import wbif.sjx.MIA.Module.ModuleCollection;
import wbif.sjx.MIA.Module.PackageNames;
import wbif.sjx.MIA.Module.Category;
import wbif.sjx.MIA.Module.Categories;
import wbif.sjx.MIA.Object.Image;
import wbif.sjx.MIA.Object.Status;
import wbif.sjx.MIA.Object.Workspace;
import wbif.sjx.MIA.Object.Parameters.BooleanP;
import wbif.sjx.MIA.Object.Parameters.ChoiceP;
import wbif.sjx.MIA.Object.Parameters.InputImageP;
import wbif.sjx.MIA.Object.Parameters.ParameterCollection;
import wbif.sjx.MIA.Object.Parameters.SeparatorP;
import wbif.sjx.MIA.Object.References.Collections.ImageMeasurementRefCollection;
import wbif.sjx.MIA.Object.References.Collections.MetadataRefCollection;
import wbif.sjx.MIA.Object.References.Collections.ObjMeasurementRefCollection;
import wbif.sjx.MIA.Object.References.Collections.ParentChildRefCollection;
import wbif.sjx.MIA.Object.References.Collections.PartnerRefCollection;
import wbif.sjx.common.Object.Metadata;

/**
 * Created by sc13967 on 03/05/2017.
 */
public class ShowImage extends Module {
    public static final String INPUT_SEPARATOR = "Image input";
    public final static String DISPLAY_IMAGE = "Display image";

    public static final String DISPLAY_SEPARATOR = "Display controls";
    public static final String TITLE_MODE = "Title mode";
    public static final String QUICK_NORMALISATION = "Quick normalisation";
    public static final String CHANNEL_MODE = "Channel mode";


    public interface TitleModes {
        String FILE_NAME = "Filename";
        String IMAGE_NAME = "Image name";
        String IMAGE_AND_FILE_NAME = "Image and filename";

        String[] ALL = new String[]{FILE_NAME,IMAGE_NAME,IMAGE_AND_FILE_NAME};

    }

    public interface ChannelModes {
        String COLOUR = "Colour (separate channels)";
        String COMPOSITE = "Composite";

        String[] ALL = new String[]{COLOUR,COMPOSITE};

    }


    public ShowImage(ModuleCollection modules) {
        super("Show image",modules);

        // This module likely wants to have this enabled (otherwise it does nothing)
        showOutput = true;

    }


    @Override
    public String getPackageName() {
        return PackageNames.VISUALISATION;
    }

    @Override
    public Category getCategory() {
        return Categories.VISUALISATION;
    }

    @Override
    public String getDescription() {
        return "Display any image held in the current workspace.  " +
                "Images are displayed using the standard ImageJ image window, so can be accessed/manipulated by any ImageJ/Fiji feature.  " +
                "Displayed images are duplicates of the image stored in the workspace, so modification of a displayed image won't alter the original.";
    }

    @Override
    public Status process(Workspace workspace) {
        String imageName = parameters.getValue(DISPLAY_IMAGE);
        Image image = workspace.getImage(imageName);
        String titleMode = parameters.getValue(TITLE_MODE);
        boolean normalisation = parameters.getValue(QUICK_NORMALISATION);
        String channelMode = parameters.getValue(CHANNEL_MODE);

        boolean composite = channelMode.equals(ChannelModes.COMPOSITE);

        Metadata metadata = workspace.getMetadata();
        String title = "";
        switch (titleMode) {
            case TitleModes.FILE_NAME:
                title = metadata.getFilename()+"."+metadata.getExt();
                break;
            case TitleModes.IMAGE_NAME:
                title = imageName;
                break;
            case TitleModes.IMAGE_AND_FILE_NAME:
                title = metadata.getFilename()+"."+metadata.getExt()+"_"+imageName;
                break;
        }

        if (showOutput) image.showImage(title,null,normalisation,composite);

        return Status.PASS;

    }

    @Override
    protected void initialiseParameters() {
        parameters.add(new SeparatorP(INPUT_SEPARATOR,this));
        parameters.add(new InputImageP(DISPLAY_IMAGE, this, "", "Image to display."));

        parameters.add(new SeparatorP(DISPLAY_SEPARATOR,this));
        parameters.add(new ChoiceP(TITLE_MODE,this,TitleModes.IMAGE_NAME,TitleModes.ALL, "Select what title the image window should have.<br>" +
                "<br>- \""+TitleModes.IMAGE_NAME+"\" Set the image window title to the name of the image.<br>" +
                "<br>- \""+TitleModes.FILE_NAME+"\" Set the image window title to the filename of the root file for this workspace (i.e. the file set in \"Input control\".<br>" +
                "<br>- \""+TitleModes.IMAGE_AND_FILE_NAME+"\" Set the image window title to a composite of the filename of the root file for this workspace and the name of the image."));
        parameters.add(new BooleanP(QUICK_NORMALISATION,this,true,"Before displaying the image, apply quick normalisation to improve contrast.  The minimum and maximum displayed intensities are simply set to the minimum and maximum pixel intensities contained within the image stack."));
        parameters.add(new ChoiceP(CHANNEL_MODE,this,ChannelModes.COMPOSITE,ChannelModes.ALL,"Select whether multi-channel images should be displayed as composites (show all channels overlaid) or individually (the displayed channel is controlled by the \"C\" slider at the bottom of the image window)."));

    }

    @Override
    public ParameterCollection updateAndGetParameters() {
        return parameters;
    }

    @Override
    public ImageMeasurementRefCollection updateAndGetImageMeasurementRefs() {
        return null;
    }

    @Override
    public ObjMeasurementRefCollection updateAndGetObjectMeasurementRefs() {
        return null;
    }

    @Override
    public MetadataRefCollection updateAndGetMetadataReferences() {
        return null;
    }

    @Override
    public ParentChildRefCollection updateAndGetParentChildRefs() {
        return null;
    }

    @Override
    public PartnerRefCollection updateAndGetPartnerRefs() {
        return null;
    }

    @Override
    public boolean verify() {
        return true;
    }
}
