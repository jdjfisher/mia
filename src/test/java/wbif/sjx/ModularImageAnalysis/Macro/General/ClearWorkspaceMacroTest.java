package wbif.sjx.ModularImageAnalysis.Macro.General;

import wbif.sjx.ModularImageAnalysis.Macro.MacroOperationTest;

import static org.junit.Assert.*;

public class ClearWorkspaceMacroTest extends MacroOperationTest {
    @Override
    public void testGetName() {
        assertNotNull(new ClearWorkspaceMacro(null).getName());
    }

    @Override
    public void testGetArgumentsDescription() {
        assertNotNull(new ClearWorkspaceMacro(null).getArgumentsDescription());
    }

    @Override
    public void testGetDescription() {
        assertNotNull(new ClearWorkspaceMacro(null).getDescription());
    }
}