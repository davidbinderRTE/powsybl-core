/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.afs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.powsybl.afs.storage.NodeInfo;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class ProjectNode extends AbstractNodeBase<ProjectFolder> {

    protected final Project project;

    protected final boolean folder;

    protected ProjectNode(ProjectFileCreationContext context, int codeVersion, boolean folder) {
        super(context.getInfo(), context.getStorage(), codeVersion);
        this.project = Objects.requireNonNull(context.getProject());
        this.folder = folder;
    }

    @Override
    public boolean isFolder() {
        return folder;
    }

    @Override
    public Optional<ProjectFolder> getParent() {
        return storage.getParentNode(info.getId())
                .filter(parentInfo -> ProjectFolder.PSEUDO_CLASS.equals(parentInfo.getPseudoClass()))
                .map(parentInfo -> new ProjectFolder(new ProjectFileCreationContext(parentInfo, storage, project)));
    }

    private static boolean pathStop(ProjectNode projectNode) {
        return !projectNode.getParent().isPresent();
    }

    private static String pathToString(List<String> path) {
        return path.stream().skip(1).collect(Collectors.joining(AppFileSystem.PATH_SEPARATOR));
    }

    @Override
    public NodePath getPath() {
        return NodePath.find(this, ProjectNode::pathStop, ProjectNode::pathToString);
    }

    public Project getProject() {
        return project;
    }

    public void moveTo(ProjectFolder folder) {
        Objects.requireNonNull(folder);
        boolean ancestorDetected = false;
        for (NodeInfo nodeInfo : findNodeAncesters(folder)) {
            if (info.getId().equals(nodeInfo.getId())) {
                ancestorDetected = true;
            }
        }
        if (!ancestorDetected) {
            storage.setParentNode(info.getId(), folder.getId());
            storage.flush();
        }
    }

    private List<NodeInfo> findNodeAncesters(ProjectFolder folder) {
        List<NodeInfo> ancesterNodes = new ArrayList<>();

        storage.getParentNode(folder.getId()).ifPresent(nodeParent -> {
            while (nodeParent != null) {
                ancesterNodes.add(nodeParent);
                if (storage.getParentNode(nodeParent.getId()).isPresent()) {
                    nodeParent = storage.getParentNode(nodeParent.getId()).get();
                } else {
                    break;
                }
            }
        });
        return ancesterNodes;
    }

    public void delete() {
        // has to be done before delete!!!
        invalidate();

        storage.deleteNode(info.getId());
        storage.flush();
    }

    public void rename(String name) {
        Objects.requireNonNull(name);
        storage.renameNode(info.getId(), name);
        storage.flush();
    }

    public List<ProjectFile> getBackwardDependencies() {
        return storage.getBackwardDependencies(info.getId())
                .stream()
                .map(project::createProjectFile)
                .collect(Collectors.toList());
    }

    protected void invalidate() {
        // propagate
        getBackwardDependencies().forEach(ProjectNode::invalidate);
    }

    public AppFileSystem getFileSystem() {
        return project.getFileSystem();
    }
}
