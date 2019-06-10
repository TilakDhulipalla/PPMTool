package com.hcl.ppmtool.services;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hcl.ppmtool.domain.Backlog;
import com.hcl.ppmtool.domain.Project;
import com.hcl.ppmtool.domain.ProjectTask;
import com.hcl.ppmtool.exceptions.ProjectNotFoundException;
import com.hcl.ppmtool.repositories.BacklogRepository;
import com.hcl.ppmtool.repositories.ProjectRepository;
import com.hcl.ppmtool.repositories.ProjectTaskRepository;

@Service
public class ProjectTaskService {
	
	@Autowired
	private BacklogRepository backlogRepository;
	
	@Autowired
	private ProjectTaskRepository projectTaskRepository;
	
	@Autowired
	private ProjectRepository projectRepository;

	public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {
		try {
		//exception not found
		//project task for specific proj, not null, backlog(bl) exists
		Backlog backlog=backlogRepository.findByProjectIdentifier(projectIdentifier);
		//set the bl to pt
		projectTask.setBacklog(backlog);
		//proj sequence from proj identifier withiin proj
		Integer BacklogSequence= backlog.getPTSequence();
		//update bl sequnce
		BacklogSequence++;
		backlog.setPTSequence(BacklogSequence);
		//add sequnce to proj task
		projectTask.setProjectSequence(backlog.getProjectIdentifier()+"-"+BacklogSequence);
		projectTask.setProjectIdentifier(projectIdentifier);
		
		//initial priority when priorty null
		if(projectTask.getPriority()==null) {
			projectTask.setPriority(3);
		}
		//initial status when null 
		if(projectTask.getStatus()==""||projectTask.getStatus()==null) {
			projectTask.setStatus("TO_DO");
		}
		return projectTaskRepository.save(projectTask);
		}catch (Exception e) {
			throw new ProjectNotFoundException("Project Not Found");
		}	
	}
	
	public Iterable<ProjectTask>findBacklogById(String id){
		
		 Project project = projectRepository.findByProjectIdentifier(id);

	        if(project==null){
	            throw new ProjectNotFoundException("Project with ID: '"+id+"' does not exist");
	        }
		
		return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
	}

	public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id) {
		//searching for existing proj
		Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);
		if(backlog==null) {
			throw new ProjectNotFoundException("Project with ID: '"+backlog_id+"' does not exist");
		}
		
		
		//make sure task it exists
		ProjectTask projectTask= projectTaskRepository.findByProjectSequence(pt_id);
		if(projectTask==null) {
			throw new ProjectNotFoundException("Project Task '"+pt_id+"' not found"); 
		}
		
		//proj/backlog id in path corresponds to right proj
		if(!projectTask.getProjectIdentifier().equals(backlog_id)) {
			throw new ProjectNotFoundException("Project Task '"+pt_id+"' does not exist in project '"+backlog_id); 
		}
		
		
		return projectTaskRepository.findByProjectSequence(pt_id);
		
	}
	
	public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id) {
		//update proj task
		
		//find task exists in proj
		
		//replace it
		//save update
		ProjectTask projectTask=findPTByProjectSequence(backlog_id,pt_id);
		
		projectTask=updatedTask;
		return projectTaskRepository.save(projectTask);
	}
	public void deletePTByProjectSequence(String backlog_id,String pt_id) {
		ProjectTask projectTask=findPTByProjectSequence(backlog_id,pt_id);
		
//		Backlog backlog=projectTask.getBacklog();
//		List<ProjectTask> pts = backlog.getProjectTasks();
//		pts.remove(projectTask);
//		backlogRepository.save(backlog);
		
		//included cascade refreesh in Backlog(project task array) so it refreshes the db when deleted, orphanremoval for deleting the all sub task when main task deletes
		
		
		projectTaskRepository.delete(projectTask);

	}
	
	}

