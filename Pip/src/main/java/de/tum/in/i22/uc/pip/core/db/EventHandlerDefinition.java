package de.tum.in.i22.uc.pip.core.db;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class EventHandlerDefinition {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String className;
	private String versionLabel;
	private int version;
	private byte[] classFile;
	
	@Column(length = 20000)
	private String sourceFile;
	private Timestamp classFileLastModified;
	private Timestamp dateReceived;
	private boolean currentlyActive;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getVersionLabel() {
		return versionLabel;
	}

	public void setVersionLabel(String versionLabel) {
		this.versionLabel = versionLabel;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public byte[] getClassFile() {
		return classFile;
	}

	public void setClassFile(byte[] classFile) {
		this.classFile = classFile;
	}

	public String getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	public boolean isCurrentlyActive() {
		return currentlyActive;
	}

	public void setCurrentlyActive(boolean currentlyActive) {
		this.currentlyActive = currentlyActive;
	}
	
	public Timestamp getClassFileLastModified() {
		return classFileLastModified;
	}

	public void setClassFileLastModified(Timestamp classFileLastModified) {
		this.classFileLastModified = classFileLastModified;
	}

	public Timestamp getDateReceived() {
		return dateReceived;
	}

	public void setDateReceived(Timestamp dateReceived) {
		this.dateReceived = dateReceived;
	}

	@Override
	public String toString() {
		return "ActionHandlerDefinition [id=" + id + ", className=" + className
				+ ", versionLabel=" + versionLabel + ", version=" + version
				+ ", dateCreated=" + classFileLastModified + ", dateReceived="
				+ dateReceived + ", currentlyActive=" + currentlyActive + "]";
	}
}