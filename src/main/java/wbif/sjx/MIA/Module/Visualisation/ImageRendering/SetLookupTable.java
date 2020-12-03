package wbif.sjx.MIA.Module.Visualisation.ImageRendering;

import java.awt.Color;

import ij.CompositeImage;
import ij.process.LUT;
import wbif.sjx.MIA.Module.Module;
import wbif.sjx.MIA.Module.ModuleCollection;
import wbif.sjx.MIA.Module.PackageNames;
import wbif.sjx.MIA.Module.Category;
import wbif.sjx.MIA.Module.Categories;
import wbif.sjx.MIA.Object.Status;
import wbif.sjx.MIA.Object.Image;
import wbif.sjx.MIA.Object.Workspace;
import wbif.sjx.MIA.Object.Parameters.ChoiceP;
import wbif.sjx.MIA.Object.Parameters.InputImageP;
import wbif.sjx.MIA.Object.Parameters.SeparatorP;
import wbif.sjx.MIA.Object.Parameters.ParameterCollection;
import wbif.sjx.MIA.Object.Parameters.Text.IntegerP;
import wbif.sjx.MIA.Object.References.Collections.ImageMeasurementRefCollection;
import wbif.sjx.MIA.Object.References.Collections.MetadataRefCollection;
import wbif.sjx.MIA.Object.References.Collections.ObjMeasurementRefCollection;
import wbif.sjx.MIA.Object.References.Collections.ParentChildRefCollection;
import wbif.sjx.MIA.Object.References.Collections.PartnerRefCollection;
import wbif.sjx.common.Object.LUTs;

public class SetLookupTable extends Module {
    public static final String INPUT_SEPARATOR = "Image input";
    public static final String INPUT_IMAGE = "Input image";
    
    public static final String LUT_SEPARATOR = "Lookup table selection";
    public static final String CHANNEL_MODE = "Channel mode";
    public static final String CHANNEL = "Channel";
    public static final String LOOKUP_TABLE = "Lookup table";
    public static final String DISPLAY_MODE = "Display mode";
    
    public SetLookupTable(ModuleCollection modules) {
        super("Set lookup table", modules);
    }
    
    public interface ChannelModes {
        String ALL_CHANNELS = "All channels";
        String SPECIFIC_CHANNELS = "Specific channels";
        
        String[] ALL = new String[] { ALL_CHANNELS, SPECIFIC_CHANNELS };
        
    }
    
    public interface DisplayModes {
        String FULL_RANGE = "Full range";
        String SET_ZERO_TO_BLACK = "Set zero to black";
        
        String[] ALL = new String[] { FULL_RANGE, SET_ZERO_TO_BLACK };
        
    }
    
    public interface LookupTables {
        String GREY = "Grey";
        String RED = "Red";
        String GREEN = "Green";
        String BLUE = "Blue";
        String CYAN = "Cyan";
        String MAGENTA = "Magenta";
        String YELLOW = "Yellow";
        String FIRE = "Fire";
        String ICE = "Ice";
        String JET = "Jet";
        String PHYSICS = "Physics";
        String SPECTRUM = "Spectrum";
        String THERMAL = "Thermal";
        String RANDOM = "Random";
        
        String[] ALL = new String[] { GREY, RED, GREEN, BLUE, CYAN, MAGENTA, YELLOW, FIRE, ICE, JET, PHYSICS, SPECTRUM,
            THERMAL, RANDOM };
            
        }
        
        public static LUT getLUT(String lookupTableName) {
            switch (lookupTableName) {
                case LookupTables.GREY:
                default:
                return LUT.createLutFromColor(Color.WHITE);
                case LookupTables.RED:
                return LUT.createLutFromColor(Color.RED);
                case LookupTables.GREEN:
                return LUT.createLutFromColor(Color.GREEN);
                case LookupTables.BLUE:
                return LUT.createLutFromColor(Color.BLUE);
                case LookupTables.CYAN:
                return LUT.createLutFromColor(Color.CYAN);
                case LookupTables.MAGENTA:
                return LUT.createLutFromColor(Color.MAGENTA);
                case LookupTables.YELLOW:
                return LUT.createLutFromColor(Color.YELLOW);
                case LookupTables.FIRE:
                return LUTs.BlackFire();
                case LookupTables.ICE:
                return LUTs.Ice();
                case LookupTables.JET:
                return LUTs.Jet();
                case LookupTables.PHYSICS:
                return LUTs.Physics();
                case LookupTables.SPECTRUM:
                return LUTs.Spectrum();
                case LookupTables.THERMAL:
                return LUTs.Thermal();
                case LookupTables.RANDOM:
                return LUTs.Random(true);
            }
        }
        
        public static LUT setZeroToBlack(LUT lut) {
            byte[] reds = new byte[lut.getMapSize()];
            byte[] greens = new byte[lut.getMapSize()];
            byte[] blues = new byte[lut.getMapSize()];
            
            lut.getReds(reds);
            lut.getGreens(greens);
            lut.getBlues(blues);
            
            reds[0] = 0;
            greens[0] = 0;
            blues[0] = 0;
            
            return new LUT(reds, greens, blues);
            
        }
        
