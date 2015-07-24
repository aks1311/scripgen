package com.airbusds.idea.gui.editor;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.airbusds.idea.gui.BlankPanel;
import com.airbusds.idea.gui.DocumentViewController;
import com.airbusds.idea.gui.EigenValuePlot;
import com.airbusds.idea.gui.ExecutionPlanListPanel;
import com.airbusds.idea.gui.FlutterPlot;
import com.airbusds.idea.gui.IDEAJobDetailsPanel;
import com.airbusds.idea.gui.IDEAProcessNodeCellRenderer;
import com.airbusds.idea.gui.IterationsPlot;
import com.airbusds.idea.gui.ParagraphView;
import com.airbusds.idea.gui.ParamFileViewer;
import com.airbusds.idea.gui.ParameterAnalysisResultsPanel;
import com.airbusds.idea.gui.ResultsPanel;
import com.airbusds.idea.job.IDEAQueue;
import com.airbusds.idea.model.AnalysisInfo;
import com.airbusds.idea.model.AnalysisStatus;
import com.airbusds.idea.model.ExecutionPlan;
import com.airbusds.idea.model.IDEAJob;
import com.airbusds.idea.model.InputFile;
import com.airbusds.idea.model.JobResult;
import com.airbusds.idea.model.Paragraph;
import com.airbusds.idea.model.XYPlot;

/**
 * 
 * Panel with forms to edit IDEA Analysis object <code>AnalysisInfo</code> info.
 * Has a navigation tree on the left to navigate various parts of the Analysis.
 * 
 * @author amit.singh
 *
 */
public class AnalysisEditor extends JPanel implements TreeSelectionListener {

	private static final long serialVersionUID = -3426163505340297511L;
	private static Logger log = LogManager.getLogger(AnalysisEditor.class.getName());
	
	Object currentPage;
	JSplitPane sp = null;
	DefaultTreeModel treeModel;
	JTree tree;
	DefaultMutableTreeNode top;
	AnalysisInfo info;
	DefaultMutableTreeNode execPlans;
	DefaultMutableTreeNode jobs;
	DefaultMutableTreeNode results;
	
	public AnalysisEditor(AnalysisInfo info){
		
		this.info = info;
		
    	//Create the content-pane-to-be.
        this.setOpaque(true);
        this.setLayout(new BorderLayout());

        //Create a left navigation.
        top = new DefaultMutableTreeNode("Navigation Tree");
        treeModel = new DefaultTreeModel(top);
        tree = new JTree(treeModel);
        tree.setRootVisible(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this);
        tree.setCellRenderer(new IDEAProcessNodeCellRenderer());
        
        createNodes(top);

        JScrollPane treePane = new JScrollPane(tree);
        JLabel lbl = new JLabel("  Analysis Explorer");
        treePane.setColumnHeaderView(lbl);

        BorderLayout gdl = new BorderLayout();
        JPanel jp = new JPanel(gdl);
        jp.add(treePane, BorderLayout.CENTER);
        
        sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        sp.setLeftComponent(treePane);
        
        if(info.getStatus() == null){
        	info.setStatus(AnalysisStatus.DRAFT);
        }
        
        log.debug("Analysis status is "+info.getStatus());
        if(info.getStatus().equals(AnalysisStatus.DRAFT) 
        		|| info.getStatus().equals(AnalysisStatus.DOCS_INVALID)
        		|| info.getStatus().equals(AnalysisStatus.DOCS_VALIDATED))
        	selectNode(info.getParamIn());
        
        this.add(sp, BorderLayout.CENTER);

	}
	
	public void editExecutionPlan(ExecutionPlan plan){
    	
    	DefaultMutableTreeNode planNode = findExecPlanNode(plan);
    	
    	TreePath path = new TreePath(planNode.getPath());
		tree.setSelectionPath(path);
		tree.makeVisible(path);
		treeModel.reload(execPlans);
    }
    
    public void deleteExecutionPlan(ExecutionPlan plan){
    	execPlans.remove(findExecPlanNode(plan));
    	treeModel.reload(execPlans);
    }
    
    public void showProgress(){
    	
    	long sessId = info.getIdeaSessionId();
    	if(sessId<=0)
    		return;
    	
    	IDEAQueue q = IDEAQueue.getInstance();
    	String batchMd = info.getAnalysisLocation()+File.separatorChar+"idea.jobs";
    	
    	try {
    		
    		final List<IDEAJob> jobs = q.getJobs(info.getIdeaSessionId(), batchMd);
			info.setJobs(jobs);
			
    		for (Iterator<IDEAJob> it = jobs.iterator(); it.hasNext();) {
				final IDEAJob ideaJob = it.next();
				ideaJob.addPropertyChangeListener(new PropertyChangeListener() {
					@Override public void propertyChange(PropertyChangeEvent evt) {
						DefaultMutableTreeNode node = findProcessNode(ideaJob);
						treeModel.nodeChanged(node);
						
				    	SwingUtilities.invokeLater(new Runnable(){
							@Override public void run() {
								showResults();// if results generated, add results to the tree
							}
				    	});

					}
				});
				findProcessNode(ideaJob);
			}
		
    	
    	} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return;
		}
    	
