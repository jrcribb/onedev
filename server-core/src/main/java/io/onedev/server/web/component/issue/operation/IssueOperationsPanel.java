package io.onedev.server.web.component.issue.operation;

import java.util.Collection;

import org.apache.wicket.Component;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;

import com.google.common.collect.Lists;

import io.onedev.server.model.Issue;
import io.onedev.server.model.Project;
import io.onedev.server.security.SecurityUtils;
import io.onedev.server.web.behavior.ChangeObserver;
import io.onedev.server.web.component.floating.FloatingPanel;
import io.onedev.server.web.component.issue.IssueStateBadge;
import io.onedev.server.web.component.link.DropdownLink;
import io.onedev.server.web.component.workspace.speclist.WorkspaceSpecListPanel;
import io.onedev.server.web.page.project.issues.create.NewIssuePage;

public abstract class IssueOperationsPanel extends Panel {
	
	public IssueOperationsPanel(String id) {
		super(id);
	}
	
	@Override
	protected void onBeforeRender() {
		var transitionMenuLink = new TransitionMenuLink("state") {
			
			@Override
			protected Issue getIssue() {
				return IssueOperationsPanel.this.getIssue();
			}
			
		};
		addOrReplace(transitionMenuLink);

		addOrReplace(new DropdownLink("workspaces") {

			@Override
			protected Component newContent(String id, FloatingPanel dropdown) {
				return new WorkspaceSpecListPanel(id, getIssue().getBranch()) {

					@Override
					protected Project getProject() {
						return getIssue().getProject();
					}

				};
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(SecurityUtils.canWriteCode(getIssue().getProject())
						&& getIssue().getBranch() != null
						&& !getIssue().getProject().getHierarchyWorkspaceSpecs().isEmpty());
			}

		});

		transitionMenuLink.add(new IssueStateBadge("name", new LoadableDetachableModel<>() {
			@Override
			protected Issue load() {
				return getIssue();
			}
		}, true));
		
		addOrReplace(new BookmarkablePageLink<Void>("newIssue", NewIssuePage.class, 
				NewIssuePage.paramsOf(getIssue().getProject())));
		
		super.onBeforeRender();
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		add(new ChangeObserver() {
			
			@Override
			public Collection<String> findObservables() {
				return Lists.newArrayList(Issue.getDetailChangeObservable(getIssue().getId()));
			}
			
		});
		
		setOutputMarkupId(true);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new IssueOperationsCssResourceReference()));
	}

	protected abstract Issue getIssue();
	
}
