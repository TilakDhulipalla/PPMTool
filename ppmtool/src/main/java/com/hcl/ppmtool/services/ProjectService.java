package com.hcl.ppmtool.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.codec.ResourceEncoder;
import org.springframework.stereotype.Service;

import com.hcl.ppmtool.domain.Project;
import com.hcl.ppmtool.exceptions.ProjectIdException;
import com.hcl.ppmtool.repositories.ProjectRepository;

@Service
public class ProjectService {
	
	@Autowired
	private ProjectRepository projectRepositiory;
	
	public Project saveOrUpdateProject(Project project) {
		try {
			project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			return projectRepositiory.save(project);
		}catch(Exception e) {
			throw new ProjectIdException("Project ID " +project.getProjectIdentifier().toUpperCase()+"already exists");
		}
		
	}
	
	public Project findProjectByIdentifier(String projectId) {
		
		Project project = projectRepositiory.findByProjectIdentifier(projectId.toUpperCase());
		
		if(project==null) {
			throw new ProjectIdException("Project ID "+ projectId +" does not exist");
		}
		return project;
		  
	}
	
	public Iterable<Project> findAllProjects(){
		
		return projectRepositiory.findAll();
	}
	
	public void deleteProjectByIdentifier(String projectId) {
		Project project= projectRepositiory.findByProjectIdentifier(projectId.toUpperCase());
		
		if (project==null) {
			throw new ProjectIdException(" cannot project with ID "+projectId+" this project does not exist");
		}
		projectRepositiory.delete(project);
	}
}