    	tree.updateUI();
    	
    }
    
    public void clearJobsAndResults(ExecutionPlan plan){
    	
    	jobs.removeAllChildren();
    	results.removeAllChildren();
    	
    	tree.updateUI();
    }
    
    public void showResults(){
    	
    	int jobCount = jobs.getChildCount();
    	if(jobCount==0)
    		return;
    	
    	if(results.getChildCount()>0){
    		tree.makeVisible(new TreePath(((DefaultMutableTreeNode)results.getFirstChild()).getPath()));
    	}else if(jobCount>1){
    		DefaultMutableTreeNode node = new DefaultMutableTreeNode("Parameter Analysis");
    		results.add(node);
    	}
    	long sessId = info.getIdeaSessionId();
    	if(sessId<=0)
    		return;
    	
    	for (int i = 0; i < jobCount; i++) {
			IDEAJob job = (IDEAJob)((DefaultMutableTreeNode)jobs.getChildAt(i)).getUserObject();
			if(job.getStatus().equals(IDEAJob.Status.COMPLETED))
				findResultNode(job);
		}
    	
		tree.updateUI();
    }
    
    public AnalysisInfo getAnalysisInfo(){
		return info;
	}

	@Override
	public void valueChanged(TreeSelectionEvent tse) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		if (node == null) return;
		Object nodeInfo = node.getUserObject();
		checkAndLoadBodyPage(nodeInfo);
		
	}

	public void selectNode(Object nodeName) {
		Enumeration nodes = ((DefaultMutableTreeNode) top.getRoot())
				.preorderEnumeration();
		while (nodes.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes
					.nextElement();
			if (node.getUserObject().equals(nodeName)) {
				TreePath path = new TreePath(node.getPath());
				tree.setSelectionPath(path);
				tree.makeVisible(path);
			}
		}
	}

	private DefaultMutableTreeNode findExecPlanNode(ExecutionPlan plan){
    	// search plan to tree
    	Enumeration<DefaultMutableTreeNode> planNodes = execPlans.children();
    	DefaultMutableTreeNode planNode = null;
    	while (planNodes.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) planNodes
					.nextElement();
			if(node.getUserObject().equals(plan)){
				planNode = node;
				break;
			}
		}
    	
    	// if not found add to tree
    	if(planNode == null){
    		planNode = new DefaultMutableTreeNode(plan);
    		execPlans.add(planNode);
    		
    		TreePath path = new TreePath(planNode.getPath());
    		tree.makeVisible(path);
    	}
    	
    	return planNode;
    }

    private DefaultMutableTreeNode findProcessNode(IDEAJob job){
    	// search plan to tree
    	Enumeration<DefaultMutableTreeNode> processNodes = jobs.children();
    	DefaultMutableTreeNode jobNode = null;
    	while (processNodes.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) processNodes
					.nextElement();
			if(node.getUserObject().equals(job)){
				jobNode = node;
				break;
			}
		}
    	
    	// if not found add to tree
    	if(jobNode == null){
    		jobNode = new DefaultMutableTreeNode(job);
    		jobs.add(jobNode);
    		
    		TreePath path = new TreePath(jobNode.getPath());
    		tree.makeVisible(path);
    	}
    	
    	return jobNode;
    }
    
    private DefaultMutableTreeNode findResultNode(IDEAJob job){
    	// search plan to tree
    	Enumeration<DefaultMutableTreeNode> resultNodes = results.children();
    	DefaultMutableTreeNode resultNode = null;
    	while (resultNodes.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) resultNodes
					.nextElement();
			if(node.getUserObject().equals(job.toString())){
				resultNode = node;
				break;
			}
		}
    	
    	// if not found add to tree
    	if(resultNode == null){
    		
    		resultNode = new DefaultMutableTreeNode(job.toString());
    		results.add(resultNode);
    		
    		// add plots to the result
    		FlutterPlot flutterPlot = new FlutterPlot(job);
    		if(flutterPlot.areResultFilesPresent())
    			resultNode.add(new DefaultMutableTreeNode(flutterPlot));
    		
    		EigenValuePlot eigenValuePlot = new EigenValuePlot(job);
    		if(eigenValuePlot.areResultFilesPresent())
    			resultNode.add(new DefaultMutableTreeNode(eigenValuePlot));
    		
    		IterationsPlot iterationPlot = new IterationsPlot(job);
    		if(iterationPlot.areResultFilesPresent())
    			resultNode.add(new DefaultMutableTreeNode(iterationPlot));
    		
    		TreePath path = new TreePath(resultNode.getPath());
    		tree.makeVisible(path);
    	}
    	
    	return resultNode;
    }

    
	private void createNodes(DefaultMutableTreeNode top) {
        
		DefaultMutableTreeNode inputFiles = new DefaultMutableTreeNode("Input Files");
        top.add(inputFiles);
        
        InputFile inFile = info.getParamIn();
        final DefaultMutableTreeNode param = new DefaultMutableTreeNode(inFile);
        inFile.addPropertyChangeListener(new PropertyChangeListener() {
			@Override public void propertyChange(PropertyChangeEvent evt) {
				treeModel.nodeChanged(param);
			}
		});
        inputFiles.add(param);
        
        
        inFile = info.getModelIn();
        final DefaultMutableTreeNode model = new DefaultMutableTreeNode(inFile);
        inFile.addPropertyChangeListener(new PropertyChangeListener() {
			@Override public void propertyChange(PropertyChangeEvent evt) {
				treeModel.nodeChanged(model);
			}
		});
        inputFiles.add(model);
        
        execPlans = new DefaultMutableTreeNode("Execution Plan");
        top.add(execPlans);
        if(info.getExecutionPlans().size()>0){
        	for (Iterator<ExecutionPlan> it = info.getExecutionPlans().iterator(); it.hasNext();) {
				ExecutionPlan plan = it.next();
				findExecPlanNode(plan);
			}
        }
        
        jobs = new DefaultMutableTreeNode("Processes");
        top.add(jobs);
        showProgress();
        
        results = new DefaultMutableTreeNode("Results");
        top.add(results);
        showResults();
        
        tree.updateUI();
    }
	
    private void checkAndLoadBodyPage(Object nodeInfo){
		
		
		if( nodeInfo!=null && !nodeInfo.equals(currentPage)){
			currentPage = nodeInfo;
			
			if(currentPage instanceof InputFile){
				
				final InputFile inputFile = (InputFile)currentPage;
				final JSplitPane editSP = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
				final ParamFileViewer viewPanel = new ParamFileViewer(inputFile);
				
				DocumentViewController viewController = new DocumentViewController() {
					
					@Override
					public void selectPara(int seq) {
						ParagraphView panel = viewPanel.getParagraph(seq);
						if(panel!=null){
//							if(viewPanel.getSelectedParagraphView()!=null)
//								viewPanel.getSelectedParagraphView().unSelect();
							panel.select();
							viewPanel.getReaderPanel().scrollRectToVisible(panel.getBounds());
						}
					}
					
					@Override
					public void reloadPara(int seq) {
						ParagraphView panel = viewPanel.getParagraph(seq);
						if(panel!=null){
							panel.resetData();
							viewPanel.getReaderPanel().scrollRectToVisible(panel.getBounds());
						}
					}
					
					@Override
					public void deletePara(int seq) {
						ParagraphView panel = viewPanel.getParagraph(seq);
						panel.unSelect();
						inputFile.getDocument().deleteParagraph(seq);
						viewPanel.deleteParagraph(seq);
						panel = viewPanel.getParagraph(seq);
						panel.select();
						viewPanel.getReaderPanel().scrollRectToVisible(panel.getBounds());
					}

					@Override
					public void insertPara(Paragraph para) {
						viewPanel.insertParagraph(para);
					}
					
				};
				
				BasicParagraphEditor editPanel = new BasicParagraphEditor(viewController);
				
				viewPanel.setEditor(editPanel);
				editSP.setTopComponent(viewPanel);
				
				editSP.setBottomComponent(editPanel);
				editSP.setDividerSize(8);
				
				sp.setRightComponent(editSP);
				
				// resizing happens correctly only when the panels are showing
				SwingUtilities.invokeLater(new Runnable() {
			            public void run() {
							editSP.setDividerLocation(editSP.getSize().height
			                        - editSP.getInsets().bottom
			                        - editSP.getDividerSize()
			                        - 180);
			            }}
				);
				
			}else if(currentPage.equals("Execution Plan")){
				ExecutionPlanListPanel panel = new ExecutionPlanListPanel(info);
				panel.setParent(this);
				sp.setRightComponent(panel);
			}else if(currentPage instanceof ExecutionPlan){
				sp.setRightComponent(new ExecutionPlanEditPanel((ExecutionPlan)currentPage, info));
			}else if(currentPage instanceof IDEAJob){
				IDEAJobDetailsPanel panel = new IDEAJobDetailsPanel((IDEAJob)currentPage);
				sp.setRightComponent(panel);
			}else if(currentPage.equals("Parameter Analysis")){
				sp.setRightComponent(new ResultsPanel((XYPlot)new ParameterAnalysisResultsPanel(info)));
				//sp.setRightComponent(new BlankPanel());
			}else if(currentPage instanceof XYPlot){
				try{
					sp.setRightComponent(new ResultsPanel((JobResult)currentPage));
				}catch(Throwable t){
					t.printStackTrace();
				}
				
				// resizing happens correctly only when the panels are showing
				SwingUtilities.invokeLater(new Runnable() {
			            public void run() {
							sp.setDividerLocation(sp.getDividerLocation() + 1);
			            }}
				);
			}else{
				sp.setRightComponent(new BlankPanel());
			}
			
		}
	}
	
}
