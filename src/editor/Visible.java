package editor;

import javax.media.j3d.BranchGroup;

public interface Visible {
	public BranchGroup getFaces();
	public BranchGroup getEdges();
	public BranchGroup getText();
	public void select();
	public void setSelected(boolean v);
	public boolean isSelected();
}
