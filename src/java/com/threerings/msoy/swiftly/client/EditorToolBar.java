package com.threerings.msoy.swiftly.client;

import javax.swing.JButton;
import javax.swing.JToolBar;

import com.threerings.msoy.swiftly.data.SwiftlyCodes;
import com.threerings.msoy.swiftly.util.SwiftlyContext;

public class EditorToolBar extends JToolBar
{
    public EditorToolBar (ProjectRoomController ctrl, SwiftlyContext ctx, SwiftlyEditor editor)
    {
        _ctrl = ctrl;
        _ctx = ctx;
        _editor = editor;
        setupToolbar();
    }

    public void updateEditorActions(SwiftlyTextPane textPane)
    {
        _saveButton.setAction(textPane.getSaveAction());
        _undoButton.setAction(textPane.getUndoAction());
        _redoButton.setAction(textPane.getRedoAction());
    }

    protected void setupToolbar ()
    {
        // TODO add mini icons for these + tooltips with keyboard shortcuts
        _saveButton = new JButton(_ctx.xlate(SwiftlyCodes.SWIFTLY_MSGS, "m.action_save"));
        add(_saveButton);
        add(_editor.createCloseCurrentTabAction());

        addSeparator();
        add(new JButton(_ctrl.buildAction));
        add(new JButton("Play"));

        addSeparator();
        // These actions get set by updateEditorActions()
        _undoButton = new JButton(_ctx.xlate(SwiftlyCodes.SWIFTLY_MSGS, "m.action_undo"));
        add(_undoButton);
        _redoButton = new JButton(_ctx.xlate(SwiftlyCodes.SWIFTLY_MSGS, "m.action_redo"));
        add(_redoButton);

        setFloatable(false);
    }

    protected ProjectRoomController _ctrl;
    protected SwiftlyContext _ctx;
    protected SwiftlyEditor _editor;

    protected JButton _saveButton;
    protected JButton _undoButton;
    protected JButton _redoButton;
}
