// AUTO-GENERATED FILE. DO NOT EDIT.
package baritone.api;

@SuppressWarnings({"all", "unchecked"})
public interface IBaritone {
    default baritone.api.process.IBuilderProcess getBuilderProcess() {
        return (baritone.api.process.IBuilderProcess) com.cope.addonparser.stubs.StubSupport.defaultValue(baritone.api.process.IBuilderProcess.class);
    }

    default baritone.api.command.manager.ICommandManager getCommandManager() {
        return (baritone.api.command.manager.ICommandManager) com.cope.addonparser.stubs.StubSupport.defaultValue(baritone.api.command.manager.ICommandManager.class);
    }

    default baritone.api.process.ICustomGoalProcess getCustomGoalProcess() {
        return (baritone.api.process.ICustomGoalProcess) com.cope.addonparser.stubs.StubSupport.defaultValue(baritone.api.process.ICustomGoalProcess.class);
    }

    default baritone.api.process.IFarmProcess getFarmProcess() {
        return (baritone.api.process.IFarmProcess) com.cope.addonparser.stubs.StubSupport.defaultValue(baritone.api.process.IFarmProcess.class);
    }

    default baritone.api.process.IMineProcess getMineProcess() {
        return (baritone.api.process.IMineProcess) com.cope.addonparser.stubs.StubSupport.defaultValue(baritone.api.process.IMineProcess.class);
    }

    default baritone.api.behavior.IPathingBehavior getPathingBehavior() {
        return (baritone.api.behavior.IPathingBehavior) com.cope.addonparser.stubs.StubSupport.defaultValue(baritone.api.behavior.IPathingBehavior.class);
    }

    default baritone.api.pathing.calc.IPathingControlManager getPathingControlManager() {
        return (baritone.api.pathing.calc.IPathingControlManager) com.cope.addonparser.stubs.StubSupport.defaultValue(baritone.api.pathing.calc.IPathingControlManager.class);
    }

    default baritone.api.selection.ISelectionManager getSelectionManager() {
        return (baritone.api.selection.ISelectionManager) com.cope.addonparser.stubs.StubSupport.defaultValue(baritone.api.selection.ISelectionManager.class);
    }

}
