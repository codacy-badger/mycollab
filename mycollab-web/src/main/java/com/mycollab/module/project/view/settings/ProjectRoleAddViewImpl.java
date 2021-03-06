/**
 * Copyright © MyCollab
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mycollab.module.project.view.settings;

import com.mycollab.common.i18n.SecurityI18nEnum;
import com.mycollab.form.view.LayoutType;
import com.mycollab.module.project.ProjectRolePermissionCollections;
import com.mycollab.module.project.domain.ProjectRole;
import com.mycollab.module.project.domain.SimpleProjectRole;
import com.mycollab.module.project.i18n.ProjectRoleI18nEnum;
import com.mycollab.module.project.i18n.RolePermissionI18nEnum;
import com.mycollab.module.project.ui.components.AbstractEditItemComp;
import com.mycollab.module.user.view.component.AccessPermissionComboBox;
import com.mycollab.module.user.view.component.YesNoPermissionComboBox;
import com.mycollab.security.PermissionMap;
import com.mycollab.vaadin.UserUIContext;
import com.mycollab.vaadin.mvp.ViewComponent;
import com.mycollab.vaadin.ui.AbstractBeanFieldGroupEditFieldFactory;
import com.mycollab.vaadin.ui.AdvancedEditBeanForm;
import com.mycollab.vaadin.ui.FormContainer;
import com.mycollab.vaadin.ui.IFormLayoutFactory;
import com.mycollab.vaadin.web.ui.KeyCaptionComboBox;
import com.mycollab.vaadin.web.ui.grid.GridFormLayoutHelper;
import com.vaadin.data.HasValue;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.TextArea;
import org.vaadin.viritin.fields.MTextField;

import java.util.HashMap;
import java.util.Map;

import static com.mycollab.vaadin.web.ui.utils.FormControlsGenerator.generateEditFormControls;

/**
 * @author MyCollab Ltd.
 * @since 1.0
 */
@ViewComponent
public class ProjectRoleAddViewImpl extends AbstractEditItemComp<ProjectRole> implements ProjectRoleAddView {
    private static final long serialVersionUID = 1L;
    private final Map<String, KeyCaptionComboBox> permissionControlsMap = new HashMap<>();

    @Override
    protected String initFormHeader() {
        return beanItem.getId() == null ? UserUIContext.getMessage(ProjectRoleI18nEnum.NEW) :
                UserUIContext.getMessage(ProjectRoleI18nEnum.DETAIL);
    }

    @Override
    protected String initFormTitle() {
        return beanItem.getId() == null ? null : beanItem.getRolename();
    }

    @Override
    protected VaadinIcons initFormIconResource() {
        return VaadinIcons.CLIPBOARD_USER;
    }

    @Override
    protected ComponentContainer createButtonControls() {
        return generateEditFormControls(editForm);
    }

    @Override
    protected AdvancedEditBeanForm<ProjectRole> initPreviewForm() {
        return new AdvancedEditBeanForm<>();
    }

    @Override
    protected IFormLayoutFactory initFormLayoutFactory() {
        return new ProjectRoleFormLayoutFactory();
    }

    @Override
    protected AbstractBeanFieldGroupEditFieldFactory<ProjectRole> initBeanFormFieldFactory() {
        return new AbstractBeanFieldGroupEditFieldFactory<ProjectRole>(editForm) {
            private static final long serialVersionUID = 1L;

            @Override
            protected HasValue<?> onCreateField(Object propertyId) {
                if (propertyId.equals("description")) {
                    return new TextArea();
                } else if (propertyId.equals("rolename")) {
                    return new MTextField().withRequiredIndicatorVisible(true);
                }
                return null;
            }
        };
    }

    @Override
    protected ComponentContainer createBottomPanel() {
        final FormContainer permissionsPanel = new FormContainer();

        PermissionMap perMap;
        if (beanItem instanceof SimpleProjectRole) {
            perMap = ((SimpleProjectRole) beanItem).getPermissionMap();
        } else {
            perMap = new PermissionMap();
        }

        final GridFormLayoutHelper permissionFormHelper = GridFormLayoutHelper.defaultFormLayoutHelper(LayoutType.TWO_COLUMN);

        for (int i = 0; i < ProjectRolePermissionCollections.PROJECT_PERMISSIONS.length; i++) {
            String permissionPath = ProjectRolePermissionCollections.PROJECT_PERMISSIONS[i];
            KeyCaptionComboBox permissionBox;
            Enum captionHelp;
            if (ProjectRolePermissionCollections.FINANCE.equals(permissionPath) ||
                    ProjectRolePermissionCollections.APPROVE_TIMESHEET.equals(permissionPath)) {
                permissionBox = new YesNoPermissionComboBox();
                captionHelp = SecurityI18nEnum.BOOLEAN_PERMISSION_HELP;
            } else {
                permissionBox = new AccessPermissionComboBox();
                captionHelp = SecurityI18nEnum.ACCESS_PERMISSION_HELP;
            }

            Integer flag = perMap.getPermissionFlag(permissionPath);
            permissionBox.setValue(KeyCaptionComboBox.Entry.of(flag));
            permissionControlsMap.put(permissionPath, permissionBox);
            permissionFormHelper.addComponent(permissionBox, UserUIContext.getMessage(RolePermissionI18nEnum.valueOf(permissionPath)),
                    UserUIContext.getMessage(captionHelp), i % 2, i / 2);
        }
        permissionsPanel.addSection(UserUIContext.getMessage(ProjectRoleI18nEnum.SECTION_PERMISSIONS), permissionFormHelper.getLayout());

        return permissionsPanel;
    }

    @Override
    public PermissionMap getPermissionMap() {
        PermissionMap permissionMap = new PermissionMap();
        for (Map.Entry<String, KeyCaptionComboBox> entry : permissionControlsMap.entrySet()) {
            KeyCaptionComboBox permissionBox = entry.getValue();
            Integer perValue = permissionBox.getValue().getKey();
            permissionMap.addPath(entry.getKey(), perValue);
        }
        return permissionMap;
    }
}
