package com.company.scrumit.entity;

import com.groupstp.workflowstp.entity.Workflow;
import com.groupstp.workflowstp.entity.WorkflowEntity;
import com.groupstp.workflowstp.entity.WorkflowEntityStatus;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Listeners("scrumit_TaskEntityListener")
@NamePattern("%s|shortdesc")
@Table(name = "SCRUMIT_TASK")
@Entity(name = "scrumit$Task")
public class Task extends StandardEntity implements ExtWorkflowEntity<UUID> {
    private static final long serialVersionUID = 8919522312858052940L;

    @NotNull
    @Column(name = "SHORTDESC", nullable = false, length = 100)
    protected String shortdesc;


    @Lob
    @Column(name = "RETURN_COMMENT")
    protected String returnComment;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_WORK")
    protected Date startWork;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_CLASS_ID")
    protected TaskClass taskClass;

    @Column(name = "STEP_NAME")
    protected String stepName;


    @Column(name = "STATUS_WORK_FLOW")
    protected Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORKFLOW_ID")
    protected Workflow workflow;

    @Lob
    @Column(name = "TESTING_PLAN")
    protected String testingPlan;

    @Column(name = "PLANNING_TIME")
    protected Double planningTime;

    /**
     * Текущее время выполнения задачи в минутах
     */
    @Column(name = "ACTUAL_TIME")
    protected Integer actualTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_BUG_ID")
    protected Tracker parentBug;

    @OneToMany(mappedBy = "project")
    @OrderBy("createTs")
    protected List<Tracker> tracker;

    @Column(name = "DONE")
    protected Boolean done;

    @Column(name = "CONTROL")
    protected Boolean control;

    @Column(name = "PRIORITY")
    protected String priority;

    @Column(name = "REALDURATION")
    protected Integer realdurationHours;

    @Column(name = "TYPE_")
    protected String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ESTIMATION_ID")
    protected TaskEstimation estimation;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERFORMER_ID")
    protected Performer performer;

    @Temporal(TemporalType.DATE)
    @Column(name = "DEADLINE")
    protected Date deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TASK_ID")
    protected Task task;

    @Column(name = "LEVEL_")
    protected Integer level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TOP_ID")
    protected Task top;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BEGIN_")
    protected Date begin;

    @Column(name = "DURATION")
    protected Integer duration;

    @Column(name = "AMOUNT")
    protected Integer amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPRINT_BACKLOG_ID")
    protected SprintBacklog sprintBacklog;

    @JoinTable(name = "SCRUMIT_COMMAND_TASK_LINK",
            joinColumns = @JoinColumn(name = "TASK_ID"),
            inverseJoinColumns = @JoinColumn(name = "COMMAND_ID"))
    @ManyToMany
    protected List<Team> teams;

    @JoinTable(name = "SCRUMIT_SPRINT_TASK_LINK",
            joinColumns = @JoinColumn(name = "TASK_ID"),
            inverseJoinColumns = @JoinColumn(name = "SPRINT_ID"))
    @ManyToMany
    protected List<Sprint> sprints;

    @Column(name = "SS_ID")
    protected Long ssId;

    public void setEstimation(TaskEstimation estimation) {
        this.estimation = estimation;
    }

    public TaskEstimation getEstimation() {
        return estimation;
    }

    @OneToMany(mappedBy = "task")
    @OnDelete(DeletePolicy.CASCADE)
    private List<Task> children;

    public String getReturnComment() {
        return returnComment;
    }

    public void setReturnComment(String returnComment) {
        this.returnComment = returnComment;
    }

    public void setStatus(WorkflowEntityStatus status) {
        this.status = status.getId();
    }

    public WorkflowEntityStatus getStatus() {
        return WorkflowEntityStatus.fromId(status);
    }

    public void setActualTime(Integer actualTime) {
        this.actualTime = actualTime;
    }

    public Integer getActualTime() {
        return actualTime;
    }

    public Date getStartWork() {

        return startWork;
    }

    public void setStartWork(Date startWork) {
        this.startWork = startWork;
    }

    public TaskClass getTaskClass() {
        return taskClass;
    }

    public void setTaskClass(TaskClass taskClass) {
        this.taskClass = taskClass;
    }

    public List<Task> getChildren() {
        return children;
    }

    public void setChildren(List<Task> children) {
        this.children = children;
    }

    public void setTestingPlan(String testingPlan) {
        this.testingPlan = testingPlan;
    }

    public String getTestingPlan() {
        return testingPlan;
    }


    public void setPlanningTime(Double planningTime) {
        this.planningTime = planningTime;
    }

    public Double getPlanningTime() {
        return planningTime;
    }


    public void setPriority(Priority priority) {
        this.priority = priority == null ? null : priority.getId();
    }

    public Priority getPriority() {
        return priority == null ? null : Priority.fromId(priority);
    }

    public Integer getDuration() {
        return duration;
    }

    public List<Tracker> getTracker() {
        return tracker;
    }


    public void setParentBug(Tracker parentBug) {
        this.parentBug = parentBug;
    }

    public Tracker getParentBug() {
        return parentBug;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setTracker(List<Tracker> tracker) {
        this.tracker = tracker;
    }

    public void setSsId(Long ssId) {
        this.ssId = ssId;
    }

    public Long getSsId() {
        return ssId;
    }

    public void setControl(Boolean control) {
        this.control = control;
    }

    public Boolean getControl() {
        return control;
    }

    public void setSprints(List<Sprint> sprints) {
        this.sprints = sprints;
    }

    public List<Sprint> getSprints() {
        return sprints;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Boolean getDone() {
        return done;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getLevel() {
        return level;
    }

    public void setTop(Task top) {
        this.top = top;
    }

    public Task getTop() {
        return top;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setRealdurationHours(Integer realdurationHours) {
        this.realdurationHours = realdurationHours;
    }

    public Integer getRealdurationHours() {
        return realdurationHours;
    }

    public void setSprintBacklog(SprintBacklog sprintBacklog) {
        this.sprintBacklog = sprintBacklog;
    }

    public SprintBacklog getSprintBacklog() {
        return sprintBacklog;
    }

    public void setType(TaskType type) {
        this.type = type == null ? null : type.getId();
    }

    public TaskType getType() {
        return type == null ? null : TaskType.fromId(type);
    }


    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getBegin() {
        return begin;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setPerformer(Performer performer) {
        this.performer = performer;
    }

    public Performer getPerformer() {
        return performer;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setShortdesc(String shortdesc) {
        this.shortdesc = shortdesc;
    }

    public String getShortdesc() {
        return shortdesc;
    }


    @Override
    public String getStepName() {
        return stepName;
    }

    @Override
    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    @Override
    public Workflow getWorkflow() {
        return workflow;
    }

    @Override
    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }




}