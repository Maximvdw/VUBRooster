package be.vubrooster.ejb.models;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sync", indexes = {
		@Index(name = "i1_sync", columnList = "id", unique = true),
		@Index(name = "i2_sync", columnList = "timeStamp", unique = true),
})
@NamedQueries({
		@NamedQuery(name = "findSyncs",
				query = "SELECT s FROM Sync s"),
		@NamedQuery(name = "findSyncById", query = "SELECT s FROM Sync s WHERE s.id = :id"),
})
public class Sync implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private long timeStamp = 0L;
	private int added = 0;
	private int removed = 0;
	private long duration = 0;
	private int studentGroups = 0;
	private int courses = 0;
	private int studyProgrammes = 0;
	private int activities = 0;

	public Sync(long timeStamp, int added, int removed, long duration) {
		setTimeStamp(timeStamp);
		setAdded(added);
		setRemoved(removed);
		setDuration(duration);
	}

	public Sync() {

	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getAdded() {
		return added;
	}

	public void setAdded(int added) {
		this.added = added;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getRemoved() {
		return removed;
	}

	public void setRemoved(int removed) {
		this.removed = removed;
	}

	public int getStudyProgrammes() {
		return studyProgrammes;
	}

	public void setStudyProgrammes(int studyProgrammes) {
		this.studyProgrammes = studyProgrammes;
	}

	public int getCourses() {
		return courses;
	}

	public void setCourses(int courses) {
		this.courses = courses;
	}

	public int getStudentGroups() {
		return studentGroups;
	}

	public void setStudentGroups(int studentGroups) {
		this.studentGroups = studentGroups;
	}

	public int getActivities() {
		return activities;
	}

	public void setActivities(int activities) {
		this.activities = activities;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public JsonObjectBuilder toJSON() {
		return Json.createObjectBuilder()
				.add("sync_id", id)
				.add("time", getTimeStamp())
				.add("duration",getDuration())
				.add("total_activities", getActivities())
				.add("total_added",getAdded())
				.add("total_removed",getRemoved())
				.add("total_studentgroups",getStudentGroups())
				.add("total_courses",getCourses())
				.add("total_studyprogrammes",getStudyProgrammes());
	}
}
