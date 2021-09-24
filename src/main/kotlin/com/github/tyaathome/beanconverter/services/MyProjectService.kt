package com.github.tyaathome.beanconverter.services

import com.intellij.openapi.project.Project
import com.github.tyaathome.beanconverter.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