        public static void setLUT(Image inputImage, LUT lut, String channelMode, int channel) {
            // Single channel images shouldn't be set to composite
            if (inputImage.getImagePlus().getNChannels() == 1) {
                inputImage.getImagePlus().setLut(lut);
                return;
            }
            
            switch (channelMode) {
                case ChannelModes.ALL_CHANNELS:
                for (int c = 1; c <= inputImage.getImagePlus().getNChannels(); c++) {
                    ((CompositeImage) inputImage.getImagePlus()).setChannelLut(lut, c);
                }
                break;
                
                case ChannelModes.SPECIFIC_CHANNELS:
                ((CompositeImage) inputImage.getImagePlus()).setChannelLut(lut, channel);
                break;
            }
        }
        
        @Override
        public String getPackageName() {
            return PackageNames.VISUALISATION_IMAGE_RENDERING;
        }


    @Override
    public Category getCategory() {
        return Categories.VISUALISATION_IMAGE_RENDERING;
    }
        
        @Override
        public String getDescription() {
            return "Set look-up table (LUT) for an image or a specific channel of an image.  The look-up table determines what colour ImageJ will render each intensity value of an image.";
        }
        
        @Override
        public Status process(Workspace workspace) {
            // Getting input image
            String inputImageName = parameters.getValue(INPUT_IMAGE);
            Image inputImage = workspace.getImages().get(inputImageName);
            
            // Getting parameters
            String lookupTableName = parameters.getValue(LOOKUP_TABLE);
            String channelMode = parameters.getValue(CHANNEL_MODE);
            int channel = parameters.getValue(CHANNEL);
            String displayMode = parameters.getValue(DISPLAY_MODE);
            
            // If this image doesn't exist, skip this module. This returns true, because
            // this isn't terminal for the analysis.
            if (inputImage == null)
            return Status.PASS;
            
            // If this image has fewer channels than the specified channel, skip the module
            // (but return true)
            if (channelMode.equals(ChannelModes.SPECIFIC_CHANNELS) && channel > inputImage.getImagePlus().getNChannels())
            return Status.PASS;
            
            LUT lut = getLUT(lookupTableName);
            
            switch (displayMode) {
                case DisplayModes.SET_ZERO_TO_BLACK:
                lut = setZeroToBlack(lut);
                break;
            }
            
            setLUT(inputImage, lut, channelMode, channel);
            inputImage.getImagePlus().updateChannelAndDraw();
            
            if (showOutput)
            inputImage.showImage(inputImageName, null, false, true);
            
            return Status.PASS;
            
        }
        
        @Override
        protected void initialiseParameters() {
            parameters.add(new SeparatorP(INPUT_SEPARATOR, this));
            parameters.add(new InputImageP(INPUT_IMAGE, this));
            
            parameters.add(new SeparatorP(LUT_SEPARATOR, this));
            parameters.add(new ChoiceP(CHANNEL_MODE, this, ChannelModes.ALL_CHANNELS, ChannelModes.ALL));
            parameters.add(new IntegerP(CHANNEL, this, 1));
            parameters.add(new ChoiceP(LOOKUP_TABLE, this, LookupTables.GREY, LookupTables.ALL));
            parameters.add(new ChoiceP(DISPLAY_MODE, this, DisplayModes.FULL_RANGE, DisplayModes.ALL));
            
            addParameterDescriptions();
            
        }
        
        @Override
        public ParameterCollection updateAndGetParameters() {
            ParameterCollection returnedParameters = new ParameterCollection();
            
            returnedParameters.add(parameters.getParameter(INPUT_SEPARATOR));
            returnedParameters.add(parameters.getParameter(INPUT_IMAGE));
            
            returnedParameters.add(parameters.getParameter(LUT_SEPARATOR));
            returnedParameters.add(parameters.getParameter(CHANNEL_MODE));
            
            switch ((String) parameters.getValue(CHANNEL_MODE)) {
                case ChannelModes.SPECIFIC_CHANNELS:
                returnedParameters.add(parameters.getParameter(CHANNEL));
                break;
            }
            
            returnedParameters.add(parameters.getParameter(LOOKUP_TABLE));
            returnedParameters.add(parameters.getParameter(DISPLAY_MODE));
            
            return returnedParameters;
            
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
        
        void addParameterDescriptions() {
            parameters.get(INPUT_IMAGE).setDescription("Image to set look-up table for.");
            
            parameters.get(CHANNEL_MODE)
            .setDescription("Control if the same look-up table is applied to all channels, or just one:<br>"
            
            + "<br>- \"" + ChannelModes.ALL_CHANNELS
            + "\" Apply the same look-up table to all channels of the input image.<br>"
            
            
            + "<br>- \"" + ChannelModes.SPECIFIC_CHANNELS
            + "\" Only apply the look-up table to the channel specified by the \""+CHANNEL+"\" parameter.  All other channels will remain unaffected.<br>");
            
            parameters.get(CHANNEL).setDescription("When in \""+ChannelModes.SPECIFIC_CHANNELS+"\" mode, this is the channel the look-up table will be applied to.  Channel numbering starts at 1.");
            
            parameters.get(LOOKUP_TABLE).setDescription("Look-up table to apply to the relevant channels.  Choices are: "+String.join(", ", LookupTables.ALL)+".");
            
            parameters.get(DISPLAY_MODE)
            .setDescription("Controls how the minimum value in the look-up table should be rendered:<br>"
            
            + "<br>- \"" + DisplayModes.FULL_RANGE
            + "\" Use the full colour range of the look-up table.  This is the default look-up table without modifications.<br>"
            
            + "<br>- \"" + DisplayModes.SET_ZERO_TO_BLACK
            + "\" Uses the standard look-up table, except for the lowest value, which is always set to black.  This is useful for cases where the background will be 0 or NaN and should be rendered as black.<br>"
            );
            
        }
    }
    