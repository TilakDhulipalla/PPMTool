import React, { Component } from "react";
import ProjectTask from "./ProjectTasks/ProjectTask";
import { connect } from "react-redux";
import {
  updateProjectTaskCategory,
  getBacklog
} from "../../actions/backlogActions";
import PropTypes from "prop-types";

class Backlog extends Component {
  constructor() {
    super();
    this.onDragOver = this.onDragOver.bind(this);
  }

  onDragOver(e) {
    e.preventDefault();
  }

  async onDrop(e, newstatus) {
    let pt_id = e.dataTransfer.getData("pt_id");
    let backlog_id = e.dataTransfer.getData("bl_id");
    await this.props.updateProjectTaskCategory(backlog_id, pt_id, newstatus);
    this.props.getBacklog(backlog_id);
  }

  render() {
    const { project_tasks_prop } = this.props;

    const tasks = project_tasks_prop.map(project_task => (
      <ProjectTask key={project_task.id} project_task={project_task} />
    ));
    let todoItems = [];
    let inProgressItems = [];
    let doneItems = [];

    for (let i = 0; i < tasks.length; i++) {
      if (tasks[i].props.project_task.status === "TO_DO") {
        todoItems.push(tasks[i]);
      }

      if (tasks[i].props.project_task.status === "IN_PROGRESS") {
        inProgressItems.push(tasks[i]);
      }

      if (tasks[i].props.project_task.status === "DONE") {
        doneItems.push(tasks[i]);
      }
    }
    return (
      <div className="container">
        <div className="row">
          <div
            className="col-md-4"
            onDragOver={this.onDragOver}
            onDrop={e => this.onDrop(e, "TO_DO")}
          >
            <div className="card text-center mb-2">
              <div className="card-header bg-secondary text-white">
                <h3>TO DO</h3>
              </div>
            </div>
            {todoItems}
            {
              // insert tasks here
            }
          </div>
          <div
            className="col-md-4"
            onDragOver={this.onDragOver}
            onDrop={e => this.onDrop(e, "IN_PROGRESS")}
          >
            <div className="card text-center mb-2">
              <div className="card-header bg-primary text-white">
                <h3>In Progress</h3>
              </div>
            </div>
            {inProgressItems}
          </div>
          <div
            className="col-md-4"
            onDragOver={this.onDragOver}
            onDrop={e => this.onDrop(e, "DONE")}
          >
            <div className="card text-center mb-2">
              <div className="card-header bg-success text-white">
                <h3>Done</h3>
              </div>
            </div>
            {doneItems}
          </div>
        </div>
      </div>
    );
  }
}

Backlog.propTypes = {
  updateProjectTaskCategory: PropTypes.func.isRequired,
  getBacklog: PropTypes.func.isRequired
};

export default connect(
  null,
  { updateProjectTaskCategory, getBacklog }
)(Backlog);
