package com.hcl.ppmtool.services;


import org.springframework.beans.factory.annotation.Autowired;
import com.hcl.ppmtool.exceptions.ProjectNotFoundException;
import org.springframework.core.codec.ResourceEncoder;
import org.springframework.stereotype.Service;

import com.hcl.ppmtool.domain.Backlog;
import com.hcl.ppmtool.domain.Project;
import com.hcl.ppmtool.domain.User;
import com.hcl.ppmtool.exceptions.ProjectIdException;  
import com.hcl.ppmtool.repositories.BacklogRepository;
import com.hcl.ppmtool.repositories.ProjectRepository;
import com.hcl.ppmtool.repositories.UserRepository;

@Service
public class ProjectService {
	
	@Autowired
	private ProjectRepository projectRepository;
	
	 @Autowired
	 private BacklogRepository backlogRepository;
	 
	 @Autowired
	 private UserRepository userRepository; 
	
	public Project saveOrUpdateProject(Project project, String username) {
		
		 if(project.getId() != null){
	            Project existingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());
	            if(existingProject !=null &&(!existingProject.getProjectLeader().equals(username))){
	                throw new ProjectNotFoundException("Project not found in your account");
	            }else if(existingProject == null){
	                throw new ProjectNotFoundException("Project with ID: '"+project.getProjectIdentifier()+"' cannot be updated because it doesn't exist");
	            }
	        }
		
		try {
			
			  User user = userRepository.findByUsername(username);
			  
	            project.setUser(user);
	            project.setProjectLeader(user.getUsername());
			project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			
			if(project.getId()==null) {
				Backlog backlog=new Backlog();
				project.setBacklog(backlog);
				backlog.setProject(project);
				backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			}
			 if(project.getId()!=null){
	                project.setBacklog(backlogRepository.findByProjectIdentifier(project.getProjectIdentifier().toUpperCase()));
	            }

			return projectRepository.save(project);
			
		}catch(Exception e) {
			throw new ProjectIdException("Project ID " +project.getProjectIdentifier().toUpperCase()+"already exists");
		}
		
	}
	
	public Project findProjectByIdentifier(String projectId,String username) {
		
		Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
		
		if(project==null) {
			throw new ProjectIdException("Project ID "+ projectId +" does not exist");
		}
		  if(!project.getProjectLeader().equals(username)){
	            throw new ProjectNotFoundException("Project not found in your account");
	        }
		return project;
		  
	}
	
	public Iterable<Project> findAllProjects(String username){
		
		return projectRepository.findAllByProjectLeader(username);
	}
	public void deleteProjectByIdentifier(String projectid, String username){
		projectRepository.delete(findProjectByIdentifier(projectid, username));
		
		
	}
}

